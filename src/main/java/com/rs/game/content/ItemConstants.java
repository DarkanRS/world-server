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

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.world.unorganized_dialogue.RepairStandD;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.utils.Ticks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@PluginEventHandler
public class ItemConstants {

	public static int[] CHARM_IDS = { 12158, 12159, 12160, 12163 };

	public enum ItemDegrade {
		AHRIM_HELM(4708, 4856, 4860, Ticks.fromHours(10), 60000),
		AHRIM_WEAP(4710, 4862, 4866, Ticks.fromHours(10), 100000),
		AHRIM_TOP(4712, 4868, 4872, Ticks.fromHours(10), 90000),
		AHRIM_LEGS(4714, 4874, 4878, Ticks.fromHours(10), 80000),
		DHAROK_HELM(4716, 4880, 4884, Ticks.fromHours(10), 60000),
		DHAROK_WEAP(4718, 4886, 4890, Ticks.fromHours(10), 100000),
		DHAROK_TOP(4720, 4892, 4896, Ticks.fromHours(10), 90000),
		DHAROK_LEG(4722, 4898, 4902, Ticks.fromHours(10), 80000),
		GUTHAN_HELM(4724, 4904, 4908, Ticks.fromHours(10), 60000),
		GUTHAN_WEAP(4726, 4910, 4914, Ticks.fromHours(10), 100000),
		GUTHAN_TOP(4728, 4916, 4920, Ticks.fromHours(10), 90000),
		GUTHAN_LEG(4730, 4922, 4926, Ticks.fromHours(10), 80000),
		KARIL_HELM(4732, 4928, 4932, Ticks.fromHours(10), 60000),
		KARIL_WEAP(4734, 4934, 4938, Ticks.fromHours(10), 100000),
		KARIL_TOP(4736, 4940, 4944, Ticks.fromHours(10), 90000),
		KARIL_LEG(4738, 4946, 4950, Ticks.fromHours(10), 80000),
		TORAG_HELM(4745, 4952, 4956, Ticks.fromHours(10), 60000),
		TORAG_WEAP(4747, 4958, 4962, Ticks.fromHours(10), 100000),
		TORAG_TOP(4749, 4964, 4968, Ticks.fromHours(10), 90000),
		TORAG_LEG(4751, 4970, 4974, Ticks.fromHours(10), 80000),
		VERAC_HELM(4753, 4976, 4980, Ticks.fromHours(10), 60000),
		VERAC_WEAP(4755, 4982, 4986, Ticks.fromHours(10), 100000),
		VERAC_TOP(4757, 4988, 4992, Ticks.fromHours(10), 90000),
		VERAC_LEG(4759, 4994, 4998, Ticks.fromHours(10), 80000),
		AKRISAE_HELM(21736, 21738, 21742, Ticks.fromHours(10), 60000),
		AKRISAE_WEAP(21744, 21746, 21750, Ticks.fromHours(10), 100000),
		AKRISAE_TOP(21752, 21754, 21758, Ticks.fromHours(10), 90000),
		AKRISAE_LEG(21760, 21762, 21766, Ticks.fromHours(10), 80000),

		ZURIELS_TOP(13858, 13860, -1, Ticks.fromHours(1), -1),
		ZURIELS_BOTTOM(13861, 13863, -1, Ticks.fromHours(1), -1),
		ZURIELS_HOOD(13858, 13860, -1, Ticks.fromHours(1), -1),
		ZURIELS_STAFF(13867, 13869, -1, Ticks.fromHours(1), -1),
		MORRIGANS_TOP(13870, 13872, -1, Ticks.fromHours(1), -1),
		MORRIGANS_BOTTOM(13873, 13875, -1, Ticks.fromHours(1), -1),
		MORRIGANS_HOOD(13876, 13878, -1, Ticks.fromHours(1), -1),
		STATIUS_TOP(13884, 13886, -1, Ticks.fromHours(1), -1),
		STATIUS_BOTTOM(13890, 13892, -1, Ticks.fromHours(1), -1),
		STATIUS_HELM(13896, 13898, -1, Ticks.fromHours(1), -1),
		STATIUS_HAMMER(13902, 13904, -1, Ticks.fromHours(1), -1),
		VESTAS_TOP(13887, 13889, -1, Ticks.fromHours(1), -1),
		VESTAS_BOTTOM(13893, 13895, -1, Ticks.fromHours(1), -1),
		VESTAS_LONGSWORD(13899, 13901, -1, Ticks.fromHours(1), -1),
		VESTAS_SPEAR(13905, 13907, -1, Ticks.fromHours(1), -1),

