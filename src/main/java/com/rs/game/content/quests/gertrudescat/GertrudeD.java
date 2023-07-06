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
package com.rs.game.content.quests.gertrudescat;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;

public class GertrudeD extends Conversation {

	private static final int GERTRUDE = 780;

	public GertrudeD(Player player) {
		super(player);

		switch(player.getQuestManager().getStage(Quest.GERTRUDES_CAT)) {
		case 0:
			addPlayer(HeadE.HAPPY_TALKING, "Hello, are you ok?");
			addNPC(GERTRUDE, HeadE.ANGRY, "Do I look ok? Those kids drive me crazy.");
			addNPC(GERTRUDE, HeadE.SAD_MILD, "I'm sorry. It's just that I've lost her.");
			addPlayer(HeadE.CONFUSED, "Lost whom?");
			addNPC(GERTRUDE, HeadE.SAD_MILD, "Fluffs, poor Fluffs. She never hurt anyone.");
			addPlayer(HeadE.CONFUSED, "Who's Fluffs?");
			addNPC(GERTRUDE, HeadE.SAD_MILD, "My beloved feline friend, Fluffs. She's been purring by my side for almost a decade. Please, could you go and search for her while I take care of the children?");
			addPlayer(HeadE.CONFUSED, "What's in it for me?");
			addNPC(GERTRUDE, HeadE.SAD_MILD, "I'm sorry, I'm too poor to pay you anything, the best I could offer is a warm meal.");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "So, can you help?");
			addPlayer(HeadE.CONFUSED, "Just a meal? It's not the best offer I've had, but I suppose I can help.");
			addNPC(GERTRUDE, HeadE.SAD_MILD, "I suppose I could give you some nice, yummy chocolate cake; maybe even a kitten too, if you seem like a nice sort.");
			addNPC(GERTRUDE, HeadE.CONFUSED, "Is that something you could be persuaded with?");
			addPlayer(HeadE.CONFUSED, "Well, I suppose I could, though I'd need more details.");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "Really? Thank you so much! I really have no idea where she could be!");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "I think my sons, Shilop and Wilough, saw the cat last. They'll be out in the marketplace.");
			addPlayer(HeadE.CONFUSED, "The marketplace? Which one would that be? It would help to know what they get up to, as well.");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "Really? Well, I generally let them do what they want, so I've no idea exactly what they would be doing. They are good lads, though. I'm sure they are just watching the passers-by in Varrock Marketplace.");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "Oh, to be young and carefree again!");
			addPlayer(HeadE.HAPPY_TALKING, "Alright then, I'll see what I can do. Two young lads in Varrock Marketplace; I can only hope that there's no school trip passing through when I arrive.", () -> {
				player.getQuestManager().setStage(Quest.GERTRUDES_CAT, 1);
			});
			break;
		case 1:
			addPlayer(HeadE.HAPPY_TALKING, "Hello Gertrude.");
			addNPC(GERTRUDE, HeadE.CONFUSED, "Have you seen my poor Fluffs?");
			addPlayer(HeadE.SAD_MILD, "I'm afraid not.");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "What about Shilop?");
			addPlayer(HeadE.CONFUSED, "No sign of him either.");
			addNPC(GERTRUDE, HeadE.CONFUSED, "Hmmm, strange; he should be in Varrock Marketplace.");
			break;
		case 2:
			addPlayer(HeadE.HAPPY_TALKING, "Hello Gertrude.");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "Hello again, did you manage to find Shilop? I can't keep an eye on him for the life of me.");
			addPlayer(HeadE.HAPPY_TALKING, "He does seem like quite a handful.");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "You have no idea! Did he help at all?");
			addPlayer(HeadE.NO_EXPRESSION, "I think so. I'm just going to look now.");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "Thanks again, adventurer.");
			break;
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
			addPlayer(HeadE.HAPPY_TALKING, "Hello again.");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "Hello. How's it going? Any luck?");
			addPlayer(HeadE.HAPPY_TALKING, "Yes, I've found Fluffs!");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "Well well, you are clever! Did you bring her back?");
			addPlayer(HeadE.CONFUSED, "Well, that's the thing, she refuses to leave.");
			addNPC(GERTRUDE, HeadE.SAD_MILD, "Oh dear, oh dear! Maybe she's hungry. She loves doogle sardines but I'm all out.");
			addPlayer(HeadE.CONFUSED, "Doogle sardines?");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "Yes, raw sardines seasoned with doogle leaves. Unfortunately, I've used all my doogle leaves but you may find some on the bush out back.");
			break;
		case 8:
			addPlayer(HeadE.CHEERFUL, "Hello, Gertrude. Fluffs had run off with her kittens, lost them and I have now returned them to her.");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "Thank you! If you hadn't found her kittens then they would have died out there. I've got some presents for you in thanks for your help.");
			addPlayer(HeadE.CHEERFUL, "That's ok, I like to do my bit.");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "I have no real material possessions but I do have kittens. I've cooked you some food too.");
			addPlayer(HeadE.CHEERFUL, "You're going to give me a kitten? Thanks.");
			addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "I would sell one to my cousin in West Ardougne. I hear there's a rat epidemic there but it's too far for me to travel, what with my boys and all.");
			addNPC(GERTRUDE, HeadE.NO_EXPRESSION, "Oh, by the way, the kitten can live in your backpack but, to ensure it grows, you must take it out, feed it and stroke it often.");
			addSimple("You've been given a cat!", () -> {
				player.getQuestManager().completeQuest(Quest.GERTRUDES_CAT);
			});
			break;
		default:
			if (player.containsItem(1555))
				addNPC(GERTRUDE, HeadE.HAPPY_TALKING, "Take good care of that kitten!");
			else
				addNPC(GERTRUDE, HeadE.ANGRY, "You lost your kitten? It found it wandering around outside my door! Take care of it this time..", () -> {
					player.getInventory().addItem(1555, 1);
				});
			break;
		}
		create();
	}

}
