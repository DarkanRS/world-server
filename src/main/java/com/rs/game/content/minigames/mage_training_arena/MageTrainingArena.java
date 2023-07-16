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
package com.rs.game.content.minigames.mage_training_arena;

import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MageTrainingArena {
	//Mage Training Arena
	public static ObjectClickHandler handlemagetrainladderup = new ObjectClickHandler(new Object[] { 10775 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3357, 3307, 1));
	});

	public static ObjectClickHandler handlemagetrainladderdown = new ObjectClickHandler(new Object[] { 10776 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3360, 3306, 0));
	});

	public static ObjectClickHandler handlemagetrainladderup2 = new ObjectClickHandler(new Object[] { 10771 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3369, 3307, 1));
	});

	public static ObjectClickHandler handlemagetrainladderdown2 = new ObjectClickHandler(new Object[] { 10773 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3367, 3306, 0));
	});

	public static ObjectClickHandler handlerunemechanicup = new ObjectClickHandler(new Object[] { 528 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3358, 3305, 0));
	});

	public static ObjectClickHandler handlerunemechanicdown = new ObjectClickHandler(new Object[] { 527 }, e -> {
		e.getPlayer().setNextTile(Tile.of(3619, 4814, 0));
	});



}
