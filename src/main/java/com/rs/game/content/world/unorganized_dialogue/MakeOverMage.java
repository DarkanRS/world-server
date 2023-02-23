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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.game.content.PlayerLook;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MakeOverMage extends Dialogue {

	private static int MAKEOVER_MAGE = 599;

	static class MakeoverMage extends Conversation {
		public MakeoverMage(Player player) {
			super(player);

			if (!player.getAppearance().isMale())
				MAKEOVER_MAGE = 2676;

			addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "Hello there! I am known as the Makeover Mage! I have spent many years researching magicks that can change your physical appearence.");
			addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "I call it a 'makeover'. Would you like to perform my magicks on you?");
			addOptions(new Options() {
				@Override
				public void create() {
					option("Tell me more about this 'makeover'.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "Tell me more about this 'makeover'.")
							.addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "Why, of course! Basically, and I will explain this so that you understand it correctly")
							.addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "I use my secret magical technique to melt your body down into a puddle of its elements. When I have broken down all components of your body, I then rebuild it into the form I am thinking of. Or, you know, something vaguely close enough, anyway.")
							.addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "When I have broken down all components of your body, I then rebuild it into the form I am thinking of. Or, you know, something vaguely close enough, anyway.")
							.addPlayer(HeadE.CALM_TALK, "Uh...that doesn't sound particularly safe to me.")
							.addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "It's as safe as houses! Why, I have only had thirty-six major accidents this month! So, what do you say? Feel like a change?")
							.addOption("Select an option", "Sure, do it.", "No, thanks.")
							.addPlayer(HeadE.NERVOUS, "Sure, do it.")
							.addNext(() -> {
								PlayerLook.openMageMakeOver(player);
							}));
					option("Sure, do it.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "Sure, do it.")
							.addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "You, of course, agree that if by some accident you are turned into a frog you have no rights for compensation or refund.")
							.addNext(() -> {
								PlayerLook.openMageMakeOver(player);
							}));
					option("No thanks.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "No thanks. I'm happy as Saradomin made me.")
							.addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "Ehhh...suit yourself."));
					option("Cool amulet! Can I have one?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Cool amulet! Can I have one?")
							.addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "No problem, but please remember that the amulet I will sell you is only a copy of my own. It contains no magical powers and, as such, it will only cost you 100 coins.")
							.addOption("Select an option", "Sure, here you go.", "No way! That's too expensive.")
							.addNext(() -> {
								if (player.getInventory().hasCoins(100)) {
									player.getInventory().removeCoins(100);
									player.getInventory().addItem(7803, 1);
								} else
									addNPC(MAKEOVER_MAGE, HeadE.CALM_TALK, "Come back with 100gp and I'll sell you a copy.");
							}));
					option("Holiday makeovers", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Can you change my skintone to another color?")
							.addNPC(MAKEOVER_MAGE, HeadE.CONFUSED, "Woah, you don't look so good. Are you okay?")
							.addOptions(new Options() {
								@Override
								public void create() {
									option("I'm blue da ba dee da ba di.", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "I'm blue da ba dee da ba di.")
											.addNext(() -> {
												PlayerLook.setSkinCustom(player, 12);
											}));
									option("That's my secret cap, I am always angry. aaaaaaaaaargghhhh.", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "That's my secret cap, I am always angry. aaaaaaaaaargghhhh.")
											.addNext(() -> {
												PlayerLook.setSkinCustom(player, 13);
											}));
									option("No, I'm good thanks.", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "No, I'm good thanks."));
								}
							}));
				}
			});
			create();
		}
	}

	public static NPCClickHandler makeoverMageHandler = new NPCClickHandler(new Object[] { 2676, 599 }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to"))
			e.getPlayer().startConversation(new MakeoverMage(e.getPlayer()));
		if (e.getOption().equalsIgnoreCase("makeover"))
			PlayerLook.openMageMakeOver(e.getPlayer());
	});
}
