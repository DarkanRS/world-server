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
package com.rs.game.model.entity.player.managers;

import com.rs.engine.cutscene.Cutscene;
import com.rs.game.model.entity.player.Player;

public final class CutsceneManager {

	private Player player;
	private Cutscene cutscene;
	
	public CutsceneManager(Player player) {
		this.player = player;
	}

	public void process() {
		if ((cutscene == null) || cutscene.process())
			return;
		cutscene = null;
	}

	public void logout() {
		if (hasCutscene())
			cutscene.logout();
	}

	public boolean hasCutscene() {
		return cutscene != null;
	}

	public boolean play(Cutscene cutscene) {
		if (hasCutscene() || (cutscene == null))
			return false;
		cutscene.setPlayer(player);
		cutscene.createObjectMap();
		cutscene.construct(player);
		this.cutscene = cutscene;
		return true;
	}

}
