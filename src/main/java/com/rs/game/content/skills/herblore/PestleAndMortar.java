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

import com.rs.game.content.skills.util.CreateActionD;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class PestleAndMortar  {

	public enum PestleMortar {
		ANCHOVIES(319, 1, new Item(11266, 1)),
		UNICORN_HORN(237, 1, new Item(235, 1)),
		CHOCOLATE_BAR(1973, 1, new Item(1975, 1)),
		KEBBIT_TEETH(10109, 1, new Item(10111, 1)),
		GORAK_CLAW(9016, 1, new Item(9018, 1)),
		BIRDS_NEST(5075, 1, new Item(6693, 1)),
		DESERT_GOAT_HORN(9735, 1, new Item(9736, 1)),
		BLUE_DRAGON_SCALES(243, 1, new Item(241, 1)),
		CHARCOAL(973, 1, new Item(704, 1)),
		RUNE_SHARDS(6466, 1, new Item(6467, 1)),
		ASHES(592, 1, new Item(8865, 1)),
		RAW_KARAMBWAN(3142, 1, new Item(3152, 1)),
		KARAMBWAN(3144, 1, new Item(3154, 1)),
		POISON_KARAMBWAN(3146, 1, new Item(3153, 1)),
		SUQAH_TOOTH(9079, 1, new Item(9082, 1)),
		FISHING_BAIT(313, 1, new Item(12129, 1)),
		DIAMOND_ROOT(14703, 1, new Item(14704, 1)),
		BLACK_MUSHROOM(4620, 1, new Item(4622, 1)),
		MUD_RUNES(4698, 1, new Item(9594, 1)),
		SHARD_OF_ARMADYL(21776, 1, new Item(21774, 8));

		private static Map<Short, PestleMortar> rawIngredients = new HashMap<>();

		public static PestleMortar forId(int itemId) {
			return rawIngredients.get((short) itemId);
		}

		static {
			for (PestleMortar rawIngredient : PestleMortar.values())
				rawIngredients.put(rawIngredient.rawId, rawIngredient);
		}

		private final short rawId;
		private final short rawQty;
		private final Item crushedItem;

		private PestleMortar(int rawId, int rawQty, Item crushedItem) {
			this.rawId = (short) rawId;
			this.rawQty = (short) rawQty;
			this.crushedItem = crushedItem;
		}

		public short getRawId() {
			return rawId;
		}

		public Item getCrushedItem() {
			return crushedItem;
		}
	}

	public static ItemOnItemHandler handle = new ItemOnItemHandler(233, e -> {
		PestleMortar p = PestleMortar.forId(e.getItem1().getId());
		if (p == null)
			p = PestleMortar.forId(e.getItem2().getId());
		if (p == null)
			return;
		e.getPlayer().startConversation(new CreateActionD(e.getPlayer(), new Item[][] {{new Item(p.getRawId(), p.rawQty)}}, new Item[][] {{p.getCrushedItem()}}, new double[] {0}, new int[] {364}, Constants.HERBLORE, 0));
	});

}