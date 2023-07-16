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

import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class FamishedEye extends DungeonNPC {

	private int weakness, type, sleepCycles;
	private boolean firstHit;
	private WorldGorgerShukarhazh boss;

	public FamishedEye(final WorldGorgerShukarhazh boss, int id, Tile tile, final DungeonManager manager) {
		super(id, tile, manager);
		this.boss = boss;
		sleepCycles = -1;
		int rotationType = (id - 12436) / 15;
		weakness = findWeakness(rotationType);
		type = findType(rotationType);
		setCantFollowUnderCombat(true);
		setForceAgressive(true);
		int rotation = manager.getRoom(manager.getCurrentRoomReference(getTile())).getRotation() + rotationType;
		setFaceAngle(Utils.getAngleTo(Utils.ROTATION_DIR_X[(rotation + 1) & 0x3], Utils.ROTATION_DIR_Y[(rotation + 1) & 0x3]));
	}

	@Override
	public boolean lineOfSightTo(Object tile, boolean checkClose) {
		//because npc is under cliped data
		return getManager().isAtBossRoom(WorldUtil.targetToTile(tile));
	}

	@Override
	public void setNextFaceEntity(Entity entity) {
		//this boss doesnt face
	}

	@Override
	public void processHit(Hit hit) {
		if (weakness != hit.getLook().getMark())
			hit.setDamage((int) (hit.getDamage() * 0.2));
		super.processHit(hit);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (sleepCycles == 45)
			resetSleepCycles();
		else if (sleepCycles >= 0)
			sleepCycles++;
	}

	private void resetSleepCycles() {
		sleepCycles = -1;
		setCantInteract(false);
		setNextAnimation(new Animation(-1));
		setHitpoints(getMaxHitpoints());
		boss.refreshCapDamage();
		for (Entity t : getPossibleTargets())
			if (t instanceof Player player)
				player.sendMessage("The creature shifts, and one of it's many eyes opens.");
	}

	@Override
	public void drop() {

	}

	@Override
	public void sendDeath(Entity source) {
		sleepCycles++;//start the cycles
		boss.refreshCapDamage();
		setCantInteract(true);
		setNextAnimation(new Animation(14917));
		for (Entity t : getPossibleTargets())
			if (t instanceof Player player)
				player.sendMessage("The creature shifts, and one of it's many eyes closes.");
	}

	public boolean isInactive() {
		return sleepCycles != -1;
	}

	public WorldGorgerShukarhazh getBoss() {
		return boss;
	}

	public int getType() {
		return type;
	}

	public int getWeaknessStyle() {
		return weakness;
	}

	private int findType(int rotation) {
		switch (type) {
		case 0: //mage
			return 2;
		case 1: //warrior
			return 0;
		default: //range
			return 1;
		}
	}

	private int findWeakness(int type) {
		switch (type) {
		case 0: //mage
			return 1; //range was mage 2
		case 1: //warrior
			return 2; //mage was melee 0
		default: //range
			return 0; //melee was range 1
		}
	}

	public boolean isFirstHit() {
		return firstHit;
	}

	public void setFirstHit(boolean firstHit) {
		this.firstHit = firstHit;
	}
}
