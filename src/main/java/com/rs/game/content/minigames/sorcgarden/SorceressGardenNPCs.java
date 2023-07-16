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
package com.rs.game.content.minigames.sorcgarden;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SorceressGardenNPCs {
	
	public static NPCClickHandler handleNpcs = new NPCClickHandler(new Object[] { 5532, 5563 }, e -> {
		if (e.getNPCId() == 5563) {
			e.getPlayer().startConversation(new Dialogue()
				.addPlayer(HeadE.CHEERFUL, "Hey kitty!")
				.addNPC(5563, HeadE.CAT_SURPRISED, "Hiss!"));
		} else if (e.getNPCId() == 5532) {
			e.getPlayer().startConversation(new Dialogue()
					.addPlayer(HeadE.CONFUSED, "Hey apprentice, do you want to try out your teleport skills again?")
					.addNPC(5532, HeadE.CHEERFUL, "Okay, here goes - and remember, to return just drink from the fountain.")
					.addNext(() -> SorceressGardenController.teleportToSorceressGardenNPC(e.getNPC(), e.getPlayer())));
		}
	});

}
