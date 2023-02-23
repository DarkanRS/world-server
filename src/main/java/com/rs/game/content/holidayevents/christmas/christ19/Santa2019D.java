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
package com.rs.game.content.holidayevents.christmas.christ19;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Santa2019D extends Conversation {

	private static final int SNOW_QUEEN = 8539, SANTA = 8540;

	public static NPCClickHandler handleSantaTalk = new NPCClickHandler(new Object[] { SNOW_QUEEN, SANTA }, e -> e.getPlayer().startConversation(new Santa2019D(e.getPlayer())));

	public Santa2019D(Player player) {
		super(player);

		switch(player.getI(Christmas2019.STAGE_KEY)) {
		case 0:
			addPlayer(HeadE.HAPPY_TALKING, "Merry Christmas, guys!");
			addNPC(SANTA, HeadE.SAD_MILD_LOOK_DOWN, "Ho ho ho hohhh noooo...");
			addPlayer(HeadE.CONFUSED, "What's the matter? Should I have said 'you two' or 'people' instead?");
			addNPC(SANTA, HeadE.SAD_MILD, "No no.. It's our son, Jack. He's convinced a few of Lumi's imps to steal some key foods for our Christmas feast.");
			addNPC(SANTA, HeadE.SAD_MILD, "I knew banishing him from the Land of Snow was a bit harsh..");
			addPlayer(HeadE.SKEPTICAL, "Oh no! Is there any way I can help out?");
			addNPC(SANTA, HeadE.SAD_MILD, "I'm not sure. Snowie, what do you think? The only one capable of locating the imps would be Rasmus.");
			addNPC(SNOW_QUEEN, HeadE.LAUGH, "Oh dear, you're always so worked up about making every Christmas perfect. My imps are easily locatable. It'll be fine.");
			addNPC(SNOW_QUEEN, HeadE.CHEERFUL, player.getDisplayName() + ", I can introduce you to one of my higher up imps, Rasmus. He'll help you find the food pretty quickly.");
			addNPC(SNOW_QUEEN, HeadE.CHEERFUL, "Here's one of my amulets, while you're wearing it, Rasmus will listen to you and help. Snow imps are also very hard to spot without it on. They have ways of avoiding being seen.");
			addPlayer(HeadE.CHEERFUL, "Alright, and how would I go about summoning Rasmus?");
			addNPC(SNOW_QUEEN, HeadE.CHEERFUL, "Just wear the necklace and operate it to summon him to you. Remember, he must be walking with you to be able to show you where the imps are.");
			addPlayer(HeadE.CHEERFUL, "Alright, sounds good. Thanks!", () -> {
				player.getInventory().addItemDrop(14599, 1);
				player.save(Christmas2019.STAGE_KEY, 1);
			});
			break;
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
			addPlayer(HeadE.CALM_TALK, "What am I supposed to be doing again?");
			addNPC(SNOW_QUEEN, HeadE.CHEERFUL, "Please help find the food that Jack's imps stole by collaborating with Rasmus. You can speak to him by operating that necklace I gave you.");
			if (!player.containsItem(14599)) {
				addPlayer(HeadE.WORRIED, "I seem to have lost the amulet you gave me..");
				addNPC(SNOW_QUEEN, HeadE.CHEERFUL, "Oh no worries. I can spin you up a new one right now. Here you go!");
				addPlayer(HeadE.CHEERFUL, "Thanks! Sorry for the inconvenience.", () -> {
					player.getInventory().addItemDrop(14599, 1);
				});
			} else
				addPlayer(HeadE.CHEERFUL, "Alright, I'll get right on that.");
			break;
		case 9:
			addPlayer(HeadE.HAPPY_TALKING, "We did it! We found all the missing items from the feast!");
			addNPC(SNOW_QUEEN, HeadE.CHEERFUL, "Great job! We are so grateful for your help.");
			addPlayer(HeadE.HAPPY_TALKING, "Is there any chance I could get a Christmas gift from you guys as a reward?");
			addNPC(SANTA, HeadE.LAUGH, "Ho ho ho! Of course! Here, pop this with your friends at the feast and have a very Merry Christmas!");
			addPlayer(HeadE.CHEERFUL, "Thank you!");
			addItem(962, "Santa has given you a christmas cracker, allowed you to keep the ice amulet, and has taught you the Snowman Dance emote!");
			addNext(() -> {
				player.save(Christmas2019.STAGE_KEY, 10);
				player.getVars().setVarBit(6934, 1);
				player.getInventory().addItemDrop(new Item(962, 1));
				player.getInventory().addItemDrop(new Item(11949, 1));
				player.addDiangoReclaimItem(11949);
				player.addDiangoReclaimItem(14599);
				player.getEmotesManager().unlockEmote(Emote.SNOWMAN_DANCE);
			});
			break;
		case 10:
			addPlayer(HeadE.HAPPY_TALKING, "Merry Christmas, guys!");
			addNPC(SANTA, HeadE.LAUGH, "Merry Christmas! Thank you for helping fix the feast!");
			addNPC(SNOW_QUEEN, HeadE.CHEERFUL, "Yes, Merry Christmas! We appreciate the help!");
			addPlayer(HeadE.HAPPY_TALKING, "It was nothing. All in the spirit of the season!");
			break;
		}

		create();
	}

}
