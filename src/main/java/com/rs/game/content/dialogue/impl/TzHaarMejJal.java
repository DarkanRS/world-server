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
package com.rs.game.content.dialogue.impl;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

public class TzHaarMejJal extends Conversation {

	public TzHaarMejJal(Player player, NPC npc) {
		super(player);
		
		addNPC(npc.getId(), HeadE.T_CONFUSED, "You want help JalYt-Ket-" + player.getDisplayName() + "?");
		addOptions(ops -> {
			ops.add("What is this place?")
				.addPlayer(HeadE.CONFUSED, "What is this place?")
				.addNPC(npc.getId(), HeadE.T_CALM_TALK, "This is the Fight Cave, ThzHaar-Xil made it for practice but many JalYt come here to fight, too. Just enter the cave and make sure you're prepared.")
				.addOptions(ops2 -> {
					ops2.add("Are there any rules?")
						.addPlayer(HeadE.CONFUSED, "Are there any rules?")
						.addNPC(npc.getId(), HeadE.T_LAUGH, "Rules? Survival is the only rule in there.")
						.addOptions(ops3 -> {
							ops3.add("Do I win anything?")
								.addPlayer(HeadE.CONFUSED, "Do I win anything?")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "You ask a lot questions.<br>Might give you TokKul if you last long enough.")
								.addPlayer(HeadE.CONFUSED, "...")
								.addNPC(npc.getId(), HeadE.T_ANGRY, "You ask a lot questions.<br>Might give you TokKul if you last long enough.")
								.addNPC(npc.getId(), HeadE.CALM_TALK, "Before you ask, TokKul is like your coins.")
								.addNPC(npc.getId(), HeadE.T_LAUGH, "Gold is like you JalYt, soft and easily broken, we use hard rock forged in fire like TzHaar!");
							
							ops3.add("Sounds good.");
						});
					
					ops2.add("Ok thanks.");
				});
			
			ops.add("What did you call me?")
				.addPlayer(HeadE.CONFUSED, "What did you call me?")
				.addNPC(npc.getId(), HeadE.T_CONFUSED, "Are you not a JalYt-Ket?")
				.addPlayer(HeadE.CONFUSED, "What's a 'JalYt-Ket'?")
				.addNPC(npc.getId(), HeadE.T_CONFUSED, "That what you are...you tough and strong, no?")
				.addPlayer(HeadE.CALM_TALK, "Well, yes I suppose I am...")
				.addNPC(npc.getId(), HeadE.T_LAUGH, "Then you JalYt-Ket!")
				.addPlayer(HeadE.CALM_TALK, "Thanks for explaining it.");
			ops.add("No I'm fine thanks.");
		});
	}
}
