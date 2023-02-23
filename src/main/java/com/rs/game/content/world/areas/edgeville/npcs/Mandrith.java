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
package com.rs.game.content.world.areas.edgeville.npcs;

import com.rs.game.content.Statuettes;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Mandrith extends Conversation {
	private static final int npcId = 6537;

	public static NPCClickHandler Mandrith = new NPCClickHandler(new Object[]{npcId}, e -> {
		switch (e.getOption()) {
		//Start Conversation
		case "Talk-to" -> e.getPlayer().startConversation(new Mandrith(e.getPlayer()));
		}
	});

	public Mandrith(Player player) {
		super(player);
		addPlayer(HeadE.CONFUSED, "Who are you?");
		addNPC(npcId, HeadE.AMAZED, "Why, I'm Mandrith! Inspiration to combatants both mighty and puny!");
		addPlayer(HeadE.CONFUSED, "Okay...fair enough.");
		addOptions(new Options() {
			@Override
			public void create() {

				option("Can you exchange some Ancient Revenant Artefacts?", new Dialogue()
						.addNext(() -> Statuettes.exchangeStatuettes(player)));//TODO INV check
				option("Bye.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Bye.")
				);
			}
		});
	}
}

