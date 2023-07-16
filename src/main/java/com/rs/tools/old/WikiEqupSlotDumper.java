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
package com.rs.tools.old;

import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.tools.old.WikiEqupSlotDumper.EquipSlot.SlotType;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class WikiEqupSlotDumper {

	private static ArrayList<EquipSlot> slots = new ArrayList<>();

	private static boolean dumpEquipmentSlot(Item item) {
		if (!item.getDefinitions().isWearItem() || item.getDefinitions().isNoted())
			return false;
		ArrayList<String> lines = getPage(item);
		if (lines == null)
			return false;
		Iterator<String> iterator = lines.iterator();
		try {
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (line.startsWith("</th><th rowspan=\"3\" colspan=\"2\" width=\"30\" align=\"center\">")) {
					line = iterator.next();
					line = line.substring(line.indexOf("title=") + "title=".length() + 1, line.indexOf("\"><img alt=\""));
					EquipSlot ep = getEquipSlot(item, line);
					if (ep != null)
						slots.add(ep);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static EquipSlot getEquipSlot(Item item, String line) {
		int id = item.getId();
		if (line.equals("Neck slot"))
			return new EquipSlot(id, SlotType.NECK_SLOT);
		if (line.equals("Weapon slot"))
			return new EquipSlot(id, SlotType.WEAPON_SLOT);
		if (line.equals("Body slot"))
			return new EquipSlot(id, SlotType.BODY_SLOT);
		else if (line.equals("Feet slot"))
			return new EquipSlot(id, SlotType.FEET_SLOT);
		else if (line.equals("Ammunition slot"))
			return new EquipSlot(id, SlotType.AMMUNITION_SLOT);
		else if (line.equals("Legwear slot"))
			return new EquipSlot(id, SlotType.LEGWEAR_SLOT);
		else if (line.equals("Head slot"))
			/*
			 * if (Equipment.isFullHat(item)) new EquipSlot(id,
			 * SlotType.FULL_HELMET); else if (Equipment.isFullMask(item))
			 * return new EquipSlot(id, SlotType.FULL_MASK);
			 */
			return new EquipSlot(id, SlotType.HEAD_SLOT);
		else if (line.equals("Shield slot"))
			return new EquipSlot(id, SlotType.SHIELD_SLOT);
		else if (line.equals("Two-handed slot"))
			return new EquipSlot(id, SlotType.TWO_HANDED);
		else if (line.equals("Ring slot"))
			return new EquipSlot(id, SlotType.RING_SLOT);
		else if (line.equals("Hands slot"))
			return new EquipSlot(id, SlotType.HANDS_SLOT);
		else if (line.equals("Cape slot"))
			return new EquipSlot(id, SlotType.CAPE_SLOT);
		else if (line.equals("Aura slot"))
			return new EquipSlot(id, SlotType.AURA_SLOT);
		else
			System.err.println("Unhandled Slot: " + line);
		return new EquipSlot(id, SlotType.valueOf(line.toUpperCase().replace("_", " ")));
	}

	public static ArrayList<String> getPage(Item item) {
		try {
			String pageName = item.getDefinitions().getName().replace(" (black)", "").replace(" (white)", "").replace(" (yellow)", "").replace(" (red)", "");
			if (pageName == null || pageName.equals("null"))
				return null;
			pageName = pageName.replace(" (p)", "");
			pageName = pageName.replace(" (p+)", "");
			pageName = pageName.replace(" (p++)", "");
			pageName = pageName.replace(" Broken", "");
			pageName = pageName.replace(" 25", "");
			pageName = pageName.replace(" 50", "");
			pageName = pageName.replace(" 75", "");
			pageName = pageName.replace(" 100", "");
			pageName = pageName.replaceAll(" ", "_");
			WebPage page = new WebPage("http://runescape.wikia.com/wiki/" + pageName);
			try {
				page.load();
			} catch (Exception e) {
				System.out.println("Invalid page: " + item.getId() + ", " + pageName);
				return null;
			}
			return page.getLines();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		//Cache.init();
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			Item item = new Item(i, 1);
			dumpEquipmentSlot(item);
		}
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream("./slots.s/"));
			for (EquipSlot slot : slots) {
				byte[] bytes = slot.getType().toString().getBytes();
				out.writeShort(slot.getId());
				out.writeByte(bytes.length);
				out.write(bytes);
			}
			out.close();
			System.out.println("Packed Defintions.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class EquipSlot {

		private int id;
		private SlotType type;

		public EquipSlot(int id, SlotType type) {
			this.id = id;
			this.type = type;
		}

		public int getId() {
			return id;
		}

		public SlotType getType() {
			return type;
		}

		public enum SlotType {
			WEAPON_SLOT, HEAD_SLOT, RING_SLOT, BODY_SLOT, LEGWEAR_SLOT, HANDS_SLOT, AURA_SLOT, FEET_SLOT, CAPE_SLOT, AMMUNITION_SLOT, NECK_SLOT, SHIELD_SLOT, FULL_HELMET, FULL_MASK, TWO_HANDED
		}
	}

}
