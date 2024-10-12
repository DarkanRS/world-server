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
package com.rs.game.content.skills.mining;

import com.rs.game.content.Effect;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.AuraManager;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;
import com.rs.utils.drop.DropTable;

public enum Ore {
	CLAY(434, 1, 5, 128, 402, 0),
	COPPER(436, 1, 17.5, 60, 220, 1),
	TIN(438, 1, 17.5, 60, 220, 1),
	BLURITE(668, 10, 17.5, 60, 140, 1),
	LIMESTONE(3211, 10, 26.5, 97, 350, 0),
	IRON(440, 15, 35, 145, 326, 1),
	DAEYALT(9632, 20, 10, 50, 130, 0),
	ELEMENTAL(2892, 1, 1, 50, 130, 0),
	SILVER(442, 20, 40, 26, 201, 1),
	COAL(453, 30, 50, 16, 100, 1),
	GOLD(444, 40, 65, 7, 76, 1),
	PERFECT_GOLD(446, 40, 65, 7, 76, 1),
	LUNAR(9076, 60, 25, 50, 130, 0),
	MITHRIL(447, 55, 80, 4, 50, 1),
	GRANITE_500G(6979, 45, 50, 16, 100, 0),
	GRANITE_2KG(6981, 45, 60, 8, 75, 0),
	GRANITE_5KG(6983, 45, 75, 6, 64, 0),
	SANDSTONE_1KG(6971, 35, 30, 16, 100, 0),
	SANDSTONE_2KG(6973, 35, 40, 8, 75, 0),
	SANDSTONE_5KG(6975, 35, 50, 6, 64, 0),
	SANDSTONE_10KG(6977, 35, 60, 3, 30, 0),
	ADAMANT(449, 70, 95, 2, 25, 1),
	BANE(21778, 77, 90, 2, 25, 0),
	RUNE(451, 85, 125, 11, 19, 0),
	RUNE_ESSENCE(1436, 1, 5, 180, 380, 0) {
		@Override
		public boolean checkRequirements(Player player) {
			return player.getSkills().getLevel(Constants.MINING) < 30;
		}
	},
	PURE_ESSENCE(7936, 1, 5, 180, 380, 0) {
		@Override
		public boolean checkRequirements(Player player) {
			return player.getSkills().getLevel(Constants.MINING) >= 30;
		}
	},
	GEM(-1, 40, 65, 26, 71, 0) {
		@Override
		public boolean rollSuccess(Player player, int level) {
			int neck = player.getEquipment().getAmuletId();
			boolean charged = (neck >= 10354 && neck <= 10360) || (neck >= 1706 && neck <= 1712);
			return Utils.skillSuccess(level, player.getAuraManager().getMiningMul(), charged ? 83 : 26, charged ? 211 : 71);
		}

		@Override
		public void giveOre(Player player) {
			double totalXp = getXp() * Mining.getXPMultiplier(player);
			for (Item gem : DropTable.calculateDrops(player, DropSets.getDropSet("gem_rock"))) {
				player.sendMessage("You successfully mine " + Utils.addArticle(gem.getDefinitions().getName().toLowerCase()) + ".", true);
				player.getSkills().addXp(Constants.MINING, totalXp);
				player.incrementCount(gem.getDefinitions().getName()+" mined");
				player.getInventory().addItem(gem);
			}
		}
	},
	LIVING_MINERALS(15263, 73, 25, 30, 145, 0) {
		@Override
		public void giveOre(Player player) {
			double totalXp = getXp() * Mining.getXPMultiplier(player);
			Item item = new Item(getId(), Utils.random(5, 21));
			player.sendMessage("You successfully mine some " + item.getDefinitions().getName().toLowerCase() + ".", true);
			player.getSkills().addXp(Constants.MINING, totalXp);
			player.getInventory().addItem(item);
			player.incrementCount(item.getDefinitions().getName()+" mined", item.getAmount());
		}
	},
	CONCENTRATED_COAL(453, 77, 50, 16, 100, 1),
	CONCENTRATED_GOLD(444, 80, 65, 7, 76, 1),
	RED_SANDSTONE(23194, 81, 70, 30, 145, 0) {
		@Override
		public boolean checkRequirements(Player player) {
			return player.getSkills().getLevel(Constants.MINING) >= getLevel() && player.getDailyI("redSandstoneMined") < 50;
		}

		@Override
		public void onGiveOre(Player player) {
			if (player.getAuraManager().isActivated(AuraManager.Aura.RESOURCEFUL) && Utils.random(10) == 0) {
				player.sendMessage("Your resourceful aura prevents the sandstone from being depleted.");
				return;
			}
			player.incDailyI("redSandstoneMined");
			player.getVars().setVarBit(10133, player.getDailyI("redSandstoneMined"));
		}
	},
	STARDUST_1(13727, 10, 14, 59, 256, 1),
	STARDUST_2(13727, 20, 25, 59, 146, 1),
	STARDUST_3(13727, 30, 29, 59, 126, 1),
	STARDUST_4(13727, 40, 32, 59, 113, 1),
	STARDUST_5(13727, 50, 47, 59, 77, 1),
	STARDUST_6(13727, 60, 71, 38, 51, 1),
	STARDUST_7(13727, 70, 114, 25, 35, 1),
	STARDUST_8(13727, 80, 145, 20, 30, 1),
	STARDUST_9(13727, 90, 210, 15, 20, 1),
	;

	private final int id;
    private final int level;
	private final int rate1;
    private final int rate99;
	private final double xp;
	private final int rollGem;

	private Ore(int id, int level, double xp, int rate1, int rate99, int rollGem) {
		this.id = id;
		this.level = level;
		this.xp = xp;
		this.rate1 = rate1;
		this.rate99 = rate99;
		this.rollGem = rollGem;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public double getXp() {
		return xp;
	}

	public int getRollGem() {
		return rollGem;
	}

	public boolean checkRequirements(Player player) {
		return player == null || player.getSkills().getLevel(Constants.MINING) >= level;
	}

	public boolean rollSuccess(Player player, int level) {
		return Utils.skillSuccess(level, player != null ? player.getAuraManager().getMiningMul() : 1.0, rate1, rate99);
	}

	public void onGiveOre(Player player) { }

	public void giveOre(Player player) {
		Item ore = new Item(id);
		double totalXp = xp * Mining.getXPMultiplier(player);
		if (player.hasEffect(Effect.JUJU_MINING)) {
			int random = Utils.random(100);
			if (random < 11)
				player.addEffect(Effect.JUJU_MINE_BANK, 75);
		}
		if (this == CLAY && player.getEquipment().getGlovesId() == 11074) {
			int charges = player.getI("braceletOfClayCharges", 28)-1;
			if (charges <= 0) {
				player.getEquipment().setSlot(Equipment.HANDS, null);
				player.getEquipment().refresh(Equipment.HANDS);
				player.sendMessage("Your bracelet of clay degrades into dust.");
				player.delete("braceletOfClayCharges");
			} else
				player.set("braceletOfClayCharges", charges);
			ore.setId(1761);
		}
		if (!name().startsWith("STARDUST"))
			player.sendMessage("You successfully mine " + Utils.addArticle(ore.getDefinitions().getName().toLowerCase()) + ".", true);
		if (player.hasEffect(Effect.JUJU_MINE_BANK) && !name().startsWith("STARDUST")) {
			player.getBank().addItem(ore, true);
			player.spotAnim(2896);
		} else
			player.getInventory().addItem(ore);
		player.getSkills().addXp(Constants.MINING, totalXp);
		player.incrementCount(ore.getDefinitions().getName()+" mined");
		onGiveOre(player);
	}
}