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
package com.rs.game.content.skills.dungeoneering.skills;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.dungeoneering.npcs.misc.DungeonFishSpot;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class DungeoneeringFishing extends PlayerAction {

	public enum Fish {
		HEIM_CRAB(17797, 1, 9),
		RED_EYE(17799, 10, 27),
		DUSK_EEL(17801, 20, 45),
		GIANT_FLATFISH(17803, 30, 63),
		SHORTFINNED_EEL(17805, 40, 81),
		WEB_SNIPPER(17807, 50, 99),
		BOULDABASS(17809, 60, 117),
		SALVE_EEL(17811, 70, 135),
		BLUE_CRAB(17813, 80, 153),
		CAVE_MORAY(17815, 90, 171),
		VILE_FISH(17374, 1, 0);

		private final int id, level;
		private final double xp;

		Fish(int id, int level, double xp) {
			this.id = id;
			this.level = level;
			this.xp = xp;
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
	}

	public static final int FLY_FISHING_ROAD_EMOTE = 622;
	public static final int FLY_FISHING_ROAD = 17794, FEATHER = 17796;

	private final DungeonFishSpot spot;

	public DungeoneeringFishing(DungeonFishSpot spot) {
		this.spot = spot;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.sendMessage("You attempt to capture a fish...", true);
		setActionDelay(player, getFishingDelay(player));
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(FLY_FISHING_ROAD_EMOTE));
		return checkAll(player);
	}

	private int getFishingDelay(Player player) {
		int playerLevel = player.getSkills().getLevel(Constants.FISHING);
		int fishLevel = spot.getFish().getLevel();
		int modifier = spot.getFish().getLevel();
		int randomAmt = Utils.random(4);
		double cycleCount, otherBonus = 0;
		otherBonus += player.getInvisibleSkillBoost(Skills.WOODCUTTING);
		cycleCount = Math.ceil(((fishLevel + otherBonus) * 50 - playerLevel * 10) / modifier * 0.25 - randomAmt * 4);
		if (cycleCount < 1)
			cycleCount = 1;
		return (int) cycleCount + 1;
	}

	@Override
	public int processWithDelay(Player player) {
		addFish(player);
		return getFishingDelay(player);
	}

	private void addFish(Player player) {
		player.sendMessage("You manage to catch a " + ItemDefinitions.getDefs(spot.getFish().id).getName().toLowerCase() + ".", true);
		player.getInventory().deleteItem(FEATHER, 1);
		player.getSkills().addXp(Constants.FISHING, spot.getFish().xp);
		player.getInventory().addItem(spot.getFish().id, 1);
		if (spot.decreaseFishes() <= 1) {
			if (spot.getFish() == Fish.VILE_FISH) {
				spot.addFishes();
				player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * .3), HitLook.TRUE_DAMAGE));
				player.sendMessage("You have a hilarious fishing accident that one day you'll tell your grandchildren about.");
				return;
			}
			spot.finish();
			player.setNextAnimation(new Animation(-1));
			player.sendMessage("You have depleted this resource.");
		}
	}

	private boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Constants.FISHING) < spot.getFish().getLevel()) {
			player.simpleDialogue("You need a fishing level of " + spot.getFish().getLevel() + " to fish here.");
			return false;
		}
		if (!player.getInventory().containsOneItem(FLY_FISHING_ROAD)) {
			player.sendMessage("You need a " + ItemDefinitions.getDefs(FLY_FISHING_ROAD).getName().toLowerCase() + " to fish here.");
			return false;
		}
		if (!player.getInventory().containsOneItem(FEATHER)) {
			player.sendMessage("You don't have " + ItemDefinitions.getDefs(FEATHER).getName().toLowerCase() + " to fish here.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.simpleDialogue("You don't have enough inventory space.");
			return false;
		}
		return !spot.hasFinished();
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
