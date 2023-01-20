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
package com.rs.game.content.skills.smithing;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IComponentDefinitions;
import com.rs.game.content.skills.smithing.Smithing.Smithable;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class ForgingInterface {

	private static final int SMITHING_INTERFACE = 300;
	
	public enum Slot {
		DAGGER(18),
		HATCHET(26),
		MACE(34),
		MEDIUM_HELM(42),
		CROSSBOW_BOLTS(50),
		SWORD(58),
		DART_TIPS(66),
		NAILS(74),
		BRONZE_WIRE(82),
		SPIT_IRON(90),
		STUDS(98),
		ARROW_TIPS(106),
		SCIMITAR(114),
		CROSSBOW_LIMBS(122),
		LONGSWORD(130),
		THROWING_KNIFE(138),
		FULL_HELM(146),
		SQUARE_SHIELD(154),
		BULLSEYE_LANTERN(162),
		GRAPPLE_TIP(170),
		WARHAMMER(178),
		BATTLEAXE(186),
		CHAINBODY(194),
		KITESHIELD(202),
		CLAWS(210),
		TWO_HAND_SWORD(218),
		PLATESKIRT(226),
		PLATELEGS(234),
		PLATEBODY(242),
		PICKAXE(267);
		
		private static final Map<Integer, Slot> CLICK_ID_MAP = new HashMap<>();
		
		static {
			for (Slot s : Slot.values()) {
				CLICK_ID_MAP.put(s.componentId+3, s); //make all
				CLICK_ID_MAP.put(s.componentId+4, s); //make x
				CLICK_ID_MAP.put(s.componentId+5, s); //make 5
				CLICK_ID_MAP.put(s.componentId+6, s); //make 1
			}
		}
		
		public static Slot forId(int componentId) {
			return CLICK_ID_MAP.get(componentId);
		}
		
		private int componentId;
		
		Slot(int componentId) {
			this.componentId = componentId;
		}
	}

	public static ButtonClickHandler handleSmithButtons = new ButtonClickHandler(SMITHING_INTERFACE, e -> {
		int barId = e.getPlayer().getTempAttribs().getI("SmithingBar");
		Slot slot = Slot.forId(e.getComponentId());
		if (slot == null)
			return;
		Map<Slot, Smithable> items = Smithable.forBar(barId);
		if (items == null || items.get(slot) == null)
			return;
		int makeX = switch(e.getComponentId() - slot.componentId) {
		case 3 -> 28;
		case 4 -> -1;
		case 5 -> 5;
		case 6 -> 1;
		default -> 1;
		};
		if (makeX < 0)
			e.getPlayer().sendInputInteger("How many would you like to make?", amount -> e.getPlayer().getActionManager().setAction(new Smithing(amount, items.get(slot))));
		else
			e.getPlayer().getActionManager().setAction(new Smithing(makeX, items.get(slot)));
	});

	public static String[] getStrings(Player player, Smithable item) {
		StringBuilder barName = new StringBuilder();
		StringBuilder levelString = new StringBuilder();
		String name = item.product.getDefinitions().getName().toLowerCase();
		String barVariableName = item.toString().toLowerCase();
		if (player.getInventory().getItems().getNumberOf(item.bar.getId()) >= item.bar.getAmount())
			barName.append("<col=00FF00>");
		barName.append(item.bar.getAmount()).append(" ").append(item.bar.getAmount() > 1 ? "bars" : "bar");
		if (player.getSkills().getLevel(Constants.SMITHING) >= item.level)
			levelString.append("<col=FFFFFF>");
		levelString.append(Utils.formatPlayerNameForDisplay(name.replace(barVariableName + " ", "")));
		return new String[] { levelString.toString(), barName.toString() };
	}
	
	public static void openSmithingInterfaceForHighestBar(Player player) {
		int bar = Smithable.getHighestBar(player);
		if (bar != -1)
			sendSmithingInterface(player, bar);
		else
			player.sendMessage("You have no bars which you have smithing level to use.");
	}

	public static void sendSmithingInterface(Player player, int barId) {
		player.getTempAttribs().setI("SmithingBar", barId);
		Map<Slot, Smithable> items = Smithable.forBar(barId);
		for (Slot slot : Slot.values()) {
			Smithable item = items.get(slot);
			if (item == null && !IComponentDefinitions.getInterface(300)[slot.componentId].hidden) {
				player.getPackets().setIFHidden(SMITHING_INTERFACE, slot.componentId-1, true);
				continue;
			}
			if (IComponentDefinitions.getInterface(300)[item.slot.componentId-1].hidden)
				player.getPackets().setIFHidden(SMITHING_INTERFACE, item.slot.componentId-1, false);
			player.getPackets().setIFItem(SMITHING_INTERFACE, item.slot.componentId, item.product.getId(), item.product.getAmount());
			String[] name = getStrings(player, item);
			if (name != null) {
				player.getPackets().setIFText(300, item.slot.componentId + 1, name[0]);
				player.getPackets().setIFText(300, item.slot.componentId + 2, name[1]);
			}
		}
		player.getPackets().setIFText(300, 14, ItemDefinitions.getDefs(barId).name.replace(" bar", ""));
		player.getInterfaceManager().sendInterface(SMITHING_INTERFACE);
	}
}
