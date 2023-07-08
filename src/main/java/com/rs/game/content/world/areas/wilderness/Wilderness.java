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
package com.rs.game.content.world.areas.wilderness;

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.thieving.Thieving;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.DropSets;

@PluginEventHandler
public class Wilderness {

	public static ObjectClickHandler handleMagicAxeHutChests = new ObjectClickHandler(new Object[] { 2566 }, new Tile[] { Tile.of(3188, 3962, 0), Tile.of(3189, 3962, 0), Tile.of(3193, 3962, 0) }, e -> {
		switch(e.getOpNum()) {
		case OBJECT_OP1 -> {
			e.getPlayer().sendMessage("You attempt to open the chest without disarming the traps.");
			e.getPlayer().applyHit(new Hit((int) (e.getPlayer().getSkills().getLevel(Skills.HITPOINTS) + 20), Hit.HitLook.TRUE_DAMAGE));
		}
		case OBJECT_OP2 -> {
			Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 32, 14, 7.5, DropSets.getDropSet("magic_axe_hut_chest"));
		}
		default -> e.getPlayer();
		}
	});

	public static ObjectClickHandler handleKBDEnterLadder = new ObjectClickHandler(new Object[] { 1765 }, new Tile[] { Tile.of(3017, 3849, 0) }, e -> {
		e.getPlayer().useStairs(828, Tile.of(3069, 10255, 0), 1, 2);
	});

	public static ObjectClickHandler handleKBDExitLadder = new ObjectClickHandler(new Object[] { 32015 }, new Tile[] { Tile.of(3069, 10256, 0) }, e -> {
		e.getPlayer().useStairs(828, Tile.of(3017, 3848, 0), 1, 2);
	});

	public static ObjectClickHandler handleKBDEnterLever = new ObjectClickHandler(new Object[] { 1816 }, new Tile[] { Tile.of(3067, 10252, 0) }, e -> {
		e.getPlayer().stopAll();
		Magic.pushLeverTeleport(e.getPlayer(), Tile.of(2273, 4681, 0));
		e.getPlayer().getControllerManager().forceStop();
	});

	public static ObjectClickHandler handleKBDExitLever = new ObjectClickHandler(new Object[] { 1817 }, new Tile[] { Tile.of(2273, 4680, 0) }, e -> {
		Magic.pushLeverTeleport(e.getPlayer(), Tile.of(3067, 10254, 0));
		e.getPlayer().getControllerManager().startController(new WildernessController());
	});

	public static ObjectClickHandler handleFireGiantDungeonExit = new ObjectClickHandler(new Object[] { 32048 }, new Tile[] { Tile.of(3043, 10328, 0) }, e -> {
		e.getPlayer().setNextTile(e.getPlayer().transform(3, -6400, 0));
		e.getPlayer().getControllerManager().startController(new WildernessController());
	});

	public static ObjectClickHandler handleRedDragIsleShortcut = new ObjectClickHandler(new Object[] { 73657 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 54)) {
			e.getPlayer().getPackets().sendGameMessage("You need level 54 agility to use this shortcut.");
			return;
		}
		e.getPlayer().forceMove(e.getPlayer().transform(e.getPlayer().getY() > 3800 ? 1 : -1, e.getPlayer().getY() > 3800 ? -2 : 2), 4721, 25, 30);
	});

	public static ObjectClickHandler handleGWDShortcut = new ObjectClickHandler(new Object[] { 26323, 26324, 26328, 26327 }, e -> {
		Player p = e.getPlayer();
		WorldObject obj = e.getObject();
		if (!Agility.hasLevel(p, 60)) {
			p.getPackets().sendGameMessage("You need level 60 agility to use this shortcut.");
			return;
		}

		//Wildy
		if(obj.getId() == 26327)
			e.getPlayer().forceMove(Tile.of(2943, 3767, 0), 2049, 25, 60);
		if(obj.getId() == 26328) {
			p.setNextTile(Tile.of(2943, 3767, 0));
			e.getPlayer().forceMove(Tile.of(2950, 3767, 0), 2050, 25, 60);
		}

		//Outside GWD
		if(obj.getId() == 26324)
			e.getPlayer().forceMove(Tile.of(2928, 3757, 0), 2049, 25, 60);
		if(obj.getId() == 26323)
			e.getPlayer().forceMove(Tile.of(2927, 3761, 0), 2050, 25, 60);
	});



}
