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
}
