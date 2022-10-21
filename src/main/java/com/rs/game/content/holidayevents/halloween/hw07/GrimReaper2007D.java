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
package com.rs.game.content.holidayevents.halloween.hw07;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GrimReaper2007D extends Conversation {

	private static int[] HWEEN_MASKS = { 1053, 1055, 1057 };

	public static NPCClickHandler handleGrimTalk = new NPCClickHandler(new Object[] { 6390 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getNPC().resetDirection();
			e.getPlayer().startConversation(new GrimReaper2007D(e.getPlayer()));
		}
	};

	public GrimReaper2007D(Player player) {
		super(player);

		switch(player.getI(Halloween2007.STAGE_KEY)) {
		case -1:
			addPlayer(HeadE.SCARED, "Erm... Excuse me... Could I ask...");
			addNPC(8867, HeadE.CALM_TALK, "Speak, mortal.");
			addPlayer(HeadE.SCARED, "Well, sir, lord, erm, your highness...");
			addNPC(8867, HeadE.CALM_TALK, "I need no title.");
			addPlayer(HeadE.SCARED, "Well, of course. Sorry. I was just wondering, what is this place?");
			addNPC(8867, HeadE.CALM_TALK, "My house. You have found yourself here because I deem it necessary.");
			addPlayer(HeadE.CONFUSED, "You do?");
			addNPC(8867, HeadE.CALM_TALK, "Long have I had a servant performing tasks as instructed. He left my service to pass on into death. Such a role is to be replaced.");
			addPlayer(HeadE.CONFUSED, "I see. So you want me to be your servant?");
			addNPC(8867, HeadE.CALM_TALK, "Eager assumptions shall bring you regret, human. A worthy soul has already been picked for the position. My house is open now as various things need attending to before the transition can take place.");
			addPlayer(HeadE.CONFUSED, "So basically you want me to tidy up some things before your new servant arrives?");
			addNPC(8867, HeadE.CALM_TALK, "Interpret as you will, mortal.");
			addOption("Select an Option", "I'll help out, no problem.", "That doesn't really appeal, I'm off.");
			addPlayer(HeadE.CALM_TALK, "I'll help out, no problem.");
			addNPC(8867, HeadE.CALM_TALK, "Proceeding as instructed is the best way to survive here, human. I can always see you and can speak with you whenever I wish, as this is my domain. Go to the garden for further instructions.");
			addPlayer(HeadE.CALM_TALK, "Always watched. To the garden. Thank you. Much appreciated.", () -> {
				player.save(Halloween2007.STAGE_KEY, 1);
			});
			break;
		case 1:
		case 2:
			addPlayer(HeadE.SCARED, "Hello. Err. Grim. It's me again.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.SCARED, "I am so very sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. Go to the garden for further instructions.");
			addPlayer(HeadE.SCARED, "Excellent. Sorry. Thank you. Pardon me.");
			break;
		case 3:
			addPlayer(HeadE.SCARED, "Hello. Err. Grim. It's me again.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.SCARED, "I am so very sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. You need to get my old servant's skull from the garden and return it to me.");
			addPlayer(HeadE.SCARED, "Excellent. Sorry. Thank you. Pardon me.");
			break;
		case 4:
			addPlayer(HeadE.SCARED, "Hello. Err. Grim. It's me again.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.SCARED, "I am so very sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. Go to the westernmost room of my home for further instructions.");
			addPlayer(HeadE.SCARED, "Excellent. Sorry. Thank you. Pardon me.");
			break;
		case 5:
			addPlayer(HeadE.SCARED, "Hello. Err. Grim. It's me again.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.SCARED, "I am so very sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. You need to organize the items in my lounge as pertains to my diary. But remember, share anything you read in that diary with anyone and you will be killed.");
			addPlayer(HeadE.SCARED, "Excellent. Sorry. Thank you. Pardon me.");
			break;
		case 6:
			addPlayer(HeadE.SCARED, "Hello. Err. Grim. It's me again.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.SCARED, "I am so very sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. Head upstairs for further instructions.");
			addPlayer(HeadE.SCARED, "Excellent. Sorry. Thank you. Pardon me.");
			break;
		case 7:
			addPlayer(HeadE.SCARED, "Hello. Err. Grim. It's me again.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.SCARED, "I am so very sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. You need to test my agility course for me upstairs.");
			addPlayer(HeadE.SCARED, "Excellent. Sorry. Thank you. Pardon me.");
			break;
		case 8:
			addPlayer(HeadE.SCARED, "Hello. Err. Grim. It's me again.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.SCARED, "I am so very sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. You need to run the course one more time for me upstairs.");
			addPlayer(HeadE.SCARED, "Excellent. Sorry. Thank you. Pardon me.");
			break;
		case 9:
			addPlayer(HeadE.CALM_TALK, "You wished to speak with me?");
			addNPC(8867, HeadE.CALM_TALK, "Correct. You have completed the tasks as ordered, hence your time here is done.");
			addPlayer(HeadE.HAPPY_TALKING, "And I'm still alive. I rock!");
			addPlayer(HeadE.CONFUSED, "So...any chance of a reward for my service?");
			addNPC(8867, HeadE.CALM_TALK, "Hmmm. Mortals and their goal-driven attitude - one thing that shall forever elude my understanding. As you wish, but trouble me no more.");
			addPlayer(HeadE.CHEERFUL, "Thank you!");
			addItem(9925, "The Grim Reaper has given you a jack-o-lantern mask, a skeleton costume, random halloween mask, and you have now unlocked the Living on Borrowed Time emote!");
			addNPC(8867, HeadE.CALM_TALK, "Death will find you one day...");
			addNext(() -> {
				player.save(Halloween2007.STAGE_KEY, 10);
				player.getInventory().addItemDrop(new Item(9920, 1));
				player.getInventory().addItemDrop(new Item(9925, 1));
				player.getInventory().addItemDrop(new Item(9924, 1));
				player.getInventory().addItemDrop(new Item(9923, 1));
				player.getInventory().addItemDrop(new Item(9921, 1));
				player.getInventory().addItemDrop(new Item(9922, 1));
				player.getInventory().addItemDrop(new Item(HWEEN_MASKS[Utils.random(HWEEN_MASKS.length)]));
				player.addDiangoReclaimItem(9920);
				player.addDiangoReclaimItem(9925);
				player.addDiangoReclaimItem(9924);
				player.addDiangoReclaimItem(9923);
				player.addDiangoReclaimItem(9921);
				player.addDiangoReclaimItem(9922);
				player.getEmotesManager().unlockEmote(Emote.LIVING_BORROWED_TIME);
				player.fakeHit(new Hit(player.getHitpoints(), HitLook.TRUE_DAMAGE));
				player.sendDeath(null);
				player.fadeScreen(() -> {
					player.startConversation(new Dialogue().addPlayer(HeadE.CONFUSED, "Well. That was an experience I will never forget."));
				});
			});
			break;
		}
		create();
	}

}
