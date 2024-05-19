package com.rs.game.content.holidayevent_items;

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

import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Rubber_chicken {

    public static ItemClickHandler handle = new ItemClickHandler(new Object[] { 4566 }, new String[] { "Dance", "Operate" }, e -> {
        e.getPlayer().setNextAnimation(new Animation(1835));
        e.getPlayer().jingle(99);
        e.getPlayer().soundEffect( 355, 100,false);

        });
    };









