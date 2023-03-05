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
package com.rs.game.content.skills.dungeoneering.npcs.misc;

import java.util.List;

import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.npcs.DungeonNPC;
import com.rs.game.content.skills.dungeoneering.npcs.MastyxTrap;
import com.rs.game.content.skills.dungeoneering.skills.DungeoneeringTraps;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class DungeonHunterNPC extends DungeonNPC {

	public DungeonHunterNPC(int id, Tile tile, DungeonManager manager) {
		super(id, tile, manager);
	}

	@Override
	public void processNPC() {
		if (isCantInteract() || getId() >= 11096 || getManager() == null)
			return;
		super.processNPC();
		List<MastyxTrap> traps = getManager().getMastyxTraps();
		if (traps.isEmpty())
			return;
		final int tier = DungeoneeringTraps.getNPCTier(getId());
		for (final MastyxTrap trap : traps) {
			if (!withinDistance(trap.getTile(), 3) || Utils.getRandomInclusive(3) != 0)
				continue;
			trap.setCantInteract(true);
			setCantInteract(true);
			setNextFaceEntity(trap);
			addWalkSteps(trap.getX() + 1, trap.getY() + 1);

			final int trap_tier = trap.getTier();
			double successRatio = getSuccessRatio(tier, trap_tier);
			final boolean failed = successRatio < Math.random();

			setCantInteract(true);
			if (failed)
				WorldTasks.schedule(new WorldTask() {

					int ticks = 0;

					@Override
					public void run() {
						ticks++;
						if (ticks == 5)
							setNextAnimation(new Animation(13264));
						else if (ticks == 8) {
							trap.setNextNPCTransformation(1957);
							trap.setNextSpotAnim(new SpotAnim(2561 + trap_tier));
						} else if (ticks == 16) {
							getManager().removeMastyxTrap(trap);
							setCantInteract(false);
							stop();
							return;
						}
					}
				}, 0, 0);
			else
				WorldTasks.schedule(new WorldTask() {

					int ticks = 0;

					@Override
					public void run() {
						ticks++;
						if (ticks == 9) {
							trap.setNextNPCTransformation(1957);
							trap.setNextSpotAnim(new SpotAnim(2551 + trap_tier));
						} else if (ticks == 13)
							setNextAnimation(new Animation(13260));
						else if (ticks == 18)
							setNextNPCTransformation(getId() + 10);
						else if (ticks == 20) {
							setCantInteract(false);
							getManager().removeMastyxTrap(trap);
							stop();
							return;
						}
					}
				}, 0, 0);
		}
	}

	@Override
	public void drop() {
		/*World.addGroundItem(new Item(532), Tile.of(this)); //big bones
		Drops drops = NPCDrops.getDrops(getId());
		if (drops == null)
			return;
		Drop drop = drops.getDrop(Drops.COMMOM, Double.MAX_VALUE); //to make 100% chance
		if (drop == null) //shouldnt
			return;
		World.addGroundItem(new Item(drop.getItemId()), Tile.of(this)); //hide*/
	}

	private static double getSuccessRatio(int tier, int trapTier) {
		double successRatio = 0.0;
		int tierProduct = trapTier - tier;
		if (tierProduct == 0)
			successRatio = 0.5;
		else if (tierProduct > 0)
			successRatio = 0.5 + (tierProduct / 10.0);

		if (successRatio > 0.9)
			successRatio = 0.9;
		return successRatio;
	}
}
