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
import com.rs.tools.old.RsWikiEquipSlotsDumper.EquipSlot.SlotType;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class RsWikiEquipSlotsDumper {

	private static ArrayList<EquipSlot> slots = new ArrayList<>();

	private static boolean dumpEquipmentSlot(Item item) {
		if (item.getDefinitions() != null && !item.getDefinitions().isWearItem())
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
					// System.out.println(line.indexOf("\"><img alt=\""));
					line = line.substring(line.indexOf("title=") + "title=".length() + 1, line.indexOf("\"><img alt=\""));
					EquipSlot ep = getEquipSlot(item.getId(), line);
					if (ep != null)
						slots.add(ep);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static EquipSlot getEquipSlot(int id, String line) {
		return new EquipSlot(id, SlotType.valueOf(line.toUpperCase()));
	}

	public static ArrayList<String> getPage(Item item) {
		try {
			WebPage page = new WebPage("http://runescape.wikia.com/wiki/" + item.getName());
			try {
				page.load();
			} catch (Exception e) {
				System.out.println("Invalid page: " + item.getId() + ", " + item.getName());
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
		for (int i = 0; i < 75; i++) {
			Item item = new Item(i, 1);
			if (dumpEquipmentSlot(item))
				System.out.println("Dumped Item " + item.getName());
		}
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(new File("./slots.s")));
			for (EquipSlot slot : slots) {
				out.writeShort(slot.getId());
				out.write(slot.getType().toString().getBytes());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class EquipSlot {

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

		enum SlotType {
			WEAPON_SLOT, HEAD_SLOT, RING_SLOT, BODY_SLOT, LEGWEAR_SLOT, HANDS_SLOT, AURA_SLOT, FEET_SLOT, CAPE_SLOT, AMMUNITION_SLOT, NECK_SLOT, SLOT_SHIELD
		}
	}
}
