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
package com.rs.game.content.world.areas.rimmington.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class DaVinci extends Conversation {
	private static final int npcId = 336;

	public static NPCClickHandler DaVinci = new NPCClickHandler(new Object[]{ npcId }, e -> {
		switch (e.getOption()) {
			//Start Conversation
			case "Talk-to" -> e.getPlayer().startConversation(new DaVinci(e.getPlayer()));
		}
	});

	public DaVinci(Player player) {
		super(player);
		if(!Quest.BIOHAZARD.isImplemented()){
			addPlayer(HeadE.HAPPY_TALKING, "Hello!");
			addNPC(npcId, HeadE.FRUSTRATED, "Bah! A great artist such as myself should not have to suffer the HUMILIATION of spending time where commoners wander everywhere!");
			player.sendMessage("Da Vinci does not feel sufficiently moved to talk.");
		}
	}
}
