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
package com.rs.game.content.skills.dungeoneering.npcs.bosses.blink;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class Blink extends DungeonBoss {

	private static final int[][] RUSH_COORDINATES =
		{
				{ 2, 3, 13, 3 },
				{ 2, 6, 13, 6 },
				{ 2, 9, 13, 9 },
				{ 2, 12, 13, 12 },
				{ 3, 2, 3, 13 },
				{ 6, 2, 6, 13 },
				{ 9, 2, 9, 13 },
				{ 12, 2, 12, 13 }, };
	//	private static final int[] FAILURE_SOUNDS = new int[]
	//	{ 3005, 3006, 3010, 3014, 3048, 2978 };
	//	private static final int[] RUSH_SOUNDS =
	//	{ 2982, 2987, 2988, 2989, 2990, 2992, 2998, 3002, 3004, 3009, 3015, 3017, 3018, 3021, 3026, 3027, 3031, 3042, 3043, 3047, 3049 };
	private static final String[] RUSH_MESSAGES =
		{
				"Grrrr...",
				"More t...tea Alice?",
				"Where...who?",
				"H..here it comes!",
				"See you all next year!",
				"",
				"",
				"",
				"Coo-coo-ca-choo!",
				"Ah! Grrrr...",
				"Aha! Huh? Ahaha!",
				"",
				"",
				"A face! A huuuge face!",
				"Aaahaahaha!",
				"C...can't catch me!",
				"A whole new world!",
				"Over here!",
				"There's no place like home.",
		"The...spire...doors...everywhere..." };

	private int rushCount, rushStage;
	private int[] selectedPath;
	private boolean inversedPath, specialRequired;
	private Tile toPath, activePillar;

	public Blink(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12865, 12878), manager.getBossLevel()), tile, manager, reference);
		setForceFollowClose(true);
		setHitpoints(getMaxHitpoints());
		setRun(true);
		rushCount = 0;
		rushStage = 4;
	}

	@Override
	public void processHit(Hit hit) {
		super.processHit(hit);
		if (getHitpoints() <= getMaxHitpoints() * (rushStage * .2125)) {
			rushStage--;
			rushCount = 0;
		}
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 1.0;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.8;
	}

	private void stopRushAttack() {
		rushCount = -1;//stops the rush
		//playSoundEffect(FAILURE_SOUNDS[Utils.random(FAILURE_SOUNDS.length)]);
		setNextForceTalk(new ForceTalk("Oof!"));
		setNextAnimation(new Animation(14946));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				setSpecialRequired(true);
				setCantInteract(false);
			}
		});
	}

	@Override
	public boolean canMove(Direction dir) {
		if (!hasActivePillar() || rushCount < 11)
			return true;
		int nextX = dir.getDx() + getX();
		int nextY = dir.getDy() + getY();
		if (nextX == activePillar.getX() && nextY == activePillar.getY()) {
			stopRushAttack();
			return false;
		}
		return true;
	}

	@Override
	public void processNPC() {
		super.processNPC();

		if (rushCount > -1) {
			if (getManager().isDestroyed() || isDead())
				return;
			rushCount++;
			if (rushCount == 1) {
				resetWalkSteps();
				setNextFaceEntity(null);
				resetCombat();
				setCantInteract(true);
			} else if (rushCount == 3)
				setNextForceTalk(new ForceTalk("He saw me!"));
			//playSoundEffect(3017);
			else if (rushCount == 4) {
				setNextAnimation(new Animation(14994));
				setNextSpotAnim(new SpotAnim(2868));
			} else if (rushCount == 15 || rushCount == 5) {
				if (rushCount == 15)
					rushCount = 5;
				setNextNPCTransformation(1957);
			} else if (rushCount == 8)
				setNextTile(getNextPath());
			else if (rushCount == 9) {
				setNextNPCTransformation(12865);
				toPath = getManager().getTile(getReference(), selectedPath[inversedPath ? 2 : 0], selectedPath[inversedPath ? 3 : 1]);
				addWalkSteps(toPath.getX(), toPath.getY(), 1, false);
			} else if (rushCount == 10) {
				addWalkSteps(toPath.getX(), toPath.getY(), -1, false);
				int index = Utils.random(RUSH_MESSAGES.length);
				setNextForceTalk(new ForceTalk(RUSH_MESSAGES[index]));
				//playSoundEffect(RUSH_SOUNDS[index]);
			} else if (rushCount == 11) {
				setNextSpotAnim(new SpotAnim(2869));
				for (Player player : getManager().getParty().getTeam()) {
					if (!getManager().getCurrentRoomReference(getTile()).equals(getManager().getCurrentRoomReference(player.getTile())) || !WorldUtil.isInRange(player.getX(), player.getY(), 1, getX(), getY(), 1, 4))
						continue;
					int damage = Utils.random(200, 600);
					if (player.getPrayer().isProtectingMage() || player.getPrayer().isProtectingRange())
						damage *= .5D;
					player.setNextSpotAnim(new SpotAnim(2854));
					player.applyHit(new Hit(this, damage, HitLook.TRUE_DAMAGE, 35));

				}
			}
		}
	}

	public Tile getNextPath() {
		selectedPath = RUSH_COORDINATES[Utils.random(RUSH_COORDINATES.length)];
		inversedPath = Utils.random(2) == 0;
		return getManager().getTile(getReference(), selectedPath[inversedPath ? 0 : 2], selectedPath[inversedPath ? 1 : 3]);
	}

	public void raisePillar(GameObject selectedPillar) {
		final GameObject newPillar = new GameObject(selectedPillar);
		newPillar.setId(32196);//Our little secret :D
		activePillar = Tile.of(selectedPillar.getTile());
		World.spawnObjectTemporary(newPillar, 4);
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				activePillar = null;
			}
		}, 4);
	}

	public boolean hasActivePillar() {
		return activePillar != null;
	}

	public boolean isSpecialRequired() {
		return specialRequired;
	}

	public void setSpecialRequired(boolean specialRequired) {
		this.specialRequired = specialRequired;
	}
}
