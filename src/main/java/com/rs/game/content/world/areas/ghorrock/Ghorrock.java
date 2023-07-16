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
package com.rs.game.content.world.areas.ghorrock;

import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Ghorrock {
	//Ghorrock
	public static ObjectClickHandler iceblocks = new ObjectClickHandler(new Object[] { 47130 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.RITUAL_OF_MAHJARRAT)) {
			if (e.getPlayer().getX() < e.getObject().getX())
				e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 4 : e.getObject().getRotation() == 3 ? 4 : 0, e.getObject().getRotation() == 1 ? 0 : e.getObject().getRotation() == 3 ? -0 : 0, 0));
			if (e.getPlayer().getX() > e.getObject().getX())
				e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -4 : e.getObject().getRotation() == 3 ? -4 : 0, e.getObject().getRotation() == 1 ? 0 : e.getObject().getRotation() == 3 ? -0 : 0, 0));
		}else
			e.getPlayer().sendMessage("You do not meet the requirements for Ritual of Mahjarrat.");
	});

	public static ObjectClickHandler handleGhorrockstairs = new ObjectClickHandler(new Object[] { 47142, 47144 }, e -> {
		if (e.getObjectId() == 47142)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? -1 : 0, e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? -4 : 0, 1));
		else if (e.getObjectId() == 47144)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? 1 : 0, e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? 4 : 0, -1));
	});
	public static ObjectClickHandler handleGhorrockstairsuponly = new ObjectClickHandler(new Object[] { 47143 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2921, 3931, 1));
	});
	public static LoginHandler makeghorrockcanoe = new LoginHandler(e -> e.getPlayer().getVars().setVarBit(6075, 1));
	public static ObjectClickHandler handlecanoetoghorrock = new ObjectClickHandler(new Object[] { 42886 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.TALE_OF_MUSPAH)) {
		e.getPlayer().setNextTile(Tile.of(2856, 3933, 0));
		} else
			e.getPlayer().sendMessage("You do not meet the requirements for Ritual of Mahjarrat.");
	});

	public static ObjectClickHandler handlecanoeleaveghorrock = new ObjectClickHandler(new Object[] { 42858 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.TALE_OF_MUSPAH)) {
		e.getPlayer().setNextTile(Tile.of(2692, 3793, 0));
		} else
			e.getPlayer().sendMessage("You do not meet the requirements for Ritual of Mahjarrat.");
	});

	public static ObjectClickHandler handlemahjarratritualsitecavernentrance = new ObjectClickHandler(new Object[] { 42859 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.TALE_OF_MUSPAH)) {
			e.getPlayer().setNextTile(Tile.of(2843, 10364, 0));
		} else
			e.getPlayer().sendMessage("You do not meet the requirements for The Tale of the Muspah.");
	});
	public static ObjectClickHandler handlemahjarratritualsitecavernexit = new ObjectClickHandler(new Object[] { 42892 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.TALE_OF_MUSPAH)) {
			e.getPlayer().setNextTile(Tile.of(2866, 3927, 0));
		} else
			e.getPlayer().sendMessage("You do not meet the requirements for The Tale of the Muspah.");
	});

	//Zemouregal's fort
	public static ObjectClickHandler handleZemouregalstairs = new ObjectClickHandler(new Object[] { 44253, 44255 }, e -> {
		if (e.getObjectId() == 44253)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? -3 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 3 ? -0 : e.getObject().getRotation() == 0 ? 3 : 0, 1));
		else if (e.getObjectId() == 44255)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 3 ? 3 : e.getObject().getRotation() == 0 ? -0 : 0, e.getObject().getRotation() == 3 ? 0 : e.getObject().getRotation() == 0 ? -3 : 0, -1));
	});
	public static ObjectClickHandler handleZemouregalrightstairsuponly = new ObjectClickHandler(new Object[] { 44254 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2836, 3868, 1));
	});



}
