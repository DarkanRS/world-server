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
package com.rs.game.content;

import com.rs.game.model.entity.player.Equipment;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;

@PluginEventHandler
public class EquipAndRemoveItems  {

	public static ItemClickHandler handle = new ItemClickHandler(new String[] { "Wear", "Remove", "Wield" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Remove":
				Equipment.remove(e.getPlayer(), Equipment.getItemSlot(e.getItem().getId()));
				break;
			case "Wear":
			case "Wield":
				if (e.getPlayer().isEquipDisabled() || !EnchantedHeadwear.canEquip(e.getItem().getId(), e.getPlayer()))
					return;
				Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId());
				break;
			}
		}
	};

	public static ItemEquipHandler handleElementalShields = new ItemEquipHandler(2890, 9731, 18691, 20436, 20438) {
		@Override
		public void handle(ItemEquipEvent e) {
			if (e.equip()) {
				switch(e.getItem().getId()) {
				case 2890 -> e.getPlayer().setNextSpotAnim(new SpotAnim(244, 0, 96));
				case 9731 -> e.getPlayer().setNextSpotAnim(new SpotAnim(809, 0, 96));
				case 18691 -> e.getPlayer().setNextSpotAnim(new SpotAnim(2683, 0, 96));
				case 20436 -> e.getPlayer().setNextSpotAnim(new SpotAnim(2023, 0, 96));
				case 20438 -> e.getPlayer().setNextSpotAnim(new SpotAnim(2022, 0, 96));
				}
			}
		}
	};

	public static ItemEquipHandler handleElementalHelmets = new ItemEquipHandler(9733) {
		@Override
		public void handle(ItemEquipEvent e) {
			if (e.equip()) {
				switch(e.getItem().getId()) {
					case 9733 -> e.getPlayer().setNextSpotAnim(new SpotAnim(810, 0, 96));
					//811 elemental helmet

				}
			}
		}
	};
}
