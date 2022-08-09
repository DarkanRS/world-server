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

import com.rs.game.content.skills.smithing.ForgingInterface.Slot;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public class Smithing extends PlayerAction {
	
	public enum Smithable {
		BRONZE_ARROWHEADS(Slot.ARROW_TIPS, new Item(39, 15), 5, 12.5, new Item(2349, 1)),
		IRON_ARROWHEADS(Slot.ARROW_TIPS, new Item(40, 15), 20, 25.0, new Item(2351, 1)),
		STEEL_ARROWHEADS(Slot.ARROW_TIPS, new Item(41, 15), 35, 37.5, new Item(2353, 1)),
		MITHRIL_ARROWHEADS(Slot.ARROW_TIPS, new Item(42, 15), 55, 50.0, new Item(2359, 1)),
		ADAMANT_ARROWHEADS(Slot.ARROW_TIPS, new Item(43, 15), 75, 62.5, new Item(2361, 1)),
		RUNE_ARROWHEADS(Slot.ARROW_TIPS, new Item(44, 15), 90, 75.0, new Item(2363, 1)),
		BRONZE_DART_TIP(Slot.DART_TIPS, new Item(819, 10), 4, 12.5, new Item(2349, 1)),
		IRON_DART_TIP(Slot.DART_TIPS, new Item(820, 10), 19, 25.0, new Item(2351, 1)),
		STEEL_DART_TIP(Slot.DART_TIPS, new Item(821, 10), 34, 37.5, new Item(2353, 1)),
		MITHRIL_DART_TIP(Slot.DART_TIPS, new Item(822, 10), 54, 50.0, new Item(2359, 1)),
		ADAMANT_DART_TIP(Slot.DART_TIPS, new Item(823, 10), 74, 62.5, new Item(2361, 1)),
		RUNE_DART_TIP(Slot.DART_TIPS, new Item(824, 10), 89, 75.0, new Item(2363, 1)),
		IRON_KNIFE(Slot.THROWING_KNIFE, new Item(863, 5), 22, 25.0, new Item(2351, 1)),
		BRONZE_KNIFE(Slot.THROWING_KNIFE, new Item(864, 5), 7, 12.5, new Item(2349, 1)),
		STEEL_KNIFE(Slot.THROWING_KNIFE, new Item(865, 5), 37, 37.5, new Item(2353, 1)),
		MITHRIL_KNIFE(Slot.THROWING_KNIFE, new Item(866, 5), 57, 50.0, new Item(2359, 1)),
		ADAMANT_KNIFE(Slot.THROWING_KNIFE, new Item(867, 5), 77, 62.5, new Item(2361, 1)),
		RUNE_KNIFE(Slot.THROWING_KNIFE, new Item(868, 5), 92, 75.0, new Item(2363, 1)),
		IRON_PLATELEGS(Slot.PLATELEGS, 1067, 31, 75.0, new Item(2351, 3)),
		STEEL_PLATELEGS(Slot.PLATELEGS, 1069, 46, 112.5, new Item(2353, 3)),
		MITHRIL_PLATELEGS(Slot.PLATELEGS, 1071, 66, 150.0, new Item(2359, 3)),
		ADAMANT_PLATELEGS(Slot.PLATELEGS, 1073, 86, 187.5, new Item(2361, 3)),
		BRONZE_PLATELEGS(Slot.PLATELEGS, 1075, 16, 37.5, new Item(2349, 3)),
		RUNE_PLATELEGS(Slot.PLATELEGS, 1079, 99, 225.0, new Item(2363, 3)),
		IRON_PLATESKIRT(Slot.PLATESKIRT, 1081, 31, 75.0, new Item(2351, 3)),
		STEEL_PLATESKIRT(Slot.PLATESKIRT, 1083, 46, 112.5, new Item(2353, 3)),
		MITHRIL_PLATESKIRT(Slot.PLATESKIRT, 1085, 66, 150.0, new Item(2359, 3)),
		BRONZE_PLATESKIRT(Slot.PLATESKIRT, 1087, 16, 37.5, new Item(2349, 3)),
		ADAMANT_PLATESKIRT(Slot.PLATESKIRT, 1091, 86, 187.5, new Item(2361, 3)),
		RUNE_PLATESKIRT(Slot.PLATESKIRT, 1093, 99, 225.0, new Item(2363, 3)),
		IRON_HELM(Slot.MEDIUM_HELM, 1137, 18, 25.0, new Item(2351, 1)),
		BRONZE_HELM(Slot.MEDIUM_HELM, 1139, 1, 12.5, new Item(2349, 1)),
		STEEL_HELM(Slot.MEDIUM_HELM, 1141, 33, 37.5, new Item(2353, 1)),
		MITHRIL_HELM(Slot.MEDIUM_HELM, 1143, 53, 50.0, new Item(2359, 1)),
		ADAMANT_HELM(Slot.MEDIUM_HELM, 1145, 73, 62.5, new Item(2361, 1)),
		RUNE_HELM(Slot.MEDIUM_HELM, 1147, 88, 75.0, new Item(2363, 1)),
		IRON_FULL_HELM(Slot.FULL_HELM, 1153, 22, 50.0, new Item(2351, 2)),
		BRONZE_FULL_HELM(Slot.FULL_HELM, 1155, 7, 25.0, new Item(2349, 2)),
		STEEL_FULL_HELM(Slot.FULL_HELM, 1157, 37, 75.0, new Item(2353, 2)),
		MITHRIL_FULL_HELM(Slot.FULL_HELM, 1159, 57, 100.0, new Item(2359, 2)),
		ADAMANT_FULL_HELM(Slot.FULL_HELM, 1161, 77, 125.0, new Item(2361, 2)),
		RUNE_FULL_HELM(Slot.FULL_HELM, 1163, 92, 150.0, new Item(2363, 2)),
		BRONZE_SQ_SHIELD(Slot.SQUARE_SHIELD, 1173, 8, 25.0, new Item(2349, 2)),
		IRON_SQ_SHIELD(Slot.SQUARE_SHIELD, 1175, 23, 50.0, new Item(2351, 2)),
		STEEL_SQ_SHIELD(Slot.SQUARE_SHIELD, 1177, 38, 75.0, new Item(2353, 2)),
		MITHRIL_SQ_SHIELD(Slot.SQUARE_SHIELD, 1181, 58, 100.0, new Item(2359, 2)),
		ADAMANT_SQ_SHIELD(Slot.SQUARE_SHIELD, 1183, 78, 125.0, new Item(2361, 2)),
		RUNE_SQ_SHIELD(Slot.SQUARE_SHIELD, 1185, 93, 150.0, new Item(2363, 2)),
		BRONZE_KITESHIELD(Slot.KITESHIELD, 1189, 12, 37.5, new Item(2349, 3)),
		IRON_KITESHIELD(Slot.KITESHIELD, 1191, 27, 75.0, new Item(2351, 3)),
		STEEL_KITESHIELD(Slot.KITESHIELD, 1193, 42, 112.5, new Item(2353, 3)),
		MITHRIL_KITESHIELD(Slot.KITESHIELD, 1197, 62, 150.0, new Item(2359, 3)),
		ADAMANT_KITESHIELD(Slot.KITESHIELD, 1199, 82, 187.5, new Item(2361, 3)),
		RUNE_KITESHIELD(Slot.KITESHIELD, 1201, 97, 225.0, new Item(2363, 3)),
		IRON_DAGGER(Slot.DAGGER, 1203, 15, 25.0, new Item(2351, 1)),
		BRONZE_DAGGER(Slot.DAGGER, 1205, 1, 12.5, new Item(2349, 1)),
		STEEL_DAGGER(Slot.DAGGER, 1207, 30, 37.5, new Item(2353, 1)),
		MITHRIL_DAGGER(Slot.DAGGER, 1209, 50, 50.0, new Item(2359, 1)),
		ADAMANT_DAGGER(Slot.DAGGER, 1211, 70, 62.5, new Item(2361, 1)),
		RUNE_DAGGER(Slot.DAGGER, 1213, 85, 75.0, new Item(2363, 1)),
		BRONZE_PICKAXE(Slot.PICKAXE, 1265, 5, 25.0, new Item(2349, 2)),
		IRON_PICKAXE(Slot.PICKAXE, 1267, 20, 50.0, new Item(2351, 2)),
		STEEL_PICKAXE(Slot.PICKAXE, 1269, 35, 75.0, new Item(2353, 2)),
		ADAMANT_PICKAXE(Slot.PICKAXE, 1271, 75, 125.0, new Item(2361, 2)),
		MITHRIL_PICKAXE(Slot.PICKAXE, 1273, 55, 100.0, new Item(2359, 2)),
		RUNE_PICKAXE(Slot.PICKAXE, 1275, 90, 150.0, new Item(2363, 2)),
		BRONZE_SWORD(Slot.SWORD, 1277, 4, 12.5, new Item(2349, 1)),
		IRON_SWORD(Slot.SWORD, 1279, 19, 25.0, new Item(2351, 1)),
		STEEL_SWORD(Slot.SWORD, 1281, 34, 37.5, new Item(2353, 1)),
		MITHRIL_SWORD(Slot.SWORD, 1285, 54, 50.0, new Item(2359, 1)),
		ADAMANT_SWORD(Slot.SWORD, 1287, 74, 62.5, new Item(2361, 1)),
		RUNE_SWORD(Slot.SWORD, 1289, 89, 75.0, new Item(2363, 1)),
		BRONZE_LONGSWORD(Slot.LONGSWORD, 1291, 6, 25.0, new Item(2349, 2)),
		IRON_LONGSWORD(Slot.LONGSWORD, 1293, 21, 50.0, new Item(2351, 2)),
		STEEL_LONGSWORD(Slot.LONGSWORD, 1295, 36, 75.0, new Item(2353, 2)),
		MITHRIL_LONGSWORD(Slot.LONGSWORD, 1299, 56, 100.0, new Item(2359, 2)),
		ADAMANT_LONGSWORD(Slot.LONGSWORD, 1301, 76, 125.0, new Item(2361, 2)),
		RUNE_LONGSWORD(Slot.LONGSWORD, 1303, 91, 150.0, new Item(2363, 2)),
		BRONZE_2H_SWORD(Slot.TWO_HAND_SWORD, 1307, 14, 37.5, new Item(2349, 3)),
		IRON_2H_SWORD(Slot.TWO_HAND_SWORD, 1309, 29, 75.0, new Item(2351, 3)),
		STEEL_2H_SWORD(Slot.TWO_HAND_SWORD, 1311, 44, 112.5, new Item(2353, 3)),
		MITHRIL_2H_SWORD(Slot.TWO_HAND_SWORD, 1315, 64, 150.0, new Item(2359, 3)),
		ADAMANT_2H_SWORD(Slot.TWO_HAND_SWORD, 1317, 84, 187.5, new Item(2361, 3)),
		RUNE_2H_SWORD(Slot.TWO_HAND_SWORD, 1319, 99, 225.0, new Item(2363, 3)),
		BRONZE_SCIMITAR(Slot.SCIMITAR, 1321, 5, 25.0, new Item(2349, 2)),
		IRON_SCIMITAR(Slot.SCIMITAR, 1323, 20, 50.0, new Item(2351, 2)),
		STEEL_SCIMITAR(Slot.SCIMITAR, 1325, 35, 75.0, new Item(2353, 2)),
		MITHRIL_SCIMITAR(Slot.SCIMITAR, 1329, 55, 100.0, new Item(2359, 2)),
		ADAMANT_SCIMITAR(Slot.SCIMITAR, 1331, 75, 125.0, new Item(2361, 2)),
		RUNE_SCIMITAR(Slot.SCIMITAR, 1333, 90, 150.0, new Item(2363, 2)),
		IRON_WARHAMMER(Slot.WARHAMMER, 1335, 24, 75.0, new Item(2351, 3)),
		BRONZE_WARHAMMER(Slot.WARHAMMER, 1337, 9, 37.5, new Item(2349, 3)),
		STEEL_WARHAMMER(Slot.WARHAMMER, 1339, 39, 112.5, new Item(2353, 3)),
		MITHRIL_WARHAMMER(Slot.WARHAMMER, 1343, 59, 150.0, new Item(2359, 3)),
		ADAMANT_WARHAMMER(Slot.WARHAMMER, 1345, 79, 187.5, new Item(2361, 3)),
		RUNE_WARHAMMER(Slot.WARHAMMER, 1347, 94, 225.0, new Item(2363, 3)),
		IRON_HATCHET(Slot.HATCHET, 1349, 16, 25.0, new Item(2351, 1)),
		BRONZE_HATCHET(Slot.HATCHET, 1351, 1, 12.5, new Item(2349, 1)),
		STEEL_HATCHET(Slot.HATCHET, 1353, 31, 37.5, new Item(2353, 1)),
		MITHRIL_HATCHET(Slot.HATCHET, 1355, 51, 50.0, new Item(2359, 1)),
		ADAMANT_HATCHET(Slot.HATCHET, 1357, 71, 62.5, new Item(2361, 1)),
		RUNE_HATCHET(Slot.HATCHET, 1359, 86, 75.0, new Item(2363, 1)),
		IRON_BATTLEAXE(Slot.BATTLEAXE, 1363, 25, 75.0, new Item(2351, 3)),
		STEEL_BATTLEAXE(Slot.BATTLEAXE, 1365, 40, 112.5, new Item(2353, 3)),
		MITHRIL_BATTLEAXE(Slot.BATTLEAXE, 1369, 60, 150.0, new Item(2359, 3)),
		ADAMANT_BATTLEAXE(Slot.BATTLEAXE, 1371, 80, 187.5, new Item(2361, 3)),
		RUNE_BATTLEAXE(Slot.BATTLEAXE, 1373, 95, 225.0, new Item(2363, 3)),
		BRONZE_BATTLEAXE(Slot.BATTLEAXE, 1375, 10, 37.5, new Item(2349, 3)),
		IRON_MACE(Slot.MACE, 1420, 17, 25.0, new Item(2351, 1)),
		BRONZE_MACE(Slot.MACE, 1422, 2, 12.5, new Item(2349, 1)),
		STEEL_MACE(Slot.MACE, 1424, 32, 37.5, new Item(2353, 1)),
		MITHRIL_MACE(Slot.MACE, 1428, 52, 50.0, new Item(2359, 1)),
		ADAMANT_MACE(Slot.MACE, 1430, 72, 62.5, new Item(2361, 1)),
		RUNE_MACE(Slot.MACE, 1432, 87, 75.0, new Item(2363, 1)),
		BRONZE_NAILS(Slot.NAILS, new Item(4819, 15), 4, 12.5, new Item(2349, 1)),
		IRON_NAILS(Slot.NAILS, new Item(4820, 15), 19, 25.0, new Item(2351, 1)),
		STEEL_NAILS(Slot.NAILS, new Item(1539, 15), 34, 37.5, new Item(2353, 1)),
		MITHRIL_NAILS(Slot.NAILS, new Item(4822, 15), 54, 50.0, new Item(2359, 1)),
		ADAMANTITE_NAILS(Slot.NAILS, new Item(4823, 15), 74, 62.5, new Item(2361, 1)),
		RUNE_NAILS(Slot.NAILS, new Item(4824, 15), 89, 75.0, new Item(2363, 1)),
		BRONZE_WIRE(Slot.BRONZE_WIRE, 1794, 4, 12.5, new Item(2349, 1)),
		STEEL_STUDS(Slot.STUDS, 2370, 36, 37.5, new Item(2353, 1)),
		BRONZE_CLAW(Slot.CLAWS, 3095, 13, 25.0, new Item(2349, 2)),
		IRON_CLAW(Slot.CLAWS, 3096, 28, 50.0, new Item(2351, 2)),
		STEEL_CLAW(Slot.CLAWS, 3097, 43, 75.0, new Item(2353, 2)),
		MITHRIL_CLAW(Slot.CLAWS, 3099, 63, 100.0, new Item(2359, 2)),
		ADAMANT_CLAW(Slot.CLAWS, 3100, 83, 125.0, new Item(2361, 2)),
		RUNE_CLAW(Slot.CLAWS, 3101, 98, 150.0, new Item(2363, 2)),
		OIL_LANTERN_FRAME(Slot.BULLSEYE_LANTERN, 4540, 26, 25.0, new Item(2351, 1)),
		BULLSEYE_LANTERN(Slot.BULLSEYE_LANTERN, 4544, 49, 37.5, new Item(2353, 1)),
		IRON_SPIT(Slot.SPIT_IRON, 7225, 17, 25.0, new Item(2351, 1)),
		BRONZE_BOLTS_UNF(Slot.CROSSBOW_BOLTS, new Item(9375, 10), 3, 12.5, new Item(2349, 1)),
		BLURITE_BOLTS_UNF(Slot.CROSSBOW_BOLTS, new Item(9376, 10), 8, 17.5, new Item(9467, 1)),
		IRON_BOLTS_UNF(Slot.CROSSBOW_BOLTS, new Item(9377, 10), 18, 25.0, new Item(2351, 1)),
		STEEL_BOLTS_UNF(Slot.CROSSBOW_BOLTS, new Item(9378, 10), 33, 37.5, new Item(2353, 1)),
		MITHRIL_BOLTS_UNF(Slot.CROSSBOW_BOLTS, new Item(9379, 10), 53, 50.0, new Item(2359, 1)),
		ADAMANT_BOLTS_UNF(Slot.CROSSBOW_BOLTS, new Item(9380, 10), 73, 62.5, new Item(2361, 1)),
		RUNITE_BOLTS_UNF(Slot.CROSSBOW_BOLTS, new Item(9381, 10), 88, 75.0, new Item(2363, 1)),
		MITH_GRAPPLE_TIP(Slot.GRAPPLE_TIP, 9416, 59, 50.0, new Item(2359, 1)),
		BRONZE_LIMBS(Slot.CROSSBOW_LIMBS, 9420, 6, 12.5, new Item(2349, 1)),
		BLURITE_LIMBS(Slot.CROSSBOW_LIMBS, 9422, 13, 17.5, new Item(9467, 1)),
		IRON_LIMBS(Slot.CROSSBOW_LIMBS, 9423, 23, 25.0, new Item(2351, 1)),
		STEEL_LIMBS(Slot.CROSSBOW_LIMBS, 9425, 36, 37.5, new Item(2353, 1)),
		MITHRIL_LIMBS(Slot.CROSSBOW_LIMBS, 9427, 56, 50.0, new Item(2359, 1)),
		ADAMANTITE_LIMBS(Slot.CROSSBOW_LIMBS, 9429, 76, 62.5, new Item(2361, 1)),
		RUNITE_LIMBS(Slot.CROSSBOW_LIMBS, 9431, 91, 75.0, new Item(2363, 1)),
		BLURITE_SWORD(Slot.SWORD, 667, 8, 17.5, new Item(9467, 1));
		
		private static Map<Integer, Map<Slot, Smithable>> BAR_MAP = new HashMap<>();
		
		static {
			for (Smithable s : Smithable.values()) {
				Map<Slot, Smithable> map = BAR_MAP.get(s.bar.getId());
				if (map == null)
					map = new HashMap<>();
				if (map.put(s.slot, s) != null)
					System.err.println("Duplicate slots for " + s.slot + " " + s);
				BAR_MAP.put(s.bar.getId(), map);
			}
		}
		
		public Slot slot;
		public Item product, bar;
		public int level;
		public double xp;
		
		public static int getHighestBar(Player player) {
			int highest = -1;
			for (Item item : player.getInventory().getItems().getItemsCopy()) {
				if (item == null)
					continue;
				if (forBar(item.getId()) != null && item.getId() > highest)
					highest = item.getId();
			}
			return highest;
		}
		
		public static Map<Slot, Smithable> forBar(int barId) {
			return BAR_MAP.get(barId);
		}
		
		Smithable(Slot slot, Item product, int level, double xp, Item bar) {
			this.product = product;
			this.level = level;
			this.xp = xp;
			this.bar = bar;
			this.slot = slot;
		}
		
		Smithable(Slot slot, int product, int level, double xp, Item bar) {
			this(slot, new Item(product, 1), level, xp, bar);
		}
		
		Smithable(Item product, int level, double xp, Item bar) {
			this(null, product, level, xp, bar);
		}
		
		Smithable(int product, int level, double xp, Item bar) {
			this(null, new Item(product, 1), level, xp, bar);
		}
	}

	public static final int HAMMER = 2347, DUNG_HAMMER = 17883;
	private Smithable item;
	private int ticks;

	public Smithing(int ticks, Smithable item) {
		this.ticks = ticks;
		this.item = item;
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().containsItem(HAMMER, 1)) {
			player.sendMessage("You need a hammer in order to work with a bar of " + item.bar.getDefinitions().getName().toLowerCase().replace(" bar", "") + ".");
			return false;
		}
		if (!player.getInventory().containsItem(item.bar)) {
			player.sendMessage("You do not have sufficient bars!");
			return false;
		}
		if (player.getSkills().getLevel(Constants.SMITHING) < item.level) {
			player.sendMessage("You need a Smithing level of " + item.level + " to create this.");
			return false;
		}
		if (player.getInterfaceManager().containsScreenInter()) {
			player.getInterfaceManager().removeCentralInterface();
			return true;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		player.setNextAnimation(new Animation(898));
		int amount = item.bar.getAmount();

		if (player.hasScrollOfEfficiency && amount > 3)
			if (Utils.getRandomInclusive(15) == 5)
				amount--;

		player.getInventory().deleteItem(item.bar.getId(), amount);
		player.getInventory().addItemDrop(item.product);
		player.incrementCount(item.product.getName() + " smithed", item.product.getAmount());
		player.getSkills().addXp(Constants.SMITHING, item.xp);
		if (ticks > 0)
			return 3;
		return -1;
	}

	@Override
	public boolean start(Player player) {
		if (item.bar.getId() != player.getTempAttribs().getI("SmithingBar"))
			return false;
		if (!player.getInventory().containsOneItem(HAMMER, item.bar.getId())) {
			player.sendMessage("You need a hammer in order to work with a bar of " + item.bar.getDefinitions().getName().replace("Bar ", "") + ".");
			return false;
		}
		if (player.getSkills().getLevel(Constants.SMITHING) < item.level) {
			player.sendMessage("You need a Smithing level of " + item.level + " to create this.");
			return false;
		}
		return true;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
