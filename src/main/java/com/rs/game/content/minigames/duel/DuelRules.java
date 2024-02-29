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
package com.rs.game.content.minigames.duel;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;

public class DuelRules {

	private final Player player;
    private final Player target;
	public boolean[] duelRules = new boolean[26];
	private final ItemsContainer<Item> stake;

	public DuelRules(Player player, Player target) {
		this.player = player;
		this.target = target;
		stake = new ItemsContainer<>(28, false);
	}

	public boolean canAccept(ItemsContainer<Item> stake) {
		if (getRule(0) && getRule(1) && getRule(2)) {
			player.sendMessage("You have to be able to use atleast one combat style in a duel.", true);
			return false;
		}
		int count = 0;
		Item item;
		for (int i = 10; i < 24; i++) {
			int slot = i - 10;
			if (getRule(i) && (item = player.getEquipment().getItem(slot)) != null)
				if (i == 23) {// arrows
					if (!(item.getDefinitions().isStackable() && player.getInventory().getItems().containsOne(item)))
						count++;
				} else
					count++;
		}
		int freeSlots = player.getInventory().getItems().freeSlots() - count;
		if (freeSlots < 0) {
			player.sendMessage("You do not have enough inventory space to remove all the equipment.");
			getTarget().sendMessage("Your opponent does not have enough space to remove all the equipment.");
			return false;
		}
		for (int i = 0; i < stake.getSize(); i++)
			if (stake.get(i) != null)
				freeSlots--;
		if (freeSlots < 0) {
			player.sendMessage("You do not have enough room in your inventory for this stake.");
			getTarget().sendMessage("Your opponent does not have enough room in his inventory for this stake.");
			return false;
		}
		return true;
	}

	public void setRules(int ruleId) {
		setRules(ruleId, true);
	}

	public void setRules(int ruleId, boolean updated) {
		if (!getRule(ruleId))
			setRule(ruleId, true);
		else if (getRule(ruleId))
			setRule(ruleId, false);
		if (updated) {
			DuelRules rules = getTarget().getLastDuelRules();
			if (rules == null)
				return;
			rules.setRules(ruleId, false);
		}
		setConfigs();
	}

	public void setConfigs() {
		int value = 0;
		int ruleId = 16;
		for (int i = 0; i < duelRules.length; i++) {
			if (getRule(i)) {
				if (i == 7) // forfiet
					value += 5;
				if (i == 25) // no movement
					value += 6;
				value += ruleId;
			}
			ruleId += ruleId;
		}
		player.getVars().setVar(286, value);
	}

	public boolean setRule(int ruleId, boolean value) {
		return duelRules[ruleId] = value;
	}

	public boolean getRule(int ruleId) {
		return duelRules[ruleId];
	}

	public ItemsContainer<Item> getStake() {
		return stake;
	}

	public Player getTarget() {
		return target;
	}
}
