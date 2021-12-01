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
package com.rs.game.player.cutscenes;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.actions.CutsceneAction;
import com.rs.game.player.cutscenes.actions.LookCameraAction;
import com.rs.game.player.cutscenes.actions.PosCameraAction;
import com.rs.lib.game.WorldTile;

public class NexCutScene extends Cutscene {

	private WorldTile dir;
	private int selected;

	public NexCutScene(WorldTile dir, int selected) {
		this.dir = dir;
		this.selected = selected;
	}

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		int xExtra = 0;
		int yExtra = 0;
		if (selected == 0)
			yExtra -= 7;
		else if (selected == 2)
			yExtra += 7;
		else if (selected == 1)
			xExtra -= 7;
		else
			xExtra += 7;
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList.add(new PosCameraAction(getX(player, 2925 + xExtra), getY(player, 5203 + yExtra), 2500, -1));
		actionsList.add(new LookCameraAction(getX(player, dir.getX()), getY(player, dir.getY()), 2500, 3));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
