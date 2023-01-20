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
package com.rs.game.content.skills.prayer;

import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class EnchantSymbols {

	public static int HOLY_BOOK = 3840;
	public static int UNHOLY_BOOK = 3842;
	public static int BOOK_OF_BALANCE = 3844;
	public static int UNBLESSED_SYMBOL = 1716;
	public static int UNPOWERED_SYMBOL = 1722;
	public static int HOLY_SYMBOL = 1718;
	public static int UNHOLY_SYMBOL = 1724;

	public static ItemOnItemHandler itemOnItem = new ItemOnItemHandler(new int[] { HOLY_BOOK, UNHOLY_BOOK, BOOK_OF_BALANCE }, new int[] { UNBLESSED_SYMBOL, UNPOWERED_SYMBOL }, e -> {
		if (e.usedWith(HOLY_BOOK, UNBLESSED_SYMBOL)) {
			if (e.getPlayer().getSkills().getLevel(Constants.PRAYER) < 50) {
				e.getPlayer().sendMessage("You need at least level 50 prayer to enchant this.");
				return;
			}

			if (e.getPlayer().getPrayer().getPoints() < 20) {
				e.getPlayer().sendMessage("You do not have enough prayer points. Please recharge your prayer at the Lumbridge church.");
				return;
			}

			e.getPlayer().getInventory().deleteItem(UNBLESSED_SYMBOL, 1);
			e.getPlayer().getInventory().addItem(HOLY_SYMBOL, 1);
			e.getPlayer().setNextAnimation(new Animation(1335));
			e.getPlayer().getPrayer().drainPrayer(20);
			e.getPlayer().sendMessage("You bless the holy symbol.");
			return;
		}

		if (e.usedWith(BOOK_OF_BALANCE, UNBLESSED_SYMBOL)) {
			if (e.getPlayer().getSkills().getLevel(Constants.PRAYER) < 50) {
				e.getPlayer().sendMessage("You need at least level 50 prayer to enchant this.");
				return;
			}

			if (e.getPlayer().getPrayer().getPoints() < 20) {
				e.getPlayer().sendMessage("You do not have enough prayer points. Please recharge your prayer at the Lumbridge church.");
				return;
			}

			e.getPlayer().getInventory().deleteItem(UNBLESSED_SYMBOL, 1);
			e.getPlayer().getInventory().addItem(HOLY_SYMBOL, 1);
			e.getPlayer().setNextAnimation(new Animation(1337));
			e.getPlayer().getPrayer().drainPrayer(20);
			e.getPlayer().sendMessage("You bless the holy symbol.");
			return;
		}

		if (e.usedWith(UNHOLY_BOOK, UNPOWERED_SYMBOL)) {
			if (e.getPlayer().getSkills().getLevel(Constants.PRAYER) < 50) {
				e.getPlayer().sendMessage("You need at least level 50 prayer to enchant this.");
				return;
			}

			if (e.getPlayer().getPrayer().getPoints() < 20) {
				e.getPlayer().sendMessage("You do not have enough prayer points. Please recharge your prayer at the Lumbridge church.");
				return;
			}

			e.getPlayer().getInventory().deleteItem(UNPOWERED_SYMBOL, 1);
			e.getPlayer().getInventory().addItem(UNHOLY_SYMBOL, 1);
			e.getPlayer().setNextAnimation(new Animation(1336));
			e.getPlayer().getPrayer().drainPrayer(20);
			e.getPlayer().sendMessage("You bless the unholy symbol.");
			return;
		}

		if (e.usedWith(BOOK_OF_BALANCE, UNPOWERED_SYMBOL)) {
			if (e.getPlayer().getSkills().getLevel(Constants.PRAYER) < 50) {
				e.getPlayer().sendMessage("You need at least level 50 prayer to enchant this.");
				return;
			}

			if (e.getPlayer().getPrayer().getPoints() < 20) {
				e.getPlayer().sendMessage("You do not have enough prayer points. Please recharge your prayer at the Lumbridge church.");
				return;
			}

			e.getPlayer().getInventory().deleteItem(UNPOWERED_SYMBOL, 1);
			e.getPlayer().getInventory().addItem(UNHOLY_SYMBOL, 1);
			e.getPlayer().setNextAnimation(new Animation(1337));
			e.getPlayer().getPrayer().drainPrayer(20);
			e.getPlayer().sendMessage("You bless the unholy symbol.");
		}
	});
}
