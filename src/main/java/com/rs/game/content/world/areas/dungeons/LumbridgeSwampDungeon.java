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

import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
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
				ops.add("Yes", () -> e.getPlayer().setNextWorldTile(e.getNPC().getId() == 2021 ? WorldTile.of(2520, 5884, 0) : WorldTile.of(3219, 9527, 2)));
				ops.add("No, that's scary");
			});
	});

	public static ObjectClickHandler enterJunaArea = new ObjectClickHandler(new Object[] { 32944 }, e -> {
		e.getPlayer().setNextWorldTile(WorldTile.of(3219, 9532, 2));
	});

	public static ObjectClickHandler exitJunaArea = new ObjectClickHandler(new Object[] { 6658 }, e -> {
		e.getPlayer().setNextWorldTile(WorldTile.of(3226, 9542, 0));
	});

	public static ObjectClickHandler handleSteppingStone1 = new ObjectClickHandler(false, new Object[] { 5948 }, e -> {
		final boolean isRunning = e.getPlayer().getRun();
		final boolean isWest = e.getPlayer().getX() < 3206;
		final WorldTile tile = isWest ? WorldTile.of(3208, 9572, 0) : WorldTile.of(3204, 9572, 0);
		e.getPlayer().lock();
		e.getPlayer().setRun(true);
		e.getPlayer().addWalkSteps(isWest ? 3208 : 3204, 9572);
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 2)
					e.getPlayer().setNextFaceWorldTile(e.getObject().getTile());
				else if (ticks == 3) {
					e.getPlayer().setNextAnimation(new Animation(1995));
					e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, tile, 4, Utils.getAngleTo(e.getObject().getX() - e.getPlayer().getX(), e.getObject().getY() - e.getPlayer().getY())));
				} else if (ticks == 4)
					e.getPlayer().setNextAnimation(new Animation(1603));
				else if (ticks == 7) {
					e.getPlayer().setNextWorldTile(tile);
					e.getPlayer().setRun(isRunning);
					e.getPlayer().unlock();
					stop();
					return;
				}
			}
		}, 0, 0);
	});

	public static ObjectClickHandler handleSteppingStone2 = new ObjectClickHandler(false, new Object[] { 5949 }, e -> {
		final boolean isRunning = e.getPlayer().getRun();
		final boolean isSouth = e.getPlayer().getY() < 9555;
		final WorldTile tile = isSouth ? WorldTile.of(3221, 9556, 0) : WorldTile.of(3221, 9553, 0);
		e.getPlayer().lock();
		e.getPlayer().setRun(true);
		e.getPlayer().addWalkSteps(3221, isSouth ? 9556 : 9553);
		WorldTasks.schedule(new WorldTask() {
			int ticks = 0;

			@Override
			public void run() {
				ticks++;
				if (ticks == 2)
					e.getPlayer().setNextFaceWorldTile(e.getObject().getTile());
				else if (ticks == 3) {
					e.getPlayer().setNextAnimation(new Animation(1995));
					e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, tile, 4, Utils.getAngleTo(e.getObject().getX() - e.getPlayer().getX(), e.getObject().getY() - e.getPlayer().getY())));
				} else if (ticks == 4)
					e.getPlayer().setNextAnimation(new Animation(1603));
				else if (ticks == 7) {
					e.getPlayer().setNextWorldTile(tile);
					e.getPlayer().setRun(isRunning);
					e.getPlayer().unlock();
					stop();
					return;
				}
			}
		}, 0, 0);
	});
}
