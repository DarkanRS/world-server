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
package com.rs.game.content.holidayevents.easter.easter21;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class EasterBunnyD extends Conversation {

	private static final int EASTER_BUNNY = 9687;

	public static NPCClickHandler handleEasterBunnyTalk = new NPCClickHandler(new Object[] { EASTER_BUNNY }, e -> e.getPlayer().startConversation(new EasterBunnyD(e.getPlayer())));

	public EasterBunnyD(Player player) {
		super(player);

		switch(player.getI(Easter2021.STAGE_KEY)) {
		case -1:
			addPlayer(HeadE.CHEERFUL, "Hello!");
			addNPC(EASTER_BUNNY, HeadE.CAT_SAD, "...");
			addPlayer(HeadE.CONFUSED, "Hello?");
			addNPC(EASTER_BUNNY, HeadE.CAT_SAD, "Oh! Hello there. Sorry, I didn't really notice you.");
			addPlayer(HeadE.CHEERFUL, "It's okay. I just hoped you'd have some chocolate for me.");
			addNPC(EASTER_BUNNY, HeadE.CAT_SAD, "So did I, but it's all gone wrong. I only wanted a bit of a rest. Now no one will get their chocolate goodness.");
			addPlayer(HeadE.MORTIFIED, "WHAT?");
			addNPC(EASTER_BUNNY, HeadE.CAT_SAD, "I know, depressing, isn't it?");
			addPlayer(HeadE.CONFUSED, "Why? What happened this year?");
			addNPC(EASTER_BUNNY, HeadE.CAT_SAD, "I'm getting too old for this chocolate delivery job, so I went away on a little holiday, hopping that would refresh me. I left my son in charge of the Egg Plant...and now it's all in pieces because he's so lazy.");
			addPlayer(HeadE.CONFUSED, "Can I help?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Oh, would you? I hope I'm not being too much truffle. Er...trouble.");
			addPlayer(HeadE.CHEERFUL, "Of course, what shall I do?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You could fix up the Egg Plant so it's working; that would be a start.");
			addPlayer(HeadE.CHEERFUL, "Okay!");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Now you've agreed to help, you'll need to get through the warrens to the Egg Plant, to speak to that lazy son of mine. For that you need to be bunny-sized!");
			addPlayer(HeadE.CONFUSED, "How do I do that?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You simply go down the rabbit hole. My magic will sort the transformation, though you may feel a little itchy for a couple of weeks afterwards. I'll meet you down there!");
			addPlayer(HeadE.CHEERFUL, "Off I go then!", () -> {
				player.save(Easter2021.STAGE_KEY, 1);
			});
			break;
		case 1:
			if (player.getY() > 5000) {
				addPlayer(HeadE.CHEERFUL, "Wow it sure is dark down here.");
				addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Yeah, 'tis the life of a bunny! Glad you made it down safely.");
				addPlayer(HeadE.CHEERFUL, "What should I be fixing up first?");
				addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Well the first problem to deal with is the Easter Bird.. He hasn't been laying eggs due to being so hungry and thirsty. There's some food and water to the east of him. You'll have to figure out which food he likes.");
				addPlayer(HeadE.CHEERFUL, "Alright, I'll get right on that!", () -> {
					player.save(Easter2021.STAGE_KEY, 2);
				});
			} else
				addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Speak to me down in my hole. I'll meet you down there!");
			break;
		case 2:
			addPlayer(HeadE.CHEERFUL, "What am I supposed to be doing again?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You should wake up the bird with some food and water. I forget what his favorite food is, though. So you're going to have to figure it out.");
			break;
		case 3:
			addPlayer(HeadE.CHEERFUL, "I woke up the bird!");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Great work! He should get back to laying the easter eggs again pretty quickly now.");
			addPlayer(HeadE.CHEERFUL, "Awesome. What should I work on next?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "The incubator seems to be broken. I am not sure where the peices even went. My son probably had something to do with it. You should head over to his room to the west ask him about where they are.");
			addPlayer(HeadE.CHEERFUL, "Alright, thanks. I'll get going.", () -> {
				player.save(Easter2021.STAGE_KEY, 4);
			});
			break;
		case 4:
		case 5:
			addPlayer(HeadE.CHEERFUL, "What am I supposed to be doing again?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "You should be trying to fix the incubator. Like I said before, I am clueless as to what happened to it. My son in his room to the west would be your best bet.");
			break;
		case 6:
			addPlayer(HeadE.CHEERFUL, "Alright, the incubator is fixed. Your son is absolutely awful..");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Amazing work! Yes, I know he is quite awful. His mother left early on and he seems to have no respect for me and anyone older than him at all despite how much work I do to raise him!");
			addPlayer(HeadE.CHEERFUL, "I'm sorry to hear about that.");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "It's quite alright, I hope I can get through to him someday.");
			addPlayer(HeadE.CHEERFUL, "I sure hope so. Is everything done yet?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Not quite, I need some workers to operate the machines if my son is not going to contribute anything to the season.");
			addPlayer(HeadE.CHEERFUL, "Where do you think I can find some workers?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Squirrels! One of my great old friends Charlie lives a little north of Falador and he certainly has some motivated and cheerful workers who'd love to help!");
			addPlayer(HeadE.SKEPTICAL_THINKING, "Squirrels? If you say so. I'll head up to Falador and see if I can find Charlie then.", () -> {
				player.save(Easter2021.STAGE_KEY, 7);
			});
			break;
		case 7:
			addPlayer(HeadE.CHEERFUL, "What am I supposed to be doing again?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "I need some workers to operate the machinery! My friend Charlie lives up north of Falador, you should speak to him.");
			break;
		case 8:
			addPlayer(HeadE.HAPPY_TALKING, "It looks like Charlie's workers made it here pretty quickly! The factory looks like it's up and running now. Is there anything else you need help with?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Oh how eggciting! I think that should be fine for this year. Thank you so much for your help.");
			addPlayer(HeadE.HAPPY_TALKING, "No problem, is there any special chocolate you can give me now that everything is fixed?");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Oh of course! I almost forgot. Here's a magical ring for all the trouble you went through!");
			addPlayer(HeadE.CHEERFUL, "Thank you!");
			addItem(7927, "The Easter Bunny hands you a magical easter ring and unlocks the Around the World emote for you!");
			addNext(() -> {
				player.save(Easter2021.STAGE_KEY, 9);
				//				player.getInventory().addItemDrop(new Item(1037, 1));
				//				player.getInventory().addItemDrop(new Item(4565, 1));
				//				player.addDiangoReclaimItem(1037);
				//				player.addDiangoReclaimItem(4565);
				player.getInventory().addItemDrop(new Item(7927, 1));
				player.addDiangoReclaimItem(7927);
				player.getEmotesManager().unlockEmote(Emote.AROUND_THE_WORLD);
			});
			break;
		case 9:
			addPlayer(HeadE.HAPPY_TALKING, "Happy Easter!");
			addNPC(EASTER_BUNNY, HeadE.CAT_CHEERFUL, "Happy Easter to you too! I am very grateful for your help. He is risen!");
			addPlayer(HeadE.HAPPY_TALKING, "It was nothing. He is risen, indeed!");
			break;
		}

		create();
	}

}
