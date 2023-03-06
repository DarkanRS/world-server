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
package com.rs.game.content.quests.ernestthechicken;

import com.rs.game.World;
import com.rs.game.content.world.doors.Doors.Door;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.VarManager;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public class PuzzleRoom {
	//Object Ids
	static int LEVER_A = 146;
	static int LEVER_B = 147;
	static int LEVER_C = 148;
	static int LEVER_D = 149;
	static int LEVER_E = 150;
	static int LEVER_F = 151;
	static int DOOR1 = 144;
	static int DOOR2 = 2373;
	static int DOOR3 = 145;
	static int DOOR4 = 140;
	static int DOOR5 = 143;
	static int DOOR6 = 138;
	static int DOOR7 = 137;
	static int DOOR8 = 2374;
	static int DOOR9 = 141;

	//vars
	static int LEVER_A_V = 1788;
	static int LEVER_B_V = 1789;
	static int LEVER_C_V = 1790;
	static int LEVER_D_V = 1791;
	static int LEVER_E_V = 1792;
	static int LEVER_F_V = 1793;
	static int DOOR1_V = 1801;
	static int DOOR2_V = 1796;
	static int DOOR3_V = 1802;
	static int DOOR4_V = 1797;
	static int DOOR5_V = 1800;
	static int DOOR6_V = 1795;
	static int DOOR7_V = 1794;
	static int DOOR8_V = 1799;
	static int DOOR9_V = 1798;





	public static ObjectClickHandler handleLevers = new ObjectClickHandler(new Object[] { LEVER_A, LEVER_B, LEVER_C, LEVER_D, LEVER_E, LEVER_F }, e -> {
		GameObject obj = e.getObject();
		VarManager vars = e.getPlayer().getVars();
		int id = obj.getId();

		//step 1/5, https://runescape.salmoneus.net/runescape-2007/quests/ernest-the-chicken.html
		if(vars.getVar(LEVER_A_V) == 0 && id == LEVER_A) {
			vars.setVarBit(LEVER_A_V, 1);
			if(vars.getVarBit(LEVER_B_V) == 1) {
				vars.setVarBit(DOOR1_V, 1);
				vars.setVarBit(DOOR4_V, 1);
				vars.setVarBit(DOOR5_V, 1);
			}
		}
		if(vars.getVar(LEVER_B_V) == 0 && id == LEVER_B) {
			vars.setVarBit(LEVER_B_V, 1);
			if(vars.getVarBit(LEVER_A_V) == 1) {
				vars.setVarBit(DOOR1_V, 1);
				vars.setVarBit(DOOR4_V, 1);
				vars.setVarBit(DOOR5_V, 1);
			}
		}

		//step 3
		if(vars.getVar(LEVER_D_V) == 0 && id == LEVER_D) {
			vars.setVarBit(LEVER_D_V, 1);
			vars.setVarBit(DOOR2_V, 1);
			vars.setVarBit(DOOR3_V, 1);

			//reset A & B
			vars.setVarBit(LEVER_A_V, 0);
			vars.setVarBit(LEVER_B_V, 0);
			vars.setVarBit(DOOR1_V, 0);
			vars.setVarBit(DOOR4_V, 0);
			vars.setVarBit(DOOR5_V, 0);
		}

		//Step 7//11
		if(vars.getVar(LEVER_E_V) == 0 && id == LEVER_E) {
			vars.setVarBit(LEVER_E_V, 1);
			if(vars.getVarBit(LEVER_F_V) == 1) {
				vars.setVarBit(DOOR6_V, 1);
				vars.setVarBit(DOOR7_V, 1);
				vars.setVarBit(DOOR1_V, 0);
				vars.setVarBit(DOOR4_V, 0);
				vars.setVarBit(DOOR5_V, 0);
			}
			if(vars.getVarBit(LEVER_C_V) == 1)
				vars.setVarBit(DOOR9_V, 1);
		}

		if(vars.getVar(LEVER_F_V) == 0 && id == LEVER_F) {
			vars.setVarBit(LEVER_F_V, 1);
			if(vars.getVarBit(LEVER_E_V) == 1) {
				vars.setVarBit(DOOR6_V, 1);
				vars.setVarBit(DOOR7_V, 1);
			}
		}

		//step 9
		if(vars.getVar(LEVER_C_V) == 0 && id == LEVER_C) {
			vars.setVarBit(LEVER_C_V, 1);
			vars.setVarBit(LEVER_E_V, 0);
			vars.setVarBit(DOOR8_V, 1);
			vars.setVarBit(DOOR2_V, 0);
		}
	});

	public static ObjectClickHandler handleDoors = new ObjectClickHandler(false, new Object[] { DOOR1, DOOR2, DOOR3, DOOR4, DOOR5, DOOR6, DOOR7, DOOR8, DOOR9 }, e -> {
		e.getPlayer().setRouteEvent(new RouteEvent(e.getObject(), () -> {
			if (!WorldUtil.isInRange(e.getPlayer().getTile(), e.getObject().getTile(), 2))
				return;
			if(e.getObject().getId() == DOOR1 || e.getObject().getId() == DOOR3 || e.getObject().getId() == DOOR8 || e.getObject().getId() == DOOR5)
				handlePuzzle2Door(e.getPlayer(), e.getObject(), 2);
			else
				handlePuzzleDoor(e.getPlayer(), e.getObject(), 2);
		}, true));
	});

	private static void handlePuzzleDoor(Player player, GameObject object, int offset) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		int rotation = object.getRotation(open ? 0 + offset : -1 + offset);
		Tile adjusted = object.getTile();
		switch (rotation) {
		case 0:
			adjusted = adjusted.transform(open ? 0 : 1, 0, 0);
			break;
		case 1:
			adjusted = adjusted.transform(0, open ? 1 : 0, 0);
			break;
		case 2:
			adjusted = adjusted.transform(open ? 0 : 0, 1, 0);
			break;
		case 3:
			adjusted = adjusted.transform(0, open ? 0 : 1, 0);
			break;
		}
		Door opp = new Door(object.getId(), object.getType(), object.getRotation(open ? 3 : -1), adjusted, object);

		Tile toTile = object.getTile();
		switch (object.getRotation()) {
		case 0:
			toTile = toTile.transform(player.getX() < object.getX() ? 0 : -1, 0, 0);
			break;
		case 1:
			toTile = toTile.transform(0, player.getY() > object.getY() ? 0 : 1, 0);
			break;
		case 2:
			toTile = toTile.transform(player.getX() > object.getX() ? 0 : 1, 0, 0);
			break;
		case 3:
			toTile = toTile.transform(0, player.getY() < object.getY() ? 0 : -1, 0);
			break;
		}
		World.spawnObjectTemporary(new GameObject(object).setIdNoRefresh(83), 2, true);
		World.spawnObjectTemporary(opp, 2, true);
		player.addWalkSteps(toTile, 3, false);

	}

	private static void handlePuzzle2Door(Player player, GameObject object, int offset) {
		boolean open = object.getDefinitions(player).containsOption("Open");
		int rotation = object.getRotation(open ? 0 + offset : -1 + offset);
		Tile adjusted = object.getTile();
		switch (rotation) {
		case 0:
			adjusted = adjusted.transform(open ? 0 : 1, 0, 0);
			break;
		case 1:
			adjusted = adjusted.transform(-1, open ? 0 : 0, 0);
			break;
		case 2:
			adjusted = adjusted.transform(open ? 1 : 0, 0, 0);
			break;
		case 3:
			adjusted = adjusted.transform(0, open ? 0 : 1, 0);
			break;
		}
		Door opp = new Door(object.getId(), object.getType(), object.getRotation(open ? 3 : -1), adjusted, object);

		Tile toTile = object.getTile();
		switch (object.getRotation()) {
		case 0:
			toTile = toTile.transform(player.getX() < object.getX() ? 0 : -1, 0, 0);
			break;
		case 1:
			toTile = toTile.transform(0, player.getY() > object.getY() ? 0 : 1, 0);
			break;
		case 2:
			toTile = toTile.transform(player.getX() > object.getX() ? 0 : 1, 0, 0);
			break;
		case 3:
			toTile = toTile.transform(0, player.getY() < object.getY() ? 0 : -1, 0);
			break;
		}
		World.spawnObjectTemporary(new GameObject(object).setIdNoRefresh(83), 2, true);
		World.spawnObjectTemporary(opp, 2, true);
		player.addWalkSteps(toTile, 3, false);

	}


	public static LoginHandler onLogin = new LoginHandler(e -> {
		e.getPlayer().getVars().setVarBit(LEVER_A_V, 0);
		e.getPlayer().getVars().setVarBit(LEVER_B_V, 0);
		e.getPlayer().getVars().setVarBit(LEVER_C_V, 0);
		e.getPlayer().getVars().setVarBit(LEVER_D_V, 0);
		e.getPlayer().getVars().setVarBit(LEVER_E_V, 0);
		e.getPlayer().getVars().setVarBit(LEVER_F_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR1_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR2_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR3_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR4_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR5_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR6_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR7_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR8_V, 0);
		e.getPlayer().getVars().setVarBit(DOOR9_V, 0);
	});
}
