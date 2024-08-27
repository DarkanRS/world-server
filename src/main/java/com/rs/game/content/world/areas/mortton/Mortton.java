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
package com.rs.game.content.world.areas.mortton;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.PotionsKt;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Mortton {

	public static ItemOnNPCHandler handleRazmireCure = new ItemOnNPCHandler(new Object[] { 7899 }, e -> {
		if (e.getPlayer().getVars().getVarBit(e.getNPC().getDefinitions().varpBit) == 1) {
			e.getPlayer().sendMessage("He's already cured!");
			return;
		}
		switch(e.getItem().getId()) {
			// Serum 207
			case 3408:
			case 3410:
			case 3412:
			case 3414:
				e.getItem().setId(e.getItem().getId() == 3414 ? PotionsKt.VIAL : e.getItem().getId()+2);
				e.getPlayer().getVars().setVarBit(e.getNPC().getDefinitions().varpBit, 1);
				e.getPlayer().getInventory().refresh();
				break;
			case 23549:
			case 23550:
			case 23551:
			case 23552:
			case 23553:
				e.getItem().setId(e.getItem().getId() + 1);
				e.getPlayer().getVars().setVarBit(e.getNPC().getDefinitions().varpBit, 1);
				e.getPlayer().getInventory().refresh();
				break;
			case 23554:
			e.getPlayer().getInventory().deleteItem(e.getItem());
			e.getPlayer().getVars().setVarBit(e.getNPC().getDefinitions().varpBit, 1);
			e.getPlayer().sendMessage("The flask shatters as you use the last dose.");
			break;

			// Serum 208
			case 3416:
			case 3417:
			case 3418:
			case 3419:
				e.getItem().setId(e.getItem().getId() == 3419 ? PotionsKt.VIAL : e.getItem().getId()+1);
				e.getPlayer().getVars().saveVarBit(e.getNPC().getDefinitions().varpBit, 1);
				e.getPlayer().getInventory().refresh();
				break;
			case 23603:
			case 23604:
			case 23605:
			case 23606:
			case 23607:
				e.getItem().setId(e.getItem().getId() + 1);
				e.getPlayer().getVars().saveVarBit(e.getNPC().getDefinitions().varpBit, 1);
				e.getPlayer().getInventory().refresh();
				break;
			case 23608:
				e.getPlayer().getInventory().deleteItem(e.getItem());
				e.getPlayer().getVars().saveVarBit(e.getNPC().getDefinitions().varpBit, 1);
				e.getPlayer().sendMessage("The flask shatters as you use the last dose.");
				break;
		}
	});

	public static NPCClickHandler handleRazmire = new NPCClickHandler(new Object[] { 7899 }, e -> {
		switch(e.getOption()) {
			case "Talk-to":
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						if (e.getPlayer().getVars().getVarBit(e.getNPC().getDefinitions().varpBit) == 1)
							addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, there!");
						else
							addNPC(e.getNPCId(), HeadE.DIZZY, "Auuhghhhh...");
						create();
					}
				});
				break;
			case "Trade-General-Store":
				ShopsHandler.openShop(e.getPlayer(), "razmire_general");
				break;
			case "Trade-Builders-Store":
				ShopsHandler.openShop(e.getPlayer(), "razmire_builders");
				break;
		}
	});

}
