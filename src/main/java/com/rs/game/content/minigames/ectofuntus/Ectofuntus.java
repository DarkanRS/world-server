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
package com.rs.game.content.minigames.ectofuntus;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.prayer.Burying;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class Ectofuntus {

	public static final int EMPTY_POT = 1931;
	public static final int EMPTY_BUCKET = 1925;
	public static final int ECTO_TOKEN = 4278;
	public static final int BUCKET_OF_SLIME = 4286;
	public static final int ECTOPHIAL = 4251;

	public static enum BoneMeal {
		BONES(526, 4255),
		BAT_BONES(530, 4256),
		BIG_BONES(532, 4257),
		BABY_DRAGON_BONES(534, 4260),
		DRAGON_BONES(536, 4261),
		DAGANNOTH_BONES(6729, 6728),
		WYVERN_BONES(6812, 6810),
		OURG_BONES(4834, 4855),
		FROST_BONES(18832, 18834),
		IMPIOUS_ASHES(20264, 20264),
		ACCURSED_ASHES(20266, 20266),
		INFERNAL_ASHES(20268, 20268);

		private int boneId;
		private int boneMealId;

		private static Map<Integer, BoneMeal> bonemeals = new HashMap<>();
		private static Map<Integer, BoneMeal> bones = new HashMap<>();

		public static BoneMeal forBoneId(int itemId) {
			return bonemeals.get(itemId);
		}

		public static BoneMeal forMealId(int itemId) {
			return bones.get(itemId);
		}

		static {
			for (final BoneMeal bonemeal : BoneMeal.values())
				bonemeals.put(bonemeal.boneId, bonemeal);
			for (final BoneMeal bonemeal : BoneMeal.values())
				bones.put(bonemeal.boneMealId, bonemeal);
		}

		private BoneMeal(int boneId, int boneMealId) {
			this.boneId = boneId;
			this.boneMealId = boneMealId;
		}

		public int getBoneId() {
			return boneId;
		}

		public int getBoneMealId() {
			return boneMealId;
		}
	}

	public static final void sendEctophialTeleport(Player player, Tile tile) {
		Magic.sendTeleportSpell(player, 8939, 8941, 1678, 1679, 0, 0, tile, 3, true, Magic.MAGIC_TELEPORT, null);
	}

	public static ObjectClickHandler handleEntrance = new ObjectClickHandler(new Object[] { 5268 }, e -> e.getPlayer().useLadder(Tile.of(3669, 9888, 3)));
	public static ObjectClickHandler handleExit = new ObjectClickHandler(new Object[] { 5264 }, e -> e.getPlayer().useLadder(Tile.of(3654, 3519, 0)));

	public static ObjectClickHandler handleShortcuts = new ObjectClickHandler(new Object[] { 9307, 9308 }, e -> {
		switch (e.getObjectId()) {
		case 9307:
			if (!Agility.hasLevel(e.getPlayer(), 53))
				return;
			e.getPlayer().useLadder(Tile.of(3670, 9888, 3));
			return;

		case 9308:
			if (!Agility.hasLevel(e.getPlayer(), 53))
				return;
			e.getPlayer().useLadder(Tile.of(3671, 9888, 2));
			return;
		}
	});

	public static ObjectClickHandler handleStairs = new ObjectClickHandler(new Object[] { 5262, 5263 }, e -> {
		switch (e.getObjectId()) {
		case 5262:
			if (e.getPlayer().getPlane() == 2)
				e.getPlayer().setNextTile(Tile.of(3692, 9888, 3));
			if (e.getPlayer().getPlane() == 1)
				e.getPlayer().setNextTile(Tile.of(3671, 9888, 2));
			if (e.getPlayer().getPlane() == 0)
				e.getPlayer().setNextTile(Tile.of(3687, 9888, 1));
			return;

		case 5263:
			if (e.getPlayer().getPlane() == 3)
				e.getPlayer().setNextTile(Tile.of(3688, 9888, 2));
			if (e.getPlayer().getPlane() == 2)
				e.getPlayer().setNextTile(Tile.of(3675, 9887, 1));
			if (e.getPlayer().getPlane() == 1)
				e.getPlayer().setNextTile(Tile.of(3683, 9888, 0));
			return;
		}
	});

	public static ObjectClickHandler handleWorship = new ObjectClickHandler(new Object[] { 5282 }, e -> {
		if (!e.getPlayer().getInventory().containsItem(BUCKET_OF_SLIME, 1)) {
			e.getPlayer().sendMessage("You need a bucket of slime before you can worship the ectofuntus.");
			return;
		}
		for (Item item : e.getPlayer().getInventory().getItems().array()) {
			if (item == null)
				continue;
			BoneMeal bone = BoneMeal.forMealId(item.getId());
			if (bone != null) {
				Burying.Bone boneData = Burying.Bone.forId(bone.getBoneId());
				if (boneData == null) {
					e.getPlayer().sendMessage("Error bone not added.. Please post the bone you tried to add on the forums.");
					return;
				}
				e.getPlayer().incrementCount(ItemDefinitions.getDefs(bone.getBoneId()).getName()+" offered at ectofuntus");
				e.getPlayer().setNextAnimation(new Animation(1651));
				e.getPlayer().getInventory().deleteItem(bone.getBoneMealId(), 1);
				if (!bone.name().contains("ASHES"))
					e.getPlayer().getInventory().addItem(EMPTY_POT, 1);
				e.getPlayer().getInventory().deleteItem(BUCKET_OF_SLIME, 1);
				e.getPlayer().getInventory().addItem(EMPTY_BUCKET, 1);
				e.getPlayer().getSkills().addXp(Constants.PRAYER, boneData.getExperience() * 4);
				e.getPlayer().unclaimedEctoTokens += 5;
				return;
			}
		}
	});

	public static ObjectClickHandler handleGrinder = new ObjectClickHandler(new Object[] { 11163 }, e -> grinder(e.getPlayer()));
	public static ObjectClickHandler handleBin = new ObjectClickHandler(new Object[] { 11164 }, e -> bin(e.getPlayer()));

	public static boolean handleItemOnObject(Player player, int itemId, int objectId) {
		ObjectDefinitions objectDefs = ObjectDefinitions.getDefs(objectId);
		ItemDefinitions itemDefs = ItemDefinitions.getDefs(itemId);

		if (itemId == EMPTY_BUCKET && objectDefs.getName().equals("Pool of Slime")) {
			player.getActionManager().setAction(new SlimeBucketFill());
			return true;
		}

		if (itemDefs.getName().toLowerCase().contains("bone") && objectId == 11162) {
			player.getActionManager().setAction(new EctoBones(itemId));
			return true;
		}
		return false;
	}

	public static boolean hopper(Player player, int itemId) {
		if (player.boneType != -1 && player.boneType != 0) {
			player.sendMessage("You already have some bones in the hopper.");
			return false;
		}
		if (!player.getInventory().containsItem(itemId, 1))
			return false;
		BoneMeal meal = BoneMeal.forBoneId(itemId);
		if (meal != null) {
			player.boneType = meal.getBoneId();
			player.sendMessage("You put the bones in the hopper.", true);
			player.setNextAnimation(new Animation(1649));
			player.getInventory().deleteItem(meal.getBoneId(), 1);
			return true;
		}
		player.boneType = -1;
		return false;
	}

	public static boolean grinder(Player player) {
		if (player.boneType != -1 && !player.bonesGrinded) {
			player.sendMessage("You turn the grinder, some crushed bones fall into the bin.", true);
			player.setNextAnimation(new Animation(1648));
			player.bonesGrinded = true;
			return true;
		}
		player.setNextAnimation(new Animation(1648));
		return false;
	}

	public static boolean bin(Player player) {
		if (player.boneType == -1) {
			player.sendMessage("You need to put some bones in the hopper and grind them first.");
			return false;
		}
		if (!player.bonesGrinded) {
			player.sendMessage("You need to grind the bones by turning the grinder first.");
			return false;
		}
		if (player.boneType != -1 && player.bonesGrinded) {
			BoneMeal meal = BoneMeal.forBoneId(player.boneType);
			if (meal != null) {
				player.sendMessage("You fill an empty pot with bones.", true);
				player.setNextAnimation(new Animation(1650));
				player.getInventory().deleteItem(EMPTY_POT, 1);
				player.getInventory().addItem(meal.getBoneMealId(), 1);
				player.boneType = -1;
				player.bonesGrinded = false;
				return true;
			}
			player.boneType = -1;
		}
		return false;
	}
}
