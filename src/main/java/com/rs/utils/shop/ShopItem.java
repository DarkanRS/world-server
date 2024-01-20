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
package com.rs.utils.shop;

import com.rs.lib.game.Item;

public class ShopItem {
	private final Item item;
	private transient int ticks;
	private int capStock;
	private int customPrice;

    public ShopItem(int id, int amount) {
		item = new Item(id, amount);
	}

	public void init() {
		capStock = item.getAmount();
	}

	public Item getItem() {
		return item;
	}

	public int getCustomPrice() {
		return customPrice;
	}

	public int getRestockTicks() {
        Integer restockTicks = -1;
        return restockTicks == null ? 50 : restockTicks;
	}

	public void setCap(int cap) {
		capStock = cap;
	}

	public boolean tickRestock() {
		if (++ticks > getRestockTicks()) {
			ticks = 0;
			if (item.getAmount() > capStock) {
				item.setAmount(item.getAmount()-1);
				return true;
			}
			if (item.getAmount() < capStock) {
				item.setAmount(item.getAmount()+1);
				return true;
			}
		}
		return false;
	}
}
