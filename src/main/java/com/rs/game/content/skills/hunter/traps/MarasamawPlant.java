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
package com.rs.game.content.skills.hunter.traps;

import com.rs.game.World;
import com.rs.game.content.skills.hunter.BoxHunterNPC;
import com.rs.game.content.skills.hunter.BoxHunterType;
import com.rs.game.content.skills.hunter.BoxTrapType;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;

public class MarasamawPlant extends BoxStyleTrap {

	public MarasamawPlant(Player player, Tile tile) {
		super(player, BoxTrapType.MARASAMAW_PLANT, tile);
	}

	@Override
	public void handleCatch(BoxHunterNPC npc, boolean success) {
		BoxHunterType npcType = npc.getType(getOwner());
		if (npcType == null)
			return;
		if (success) {
			setId(npcType.getObjectCatch());
			npc.setNextAnimation(new Animation(-1));
			npc.setRespawnTask();
		} else
			setId(npcType.getObjectFail());
		setStatus(Status.CATCHING);
		if (success)
			WorldTasks.schedule(new Task() {
				@Override
				public void run() {
					World.sendObjectAnimation(MarasamawPlant.this, new Animation(3300));
				}
			}, 0);
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				if (success)
					setId(npcType.getObjectSuccess());
				setNpcTrapped(npcType);
				setStatus(success ? Status.SUCCESS : Status.FAIL);
			}
		}, 3);
	}
}
