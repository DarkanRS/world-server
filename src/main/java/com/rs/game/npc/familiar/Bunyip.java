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
package com.rs.game.npc.familiar;

import com.rs.game.player.Player;
import com.rs.game.player.content.skills.cooking.Foods.Food;
import com.rs.game.player.content.skills.fishing.Fish;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Bunyip extends Familiar {

	private int healTicks = 25;

	public Bunyip(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Swallow Whole";
	}

	@Override
	public String getSpecialDescription() {
		return "Eat an uncooked fish and gain the correct number of life points corresponding to the fish eaten if you have the cooking level to cook the fish.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (healTicks-- <= 0 && getOwner() != null) {
			getOwner().heal((int) (getOwner().getMaxHitpoints()*0.02));
			getOwner().setNextSpotAnim(new SpotAnim(1300));
			healTicks = 25;
		}
	}

	@Override
	public int getSpecialAmount() {
		return 7;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.ITEM;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Item item = getOwner().getInventory().getItem((Integer) object);
		if (item == null)
			return false;
		for (Fish fish : Fish.values()) {
			if (fish.getId() != item.getId()) {
				getOwner().sendMessage("Your bunyip cannot eat this.");
				return false;
			}
			if (getOwner().getSkills().getLevel(Constants.COOKING) < fish.getLevel()) {
				getOwner().sendMessage("Your cooking level is not high enough for the bunyip to eat this fish.");
				return false;
			} else {
				getOwner().setNextSpotAnim(new SpotAnim(1316));
				getOwner().setNextAnimation(new Animation(7660));
				getOwner().heal(Food.forId(item.getId()).getHeal());
				getOwner().getInventory().deleteItem(item.getId(), item.getAmount());
				return true;// stop here
			}
		}
		return true;
	}
}
