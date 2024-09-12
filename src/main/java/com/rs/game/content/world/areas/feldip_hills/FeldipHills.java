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
package com.rs.game.content.world.areas.feldip_hills;

import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FeldipHills {
	public static ObjectClickHandler handleRantzCaves = new ObjectClickHandler(new Object[] { 3379, 32068, 32069 }, e -> e.getPlayer().useStairs(e.getObjectId() == 3379 ? Tile.of(2646, 9378, 0) : Tile.of(2631, 2997, 0)));
	public static ObjectClickHandler handleStairsAndTunnel = new ObjectClickHandler(new Object[]{2811, 2812}, e -> {
		Tile destination = (e.getObjectId() == 2812) ? Tile.of(2501, 2989, 0) : Tile.of(2574, 3029, 0);
		e.getPlayer().useStairs(827, destination);
		WorldTasks.schedule(() -> e.getPlayer().playerDialogue(HeadE.AMAZED, "Wow! That tunnel went a long way."));
	});

}
