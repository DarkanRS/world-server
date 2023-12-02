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
package com.rs.game.content.skills.slayer.npcs;

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class MutatedZygomite extends ConditionalDeath {

	boolean lvl74;

	public MutatedZygomite(int id, Tile tile) {
		super(7421, null, false, id, tile);
		lvl74 = id == 3344;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!isUnderCombat() && !isDead())
			resetNPC();
	}

	private void resetNPC() {
		setNextNPCTransformation(lvl74 ? 3344 : 3345);
		setNextTile(getRespawnTile());
	}

	@Override
	public void onRespawn() {
		resetNPC();
	}

	public static void transform(final Player player, final NPC npc) {
		if (npc.isCantInteract())
			return;
		player.setNextAnimation(new Animation(2988));
		npc.setNextNPCTransformation(npc.getId() + 2);
		npc.setNextAnimation(new Animation(2982));
		npc.setCantInteract(true);
		npc.getCombat().setTarget(player);
		npc.setHitpoints(npc.getMaxHitpoints());
		npc.resetLevels();
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				npc.setCantInteract(false);
			}
		}, 1);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 3344, 3345, 3346, 3347 }, (npcId, tile) -> new MutatedZygomite(npcId, tile));
}
