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
package com.rs.game.content.items.liquid_containers;

import com.rs.game.content.world.unorganized_dialogue.FillingD;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class FillAction extends PlayerAction {

	private int amount;
	private Animation FILLING = new Animation(883);
	private Filler fill;

	public static ItemOnObjectHandler handleFilling = new ItemOnObjectHandler(new Object[] { "Waterpump", "Water pump", "Fountain", "Sink", "Well", "Pump" }, e -> {
		Filler fill = FillAction.isFillable(e.getItem());
		if (fill != null)
			e.getPlayer().startConversation(new FillingD(e.getPlayer(), fill));
	});

	public enum Filler {
		//Empty Item, Full Item
		VIAL(new Item(229, 1), new Item(227, 1)),
		CLAY(new Item(434, 1), new Item(1761, 1)),
		BOWL(new Item(1923, 1), new Item(1921, 1)),
		BUCKET(new Item(1925, 1), new Item(1929, 1)),
		VASE(new Item(3734, 1), new Item(3735, 1)),
		JUJU_VIAL(new Item(19996, 1), new Item(19994, 1)),
		JUG(new Item(1935, 1), new Item(1937, 1)),
		WATERING_CAN(new Item(5331, 1), new Item(5340, 1)),
		WATERING_CAN1(new Item(5333, 1), new Item(5340, 1)),
		WATERING_CAN2(new Item(5334, 1), new Item(5340, 1)),
		WATERING_CAN3(new Item(5335, 1), new Item(5340, 1)),
		WATERING_CAN4(new Item(5336, 1), new Item(5340, 1)),
		WATERING_CAN5(new Item(5337, 1), new Item(5340, 1)),
		WATERING_CAN6(new Item(5338, 1), new Item(5340, 1)),
		WATERING_CAN7(new Item(5339, 1), new Item(5340, 1)),
		KETTLE(new Item(7688, 1), new Item(7690, 1));

		private static final Map<Integer, Filler> EMPTY = new HashMap<>();
		private static final Map<Integer, Filler> FULL = new HashMap<>();

		public static Filler forEmpty(int itemId) {
			return EMPTY.get(itemId);
		}
		
		public static Filler forFull(int itemId) {
			return FULL.get(itemId);
		}

		static {
			for (Filler ingredient : Filler.values()) {
				EMPTY.put(ingredient.getEmptyItem().getId(), ingredient);
				FULL.put(ingredient.getFilledItem().getId(), ingredient);
			}
		}

		private Item empty;
		private Item filled;

		private Filler(Item empty, Item filled) {
			this.empty = empty;
			this.filled = filled;
		}

		public Item getEmptyItem() {
			return empty;
		}

		public Item getFilledItem() {
			return filled;
		}
	}

	public static Filler isFillable(Item item) {
		return Filler.forEmpty((short) item.getId());
	}

	public FillAction(int amount, Filler fil) {
		this.amount = amount;
		this.fill = fil;
	}

	@Override
	public boolean start(Player player) {
		player.sendMessage("You start filling!", true);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().containsItem(fill.getEmptyItem().getId(), 1))
			return false;
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		amount--;
		player.setNextAnimation(FILLING);
		player.getInventory().deleteItem(fill.getEmptyItem().getId(), 1);
		player.getInventory().addItem(fill.getFilledItem().getId(), 1);
		if (amount > 0)
			return 1;
		return -1;
	}

	@Override
	public void stop(Player player) {

	}
}
