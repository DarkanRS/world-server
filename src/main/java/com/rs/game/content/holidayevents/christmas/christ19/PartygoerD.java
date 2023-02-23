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
package com.rs.game.content.holidayevents.christmas.christ19;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class PartygoerD extends Conversation {

	public static NPCClickHandler handle = new NPCClickHandler(new Object[] { 9386, 9389, 9392 }, e -> e.getPlayer().startConversation(new PartygoerD(e.getPlayer(), e.getNPC())));

	public PartygoerD(Player player, NPC npc) {
		super(player);

		switch(player.getI(Christmas2019.STAGE_KEY)) {
		case 10:
			addPlayer(HeadE.HAPPY_TALKING, "Merry Christmas!");
			addNPC(npc.getId(), HeadE.HAPPY_TALKING, "Merry Christmas! I'm so glad the feast was able to be saved!");
			addPlayer(HeadE.HAPPY_TALKING, "It was pretty exhausting, but I am pretty happy as well.");
			break;
		default:
			addPlayer(HeadE.CHEERFUL, "Merry Christmas!");
			addNPC(npc.getId(), HeadE.UPSET_SNIFFLE, "Merry Christmas..");
			addPlayer(HeadE.CONFUSED, "What's wrong?");
			addNPC(npc.getId(), HeadE.UPSET_SNIFFLE, "Someone stole the food for the feast we were going to have. Santa had really planned it out well.");
			addPlayer(HeadE.WORRIED, "Oh no! I'd better go talk to Santa and see what I can do then!");
			break;
		}

		create();
	}

}
