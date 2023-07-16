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
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;

@PluginEventHandler
public class EquipAndRemoveItems  {

	public static ItemClickHandler handle = new ItemClickHandler(new String[] { "Wear", "Remove", "Wield" }, e -> {
		switch(e.getOption()) {
		case "Remove":
			Equipment.remove(e.getPlayer(), Equipment.getItemSlot(e.getItem().getId()));
			break;
		case "Wear":
		case "Wield":
			if (e.getPlayer().isEquipDisabled())
				return;
			Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId());
			break;
		}
	});

	public static ItemEquipHandler handleElementalGear = new ItemEquipHandler(new Object[] {
			9733, 9729, 18693, 20440, 20442,
			18699, 18697, 18695, 20444, 20446,
			20458, 20460, 20462, 20464, 20466,
			20448, 20450, 20452, 20456, 20454,
			2890, 9731, 18691, 20436, 20438 }, 
			e -> {
				if (e.equip()) {
					switch(e.getItem().getId()) {
					//helms
					case 9733 -> e.getPlayer().spotAnim(810, 0, 96);
					case 9729 -> e.getPlayer().spotAnim(811, 0, 96);
					case 18693 -> e.getPlayer().spotAnim(2686, 0, 96);
					//case 20440 -> e.getPlayer().spotAnim(cosmic, 0, 96);
					//case 20442 -> e.getPlayer().spotAnim(chaos, 0, 96);

					//bodies
					case 18699 -> e.getPlayer().spotAnim(2679, 0, 96);
					case 18697 -> e.getPlayer().spotAnim(2678, 0, 96);
					case 18695 -> e.getPlayer().spotAnim(2677, 0, 96);
					case 20444 -> e.getPlayer().spotAnim(1532, 0, 96);
					case 20446 -> e.getPlayer().spotAnim(1531, 0, 96);

					//gloves
					case 20458 -> e.getPlayer().spotAnim(699, 0, 96);
					case 20460 -> e.getPlayer().spotAnim(700, 0, 96);
					case 20462 -> e.getPlayer().spotAnim(65, 0, 96);
					case 20464 -> e.getPlayer().spotAnim(487, 0, 96);
					case 20466 -> e.getPlayer().spotAnim(66, 0, 96);

					//boots
					case 20448 -> e.getPlayer().spotAnim(63, 0, 96);
					case 20450 -> e.getPlayer().spotAnim(64, 0, 96);
					case 20452 -> e.getPlayer().spotAnim(59, 0, 96);
					case 20456 -> e.getPlayer().spotAnim(61, 0, 96);
					case 20454 -> e.getPlayer().spotAnim(62, 0, 96);

					//shields
					case 2890 -> e.getPlayer().spotAnim(244, 0, 96);
					case 9731 -> e.getPlayer().spotAnim(809, 0, 96);
					case 18691 -> e.getPlayer().spotAnim(2683, 0, 96);
					case 20436 -> e.getPlayer().spotAnim(2023, 0, 96);
					case 20438 -> e.getPlayer().spotAnim(2022, 0, 96);
					}
				}	
			});

	public static ItemEquipHandler handleBloodNecklace = new ItemEquipHandler(new Object[] {
		15834, 17291 },
			e -> {
				if(e.equip())
					e.getPlayer().addEffect(Effect.BLOOD_NECKLACE, Integer.MAX_VALUE);
				if(e.dequip())
					e.getPlayer().removeEffect(Effect.BLOOD_NECKLACE);
			}
	);
}
