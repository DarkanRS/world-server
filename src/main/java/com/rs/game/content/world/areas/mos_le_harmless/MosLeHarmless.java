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
package com.rs.game.content.world.areas.mos_le_harmless;

import com.rs.game.content.world.areas.dungeons.UndergroundDungeonController;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MosLeHarmless {
    public static ObjectClickHandler handleGangplanks = new ObjectClickHandler(new Object[] { 11211, 11212 }, e -> {
        boolean entering = e.getPlayer().getY() > e.getObject().getY();
        e.getPlayer().useStairs(-1, e.getPlayer().transform(0, entering ? -3 : 3, entering ? 1 : -1), 0, 1);
    });

    public static ObjectClickHandler handleUndergroundDungeonEntrance = new ObjectClickHandler(new Object[] { 15767 }, e -> {
            e.getPlayer().useStairs(-1, Tile.of(3748, 9373, 0), 0, 1); // Inline useStairs call
            e.getPlayer().getControllerManager().startController(new UndergroundDungeonController(false, true)); // Inline startController call
    });

    public static ObjectClickHandler handleUndergroundDungeonStairs = new ObjectClickHandler(new Object[] { 15791 }, e -> {
            switch(e.getObject().getX()) {
                case 3829 -> e.getPlayer().useStairs(-1, Tile.of(3830, 9461, 0));
                case 3814 -> e.getPlayer().useStairs(-1, Tile.of(3815, 9461, 0));
            }
            e.getPlayer().getControllerManager().startController(new UndergroundDungeonController(false, true));
        }
    );

    public static ObjectClickHandler handleUndergroundDungeonStairsExit = new ObjectClickHandler(new Object[] { 15811, 15812 }, e -> {
        e.getPlayer().useStairs(-1, Tile.of(3749, 2973, 0), 0, 1);
    });


}
