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
package com.rs.game.content.world.npcs.tzHaarCity;

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler

public class TzHaarMejJal extends Conversation {
	private static int npcId = 2617;

	public static NPCClickHandler TzHaarMejJal = new NPCClickHandler(new Object[]{npcId}) {
		@Override
		//Handle Right-Click
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
				case "Bank":
					e.getPlayer().getBank().open();
					break;
				case "Collect":
					GE.openCollection(e.getPlayer());
					break;
				case "Talk-to":
					e.getPlayer().startConversation(new TzHaarMejJal(e.getPlayer()));
					break;
			}
		}
	};

	public TzHaarMejJal(Player player) {
		super(player);
		
		addNPC(npcId, HeadE.T_CONFUSED, "You want help JalYt-Ket-" + player.getDisplayName() + "?");
		addOptions(ops -> {
			ops.add("What is this place?")
				.addPlayer(HeadE.CONFUSED, "What is this place?")
				.addNPC(npcId, HeadE.T_CALM_TALK, "This is the Fight Cave, ThzHaar-Xil made it for practice but many JalYt come here to fight, too. Just enter the cave and make sure you're prepared.")
				.addOptions(ops2 -> {
					ops2.add("Are there any rules?")
						.addPlayer(HeadE.CONFUSED, "Are there any rules?")
						.addNPC(npcId, HeadE.T_LAUGH, "Rules? Survival is the only rule in there.")
						.addOptions(ops3 -> {
							ops3.add("Do I win anything?")
								.addPlayer(HeadE.CONFUSED, "Do I win anything?")
								.addNPC(npcId, HeadE.T_CALM_TALK, "You ask a lot questions.<br>Might give you TokKul if you last long enough.")
								.addPlayer(HeadE.CONFUSED, "...")
								.addNPC(npcId, HeadE.T_ANGRY, "You ask a lot questions.<br>Might give you TokKul if you last long enough.")
								.addNPC(npcId, HeadE.CALM_TALK, "Before you ask, TokKul is like your coins.")
								.addNPC(npcId, HeadE.T_LAUGH, "Gold is like you JalYt, soft and easily broken, we use hard rock forged in fire like TzHaar!");
							
							ops3.add("Sounds good.");
						});
					
					ops2.add("Ok thanks.");
				});
			
			ops.add("What did you call me?")
				.addPlayer(HeadE.CONFUSED, "What did you call me?")
				.addNPC(npcId, HeadE.T_CONFUSED, "Are you not a JalYt-Ket?")
				.addPlayer(HeadE.CONFUSED, "What's a 'JalYt-Ket'?")
				.addNPC(npcId, HeadE.T_CONFUSED, "That what you are...you tough and strong, no?")
				.addPlayer(HeadE.CALM_TALK, "Well, yes I suppose I am...")
				.addNPC(npcId, HeadE.T_LAUGH, "Then you JalYt-Ket!")
				.addPlayer(HeadE.CALM_TALK, "Thanks for explaining it.");
			ops.add("No I'm fine thanks.");
		});
	}
}
