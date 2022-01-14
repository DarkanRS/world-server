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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions.MovementType;
import com.rs.cache.loaders.ObjectType;
import com.rs.cache.loaders.animations.AnimationDefinitions;
import com.rs.cache.loaders.map.RegionSize;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.dungeoneering.Stomp;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.ClipType;
import com.rs.game.pathing.Direction;
import com.rs.game.pathing.DumbRouteFinder;
import com.rs.game.pathing.EntityStrategy;
import com.rs.game.pathing.FixedTileStrategy;
import com.rs.game.pathing.ObjectStrategy;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.pathing.RouteFinder;
import com.rs.game.pathing.WalkStep;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.skills.prayer.Prayer;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.GenericAttribMap;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import com.rs.lib.util.Utils;
import com.rs.lib.util.Vec2;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.utils.WorldUtil;

public abstract class Entity extends WorldTile {

	private final static AtomicInteger HASH_CODE_GENERATOR = new AtomicInteger();

	// transient stuff
	private transient int index;
	private transient int sceneBaseChunkId;
	private transient int lastRegionId; // the last region the entity was at
	private transient int lastChunkId;
	private transient boolean forceUpdateEntityRegion;
	private transient Set<Integer> mapRegionIds;
	protected transient GenericAttribMap temporaryAttributes;
	protected transient GenericAttribMap nonsavingVars;
	private transient int faceAngle;
	private transient WorldTile lastWorldTile;
	private transient WorldTile tileBehind;
	private transient WorldTile nextWorldTile;
	private transient Direction nextWalkDirection;
	private transient Direction nextRunDirection;
	private transient WorldTile nextFaceWorldTile;
	private transient boolean teleported;
	private transient ConcurrentLinkedQueue<WalkStep> walkSteps;
	protected transient RouteEvent routeEvent;
	private transient ClipType clipType = ClipType.NORMAL;

	private transient BodyGlow nextBodyGlow;
	private transient ConcurrentLinkedQueue<Hit> receivedHits;
	private transient Map<Entity, Integer> receivedDamage;
	private transient boolean finished;
	private transient long tickCounter = 0;
	// entity masks
	private transient Animation nextAnimation;
	private transient SpotAnim nextSpotAnim1;
	private transient SpotAnim nextSpotAnim2;
	private transient SpotAnim nextSpotAnim3;
	private transient SpotAnim nextSpotAnim4;
	private transient ArrayList<Hit> nextHits;
	private transient ArrayList<HitBar> nextHitBars;
	private transient ForceMovement nextForceMovement;
	private transient ForceTalk nextForceTalk;
	private transient int nextFaceEntity;
	private transient int lastFaceEntity;
	private transient Entity attackedBy; // whos attacking you, used for single
	protected transient long attackedByDelay; // delay till someone else can
	// attack you
	private transient boolean multiArea;
	private transient boolean isAtDynamicRegion;
	private transient long lastAnimationEnd;
	private transient boolean forceMultiArea;
	private transient long findTargetDelay;
	private transient int hashCode;

	// saving stuff
	private int hitpoints;
	private RegionSize regionSize;

	private boolean run;
	private Poison poison;
	private Map<Effect, Long> effects = new HashMap<>();

	// creates Entity and saved classes
	public Entity(WorldTile tile) {
		super(tile);
		poison = new Poison();
	}

	public void clearEffects() {
		effects = new HashMap<>();
	}

	public boolean hasEffect(Effect effect) {
		return effects != null && effects.containsKey(effect);
	}

	public void addEffect(Effect effect, long ticks) {
		if (effects == null)
			effects = new HashMap<>();
		effects.put(effect, ticks);
		effect.apply(this);
		effect.tick(this, ticks);
	}

	public void removeEffect(Effect effect) {
		if (effect.sendWarnings() && this instanceof Player p)
			p.sendMessage(effect.getExpiryMessage());
		effects.remove(effect);
		effect.expire(this);
	}

	public void removeEffects(Effect... effects) {
		for (Effect e : effects)
			removeEffect(e);
	}

