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
package com.rs.game.content.world.areas.port_khazard;

import com.rs.game.content.minigames.trawler.FishingTrawler;
import com.rs.game.content.minigames.trawler.FishingTrawlerGameController;
import com.rs.game.content.minigames.trawler.MontyConversation;
import com.rs.game.content.minigames.trawler.MontyGameConversation;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Controller;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PortKhazard {

	public static NPCClickHandler handleMontyClick = new NPCClickHandler(new Object[] { 463 }, e -> {
		Controller controller = e.getPlayer().getControllerManager().getController();
		if((controller instanceof FishingTrawlerGameController) && FishingTrawler.getInstance().isTrawlerMonty(e.getNPC())) {
			e.getPlayer().startConversation(new MontyGameConversation(e.getPlayer()));
			return;
		}
		e.getPlayer().startConversation(new MontyConversation(e.getPlayer()));
	});

	public static NPCClickHandler handleKhazardShopkeeper = new NPCClickHandler(new Object[] { 555 }, e -> {
		int option = e.getOpNum();
		if (option == 1)
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {{
				addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Can I help you at all?");
				addNext(() -> {
					ShopsHandler.openShop(e.getPlayer(), "khazard_general_store");
				});
				create();
			}});
		if (option == 3)
			ShopsHandler.openShop(e.getPlayer(), "khazard_general_store");
	});
}
