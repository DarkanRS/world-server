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
package com.rs.game.content.world.areas;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.world.doors.Doors;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Entrana {

	public static ObjectClickHandler handleMagicDoor = new ObjectClickHandler(new Object[] { 2407 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoor(e.getPlayer(), e.getObject());
			Magic.sendNormalTeleportSpell(e.getPlayer(), 0, 0, WorldTile.of(3093, 3222, 0));
		}
	};

	public static ObjectClickHandler handleEntranaDungeonLadders = new ObjectClickHandler(new Object[] { 2408 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer())
					.addNPC(656, HeadE.CONFUSED, "Be careful going in there! You are unarmed, and there is much evilness lurking down there! The evilness seems to block off our contact with our gods,")
					.addNPC(656, HeadE.CONFUSED, "so our prayers seem to have less effect down there. Oh, also, you won't be able to come back this way - This ladder only goes one way!")
					.addNPC(656, HeadE.CONFUSED, "The only exit from the caves below is a portal which is guarded by greater demons!")
					.addOption("Select an Option", "Well, that is a risk I will have to take.", "I don't think I'm strong enough to enter then.")
					.addPlayer(HeadE.CALM_TALK, "Well, that's a risk I will have to take.")
					.addNext(() -> {
						e.getPlayer().useLadder(WorldTile.of(2822, 9774, 0));
					}));
		}
	};

	public static NPCClickHandler handleCaveMonkDialogue = new NPCClickHandler(new Object[] { 656 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(656, HeadE.CALM_TALK, "Hello, I don't recommend going down. But if you must, be careful, it is a one way path!");
					addPlayer(HeadE.CALM_TALK, "All right...");
					create();
				}
			});
		}
	};
}
