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
package com.rs.game.player.content.world;

import com.rs.game.ForceTalk;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Cow extends NPC {

    public Cow(int id, WorldTile tile) {
        super(id, tile);
    }

    @Override
    public void processNPC() {
        if (Utils.random(100) == 0)
            setNextForceTalk(new ForceTalk("Moo"));
        super.processNPC();
    }

    public static ItemOnNPCHandler itemOnCow = new ItemOnNPCHandler("Cow") {
        @Override
        public void handle(ItemOnNPCEvent e) {
            e.getPlayer().sendMessage("The cow doesn't want that.");
        }
    };

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler("Cow") {
        @Override
        public NPC getNPC(int npcId, WorldTile tile) {
            return new Cow(npcId, tile);
        }
    };

}