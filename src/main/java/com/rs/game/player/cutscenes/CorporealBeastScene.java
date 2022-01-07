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
import com.rs.game.player.cutscenes.actions.DialogueAction;
import com.rs.game.player.cutscenes.actions.LookCameraAction;
import com.rs.game.player.cutscenes.actions.PosCameraAction;

public class CorporealBeastScene extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return true;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<>();
		actionsList.add(new LookCameraAction(2993, 4378, 1000, -1));
		actionsList.add(new PosCameraAction(2984, 4383, 5000, -1));
		actionsList.add(new DialogueAction("You peek through the door."));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}

}
