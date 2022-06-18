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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;

public class DungeoneeringRunecrafting extends PlayerAction {

	private final int runeId;
	private final int levelRequirement;
	@SuppressWarnings("unused")
	private final double experience;
	private final int[] multipliers;
	private int cycles;

	public DungeoneeringRunecrafting(int cycles, int runeId, int levelRequirement, double experience, int... multipliers) {
		this.cycles = cycles;
		this.runeId = runeId;
		this.levelRequirement = levelRequirement;
		this.experience = experience;
		this.multipliers = multipliers;
	}

	@Override
	public boolean start(Player player) {
		int actualLevel = player.getSkills().getLevel(Constants.RUNECRAFTING);
		if (actualLevel < levelRequirement) {
			player.simpleDialogue("You need a runecrafting level of " + levelRequirement + " to craft this rune.");
			return false;
		}
		int essense = player.getInventory().getNumberOf(DungeonConstants.ESSENCE);
		if (essense == 0)
			return false;
		if (cycles < essense)
			cycles = essense;
		if (cycles > 200)
			cycles = 200;
		return true;
	}

	@Override
	public boolean process(Player player) {
		return cycles > 0;
	}

	@Override
	public int processWithDelay(Player player) {
		boolean incompleteCycle = cycles < 10;

		int cycleCount = incompleteCycle ? cycles : 10;
		cycles -= cycleCount;

		player.setNextAnimation(new Animation(13659));
		player.setNextSpotAnim(new SpotAnim(2571));

		player.getSkills().addXp(Constants.RUNECRAFTING, 1);
		player.getInventory().deleteItem(new Item(DungeonConstants.ESSENCE, cycleCount));
		for (int i = multipliers.length - 2; i >= 0; i -= 2)
			if (player.getSkills().getLevel(Constants.RUNECRAFTING) >= multipliers[i]) {
				cycleCount *= multipliers[i + 1];
				break;
			}

		player.getInventory().addItem(new Item(runeId, cycleCount));
		return 0;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
