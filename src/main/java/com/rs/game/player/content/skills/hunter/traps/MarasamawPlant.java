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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.hunter.traps;

import com.rs.game.World;
import com.rs.game.npc.others.BoxHunterNPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.BoxTrapType;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class MarasamawPlant extends BoxStyleTrap {

	public MarasamawPlant(Player player, WorldTile tile) {
		super(player, BoxTrapType.MARASAMAW_PLANT, tile);
	}

	@Override
	public void handleCatch(BoxHunterNPC npc, boolean success) {
		if (success) {
			setId(npc.getType().getObjectCatch());
			npc.setNextAnimation(new Animation(-1));
			npc.setRespawnTask();
		} else
			setId(npc.getType().getObjectFail());
		setStatus(Status.CATCHING);
		if (success)
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					World.sendObjectAnimation(getOwner(), MarasamawPlant.this, new Animation(3300));
				}
			}, 0);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (success)
					setId(npc.getType().getObjectSuccess());
				setNpcTrapped(npc.getType());
				setStatus(success ? Status.SUCCESS : Status.FAIL);
			}
		}, 3);
	}
}