		C_ZURIELS_TOP(13932, 13934, -1, Ticks.fromMinutes(15), -1),
		C_ZURIELS_BOTTOM(13935, 13937, -1, Ticks.fromMinutes(15), -1),
		C_ZURIELS_HOOD(13938, 13940, -1, Ticks.fromMinutes(15), -1),
		C_ZURIELS_STAFF(13941, 13943, -1, Ticks.fromMinutes(15), -1),
		C_MORRIGANS_TOP(13944, 13946, -1, Ticks.fromMinutes(15), -1),
		C_MORRIGANS_BOTTOM(13947, 13949, -1, Ticks.fromMinutes(15), -1),
		C_MORRIGANS_HOOD(13950, 13952, -1, Ticks.fromMinutes(15), -1),
		C_STATIUS_TOP(13908, 13910, -1, Ticks.fromMinutes(15), -1),
		C_STATIUS_BOTTOM(13914, 13916, -1, Ticks.fromMinutes(15), -1),
		C_STATIUS_HELM(13920, 13922, -1, Ticks.fromMinutes(15), -1),
		C_STATIUS_HAMMER(13926, 13928, -1, Ticks.fromMinutes(15), -1),
		C_VESTAS_TOP(13911, 13913, -1, Ticks.fromMinutes(15), -1),
		C_VESTAS_BOTTOM(13917, 13919, -1, Ticks.fromMinutes(15), -1),
		C_VESTAS_LONGSWORD(13923, 13925, -1, Ticks.fromMinutes(15), -1),
		C_VESTAS_SPEAR(13929, 13931, -1, Ticks.fromMinutes(15), -1),

