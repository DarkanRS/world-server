package com.rs.game.player.content.skills.prayer;

import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnItemEvent;
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

	public static ItemOnItemHandler itemOnItem = new ItemOnItemHandler(new int[] { HOLY_BOOK, UNHOLY_BOOK, BOOK_OF_BALANCE }, new int[] { UNBLESSED_SYMBOL, UNPOWERED_SYMBOL }) {
		@Override
		public void handle(ItemOnItemEvent e) {
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
		}
	};
}
