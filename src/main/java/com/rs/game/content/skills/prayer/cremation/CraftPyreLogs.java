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
package com.rs.game.content.skills.prayer.cremation;

import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.content.Potions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

public class CraftPyreLogs extends PlayerAction {

	private static final int DOSE_4 = 3430, DOSE_3 = 3432, DOSE_2 = 3434, DOSE_1 = 3436;

	private PyreLog log;
	private int makeX;

	public CraftPyreLogs(PyreLog log) {
		this.log = log;
	}

	@Override
	public boolean start(Player player) {
		makeX = MakeXStatement.getQuantity(player);
		return true;
	}

	@Override
	public boolean process(Player player) {
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!player.getInventory().containsItem(log.baseLog, 1))
			return -1;
		int doses = getDosesInInv(player);
		if ((doses < log.oilDoses) || (makeX-- <= 0))
			return -1;
		player.getInventory().deleteItem(log.baseLog, 1);
		deleteDoses(player, log.oilDoses);
		player.getInventory().addItem(log.itemId);
		player.getSkills().addXp(Constants.FIREMAKING, log.getCreationXP());
		return 0;
	}

	@Override
	public void stop(Player player) {

	}

	private static void deleteDoses(Player player, int doses) {
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			if (doses <= 0) {
				player.getInventory().refresh();
				return;
			}
			switch(item.getId()) {
			case DOSE_4:
				if (doses >= 4) {
					item.setId(Potions.VIAL);
					doses -= 4;
				} else if (doses >= 3) {
					item.setId(DOSE_1);
					doses -= 3;
				} else if (doses >= 2) {
					item.setId(DOSE_2);
					doses -= 2;
				} else if (doses >= 1) {
					item.setId(DOSE_3);
					doses -= 1;
				}
				break;
			case DOSE_3:
				if (doses >= 3) {
					item.setId(Potions.VIAL);
					doses -= 3;
				} else if (doses >= 2) {
					item.setId(DOSE_1);
					doses -= 2;
				} else if (doses >= 1) {
					item.setId(DOSE_2);
					doses -= 1;
				}
				break;
			case DOSE_2:
				if (doses >= 2) {
					item.setId(Potions.VIAL);
					doses -= 2;
				} else if (doses >= 1) {
					item.setId(DOSE_1);
					doses -= 1;
				}
				break;
			case DOSE_1:
				if (doses >= 1) {
					item.setId(Potions.VIAL);
					doses -= 1;
				}
				break;
			}
		}
		player.getInventory().refresh();
	}

	private static int getDosesInInv(Player player) {
		int doses = 0;
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			switch(item.getId()) {
			case DOSE_4:
				doses += 4;
				break;
			case DOSE_3:
				doses += 3;
				break;
			case DOSE_2:
				doses += 2;
				break;
			case DOSE_1:
				doses += 1;
				break;
			}
		}
		return doses;
	}

}
