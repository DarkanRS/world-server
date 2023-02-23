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
package com.rs.game.content.quests.dwarfcannon;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CaptainLawgofD extends Conversation {

	public static final int CAPTAIN_LAWGOF = 208;

	public static NPCClickHandler talkToLawgof = new NPCClickHandler(new Object[] { 208 }, e -> e.getPlayer().startConversation(new CaptainLawgofD(e.getPlayer())));

	public CaptainLawgofD(Player player) {
		super(player);

		switch(player.getQuestManager().getStage(Quest.DWARF_CANNON)) {
		case 0:
			addPlayer(HeadE.NO_EXPRESSION, "Hello.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Guthix be praised, the cavalry has arrived! Hero, how would you like to be made an honorary member of the Black Guard?");
			addPlayer(HeadE.NO_EXPRESSION, "The Black Guard? What's that?");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Hawhaw! 'What's that' " + (player.getAppearance().isMale() ? "he" : "she") + " asks, what a sense of humour! The Black Guard is the finest regiment in the dwarven army. Only the best of the best are allowed to join it and then they receive months of rigorous training.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "However, we are currently in need of a hero, so for a limited time only I'm offering you, a human, a chance to join this prestigious regiment. What do you say?");
			addPlayer(HeadE.NO_EXPRESSION, "Sure, I'd be honoured to join.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "That's the spirit! Now trooper, we have no time to waste - the goblins are attacking from the forests to the South. There are so many of them,");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "they are overwhelming my men and breaking through our perimeter defences; could you please try to fix the stockade by replacing the broken rails with these new ones?");
			addPlayer(HeadE.NO_EXPRESSION, "Sure, sounds easy enough...", () -> {
				player.getQuestManager().setStage(Quest.DWARF_CANNON, 1);
			});

		case 1:
			if (DwarfCannon.checkRemainingRepairs(player) > 0) {
				int amount = DwarfCannon.checkRemainingRepairs(player) - player.getInventory().getNumberOf(DwarfCannon.RAILINGS);
				if (amount > 0)
					addSimple("The Dwarf Captain gives you some railings.", () -> {
						player.getInventory().addItemDrop(DwarfCannon.RAILINGS, DwarfCannon.checkRemainingRepairs(player) - player.getInventory().getNumberOf(DwarfCannon.RAILINGS));
					});
			}
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Report back to me once you've fixed the railings.");
			addPlayer(HeadE.NO_EXPRESSION, "Yes Sir, Captain!");
			break;
		case 2:
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Well done, trooper! The goblins seems to have stopped getting in, I think you've done the job!");
			addPlayer(HeadE.NO_EXPRESSION, "Great, I'll be getting on then.");
			addNPC(CAPTAIN_LAWGOF, HeadE.ANGRY, "What? I'll have you jailed for desertion! Besides, I have another commission for you. Just before the goblins over-ran us we lost contact with our watch tower to the South,");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "that's why the goblins managed to catch us unawares. I'd like you to perform a covert operation into enemy territory, to check up on the guards we have stationed there. They should have reported in by now ...");
			addPlayer(HeadE.NO_EXPRESSION, "Okay, I'll see what I can find out.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Excellent! I have two men there, the dwarf-in-charge is called Gilob, find him and tell him that I'll send him a relief guard just as soon as we mop up these remaining goblins.", () -> {
				player.getQuestManager().setStage(Quest.DWARF_CANNON, 3);
				DwarfCannon.updateVars(player);
			});
			break;
		case 3:
			addPlayer(HeadE.NO_EXPRESSION, "Hello.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Have you been to the watch tower yet?");
			if (player.getInventory().containsItem(DwarfCannon.DWARF_REMAINS)) {
				addPlayer(HeadE.NO_EXPRESSION, "I have some terrible news for you Captain, the goblins over ran the tower, your guards fought well but were overwhelmed.");
				addSimple("You give the Dwarf Captain his subordinate's remains...", () -> {
					player.getInventory().deleteItem(DwarfCannon.DWARF_REMAINS, 1);
					player.getQuestManager().setStage(Quest.DWARF_CANNON, 4);
					player.getVars().setVar(0, 4);
				});
				addNPC(CAPTAIN_LAWGOF, HeadE.SAD_MILD_LOOK_DOWN, "I can't believe it, Gilob was the finest lieutenant I had! We'll give him a fitting funeral, but what of his command? His son, Lollk, was with him. Did you find his body too?");
				addPlayer(HeadE.NO_EXPRESSION, "No, there was only one body there. I searched pretty well.");
				addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "The goblins must have taken him. Please traveler, seek out the goblins' hideout and return the lad to us. They always attack from the South-east, so they must be based down there.");
				addPlayer(HeadE.NO_EXPRESSION, "Okay, I'll see if I can find their hideout.");
			} else
				addPlayer(HeadE.NO_EXPRESSION, "Not yet.");
			break;
		case 4:
		case 5:
			addPlayer(HeadE.NO_EXPRESSION, "Hello, has Lolk returned yet?");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Not yet, please keep searching before it is too late!");
			break;
		case 6:
			addPlayer(HeadE.NO_EXPRESSION, "Hello, has Lolk returned yet?");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "He has, and I thank you from the bottom of my heart - without you he'd be a goblin barbeque!");
			addPlayer(HeadE.NO_EXPRESSION, "Always a pleasure to help.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "In that case could I ask one more favour of you... When the goblins attacked us some of them managed to slip past my guards and sabotage our cannon. I don't have anybody who understands how it works, could you have a look at it and see if you could get it working for us, please?");
			addPlayer(HeadE.NO_EXPRESSION, "Okay, I'll see what I can do.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Thank you, take this toolbelt, you'll need it... Report back to me if you manage to fix it.", () -> {
				player.getInventory().addItemDrop(DwarfCannon.TOOLKIT, 1);
				player.getQuestManager().setStage(Quest.DWARF_CANNON, 7);
			});
			break;
		case 7:
			addPlayer(HeadE.NO_EXPRESSION, "Hello again.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Hello there trooper, how's things?");
			if (!player.getInventory().containsItem(DwarfCannon.TOOLKIT)) {
				addPlayer(HeadE.NO_EXPRESSION, "I've lost your tool kit.");
				addNPC(CAPTAIN_LAWGOF, HeadE.SHAKING_HEAD, "Here, take this one.");
				player.getInventory().addItemDrop(DwarfCannon.TOOLKIT, 1);
			} else
				addPlayer(HeadE.NO_EXPRESSION, "Still working on it.");
			break;
		case 8:
			addPlayer(HeadE.NO_EXPRESSION, "Hello again.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Hello there trooper, how's things?");
			addPlayer(HeadE.HAPPY_TALKING, "Well, I think I've done it, take a look...");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "That's fantastic, well done!");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Well I don't believe it, it seems to be working perfectly! I seem to have underestimated you, trooper!");
			addPlayer(HeadE.NO_EXPRESSION, "Not bad for an adventurer eh?");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Not bad at all, your effort is appreciated, my friend. Now, if I could figure what the thing uses as ammo... The Black Guard forgot to send instructions. I know I said that was the last favour, but...");
			addPlayer(HeadE.NO_EXPRESSION, "What now?");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "I can't leave this post, could you go to the Black Guard base and find out what this thing actually shoots?");
			addPlayer(HeadE.NO_EXPRESSION, "Okay then, just for you!");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "That's great, we were lucky you came along when you did. The base is located just South of the Ice Mountain. You'll need to speak to Nulodion, the Dwarf Cannon engineer. He's the Weapon Development Chief for the Black Guard, so if anyone knows how to fire this thing, it'll be him.");
			addPlayer(HeadE.NO_EXPRESSION, "Okay, I'll see what I can do.", () -> {
				player.getQuestManager().setStage(Quest.DWARF_CANNON, 9);
			});
			break;
		case 9:
			addPlayer(HeadE.NO_EXPRESSION, "Hi.");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Hello trooper, any word from the Cannon Engineer?");
			if (player.getInventory().containsItem(DwarfCannon.AMMO_MOULD) && player.getInventory().containsItem(DwarfCannon.NULODIONS_NOTES))
				addPlayer(HeadE.NO_EXPRESSION, "Yes, I have spoken to him. He gave me an ammo mould and some notes to give to you...", () -> {
					player.getInventory().deleteItem(DwarfCannon.AMMO_MOULD, 1);
					player.getInventory().deleteItem(DwarfCannon.NULODIONS_NOTES, 1);
					player.getQuestManager().setStage(Quest.DWARF_CANNON, 10);
				});
			else
				addPlayer(HeadE.NO_EXPRESSION, "Not yet.");
			break;
		case 10:
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Aah, of course, we make the ammo! This is great, now we will be able to defend ourselves. I don't know how to thank you...");
			addPlayer(HeadE.NO_EXPRESSION, "You could give me a cannon...");
			addNPC(CAPTAIN_LAWGOF, HeadE.HAPPY_TALKING, "Hah! You'd be lucky, those things are worth a fortune. I'll tell you what though, I'll write to the Cannon Engineer requesting him to sell you one. He controls production of the cannons. He won't be able to give you one, but for the right price, I'm sure he'll sell one to you.");
			addPlayer(HeadE.NO_EXPRESSION, "Hmmm... sounds interesting. I might take you up on that.", () -> {
				player.getQuestManager().completeQuest(Quest.DWARF_CANNON);
			});
		}
		create();
	}
}
