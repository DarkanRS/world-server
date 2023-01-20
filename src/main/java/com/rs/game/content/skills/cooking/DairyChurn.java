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
package com.rs.game.content.skills.cooking;

import com.rs.game.content.skills.util.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DairyChurn  {
	private static Item[][] materials = { { new Item(1927) }, { new Item(1927) }, { new Item(1927) } };
	private static Item[][] products = { { new Item(2130), new Item(3727) }, { new Item(6697), new Item(3727) }, { new Item(1985), new Item(3727) } };
	private static int[] reqs = { 21, 38, 48 };
	private static double[] xp = { 18, 40.5, 64 };
	private static int[] anims = { -1, -1, -1 };

	public static ObjectClickHandler handleChurns = new ObjectClickHandler(new Object[] { "Dairy churn", "Dairy Churn" }, e -> e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), materials, products, xp, anims, reqs, Constants.COOKING, 8)));
}
