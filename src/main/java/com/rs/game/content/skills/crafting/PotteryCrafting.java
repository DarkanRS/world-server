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
package com.rs.game.content.skills.crafting;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.content.skills.crafting.urns.CreateUnfUrnD;
import com.rs.game.content.skills.crafting.urns.FireUrnD;
import com.rs.game.content.skills.util.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PotteryCrafting  {

	private static final Item[][] UNF_MATS = { { new Item(1761) }, { new Item(1761) }, { new Item(1761) }, { new Item(1761) }, { new Item(1761) }, { new Item(1761) } };
	private static final Item[][] UNF_PRODS = { { new Item(1787) }, { new Item(20052) }, { new Item(1789) }, { new Item(1791) }, { new Item(5352) }, { new Item(4438) } };
	private static final Item[][] FIRED_PRODS = { { new Item(1931) }, { new Item(20053) }, { new Item(2313) }, { new Item(1923) }, { new Item(5350) }, { new Item(4440) } };
	private static final int[] REQS = { 1, 4, 7, 8, 19, 25 };
	private static final double[] XP = { 6.25, 11.5, 15, 18, 20, 20 };

	public static ObjectClickHandler handleWheels = new ObjectClickHandler(new Object[] { "Potter's Wheel", "Potter's wheel", "Pottery Wheel" }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer())
				.addNext(new MakeXStatement(new int[] { 1931, 20347 }, new String[] { "Normal Pottery", "Skilling Urns" }),
						() -> e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), UNF_MATS, UNF_PRODS, XP, 896, REQS, Constants.CRAFTING, 2)),
						() -> e.getPlayer().startConversation(new CreateUnfUrnD(e.getPlayer()))));
	});

	public static ObjectClickHandler handleOvens = new ObjectClickHandler(new Object[] { "Pottery Oven", "Pottery oven" }, e -> {
		if (e.getPlayer().containsAnyItems(1787, 20052, 1789, 1791, 5352, 4438)) //TODO move these to generic firing dialogue
			e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), UNF_PRODS, FIRED_PRODS, XP, 899, REQS, Constants.CRAFTING, 4));
		else if (!e.getPlayer().startConversation(new FireUrnD(e.getPlayer())))
			e.getPlayer().sendMessage("You don't have anything to fire.");
	});
}