	private void processEffects() {
		if (effects == null)
			return;
		Set<Effect> expired = new HashSet<>();
		for (Effect effect : effects.keySet()) {
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
			if (getX() < other.getX())
				return true;
		return false;
	}

	public WorldTile getBackfacingTile() {
		int[] backFaceDirs = Utils.getBackFace(faceAngle);
		return transform(backFaceDirs[0], backFaceDirs[1], 0);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public boolean inArea(int a, int b, int c, int d) {
		return getX() >= a && getY() >= b && getX() <= c && getY() <= d;
	}

	public final void initEntity() {
		hashCode = HASH_CODE_GENERATOR.getAndIncrement();
		mapRegionIds = ConcurrentHashMap.newKeySet();
		walkSteps = new ConcurrentLinkedQueue<>();
		receivedHits = new ConcurrentLinkedQueue<>();
		receivedDamage = new ConcurrentHashMap<>();
		temporaryAttributes = new GenericAttribMap();
		nonsavingVars = new GenericAttribMap();
		nextHits = new ArrayList<>();
		nextHitBars = new ArrayList<>();
		nextWalkDirection = nextRunDirection = null;
		lastFaceEntity = -1;
		nextFaceEntity = -2;
		if (!(this instanceof NPC))
			faceAngle = 2;
		poison.setEntity(this);
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
		if (delay < 0)
			receivedHits.add(hit);
		else
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead()) {
						hit.setDamage(0);
						return;
					}
					handlePostHit(hit);
					if (onHit != null)
						onHit.run();
					receivedHits.add(hit);
				}
			}, delay);
	}

	public abstract void handlePreHit(Hit hit);
	public abstract void handlePreHitOut(Entity target, Hit hit);

	public void reset(boolean attributes) {
		setHitpoints(getMaxHitpoints());
		receivedHits.clear();
		resetCombat();
		walkSteps.clear();
		poison.reset();
		resetReceivedDamage();
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
			World.sendProjectile(user, this, 2263, 11, 11, 0, -1, 0, 0);
		user.heal(hit.getDamage() / 5);
		if (user instanceof Player p)
			p.incrementCount("Health soulsplitted back", hit.getDamage() / 5);
		if (this instanceof Player p)
			p.getPrayer().drainPrayer(hit.getDamage() / 5);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextSpotAnim(new SpotAnim(2264));
				if (hit.getDamage() > 0)
					World.sendProjectile(Entity.this, user, 2263, 11, 11, 0, -1, 0, 0);
			}
		}, 0);
	}

	protected void processHit(Hit hit) {
		if (isDead())
			return;
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
				if (Magic.sendItemTeleportSpell(player, true, 9603, 1684, 4, Settings.getConfig().getPlayerRespawnTile())) {
					player.getEquipment().set(Equipment.RING, null);
					player.getEquipment().refresh(Equipment.RING);
					player.sendMessage("Your ring of life saves you and is destroyed in the process.");
				}
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
				calcFollow(target, getRun() ? 2 : 1, true, true);
			}
		} else
			resetWalkSteps();
	}

	public boolean calcFollow(WorldTile target, boolean inteligent) {
		return calcFollow(target, -1, true, inteligent);
	}

	public abstract boolean canMove(Direction dir);

	public void handlePostHit(Hit hit) {

	}

	public boolean calcFollow(WorldTile target, int maxStepsCount, boolean calculate, boolean intelligent) {
		if (intelligent) {
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, getX(), getY(), getPlane(), getSize(), target instanceof GameObject go ? new ObjectStrategy(go) : target instanceof Entity e ? new EntityStrategy(e) : new FixedTileStrategy(target.getX(), target.getY()), true);
			if (steps == -1)
				return false;
			if (steps == 0)
				return true;
			int[] bufferX = RouteFinder.getLastPathBufferX();
			int[] bufferY = RouteFinder.getLastPathBufferY();
			for (int step = steps - 1; step >= 0; step--)
				if (!addWalkSteps(bufferX[step], bufferY[step], 25, true, true))
					break;
			return true;
		}
		return DumbRouteFinder.addDumbPathfinderSteps(this, target, getClipType());
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
		return !walkSteps.isEmpty();
	}

	public abstract void sendDeath(Entity source);

	public void updateAngle(WorldTile base, int sizeX, int sizeY) {
		WorldTile from = nextWorldTile != null ? nextWorldTile : this;
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

		lastWorldTile = new WorldTile(this);
		if (lastFaceEntity >= 0) {
			Entity target = lastFaceEntity >= 32768 ? World.getPlayers().get(lastFaceEntity - 32768) : World.getNPCs().get(lastFaceEntity);
			if (target != null) {
				int size = target.getSize();
				updateAngle(target, size, size);
			}
		}
		nextWalkDirection = nextRunDirection = null;
		if (nextWorldTile != null) {
			setLocation(nextWorldTile);
			tileBehind = getBackfacingTile();
			nextWorldTile = null;
			teleported = true;
			if (player != null && player.getTemporaryMoveType() == -1)
				player.setTemporaryMoveType(Player.TELE_MOVE_TYPE);
			World.updateEntityRegion(this);
			if (needMapUpdate())
				loadMapRegions();
			resetWalkSteps();
			return;
		}
		teleported = false;
		if (walkSteps.isEmpty())
			return;

		if (player != null) {
			if (player.getEmotesManager().isAnimating())
				return;
			if (player.getRunEnergy() <= 0.0 || player.isRunBlocked())
				setRun(false);
			if (walkSteps.size() <= 1)
				player.setTemporaryMoveType(Player.WALK_MOVE_TYPE);
		}

		if (npc != null)
			if (npc.getDefinitions().movementType == MovementType.HALF_WALK)
				if (npc.switchWalkStep())
					return;

		for (int stepCount = 0; stepCount < (run ? 2 : 1); stepCount++) {
			WalkStep nextStep = getNextWalkStep();
			if (nextStep == null)
				break;
			if (player != null)
				PluginManager.handle(new PlayerStepEvent(player, nextStep, new WorldTile(getX() + nextStep.getDir().getDx(), getY() + nextStep.getDir().getDy(), getPlane())));
			if ((nextStep.checkClip() && !World.checkWalkStep(getPlane(), getX(), getY(), nextStep.getDir(), getSize(), getClipType())) || (npc != null && !npc.checkNPCCollision(nextStep.getDir())) || !canMove(nextStep.getDir())) {
				resetWalkSteps();
				break;
			}
			if (stepCount == 0)
				nextWalkDirection = nextStep.getDir();
			else
				nextRunDirection = nextStep.getDir();
			tileBehind = new WorldTile(this);
			moveLocation(nextStep.getDir().getDx(), nextStep.getDir().getDy(), 0);
			if (run && stepCount == 0) { // fixes impossible steps TODO is this even necessary?
				WalkStep previewStep = previewNextWalkStep();
				if (previewStep == null)
					break;
				int dx = nextStep.getDir().getDx() + previewStep.getDir().getDx();
				int dy = nextStep.getDir().getDy() + previewStep.getDir().getDy();
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
		World.updateEntityRegion(this);
		if (needMapUpdate())
			loadMapRegions();
	}

	private WalkStep previewNextWalkStep() {
		WalkStep step = walkSteps.peek();
		if (step == null)
			return null;
		return step;
	}

	@Override
	public void moveLocation(int xOffset, int yOffset, int planeOffset) {
		super.moveLocation(xOffset, yOffset, planeOffset);
		faceAngle = Utils.getAngleTo(xOffset, yOffset);
	}

	private boolean needMapUpdate() {
		return needMapUpdate(this);
	}

	public boolean needMapUpdate(WorldTile tile) {
		int baseChunk[] = MapUtils.decode(Structure.CHUNK, sceneBaseChunkId);
		// chunks length - offset. if within 16 tiles of border it updates map
		int limit = getMapSize().size / 8 - 2;

		int offsetX = tile.getChunkX() - baseChunk[0];
		int offsetY = tile.getChunkY() - baseChunk[1];

		return offsetX < 2 || offsetX >= limit || offsetY < 2 || offsetY >= limit;
	}

	public boolean addWalkSteps(int destX, int destY) {
		return addWalkSteps(destX, destY, -1, true);
	}

	public WorldTile getMiddleWorldTile() {
		int size = getSize();
		return size == 1 ? this : new WorldTile(getCoordFaceX(size), getCoordFaceY(size), getPlane());
	}

	public boolean ignoreWallsWhenMeleeing() {
		return false;
	}

	public boolean lineOfSightTo(WorldTile tile, boolean melee) {
		if (tile instanceof NPC npc) {
			switch(npc.getId()) {
			case 2440:
			case 2443:
			case 2446:
			case 7567:
			case 3777:
			case 9712:
			case 9710:
			case 706:
			case 14860:
			case 14864:
			case 14858:
			case 14883:
			case 2859:
			case 8709://Desert musician
			case 8715://Drunken musician
			case 8723://Elf musician
			case 8712://Goblin musician
				return true;
			}
			switch(npc.getName()) {
			case "Xuan":
			case "Fremennik shipmaster":
			case "Fishing spot":
			case "Fishing Spot":
			case "Cavefish shoal":
			case "Rocktail shoal":
			case "Musician":
			case "Ghostly piper":
				return true;
			}
		}
		if (tile instanceof Stomp stomp)
			return stomp.getManager().isAtBossRoom(this);
		if (melee && !(tile instanceof Entity e ? e.ignoreWallsWhenMeleeing() : false)) {
            boolean f = World.checkMeleeStep(this, tile) && World.hasLineOfSight(getMiddleWorldTile(), tile instanceof Entity e ? e.getMiddleWorldTile() : tile);
            return f;
        }
		return World.hasLineOfSight(getMiddleWorldTile(), tile instanceof Entity e ? e.getMiddleWorldTile() : tile);
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
		return new int[] { step.getX(), step.getY() };
	}

	public boolean walkOneStep(int x, int y, boolean clipped) {
		return addWalkStep(getX() + x, getY() + y, getX(), getY(), clipped, false);
	}

	public boolean addWalkStep(int nextX, int nextY, int lastX, int lastY, boolean check) {
		return addWalkStep(nextX, nextY, lastX, lastY, check, true);
	}

	public boolean addWalkStep(int nextX, int nextY, int lastX, int lastY, boolean check, boolean force) {
		Direction dir = Direction.forDelta(nextX - lastX, nextY - lastY);
		if (dir == null)
			return false;
		if (!force && check && !World.checkWalkStep(getPlane(), lastX, lastY, dir, getSize(), getClipType()) || (this instanceof NPC n && !n.checkNPCCollision(dir)))// double
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
		if (step == null)
			return null;
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
		return nextBodyGlow != null || nextFaceEntity != -2 || nextAnimation != null || nextSpotAnim1 != null || nextSpotAnim2 != null || nextSpotAnim3 != null || nextSpotAnim4 != null || (nextWalkDirection == null && nextFaceWorldTile != null) || !nextHits.isEmpty() || !nextHitBars.isEmpty() || nextForceMovement != null || nextForceTalk != null;
	}

	public boolean isDead() {
		return hitpoints <= 0;
	}

	public void resetMasks() {
		nextBodyGlow = null;
		nextAnimation = null;
		nextSpotAnim1 = null;
		nextSpotAnim2 = null;
		nextSpotAnim3 = null;
		nextSpotAnim4 = null;
		if (nextWalkDirection == null)
			nextFaceWorldTile = null;
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
		if (routeEvent != null && routeEvent.processEvent(this))
			routeEvent = null;
		poison.processPoison();
		processMovement();
		processReceivedHits();
		processReceivedDamage();
		if (!isDead())
			if (tickCounter % 10 == 0)
				restoreHitPoints();
		processEffects();
	}

	public void loadMapRegions() {
		mapRegionIds.clear();
		isAtDynamicRegion = false;
		int chunkX = getChunkX();
		int chunkY = getChunkY();
		int sceneChunksRadio = getMapSize().size / 16;
		int sceneBaseChunkX = (chunkX - sceneChunksRadio);
		int sceneBaseChunkY = (chunkY - sceneChunksRadio);
		if (sceneBaseChunkX < 0)
			sceneBaseChunkX = 0;
		if (sceneBaseChunkY < 0)
			sceneBaseChunkY = 0;
		int fromRegionX = sceneBaseChunkX / 8;
		int fromRegionY = sceneBaseChunkY / 8;
		int toRegionX = (chunkX + sceneChunksRadio) / 8;
		int toRegionY = (chunkY + sceneChunksRadio) / 8;

		for (int regionX = fromRegionX; regionX <= toRegionX; regionX++)
			for (int regionY = fromRegionY; regionY <= toRegionY; regionY++) {
				int regionId = MapUtils.encode(Structure.REGION, regionX, regionY);
				Region region = World.getRegion(regionId, this instanceof Player);
				if (region instanceof DynamicRegion)
					isAtDynamicRegion = true;
				mapRegionIds.add(regionId);
			}
		sceneBaseChunkId = MapUtils.encode(Structure.CHUNK, sceneBaseChunkX, sceneBaseChunkY);
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

	public void setLastRegionId(int lastRegionId) {
		this.lastRegionId = lastRegionId;
	}

	public int getLastRegionId() {
		return lastRegionId;
	}

	public RegionSize getMapSize() {
		if (regionSize == null)
			regionSize = RegionSize.SIZE_104;
		return regionSize;
	}

	public void setMapSize(RegionSize size) {
		regionSize = size;
		loadMapRegions();
	}

	public Set<Integer> getMapRegionsIds() {
		return mapRegionIds;
	}

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

	public void setNextWorldTile(WorldTile nextWorldTile) {
		this.nextWorldTile = nextWorldTile;
	}

	public WorldTile getNextWorldTile() {
		return nextWorldTile;
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
		this.run = run;
	}

	public boolean getRun() {
		return run;
	}

	public WorldTile getNextFaceWorldTile() {
		return nextFaceWorldTile;
	}

	public Direction getDirection() {
		return Direction.fromAngle(getFaceAngle());
	}

	public void setNextFaceWorldTile(WorldTile nextFaceWorldTile) {
		if (nextFaceWorldTile != null && nextFaceWorldTile.getX() == getX() && nextFaceWorldTile.getY() == getY())
			return;
		this.nextFaceWorldTile = nextFaceWorldTile;
		if (nextFaceWorldTile == null)
			return;
		if (nextWorldTile != null)
			faceAngle = Utils.getAngleTo(nextFaceWorldTile.getX() - nextWorldTile.getX(), nextFaceWorldTile.getY() - nextWorldTile.getY());
		else
			faceAngle = Utils.getAngleTo(nextFaceWorldTile.getX() - getX(), nextFaceWorldTile.getY() - getY());
	}

	public void faceNorth() {
		setNextFaceWorldTile(new WorldTile(getX(), getY()+1, getPlane()));
	}

	public void faceEast() {
		setNextFaceWorldTile(new WorldTile(getX()+1, getY(), getPlane()));
	}

	public void faceSouth() {
		setNextFaceWorldTile(new WorldTile(getX(), getY()-1, getPlane()));
	}

	public void faceWest() {
		setNextFaceWorldTile(new WorldTile(getX()-1, getY(), getPlane()));
	}

	public abstract int getSize();

	public void cancelFaceEntityNoCheck() {
		nextFaceEntity = -2;
		lastFaceEntity = -1;
	}

	public void setNextFaceEntity(Entity entity) {
		if (entity == null) {
			nextFaceEntity = -1;
			lastFaceEntity = -1;
		} else {
			nextFaceEntity = entity.getClientIndex();
			lastFaceEntity = nextFaceEntity;
		}
	}

	public int getNextFaceEntity() {
		return nextFaceEntity;
	}

	public int getLastFaceEntity() {
		return lastFaceEntity;
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

	public boolean inCombat(int time) {
		return (attackedByDelay + time) > System.currentTimeMillis();
	}

	public boolean inCombat() {
		return attackedByDelay > System.currentTimeMillis();
	}

	public boolean isAttacking() {
		if (this instanceof Player p)
			return p.getActionManager().doingAction(PlayerCombat.class);
		if (this instanceof NPC n)
			return n.getCombat().hasTarget();
		return false;
	}

	public void setAttackedByDelay(long attackedByDelay) {
		this.attackedByDelay = attackedByDelay;
	}

	public void checkMultiArea() {
		multiArea = forceMultiArea ? true : World.isMultiArea(this);
	}

	public boolean isAtMultiArea() {
		if (this instanceof NPC n && n.isForceMultiAttacked())
			return true;
		return multiArea;
	}

	public void setAtMultiArea(boolean multiArea) {
		this.multiArea = multiArea;
	}

	public boolean isAtDynamicRegion() {
		return isAtDynamicRegion;
	}

	public ForceMovement getNextForceMovement() {
		return nextForceMovement;
	}

	public void setNextForceMovement(ForceMovement nextForceMovement) {
		this.nextForceMovement = nextForceMovement;
	}

	public Poison getPoison() {
		return poison;
	}

	public ForceTalk getNextForceTalk() {
		return nextForceTalk;
	}

	public void setNextForceTalk(ForceTalk nextForceTalk) {
		this.nextForceTalk = nextForceTalk;
	}

	public void forceTalk(String message) {
		setNextForceTalk(new ForceTalk(message));
	}

	public void faceEntity(Entity target) {
		setNextFaceWorldTile(new WorldTile(target.getCoordFaceX(target.getSize()), target.getCoordFaceY(target.getSize()), target.getPlane()));
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
		} else if (object.getType() == ObjectType.WALL_DIAGONAL_CORNER || object.getType() == ObjectType.WALL_WHOLE_CORNER) { // corner and cornerwall
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
		setNextFaceWorldTile(new WorldTile(x, y, object.getPlane()));
	}

	public void faceTile(WorldTile tile) {
		setNextFaceWorldTile(tile);
	}

	public long getLastAnimationEnd() {
		return lastAnimationEnd;
	}

	public GenericAttribMap getTempAttribs() {
		return temporaryAttributes;
	}

	public GenericAttribMap getNSV() {
		return nonsavingVars;
	}

	public boolean isForceMultiArea() {
		return forceMultiArea;
	}

	public void setForceMultiArea(boolean forceMultiArea) {
		this.forceMultiArea = forceMultiArea;
		checkMultiArea();
	}

	public WorldTile getLastWorldTile() {
		return lastWorldTile;
	}

	public ArrayList<Hit> getNextHits() {
		return nextHits;
	}

	public int getSceneBaseChunkId() {
		return sceneBaseChunkId;
	}

	public void playSound(int soundId, int type) {
		for (int regionId : getMapRegionsIds()) {
			Set<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null)
				for (int playerIndex : playerIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (player == null || !player.isRunning() || !withinDistance(player))
						continue;
					player.getPackets().sendSound(soundId, 0, type);
				}
		}
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

	public WorldTile getTileBehind() {
		return tileBehind;
	}

	public void setRouteEvent(RouteEvent routeEvent) {
		this.routeEvent = routeEvent;
	}

	public long getTickCounter() {
		return tickCounter;
	}

	public Vec2 getMiddleWorldTileAsVector() {
		int size = getSize();
		if (size == 1)
			return new Vec2(this);
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

	public ClipType getClipType() {
		if (clipType == null)
			clipType = ClipType.NORMAL;
		return clipType;
	}

	public void setClipType(ClipType clipType) {
		this.clipType = clipType;
	}
	
	public WorldTile getNearestTeleTile(Entity toMove) {
		return getNearestTeleTile(toMove.getSize());
	}
	
	public WorldTile getNearestTeleTile(int size) {
		WorldTile teleTile = null;
		for (int att = 0; att < 10; att++) {
			Direction dir = Direction.values()[Utils.random(Direction.values().length)];
			if (World.checkWalkStep(getPlane(), getX(), getY(), dir, size)) {
				teleTile = transform(dir.getDx(), dir.getDy(), 0);
				break;
			}
		}
		return teleTile;
	}
}
