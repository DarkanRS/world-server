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
package com.rs.game.content.bosses.godwars.zaros.attack;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.content.bosses.godwars.zaros.Nex;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Siphon implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.killBloodReavers();
		nex.setNextForceTalk(new ForceTalk("A siphon will solve this!"));
		nex.voiceEffect(3317);
		nex.setNextAnimation(new Animation(6948));
		nex.setNextSpotAnim(new SpotAnim(1201));
		nex.getTempAttribs().setB("siphoning", true);
		int reaverSize = NPCDefinitions.getDefs(13458).size;
		int respawnedBloodReaverCount = 0;
		for (int i = 0; i < 3; i++) {
			WorldTile tile = nex.getNearestTeleTile(reaverSize);
			if (tile != null)
				nex.getBloodReavers()[respawnedBloodReaverCount++] = new NPC(13458, tile, true);
		}
		WorldTasks.schedule(6, () -> nex.getTempAttribs().setB("siphoning", false));
		return nex.getAttackSpeed();
	}

}
