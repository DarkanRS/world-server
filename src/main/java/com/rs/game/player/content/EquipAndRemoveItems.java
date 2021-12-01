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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content;

import com.rs.game.player.Equipment;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class EquipAndRemoveItems  {
	
	public static ItemClickHandler handle = new ItemClickHandler(new String[] { "Wear", "Remove", "Wield" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Remove":
				Equipment.sendRemove(e.getPlayer(), Equipment.getItemSlot(e.getItem().getId()));
				break;
			case "Wear":
			case "Wield":
				if (e.getPlayer().isEquipDisabled())
					return;
				if (!EnchantedHeadwear.canEquip(e.getItem().getId(), e.getPlayer()))
					return;
				Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId());
				break;
			}
		}
	};

}
