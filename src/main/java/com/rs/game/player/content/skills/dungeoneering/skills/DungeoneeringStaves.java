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
package com.rs.game.player.content.skills.dungeoneering.skills;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class DungeoneeringStaves extends Action {

	private static final int[] EMPTY_STAVES = { 16977, 16979, 16981, 16983, 16985, 16987, 16989, 16991, 16993, 16995 };
	private static final int[] LEVELS = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 99 };
	private static final double[] EXPERIENCE = { 5.5, 12, 20.5, 29, 39.5, 51, 63.5, 76, 90.5, 106 };

	private final int index;
	private int cycles;

	public DungeoneeringStaves(int index, int cycles) {
		this.index = index;
		this.cycles = cycles;
	}

	@Override
	public boolean start(Player player) {
		int levelReq = LEVELS[index];
		if (player.getSkills().getLevel(Constants.RUNECRAFTING) < levelReq) {
			player.getDialogueManager().execute(new SimpleMessage(), "You need a Runecrafting level of " + levelReq + " in order to imbue this stave.");
			return false;
		}
		int staves = getUsableStaves(player, index);
		if (staves == 0)
			return false;
		if (cycles < staves)
			cycles = staves;
		if (cycles > 28)
			cycles = 28;
		return true;
	}

	@Override
	public boolean process(Player player) {
		return cycles > 0;
	}

	@Override
	public int processWithDelay(Player player) {
		cycles--;

		int stave = getNextStave(player, index);
		if (stave == -1)
			return -1;

		player.setNextAnimation(new Animation(13662));

		player.getInventory().deleteItem(new Item(stave, 1));
		player.getInventory().addItem(new Item(DungeoneeringRCD.RUNES[3][index]));

		double experience = EXPERIENCE[index];
		player.getSkills().addXp(Constants.RUNECRAFTING, experience);
		player.getSkills().addXp(Constants.MAGIC, experience);
		return 3;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

	private int getUsableStaves(Player player, int beginningIndex) {
		int staves = 0;
		for (int i = beginningIndex; i < EMPTY_STAVES.length; i++)
			staves += player.getInventory().getNumberOf(EMPTY_STAVES[i]);
		return staves;
	}

	private int getNextStave(Player player, int beginningIndex) {
		for (int i = beginningIndex; i < EMPTY_STAVES.length; i++) {
			int stave = EMPTY_STAVES[i];
			if (player.getInventory().containsItem(stave, 1))
				return stave;
		}
		return -1;
	}
}
