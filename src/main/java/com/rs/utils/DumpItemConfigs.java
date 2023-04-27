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
package com.rs.utils;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.player.Equipment;
import com.rs.lib.game.Item;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class DumpItemConfigs {

	public static void main(String[] args) {
		//Cache.init();
		dump2h();
	}

	public static void dump2h() {
		try {
			File file = new File("2hs.txt");
			if (file.exists())
				file.delete();
			else
				file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.flush();
			for (int id = 0; id < 7956; id++) {
				if (Equipment.isTwoHandedWeapon(new Item(id, 1)))
					writer.append(id + ", ");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dumpEquipInfo() {
		try {
			File file = new File("itemEquipInfo.txt");
			if (file.exists())
				file.delete();
			else
				file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.flush();
			for (int id = 0; id < 7956; id++) {
				ItemDefinitions def = ItemDefinitions.getDefs(id);
				if (def.isWearItem())
					//writer.append(id + " " + def.getEquipType() + " " + def.getEquipSlot());
					writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean hasCombatRequirements(ItemDefinitions def) {
		if ((def.getWearingSkillRequiriments() == null) || def.getWearingSkillRequiriments().isEmpty())
			return false;
		for (int skillId : def.getWearingSkillRequiriments().keySet()) {
			if (skillId < 0 && skillId > 6)
				return false;
			int level = def.getWearingSkillRequiriments().get(skillId);
			if (level < 0 || level > 99)
				return false;
		}
		return true;
	}

	public static void dumpItemInfo() {
		try {
			File file = new File("wieldReqs.txt");
			if (file.exists())
				file.delete();
			else
				file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.flush();
			for (int id = 0; id < 7956; id++) {
				ItemDefinitions def = ItemDefinitions.getDefs(id);
				if (def.isWearItem())
					if (hasCombatRequirements(def)) {
						writer.append(id + " ");
						for (int skillId : def.getWearingSkillRequiriments().keySet()) {
							if (skillId < 0 && skillId > 6)
								continue;
							int level = def.getWearingSkillRequiriments().get(skillId);
							if (level < 0 || level > 120)
								continue;
							writer.append(skillId + "-" + level + " ");
						}
						writer.newLine();
					}
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
