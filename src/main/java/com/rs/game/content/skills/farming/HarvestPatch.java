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
package com.rs.game.content.skills.farming;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.Effect;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.entity.player.managers.AuraManager.Aura;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class HarvestPatch extends PlayerAction {

	private FarmPatch patch;
	private int tool = -1;
	private Animation animation;

	public HarvestPatch(FarmPatch patch) {
		this.patch = patch;
		switch(patch.seed.type) {
		case TREE:
			animation = FarmPatch.PRUNING_ANIMATION;
			break;
		case ALLOTMENT:
		case HOP:
		case EVIL_TURNIP:
			tool = 952;
			animation = FarmPatch.SPADE_ANIMATION;
			break;
		case FLOWER:
		case VINE_FLOWER:
		case BELLADONNA:
			tool = 952;
			animation = FarmPatch.FLOWER_PICKING_ANIMATION;
			break;
		case HERB:
		case MUSHROOM:
		case VINE_HERB:
			tool = 952;
			animation = FarmPatch.HERB_PICKING_ANIMATION;
			break;
		case CALQUAT:
		case FRUIT_TREE:
			animation = FarmPatch.FRUIT_PICKING_ANIMATION;
			break;
		case BUSH:
		case VINE_BUSH:
		case CACTUS:
			animation = FarmPatch.BUSH_PICKING_ANIMATION;
			break;
		case COMPOST:
			animation = FarmPatch.FILL_COMPOST_ANIMATION;
			break;
		default:
			tool = -1;
			break;
		}
	}

	@Override
	public boolean start(Player player) {
		if (patch.lives <= 0)
			return false;
		if (tool != -1 && !player.getInventory().containsItem(tool)) {
			player.sendMessage("You need " + Utils.addArticle(ItemDefinitions.getDefs(tool).name.toLowerCase()) + " to harvest this patch.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (patch.lives > 0)
			return true;
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(animation);
		if (patch.seed.decLife(player))
			patch.lives--;
		if (patch.seed == ProduceType.Willow)
			player.getInventory().addItemDrop(5933, 1);
		else {
			int amount = patch.seed == ProduceType.Limpwurt ? 3 : 1;
			if (player.getAuraManager().isActivated(Aura.GREENFINGERS))
				amount += Math.random() < 0.03 ? 1 : 0;
			if (player.getAuraManager().isActivated(Aura.GREATER_GREENFINGERS))
				amount += Math.random() < 0.05 ? 1 : 0;
			if (player.getAuraManager().isActivated(Aura.MASTER_GREENFINGERS))
				amount += Math.random() < 0.07 ? 1 : 0;
			if (player.getAuraManager().isActivated(Aura.SUPREME_GREENFINGERS))
				amount += Math.random() < 0.1 ? 1 : 0;
			if (player.hasEffect(Effect.JUJU_FARMING) && (patch.seed.type == PatchType.HERB || patch.seed.type == PatchType.VINE_HERB))
				if (Utils.random(3) == 0)
					amount++;
			player.incrementCount(patch.seed.productId.getName() + " harvested", amount);
			player.getInventory().addItemDrop(patch.seed.productId.getId(), amount);
		}
		switch (patch.seed.type) {
		case CALQUAT:
		case FRUIT_TREE:
		case BUSH:
		case VINE_BUSH:
		case CACTUS:
		case TREE:
			player.getSkills().addXp(Constants.FARMING, patch.seed.plantingExperience * 0.375);
			patch.updateVars(player);
			if (patch.lives <= 0)
				return -1;
			break;
		default:
			player.getSkills().addXp(Constants.FARMING, patch.seed.experience);
			if (patch.lives <= 0) {
				patch.empty();
				patch.updateVars(player);
				return -1;
			}
			break;
		}
		return 1;
	}

	@Override
	public void stop(Player player) {
	}

}
