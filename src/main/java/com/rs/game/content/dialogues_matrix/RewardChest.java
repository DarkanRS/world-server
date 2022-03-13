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

import com.rs.game.model.entity.npc.qbd.QueenBlackDragon;

/**
 * Handles the Queen Black Dragon reward chest dialogue.
 *
 * @author Emperor
 *
 */
public final class RewardChest extends MatrixDialogue {

	/**
	 * The NPC.
	 */
	private QueenBlackDragon npc;

	@Override
	public void start() {
		npc = (QueenBlackDragon) parameters[0];
		super.sendDialogue("This strange device is covered in indecipherable script. It opens for you,", "displaying only a small sample of the objects it contains.");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		npc.openRewardChest(true);
		super.end();
	}

	@Override
	public void finish() {
	}

}