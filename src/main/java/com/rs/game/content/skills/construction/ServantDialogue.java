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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.construction.HouseConstants.Servant;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;

public class ServantDialogue extends Conversation {

	private static final String[] BEGINNING_MESSAGE = {

			"Ah, I visited with her Royal Highness for the Diamond Jubillee," + " and I liked it so much I never left! However, I find myself without" + " an income. Care to hire me, for only 375 coins?", "'Allo mate! Got a job going? Only 375 coins!",

			"Oh! Please hire me sir! I'm very good - well, I'm not bad- and my fee's only 750 coins.",

			"You're not aristocracy, but I suppose you'd do. Do oyu want a good cook for 2250 coins?",

			"Good day, sir! Would sir care to hire a good butler for 3750 coins? ",

			"Greetings! I am Alathazdrar, butler to the Demon Lords, and I offer thee my services for a mere 7500 coins!",

	};

	private static final String[] WHAT_CAN_YOU_DO = {

			"I have some experience cooking, and I'm happy to take items to and from the bank.",

			"I'm a great cook, me! I used to work with a rat-catcher, I used to cook for him." + "There is a dozen different ways you can cook rat!",

			"Well, I can, um. I can cook meals and make tea and everthing, and I can even take things" + " to and from the bank for you. I won't make any mistakes this time and everything will be fine!",

			"I, sir, am the finest cook in all Arrow! I can also make good time going to the bank or sawmill.",

			"I can fulfill sir's domestic service needs with efficiency and impeccable manners. I hate to boast, but" + " I can say with confidence that no mortal can make trips to the bank or sawmill fast than I!",

			"I have learned my trade under the leash of some of the harshest maters of the Demon Dimensions. I can cook" + " to statisfy the most infernal stomachs, and fly on wings of flame to deposit thine items in the bank in seconds.",

	};

	private static final String[] JOB_HISTORY = {

			"I've worked in the Queen's service almost since I was a lad. A high quality" + "of service expected there, let me tell you!", "Well, city warder Bravek once threw a chair at me and yelled at me to get him a hangover cure." + "So I made it and I think it worked, 'cause then he threw another chair at me and that one hit!",

			"Oh! Oh! I,well I er. It wasn't really my fault, I mean, it was, but not really. I mean, how was" + " I to know that that plate was so valuable? It was just lying around and I odn't know art, it just looked like a pretty pattern.",

			"I used to be the cook for the old Duke of Lumbridge. Visiting dignataries praised me for my fine banquets!" + "But then someone found a rule that only one family could hold that post. Overnight, I was fired for someone who" + " couldn't even bake cakes without burning them!",

			"From a humble beginning as a dish-washer I have worked my way up through the ranks of domestic service" + " in the households of nobles from Varrock and Ardougne. As a life-long servant I have naturally suppressed my personality.",

			"For millennia I have served and waited on the mighty Demon Lords of the infernal Dimensions. I began as a humble footman in the household of Lord Thammaron." + " Currently, I come to serve the mortal masters in the realms of light.",

	};
	
	public ServantDialogue(Player player, NPC npc) {
		super(player);
		
		int slot = getSlot(npc.getId());
		Servant servant = Servant.values()[slot];
		
		addNPC(npc.getId(), HeadE.CALM_TALK, BEGINNING_MESSAGE[slot]);
		addOptions(ops -> {
			ops.add("What can you do?")
				.addPlayer(HeadE.CONFUSED, "What can you do?")
				.addNPC(npc.getId(), HeadE.CALM_TALK, WHAT_CAN_YOU_DO[slot]);
			ops.add("Tell me about your previous jobs.")
				.addPlayer(HeadE.CONFUSED, "Tell me about your previous jobs.")
				.addNPC(npc.getId(), HeadE.CALM_TALK, JOB_HISTORY[slot]);
			ops.add("You're hired!", getHireDialogue(servant));
		});
	}

	private int getSlot(int npcId) {
		return (npcId - 4236) / 2;
	}
	
	private Dialogue getHireDialogue(Servant servant) {
		if (player.getHouse().hasServant())
			return new Dialogue().addSimple("You already have a servant!");
		if (player.getSkills().getLevelForXp(Skills.CONSTRUCTION) < servant.getLevel())
			return new Dialogue().addSimple("You need a Construction level of at least " + servant.getLevel() + ".");
		
		return new Dialogue()
				.addPlayer(HeadE.CHEERFUL, "You're hired!")
				.addNPC(servant.getId(), HeadE.CHEERFUL, "Thank you master.", () -> player.getHouse().setServantOrdinal((byte) servant.ordinal()))
				.getHead();
	}
}
