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
package com.rs.game.content.minigames.domtower;

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class DreadNip extends NPC {

	public static final String[] DREADNIP_MESSAGES = { "Your dreadnip couldn't attack so it left.", "The dreadnip gave up as you were too far away.", "Your dreadnip served its purpose and fled." };

	private Player owner;
	private int ticks;

	public DreadNip(Player owner, int id, Tile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile);
		this.owner = owner;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (owner == null || owner.hasFinished()) {
			finish(-1);
			return;
		}
		if (getCombat().getTarget() == null || getCombat().getTarget().isDead()) {
			finish(0);
			return;
		}
		if (Utils.getDistance(owner.getTile(), getTile()) >= 10) {
			finish(1);
			return;
		} else if (ticks++ == 33) {
			finish(2);
			return;
		}
	}

	private void finish(int index) {
		if (index != -1) {
			owner.sendMessage(DREADNIP_MESSAGES[index]);
			owner.getTempAttribs().removeB("hasDN");
		}
		this.finish();
	}

	public Player getOwner() {
		return owner;
	}

	public int getTicks() {
		return ticks;
	}
}
