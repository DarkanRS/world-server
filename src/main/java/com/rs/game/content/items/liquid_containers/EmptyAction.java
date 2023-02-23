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

import java.util.HashMap;
import java.util.Map;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class EmptyAction extends PlayerAction {

    private static Container empty;

    public static ItemClickHandler handleGenericEmpty = new ItemClickHandler(new String[] { "Empty" }, e -> {
        if(e.getOption().equalsIgnoreCase("Empty")) {
            Container empty = EmptyAction.Emptyable(e.getItem());
            if (empty != null)
                new EmptyAction(e.getPlayer(), empty);
        }
    });

    public enum Container {
        //Empty Item, Full Item
        WATER_VIAL(new Item(229, 1), new Item(227, 1)),
        BOWL_OF_WATER(new Item(1923, 1), new Item(1921, 1)),
        BUCKET_OF_WATER(new Item(1925, 1), new Item(1929, 1)),
        JUJU_VIAL(new Item(19996, 1), new Item(19994, 1)),
        JUG_OF_WATER(new Item(1935, 1), new Item(1937, 1)),
        KETTLE(new Item(7688, 1), new Item(7690, 1)),
        NETTLE_WATER(new Item(1923, 1), new Item(4237, 1)),
        NETTLE_TEA_BOWL(new Item(1923, 1), new Item(4239, 1)),
        NETTLE_TEA_BOWL_MILKY(new Item(1923, 1), new Item(4240, 1)),
        NETTLE_TEA_CUP(new Item(4244, 1), new Item(4245, 1)),
        NETTLE_TEA_CUP_MILKY(new Item(4244, 1), new Item(4246, 1)),
        MILK_BUCKET(new Item(1925, 1), new Item(1927, 1));

        private static final Map<Integer, Container> EMPTY = new HashMap<>();
        private static final Map<Integer, Container> FULL = new HashMap<>();

        public static Container forEmpty(int itemId) {
            return EMPTY.get(itemId);
        }

        public static Container forFull(int itemId) {
            return FULL.get(itemId);
        }

        static {
            for (Container ingredient : Container.values()) {
                EMPTY.put(ingredient.getEmptyItem().getId(), ingredient);
                FULL.put(ingredient.getFilledItem().getId(), ingredient);
            }
        }

        private Item empty;
        private Item filled;

        private Container(Item empty, Item filled) {
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

    public static Container Emptyable(Item item) {
        return Container.forFull((short) item.getId());
    }

    public EmptyAction(Player player, Container empty) {
        player.getInventory().deleteItem(empty.getFilledItem().getId(), 1);
        player.getInventory().addItem(empty.getEmptyItem().getId(), 1);
    }


    @Override
    public boolean start(Player entity) {
        return false;
    }

    @Override
    public boolean process(Player player) {
        if (!player.getInventory().containsItem(empty.getFilledItem().getId(), 1))
            return false;
        return true;
    }

    @Override
    public int processWithDelay(Player entity) {
        return 0;
    }

    @Override
    public void stop(Player player) {

    }
}
