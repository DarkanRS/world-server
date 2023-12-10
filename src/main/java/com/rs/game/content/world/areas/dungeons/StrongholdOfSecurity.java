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
package com.rs.game.content.world.areas.dungeons;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class StrongholdOfSecurity {

	public static ItemClickHandler handleSkullSceptreTele = new ItemClickHandler(new Object[] { 9013 }, new String[] { "Invoke" }, e -> {
		Magic.sendTeleportSpell(e.getPlayer(), 9601, -1, 1683, -1, 0, 0, Tile.of(3080, 3424, 0), 4, true, Magic.MAGIC_TELEPORT, null);
	});

	public static ObjectClickHandler handleEntrance = new ObjectClickHandler(new Object[] { 16154 }, e -> {
		e.getPlayer().setNextTile(Tile.of(1859, 5243, 0));
	});

	public static ObjectClickHandler handleExitLadders = new ObjectClickHandler(new Object[] { 16148, 16080, 16078, 16112, 16049, 16048 }, e -> {
		e.getPlayer().ladder(Tile.of(3081, 3421, 0));
	});

	public static ObjectClickHandler handleF1Shortcut = new ObjectClickHandler(new Object[] { 16150 }, e -> {
		if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.FLAP))
			e.getPlayer().setNextTile(Tile.of(1907, 5221, 0));
		else
			e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
	});

	public static ObjectClickHandler handleF1DownLadder = new ObjectClickHandler(new Object[] { 16149 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2042, 5245, 0));
	});

	public static ObjectClickHandler handleF2Shortcut = new ObjectClickHandler(new Object[] { 16082 }, e -> {
		if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.SLAP_HEAD))
			e.getPlayer().setNextTile(Tile.of(2022, 5214, 0));
		else
			e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
	});

	public static ObjectClickHandler handleF2DownLadder = new ObjectClickHandler(new Object[] { 16081 }, e -> {
		e.getPlayer().ladder(Tile.of(2123, 5252, 0));
	});

	public static ObjectClickHandler handleF3Shortcut = new ObjectClickHandler(new Object[] { 16116 }, e -> {
		if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.IDEA))
			e.getPlayer().setNextTile(Tile.of(2144, 5279, 0));
		else
			e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
	});

	public static ObjectClickHandler handleF3DownLadder = new ObjectClickHandler(new Object[] { 16115 }, e -> {
		e.getPlayer().ladder(Tile.of(2358, 5215, 0));
	});

	public static ObjectClickHandler handleF4Shortcut = new ObjectClickHandler(new Object[] { 16050 }, e -> {
		if (e.getPlayer().getEmotesManager().unlockedEmote(Emote.STOMP))
			e.getPlayer().setNextTile(Tile.of(2344, 5213, 0));
		else
			e.getPlayer().sendMessage("The portal does not allow you to enter yet.");
	});

	public static ObjectClickHandler handleGates = new ObjectClickHandler(new Object[] { 16123, 16124, 16065, 16066, 16089, 16090, 16043, 16044 }, e -> {
		e.getPlayer().lock(3);
		e.getPlayer().setNextAnimation(new Animation(4282));
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				Tile tile = switch (e.getObject().getRotation()) {
				case 0 -> Tile.of(e.getObject().getX() == e.getPlayer().getX() ? e.getObject().getX() - 1 : e.getObject().getX(), e.getPlayer().getY(), 0);
				case 1 -> Tile.of(e.getPlayer().getX(), e.getObject().getY() == e.getPlayer().getY() ? e.getObject().getY() + 1 : e.getObject().getY(), 0);
				case 2 -> Tile.of(e.getObject().getX() == e.getPlayer().getX() ? e.getObject().getX() + 1 : e.getObject().getX(), e.getPlayer().getY(), 0);
				case 3 -> Tile.of(e.getPlayer().getX(), e.getObject().getY() == e.getPlayer().getY() ? e.getObject().getY() - 1 : e.getObject().getY(), 0);
				default -> null;
				};
				e.getPlayer().setNextTile(tile);
				e.getPlayer().setNextAnimation(new Animation(4283));
				e.getPlayer().faceObject(e.getObject());
			}
		}, 0);
	});

}
