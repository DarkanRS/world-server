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
package com.rs.game.content.skills.dungeoneering.npcs;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.content.skills.dungeoneering.npcs.combat.YkLagorThunderousCombat;
import com.rs.game.content.skills.prayer.Prayer;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.List;

public class KalGerWarmonger extends DungeonBoss {

	private static final int SIZE = 5;
	private static final int[] WEAPONS =
		{ -1, 56057, 56054, 56056, 56055, 56053 };
	private static final int[][] FLY_COORDINATES =
		{ { 4, 2 },//correct
				{ 0, 0 },//correct cuz he doesn't even fly
				{ 10, 10 },//correct
				{ 10, 2 },//correct
				{ 5, 10 },//correct
				{ 5, 3 } };//correct

	private WarpedSphere sphere;
	private Tile nextFlyTile;
	private GameObject nextWeapon;
	private int type, typeTicks, pullTicks, annoyanceMeter;
	private boolean stolenEffects;

	public KalGerWarmonger(Tile tile, final DungeonManager manager, final RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12752, 12766), manager.getBossLevel()), tile, manager, reference);
		setCapDamage(5000);
		setCantInteract(true);
		typeTicks = -1;
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				//playSoundEffect(3033);
				setNextForceTalk(new ForceTalk("NOW IT'S YOUR TURN!"));
				sphere = new WarpedSphere(reference, 12842, manager.getTile(reference, 11, 12), manager);
				beginFlyCount();
			}
		}, 3);
	}

	private void beginFlyCount() {
		type++;
		if (type != 1)
			setHitpoints((int) (getHitpoints() + (getMaxHitpoints() * .15D)));// He heals a bit not 100% sure what the multiplier is
		typeTicks = type == 2 ? 7 : 0;
		setCantInteract(true);
		setNextFaceEntity(null);//Resets?
	}



	@Override
	public void processHit(Hit hit) {
		if (type != 6) {
			int max_hp = getMaxHitpoints(), nextStageHP = (int) (max_hp - (max_hp * (type * .20)));
			if (getHitpoints() - hit.getDamage() < nextStageHP) {
				hit.setDamage(getHitpoints() - (nextStageHP - 1));
				beginFlyCount();
			}
		}
		super.processHit(hit);
	}

	@Override
	public void handlePreHitOut(Entity target, Hit hit) {
		if (annoyanceMeter == 10) {
			annoyanceMeter = 0;// resets it
			if (target instanceof Player player) {
				player.setProtectionPrayBlock(2);
				player.sendMessage("You have been injured and cannot use protective prayers.");
			}
			hit.setDamage(target.getHitpoints() - 1);
		} else if (hit.getDamage() == 0)
			if (target instanceof Player player)
				if (player.getPrayer().isUsingProtectionPrayer())
					annoyanceMeter++;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (typeTicks >= 0) {
			processNextType();
			typeTicks++;
		} else if (type != 0) {
			pullTicks++;
			if (isMaximumPullTicks()) {
				submitPullAttack();
				return;
			}
		}
	}

	public boolean isUsingMelee() {
		return getAttackStyle() == AttackStyle.MELEE;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		setNextSpotAnim(new SpotAnim(2754));
		setNextForceTalk(new ForceTalk("Impossible!"));

		final NPC boss = this;
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				for (Entity t : getPossibleTargets())
					if (Utils.inCircle(t.getTile(), boss.getTile(), 8))
						t.applyHit(new Hit(boss, Utils.random(300, 990), HitLook.TRUE_DAMAGE));
			}
		}, 2);
	}

	@Override
	public int getMaxHit() {
		return getCombatLevel() < 300 ? 400 : 650;
	}

	@Override
	public int getMaxHitpoints() {
		return super.getMaxHitpoints() * 2;//Maybe * 3
	}

	@Override
	public double getMagePrayerMultiplier() {
		return .6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return .6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return stolenEffects ? 1 : type == 4 ? .3 : .6;
	}

	private void processNextType() {
		if (getManager().isDestroyed()) // Should fix some nullpointers
			return;

		if (typeTicks == 1) {
			setNextSpotAnim(new SpotAnim(2870));
			final int[] FLY_LOCATION = FLY_COORDINATES[type - 1];
			nextFlyTile = getManager().getTile(getReference(), FLY_LOCATION[0], FLY_LOCATION[1], SIZE, SIZE);
			forceMove(nextFlyTile, 14995, 25, 150, () -> spotAnim(2870));
		} else if (typeTicks == 9) {
			if (type == 1) {
				typeTicks = 16;
				return;
			}
			int selectedWeapon = WEAPONS[type - 1];
			outer: for (int x = 0; x < 16; x++)
				for (int y = 0; y < 16; y++) {
					GameObject next = getManager().getObjectWithType(getReference(), ObjectType.SCENERY_INTERACT, x, y);
					if (next != null && next.getId() == selectedWeapon) {
						nextWeapon = next;
						break outer;
					}
				}
			calcFollow(nextWeapon, false);
		} else if (typeTicks == 11) {
			faceObject(nextWeapon);
			setNextAnimation(new Animation(15027));
		} else if (typeTicks == 13)
			setNextAnimation(new Animation(14923 + (type - 1)));
		else if (typeTicks == 14) {
			setNextNPCTransformation(getId() + 17);
			World.removeObject(nextWeapon);
		} else if (typeTicks == 17) {
			if (type == 6)
				stealPlayerEffects();
			sphere.nextStage();
			setCantInteract(false);
			typeTicks = -2;// cuz it increments by one
		}
	}

	private void submitPullAttack() {
		//playSoundEffect(3025);
		setNextForceTalk(new ForceTalk("You dare hide from me? BURN!"));
		setNextAnimation(new Animation(14996));
		final NPC boss = this;
		WorldTasks.schedule(new WorldTask() {

			private int ticks;
			private List<Entity> possibleTargets;

			@Override
			public void run() {
				ticks++;
				if (ticks == 1) {
					possibleTargets = getPossibleTargets();
					Tile tile = getManager().getTile(getReference(), 9, 8);
					for (Entity t : possibleTargets)
						if (t instanceof Player player) {
							player.setCantWalk(true);
							YkLagorThunderousCombat.sendPullAttack(tile, player, false);
						}
				} else if (ticks == 10) {
					for (Entity t : getPossibleTargets())
						t.setNextTile(Tile.of(boss.getTile()));
					stop();
					pullTicks = 0;
					return;
				} else if (ticks > 3)
					for (Entity t : possibleTargets) {
						if (!getManager().isAtBossRoom(t.getTile()))
							continue;
						((Player) t).setCantWalk(false);
						if (Utils.random(5) == 0)
							t.setNextForceTalk(new ForceTalk("Ow!"));
						if (ticks == 8) {
							t.setNextAnimation(new Animation(14388));
							setNextAnimation(new Animation(14996));
						}
						t.applyHit(new Hit(boss, Utils.random(33, 87), HitLook.TRUE_DAMAGE));
					}
			}
		}, 0, 0);
	}

	private void stealPlayerEffects() {
		//playSoundEffect(3029);
		setNextForceTalk(new ForceTalk("Your gods can't help you now!"));
		for (Player player : getManager().getParty().getTeam()) {
			if (!getManager().getCurrentRoomReference(player.getTile()).equals(getReference()))
				continue;
			boolean usingPiety = player.getPrayer().active(Prayer.PIETY);
			boolean usingTurmoil = player.getPrayer().active(Prayer.TURMOIL);
			if (!usingPiety && !usingTurmoil)
				continue;
			player.sendMessage("The Warmonger steals your " + (usingPiety ? "Piety" : "Turmoil") + " effects!");
			stolenEffects = true;
		}
	}

	public int getType() {
		return type;
	}

	public void setPullTicks(int pullCount) {
		pullTicks = pullCount;
	}

	public boolean hasStolenEffects() {
		return stolenEffects;
	}

	public int getAnnoyanceMeter() {
		return annoyanceMeter;
	}

	public void setAnnoyanceMeter(int annoyanceMeter) {
		this.annoyanceMeter = annoyanceMeter;
	}

	public boolean isMaximumPullTicks() {
		return pullTicks == 35;
	}
}
