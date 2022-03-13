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
package com.rs.game.content.dialogues_matrix;

import com.rs.game.World;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;

public class FlowerPickup extends MatrixDialogue {

	GameObject flowerObject;
	int flowerId;

	public int getFlowerId(int objectId) {
		return 2460 + ((objectId - 2980) * 2);
	}

	@Override
	public void start() {
		flowerObject = (GameObject) parameters[0];
		flowerId = (int) parameters[1];
		sendOptionsDialogue("What do you want to do with the flowers?", "Pick", "Leave them");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (componentId == 11) {
				player.setNextAnimation(new Animation(827));
				player.getInventory().addItem(getFlowerId(flowerId), 1);
				player.getInventory().refresh();
				World.removeObject(flowerObject);
			}
			end();
		}
	}

	@Override
	public void finish() {

	}
}