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
import com.rs.game.content.dialogues_matrix.SimpleMessage;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public class GemTipCutting extends PlayerAction {

	public enum GemTips {
		OPAL(1609, 1.5, 11, 886, 45),

		JADE(1611, 2, 26, 886, 9187),

		RED_TOPAZ(1613, 3, 48, 887, 9188),

		SAPPHIRE(1607, 4, 56, 888, 9189),

		EMERALD(1605, 5.5, 58, 889, 9190),

		RUBY(1603, 6.5, 63, 887, 9191),

		DIAMOND(1601, 8, 65, 890, 9192),

		DRAGONSTONE(1615, 10, 71, 885, 9193),

		ONYX(6573, 25, 73, 2717, 9194);

		private double experience;
		private int levelRequired;
		private int cut;

		private int emote;
		private int boltTips;

		private GemTips(int cut, double experience, int levelRequired, int emote, int boltTips) {
			this.cut = cut;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.emote = emote;
			this.boltTips = boltTips;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public double getExperience() {
			return experience;
		}

		public int getCut() {
			return cut;
		}

		public int getEmote() {
			return emote;
		}

		public int getBoltTips() {
			return boltTips;
		}

	}

	public static void cut(Player player, GemTips gem) {
		player.getActionManager().setAction(new GemTipCutting(gem, player.getInventory().getNumberOf(gem.getCut())));
	}

	private GemTips gem;
	private int quantity;

	public GemTipCutting(GemTips gem, int quantity) {
		this.gem = gem;
		this.quantity = quantity;
	}

	public boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Constants.FLETCHING) < gem.getLevelRequired()) {
			player.getDialogueManager().execute(new SimpleMessage(), "You need a fletching level of " + gem.getLevelRequired() + " to cut that gem.");
			return false;
		}
		if (!player.getInventory().containsOneItem(gem.getCut())) {
			player.getDialogueManager().execute(new SimpleMessage(), "You don't have any " + ItemDefinitions.getDefs(gem.getCut()).getName().toLowerCase() + " to cut.");
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			setActionDelay(player, 2);
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
		player.getInventory().deleteItem(gem.getCut(), 1);
		player.getInventory().addItem(gem.getBoltTips(), 12);
		player.getSkills().addXp(Constants.FLETCHING, gem.getExperience());
		player.sendMessage("You cut the " + ItemDefinitions.getDefs(gem.getCut()).getName().toLowerCase() + ".", true);
		quantity--;
		if (quantity <= 0)
			return -1;
		player.setNextAnimation(new Animation(gem.getEmote()));
		return 2;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}
