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
package com.rs.game.content.quests.princealirescue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class OsmanPrinceAliRescueD extends Conversation {
	public final static int OSMAN = 5282;
	final int FIRSTTHING = 0;
	final int SECONDTHING = 1;

	public OsmanPrinceAliRescueD(Player player) {
		super(player);
		if(player.getQuestManager().getStage(Quest.PRINCE_ALI_RESCUE) == PrinceAliRescue.NOT_STARTED)
			addNPC(OSMAN, HeadE.HAPPY_TALKING, "I have no reason to trust you regarding these affairs.");
		if(player.getQuestManager().getStage(Quest.PRINCE_ALI_RESCUE) == PrinceAliRescue.STARTED) {
			addPlayer(HeadE.HAPPY_TALKING, "The chancellor trusts me. I have come for instructions.");
			addNPC(OSMAN, HeadE.TALKING_ALOT, "Our prince is captive by the Lady Keli. We just need to make the rescue. There are two things we need you to do.");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("What is the first thing I must do?", new Dialogue()
							.addNext(()->{
								player.startConversation(new OsmanPrinceAliRescueD(player, FIRSTTHING));}));
					option("What is the second thing you need?", new Dialogue()
							.addNext(()->{
								player.startConversation(new OsmanPrinceAliRescueD(player, SECONDTHING));}));
				}
			});
		}

		if(player.getQuestManager().getStage(Quest.PRINCE_ALI_RESCUE) == PrinceAliRescue.GEAR_CHECK)
			if(player.getInventory().containsItem(PrinceAliRescue.KEY_PRINT, 1) && player.getInventory().containsItem(PrinceAliRescue.BRONZE_BAR, 1) ) {
				addNPC(OSMAN, HeadE.HAPPY_TALKING, "Well done, we can make the key now.");
				addSimple("Osman takes the key imprint and the bronze bar.", () -> {
					player.getInventory().deleteItem(PrinceAliRescue.KEY_PRINT, 1);
					player.getInventory().deleteItem(PrinceAliRescue.BRONZE_BAR, 1);
					player.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).setB("Leela_has_key", true);
				});
				addNPC(OSMAN, HeadE.HAPPY_TALKING, "Pick the key up from Leela.");
				addPlayer(HeadE.HAPPY_TALKING, "Thank you. I will try to find the other items.");
			} else {
				addPlayer(HeadE.TALKING_ALOT, "Can you tell me what I still need to get?");
				if (player.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).getB("Leela_has_key"))
					addNPC(OSMAN, HeadE.CALM_TALK, "Make sure to have the bronze key from Leela.");
				else
					addNPC(OSMAN, HeadE.CALM_TALK, "A print of the key in soft clay and a bronze bar. Then, collect the key from Leela.");
				addNPC(OSMAN, HeadE.CALM_TALK, "You need to make a blonde wig somehow. Leela may help.");
				addNPC(OSMAN, HeadE.CALM_TALK, "You will need a skirt that looks the same as Keli's");
				addNPC(OSMAN, HeadE.CALM_TALK, "Something to make the prince's skin appear lighter.");
				addNPC(OSMAN, HeadE.CALM_TALK, "A rope with which to tie Keli up.");
				addNPC(OSMAN, HeadE.CALM_TALK, "Once you have everything, go to Leela. She must be ready to get the prince away.");
			}


		if(player.isQuestComplete(Quest.PRINCE_ALI_RESCUE))
			addNPC(OSMAN, HeadE.HAPPY_TALKING, "Well done. A great rescue. I will remember you if I have anything dangerous to do.");
		create();
	}

	public OsmanPrinceAliRescueD(Player player, int convoID) {
		super(player);
		switch(convoID) {
			case FIRSTTHING:
				firsthing();
				break;
			case SECONDTHING:
				secondthing();
				break;
		}
	}

	private void firsthing() {
		addPlayer(HeadE.SKEPTICAL_THINKING, "What is the first thing I must do?");
		addNPC(OSMAN, HeadE.CALM_TALK, "The prince is guarded by some stupid guards and a clever woman. The woman is our only way " +
				"to get the prince out. Only she can walk freely about the area.");
		addNPC(OSMAN, HeadE.CALM_TALK, "I think you will need to tie her up. One coil of rope should do for that. Then, disguise the " +
				"prince as her to get him out without suspicion.");
		addPlayer(HeadE.SKEPTICAL_THINKING, "How good must the disguise be?");
		addNPC(OSMAN, HeadE.CALM_TALK, "Only enough to fool the guards at a distance. Get a skirt like hers. Same colour, same style." +
				" We will only have a short time.");
		addNPC(OSMAN, HeadE.CALM_TALK, "Get a blonde wig, too. That is up to you to make or find. Something to colour the skin of the prince.");
		addNPC(OSMAN, HeadE.CALM_TALK, "My daughter and top spy, Leela, can help you. She has sent word that she has discovered where they are keeping the prince.");
		addNPC(OSMAN, HeadE.CALM_TALK, "It's near Draynor Village. She is lurking somewhere near there now.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Explain the first thing again.", new Dialogue()
						.addNext(()->{player.startConversation(new OsmanPrinceAliRescueD(player, FIRSTTHING));}));
				option("What is the second thing you need?", new Dialogue()
						.addNext(()->{player.startConversation(new OsmanPrinceAliRescueD(player, SECONDTHING));}));
				option("Okay, I better go find some things.", new Dialogue()
						.addNPC(OSMAN, HeadE.TALKING_ALOT, "May good luck travel with you. Don't forget to find Leela. It can't be done without her help."));
			}
		});
		create();
	}

	private void secondthing() {
		addNPC(OSMAN, HeadE.TALKING_ALOT, "We need the key, or we need a copy made. If you can get some soft clay then you can copy the key...");
		addNPC(OSMAN, HeadE.TALKING_ALOT, "...If you can convince Lady Keli to show it to you for a moment. She is very boastful. It should not be too hard.");
		addNPC(OSMAN, HeadE.TALKING_ALOT, "Bring the imprint to me, with a bar of bronze.", () -> {
			player.getQuestManager().setStage(Quest.PRINCE_ALI_RESCUE, PrinceAliRescue.GEAR_CHECK);});
		addPlayer(HeadE.HAPPY_TALKING, "Sounds like a mouthful, okay ill get on it!");
		create();
	}
}

