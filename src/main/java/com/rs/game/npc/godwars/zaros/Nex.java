package com.rs.game.npc.godwars.zaros;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.godwars.zaros.attack.NexAttack;
import com.rs.game.pathing.Direction;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public final class Nex extends NPC {
	
	public enum Phase {
		SMOKE,
		SHADOW,
		BLOOD,
		ICE,
		ZAROS
	}

	private NexArena arena;
	private boolean followTarget;
	private Phase phase;
	private int minionStage;
	private int attackCount = 0;
	private long ticksLastAttack;
	
	private NPC[] bloodReavers;

	public Nex(NexArena arena, WorldTile tile) {
		super(13447, tile, true);
		this.arena = arena;
		setCantInteract(true);
		setCapDamage(500);
		setLureDelay(3000);
		setIntelligentRouteFinder(true);
		setRun(true);
		bloodReavers = new NPC[3];
		setIgnoreDocile(true);
		phase = Phase.SMOKE;
		attackCount = 1;
		refreshTicksAttacked();
	}
	
	public static ObjectClickHandler handleIcePrison = new ObjectClickHandler(new Object[] { 57263 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getTempB("inIcePrison")) {
				e.getPlayer().sendMessage("You can't move!");
				return;
			}
			e.getPlayer().setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(e.getPlayer().getEquipment().getWeaponId(), e.getPlayer().getCombatDefinitions().getAttackStyle())));
			e.getPlayer().lock(2);
			World.removeObject(e.getObject());
		}
	};

	@Override
	public void processNPC() {
		if (phase == Phase.SMOKE && minionStage == 0 && getHitpoints() <= 24000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Fumus, don't fail me!"));
			getCombat().addCombatDelay(1);
			arena.breakFumusBarrier();
			playSound(3321, 2);
			minionStage = 1;
		} else if (phase == Phase.SHADOW && minionStage == 1 && getHitpoints() <= 18000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Umbra, don't fail me!"));
			getCombat().addCombatDelay(1);
			arena.breakUmbraBarrier();
			playSound(3307, 2);
			minionStage = 2;
		} else if (phase == Phase.BLOOD && minionStage == 2 && getHitpoints() <= 12000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Cruor, don't fail me!"));
			getCombat().addCombatDelay(1);
			arena.breakCruorBarrier();
			playSound(3298, 2);
			minionStage = 3;
		} else if (phase == Phase.ICE && minionStage == 3 && getHitpoints() <= 6000) {
			setCapDamage(0);
			setNextForceTalk(new ForceTalk("Glacies, don't fail me!"));
			getCombat().addCombatDelay(1);
			arena.breakGlaciesBarrier();
			playSound(3327, 2);
			minionStage = 4;
		}
		if (isDead() || isCantInteract())
			return;
		if (!getCombat().process()) {
			checkAggressivity();
			if (getTarget() == null)
				return;
			Entity target = getTarget();
			int maxDistance = isFollowTarget() ? 0 : 9;
			if ((!lineOfSightTo(target, isFollowTarget())) || !WorldUtil.isInRange(getX(), getY(), getSize(), target.getX(), target.getY(), target.getSize(), maxDistance)) {
				resetWalkSteps();
				if (!WorldUtil.isInRange(getX(), getY(), getSize(), target.getX(), target.getY(), target.getSize(), 5)) {
					int[][] dirs = Utils.getCoordOffsetsNear(getSize());
					for (int dir = 0; dir < dirs[0].length; dir++) {
						final WorldTile tile = new WorldTile(new WorldTile(target.getX() + dirs[0][dir], target.getY() + dirs[1][dir], target.getPlane()));
						if (World.floorAndWallsFree(tile, getSize())) {
							setNextForceMovement(new ForceMovement(new WorldTile(this), 0, tile, 1, Direction.forDelta(tile.getX() - getX(), tile.getY() - getY())));
							setNextAnimation(new Animation(6985));
							setNextWorldTile(tile);
							return;
						}
					}
				} else
					calcFollow(target, 2, true, true);
				return;
			} else
				resetWalkSteps();
		}
	}

	@Override
	public void sendDeath(Entity source) {
		transformIntoNPC(13450);
		final NPCCombatDefinitions defs = getCombatDefinitions();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					finish();
					arena.endWar();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		setNextForceTalk(new ForceTalk("Taste my wrath!"));
		playSound(3323, 2);
		sendWrath();
	}
	
	public void sendWrath() {
		setNextSpotAnim(new SpotAnim(2259));
		sendWrathProj(this, new WorldTile(getX() + 3, getY() + 3, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() + 3, getY(), getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() + 3, getY() - 3, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() - 3, getY() + 3, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() - 3, getY(), getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() - 3, getY() - 3, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX(), getY() + 3, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX(), getY() - 3, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() + 2, getY() - 2, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() - 2, getY() + 2, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() + 2, getY() + 2, getPlane()), 0.4);
		sendWrathProj(this, new WorldTile(getX() - 2, getY() - 2, getPlane()), 0.4);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				List<Entity> possibleTargets = getPossibleTargets();

				if (possibleTargets != null) {
					for (Entity entity : possibleTargets) {
						if (entity == null || entity.isDead() || entity.hasFinished() || !entity.withinDistance(Nex.this, 5))
							continue;
						entity.applyHit(new Hit(Nex.this, Utils.getRandomInclusive(600), HitLook.TRUE_DAMAGE));
					}
				}
			}
		}, 5);
	}
	
	public static void sendWrathProj(Entity nex, WorldTile tile, double speed) {
		World.sendProjectile(nex, tile, 2261, 24, 0, 1, speed, 30, 0, () -> {
			World.sendSpotAnim(nex, new SpotAnim(2260), tile);
		});
	}

	public ArrayList<Entity> calculatePossibleTargets(WorldTile current, WorldTile position, boolean northSouth) {
		ArrayList<Entity> list = new ArrayList<Entity>();
		for (Entity e : getPossibleTargets()) {
			if (e.inArea(current.getX(), current.getY(), position.getX() + (northSouth ? 2 : 0), position.getY() + (!northSouth ? 2 : 0))

			|| e.inArea(position.getX(), position.getY(), current.getX() + (northSouth ? 2 : 0), current.getY() + (!northSouth ? 2 : 0)))
				list.add(e);
		}
		return list;
	}

	public void nextPhase() {
		if (phase == Phase.SMOKE && minionStage == 1) {
			setCapDamage(500);
			setNextForceTalk(new ForceTalk("Darken my shadow!"));
			World.sendProjectile(arena.umbra, this, 2244, 18, 18, 60, 30, 0, 0);
			getCombat().addCombatDelay(1);
			playSound(3302, 2);
		} else if (phase == Phase.SHADOW && minionStage == 2) {
			setCapDamage(500);
			setNextForceTalk(new ForceTalk("Flood my lungs with blood!"));
			World.sendProjectile(arena.cruor, this, 2244, 18, 18, 60, 30, 0, 0);
			getCombat().addCombatDelay(1);
			playSound(3306, 2);
		} else if (phase == Phase.BLOOD && minionStage == 3) {
			setCapDamage(500);
			killBloodReavers();
			setNextForceTalk(new ForceTalk("Infuse me with the power of ice!"));
			World.sendProjectile(arena.glacies, this, 2244, 18, 18, 60, 30, 0, 0);
			getCombat().addCombatDelay(1);
			playSound(3303, 2);
		} else if (phase == Phase.ICE && minionStage == 4) {
			setCapDamage(500);
			setNextForceTalk(new ForceTalk("NOW, THE POWER OF ZAROS!"));
			setNextAnimation(new Animation(6326));
			setNextSpotAnim(new SpotAnim(1204));
			getCombat().addCombatDelay(1);
			heal(6000);
			playSound(3312, 2);
		}
	}
	
	public void checkPhase() {
		switch(phase) {
		case SMOKE:
			if (minionStage == 1 && (arena.fumus == null || arena.fumus.isDead() || arena.fumus.hasFinished()))
				setPhase(Phase.SHADOW);
			break;
		case SHADOW:
			if (minionStage == 2 && (arena.umbra == null || arena.umbra.isDead() || arena.umbra.hasFinished()))
				setPhase(Phase.BLOOD);
			break;
		case BLOOD:
			if (minionStage == 3 && (arena.cruor == null || arena.cruor.isDead() || arena.cruor.hasFinished()))
				setPhase(Phase.ICE);
			break;
		case ICE:
			if (minionStage == 4 && (arena.glacies == null || arena.glacies.isDead() || arena.glacies.hasFinished()))
				setPhase(Phase.ZAROS);
			break;
		default:
			break;
		}
	}

	public void switchPrayers() {
		if (this.isDead())
			return;
		transformIntoNPC(getId() == 13449 ? 13447 : getId() + 1);
	}

	@Override
	public void handlePreHit(Hit hit) {
		checkPhase();
		if (getTempB("siphoning"))
			hit.setHealHit();
		if (getId() == 13449 && hit.getLook() == HitLook.MELEE_DAMAGE) {
			Entity source = hit.getSource();
			if (source != null) {
				int deflectedDamage = (int) (hit.getDamage() * 0.1);
				hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
				if (deflectedDamage > 0)
					source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
			}
		}
		super.handlePreHit(hit);
	}

	@Override
	public void setNextAnimation(Animation nextAnimation) {
		if (getTempB("siphoning"))
			return;
		super.setNextAnimation(nextAnimation);
	}

	@Override
	public void setNextSpotAnim(SpotAnim nextGraphic) {
		if (getTempB("siphoning"))
			return;
		super.setNextSpotAnim(nextGraphic);
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public List<Entity> getPossibleTargets() {
		return arena.getPossibleTargets();
	}

	public boolean isFollowTarget() {
		return followTarget;
	}

	public void setFollowTarget(boolean followTarget) {
		this.followTarget = followTarget;
	}
	
	public void setPhase(Phase phase) {
		this.phase = phase;
		this.attackCount = 0;
	}
	
	public NPC[] getBloodReavers() {
		return bloodReavers;
	}

	public void killBloodReavers() {
		for (int index = 0; index < bloodReavers.length; index++) {
			if (bloodReavers[index] == null)
				continue;
			NPC npc = bloodReavers[index];
			bloodReavers[index] = null;
			if (npc.isDead())
				return;
			heal(npc.getHitpoints());
			npc.sendDeath(this);
		}
	}

	public Phase getPhase() {
		return phase;
	}
	
	public NexArena getArena() {
		return arena;
	}

	public int getAttackCount() {
		return attackCount;
	}

	public void incrementAttack() {
		refreshTicksAttacked();
		attackCount++;
	}

	public int performAttack(Entity target, NexAttack attack) {
		incrementAttack();
		return attack.attack(this, target);
	}

	public boolean shouldStopMeleeing() {
		return World.getServerTicks() - ticksLastAttack > 20;
	}

	public void refreshTicksAttacked() {
		ticksLastAttack = World.getServerTicks();
	}
}
