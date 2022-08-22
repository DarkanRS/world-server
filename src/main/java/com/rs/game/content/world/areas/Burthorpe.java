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

import static com.rs.game.content.world.doors.Doors.handleDoubleDoor;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Burthorpe {

	public static ObjectClickHandler handleCaveEntrance = new ObjectClickHandler(new Object[]{66876}) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2292, 4516, 0));
		}
	};

	public static ObjectClickHandler handleCaveExit = new ObjectClickHandler(new Object[]{67002}) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2876, 3502, 0));
		}
	};

	public static ObjectClickHandler handleHeroesGuildDoors = new ObjectClickHandler(new Object[]{2624, 2625}) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().isQuestComplete(Quest.HEROES_QUEST) || e.getPlayer().getX() < e.getObject().getX()) {
				handleDoubleDoor(e.getPlayer(), e.getObject());
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(77, true);
			}
			else
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					int NPC = 796;

					{
						addNPC(NPC, HeadE.FRUSTRATED, "Hey! Only heroes are allowed in there.");
						addPlayer(HeadE.SECRETIVE, "Umm, how do I know if I am a hero?");
						addNPC(NPC, HeadE.HAPPY_TALKING, "By completing the Heroes' Quest of course");
						addPlayer(HeadE.SAD, "Oh..");
						create();
					}
				});
		}
	};

}
