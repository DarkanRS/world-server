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
package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class TamMcGrubor  {

	public static NPCClickHandler handleTalkTo = new NPCClickHandler(new Object[] { 14414 }, e -> {
		e.getPlayer().startConversation(new Dialogue()
				.addNPC(14414, HeadE.SECRETIVE, "You interested in Runecrafting buddy?.. You wanna get some of that wicked action?")
				.addOptions(ops -> {
					ops.add("No thanks.. You seem a little sketchy to me \"buddy\"..")
						.addPlayer(HeadE.CONFUSED, "No thanks.. You seem a little sketchy to me \"buddy\"..");
					ops.add("Sure, give me one of those things.")
						.addPlayer(HeadE.CHEERFUL, "Sure, give me one of those things.")
						.addNPC(14414, HeadE.SECRETIVE, "Alright son. Go ahead and take it. It recharges its power each day.")
						.addItem(22332, "Tam hands you a wicked hood.", () -> e.getPlayer().getInventory().addItem(22332, 1));
				}));
	});
}
