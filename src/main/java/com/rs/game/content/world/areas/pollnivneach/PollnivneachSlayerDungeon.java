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
package com.rs.game.content.world.areas.pollnivneach;

import com.rs.game.content.world.AgilityShortcuts;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PollnivneachSlayerDungeon {

	public static ObjectClickHandler handleBarriers = new ObjectClickHandler(new Object[] { 31435, 31436 }, e -> {
		e.getPlayer().setNextSpotAnim(new SpotAnim(1659));
		if (e.getObjectId() == 31435)
			AgilityShortcuts.forceMovementInstant(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() > e.getObject().getX() ? -2 : 2, 0), 10584, 1);
		else
			AgilityShortcuts.forceMovementInstant(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -2 : 2), 10584, 1);
	});

	public static ObjectClickHandler handleDownStairsNE = new ObjectClickHandler(new Object[] { 31412 }, new Tile[] { Tile.of(3374, 9426, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(-52, -5061));
	});

	public static ObjectClickHandler handleUpStairsNE = new ObjectClickHandler(new Object[] { 31417 }, new Tile[] { Tile.of(3317, 4364, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(52, 5061));
	});

	public static ObjectClickHandler handleDownStairsSE = new ObjectClickHandler(new Object[] { 31412 }, new Tile[] { Tile.of(3377, 9367, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(-54, -5028));
	});

	public static ObjectClickHandler handleUpStairsSE = new ObjectClickHandler(new Object[] { 31417 }, new Tile[] { Tile.of(3318, 4339, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(54, 5028));
	});

	public static ObjectClickHandler handleDownStairsSW = new ObjectClickHandler(new Object[] { 31412 }, new Tile[] { Tile.of(3338, 9368, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(-72, -5029));
	});

	public static ObjectClickHandler handleUpStairsSW = new ObjectClickHandler(new Object[] { 31417 }, new Tile[] { Tile.of(3271, 4339, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(72, 5029));
	});

	public static ObjectClickHandler handleDownStairsNW = new ObjectClickHandler(new Object[] { 31412 }, new Tile[] { Tile.of(3340, 9426, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(-68, -5059));
	});

	public static ObjectClickHandler handleUpStairsNW = new ObjectClickHandler(new Object[] { 31417 }, new Tile[] { Tile.of(3277, 4367, 0) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(68, 5059));
	});
}
