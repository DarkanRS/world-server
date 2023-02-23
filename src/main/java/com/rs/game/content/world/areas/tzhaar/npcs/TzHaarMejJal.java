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
package com.rs.game.content.world.areas.tzhaar.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler

public class TzHaarMejJal extends Conversation {
	private static final int npcId = 2617;

	public static NPCClickHandler TzHaarMejJal = new NPCClickHandler(new Object[]{npcId}, e -> {
		switch(e.getOption()) {
		case "Bank":
			e.getPlayer().getBank().open();
			break;
		case "Collect":
			GE.openCollection(e.getPlayer());
			break;
		case "Talk-to":
			e.getPlayer().startConversation(new TzHaarMejJal(e.getPlayer(), e.getNPC()));
			break;
		}
	});

	public TzHaarMejJal(Player player, NPC npc) {
		super(player);
		
		addNPC(npc.getId(), HeadE.T_CONFUSED, "You want help TzHaar-Mej-" + player.getDisplayName() + "?");
		addOptions(this, "baseOptions", ops -> {
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
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "You ask a lot questions. Might give you TokKul if you last long enough.")
								.addPlayer(HeadE.CONFUSED, "You're still handing out ToKKul as a reward? TzHaar-Mej- Jeh said it was going to be melted down in the sacred lava, to release your dead from their torment.")
								.addNPC(npc.getId(), HeadE.T_ANGRY, "You ask a lot questions. Might give you TokKul if you last long enough.")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "TzHaar do not need currency. Each TzHaar work hard, and does their duty according to their caste. TzHaar ensure they have food and shelter,")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "and all needs are met. JalYt come to TzHaar City, with heads full of gold and wealth and greed.")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "Gold no use to TzHaar. Soft and easily broken, like JalYt. JalYt need rare token to trade. TokKul is memories of dead TzHaar. Trapped. Precious.")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "TokKul is only rare token TzHaar have. Until TzHaar find new token, or JalYt have less greed, must trade in TokKul.");
							
							ops3.add("Sounds good.")
								.addGotoStage("baseOptions", this);
						});
					
					ops2.add("Thanks.");
				});
			
			ops.add("What did you call me?")
				.addPlayer(HeadE.CONFUSED, "What did you call me?")
				.addNPC(npc.getId(), HeadE.T_CONFUSED, "Are you not TzHaar-Mej?")
				.addOptions(ops2 -> {
					ops2.add("Why do you call me 'TzHaar-Mej'?")
						.addPlayer(HeadE.CONFUSED, "Why do you call me 'TzHaar-Mej'?")
						.addNPC(npc.getId(), HeadE.T_CALM_TALK, "That what you are...you user of mystic powers, no? And you are JalYt no longer. You are TzHaar now.")
						.addPlayer(HeadE.CONFUSED, "Well, yes, I suppose I am...")
						.addNPC(npc.getId(), HeadE.T_LAUGH, "Then you TzHaar-Mej!")
						.addOptions(ops3 -> {
							ops3.add("What are you then?")
								.addPlayer(HeadE.CONFUSED, "What are you then?")
								.addNPC(npc.getId(), HeadE.T_CALM_TALK, "I am TzHaar-Mej, one of the mystics of this city. The TzHaar-Mej guide the TzHaar when change is necessary, and tend to TzHaar eggs to ensure they are hot and healthy.")
								.addNPC(npc.getId(), HeadE.CALM_TALK, "TzHaar-Mej are keepers of knowledge and magic. There are also the mighty TzHaar-Ket who guard us, the swift TzHaar-Xil who hunt for our food, and the skilled TzHaar-Hur who craft our homes and tools.");
						
							ops3.add("Thanks for explaining it.");
						});
					
					ops2.add("Yes, I certainly am.")
						.addPlayer(HeadE.CHEERFUL, "Yes, I certainly am.")
						.addNPC(npc.getId(), HeadE.T_CALM_TALK, "Then it is an honour to speak with you, TzHaar-Mej-" + player.getDisplayName() + ". You great and powerful TzHaar, and defender of our city.");
					
					ops2.add("You must have me confused with another TzHaar.")
						.addPlayer(HeadE.CONFUSED, "You must have me confused with another TzHaar.")
						.addNPC(npc.getId(), HeadE.T_LAUGH, "I heard you are modest, TzHaar-Mej-" + player.getDisplayName() + ". No need. Revel in our praise. You deserve all honour.");
				});
			ops.add("No I'm fine thanks.");
		});
	}
}
