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
package com.rs.game.player.dialogues;

import com.rs.game.player.content.ItemConstants.ItemDegrade;
import com.rs.lib.game.Item;

public class RepairStandD extends Dialogue {

	ItemDegrade details;
	Item item;
	boolean stand;
	int slot;

	@Override
	public void start() {
		details = (ItemDegrade) parameters[0];
		item = (Item) parameters[1];
		stand = (Boolean) parameters[2];
		slot = (Integer) parameters[3];
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().setIFText(1183, 12, "It will cost " + (stand ? details.getRepairStandCost(player) : details.getCost(item)) + " to repair your " + item.getDefinitions().getName() + ".");
		player.getPackets().setIFItem(1183, 13, details.getItemId(), 1);
		player.getPackets().setIFText(1183, 7, "Repair this item fully for " + (stand ? details.getRepairStandCost(player) : details.getCost(item)) + " coins?");
		player.getPackets().setIFText(1183, 22, "Confirm repair");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 9) {
			if (player.getInventory().containsItem(995, stand ? details.getRepairStandCost(player) : details.getCost(item))) {
				if (player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getId() != item.getId()) {
					return;
				}
				player.getInventory().getItems().set(slot, new Item(details.getItemId(), 1));
				player.getInventory().deleteItem(995, stand ? details.getRepairStandCost(player) : details.getCost(item));
				player.getInventory().refresh();
			} else {
				player.sendMessage("You don't have enough coins.");
			}
		}
		end();
	}

	@Override
	public void finish() {

	}

}