		C_DRAG_CHAIN(13958, 13960, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_HELM(13961, 13963, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_SQ_S(13964, 13966, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_SKIRT(13967, 13969, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_LEGS(13970, 13972, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_BAXE(13973, 13975, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_DAG(13976, 13978, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_SCIM(13979, 13981, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_LONG(13982, 13984, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_MACE(13985, 13987, -1, Ticks.fromMinutes(30), -1),
		C_DRAG_SPEAR(13988, 13990, -1, Ticks.fromMinutes(30), -1),

		GOLIATH_B(22358, 22358, -1, Ticks.fromHours(10), -1),
		GOLIATH_W(22359, 22359, -1, Ticks.fromHours(10), -1),
		GOLIATH_Y(22360, 22360, -1, Ticks.fromHours(10), -1),
		GOLIATH_R(22361, 22361, -1, Ticks.fromHours(10), -1),
		SWIFT_B(22362, 22362, -1, Ticks.fromHours(10), -1),
		SWIFT_W(22363, 22363, -1, Ticks.fromHours(10), -1),
		SWIFT_Y(22364, 22364, -1, Ticks.fromHours(10), -1),
		SWIFT_R(22365, 22365, -1, Ticks.fromHours(10), -1),
		SPELLCASTER_B(22366, 22366, -1, Ticks.fromHours(10), -1),
		SPELLCASTER_W(22367, 22367, -1, Ticks.fromHours(10), -1),
		SPELLCASTER_Y(22368, 22368, -1, Ticks.fromHours(10), -1),
		SPELLCASTER_R(22369, 22369, -1, Ticks.fromHours(10), -1),
		
		BATTLE_HOOD(12866, -1, 12871, Ticks.fromHours(6), -1),
		BATTLE_RTOP(12873, -1, 12878, Ticks.fromHours(6), -1),
		BATTLE_RBOT(12880, -1, 12885, Ticks.fromHours(6), -1),
		
		DMAGE_HOOD(12887, -1, 12892, Ticks.fromHours(6), -1),
		DMAGE_RTOP(12894, -1, 12899, Ticks.fromHours(6), -1),
		DMAGE_RBOT(12901, -1, 12906, Ticks.fromHours(6), -1),
		
		COMBAT_HOOD(12964, -1, 12969, Ticks.fromHours(6), -1),
		COMBAT_RTOP(12971, -1, 12976, Ticks.fromHours(6), -1),
		COMBAT_RBOT(12978, -1, 12983, Ticks.fromHours(6), -1),
		
		ADDY_SPIKE(12908, -1, 12913, Ticks.fromHours(6), -1),
		ADDY_ZERK(12915, -1, 12920, Ticks.fromHours(6), -1),
		RUNE_SPIKE(12922, -1, 12927, Ticks.fromHours(6), -1),
		RUNE_ZERK(12929, -1, 12934, Ticks.fromHours(6), -1),
		
		GREEN_COIF(12936, -1, 12941, Ticks.fromHours(6), -1),
		BLUE_COIF(12943, -1, 12948, Ticks.fromHours(6), -1),
		RED_COIF(12950, -1, 12955, Ticks.fromHours(6), -1),
		BLACK_COIF(12957, -1, 12962, Ticks.fromHours(6), -1),
		ROYAL_COIF(24388, -1, 24393, Ticks.fromHours(6), -1),

		CHAOTIC_RAPIER(18349, -1, 18350, Ticks.fromHours(10), 2000000),
		CHAOTIC_LONGSWORD(18351, -1, 18352, Ticks.fromHours(10), 2000000),
		CHAOTIC_MAUL(18353, -1, 18354, Ticks.fromHours(10), 2000000),
		CHAOTIC_STAFF(18355, -1, 18356, Ticks.fromHours(10), 2000000),
		CHAOTIC_CROSSBOW(18357, -1, 18358, Ticks.fromHours(10), 2000000),
		CHAOTIC_KITESHIELD(18359, -1, 18360, Ticks.fromHours(10), 2000000),
		EAGLE_KITESHIELD(18361, -1, 18362, Ticks.fromHours(10), 2000000),
		FARSEER_KITESHIELD(18363, -1, 18364, Ticks.fromHours(10), 2000000),

		GRAVITE_RAPIER(18365, -1, 18366, Ticks.fromHours(5), 1000000),
		GRAVITE_LONGSWORD(18367, -1, 18368, Ticks.fromHours(5), 1000000),
		GRAVITE_2H(18369, -1, 18370, Ticks.fromHours(5), 1000000),
		GRAVITE_STAFF(18371, -1, 18372, Ticks.fromHours(5), 1000000),
		GRAVITE_SHORTBOW(18373, -1, 18374, Ticks.fromHours(5), 1000000),

		FUNGAL_VISOR(22458, 22460, 22452, Ticks.fromHours(10), -1),
		FUNGAL_PONCHO(22466, 22468, 22456, Ticks.fromHours(10), -1),
		FUNGAL_LEGGINGS(22462, 22464, 22454, Ticks.fromHours(10), -1),

		GRIFOLIC_VISOR(22470, 22472, 22452, Ticks.fromHours(10), -1),
		GRIFOLIC_PONCHO(22478, 22480, 22456, Ticks.fromHours(10), -1),
		GRIFOLIC_LEGGINGS(22474, 22476, 22454, Ticks.fromHours(10), -1),

		GANODERMIC_VISOR(22482, 22484, 22452, Ticks.fromHours(10), -1),
		GANODERMIC_PONCHO(22490, 22492, 22456, Ticks.fromHours(10), -1),
		GANODERMIC_LEGGINGS(22486, 22488, 22454, Ticks.fromHours(10), -1),

		CRYSTAL_BOW_NEW(4212, 4214, 4207, Ticks.fromHours(10), -1),
		CRYSTAL_SHIELD_NEW(4224, 4225, 4207, Ticks.fromHours(10), -1),

		ROYAL_CROSSBOW(24338, 24338, 24339, Ticks.fromHours(10), -1),

		TORVA_HELM(20135, 20137, 20138, Ticks.fromHours(10), 500000),
		TORVA_PLATE(20139, 20141, 20142, Ticks.fromHours(10), 2000000),
		TORVA_LEGS(20143, 20145, 20146, Ticks.fromHours(10), 1000000),
		TORVA_GLOVES(24977, 24978, 24979, Ticks.fromHours(10), 200000),
		TORVA_BOOTS(24983, 24984, 24985, Ticks.fromHours(10), 100000),
		PERNIX_HELM(20147, 20149, 20150, Ticks.fromHours(10), 500000),
		PERNIX_PLATE(20151, 20153, 20154, Ticks.fromHours(10), 2000000),
		PERNIX_LEGS(20155, 20157, 20158, Ticks.fromHours(10), 1000000),
		PERNIX_GLOVES(24974, 24975, 24976, Ticks.fromHours(10), 200000),
		PERNIX_BOOTS(24989, 24990, 24991, Ticks.fromHours(10), 100000),
		VIRTUS_HELM(20159, 20161, 20162, Ticks.fromHours(10), 500000),
		VIRTUS_TOP(20163, 20165, 20166, Ticks.fromHours(10), 2000000),
		VIRTUS_LEGS(20167, 20169, 20170, Ticks.fromHours(10), 1000000),
		VIRTUS_GLOVES(24980, 24981, 24982, Ticks.fromHours(10), 200000),
		VIRTUS_BOOTS(24986, 24987, 24988, Ticks.fromHours(10), 100000),
		ZARYTE_BOW(20171, 20173, 20174, Ticks.fromHours(10), 2000000);

		private final int itemId;
		private final int degradedId;
		private final int brokenId;
		private final int defaultCharges;
		private final int cost;

		private static final Map<Integer, ItemDegrade> BROKEN = new HashMap<>();
		private static final Map<Integer, ItemDegrade> DEGRADE = new HashMap<>();
		private static final Map<Integer, ItemDegrade> REPAIRED = new HashMap<>();

		static {
			for (ItemDegrade item : ItemDegrade.values()) {
				if (item.getBrokenId() != -1)
					BROKEN.put(item.getBrokenId(), item);
				if (item.getDegradedId() != -1)
					DEGRADE.put(item.getDegradedId(), item);
				else
					DEGRADE.put(item.getItemId(), item);
				REPAIRED.put(item.getItemId(), item);
			}
		}

		public static ItemDegrade forId(int itemId) {
			ItemDegrade deg = BROKEN.get(itemId);
			if (deg == null)
				deg = DEGRADE.get(itemId);
			if (deg == null)
				deg = REPAIRED.get(itemId);
			if (deg == null)
				deg = BROKEN.get(itemId);
			return deg;
		}

		public static ItemDegrade forBrokenId(int itemId) {
			return BROKEN.get(itemId);
		}

		public static ItemDegrade forDegradedId(int itemId) {
			return DEGRADE.get(itemId);
		}

		public static ItemDegrade forNewId(int itemId) {
			return REPAIRED.get(itemId);
		}

		ItemDegrade(int itemId, int degradedId, int brokenId, int defaultCharges, int repairCost) {
			this.itemId = itemId;
			this.degradedId = degradedId;
			this.brokenId = brokenId;
			this.defaultCharges = defaultCharges;
			cost = repairCost;
		}

		public int getItemId() {
			return itemId;
		}

		public int getDegradedId() {
			return degradedId;
		}

		public int getDefaultCharges() {
			return defaultCharges;
		}

		public int getBrokenId() {
			return brokenId;
		}

		public int getCost(Item item) {
			if (forBrokenId(item.getId()) != null)
				return cost;
			return cost - ((int) (cost * (((double) item.getMetaDataI("combatCharges", 0)) / ((double) defaultCharges))));
		}

		public int getRepairStandCost(Player player) {
			return cost * (1-(player.getSkills().getLevel(Constants.SMITHING)/200));
		}
	}

	public static ItemClickHandler handleGenericCheckCharges = new ItemClickHandler(new String[] { "Check-charges", "Check state", "Check-state" }, e -> {
		if (e.getItem().getMetaData("combatCharges") != null)
			e.getPlayer().sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(e.getItem().getMetaDataI("combatCharges")));
		else
			e.getPlayer().sendMessage("<col=FF0000>It looks brand new.");
	});

	public static void handleRepairs(Player player, Item item, final boolean stand, final int slot) {
		if (item.getId() >= 18349 && item.getId() <= 18374) {
			player.sendMessage("This item cannot be repaired here. You should speak with the rewards trader in Damonheim.");
			return;
		}
		if (item.getId() == 20120) {
			if (item.getMetaData("frozenKeyCharges") != null && item.getMetaDataI("frozenKeyCharges") < 100)
				player.sendOptionDialogue("Would you like to add a charge to your frozen key? It will cost 50,000 coins.", ops -> {
					ops.add("Yes please.", () -> {
						if (player.getInventory().hasCoins(50000)) {
							if (player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getId() != item.getId())
								return;
							player.getInventory().removeCoins(50000);
							player.getInventory().getItems().set(slot, new Item(20120, 1).addMetaData("frozenKeyCharges", item.getMetaDataI("frozenKeyCharges") + 1));
						} else
							player.sendMessage("You don't have enough money to add a charge.");
					});
					ops.add("No, that's too much.");
				});
			return;
		}
		ItemDegrade details = ItemDegrade.forId(item.getId());
		if (details == null || details.cost == -1) {
			if (item.getId() == 4207 || (item.getId() >= 4212 && item.getId() <= 4234)) {
				player.sendMessage("This item cannot be repaired here, try speaking with Arianwyn in the hunting grounds near Lletya.");
				return;
			}
			player.sendMessage("This item cannot be repaired or is already fully repaired.");
		} else
			player.startConversation(new RepairStandD(player, details, item, stand, slot));
	}

	private static final Set<Quest> GOLIATH_QUESTS = Set.of(Quest.NOMADS_REQUIEM, Quest.DO_NO_EVIL, Quest.BLOOD_RUNS_DEEP, Quest.DREAM_MENTOR, Quest.WHILE_GUTHIX_SLEEPS, Quest.TEMPLE_AT_SENNTISTEN);
	private static final Set<Quest> SWIFT_QUESTS = Set.of(Quest.VOID_STARES_BACK, Quest.LOVE_STORY, Quest.DESERT_TREASURE, Quest.RECIPE_FOR_DISASTER, Quest.MONKEY_MADNESS, Quest.DREAM_MENTOR);
	private static final Set<Quest> SPELLCASTER_QUESTS = Set.of(Quest.NOMADS_REQUIEM, Quest.BLOOD_RUNS_DEEP, Quest.DREAM_MENTOR, Quest.LEGENDS_QUEST, Quest.CURSE_OF_ARRAV, Quest.MY_ARMS_BIG_ADVENTURE);

	public static boolean canWear(Item item, Player player) {
		if (player.hasRights(Rights.ADMIN))
			return true;
		if (item.getId() == 22358 || item.getId() == 22359 || item.getId() == 22360 || item.getId() == 22361) {
			boolean canWear = true;
			for (Quest quest : GOLIATH_QUESTS) {
				if (!player.isQuestComplete(quest, "to use Goliath gloves."))
					canWear = false;
			}
			if (!canWear) return false;
		}
		if (item.getId() == 22362 || item.getId() == 22363 || item.getId() == 22364 || item.getId() == 22365) {
			boolean canWear = true;
			for (Quest quest : SWIFT_QUESTS) {
				if (!player.isQuestComplete(quest, "to use Swift gloves."))
					canWear = false;
			}
			if (!canWear) return false;
		}
		if (item.getId() == 22366 || item.getId() == 22367 || item.getId() == 22368 || item.getId() == 22369) {
			boolean canWear = true;
			for (Quest quest : SPELLCASTER_QUESTS) {
				if (!player.isQuestComplete(quest, "to use Spellcaster gloves."))
					canWear = false;
			}
			if (!canWear) return false;
		}
		if (item.getId() == 9813 || item.getId() == 9814 || item.getId() == 10662)
			if (!player.getQuestManager().completedAllQuests()) {
				player.sendMessage("You need to have completed all quests to be able to wear this.");
				return false;
			}
		Quest quest = Quest.forSlot(item.getDefinitions().getWieldQuestReq());
		if (quest != null) {
			if (!player.isQuestComplete(quest, "to wear this."))
				return false;
		}
		HashMap<Integer, Integer> requirements = item.getDefinitions().getWearingSkillRequiriments();
		boolean hasRequirements = true;
		if (requirements != null)
			for (int skillId : requirements.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;
				int level = requirements.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequirements)
						if (player.getSession() != null)
							player.sendMessage("You are not a high enough level to use this item.");
					hasRequirements = false;
					String name = Constants.SKILL_NAME[skillId].toLowerCase();
					if (player.getSession() != null)
						player.sendMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name + " level of " + level + ".");
				}

			}
		SetReward reward = SetReward.forId(item.getId());
		if ((reward != null && !reward.hasRequirements(player, item.getId())) || !hasRequirements)
			return false;
		if (item.getDefinitions().getName().contains("Max cape") || item.getDefinitions().getName().contains("Max hood"))
			if (!player.getSkills().isMaxed(false))
				return false;
		if ((item.getId() == 20769 || item.getId() == 20770 || item.getId() == 20771 || item.getId() == 20772))
			if (!player.checkCompRequirements(item.getId() == 20771 || item.getId() == 20772))
				return false;
		String itemName = item.getName();
		if (itemName.contains("goliath gloves") || itemName.contains("spellcaster glove") || itemName.contains("swift glove"))
			if (player.getDominionTower().getKilledBossesCount() < 50 && !Settings.getConfig().isDebug()) {
				player.sendMessage("You need to have kill at least 50 bosses in the Dominion Tower to wear these gloves.");
				return true;
			}
		return true;
	}

	public static boolean isDungItem(int itemId) {
		if (itemId < 15750)
			return false;
		//General dung items, dung pouches, dung only kinship rings
        return (itemId >= 15750 && itemId <= 18329) || (itemId >= 18511 && itemId <= 18570) || (itemId >= 18817 && itemId <= 18829);
    }

	public static boolean isHouseOnlyItem(int itemId) {
        return itemId >= 7671 && itemId <= 7755;
    }

	public static int noteIfPossible(int originalId) {
		ItemDefinitions def = ItemDefinitions.getDefs(originalId);
		if (def.noted || def.certId == -1) return originalId;
		return def.certId;
	}

	public static boolean isTradeable(Item item) {
		if (item.getMetaData() != null)
			return false;
		switch(item.getId()) {
			//tradeable non-exchangeable item exceptions
			case 995:
			case 1706:
			case 1707:
			case 1708:
			case 1709:
			case 1710:
			case 1711:
			case 20653:
			case 20654:
			case 20655:
			case 20656:
			case 20657:
			case 20658:
			case 11173:
			case 11174:
			case 759:
				return true;
		}
		if ((!item.getDefinitions().isStackable() && item.getDefinitions().getCertId() == -1) || item.getDefinitions().isDestroyItem() || item.getDefinitions().isLended())
			return false;
		return item.getDefinitions().canExchange();
	}

	public static ItemOnObjectHandler handleArmorRepair = new ItemOnObjectHandler(new Object[] { 13715 }, new Object[] { 4207, 4212, 4213, 4214, 4215, 4216, 4217, 4218, 4219,4220, 4221, 4222, 4223, 4224, 4225, 4226, 4227, 4228, 4229, 4230,4231,4232,4233, 4234,
		18349, 18350, 18351, 18352, 18353, 18354, 18355, 18356, 18357, 18358, 18359, 18360, 18361, 18362, 18363, 18364, 18365, 18366, 18367, 18368, 18369, 18370, 18371, 18372, 18373, 18374, 20120 },
		e -> ItemConstants.handleRepairs(e.getPlayer(), e.getItem(), true, e.getItem().getSlot()));

}
