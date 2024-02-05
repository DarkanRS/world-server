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
package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.PlayerLook;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MakeOverMage {

	public static NPCClickHandler makeoverMageHandler = new NPCClickHandler(new Object[] { 599, 2676 }, new String[] { "Talk-to", "Makeover" }, e -> {
		Player player = e.getPlayer();

		int npc = player.getAppearance().isMale() ? 599 : 2676;
		switch (e.getOption()) {
			case "Makeover" -> PlayerLook.openMageMakeOver(player);
			case "Talk-to" -> player.startConversation(new Dialogue()
                    .addNPC(npc, HeadE.CALM_TALK, "Hello there! I am known as the Makeover Mage! I have spent many years researching magicks that can change your physical appearence.")
                    .addNPC(npc, HeadE.CALM_TALK, "I call it a 'makeover'. Would you like to perform my magicks on you?")
                    .addOptions((ops) -> {
                        ops.add("Tell me more about this 'makeover'.")
                                .addPlayer(HeadE.CALM_TALK, "Tell me more about this 'makeover'.")
                                .addNPC(npc, HeadE.CALM_TALK, "Why, of course! Basically, and I will explain this so that you understand it correctly")
                                .addNPC(npc, HeadE.CALM_TALK, "I use my secret magical technique to melt your body down into a puddle of its elements. When I have broken down all components of your body, I then rebuild it into the form I am thinking of. Or, you know, something vaguely close enough, anyway.")
                                .addNPC(npc, HeadE.CALM_TALK, "When I have broken down all components of your body, I then rebuild it into the form I am thinking of. Or, you know, something vaguely close enough, anyway.")
                                .addPlayer(HeadE.CALM_TALK, "Uh...that doesn't sound particularly safe to me.")
                                .addNPC(npc, HeadE.CALM_TALK, "It's as safe as houses! Why, I have only had thirty-six major accidents this month! So, what do you say? Feel like a change?")
                                .addOption("Select an option", "Sure, do it.", "No, thanks.")
                                .addPlayer(HeadE.NERVOUS, "Sure, do it.")
                                .addNext(() -> PlayerLook.openMageMakeOver(player));
                        ops.add("Sure, do it.")
                                .addPlayer(HeadE.CALM_TALK, "Sure, do it.")
                                .addNPC(npc, HeadE.CALM_TALK, "You, of course, agree that if by some accident you are turned into a frog you have no rights for compensation or refund.")
                                .addNext(() -> PlayerLook.openMageMakeOver(player));
                        ops.add("No thanks.")
                                .addPlayer(HeadE.CALM_TALK, "No thanks. I'm happy as Saradomin made me.")
                                .addNPC(npc, HeadE.CALM_TALK, "Ehhh...suit yourself.");
                        ops.add("Cool amulet! Can I have one?")
                                .addPlayer(HeadE.HAPPY_TALKING, "Cool amulet! Can I have one?")
                                .addNPC(npc, HeadE.CALM_TALK, "No problem, but please remember that the amulet I will sell you is only a copy of my own. It contains no magical powers and, as such, it will only cost you 100 coins.")
                                .addOption("Select an option", "Sure, here you go.", "No way! That's too expensive.")
                                .addNext(() -> {
                                    if (player.getInventory().hasCoins(100)) {
                                        player.getInventory().removeCoins(100);
                                        player.getInventory().addItem(7803, 1);
                                    } else
                                        player.npcDialogue(npc, HeadE.CALM_TALK, "Come back with 100gp and I'll sell you a copy.");
                                });
                        ops.add("Holiday makeovers")
                                .addPlayer(HeadE.HAPPY_TALKING, "Can you change my skintone to another color?")
                                .addNPC(npc, HeadE.CONFUSED, "Woah, you don't look so good. Are you okay?")
                                .addOptions((ops1) -> {
                                    ops1.add("I'm blue da ba dee da ba di.")
                                            .addPlayer(HeadE.CALM_TALK, "I'm blue da ba dee da ba di.")
                                            .addNext(() -> PlayerLook.setSkinCustom(player, 12));
                                    ops1.add("That's my secret cap, I am always angry. aaaaaaaaaargghhhh.")
                                            .addPlayer(HeadE.CALM_TALK, "That's my secret cap, I am always angry. aaaaaaaaaargghhhh.")
                                            .addNext(() -> PlayerLook.setSkinCustom(player, 13));
                                    ops1.add("No, I'm good thanks.")
                                            .addPlayer(HeadE.CALM_TALK, "No, I'm good thanks.");
                                });
                    }));
		}
	});
}
