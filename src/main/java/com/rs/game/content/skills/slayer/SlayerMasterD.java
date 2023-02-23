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
package com.rs.game.content.skills.slayer;

import com.rs.game.content.Skillcapes;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SlayerMasterD extends Conversation {

	public static NPCClickHandler handleMasters = new NPCClickHandler(new Object[] { 8480, 8481, 1597, 1598, 7779, 8466, 9085 }, e -> {
		Master master = Master.getMasterForId(e.getNPC().getId());
		switch(e.getOption()) {
		case "Talk-to" -> e.getPlayer().startConversation(new SlayerMasterD(e.getPlayer(), master));
		case "Get-task" -> e.getPlayer().getSlayer().getTaskFrom(e.getPlayer(), master);
		case "Trade" -> e.getPlayer().getSlayer().openShop(e.getPlayer(), master);
		case "Rewards" -> Slayer.openBuyInterface(e.getPlayer());
		}
	});

	public SlayerMasterD(Player player, Master master) {
		super(player);

		addNPC(master.npcId, HeadE.NO_EXPRESSION, "'Ello, and what are you after then?");
		addOptions("What would you like to say?", ops -> {
			ops.add("I need another assignment.", new Dialogue()
					.addPlayer(HeadE.CHEERFUL, "I need another assignment.")
					.addNext(() -> player.getSlayer().getTaskFrom(player, master)));
			
			ops.add("Do you have anything for trade?", new Dialogue()
					.addPlayer(HeadE.CHEERFUL, "Do you have anything for trade?")
					.addNext(() -> player.getSlayer().openShop(player, master)));
			
			ops.add("I'd like to see the rewards shop please.", new Dialogue()
					.addPlayer(HeadE.CHEERFUL, "I'd like to see the rewards shop please.")
					.addNext(() -> Slayer.openBuyInterface(player)));
			
			if (master == Master.Kuradal)
				ops.add("What is that cape you're wearing?", Skillcapes.Slayer.getOffer99CapeDialogue(player, master.npcId));
			if (master == Master.Vannaka)
				ops.add("About the Achievement System...", new AchievementSystemDialogue(player, master.npcId, SetReward.VARROCK_ARMOR).getStart());
		});
		create();
	}
}
