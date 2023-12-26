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

import com.rs.game.content.items.liquid_containers.FillAction.Filler;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Empty {
    public static ItemClickHandler handleGenericEmpty = new ItemClickHandler(new String[] { "Empty" }, e -> {
        Filler fillable = Filler.forFull(e.getItem().getId());
        if (fillable == null)
            return;
        e.getItem().setId(fillable.getEmptyItem().getId());
        e.getPlayer().getInventory().refresh(e.getSlotId());
    });

    public static ItemClickHandler potEmpty = new ItemClickHandler(new Object[] { 1933, 7468, 7811, 14816}, new String[] { "Empty" }, e -> {
        e.getItem().setId(1931);
        e.getPlayer().getInventory().refresh(e.getItem().getSlot());
    });

    public static ItemClickHandler teaPotBrownEmpty = new ItemClickHandler(new Object[] { 7692, 7694, 7696, 7698, 7700 }, new String[] { "Empty" }, e -> {
        e.getItem().setId(7702);
        e.getPlayer().getInventory().refresh(e.getItem().getSlot());
    });

    public static ItemClickHandler teaPotWhiteEmpty = new ItemClickHandler(new Object[] { 7704, 7706, 7708, 7710, 7712 }, new String[] { "Empty" }, e -> {
        e.getItem().setId(7714);
        e.getPlayer().getInventory().refresh(e.getItem().getSlot());
    });

    public static ItemClickHandler teaPotGoldEmpty = new ItemClickHandler(new Object[] { 7716, 7718, 7720, 7722, 7724 }, new String[] { "Empty" }, e -> {
        e.getItem().setId(7726);
        e.getPlayer().getInventory().refresh(e.getItem().getSlot());
    });


}
