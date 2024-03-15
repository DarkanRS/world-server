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
package com.rs.game.content.world.areas.canifis.npcs;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Werewolf extends NPC {

	private final int realId;

	public Werewolf(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		realId = id;
	}

	public boolean hasWolfbane(Entity target) {
		if (target instanceof NPC)
			return false;
		return ((Player) target).getEquipment().getWeaponId() == 2952;
	}

	@Override
	public void processNPC() {
		if (isDead() || isCantInteract())
			return;
		if (isUnderCombat() && getId() == realId && Utils.random(5) == 0) {
			final Entity target = getCombat().getTarget();
			if (!hasWolfbane(target)) {
				setNextAnimation(new Animation(6554));
				setCantInteract(true);
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						transformIntoNPC(realId - 20);
						setNextAnimation(new Animation(-1));
						setCantInteract(false);
						setTarget(target);
					}
				}, 1);
				return;
			}
		}
		super.processNPC();
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6026, 6027, 6028, 6029, 6030, 6031, 6032, 6033, 6034, 6035, 6036, 6037, 6038, 6039, 6040, 6041, 6042, 6043, 6044, 6045 }, (npcId, tile) -> new Werewolf(npcId, tile, false));
}
