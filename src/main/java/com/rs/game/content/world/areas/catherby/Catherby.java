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
package com.rs.game.content.world.areas.catherby;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Catherby {

	public static ObjectClickHandler taverlyDungeonClimbToWaterObelisk = new ObjectClickHandler(new Object[] { 32015 }, new Tile[] { Tile.of(2842, 9824, 0) }, e -> {
		e.getPlayer().ladder(Tile.of(2842, 3423, 0));
	});

	public static ObjectClickHandler ArheinsShip = new ObjectClickHandler(new Object[] { 69 }, new Tile[] { Tile.of(2805, 3421, 0) }, e -> {
		int npcId = 563;
		e.getPlayer().startConversation(new Dialogue()
				.addNPC(npcId, HeadE.ANGRY, "Hey buddy! Get away from my ship alright?")
				.addPlayer(HeadE.SAD, "Yeah... uh... sorry...")
		);
	});
	
}
