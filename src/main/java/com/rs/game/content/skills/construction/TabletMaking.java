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
package com.rs.game.content.skills.construction;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.Rune;
import com.rs.game.content.skills.magic.RuneSet;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

import java.util.HashMap;
import java.util.Map;

public class TabletMaking {

	public enum Tablet {
		ONYX_ENCHANT(8021, 87, new RuneSet(Rune.COSMIC, 1, Rune.EARTH, 20, Rune.FIRE, 20), 72.8, 9),
		LUMBRIDGE_TELEPORT(8008, 31, new RuneSet(Rune.AIR, 3, Rune.EARTH, 1, Rune.LAW, 1), 41, 13),
		DIAMOND_ENCHANT(8019, 57, new RuneSet(Rune.COSMIC, 1, Rune.EARTH, 10), 50.3, 6),
		WATCHTOWER_TELEPORT(8012, 58, new RuneSet(Rune.EARTH, 2, Rune.LAW, 2), 68, 16),
		HOUSE_TELEPORT(8013, 40, new RuneSet(Rune.AIR, 1, Rune.EARTH, 1, Rune.LAW, 1), 30, 14),
		EMERALD_ENCHANT(8017, 27, new RuneSet(Rune.COSMIC, 1, Rune.AIR, 3), 27.8, 8),
		SAPPHIRE_ENCHANT(8016, 7, new RuneSet(Rune.COSMIC, 1, Rune.WATER, 1), 13.2, 11),
		FALADOR_TELEPORT(8009, 37, new RuneSet(Rune.AIR, 3, Rune.WATER, 1, Rune.LAW, 1), 48, 12),
		ARDOUGNE_TELEPORT(8011, 51, new RuneSet(Rune.WATER, 2, Rune.LAW, 2), 63.5, 2),
		BONES_TO_BANANAS(8014, 15, new RuneSet(Rune.WATER, 2, Rune.EARTH, 2, Rune.NATURE, 1), 25, 3),
		DRAGONSTONE_ENCHANT(8020, 68, new RuneSet(Rune.COSMIC, 1, Rune.WATER, 15, Rune.EARTH, 15), 58.5, 7),
		RUBY_ENCHANT(8018, 49, new RuneSet(Rune.COSMIC, 1, Rune.FIRE, 5), 44.3, 10),
		VARROCK_TELEPORT(8007, 25, new RuneSet(Rune.AIR, 3, Rune.LAW, 1, Rune.FIRE, 1), 35, 15),
		CAMELOT_TELEPORT(8010, 45, new RuneSet(Rune.AIR, 5, Rune.LAW, 1), 55.5, 5),
		BONES_TO_PEACHES(8015, 60, new RuneSet(Rune.WATER, 4, Rune.EARTH, 4, Rune.NATURE, 2), 35.5, 4);

		private static Map<Integer, Tablet> MAP = new HashMap<>();

		static {
			for (Tablet t : Tablet.values())
				MAP.put(t.componentId(), t);
		}

		public static Tablet forId(int componentId) {
			return MAP.get(componentId);
		}

		private int id;
		private int levelReq;
		private RuneSet runeReq;
		private double experience;
		private int componentId;

		private Tablet(int id, int level, RuneSet runes, double experience, int componentId) {
			this.id = id;
			levelReq = level;
			runeReq = runes;
			this.experience = experience;
			this.componentId = componentId;
		}

		public int id() {
			return id;
		}

		public int levelReq() {
			return levelReq;
		}

		public RuneSet runeReq() {
			return runeReq;
		}

		public double experience() {
			return experience;
		}

		public int componentId() {
			return componentId;
		}
	}


	private static final int SOFT_CLAY = 1761;
	private static final int[][] ENABLED_SLOTS = { { 11, 15 }, { 11, 15, 12, 13 }, { 11, 8, 15, 3 }, { 11, 15, 12, 13, 5, 2 }, { 11, 8, 10, 6, 15, 3 }, { 15, 13, 12, 5, 2, 16, 14 }, { 11, 8, 10, 6, 7, 9, 15, 3, 4 } };


	public static void openTabInterface(Player player, int index) {
		player.getPackets().sendVarc(943, index + 1);
		player.getInterfaceManager().sendInterface(400);
		player.getTempAttribs().setI("tablet_index", index);
		player.setNextAnimation(new Animation(3652));
	}

	public static void handleTabletCreation(final Player player, int componentId, int amount) {
		player.closeInterfaces();
		final int index = player.getTempAttribs().getI("tablet_index");
		for (int enabledSlot : ENABLED_SLOTS[index])
			if (enabledSlot == componentId) {
				Tablet t = Tablet.forId(componentId);
				if (player.getSkills().getLevel(Constants.MAGIC) < t.levelReq) {
					player.sendMessage("You need a Magic level of " + t.levelReq + " in order to create this tablet.");
					return;
				}
				int realAmount = 0;
				for (int loop = 0; loop < amount; loop++) {
					if (!Magic.checkRunes(player, false, t.runeReq))
						return;
					if (!player.getInventory().containsItem(SOFT_CLAY, 1)) {
						player.sendMessage("You need some soft clay to craft tablets.");
						return;
					}
					realAmount++;
					Magic.checkRunes(player, true, t.runeReq);
					player.getInventory().deleteItem(SOFT_CLAY, 1);
					player.getInventory().addItem(new Item(t.id, 1));

				}
				player.getSkills().addXp(Constants.MAGIC, t.experience * realAmount);
				player.lock(2);
				player.setNextAnimation(new Animation(3645));
				player.getTempAttribs().removeI("tablet_index");
				return;
			}
		player.sendMessage("You cannot create that tablet, please select another.");
	}
}
