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
package com.rs.game.content.quests.handlers.merlinscrystal;

import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class MerlinsCrystalRitualScene extends Controller {
	NPC spirit;
	final int THRANTAX_SPIRIT = 238;

	@Override
	public void start() {
		player.lock();
		playCutscene();
	}

	private void playCutscene() {
		boolean hasSpirit = false;
		for(NPC npc : World.getNPCsInRegion(player.getRegionId()))
			if(npc.getId() == THRANTAX_SPIRIT)
				hasSpirit = true;
		if(hasSpirit)
			;
		else {
            OwnedNPC spirit = new OwnedNPC(player, THRANTAX_SPIRIT, new WorldTile(2780, 3516, 0), true);
			player.musicTrack(449);
			spirit.setNextSpotAnim(new SpotAnim(1605, 0, 0));
			spirit.setCantInteract(true);
			spirit.faceSouth();
			spirit.setRandomWalk(false);
		}
		player.startConversation(new ThrantaxMerlinsCrystalD(player).getStart());
	}

	@Override
	public boolean login() {
		forceClose();
		return false;
	}

	@Override
	public boolean logout() {
		player.unlock();
		return false;
	}

	@Override
	public void forceClose() {
		player.unlock();
	}

}
