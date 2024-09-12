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
package com.rs.game.content.world.areas.dorgeshuun;

import com.rs.game.content.world.areas.dungeons.UndergroundDungeonController;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Dorgeshuun {

	//Dorgeshuun
	public static ObjectClickHandler handleDorgeshuunboilerstairs = new ObjectClickHandler(new Object[] { 22651, 22650 }, e -> {
		if (e.getObjectId() == 22651)
			e.getPlayer().tele(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 3 ? -0 : 0, -1));
		else if (e.getObjectId() == 22650)
			e.getPlayer().tele(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? 3 : 0, 1));
	});
	public static ObjectClickHandler handleDorgeshuunboilerstairs2 = new ObjectClickHandler(new Object[] { 22608, 22609 }, e -> {
		if (e.getObjectId() == 22608)
			e.getPlayer().tele(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -0 : e.getObject().getRotation() == 0 ? -3 : 0, e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 0 ? -0 : 0, 1));
		else if (e.getObjectId() == 22609)
			e.getPlayer().tele(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 1 ? 0 : e.getObject().getRotation() == 0 ? 3 : 0, -1));
	});

	public static ObjectClickHandler handleUndergroundDungeonEntrance = new ObjectClickHandler(new Object[] { 22945 }, e -> {
			e.getPlayer().useStairs(-1, Tile.of(3318, 9602, 0), 0, 1);
			e.getPlayer().getControllerManager().startController(new UndergroundDungeonController(false, true));
	});

	public static ObjectClickHandler handleCableCross = new ObjectClickHandler(new Object[] { 22666 }, e -> e.getPlayer().useStairs(-1, e.getPlayer().transform(-5, 0, -3), 1, 1));
	public static ObjectClickHandler handleAgilityLadder = new ObjectClickHandler(new Object[] { 22600 }, e -> e.getPlayer().useStairs(-1, e.getPlayer().transform(5, 0, 3), 1, 1));
	public static ObjectClickHandler handleDorgeshuunStairsUp = new ObjectClickHandler(new Object[] { 22937 }, e -> {
		var player = e.getPlayer();
		switch (e.getObject().getRotation()) {
			case 0 -> player.useStairs(-1, player.transform(4, 0, 1), 1, 1);
			case 1 -> player.useStairs(-1, player.transform(0, -4, 1), 1, 1);
			case 2 -> player.useStairs(-1, player.transform(-4, 0, 1), 1, 1);
			case 3 -> player.useStairs(-1, player.transform(0, 4, 1), 1, 1);
		}
	});

	public static ObjectClickHandler handleDorgeshuunStairsDown = new ObjectClickHandler(new Object[] { 22938 }, e -> {
		var player = e.getPlayer();
		switch (e.getObject().getRotation()) {
			case 0 -> player.useStairs(-1, player.transform(-4, 0, -1), 1, 1);
			case 1 -> player.useStairs(-1, player.transform(0, 4, -1), 1, 1);
			case 2 -> player.useStairs(-1, player.transform(4, 0, -1), 1, 1);
			case 3 -> player.useStairs(-1, player.transform(0, -4, -1), 1, 1);
		}
	});

	public static ObjectClickHandler handleDorgeshuunStairsUp1 = new ObjectClickHandler(new Object[] { 22931 }, e -> {
		var player = e.getPlayer();
		switch (e.getObject().getRotation()) {
			case 0 -> player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
			case 1 -> player.useStairs(-1, player.transform(3, 0, 1), 1, 1);
			case 2 -> player.useStairs(-1, player.transform(0, -3, 1), 1, 1);
			case 3 -> player.useStairs(-1, player.transform(-3, 0, 1), 1, 1);
		}
	});

	public static ObjectClickHandler handleDorgeshuunStairsDown1 = new ObjectClickHandler(new Object[] { 22932 }, e -> {
		var player = e.getPlayer();
		switch (e.getObject().getRotation()) {
			case 0 -> player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
			case 1 -> player.useStairs(-1, player.transform(-3, 0, -1), 1, 1);
			case 2 -> player.useStairs(-1, player.transform(0, 3, -1), 1, 1);
			case 3 -> player.useStairs(-1, player.transform(3, 0, -1), 1, 1);
		}
	});

	public static ObjectClickHandler handleDorgeshuunStairsUp2 = new ObjectClickHandler(new Object[] { 22941, 22939 }, e -> {
		var player = e.getPlayer();
		switch (e.getObject().getRotation()) {
			case 0 -> player.useStairs(-1, player.transform(3, 0, 1), 1, 1);
			case 1 -> player.useStairs(-1, player.transform(0, -3, 1), 1, 1);
			case 2 -> player.useStairs(-1, player.transform(-3, 0, 1), 1, 1);
			case 3 -> player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
		}
	});

	public static ObjectClickHandler handleDorgeshuunStairsDown2 = new ObjectClickHandler(new Object[] { 22933 }, e -> {
		var player = e.getPlayer();
		switch (e.getObject().getRotation()) {
			case 0 -> player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
			case 1 -> player.useStairs(-1, player.transform(-3, 0, -1), 1, 1);
			case 2 -> player.useStairs(-1, player.transform(0, 3, -1), 1, 1);
			case 3 -> player.useStairs(-1, player.transform(3, 0, -1), 1, 1);
		}
	});

	public static ObjectClickHandler handleDorgeshuunStairsDown3 = new ObjectClickHandler(new Object[] { 22942, 22940 }, e -> {
		var player = e.getPlayer();
		switch (e.getObject().getRotation()) {
			case 0 -> player.useStairs(-1, player.transform(-3, 0, -1), 1, 1);
			case 1 -> player.useStairs(-1, player.transform(0, 3, -1), 1, 1);
			case 2 -> player.useStairs(-1, player.transform(3, 0, -1), 1, 1);
			case 3 -> player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
		}
	});

}
