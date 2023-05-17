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
package com.rs.plugin.events;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class ItemAddedToInventoryEvent implements PluginEvent {

    private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

    private Player player;
    private Item item;

    public ItemAddedToInventoryEvent(Player player, Item item) {
        this.player = player;
        this.item = item;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    public void cancel() {
        item.setId(-1);
    }

    @Override
    public PluginHandler<? extends PluginEvent> getMethod() {
        PluginHandler<? extends PluginEvent> method = HANDLERS.get(item.getId());
        if (method == null)
            method = HANDLERS.get(item.getDefinitions().getName());
        if (method == null)
            return null;
        return method;
    }

    public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
        for (Object key : method.keys()) {
            PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, method);
            if (old != null)
                System.err.println("ERROR: Duplicate ItemAddedToInventory methods for key: " + key + ":");
        }
    }
}
