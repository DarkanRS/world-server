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
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;

public class ShilopWiloughD extends Conversation {

	public ShilopWiloughD(Player player, NPC npc) {
		super(player);

		int npcId = npc.getId();

		if (player.getQuestManager().getStage(Quest.GERTRUDES_CAT) == 0) {
			addPlayer(HeadE.NO_EXPRESSION, "Hello youngster.");
			addNPC(npcId, HeadE.CHILD_UNSURE, "I don't talk to strange old people.");
		} else if (player.getQuestManager().getStage(Quest.GERTRUDES_CAT) == 1) {
			addPlayer(HeadE.NO_EXPRESSION, "Hello there, I've been looking for you and it's important.");
			addNPC(npcId, HeadE.CHILD_CALM_TALK, "I didn't mean to take it! I just forgot to pay.");
			addPlayer(HeadE.CONFUSED, "What? I'm trying to help your mum find some cat called Fluffs.");
			addNPC(npcId, HeadE.CHILD_CALM_TALK, "Ohh...well, in that case I might be able to help. Fluffs followed me to my super secret hideout, I haven't seen her since. She's probably off eating small creatures somewhere.");
			addPlayer(HeadE.CONFUSED, "Where is this secret hideout? I really need to find that cat for your mum.");
			addNPC(npcId, HeadE.CHILD_CALM_TALK, "If I told you that, it wouldn't be a secret. What if I need to escape from the law? I need a hideout.");
			addPlayer(HeadE.NO_EXPRESSION, "From my limited knowledge of the law, they are not usually involved in manhunts for children.");
			addNPC(npcId, HeadE.CHILD_FRUSTRATED, "Well it's still mine anyway, I need a place to be alone sometimes. Those two little brothers at the house are just such babies.");

			addOptions(new Options() {
				@Override
				public void create() {
					option("Tell me sonny, or I will inform your mum you are a criminal.", new Dialogue()
							.addPlayer(HeadE.ANGRY, "Tell me sonny, or I will inform your mum you are a criminal.")
							.addNPC(npcId, HeadE.CHILD_AWE, "W..wh..what?! Y..you wouldn't! Anyway, I'll deny it all and she'll be sure to believe me over some wandering killer like you.")
							.addPlayer(HeadE.ANGRY, "I'm an upstanding citizen!")
							.addNPC(npcId, HeadE.CHILD_EVIL_LAUGH, "I'm her darling boy and you'd have to forget about her rewarding you. Hop it snitch.")
							.addSimple("You decide it's best not to aggravate the repulsive boy any more."));

					option("What will make you tell me?", new Dialogue()
							.addPlayer(HeadE.CONFUSED, "What will make you tell me?")
							.addNPC(npcId, HeadE.CHILD_UNSURE, "Well...now you ask, I am a bit short on cash.")
							.addPlayer(HeadE.ROLL_EYES, "How much?")
							.addNPC(npcId, HeadE.CHILD_CALM_TALK, "100 coins should cover it.")
							.addPlayer(HeadE.FRUSTRATED, "100 coins! What sort of expensive things do you need that badly?")
							.addNPC(npcId, HeadE.CHILD_CALM_TALK, "Well I don't like chocolate and have you seen how much sweets cost to buy?")
							.addPlayer(HeadE.ROLL_EYES, "Why should I pay you then, can you answer that as easily?")
							.addNPC(npcId, HeadE.CHILD_CALM_TALK, "Obviously you shouldn't pay that much, but I won't help otherwise. I never liked that cat anyway, fussy scratchy thing it is, so what do you say?")
							.addOptions(new Options() {
								@Override
								public void create() {
									Dialogue pay = new Dialogue();
									if (player.getInventory().hasCoins(100))
										pay.addPlayer(HeadE.NO_EXPRESSION, "Okay then, I'll pay, but I'll want you to tell your mother what a nice person I am.")
										.addNPC(npcId, HeadE.CHILD_UNSURE, "What?")
										.addPlayer(HeadE.NO_EXPRESSION, "I'll want you to tell your mother what a nice person I am so she rewards me for this search.")
										.addNPC(npcId, HeadE.CHILD_CALM_TALK, "It's a deal.").addItem(997, "You give the lad 100 coins.", () -> {
											if (player.getInventory().hasCoins(100)) {
												player.getInventory().removeCoins(100);
												player.getQuestManager().setStage(Quest.GERTRUDES_CAT, 2);
												GertrudesCat.updateFluffs(player);
											}
										}).addPlayer(HeadE.NO_EXPRESSION, "There you go, now where did you see Fluffs?")
										.addNPC(npcId, HeadE.CHILD_CALM_TALK, "I hide out at the lumber mill to the north-east. Just beyond the Jolly Boar Inn. I saw Fluffs running around in there. Well, not so much running as plodding lazily, but you get the idea.")
										.addPlayer(HeadE.NO_EXPRESSION, "Anything else?")
										.addNPC(npcId, HeadE.CHILD_CALM_TALK, "Well, technically you are trespassing inside there but no-one seems to care. You'll have to find the broken fence to get in. It will be a bit of a squeeze for a grown up but I'm sure you can manage that.");
									else
										pay.addPlayer(HeadE.NO_EXPRESSION, "I actually don't have 100 coins with me..")
										.addNPC(npcId, HeadE.CHILD_UNSURE, "Well it looks like someone else is going to have to find the fleabag then.");

									option("I'm not paying you a thing.", new Dialogue()
											.addPlayer(HeadE.ANGRY, "I'm not paying you a thing.")
											.addNPC(npcId, HeadE.CHILD_DOWN, "Okay then, I'll find another way to make money. You only have yourself to blame if I'm forced into a life of crime."));

									option("Okay then, I'll pay.", pay);
								}
							}));

					option("Well, never mind. It's Fluffs' loss.", new Dialogue()
							.addPlayer(HeadE.ROLL_EYES, "Well, never mind, it's Fluffs' loss.")
							.addNPC(npcId, HeadE.CHILD_CALM_TALK, "I'm sure my mum will get over it."));
				}
			});
		} else if (player.getQuestManager().getStage(Quest.GERTRUDES_CAT) == 2) {
			addPlayer(HeadE.NO_EXPRESSION, "Where did you say you saw Fluffs?");
			addNPC(npcId, HeadE.CHILD_CALM_TALK, "Weren't you listening? I saw the fleabag in the old Lumber Yard north-east of here. Walk past the Jolly Boar inn and you should find it.");
		} else {
			addPlayer(HeadE.CHEERFUL, "Hello again.");
			addNPC(npcId, HeadE.CHILD_ANGRY, "You think you're tough do you?");
			addPlayer(HeadE.SCARED, "Pardon?");
			addNPC(npcId, HeadE.CHILD_ANGRY, "I can beat anyone up!");
			addPlayer(HeadE.ROLL_EYES, "Really?");
			addSimple("The boy begins to jump around with his fists up. You wonder what sort of desperado he'll grow up to be.", () -> {
				npc.setNextAnimation(new Animation(12447));
			});
		}

		create();
	}

}
