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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.fishing;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;

public enum Fish {
	CRAYFISH(13435, 1, 10, 60, 180),

	SHRIMP(317, 1, 10, 60, 180),
	ANCHOVIES(321, 15, 40, 30, 90),

	SARDINES(327, 5, 20, 32, 180),
	GIANT_CARP(338, 10, 50, 32, 192),
	HERRING(345, 10, 30, 16, 96),
	TROUT(335, 20, 50, 32, 192),
	SALMON(331, 30, 70, 16, 96),

	PIKE(349, 25, 60, 16, 110),

	FROGSPAWN(5004, 33, 75, 16, 96),

	TUNA(359, 35, 80, 16, 70, (p) -> {
		if (Utils.random(10) == 0)
			if (p.getSkills().getLevel(Constants.AGILITY) >= 35) {
				p.getInventory().addItemDrop(359, 1);
				p.sendMessage("Your quick reflexes allow you to catch an extra fish!", true);
			}
	}),
	SWORDFISH(371, 50, 100, 8, 50, (p) -> {
		if (Utils.random(10) == 0)
			if (p.getSkills().getLevel(Constants.AGILITY) >= 50) {
				p.getInventory().addItemDrop(371, 1);
				p.sendMessage("Your quick reflexes allow you to catch an extra fish!", true);
			}
	}),

	LOBSTER(377, 40, 90, 1, 120),

	MACKEREL(353, 16, 20, 10, 50),
	COD(341, 23, 45, 6, 30),
	BASS(363, 46, 100, 6, 20),
	LEATHER_BOOTS(1061, 16, 1, 8, 8),
	SEAWEED(401, 16, 1, 8, 8),
	LEATHER_GLOVES(1059, 16, 1, 4, 4),
	OYSTER(407, 16, 10, 4, 4),
	CASKET(405, 16, 10, 2, 3),

	LEAPING_TROUT(11328, 48, 50, 32, 192, (p) -> {
		return p.getSkills().getLevel(Constants.AGILITY) >= 15 && p.getSkills().getLevel(Constants.STRENGTH) >= 15;
	}, (p) -> {
		p.getSkills().addXp(Constants.AGILITY, 5);
		p.getSkills().addXp(Constants.STRENGTH, 5);
	}),
	LEAPING_SALMON(11330, 58, 70, 16, 96, (p) -> {
		return p.getSkills().getLevel(Constants.AGILITY) >= 30 && p.getSkills().getLevel(Constants.STRENGTH) >= 30;
	}, (p) -> {
		p.getSkills().addXp(Constants.AGILITY, 6);
		p.getSkills().addXp(Constants.STRENGTH, 6);
	}),
	LEAPING_STURGEON(11332, 70, 80, 8, 64, (p) -> {
		return p.getSkills().getLevel(Constants.AGILITY) >= 45 && p.getSkills().getLevel(Constants.STRENGTH) >= 45;
	}, (p) -> {
		p.getSkills().addXp(Constants.AGILITY, 7);
		p.getSkills().addXp(Constants.STRENGTH, 7);
	}),

	MONKFISH(7944, 62, 120, 25, 80),

	SHARK(383, 76, 110, 1, 37, (p) -> {
		if (Utils.random(10) == 0)
			if (p.getSkills().getLevel(Constants.AGILITY) >= 76) {
				p.getInventory().addItemDrop(383, 1);
				p.sendMessage("Your quick reflexes allow you to catch an extra fish!", true);
			}
	}),

	CAVEFISH(15264, 85, 300, -40, 40),

	ROCKTAIL(15270, 90, 385, -35, 35);

	private static final int[] BONUS_FISH = { 341, 349, 401, 407 };

	private int id, level;
	private int rate1, rate99;
	private double xp;
	private Predicate<Player> extraReqs;
	private Consumer<Player> extraRewards;

	private Fish(int id, int level, double xp, int rate1, int rate99, Predicate<Player> extraReqs, Consumer<Player> extraRewards) {
		this.id = id;
		this.level = level;
		this.xp = xp;
		this.rate1 = rate1;
		this.rate99 = rate99;
		this.extraReqs = extraReqs;
		this.extraRewards = extraRewards;
	}

	private Fish(int id, int level, double xp, int rate1, int rate99, Predicate<Player> extraReq) {
		this(id, level, xp, rate1, rate99, extraReq, null);
	}

	private Fish(int id, int level, double xp, int rate1, int rate99, Consumer<Player> extraRewards) {
		this(id, level, xp, rate1, rate99, null, extraRewards);
	}

	private Fish(int id, int level, double xp, int rate1, int rate99) {
		this(id, level, xp, rate1, rate99, null, null);
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

	public boolean checkRequirements(Player player) {
		return player.getSkills().getLevel(Constants.FISHING) >= level && (extraReqs != null ? extraReqs.test(player) : true);
	}

	public boolean rollSuccess(Player player, int level) {
		return Utils.skillSuccess(level, player.getAuraManager().getFishingMul(), rate1, rate99*10);
	}

	public void giveFish(Player player, FishingSpot spot) {
		Item fish = new Item(id, 10);
		int baitToDelete = -1;
		if (spot.getBait() != null)
			for (int bait : spot.getBait())
				if (player.getInventory().containsItem(bait)) {
					baitToDelete = bait;
					break;
				}
		if (baitToDelete != -1)
			player.getInventory().deleteItem(baitToDelete, 1);
		double totalXp = xp;
		if (Fishing.hasFishingSuit(player))
			totalXp *= 1.025;
		if (fish.getId() == 383 && player.hasEffect(Effect.JUJU_FISHING)) {
			int random = Utils.random(100);
			if (random < 30)
				fish.setId(19947);
		}
		player.sendMessage(Fishing.getMessage(spot, this), true);
		if (fish.getId() != -1)
			player.getInventory().addItem(fish);
		player.getSkills().addXp(Constants.FISHING, totalXp);
		player.incrementCount(fish.getDefinitions().getName()+" caught fishing");
		if (player.getFamiliar() != null)
			if (Utils.getRandomInclusive(50) == 0 && Fishing.getSpecialFamiliarBonus(player.getFamiliar().getId()) > 0) {
				player.getInventory().addItem(new Item(BONUS_FISH[Utils.random(BONUS_FISH.length)]));
				player.getSkills().addXp(Constants.FISHING, 5.5);
			}
		if (extraRewards != null)
			extraRewards.accept(player);
	}
}