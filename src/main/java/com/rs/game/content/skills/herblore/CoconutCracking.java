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
package com.rs.game.content.skills.herblore;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class CoconutCracking  {

	public static final int HAMMER = 2347;
	public static final int COCONUT = 5974;
	public static final int OPEN_COCONUT = 5976;

	public static ItemOnItemHandler handle = new ItemOnItemHandler(HAMMER, COCONUT, e -> {
		e.getPlayer().getInventory().deleteItem(COCONUT, 1);
		e.getPlayer().getInventory().addItem(OPEN_COCONUT, 1);
		e.getPlayer().sendMessage("You break the coconut open with the hammer.");
	});
}
