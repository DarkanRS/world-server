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
package com.rs.game.content.world.unorganized_dialogue;

import java.util.ArrayList;
import java.util.List;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.engine.dialogue.statements.MakeXStatement.MakeXType;
import com.rs.game.model.entity.player.Player;

public class TanningD extends Conversation {
	
	private enum Leather {
		SOFT(1739, 1741, 0, 2),
		HARD(1739, 1743, 3, 5),
		SNAKE(6287, 6289, 15, 25),
		GREEN(1753, 1745, 20, 45),
		BLUE(1751, 2505, 20, 45),
		RED(1749, 2507, 20, 45),
		BLACK(1747, 2509, 20, 45),
		ROYAL(24372, 24374, 20, 45);
		
		private int kharidPrice, canifisPrice;
		private int raw, tanned;
		
		private Leather(int raw, int tanned, int kharidPrice, int canifisPrice) {
			this.raw = raw;
			this.tanned = tanned;
			this.kharidPrice = kharidPrice;
			this.canifisPrice = canifisPrice;
		}
	}
	
	public TanningD(Player player, boolean canifis, int NPC) {
		super(player);
		
		List<Leather> craftable = new ArrayList<>();
		for (Leather leather : Leather.values())
			if (player.getInventory().containsItem(leather.raw))
				craftable.add(leather);
		
		if (craftable.isEmpty()) {
			addNPC(NPC, HeadE.CHEERFUL, "You don't have any leather that I can tan. Bring me some raw leather and then I can be more help to you.");
			create();
			return;
		}
		
		Dialogue makeX = addNext(new MakeXStatement(MakeXType.MAKE, "How many hides would you like to tan?<br>Choose a number, then click the hide to begin.", craftable.stream().mapToInt(leather -> leather.tanned).toArray(), 28));
		
		for (Leather leather : craftable) {
			makeX.addNext(() -> {
				int amount = MakeXStatement.getQuantity(player);
				int pricePer = canifis ? leather.canifisPrice : leather.kharidPrice;
				if (amount > player.getInventory().getAmountOf(leather.raw))
					amount = player.getInventory().getAmountOf(leather.raw);
				for (int i = 0;i < amount;i++) {
					if (!player.getInventory().hasCoins(pricePer)) {
						player.sendMessage("You have run out of gold.");
						break;
					}
					if (!player.getInventory().containsItem(leather.raw)) {
						player.sendMessage("You have run out of raw leather.");
						break;
					}
					player.getInventory().removeCoins(pricePer);
					player.getInventory().deleteItem(leather.raw, 1);
					player.getInventory().addItem(leather.tanned);
				}
			});
		}
		
		create();
	}
}
