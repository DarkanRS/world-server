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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MrEx extends Conversation {

	private static int MREX = 3709;

	public MrEx(Player player) {
		super(player);

		Dialogue options = player.isIronMan() ? addOption("Select an option", "Wilderness hats", "Ironman mode", "Skull me") : addOption("Select an option", "Wilderness hats", "Skull me");

		options.addPlayer(HeadE.CONFUSED, "I've seen wilderness hats around, what are those?")
		.addNPC(MREX, HeadE.NO_EXPRESSION, "Oh, I'm not giving those out quite yet. Sorry.")
		.addPlayer(HeadE.NO_EXPRESSION, "Unlucky..");

		if (player.isIronMan())
			options.addNPC(MREX, HeadE.CONFUSED, "Hey, I don't mean any offence by this, but would you like me to teach you to interact with other players?")
			.addNPC(MREX, HeadE.CHEERFUL, "Essentially, I am asking if you would be interested in de-activating your ironman mode by teaching you this.")
			.addOption("De-activate Ironman Mode?", "Yes, deactivate my ironman mode please.", "No, I'm quite happy being an Ironman.")
			.addNPC(MREX, HeadE.CHEERFUL, "Are you sure? You don't sound very sure.")
			.addOption("De-activate Ironman Mode?", "Yes, I am completely sure. De-activate my ironman status.", "No, I've changed my mind.")
			.addNPC(MREX, HeadE.CHEERFUL, "Alright, here's the secret to interacting with other players then")
			.addSimple("Mr. Ex whispers in your ears and teaches you how to trade.", () -> {
				player.setIronMan(false);
				player.clearTitle();
			})
			.addPlayer(HeadE.CHEERFUL, "Thank you!")
			.addNPC(MREX, HeadE.CHEERFUL, "No problem.");

		options.addNPC(MREX, HeadE.CONFUSED, "Skull you?")
		.addOption("Skull?", "Yes, skull me.", "No, nevermind.")
		.addNPC(MREX, HeadE.CHEERFUL, "Are you sure?")
		.addOption("Skull?", "Yes, skull me.", "No, I've changed my mind.")
		.addSimple("You have been skulled.", () -> {
			player.setWildernessSkull();
		})
		.addPlayer(HeadE.CHEERFUL, "Thank you!")
		.addNPC(MREX, HeadE.CHEERFUL, "No problem.");

		create();
	}

	public static NPCClickHandler handleTalk = new NPCClickHandler(new Object[] { MREX }, e -> {
		e.getPlayer().startConversation(new MrEx(e.getPlayer()));
	});

}
