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
package com.rs.game.content.skills.crafting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.world.unorganized_dialogue.GemCuttingD;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.net.decoders.handlers.InventoryOptionsHandler;

public class GemCutting extends PlayerAction {

	public enum Gem {
		OPAL(1625, 1609, 15.0, 1, 886),

		JADE(1627, 1611, 20, 13, 886),

		RED_TOPAZ(1629, 1613, 25, 16, 887),

		SAPPHIRE(1623, 1607, 50, 20, 888),

		EMERALD(1621, 1605, 67, 27, 889),

		RUBY(1619, 1603, 85, 34, 887),

		DIAMOND(1617, 1601, 107.5, 43, 890),

		DRAGONSTONE(1631, 1615, 137.5, 55, 885),

		ONYX(6571, 6573, 167.5, 67, 2717),

		GRAY_SHELL_ROUND(3345, 3327, 35.5, 15, -1),

		RED_AND_BLACK_SHELL_ROUND(3347, 3329, 35.5, 15, -1),

		OCHRE_SHELL_ROUND(3349, 3331, 35.5, 15, -1),

		BLUE_SHELL_ROUND(3351, 3333, 35.5, 15, -1),

		BROKEN_SHELL_ROUND(3353, 3335, 35.5, 15, -1),

		GRAY_SHELL_POINTY(3355, 3337, 35.5, 15, -1),

		RED_AND_BLACK_SHELL_POINTY(3357, 3339, 35.5, 15, -1),

		OCHRE_SHELL_POINTY(3359, 3341, 35.5, 15, -1),

		BLUE_SHELL_POINTY(3361, 3343, 35.5, 15, -1),

		DRAMEN_STAFF(771, 772, 2.0, 31, -1);

		private double experience;
		private int levelRequired;
		private int uncut, cut;

		private int emote;

		private Gem(int uncut, int cut, double experience, int levelRequired, int emote) {
			this.uncut = uncut;
			this.cut = cut;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.emote = emote;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public double getExperience() {
			return experience;
		}

		public int getUncut() {
			return uncut;
		}

		public int getCut() {
			return cut;
		}

		public int getEmote() {
			return emote;
		}

	}

	public static boolean isCutting(Player player, Item item1, Item item2) {
		Item gem = InventoryOptionsHandler.contains(1755, item1, item2);
		if (gem == null)
			return false;
		return isCutting(player, gem.getId());
	}

	private static boolean isCutting(Player player, int gemId) {
		for (Gem gem : Gem.values())
			if (gem.uncut == gemId) {
				cut(player, gem);
				return true;
			}
		return false;
	}

	public static void cut(Player player, Gem gem) {
		if (player.getInventory().getItems().getNumberOf(new Item(gem.getUncut(), 1)) <= 1)
			player.getActionManager().setAction(new GemCutting(gem, 1));
		else
			player.startConversation(new GemCuttingD(player, gem));
	}

	private Gem gem;
	private int quantity;

	public GemCutting(Gem gem, int quantity) {
		this.gem = gem;
		this.quantity = quantity;
	}

	public boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Constants.CRAFTING) < gem.getLevelRequired()) {
			player.simpleDialogue("You need a crafting level of " + gem.getLevelRequired() + " to cut that gem.");
			return false;
		}
		if (!player.getInventory().containsOneItem(gem.getUncut())) {
			player.simpleDialogue("You don't have any " + ItemDefinitions.getDefs(gem.getUncut()).getName().toLowerCase() + " to cut.");
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			setActionDelay(player, 1);
			player.setNextAnimation(new Animation(gem.getEmote()));
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().deleteItem(gem.getUncut(), 1);
		player.getInventory().addItem(gem.getCut(), 1);
		player.getSkills().addXp(Constants.CRAFTING, gem.getExperience());
		player.sendMessage("You cut the " + ItemDefinitions.getDefs(gem.getUncut()).getName().toLowerCase() + ".", true);
		quantity--;
		if (quantity <= 0)
			return -1;
		player.setNextAnimation(new Animation(gem.getEmote()));
		return 0;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}
