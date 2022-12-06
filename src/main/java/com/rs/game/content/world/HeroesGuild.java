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
package com.rs.game.content.world;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class HeroesGuild  {

	public static ItemOnObjectHandler handleFountainOfHeroes = new ItemOnObjectHandler(new Object[] { 36695 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (isGloryOrROW(e.getItem().getId()) && e.getItem().getName().toLowerCase().indexOf("(4)") < 0) {
				for (Item item : e.getPlayer().getInventory().getItems().array())
					if (item != null)
						if (isGloryOrROW(item.getId())) {
							int fullyChargedItemId = getChargedId(item.getId());
							if (fullyChargedItemId != -1) {
								e.getPlayer().getInventory().replace(item, new Item(fullyChargedItemId));
								e.getPlayer().setNextAnimation(new Animation(899));
							}
						}
				e.getPlayer().sendMessage("You dip it into the fountain restoring all charges.");
			}
		}
	};

	public static ObjectClickHandler handleLadders = new ObjectClickHandler(new Object[] { 67691, 67690 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getId() == 67691)
				e.getPlayer().useLadder(WorldTile.of(2906, 3516, 0));
			else if (e.getObject().getId() == 67690)
				e.getPlayer().useLadder(WorldTile.of(2893, 9907, 0));
		}
	};

	public static boolean isGloryOrROW(int itemId) {
		if ((itemId == 1704) || (itemId == 1706) || (itemId == 1708) || (itemId == 1710) || (itemId == 1712) || (itemId == 10354) || (itemId == 10356) || (itemId == 10358) || (itemId == 10360) || (itemId == 10362) || (itemId == 2572) || (itemId == 20653) || (itemId == 20655) || (itemId == 20657) || (itemId == 20659))
			return true;
		return false;
	}

	public static int getChargedId(int itemId) {
		//glory
		if ((itemId == 1704) || (itemId == 1706) || (itemId == 1708) || (itemId == 1710))
			return 1712;

		//trimmed glory
		if ((itemId == 10356) || (itemId == 10358) || (itemId == 10360) || (itemId == 10362))
			return 10354;

		//row
		if ((itemId == 2572) || (itemId == 20653) || (itemId == 20655) || (itemId == 20657))
			return 20659;

		return -1;
	}
}
