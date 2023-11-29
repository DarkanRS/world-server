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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;

public enum DungTree {
	TANGLE_GUM_VINE(1, 35, 17682, 150, 255),
	SEEPING_ELM_TREE(10, 60, 17684, 150, 240),
	BLOOD_SPINDLE_TREE(20, 85, 17686, 150, 230),
	UTUKU_TREE(30, 115, 17688, 150, 220),
	SPINEBEAM_TREE(40, 145, 17690, 150, 210),
	BOVISTRANGLER_TREE(50, 175, 17692, 150, 200),
	THIGAT_TREE(60, 210, 17694, 150, 190),
	CORPESTHORN_TREE(70, 245, 17696, 150, 180),
	ENTGALLOW_TREE(80, 285, 17698, 150, 170),
	GRAVE_CREEPER_TREE(90, 330, 17700, 150, 170);

	private final int level, logsId, rate1, rate99;
	private final double xp;

	DungTree(int level, double xp, int logsId, int rate1, int rate99) {
		this.level = level;
		this.xp = xp;
		this.logsId = logsId;
		this.rate1 = rate1;
		this.rate99 = rate99;
	}

	public int getLevel() {
		return level;
	}

	public double getXp() {
		return xp;
	}

	public int getLogsId() {
		return logsId;
	}

	public boolean rollSuccess(int level, DungHatchet hatchet) {
		return Utils.skillSuccess(level, hatchet.getToolMod(), rate1, rate99);
	}

	public void giveLog(Player player) {
		player.getInventory().addItem(logsId, 1);
		player.getSkills().addXp(Constants.WOODCUTTING, xp);
	}
}
