// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.entity;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions.MovementType;
import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.cache.loaders.map.RegionSize;
import com.rs.engine.pathfinder.*;
import com.rs.engine.pathfinder.collision.CollisionStrategy;
import com.rs.engine.pathfinder.collision.CollisionStrategyType;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.combat.PlayerCombatKt;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.prayer.Prayer;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.map.Chunk;
import com.rs.game.map.ChunkManager;
import com.rs.game.map.instance.InstancedChunk;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.actions.Action;
import com.rs.game.model.entity.actions.EntityFollow;
import com.rs.game.model.entity.async.AsyncTaskScheduler;
import com.rs.game.model.entity.interactions.InteractionManager;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.ActionManager;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.TaskManager;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.net.packets.decoders.Walk;
import com.rs.lib.net.packets.encoders.Sound;
import com.rs.lib.util.*;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.utils.TriFunction;
import com.rs.utils.WorldUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import kotlin.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Entity {
	public enum MoveType {
		WALK(1),
		RUN(2),
		TELE(127);

		private final int id;

		MoveType(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	// transient stuff
	private transient int index;
	private final String uuid;
	private transient int sceneBaseChunkId;
	private transient int lastChunkId;
	private transient boolean forceUpdateEntityRegion;
	private transient boolean nextTickUnlock;
	private transient Set<Integer> mapChunkIds;
	protected transient GenericAttribMap temporaryAttributes;
	protected transient GenericAttribMap nonsavingVars;
	private transient int faceAngle;
	private transient Tile lastTile;
	private transient Tile tileBehind;
	private transient Tile nextTile;
	public transient Walk walkRequest;
	private transient boolean walkedThisTick;
	private transient Direction nextWalkDirection;
	private transient Direction nextRunDirection;
	private transient Tile nextFaceTile;
	private transient Tile fixedFaceTile;
	private transient boolean teleported;
	private transient ConcurrentLinkedQueue<WalkStep> walkSteps;
	protected transient RouteEvent routeEvent;
	private transient ActionManager actionManager;
	private transient InteractionManager interactionManager;
	private transient CollisionStrategy collisionStrategy = CollisionStrategyType.NORMAL.getStrategy();
	private transient long lockDelay; // used for doors and stuff like that

	private transient BodyGlow nextBodyGlow;
	private transient ConcurrentLinkedQueue<Hit> receivedHits;
	private transient Map<Entity, Integer> receivedDamage;
	private transient boolean finished;
	private transient long tickCounter = 0;
	// entity masks
	private transient int bas = -1;
	private transient Animation nextAnimation;
	private transient SpotAnim nextSpotAnim1;
	private transient SpotAnim nextSpotAnim2;
	private transient SpotAnim nextSpotAnim3;
	private transient SpotAnim nextSpotAnim4;
	private transient ModelRotator bodyModelRotator;
	private transient ArrayList<Hit> nextHits;
	private transient ArrayList<HitBar> nextHitBars;
	private transient ForceMovement nextForceMovement;
	private transient ForceTalk nextForceTalk;
	private transient int nextFaceEntity;
	private transient int lastFaceEntity;
	private transient Entity attackedBy; // whos attacking you, used for single
	protected transient long attackedByDelay; // delay till someone else can attack you
	protected transient long timeLastHit;
	private transient boolean multiArea;
	private transient boolean hasNearbyInstancedChunks;
	private transient long lastAnimationEnd;
	private transient boolean forceMultiArea;
	private transient long findTargetDelay;

	// saving stuff
	private int hitpoints;
	private Tile tile;
	private RegionSize regionSize;

	protected MoveType moveType = MoveType.WALK;
	private final Poison poison;
	private Map<Effect, Long> effects = new HashMap<>();

	private transient AsyncTaskScheduler asyncTasks = new AsyncTaskScheduler();
	private transient TaskManager tasks = new TaskManager();

	// creates Entity and saved classes
	public Entity(Tile tile) {
		this.tile = Tile.of(tile);
		this.uuid = UUID.randomUUID().toString();
		poison = new Poison();
	}

	public void clearEffects() {
		if (effects == null)
			return;
		Map<Effect, Long> persisted = new HashMap<>();
		for (Effect effect : effects.keySet()) {
			if (effect == null)
				continue;
			if (!effect.isRemoveOnDeath())
				persisted.put(effect, effects.get(effect));
		}
		effects = persisted;
	}

	public boolean hasEffect(Effect effect) {
		return effects != null && effects.containsKey(effect);
	}

	public long getEffectTicks(Effect effect) {
		return effects != null && effects.containsKey(effect) ? effects.get(effect) : 0;
	}

	public void addEffect(Effect effect, long ticks) {
		if (effects == null)
			effects = new HashMap<>();
		effects.put(effect, ticks);
		effect.apply(this);
		effect.tick(this, ticks);
	}

	public void extendEffect(Effect effect, long ticks) {
		if (effects == null)
			effects = new HashMap<>();
		if (effects.containsKey(effect))
			effects.put(effect, effects.get(effect) + ticks);
		else
			addEffect(effect, ticks);
	}

	public void removeEffect(Effect effect) {
		if (effects == null)
			return;
		if (effect.sendWarnings() && this instanceof Player p)
			p.sendMessage(effect.getExpiryMessage());
		effects.remove(effect);
		effect.expire(this);
	}

	public void removeEffects(Effect... effects) {
		for (Effect e : effects)
			removeEffect(e);
	}

	public void walkToAndExecute(Tile startTile, Runnable event) {
		setRouteEvent(new RouteEvent(startTile, event));
	}

	public void walkToAndExecute(GameObject object, Runnable event) {
		setRouteEvent(new RouteEvent(object, event));
	}

	private void processEffects() {
		if (effects == null)
			return;
		Set<Effect> expired = new HashSet<>();
		for (Effect effect : effects.keySet()) {
			if (effect == null)
				continue;
			long time = effects.get(effect);
			time--;
			effect.tick(this, time);
			if (time == 50 && effect.sendWarnings() && this instanceof Player p)
				p.sendMessage(effect.get30SecWarning());
			if (time <= 0) {
				if (effect.sendWarnings() && this instanceof Player p)
					p.sendMessage(effect.getExpiryMessage());
				expired.add(effect);
			} else
				effects.put(effect, time);
		}
		for (Effect e : expired) {
			effects.remove(e);
			e.expire(this);
		}
	}

	public boolean isBehind(Entity other) {
		if (other.getFaceAngle() <= 2048 || other.getFaceAngle() >= 14336)
			if (getY() > other.getY())
				return true;
		if (other.getFaceAngle() <= 10240 && other.getFaceAngle() >= 6144)
			if (getY() < other.getY())
				return true;
		if (other.getFaceAngle() <= 6144 && other.getFaceAngle() >= 2048)
			if (getX() > other.getX())
				return true;
		if (other.getFaceAngle() <= 14336 && other.getFaceAngle() >= 10240)
            return getX() < other.getX();
		return false;
	}

	public Tile getBackfacingTile() {
		int[] backFaceDirs = Utils.getBackFace(faceAngle);
		return transform(backFaceDirs[0], backFaceDirs[1], 0);
	}

	public Tile getBackfacingTile(int distance) {
		int[] backFaceDirs = Utils.getBackFace(faceAngle);
		return transform(backFaceDirs[0] * distance, backFaceDirs[1] * distance, 0);
	}

	public Tile getFrontfacingTile() {
		int[] frontFaceDirs = Utils.getFrontFace(faceAngle);
		return transform(frontFaceDirs[0], frontFaceDirs[1], 0);
	}

	public Tile getFrontfacingTile(int distance) {
		int[] frontFaceDirs = Utils.getFrontFace(faceAngle);
		return transform(frontFaceDirs[0] * distance, frontFaceDirs[1] * distance, 0);
	}

	public boolean inArea(int a, int b, int c, int d) {
		return getX() >= a && getY() >= b && getX() <= c && getY() <= d;
	}

	public Tile getTileInScene(int x, int y) {
		Tile tile = Tile.of(x, y, getPlane());
		return Tile.of(tile.getXInScene(getSceneBaseChunkId()), tile.getYInScene(getSceneBaseChunkId()), getPlane());
	}

	public int getSceneX(int targetX) {
		return Tile.of(targetX, 0, 0).getXInScene(getSceneBaseChunkId());
	}

	public int getSceneY(int targetY) {
		return Tile.of(0, targetY, 0).getYInScene(getSceneBaseChunkId());
	}

	public final void initEntity() {
		mapChunkIds = IntSets.synchronize(new IntOpenHashSet());
		walkSteps = new ConcurrentLinkedQueue<>();
		receivedHits = new ConcurrentLinkedQueue<>();
		receivedDamage = new ConcurrentHashMap<>();
		temporaryAttributes = new GenericAttribMap();
		nonsavingVars = new GenericAttribMap();
		nextHits = new ArrayList<>();
		nextHitBars = new ArrayList<>();
		actionManager = new ActionManager(this);
		tasks = new TaskManager();
		asyncTasks = new AsyncTaskScheduler();
		interactionManager = new InteractionManager(this);
		nextWalkDirection = nextRunDirection = null;
		lastFaceEntity = -1;
		nextFaceEntity = -2;
		bas = -1;
		if (!(this instanceof NPC))
			faceAngle = 2;
		poison.setEntity(this);
		if (moveType == null)
			moveType = MoveType.WALK;
	}

	public int getClientIndex() {
		return index + (this instanceof Player ? 32768 : 0);
	}

	public void applyHit(Hit hit) {
		applyHit(hit, -1);
	}

	public void applyHit(Hit hit, int delay) {
		applyHit(hit, delay, null);
	}

	public void applyHit(Hit hit, int delay, Runnable onHit) {
		if (isDead()) {
			hit.setDamage(0);
			return;
		}
		handlePreHit(hit);
		if (hit.getSource() != null)
			hit.getSource().handlePreHitOut(this, hit);
		if (hit.getSource() instanceof Familiar f) {
			hit.setSource(f.getOwner());
			PlayerCombatKt.addXpFamiliar(f.getOwner(), this, f.getPouch().getXpType(), hit);
		}
		if (delay < 0) {
			handlePostHit(hit);
			if (onHit != null)
				onHit.run();
			receivedHits.add(hit);
		} else
			(hit.getSource() != null ? hit.getSource().getTasks() : tasks).schedule(delay, () -> {
				if (isDead()) {
					hit.setDamage(0);
					return;
				}
				if (onHit != null)
					onHit.run();
				receivedHits.add(hit);
			});
	}

	public abstract void handlePreHit(Hit hit);

	public abstract void handlePreHitOut(Entity target, Hit hit);

	public void reset(boolean attributes) {
		setHitpoints(getMaxHitpoints());
		receivedHits.clear();
		resetCombat();
		walkSteps.clear();
		poison.reset();
		clearPendingTasks();
		resetReceivedDamage();
		clearEffects();
		if (attributes)
			temporaryAttributes.clear();
	}

	public void reset() {
		reset(true);
	}

	public void resetCombat() {
		attackedBy = null;
		attackedByDelay = 0;
		removeEffects(Effect.FREEZE, Effect.FREEZE_BLOCK);
	}

	public void processReceivedHits() {
		if (lockDelay > tickCounter)
			return;
		if (this instanceof Player p)
			if (p.getEmotesManager().isAnimating())
				return;
		Hit hit;
		int count = 0;
		while ((hit = receivedHits.poll()) != null && count++ < 10)
			processHit(hit);
	}

	public boolean hasPendingHits() {
		return !receivedHits.isEmpty();
	}

	public void sendSoulSplit(Hit hit, Entity user) {
		if (hit.getDamage() > 0)
			World.sendProjectile(user, this, 2263, new Pair<>(11, 11), 0, 50, 0);
		user.heal(hit.getDamage() / 5);
		if (user instanceof Player p)
			p.incrementCount("Health soulsplitted back", hit.getDamage() / 5);
		if (this instanceof Player p)
			p.getPrayer().drainPrayer((double) hit.getDamage() / 5);
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				setNextSpotAnim(new SpotAnim(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(Entity.this, user, 2263, new Pair<>(11, 11), 0, 50, 0);
			}
		}, 0);
	}

	protected void processHit(Hit hit) {
		if (isDead())
			return;
		if (hit.getSource() != this)
			refreshTimeLastHit();
		removeHitpoints(hit);
		nextHits.add(hit);
		if (nextHitBars.isEmpty())
			addHitBars();
	}

	public void fakeHit(Hit hit) {
		nextHits.add(hit);
		if (nextHitBars.isEmpty())
			addHitBars();
	}

	public Sound sound(Entity playTo, Sound sound, boolean alsoAmbient) {
		if (sound.getId() == -1)
			return sound;
		//Logger.info(Entity.class, "sound", this + " playing " + sound.getType() + " sound to " + playTo + ", " + alsoAmbient + " -> [id: " + sound.getId() + ", rad: " + sound.getRadius() + ", vol: " + sound.getVolume() + "]");
		if (this instanceof Player player)
			player.addSound(sound);
		if (playTo instanceof Player player && this != playTo)
			player.addSound(sound);
		if (alsoAmbient)
			ChunkManager.getChunk(getChunkId()).addSound(tile, sound);
		return sound;
	}

	public Sound sound(Entity playTo, int soundId, int delay, Sound.SoundType type, int radius, boolean alsoAmbient) {
		return sound(playTo, new Sound(soundId, delay, type).radius(radius), alsoAmbient);
	}

	public Sound soundEffect(Entity playTo, int soundId, int delay, boolean alsoAmbient) {
		return sound(playTo, soundId, delay, Sound.SoundType.EFFECT, 10, alsoAmbient);
	}

	public Sound soundEffect(Entity playTo, int soundId, boolean alsoAmbient) {
		return soundEffect(playTo, soundId, 0, alsoAmbient);
	}

	public Sound voiceEffect(Entity playTo, int voiceId, int delay, boolean alsoAmbient) {
		return sound(playTo, voiceId, delay, Sound.SoundType.VOICE, 10, alsoAmbient);
	}

	public Sound voiceEffect(Entity playTo, int voiceId, boolean alsoAmbient) {
		return voiceEffect(playTo, voiceId, 0, alsoAmbient);
	}

	public Sound sound(int soundId, int delay, Sound.SoundType type, boolean alsoAmbient) {
		return sound(null, new Sound(soundId, delay, type), alsoAmbient);
	}

	public Sound soundEffect(int soundId, int delay, boolean alsoAmbient) {
		return sound(null, soundId, delay, Sound.SoundType.EFFECT, 10, alsoAmbient);
	}

	public Sound soundEffect(int soundId, boolean alsoAmbient) {
		return soundEffect(null, soundId, 0, alsoAmbient);
	}

	public Sound voiceEffect(int voiceId, int delay, boolean alsoAmbient) {
		return sound(null, voiceId, delay, Sound.SoundType.VOICE, 10, alsoAmbient);
	}

	public Sound voiceEffect(int voiceId, boolean alsoAmbient) {
		return voiceEffect(null, voiceId, 0, alsoAmbient);
	}


	public void addHitBars() {
		nextHitBars.add(new EntityHitBar(this));
	}

	public void resetReceivedHits() {
		nextHits.clear();
		receivedHits.clear();
	}

	public void removeHitpoints(Hit hit) {
		if (isDead() || hit.getLook() == HitLook.ABSORB_DAMAGE)
			return;
		if (hit.getLook() == HitLook.HEALED_DAMAGE) {
			heal(hit.getDamage());
			return;
		}
		if (hit.getDamage() > hitpoints)
			hit.setDamage(hitpoints);
		addReceivedDamage(hit.getSource(), hit.getDamage());
		setHitpoints(hitpoints - hit.getDamage());

		if (this instanceof Player p)
			if (p.getNSV().getB("godMode"))
				setHitpoints(getMaxHitpoints());

		if (hitpoints <= 0)
			sendDeath(hit.getSource());
		else if (this instanceof Player player) {
			if (player.getEquipment().getRingId() == 2550)
				if (hit.getSource() != null && hit.getSource() != player && (hit.getLook() == HitLook.MELEE_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MAGIC_DAMAGE))
					hit.getSource().applyHit(new Hit(player, (int) (hit.getDamage() * 0.1), HitLook.REFLECTED_DAMAGE));
			if (player.getPrayer().hasPrayersOn())
				if ((hitpoints < player.getMaxHitpoints() * 0.1) && player.getPrayer().active(Prayer.REDEMPTION)) {
					player.soundEffect(hit.getSource(), 2681, true);
					setNextSpotAnim(new SpotAnim(436));
					setHitpoints((int) (hitpoints + player.getSkills().getLevelForXp(Constants.PRAYER) * 2.5));
					player.getPrayer().setPoints(0);
				}
			if (player.getEquipment().getAmuletId() == 11090 && player.getHitpoints() <= player.getMaxHitpoints() * 0.2) {
				player.heal((int) (player.getMaxHitpoints() * 0.3));
				player.getEquipment().deleteItem(11090, 1);
				player.getAppearance().generateAppearanceData();
				player.sendMessage("Your pheonix necklace heals you, but is destroyed in the process.");
			}
			if (player.getHitpoints() <= (player.getMaxHitpoints() * 0.1) && player.getEquipment().getRingId() == 2570)
				Magic.sendItemTeleportSpell(player, true, 9603, 1684, 4, Settings.getConfig().getPlayerRespawnTile(), () -> {
					player.getEquipment().deleteSlot(Equipment.RING);
					player.getEquipment().refresh(Equipment.RING);
					player.sendMessage("Your ring of life saves you and is destroyed in the process.");
				});
		}
	}

	public void resetReceivedDamage() {
		receivedDamage.clear();
	}

	public void removeDamage(Entity entity) {
		receivedDamage.remove(entity);
	}

	public void entityFollow(Entity target, boolean intelligent, int distance) {
		if (!target.hasWalkSteps() && WorldUtil.collides(getX(), getY(), getSize(), target.getX(), target.getY(), target.getSize())) {
			resetWalkSteps();
			if (!addWalkSteps(target.getX() + target.getSize(), getY())) {
				resetWalkSteps();
				if (!addWalkSteps(target.getX() - getSize(), getY())) {
					resetWalkSteps();
					if (!addWalkSteps(getX(), target.getY() + target.getSize())) {
						resetWalkSteps();
						if (!addWalkSteps(getX(), target.getY() - getSize()))
							return;
					}
				}
			}
			return;
		}
		if (target.getSize() == 1 && getSize() == 1 && Math.abs(getX() - target.getX()) == 1 && Math.abs(getY() - target.getY()) == 1 && !target.hasWalkSteps()) {
			if (!addWalkSteps(target.getX(), getY(), 1, true))
				addWalkSteps(getX(), target.getY(), 1, true);
			return;
		}
		if (!WorldUtil.isInRange(this, target, distance) || !lineOfSightTo(target, distance == 0)) {
			if (!hasWalkSteps() || target.hasWalkSteps()) {
				resetWalkSteps();
				calcFollow(target, getRun() ? 2 : 1, true);
			}
		} else
			resetWalkSteps();
	}

	public boolean calcFollow(Object target, boolean inteligent) {
		return calcFollow(target, -1, inteligent);
	}

	public abstract boolean canMove(Direction dir);

	public void handlePostHit(Hit hit) {

	}

	public boolean calcFollow(Object target, int maxStepsCount, boolean intelligent) {
		if (intelligent) {
			Route route = RouteFinderKt.routeEntityTo(this, target, maxStepsCount < 0 ? 25 : maxStepsCount);
			if (route.getFailed())
				return false;
//			if (route.getStepCount() == 0) //TODO why did I add this?
//				return DumbRouteFinder.addDumbPathfinderSteps(this, target, getClipType());
			RouteFinderKt.addSteps(this, route,true, maxStepsCount);
			return true;
		}
		return DumbRouteFinder.addDumbPathfinderSteps(this, target, getCollisionStrategy());
	}

	public Player getMostDamageReceivedSourcePlayer() {
		Player player = null;
		int damage = -1;
		for (Entity source : receivedDamage.keySet()) {
			if (!(source instanceof Player))
				continue;
			Integer d = receivedDamage.get(source);
			if (d == null || source.hasFinished()) {
				receivedDamage.remove(source);
				continue;
			}
			if (d > damage) {
				player = (Player) source;
				damage = d;
			}
		}
		return player;
	}

	public void processReceivedDamage() {
		if (isDead())
			return;
		for (Entity source : receivedDamage.keySet()) {
			Integer damage = receivedDamage.get(source);
			if (damage == null || source.hasFinished()) {
				receivedDamage.remove(source);
				continue;
			}
			damage--;
			if (damage == 0) {
				receivedDamage.remove(source);
				continue;
			}
			receivedDamage.put(source, damage);
		}
	}

	public void addReceivedDamage(Entity source, int amount) {
		if (source == null)
			return;
		Integer damage = receivedDamage.get(source);
		damage = damage == null ? amount : damage + amount;
		if (source instanceof Familiar fs) {
			if (damage < 0)
				receivedDamage.remove(fs.getOwner());
			else
				receivedDamage.put(fs.getOwner(), damage);
		} else if (damage < 0)
			receivedDamage.remove(source);
		else
			receivedDamage.put(source, damage);
	}

	public void heal(int ammount) {
		heal(ammount, 0);
	}

	public void heal(int ammount, int extra) {
		setHitpoints((hitpoints + ammount) >= (getMaxHitpoints() + extra) ? (getMaxHitpoints() + extra) : (hitpoints + ammount));
	}

	public boolean hasWalkSteps() {
		return !walkSteps.isEmpty() || walkedThisTick;
	}

//	public boolean isMoving() {
//		return hasWalkSteps() || walkedThisTick;
//	}

	public abstract void sendDeath(Entity source);

	public void updateAngle(Tile base, int sizeX, int sizeY) {
		Tile from = nextTile != null ? nextTile : this.getTile();
		int srcX = (from.getX() * 512) + (getSize() * 256);
		int srcY = (from.getY() * 512) + (getSize() * 256);
		int dstX = (base.getX() * 512) + (sizeX * 256);
		int dstY = (base.getY() * 512) + (sizeY * 256);
		int deltaX = srcX - dstX;
		int deltaY = srcY - dstY;
		faceAngle = deltaX != 0 || deltaY != 0 ? (int) (Math.atan2(deltaX, deltaY) * 2607.5945876176133) & 0x3FFF : 0;
	}

	public void processMovement() {
		NPC npc = this instanceof NPC ? (NPC) this : null;
		Player player = this instanceof Player ? (Player) this : null;

		lastTile = Tile.of(getTile());
		if (lastFaceEntity >= 0) {
			Entity target = lastFaceEntity >= 32768 ? World.getPlayers().get(lastFaceEntity - 32768) : World.getNPCs().get(lastFaceEntity);
			if (target != null) {
				int size = target.getSize();
				updateAngle(target.getTile(), size, size);
			}
		}
		nextWalkDirection = nextRunDirection = null;
		if (nextTile != null && nextTile.getX() > 32 && nextTile.getY() > 32) {
			tile = nextTile;
			tileBehind = getBackfacingTile();
			nextTile = null;
			teleported = true;

			int deltaX = lastTile.getX() - this.getX();
			int deltaY = lastTile.getY() - this.getY();

			if (player != null && player.getTemporaryMoveType() == null && !(deltaX <= 14 && deltaX >= -14 && deltaY <= 14 && deltaY >= -14))
				player.setTemporaryMoveType(MoveType.TELE);
			ChunkManager.updateChunks(this);
			if (needMapUpdate())
				loadMapRegions();
			resetWalkSteps();
			return;
		}
		teleported = false;
		if (walkSteps.isEmpty())
			return;
		walkedThisTick = true;
		if (player != null) {
			if (player.getEmotesManager().isAnimating())
				return;
			if (player.getRunEnergy() <= 0.0 || player.isRunBlocked())
				setRun(false);
			if (walkSteps.size() <= 1)
				player.setTemporaryMoveType(MoveType.WALK);
		}

		if (npc != null)
			if (npc.getDefinitions().movementType == MovementType.HALF_WALK)
				if (npc.switchWalkStep())
					return;

		StepValidator step = new StepValidator(WorldCollision.INSTANCE.getAllFlags());
		for (int stepCount = 0; stepCount < (moveType == MoveType.RUN ? 2 : 1); stepCount++) {
			WalkStep nextStep = getNextWalkStep();
			if (nextStep == null)
				break;
			if (player != null)
				PluginManager.handle(new PlayerStepEvent(player, nextStep, Tile.of(getX() + nextStep.dir.dx, getY() + nextStep.dir.dy, getPlane())));
			if ((nextStep.checkClip() && !step.canTravel(getPlane(), getX(), getY(), nextStep.dir.dx, nextStep.dir.dy, getSize(), 0, getCollisionStrategy())) || (nextStep.checkClip() && npc != null && !npc.checkNPCCollision(nextStep.dir)) || !canMove(nextStep.dir)) {
				resetWalkSteps();
				break;
			}
			if (stepCount == 0)
				nextWalkDirection = nextStep.dir;
			else
				nextRunDirection = nextStep.dir;
			tileBehind = Tile.of(getTile());
			moveLocation(nextStep.dir.dx, nextStep.dir.dy, 0);
			if (moveType == MoveType.RUN && stepCount == 0) { // fixes impossible steps TODO is this even necessary?
				WalkStep previewStep = previewNextWalkStep();
				if (previewStep == null)
					break;
				int dx = nextStep.dir.dx + previewStep.dir.dx;
				int dy = nextStep.dir.dy + previewStep.dir.dy;
				if (Utils.getPlayerRunningDirection(dx, dy) == -1 && Utils.getPlayerWalkingDirection(dx, dy) == -1)
					break;
			}
			if (player != null)
				if (nextRunDirection != null) {
					player.drainRunEnergy((Math.min(player.getWeight(), 64) / 100.0) + 0.64);
					if (player.getRunEnergy() == 0.0)
						player.setRun(false);
				}
		}
		ChunkManager.updateChunks(this);
		if (needMapUpdate())
			loadMapRegions();
	}

	public WalkStep previewNextWalkStep() {
		WalkStep step = walkSteps.peek();
        return step;
	}

	public void moveLocation(int xOffset, int yOffset, int planeOffset) {
		tile = tile.transform(xOffset, yOffset, planeOffset);
		faceAngle = Utils.getAngleTo(xOffset, yOffset);
	}

	private boolean needMapUpdate() {
		return needMapUpdate(getTile());
	}

	public boolean needMapUpdate(Tile tile) {
		int[] baseChunk = MapUtils.decode(Structure.CHUNK, sceneBaseChunkId);
		// chunks length - offset. if within 16 tiles of border it updates map
		int limit = getMapSize().size / 8 - 2;

		int offsetX = tile.getChunkX() - baseChunk[0];
		int offsetY = tile.getChunkY() - baseChunk[1];

		return offsetX < 2 || offsetX >= limit || offsetY < 2 || offsetY >= limit;
	}

	public boolean addWalkSteps(int destX, int destY) {
		return addWalkSteps(destX, destY, -1, true);
	}

	public Tile getMiddleTile() {
		int size = getSize();
		return size == 1 ? getTile() : Tile.of(getCoordFaceX(size), getCoordFaceY(size), getPlane());
	}

	public boolean ignoreWallsWhenMeleeing() {
		return false;
	}

	private static final Set<Object> LOS_NPC_OVERRIDES = new HashSet<>();
	private static final List<TriFunction<Entity, Object, Boolean, Boolean>> LOS_FUNCTION_OVERRIDES = new ArrayList<>();

	public static void addLOSOverride(int npcId) {
		LOS_NPC_OVERRIDES.add(npcId);
	}

	public static void addLOSOverride(String npcName) {
		LOS_NPC_OVERRIDES.add(npcName);
	}

	public static void addLOSOverrides(int... npcIds) {
		for (int npcId : npcIds)
			addLOSOverride(npcId);
	}

	public static void addLOSOverrides(String... npcNames) {
		for (String npcName : npcNames)
			addLOSOverride(npcName);
	}

	public static void addLOSOverride(TriFunction<Entity, Object, Boolean, Boolean> func) {
		LOS_FUNCTION_OVERRIDES.add(func);
	}

	public boolean lineOfSightTo(Object target, boolean melee) {
		Tile tile = WorldUtil.targetToTile(target);
		int targSize = target instanceof Entity ? ((Entity) target).getSize() : 1;
		if (target instanceof NPC npc) {
			if (LOS_NPC_OVERRIDES.contains(npc.getId()) || LOS_NPC_OVERRIDES.contains(npc.getName()))
				return true;
		}
		for (TriFunction<Entity, Object, Boolean, Boolean> func : LOS_FUNCTION_OVERRIDES)
			if (func.apply(this, target, melee))
				return true;
		if (melee && !(target instanceof Entity e && e.ignoreWallsWhenMeleeing()))
			return World.checkMeleeStep(this, this.getSize(), target, targSize) && World.hasLineOfSight(getMiddleTile(), getSize(), target instanceof Entity e ? e.getMiddleTile() : tile, targSize);
		return World.hasLineOfSight(getMiddleTile(), getSize(), target instanceof Entity e ? e.getMiddleTile() : tile, targSize);
	}

	public boolean addWalkSteps(final int destX, final int destY, int maxStepsCount) {
		return addWalkSteps(destX, destY, maxStepsCount, true, false);
	}

	public boolean addWalkSteps(final int destX, final int destY, int maxStepsCount, boolean clip) {
		return addWalkSteps(destX, destY, maxStepsCount, clip, false);
	}

	public boolean addWalkSteps(final int destX, final int destY, int maxStepsCount, boolean clip, boolean force) {
		int[] lastTile = getLastWalkTile();
		int myX = lastTile[0];
		int myY = lastTile[1];
		int stepCount = 0;
		while (true) {
			stepCount++;
			if (myX < destX)
				myX++;
			else if (myX > destX)
				myX--;
			if (myY < destY)
				myY++;
			else if (myY > destY)
				myY--;
			if (!addWalkStep(myX, myY, lastTile[0], lastTile[1], clip, force))
				return false;
			if (stepCount == maxStepsCount)
				return true;
			lastTile[0] = myX;
			lastTile[1] = myY;
			if (lastTile[0] == destX && lastTile[1] == destY)
				return true;
		}
	}

	private int[] getLastWalkTile() {
		Object[] steps = walkSteps.toArray();
		if (steps.length == 0)
			return new int[] { getX(), getY() };
		WalkStep step = (WalkStep) steps[steps.length - 1];
		return new int[] {step.x, step.y};
	}

	public boolean walkOneStep(int x, int y, boolean clipped) {
		return addWalkStep(getX() + x, getY() + y, getX(), getY(), clipped, false);
	}

	public boolean addWalkStep(int nextX, int nextY, int lastX, int lastY, boolean check) {
		return addWalkStep(nextX, nextY, lastX, lastY, check, true);
	}

	public boolean addWalkStep(int nextX, int nextY, int lastX, int lastY, boolean check, boolean force) {
		//World.sendSpotAnim(Tile.of(nextX, nextY, getPlane()), new SpotAnim(2000));
		Direction dir = Direction.forDelta(nextX - lastX, nextY - lastY);
		StepValidator step = new StepValidator(WorldCollision.INSTANCE.getAllFlags());
		if (dir == null)
			return false;
		if (!force && check && !step.canTravel(getPlane(), lastX, lastY, dir.dx, dir.dy, getSize(), 0, getCollisionStrategy()) || (check && this instanceof NPC n && !n.checkNPCCollision(dir)))// double
			return false;
		if (this instanceof Player player)
			if (!player.getControllerManager().checkWalkStep(lastX, lastY, nextX, nextY))
				return false;
		walkSteps.add(new WalkStep(dir, nextX, nextY, check));
		return true;
	}

	public ConcurrentLinkedQueue<WalkStep> getWalkSteps() {
		return walkSteps;
	}

	public void resetWalkSteps() {
		walkSteps.clear();
	}

	private WalkStep getNextWalkStep() {
		WalkStep step = walkSteps.poll();
        return step;
	}

	public boolean restoreHitPoints() {
		int maxHp = getMaxHitpoints();
		if (hitpoints > maxHp) {
			setHitpoints(hitpoints - 1);
			return true;
		}
		if (hitpoints < maxHp) {
			setHitpoints(hitpoints + 1);
			return true;
		}
		return false;
	}

	public boolean needMasksUpdate() {
		return nextBodyGlow != null || nextFaceEntity != -2 || nextAnimation != null || nextSpotAnim1 != null || nextSpotAnim2 != null || nextSpotAnim3 != null || nextSpotAnim4 != null || (nextWalkDirection == null && nextFaceTile != null) || fixedFaceTile != null || !nextHits.isEmpty() || !nextHitBars.isEmpty() || nextForceMovement != null || nextForceTalk != null || bodyModelRotator != null;
	}

	public boolean isDead() {
		return hitpoints <= 0;
	}

	public void resetMasks() {
		walkedThisTick = false;
		nextBodyGlow = null;
		nextAnimation = null;
		nextSpotAnim1 = null;
		nextSpotAnim2 = null;
		nextSpotAnim3 = null;
		nextSpotAnim4 = null;
		if (nextWalkDirection == null)
			nextFaceTile = null;
		if (bodyModelRotator == ModelRotator.RESET)
			bodyModelRotator = null;
		nextForceMovement = null;
		nextForceTalk = null;
		nextFaceEntity = -2;
		nextHits.clear();
		nextHitBars.clear();
	}

	public abstract void finish();

	public abstract int getMaxHitpoints();

	public void processEntity() {
		tickCounter++;
		if (walkRequest != null) {
			resetWalkSteps();
			RouteFinderKt.addSteps(this, RouteFinderKt.routeEntityWalkRequest(this, walkRequest), true);
			walkRequest = null;
		}
		RouteEvent prevEvent = routeEvent;
		if (routeEvent != null && routeEvent.processEvent(this)) {
			if (routeEvent == prevEvent)
				routeEvent = null;
		}
		poison.processPoison();
		processReceivedHits();
		processReceivedDamage();
		if (!isDead())
			if (tickCounter % 10 == 0)
				restoreHitPoints();
		processEffects();
		interactionManager.process();
		actionManager.process();
		if (nextTickUnlock) {
			unlock();
			nextTickUnlock = false;
		}
	}

	public void loadMapRegions() {
		loadMapRegions(getMapSize());
	}

	public void loadMapRegions(RegionSize oldSize) {
		Player player = this instanceof Player p ? p : null;
		NPC npc = this instanceof NPC n ? n : null;
		Set<Integer> old = player != null && mapChunkIds != null ? IntSets.synchronize(new IntOpenHashSet(mapChunkIds)) : null;
		if (player != null)
			ChunkManager.getUpdateZone(sceneBaseChunkId, oldSize).removePlayerWatcher(player.getIndex());
		if (npc != null && npc.isLoadsUpdateZones())
			ChunkManager.getUpdateZone(sceneBaseChunkId, oldSize).removeNPCWatcher(npc.getIndex());
		mapChunkIds.clear();
		if (player != null)
			player.getMapChunksNeedInit().clear();
		hasNearbyInstancedChunks = false;
		int currChunkX = getChunkX();
		int currChunkY = getChunkY();
		int sceneChunkRadius = getMapSize().size / 16;
		int sceneBaseChunkX = (currChunkX - sceneChunkRadius);
		int sceneBaseChunkY = (currChunkY - sceneChunkRadius);
		if (sceneBaseChunkX < 0)
			sceneBaseChunkX = 0;
		if (sceneBaseChunkY < 0)
			sceneBaseChunkY = 0;
		sceneBaseChunkId = MapUtils.encode(Structure.CHUNK, sceneBaseChunkX, sceneBaseChunkY, 0);
		for (int planeOff = 0;planeOff < 4 * Chunk.PLANE_INC;planeOff += Chunk.PLANE_INC) {
			for (int chunkOffX = 0; chunkOffX <= sceneChunkRadius * Chunk.X_INC * 2; chunkOffX += Chunk.X_INC) {
				for (int chunkOffY = 0; chunkOffY <= sceneChunkRadius * 2; chunkOffY++) {
					int chunkId = sceneBaseChunkId + chunkOffX + chunkOffY + planeOff;
					Chunk chunk = ChunkManager.getChunk(chunkId, (npc != null && npc.isLoadsUpdateZones()) || player != null);
					if (chunk instanceof InstancedChunk)
						hasNearbyInstancedChunks = true;
					mapChunkIds.add(chunkId);
					if (old != null) {
						if (!old.contains(chunkId))
							player.getMapChunksNeedInit().add(chunkId);
						old.remove(chunkId);
					}
				}
			}
		}
		if (player != null)
			ChunkManager.getUpdateZone(sceneBaseChunkId, oldSize).addPlayerWatcher(player.getIndex());
		if (npc != null && npc.isLoadsUpdateZones())
			ChunkManager.getUpdateZone(sceneBaseChunkId, oldSize).addNPCWatcher(npc.getIndex());
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(int hitpoints) {
		this.hitpoints = hitpoints;
	}

	public RegionSize getMapSize() {
		if (regionSize == null)
			regionSize = RegionSize.SIZE_104;
		return regionSize;
	}

	public void setMapSize(RegionSize size) {
		RegionSize oldSize = regionSize;
		regionSize = size;
		loadMapRegions(oldSize);
	}

	public Set<Integer> getMapChunkIds() {
		return mapChunkIds;
	}

	/**
	 * use .anim(id) instead
	 */
	@Deprecated()
	public void setNextAnimation(Animation nextAnimation) {
		if (nextAnimation != null && nextAnimation.getIds()[0] >= 0)
			lastAnimationEnd = World.getServerTicks() + AnimationDefinitions.getDefs(nextAnimation.getIds()[0]).getEmoteGameTicks();
		this.nextAnimation = nextAnimation;
	}

	public void setNextAnimationNoPriority(Animation nextAnimation) {
		if (lastAnimationEnd > World.getServerTicks())
			return;
		setNextAnimation(nextAnimation);
	}

	public Animation getNextAnimation() {
		return nextAnimation;
	}

	/**
	 * Use .spotAnim(id) instead
	 * @param nextSpotAnim
	 */
	@Deprecated
	public void setNextSpotAnim(SpotAnim nextSpotAnim) {
		if (nextSpotAnim == null) {
			if (nextSpotAnim4 != null)
				nextSpotAnim4 = null;
			else if (nextSpotAnim3 != null)
				nextSpotAnim3 = null;
			else if (nextSpotAnim2 != null)
				nextSpotAnim2 = null;
			else
				nextSpotAnim1 = null;
		} else {
			if (nextSpotAnim.equals(nextSpotAnim1) || nextSpotAnim.equals(nextSpotAnim2) || nextSpotAnim.equals(nextSpotAnim3) || nextSpotAnim.equals(nextSpotAnim4))
				return;
			if (nextSpotAnim1 == null)
				nextSpotAnim1 = nextSpotAnim;
			else if (nextSpotAnim2 == null)
				nextSpotAnim2 = nextSpotAnim;
			else if (nextSpotAnim3 == null)
				nextSpotAnim3 = nextSpotAnim;
			else
				nextSpotAnim4 = nextSpotAnim;
		}
	}

	public void tele(Tile tile) {
		if (this instanceof Player player)
			player.setTemporaryMoveType(MoveType.TELE);
		move(tile);
	}

	public void tele(int x, int y, int plane) {
		tele(Tile.of(x, y, plane));
	}

	public SpotAnim getNextSpotAnim1() {
		return nextSpotAnim1;
	}

	public SpotAnim getNextSpotAnim2() {
		return nextSpotAnim2;
	}

	public SpotAnim getNextSpotAnim3() {
		return nextSpotAnim3;
	}

	public SpotAnim getNextSpotAnim4() {
		return nextSpotAnim4;
	}

	public void setFaceAngle(int direction) {
		faceAngle = direction;
	}

	public int getFaceAngle() {
		return faceAngle;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean hasFinished() {
		return finished;
	}

	/**
	 * This method will NOT teleport the player if the player is close to the destination tile.
	 * @param tile
	 */
	public void move(Tile tile) {
		this.nextTile = Tile.of(tile);
	}

	public Tile getMoveTile() {
		return nextTile;
	}

	public boolean hasTeleported() {
		return teleported;
	}

	public Direction getNextWalkDirection() {
		return nextWalkDirection;
	}

	public Direction getNextRunDirection() {
		return nextRunDirection;
	}

	public void setRun(boolean run) {
		this.moveType = run ? MoveType.RUN : MoveType.WALK;
	}

	public boolean getRun() {
		return this.moveType == MoveType.RUN;
	}

	public Tile getNextFaceTile() {
		return nextFaceTile;
	}

	public Tile getFixedFaceTile() {
		return fixedFaceTile;
	}

	public Direction getDirection() {
		return Direction.fromAngle(getFaceAngle());
	}

	public void setNextFaceTile(Tile nextFaceTile) {
		if (nextFaceTile != null && nextFaceTile.getX() == getX() && nextFaceTile.getY() == getY())
			return;
		this.nextFaceTile = nextFaceTile;
		if (nextFaceTile == null)
			return;
		if (nextTile != null)
			faceAngle = Utils.getAngleTo(nextFaceTile.getX() - nextTile.getX(), nextFaceTile.getY() - nextTile.getY());
		else
			faceAngle = Utils.getAngleTo(nextFaceTile.getX() - getX(), nextFaceTile.getY() - getY());
	}

	public void setFixedFaceTile(Tile tile) {
		this.fixedFaceTile = tile;
	}

	public void faceDir(Direction dir) {
		setNextFaceTile(transform(dir.dx, dir.dy));
	}

	public abstract int getSize();

	public void stopFaceEntity() {
		setNextFaceEntity(null);
	}

	/**
	 * Use faceEntity() instead
	 * @param entity
	 */
	@Deprecated
	public void setNextFaceEntity(Entity entity) {
		if (entity == null) {
			nextFaceEntity = -1;
			lastFaceEntity = -1;
		} else {
			nextFaceEntity = entity.getClientIndex();
			lastFaceEntity = nextFaceEntity;
		}
	}

	public void faceEntity(Entity entity) {
		setNextFaceEntity(entity);
	}

	public int getNextFaceEntity() {
		return nextFaceEntity;
	}

	public int getLastFaceEntity() {
		return lastFaceEntity;
	}

	public void unfreeze() {
		removeEffect(Effect.FREEZE);
	}

	public void freeze() {
		freeze(Integer.MAX_VALUE, false);
	}

	public void freeze(int ticks) {
		freeze(ticks, false);
	}

	public void freeze(int ticks, boolean freezeBlock) {
		if (hasEffect(Effect.FREEZE_BLOCK) && freezeBlock)
			return;
		if (this instanceof Player p)
			p.sendMessage("You have been frozen!");
		resetWalkSteps();
		addEffect(Effect.FREEZE, ticks);
		if (freezeBlock)
			addEffect(Effect.FREEZE_BLOCK, ticks + 15);
	}

	public abstract double getMagePrayerMultiplier();

	public abstract double getRangePrayerMultiplier();

	public abstract double getMeleePrayerMultiplier();

	public Entity getAttackedBy() {
		return attackedBy;
	}

	public void setAttackedBy(Entity attackedBy) {
		this.attackedBy = attackedBy;
	}

	public boolean hasBeenHit(int time) {
		return (timeLastHit + time) > System.currentTimeMillis();
	}

	public void refreshTimeLastHit() {
		timeLastHit = System.currentTimeMillis();
	}

	public boolean inCombat(int time) {
		return (attackedByDelay + time) > System.currentTimeMillis();
	}

	public boolean inCombat() {
		return attackedByDelay > System.currentTimeMillis();
	}

	public boolean isAttacking() {
		if (this instanceof Player p)
			return p.getInteractionManager().doingInteraction(PlayerCombatInteraction.class);
		if (this instanceof NPC n)
			return n.getCombat().hasTarget();
		return false;
	}

	public void setAttackedByDelay(long attackedByDelay) {
		this.attackedByDelay = attackedByDelay;
	}

	public void checkMultiArea() {
		multiArea = forceMultiArea || World.isMultiArea(this.getTile());
	}

	public boolean isAtMultiArea() {
		if (this instanceof NPC n && n.isForceMultiAttacked())
			return true;
		return multiArea;
	}

	public void setAtMultiArea(boolean multiArea) {
		this.multiArea = multiArea;
	}

	public boolean isHasNearbyInstancedChunks() {
		return hasNearbyInstancedChunks;
	}

	public ForceMovement getNextForceMovement() {
		return nextForceMovement;
	}

	protected void setNextForceMovement(ForceMovement movement) {
		this.nextForceMovement = movement;
	}

	public void forceMoveVisually(Tile destination, int animation, int startClientCycles, int speedClientCycles) {
		forceMoveVisually(getTile(), destination, animation, startClientCycles, speedClientCycles);
	}

	public void forceMoveVisually(Tile start, Tile destination, int animation, int startClientCycles, int speedClientCycles) {
		if (animation != -1)
			anim(animation);
		setNextForceMovement(new ForceMovement(start, destination, startClientCycles, speedClientCycles));
	}

	public void forceMoveVisually(Direction dir, int distance, int animation, int startClientCycles, int speedClientCycles) {
		if (animation != -1)
			anim(animation);
		setNextForceMovement(new ForceMovement(getTile(), transform(dir.dx *distance, dir.dy *distance), startClientCycles, speedClientCycles));
	}

	public void forceMoveVisually(Tile destination, int startClientCycles, int speedClientCycles) {
		forceMoveVisually(destination, -1, startClientCycles, speedClientCycles);
	}

	public void forceMoveVisually(Direction dir, int distance, int startClientCycles, int speedClientCycles) {
		forceMoveVisually(dir, distance, -1, startClientCycles, speedClientCycles);
	}

	public void forceMove(Tile destination, int animation, int startClientCycles, int speedClientCycles, boolean autoUnlock, Runnable afterComplete) {
		forceMove(Tile.of(getTile()), destination, animation, startClientCycles, speedClientCycles, autoUnlock, afterComplete);
	}

	public void forceMove(Tile destination, Direction faceDir, int animation, int startClientCycles, int speedClientCycles) {
		forceMove(Tile.of(getTile()), destination, faceDir, animation, startClientCycles, speedClientCycles, true, null);
	}

	public void forceMove(Tile start, Tile destination, Direction faceDir, int animation, int startClientCycles, int speedClientCycles) {
		forceMove(start, destination, faceDir, animation, startClientCycles, speedClientCycles, true, null);
	}

	public void forceMove(Tile start, Tile destination, int animation, int startClientCycles, int speedClientCycles, boolean autoUnlock, Runnable afterComplete) {
		forceMove(start, destination, null, animation, startClientCycles, speedClientCycles, autoUnlock, afterComplete);
	}

	public void forceMove(Tile start, Tile destination, Direction faceDir, int animation, int startClientCycles, int speedClientCycles, boolean autoUnlock, Runnable afterComplete) {
		ForceMovement movement = new ForceMovement(start, destination, startClientCycles, speedClientCycles, faceDir == null ? Utils.getAngleTo(start, destination) : faceDir.getAngle());
		if (animation != -1)
			anim(animation);
		lock();
		resetWalkSteps();
		if (this instanceof Player player)
			player.setTemporaryMoveType(MoveType.WALK);
		move(destination);
		setNextForceMovement(movement);
		tasks.schedule(movement.getTickDuration(), () -> {
			if (autoUnlock)
				unlock();
			if (afterComplete != null)
				afterComplete.run();
		});
	}

	public void forceMove(Tile destination, int animation, int startClientCycles, int speedClientCycles, boolean autoUnlock) {
		forceMove(destination, animation, startClientCycles, speedClientCycles, autoUnlock, null);
	}

	public void forceMove(Tile start, Tile destination, int animation, int startClientCycles, int speedClientCycles, boolean autoUnlock) {
		forceMove(start, destination, animation, startClientCycles, speedClientCycles, autoUnlock, null);
	}

	public void forceMove(Tile destination, int startClientCycles, int speedClientCycles, boolean autoUnlock, Runnable afterComplete) {
		forceMove(destination, -1, startClientCycles, speedClientCycles, autoUnlock, afterComplete);
	}

	public void forceMove(Tile destination, int anim, int startClientCycles, int speedClientCycles, Runnable afterComplete) {
		forceMove(destination, anim, startClientCycles, speedClientCycles, true, afterComplete);
	}

	public void forceMove(Tile start, Tile destination, int anim, int startClientCycles, int speedClientCycles, Runnable afterComplete) {
		forceMove(start, destination, anim, startClientCycles, speedClientCycles, true, afterComplete);
	}

	public void forceMove(Tile destination, int anim, int startClientCycles, int speedClientCycles) {
		forceMove(destination, anim, startClientCycles, speedClientCycles, true, null);
	}

	public void forceMove(Direction dir, int distance, int anim, int startClientCycles, int speedClientCycles, boolean autoUnlock, Runnable afterComplete) {
		forceMove(transform(dir.dx *distance, dir.dy *distance), anim, startClientCycles, speedClientCycles, autoUnlock, afterComplete);
	}

	public void forceMove(Direction dir, int distance, int anim, int startClientCycles, int speedClientCycles, Runnable afterComplete) {
		forceMove(dir, distance, anim, startClientCycles, speedClientCycles, true, afterComplete);
	}

	public void forceMove(Direction dir, int distance, int anim, int startClientCycles, int speedClientCycles) {
		forceMove(dir, distance, anim, startClientCycles, speedClientCycles, true, null);
	}

	public void forceMove(Tile destination, int startClientCycles, int speedClientCycles, Runnable afterComplete) {
		forceMove(destination, -1, startClientCycles, speedClientCycles, true, afterComplete);
	}

	public void forceMove(Tile destination, int startClientCycles, int speedClientCycles) {
		forceMove(destination, -1, startClientCycles, speedClientCycles, true, null);
	}

	public void forceMove(Direction dir, int distance, int startClientCycles, int speedClientCycles, boolean autoUnlock, Runnable afterComplete) {
		forceMove(transform(dir.dx *distance, dir.dy *distance), -1, startClientCycles, speedClientCycles, autoUnlock, afterComplete);
	}

	public void forceMove(Direction dir, int distance, int startClientCycles, int speedClientCycles, Runnable afterComplete) {
		forceMove(dir, distance, -1, startClientCycles, speedClientCycles, true, afterComplete);
	}

	public void forceMove(Direction dir, int distance, int startClientCycles, int speedClientCycles) {
		forceMove(dir, distance, -1, startClientCycles, speedClientCycles, true, null);
	}

	public Poison getPoison() {
		return poison;
	}

	public ForceTalk getNextForceTalk() {
		return nextForceTalk;
	}

	/**
	 * Use forceTalk(string message) instead
	 * @param nextForceTalk
	 */
	@Deprecated
	public void setNextForceTalk(ForceTalk nextForceTalk) {
		this.nextForceTalk = nextForceTalk;
	}

	public void forceTalk(String message) {
		setNextForceTalk(new ForceTalk(message));
	}

	public void faceEntityTile(Entity target) {
		setNextFaceTile(Tile.of(target.getCoordFaceX(target.getSize()), target.getCoordFaceY(target.getSize()), target.getPlane()));
	}

	public void faceObject(GameObject object) {
		int x = -1, y = -1;
		if (object.getType() == ObjectType.WALL_STRAIGHT) { // wall
			if (object.getRotation() == 0) { // west
				x = object.getX() - 1;
				y = object.getY();
			} else if (object.getRotation() == 1) { // north
				x = object.getX();
				y = object.getY() + 1;
			} else if (object.getRotation() == 2) { // east
				x = object.getX() + 1;
				y = object.getY();
			} else if (object.getRotation() == 3) { // south
				x = object.getX();
				y = object.getY() - 1;
			}
		} else if (object.getType() == ObjectType.WALL_DIAGONAL_CORNER || object.getType() == ObjectType.WALL_WHOLE_CORNER) { // corner and corner wall
			if (object.getRotation() == 0) { // nw
				x = object.getX() - 1;
				y = object.getY() + 1;
			} else if (object.getRotation() == 1) { // ne
				x = object.getX() + 1;
				y = object.getY() + 1;
			} else if (object.getRotation() == 2) { // se
				x = object.getX() + 1;
				y = object.getY() - 1;
			} else if (object.getRotation() == 3) { // sw
				x = object.getX() - 1;
				y = object.getY() - 1;
			}
		} else if (object.getType() == ObjectType.WALL_STRAIGHT_CORNER) { // inverted corner
			if (object.getRotation() == 0) { // se
				x = object.getX() + 1;
				y = object.getY() - 1;
			} else if (object.getRotation() == 1) { // sw
				x = object.getX() - 1;
				y = object.getY() - 1;
			} else if (object.getRotation() == 2) { // nw
				x = object.getX() - 1;
				y = object.getY() + 1;
			} else if (object.getRotation() == 3) { // ne
				x = object.getX() + 1;
				y = object.getY() + 1;
			}
		} else if (object.getType().id < ObjectType.SCENERY_INTERACT.id) { // walldeco's
			if (object.getRotation() == 0) { // west
				x = object.getX() - 1;
				y = object.getY();
			} else if (object.getRotation() == 1) { // north
				x = object.getX();
				y = object.getY() + 1;
			} else if (object.getRotation() == 2) { // east
				x = object.getX() + 1;
				y = object.getY();
			} else if (object.getRotation() == 3) { // south
				x = object.getX();
				y = object.getY() - 1;
			}
		} else {
			// rest
			x = object.getCoordFaceX();
			y = object.getCoordFaceY();
		}
		setNextFaceTile(Tile.of(x, y, object.getPlane()));
	}

	public void faceTile(Tile tile) {
		setNextFaceTile(tile);
	}

	public long getLastAnimationEnd() {
		return lastAnimationEnd;
	}

	public GenericAttribMap getTempAttribs() {
		return temporaryAttributes;
	}

	public List<NPC> queryNearbyNPCsByTileRange(int tileRange, Function<NPC, Boolean> predicate) {
		List<NPC> startList = World.getNPCsInChunkRange(getChunkId(), ((tileRange / 8) + 1));
		List<NPC> list = new ObjectArrayList<>();
		for (NPC npc : startList) {
			if (predicate == null || predicate.apply(npc))
				list.add(npc);
		}
		return list;
	}

	public List<Player> queryNearbyPlayersByTileRange(int tileRange, Function<Player, Boolean> predicate) {
		List<Player> startList = World.getPlayersInChunkRange(getChunkId(), ((tileRange / 8) + 1));
		List<Player> list = new ObjectArrayList<>();
		for (Player npc : startList) {
			if (predicate == null || predicate.apply(npc))
				list.add(npc);
		}
		return list;
	}

	public List<Entity> queryNearbyPlayersByTileRangeAsEntityList(int tileRange, Function<Player, Boolean> predicate) {
		return queryNearbyPlayersByTileRange(tileRange, predicate).stream().map(e -> (Entity) e).collect(Collectors.toList());
	}

	public List<Entity> queryNearbyNPCsByTileRangeAsEntityList(int tileRange, Function<NPC, Boolean> predicate) {
		return queryNearbyNPCsByTileRange(tileRange, predicate).stream().map(e -> (Entity) e).collect(Collectors.toList());
	}

	public GenericAttribMap getNSV() {
		return nonsavingVars;
	}

	public boolean isForceMultiArea() {
		return forceMultiArea;
	}

	public Entity setForceMultiArea(boolean forceMultiArea) {
		this.forceMultiArea = forceMultiArea;
		checkMultiArea();
		return this;
	}

	public Tile getLastTile() {
		return lastTile;
	}

	public ArrayList<Hit> getNextHits() {
		return nextHits;
	}

	public int getSceneBaseChunkId() {
		return sceneBaseChunkId;
	}

	public long getFindTargetDelay() {
		return findTargetDelay;
	}

	public void setFindTargetDelay(long findTargetDelay) {
		this.findTargetDelay = findTargetDelay;
	}

	public BodyGlow getNextBodyGlow() {
		return nextBodyGlow;
	}

	public void setNextBodyGlow(BodyGlow nextBodyGlow) {
		this.nextBodyGlow = nextBodyGlow;
	}

	public ArrayList<HitBar> getNextHitBars() {
		return nextHitBars;
	}

	public int getLastChunkId() {
		return lastChunkId;
	}

	public void setLastChunkId(int lastChunkId) {
		this.lastChunkId = lastChunkId;
	}

	public Tile getTileBehind() {
		return tileBehind;
	}

	public void setRouteEvent(RouteEvent routeEvent) {
		this.resetWalkSteps();
		this.routeEvent = routeEvent;
	}

	public long getTickCounter() {
		return tickCounter;
	}

	public Vec2 getMiddleTileAsVector() {
		int size = getSize();
		if (size == 1)
			return new Vec2(this.getTile());
		return new Vec2(getX() + (size-1)/ 2.0f, getY() + (size-1)/ 2.0f);
	}

	public boolean inMeleeRange(Entity target) {
		return WorldUtil.isInRange(getX(), getY(), getSize(), target.getX(), target.getY(), target.getSize(), 0);
	}

	public boolean isForceUpdateEntityRegion() {
		return forceUpdateEntityRegion;
	}

	public void setForceUpdateEntityRegion(boolean forceUpdateEntityRegion) {
		this.forceUpdateEntityRegion = forceUpdateEntityRegion;
	}

	public CollisionStrategy getCollisionStrategy() {
		if (collisionStrategy == null)
			collisionStrategy = CollisionStrategyType.NORMAL.getStrategy();
		return collisionStrategy;
	}

	public Entity setCollisionStrategy(CollisionStrategy strategy) {
		this.collisionStrategy = strategy;
		return this;
	}

	public Entity setCollisionStrategyType(CollisionStrategyType type) {
		setCollisionStrategy(type == null ? CollisionStrategyType.NORMAL.getStrategy() : type.getStrategy());
		return this;
	}

	public Tile getNearestTeleTile(Entity toMove) {
		return getNearestTeleTile(toMove.getSize());
	}

	public Tile getNearestTeleTile(int size) {
		return World.findAdjacentFreeSpace(this.getTile(), size);
	}

	public Tile getNearestTeleTile(Direction... blacklistedDirections) {
		return World.findAdjacentFreeTile(this.getTile(), blacklistedDirections);
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public void setLocation(Tile tile) {
		this.tile = tile;
	}

	public void setLocation(int x, int y, int z) {
		this.tile = Tile.of(x, y, z);
	}

	public boolean isAt(int x, int y) {
		return tile.isAt(x, y);
	}

	public boolean isAt(int x, int y, int z) {
		return tile.isAt(x, y, z);
	}

	public int getX() {
		return tile.getX();
	}

	public int getXInRegion() {
		return tile.getXInRegion();
	}

	public int getYInRegion() {
		return tile.getYInRegion();
	}

	public int getXInChunk() {
		return tile.getXInChunk();
	}

	public int getYInChunk() {
		return tile.getYInChunk();
	}

	public int getY() {
		return tile.getY();
	}

	public int getPlane() {
		return tile.getPlane();
	}

	public int getChunkX() {
		return tile.getChunkX();
	}

	public int getChunkId() {
		return tile.getChunkId();
	}

	public int getChunkXInScene(int chunkId) {
		return tile.getChunkXInScene(chunkId);
	}

	public int getChunkYInScene(int chunkId) {
		return tile.getChunkYInScene(chunkId);
	}

	public int getXInScene(int chunkId) {
		return tile.getXInScene(chunkId);
	}

	public int getYInScene(int chunkId) {
		return tile.getYInScene(chunkId);
	}

	public int getChunkY() {
		return tile.getChunkY();
	}

	public int getRegionX() {
		return tile.getRegionX();
	}

	public int getRegionY() {
		return tile.getRegionY();
	}

	public int getRegionId() {
		return tile.getRegionId();
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Entity n)
			return n.hashCode() == hashCode();
		return false;
	}

	public int getRegionHash() {
		return tile.getRegionHash();
	}

	public int getTileHash() {
		return tile.getTileHash();
	}

	public boolean withinDistance(Tile other, int distance) {
		return tile.withinDistance(other, distance);
	}

	public boolean withinDistance(Tile tile) {
		return tile.withinDistance(tile);
	}

	public int getCoordFaceX(int sizeX) {
		return tile.getCoordFaceX(sizeX);
	}

	public int getCoordFaceX(int sizeX, int sizeY, int rotation) {
		return tile.getCoordFaceX(sizeX, sizeY, rotation);
	}

	public int getCoordFaceY(int sizeY) {
		return tile.getCoordFaceY(sizeY);
	}

	public int getCoordFaceY(int sizeX, int sizeY, int rotation) {
		return tile.getCoordFaceY(sizeX, sizeY, rotation);
	}

	public int getLongestDelta(Tile other) {
		return tile.getLongestDelta(other);
	}

	public Tile transform(int x, int y) {
		return tile.transform(x, y);
	}

	public Tile transform(int x, int y, int plane) {
		return tile.transform(x, y, plane);
	}

	public boolean matches(Tile other) {
		return tile.matches(other);
	}

	public boolean withinArea(int a, int b, int c, int d) {
		return tile.withinArea(a, b, c, d);
	}

	public boolean canAttackMulti(Entity target) {
		if(this instanceof Familiar && target.isForceMultiArea())
			return true;
		if(target instanceof Familiar && this.isForceMultiArea())
			return true;
		if ((this instanceof NPC n && n.isForceMultiAttacked()) || (target instanceof NPC npc && npc.isForceMultiAttacked()))
			return true;
		if (target.isAtMultiArea() && isAtMultiArea())
			return true;

		if (this instanceof Player p) {
			if (target instanceof Player) {
				if (getAttackedBy() instanceof Player && inCombat() && getAttackedBy() != target) {
					p.sendMessage("You are already in combat.");
					return false;
				}
				if (target.getAttackedBy() instanceof Player && target.inCombat() && target.getAttackedBy() != this) {
					p.sendMessage("They are already in combat.");
					return false;
				}
			} else if (target instanceof NPC) {
				if (inCombat() && getAttackedBy() != target) {
					p.sendMessage("You are already in combat.");
					return false;
				}
				if (target.inCombat() && target.getAttackedBy() != this) {
					p.sendMessage("They are already in combat.");
					return false;
				}
			}
		} else if (this instanceof NPC n) {
			if (inCombat() && getAttackedBy() != target)
				return false;
            return !target.inCombat() || target.getAttackedBy() == this;
		}
		return true;
	}

	public void anim(int anim) {
		setNextAnimation(new Animation(anim));
	}

	public void stopAnim() {
		anim(-1);
	}

	public void anim(Animation anim) {
		setNextAnimation(anim);
	}

	public void spotAnim(int spotAnim, int speed, int height) {
		setNextSpotAnim(new SpotAnim(spotAnim, speed, height));
	}

	public void spotAnim(int spotAnim, int speed) {
		setNextSpotAnim(new SpotAnim(spotAnim, speed));
	}

	public void spotAnim(int spotAnim) {
		setNextSpotAnim(new SpotAnim(spotAnim));
	}

	public void spotAnim(SpotAnim spotAnim) {
		setNextSpotAnim(spotAnim);
	}

	public void sync(int anim, int spotAnim) {
		anim(anim);
		spotAnim(spotAnim);
	}

	public void sync(Animation anim, SpotAnim spotAnim) {
		setNextAnimation(anim);
		setNextSpotAnim(spotAnim);
	}

	public InteractionManager getInteractionManager() {
		return interactionManager;
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public void follow(Entity target) {
		actionManager.setAction(new EntityFollow(target));
	}

	public int lowerStat(int skillId, double perc, double maxDrain) {
		if (this instanceof Player player) {
			if (skillId == Skills.HITPOINTS)
				return 0;
			if (skillId == Skills.PRAYER) {
				player.getPrayer().drainPrayer(player.getPrayer().getPoints() * perc);
				return (int) (player.getPrayer().getPoints() / 10);
			}
			return player.getSkills().lowerStat(skillId, perc, maxDrain);
		} else if (this instanceof NPC npc) {
			return switch(skillId) {
				case Skills.ATTACK -> npc.lowerAttack(perc, maxDrain);
				case Skills.STRENGTH -> npc.lowerStrength(perc, maxDrain);
				case Skills.DEFENSE -> npc.lowerDefense(perc, maxDrain);
				case Skills.MAGIC -> npc.lowerMagic(perc, maxDrain);
				case Skills.RANGE -> npc.lowerRange(perc, maxDrain);
				default -> 0;
			};
		}
		return 0;
	}

	public int lowerStat(int skillId, int amt, double maxDrain) {
		if (this instanceof Player player) {
			if (skillId == Skills.HITPOINTS)
				return 0;
			if (skillId == Skills.PRAYER) {
				player.getPrayer().drainPrayer(amt * 10);
				return (int) (player.getPrayer().getPoints() / 10);
			}
			return player.getSkills().lowerStat(skillId, amt, maxDrain);
		} else if (this instanceof NPC npc) {
			return switch(skillId) {
				case Skills.ATTACK -> npc.lowerAttack(amt, maxDrain);
				case Skills.STRENGTH -> npc.lowerStrength(amt, maxDrain);
				case Skills.DEFENSE -> npc.lowerDefense(amt, maxDrain);
				case Skills.MAGIC -> npc.lowerMagic(amt, maxDrain);
				case Skills.RANGE -> npc.lowerRange(amt, maxDrain);
                default -> 0;
            };
		}
		return 0;
	}

	public void repeatAction(int ticks, Function<Integer, Boolean> action) {
		getActionManager().setAction(new Action() {
			int count = 0;

			@Override
			public boolean start(Entity entity) {
				return true;
			}

			@Override
			public boolean process(Entity entity) {
				return true;
			}

			@Override
			public int processWithDelay(Entity entity) {
				if (action.apply(++count))
					return ticks;
				return -1;
			}

			@Override
			public void stop(Entity entity) {

			}
		});
	}

	public ModelRotator getBodyModelRotator() {
		return bodyModelRotator;
	}

	public void setBodyModelRotator(ModelRotator bodyModelRotator) {
		if (bodyModelRotator == null) {
			this.bodyModelRotator = ModelRotator.RESET;
			return;
		}
		this.bodyModelRotator = bodyModelRotator;
	}

	public int getBas() {
		return bas;
	}

	public void setBas(int basAnim) {
		if (basAnim == -1) {
			setBasNoReset(-2);
			return;
		}
		setBasNoReset(basAnim);
	}

	public void setBasNoReset(int bas) {
		this.bas = bas;
	}

	public boolean isLocked() {
		return lockDelay >= tickCounter;
	}

	/**
	 * You are invincible & cannot use your character until unlocked.
	 * All hits are processed after unlocking.
	 * If you use resetRecievedHits you lose those hits.
	 */
	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void lock(int ticks) {
		lockDelay = tickCounter + ticks;
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void unlockNextTick() {
		nextTickUnlock = true;
	}

	public TaskManager getTasks() {
		return tasks;
	}

	public void processTasks() {
		if (tasks != null)
			tasks.processTasks();
		if (asyncTasks != null)
			asyncTasks.tick();
	}

	public void clearPendingTasks() {
		tasks = new TaskManager();
		asyncTasks.stopAll();
	}

	public AsyncTaskScheduler getAsyncTasks() {
		return asyncTasks;
	}
}
