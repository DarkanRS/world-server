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
package com.rs.game.content.quests.holygrail;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class BlackKnightTitan extends NPC {
	public BlackKnightTitan(int id, Tile tile, boolean permaDeath) {
		super(id, tile, permaDeath);
	}

	@Override
	public boolean canMove(Direction dir) {
		return false;
	}

	@Override
	public void sendDeath(final Entity source) {
		if(source instanceof Player player && player.getEquipment().getWeaponId() == 35) {
			super.sendDeath(source);
			player.sendMessage("Well done, you have defeated the Black Knight Titan!");
			boolean isRun = player.getRun();
			player.setRunHidden(false);
			player.addWalkSteps(Tile.of(player.getX() >= 2791 ? 2790 : 2792, 4722, 0), 4, false);
			WorldTasks.delay(2, () -> {
				player.setRunHidden(isRun);
			});
			return;
		}
		resetHP();
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(221, (npcId, tile) -> new BlackKnightTitan(npcId, tile, false));

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{221}, new String[]{"Talk-to"}, e -> {
		e.getPlayer().startConversation(new Dialogue()
				.addNPC(221, HeadE.CHILD_CALM_TALK, "I am the Black Knight Titan! You must pass through me before you can continue in this realm!")
				.addPlayer(HeadE.HAPPY_TALKING, "Ok, have at ye oh evil knight!")
				.addPlayer(HeadE.HAPPY_TALKING, "Actually I think I'll run away!")
		);
	});
}
