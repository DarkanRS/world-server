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
package com.rs.game.content.skills.prayer;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;

public class Burying {

	public enum Bone {
		NORMAL(526, 4.5),
		BURNT(528, 4.5),
		WOLF(2859, 4.5),
		MONKEY(3183, 4.5),
		BAT(530, 5.2),
		BIG(532, 15),
		JOGRE(3125, 15),
		ZOGRE(4812, 22.5),
		SHAIKAHAN(3123, 25),
		BABY(534, 30),
		WYVERN(6812, 50),
		DRAGON(536, 72),
		FAYRG(4830, 84),
		RAURG(4832, 96),
		DAGANNOTH(6729, 125),
		OURG(4834, 140),
		OURG2(14793, 140),
		FROST_DRAGON2(18830, 180),
		FROST_DRAGON(18832, 180),
		IMPIOUS_ASHES(20264, 4),
		ACCURSED_ASHES(20266, 12.5),
		INFERNAL_ASHES(20268, 62.5);

		private int id;
		private double experience;

		private static Map<Integer, Bone> bones = new HashMap<>();

		static {
			for (Bone bone : Bone.values())
				bones.put(bone.getId(), bone);
		}

		public static Bone forId(int id) {
			return bones.get(id);
		}

		private Bone(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public int getId() {
			return id;
		}

		public double getExperience() {
			return experience;
		}

		public static final Animation BURY_ANIMATION = new Animation(827);
		public static final Animation SCATTER_ANIMATION = new Animation(445);

		public static void bury(final Player player, int inventorySlot) {
			final Item item = player.getInventory().getItem(inventorySlot);
			if (item == null || Bone.forId(item.getId()) == null || !player.canBury())
				return;
			final Bone bone = Bone.forId(item.getId());
			final ItemDefinitions itemDef = new ItemDefinitions(item.getId());
			player.lock();
			player.soundEffect(2738);

			player.setNextAnimation(bone.name().contains("ASHES") ? SCATTER_ANIMATION : BURY_ANIMATION);
			if (bone == Bone.ACCURSED_ASHES)
				player.setNextSpotAnim(new SpotAnim(56));
			else if (bone == Bone.IMPIOUS_ASHES)
				player.setNextSpotAnim(new SpotAnim(47));
			else if (bone == Bone.INFERNAL_ASHES)
				player.setNextSpotAnim(new SpotAnim(40));
			player.sendMessage(bone.name().contains("ASHES") ? "You scatter the ashes in the wind..." : "You dig a hole in the ground...");

			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					handleNecklaces(player, bone.getId());
					player.sendMessage("You bury the " + itemDef.getName().toLowerCase());
					player.getInventory().deleteItem(item.getId(), 1);
					double xp = bone.getExperience();
					player.getSkills().addXp(Constants.PRAYER, xp);
					player.incrementCount(itemDef.getName()+" buried");
					player.addBoneDelay(1);
					player.unlock();
					stop();
				}

			}, 0);
		}
	}

	public static void handleNecklaces(Player player, int itemId) {
		ItemDefinitions itemDef = ItemDefinitions.getDefs(itemId);
		int prayerGain = 0;
		if (itemDef.getName().toLowerCase().contains("dragon") || itemDef.getName().toLowerCase().contains("ourg"))
			switch (player.getEquipment().getAmuletId()) {
			case 19888:
				prayerGain = 300;
				break;
			case 19887:
			case 19886:
				prayerGain = 10;
				break;
			default:
				break;
			}
		else if (itemDef.getName().toLowerCase().contains("big"))
			switch (player.getEquipment().getAmuletId()) {
			case 19888:
			case 19887:
				prayerGain = 200;
				break;
			case 19886:
				prayerGain = 10;
				break;
			default:
				break;
			}
		else if (itemDef.getName().toLowerCase().equals("bones"))
			switch (player.getEquipment().getAmuletId()) {
			case 19888:
			case 19887:
			case 19886:
				prayerGain = 100;
				break;
			default:
				break;
			}
		player.getPrayer().restorePrayer(prayerGain);
	}
}
