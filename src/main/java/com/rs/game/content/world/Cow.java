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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.world;

import com.rs.game.World;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Cow extends NPC {

    public Cow(int id, Tile tile) {
        super(id, tile);
    }

    @Override
    public void processNPC() {
        if (Utils.random(100) == 0)
            setNextForceTalk(new ForceTalk("Moo"));
        super.processNPC();
    }

    public static ItemOnNPCHandler itemOnCow = new ItemOnNPCHandler(new Object[] { "Cow" }, e -> {
    	 e.getPlayer().sendMessage("The cow doesn't want that.");
    });

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { "Cow" }, (npcId, tile) -> new Cow(npcId, tile));
    
    public static NPCDeathHandler count = new NPCDeathHandler(new Object[] { "Cow" }, e -> {
    	World.getData().getAttribs().incI("cowTrackerKills");
    });
    
    public static ObjectClickHandler signpost = new ObjectClickHandler(new Object[] { 31297 }, e -> {
    	e.getPlayer().sendMessage("So far, "+World.getData().getAttribs().getI("cowTrackerKills")+" cows have been killed by adventurers.");
    });

}