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
package com.rs.game.content.holidayevents.halloween.hw09;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SpiderQueenD extends Conversation {

	public static NPCClickHandler handleSpiderTalk = new NPCClickHandler(new Object[] { 8975 }, e -> {
		e.getNPC().resetDirection();
		e.getPlayer().startConversation(new SpiderQueenD(e.getPlayer()));
	});

	public SpiderQueenD(Player player) {
		super(player);

		switch(player.getI(Halloween2009.STAGE_KEY, 0)) {
		case 4:
			addNPC(8975, HeadE.SPIDER_CALM, "Who approaches the Spider Queen?");
			addPlayer(HeadE.CALM_TALK, "A messenger of the Grim Reaper.");
			addNPC(8975, HeadE.SPIDER_CALM, "I knew he would not have the courage to meet me himself.");
			addNPC(8975, HeadE.SPIDER_CALM, "But what of the messenger? Who are you?");
			addPlayer(HeadE.CALM_TALK, player.getDisplayName()+", Your Majesty.");
			addNPC(8975, HeadE.SPIDER_CALM, "Good. You show respect, unlike your master.");
			addNPC(8975, HeadE.SPIDER_NONE, "So, "+player.getDisplayName()+", servant of the Grim Reaper - what is your master's business with me?");
			addPlayer(HeadE.CALM_TALK, "There's a spider in the Grim Reaper's bathtub.");
			addNPC(8975, HeadE.SPIDER_NONE, "Yes! I know this! I put that spider there. She is a faithful servant and she will not leave until I command it.");
			addPlayer(HeadE.CONFUSED, "Why did you put the spider in Grim's bathtub?");
			addNPC(8975, HeadE.SPIDER_EXCLAIM, "Because he insulted me! At Halloween of all times!");
			addPlayer(HeadE.CONFUSED, "What did he do to insult you?");
			addNPC(8975, HeadE.SPIDER_CALM, "He has cleaned the cobwebs from his house!");
			addNPC(8975, HeadE.SPIDER_CALM, "Since time immemorial, Guthix has declared that the Grim Reaper's role involve a particular aesthetic.");
			addNPC(8975, HeadE.SPIDER_CALM, "The scythe! The hood! The bones! The Grim Reaper must have those things!");
			addNPC(8975, HeadE.SPIDER_CALM, "The decor of his house is similarly prescribed. It must be dark. It must be ominous. It must be spooky.");
			addNPC(8975, HeadE.SPIDER_CALM, "And it must have spider-webs!");
			addNPC(8975, HeadE.SPIDER_CALM, "But the Grim Reaper has cleaned his house and removed all the spider-webs. At Hallowe'en, the most spooky of seasons!");
			addNPC(8975, HeadE.SPIDER_CALM, "I take this as an insult! The bath spider will remain until the Grim Reaper's house is full of its proper cobwebs.");
			addPlayer(HeadE.CONFUSED, "Shall I ask the Grim Reaper to put cobwebs in his house?");
			addNPC(8975, HeadE.SPIDER_CALM, "Do not ask him! Tell him! It is an instruction from the Queen of Spiders herself, and the Grim Reaper will obey it!");
			addNPC(8975, HeadE.SPIDER_CALM, "Go now! But first, speak to my herald. he will give you a companion to help you in this task.", () -> {
				player.save(Halloween2009.STAGE_KEY, 5);
			});
			break;
		case 5:
			addNPC(8975, HeadE.SPIDER_CALM, "Go now! But first, speak to my herald. he will give you a companion to help you in this task.");
			break;
		case 6:
		case 7:
		case 8:
			addNPC(8975, HeadE.SPIDER_EXCLAIM, "What are you doing here, mammal? Have you finished spinning webs in the house of Death already?");
			addPlayer(HeadE.SCARED, "No.. not quite yet.");
			addNPC(8975, HeadE.SPIDER_CALM, "Better get to it then, quickly. You're a worse procrastinator than Trent is when it comes to Hallowe'en.");
			break;
		case 9:
			addNPC(8975, HeadE.SPIDER_EXCLAIM, "What are you doing here, mammal? Have you finished spinning webs in the house of Death already?");
			addNext(new SpiderStatement("Y-yes your m-majesty! It's totally spooky!"));
			addNPC(8975, HeadE.SPIDER_CALM, "Good. Thank you, Eek.");
			addPlayer(HeadE.CONFUSED, "Will you tell the bath spider to move now?");
			addNPC(8975, HeadE.SPIDER_CALM, "Of course! Are you suggesting that the Queen of Spiders would not keep her word?");
			addPlayer(HeadE.CONFUSED, "Thank you, is that everything then?");
			addNPC(8975, HeadE.SPIDER_CALM, "Yes! I am satisfied.");
			addNPC(8975, HeadE.SPIDER_CALM, "I have a reward for you, mammal. Never let it be said that the Spider Queen is ungrateful.");
			addItem(9924, "The Spider Queen gives you a spooky skeleton outfit.");
			addItem(1053, "The Spider Queen gives you a few halloween masks.");
			addNPC(8975, HeadE.SPIDER_CALM, "There! Our business is concluded. Eek, return to your web. Mammal, you may return to your world now.");
			addNext(new SpiderStatement("Y-your majesty?"));
			addNPC(8975, HeadE.SPIDER_CALM, "What is it, Eek? Speak up!");
			addNext(new SpiderStatement("Your majesty, I want to go with " + player.getDisplayName() + "."));
			addNPC(8975, HeadE.SPIDER_CALM, "You want to go with this mammal?");
			addNext(new SpiderStatement("I want to see the world! See all the human lands and have adventures, and...and maybe I can come back and tell everyone. It'll be fun!"));
			addNPC(8975, HeadE.SPIDER_CALM, "Yes...most amusing.");
			addNPC(8975, HeadE.SPIDER_CALM, "Eek the Spider, I make you my emissary.");
			addNPC(8975, HeadE.SPIDER_CALM, "Mammal, you will protect this spider, or feel my wrath.");
			addNPC(8975, HeadE.SPIDER_CALM, "Now go, both of you.");
			addNext(() -> {
				player.save(Halloween2009.STAGE_KEY, 10);
				//player.getInventory().addItemDrop(new Item(15352, 1));
				player.getInventory().addItemDrop(9921, 1);
				player.getInventory().addItemDrop(9922, 1);
				player.getInventory().addItemDrop(9923, 1);
				player.getInventory().addItemDrop(9924, 1);
				player.getInventory().addItemDrop(9925, 1);
				player.getInventory().addItemDrop(1053, 1);
				player.getInventory().addItemDrop(1055, 1);
				player.getInventory().addItemDrop(1057, 1);
				//player.addDiangoReclaimItem(15352);
				player.addDiangoReclaimItem(9921);
				player.addDiangoReclaimItem(9922);
				player.addDiangoReclaimItem(9923);
				player.addDiangoReclaimItem(9924);
				player.addDiangoReclaimItem(9925);
				player.addDiangoReclaimItem(15353);
				player.getEmotesManager().unlockEmote(Emote.SCARED);
				player.sendMessage("You've unlocked the Scared emote!");
				player.getEmotesManager().unlockEmote(Emote.PUPPET_MASTER);
				player.sendMessage("You've unlocked the Puppet Master emote!");
				player.getEmotesManager().unlockEmote(Emote.TRICK);
				player.sendMessage("You've unlocked the Trick emote!");
			});
			break;
		default:
			addNPC(8975, HeadE.SPIDER_CALM, "You will protect Eek, or feel my wrath.");
			break;
		}

		create();
	}

}
