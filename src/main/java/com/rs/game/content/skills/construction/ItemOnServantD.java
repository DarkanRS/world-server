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
package com.rs.game.content.skills.construction;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.dialogues_matrix.MatrixDialogue;

public class ItemOnServantD extends MatrixDialogue {

	private ServantNPC servant;
	private int item;

	@Override
	public void start() {
		servant = (ServantNPC) parameters[0];
		item = (int) parameters[1];
		boolean procceed = false;
		for (int[] element : HouseConstants.BANKABLE_ITEMS)
			for (int bankable : element)
				if (item == bankable) {
					procceed = true;
					break;
				}
		ItemDefinitions definition = ItemDefinitions.getDefs(item);
		final int[] plank = SawmillOperator.getPlankForLog(item);
		if (plank != null || definition.isNoted())
			procceed = true;
		if (!procceed) {
			end();
			return;
		}
		int paymentStage = player.getHouse().getPaymentStage();
		if (paymentStage == 1) {
			sendNPCDialogue(servant.getId(), NORMAL, "Excuse me, but before I can continue working you must pay my fee.");
			stage = 3;
		}
		String name = definition.getName().toLowerCase();

		if (definition.isNoted()) {
			sendOptionsDialogue("Un-cert this item?", "Un-cert " + name + ".", "Fetch another " + name + ".", "Bank", "Cancel");
			stage = 0;
		} else if ((boolean) parameters[2] && plank != null) {
			sendOptionsDialogue("Take this to the sawmill?", "Take it to the sawmill.", "Bank", "Cancel");
			stage = 2;
		} else {
			sendOptionsDialogue("Take this item to the bank?", "Fetch another " + name + ".", "Bank", "Cancel");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 0) {
			if (componentId == OPTION_1)
				setFetchAttributes(2, "How many would you like to un-note?");
			else if (componentId == OPTION_2)
				setFetchAttributes(0, "How many would you like to retrieve?");
			else if (componentId == OPTION_3)
				setFetchAttributes(3, "How many would you like to bank?");
			else
				end();
		} else if (stage == 1) {
			if (componentId == OPTION_1)
				setFetchAttributes(0, "How many would you like to retrieve?");
			else if (componentId == OPTION_2)
				setFetchAttributes(3, "How many would you like to bank?");
			else
				end();
		} else if (stage == 2) {
			if (componentId == OPTION_1)
				setFetchAttributes(1, "How many would you like to create?");
			else if (componentId == OPTION_2)
				setFetchAttributes(3, "How many would you like to bank?");
			else
				end();
		} else if (stage == 3)
			end();
	}

	private void setFetchAttributes(int type, String title) {
		player.sendInputInteger(title, amount -> {
			if (!player.getHouse().isLoaded() || !player.getHouse().getPlayers().contains(player))
				return;
			player.getHouse().getServantInstance().requestType(item, amount, (byte) type);
		});
		end();
	}

	@Override
	public void finish() {

	}
}
