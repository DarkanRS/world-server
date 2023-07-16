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
package com.rs.game.content.minigames.pest.npcs;

import com.rs.game.content.minigames.pest.PestControl;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class PestPortal extends NPC {

	boolean isLocked;
	PestControl control;
	int ticks;

	public PestPortal(int id, boolean canbeAttackedOutOfArea, Tile tile, PestControl control) {
		super(id, tile, true);
		this.control = control;
		setCantFollowUnderCombat(true);
		setForceMultiArea(true);
		setCapDamage(400);
		isLocked = true;
	}

	@Override
	public boolean isLocked() {
		return isLocked;
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	public void unlock() {
		if (getId() >= 6146) {
			transformIntoNPC(getId() - 4);
			control.sendTeamMessage(getStringForId() + " portal shield has been dropped!");
		}
		isLocked = false;
	}

	private String getStringForId() {
		switch (getId()) {
		case 6142:
			return "The purple, western";
		case 6143:
			return "The blue, eastern";
		case 6144:
			return "The yellow, south-eastern";
		case 6145:
			return "The red, south-western";
		}
		return "THIS SHOULDN'T EVER HAPPEN.";
	}

	private int getIndexForId() {
		switch (getId()) {
		case 6146:
		case 6142:
			return 0;
		case 6147:
		case 6143:
			return 1;
		case 6148:
		case 6144:
			return 2;
		case 6149:
		case 6145:
			return 3;
		case 3782:
		case 3784:
		case 3785:
			return 4;
		}
		return -1;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		ticks++;
		if (ticks % 15 == 0)
			if (control.createPestNPC(getIndexForId()))
				if (Utils.random(5) == 0) // double spawn xD
					control.createPestNPC(getIndexForId());
		if (isDead() || isLocked)
			return;
		cancelFaceEntityNoCheck();
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		setNextAnimation(null);
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				setNextAnimation(new Animation(defs.getDeathEmote()));
			else if (loop >= defs.getDeathDelay()) {
				if (getIndexForId() != 4) {
					control.unlockPortal();
					control.getKnight().heal(500);
				}
				finish();
				return false;
			}
			return true;
		});
	}
}