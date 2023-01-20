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
package com.rs.game.content.skills.magic;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class BoltEnchanting  {

	public enum Bolt {

		OPAL(14, 4, new RuneSet(Rune.COSMIC, 1, Rune.AIR, 2), 879, 9236, 9),
		SAPPHIRE(29, 7, new RuneSet(Rune.COSMIC, 1, Rune.WATER, 1, Rune.MIND, 1), 9337, 9240, 17),
		JADE(18, 14, new RuneSet(Rune.COSMIC, 1, Rune.EARTH, 2), 9335, 9237, 19),
		PEARL(22, 24, new RuneSet(Rune.COSMIC, 1, Rune.AIR, 2), 880, 9238, 29),
		EMERALD(32, 27, new RuneSet(Rune.COSMIC, 1, Rune.AIR, 3, Rune.NATURE, 1), 9338, 9241, 37),
		RED_TOPAZ(26, 29, new RuneSet(Rune.COSMIC, 1, Rune.FIRE, 2), 9336, 9239, 33),
		RUBY(35, 49, new RuneSet(Rune.COSMIC, 1, Rune.FIRE, 5, Rune.BLOOD, 1), 9339, 9242, 59),
		DIAMOND(38, 57, new RuneSet(Rune.COSMIC, 1, Rune.EARTH, 10, Rune.LAW, 2), 9340, 9243, 67),
		DRAGONSTONE(41, 68, new RuneSet(Rune.COSMIC, 1, Rune.EARTH, 15, Rune.SOUL, 1), 9341, 9244, 78),
		ONYX(44, 87, new RuneSet(Rune.COSMIC, 1, Rune.FIRE, 20, Rune.DEATH, 1), 9342, 9245, 97);

		private final int componentId;
		private final int levelRequired;
		private final RuneSet runes;
		private final int original;
		private final int product;
		private final double xp;

		Bolt(int componentId, int levelRequired, RuneSet runes, int original, int product, double xp) {
			this.componentId = componentId;
			this.levelRequired = levelRequired;
			this.runes = runes;
			this.original = original;
			this.product = product;
			this.xp = xp;
		}

		public static Bolt forId(int id) {
			for (Bolt bolt : Bolt.values())
				if (bolt.getComponentId() == id)
					return bolt;
			return null;
		}

		public int getComponentId() {
			return componentId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public RuneSet getRunes() {
			return runes;
		}

		public int getProduct() {
			return product;
		}

		public int getOriginal() {
			return original;
		}

		public double getXp() {
			return xp;
		}
	}

	static class BoltEnchantingAction extends PlayerAction {

		private Bolt bolt;
		private int amount;

		public BoltEnchantingAction(Bolt bolt, int amount) {
			this.bolt = bolt;
			this.amount = amount;
		}

		public boolean checkAll(Player player) {
			if (!Magic.checkRunes(player, false, bolt.getRunes()))
				return false;
			if (!player.getInventory().containsItem(bolt.getOriginal(), 10)) {
				player.sendMessage("You don't have 10 bolts of this type to enchant.");
				return false;
			}
			if (player.getSkills().getLevel(Constants.MAGIC) < bolt.getLevelRequired()) {
				player.sendMessage("You need a magic level of "+bolt.getLevelRequired()+" to enchant those.");
				return false;
			}
			if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("You don't have enough inventory space.");
				return false;
			}
			return true;
		}

		@Override
		public boolean start(Player player) {
			return checkAll(player);
		}

		@Override
		public boolean process(Player player) {
			return checkAll(player);
		}

		@Override
		public int processWithDelay(Player player) {
			if (!checkAll(player) || !Magic.checkRunes(player, true, bolt.getRunes()))
				return -1;
			amount--;
			player.getInventory().deleteItem(bolt.getOriginal(), 10);
			player.getInventory().addItem(bolt.getProduct(), 10);
			player.setNextAnimation(new Animation(4462));
			player.setNextSpotAnim(new SpotAnim(759));
			player.getSkills().addXp(Constants.MAGIC, bolt.getXp());
			if (amount <= 0)
				return -1;
			return 2;
		}

		@Override
		public void stop(Player player) {

		}

	}

	public static ButtonClickHandler handleInter = new ButtonClickHandler(432, e -> {
		Bolt bolt = Bolt.forId(e.getComponentId());
		if (bolt != null)
			switch(e.getPacket()) {
			case IF_OP1: //1
				e.getPlayer().stopAll();
				e.getPlayer().getActionManager().setAction(new BoltEnchantingAction(bolt, 1));
				break;
			case IF_OP2: //5
				e.getPlayer().stopAll();
				e.getPlayer().getActionManager().setAction(new BoltEnchantingAction(bolt, 5));
				break;
			case IF_OP3: //10
				e.getPlayer().stopAll();
				e.getPlayer().getActionManager().setAction(new BoltEnchantingAction(bolt, 10));
				break;
			default:
				break;
			}
	});

}
