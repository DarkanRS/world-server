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

import com.rs.engine.quest.Quest;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class LumbridgeSwampDungeon {

	public static ItemOnNPCHandler handleLightCreatures = new ItemOnNPCHandler(false, new Object[] { 2021, 2022 }, e -> {
		if (e.isAtNPC())
			return;
		if (!e.getPlayer().isQuestComplete(Quest.WHILE_GUTHIX_SLEEPS, "to lure the light creature."))
			return;
		//spotanims 1932 1933
		if (e.getItem().getId() == 4702)
			e.getPlayer().sendOptionDialogue(e.getNPC().getId() == 2021 ? "Would you like to go down into the chasm?" : "Would you like to go back up the chasm?", ops -> {
				ops.add("Yes", () -> e.getPlayer().tele(e.getNPC().getId() == 2021 ? Tile.of(2520, 5884, 0) : Tile.of(3219, 9527, 2)));
				ops.add("No, that's scary");
			});
	});

	public static ObjectClickHandler enterJunaArea = new ObjectClickHandler(new Object[] { 32944 }, e -> {
		e.getPlayer().tele(Tile.of(3219, 9532, 2));
	});

	public static ObjectClickHandler exitJunaArea = new ObjectClickHandler(new Object[] { 6658 }, e -> {
		e.getPlayer().tele(Tile.of(3226, 9542, 0));
	});

	public static ObjectClickHandler handleSteppingStone1 = new ObjectClickHandler(false, new Object[] { 5948 }, e -> {
		final boolean isRunning = e.getPlayer().getRun();
		final boolean isWest = e.getPlayer().getX() < 3206;
		final Tile tile = isWest ? Tile.of(3208, 9572, 0) : Tile.of(3204, 9572, 0);
		e.getPlayer().lock();
		e.getPlayer().setRun(true);
		e.getPlayer().addWalkSteps(isWest ? 3208 : 3204, 9572);
		WorldTasks.schedule(new Task() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 2)
					e.getPlayer().setNextFaceTile(e.getObject().getTile());
				else if (ticks == 3)
					e.getPlayer().forceMove(tile, 1995, 0, 120, () -> e.getPlayer().setRun(isRunning));
				else if (ticks == 4) {
					e.getPlayer().setNextAnimation(new Animation(1603));
					stop();
				}
			}
		}, 0, 0);
	});

	public static ObjectClickHandler handleSteppingStone2 = new ObjectClickHandler(false, new Object[] { 5949 }, e -> {
		final boolean isRunning = e.getPlayer().getRun();
		final boolean isSouth = e.getPlayer().getY() < 9555;
		final Tile tile = isSouth ? Tile.of(3221, 9556, 0) : Tile.of(3221, 9553, 0);
		e.getPlayer().lock();
		e.getPlayer().setRun(true);
		e.getPlayer().addWalkSteps(3221, isSouth ? 9556 : 9553);
		WorldTasks.schedule(new Task() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 2)
					e.getPlayer().setNextFaceTile(e.getObject().getTile());
				else if (ticks == 3)
					e.getPlayer().forceMove(tile, 1995, 0, 120, () -> e.getPlayer().setRun(isRunning));
				else if (ticks == 4) {
					e.getPlayer().setNextAnimation(new Animation(1603));
					stop();
				}
			}
		}, 0, 0);
	});
}
