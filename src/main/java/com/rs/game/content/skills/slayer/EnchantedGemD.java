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
package com.rs.game.content.skills.slayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;

public class EnchantedGemD extends Conversation {
	public EnchantedGemD(Player player, Master master) {
		super(player);
		int NPC = master.npcId;
		addNPC(NPC, HeadE.CALM_TALK, "Hello there, "+ player.getDisplayName() + ", what can I help you with?");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("How am I doing so far?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "How am I doing so far?")
						.addNPC(NPC, HeadE.CALM_TALK, player.getSlayer().getTaskString())
				);
				option("Who are you?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Who are you?")
						.addNPC(NPC, HeadE.CALM_TALK, "My name is "+Utils.formatPlayerNameForDisplay(Master.getMasterForId(NPC).name().toLowerCase())+"; I'm a Slayer Master.")
				);
				option("Where are you?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Where are you?")
						.addNPC(NPC, HeadE.CALM_TALK, "I am talking to you through a spectral dimension and don't want to reveal my location.")
						.addPlayer(HeadE.HAPPY_TALKING, "Is that so?")
						.addNPC(NPC, HeadE.CALM_TALK, "Yes.")
				);
			}
		});
		create();
	}
}
