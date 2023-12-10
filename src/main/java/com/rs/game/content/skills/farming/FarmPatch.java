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
package com.rs.game.content.skills.farming;

import com.rs.engine.dialogue.Dialogue;
import com.rs.game.content.Effect;
import com.rs.game.content.Potions;
import com.rs.game.content.achievements.AchievementDef;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.skills.woodcutting.TreeType;
import com.rs.game.content.skills.woodcutting.Woodcutting;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class FarmPatch {
	public static int FARMING_TICK = 500;

	public static final int[] COMPOST_ORGANIC = { 6055, 1942, 1957, 1965, 5986, 5504, 5982, 249, 251, 253, 255, 257, 2998, 259, 261, 263, 3000, 265, 2481, 267, 269, 1951, 753, 2126, 247, 239, 6018 };
	public static final int[] SUPER_COMPOST_ORGANIC = { 2114, 5978, 5980, 5982, 6004, 247, 6469, 19974 };

	public static final Animation
	RAKING_ANIMATION = new Animation(2273),
	WATERING_ANIMATION = new Animation(2293),
	SEED_DIBBING_ANIMATION = new Animation(2291),
	SPADE_ANIMATION = new Animation(830),
	HERB_PICKING_ANIMATION = new Animation(2282),
	MAGIC_PICKING_ANIMATION = new Animation(2286),
	CURE_PLANT_ANIMATION = new Animation(2288),
	CHECK_TREE_ANIMATION = new Animation(832),
	PRUNING_ANIMATION = new Animation(2275),
	FLOWER_PICKING_ANIMATION = new Animation(2292),
	FRUIT_PICKING_ANIMATION = new Animation(2280),
	COMPOST_ANIMATION = new Animation(2283),
	BUSH_PICKING_ANIMATION = new Animation(2281),
	FILL_COMPOST_ANIMATION = new Animation(832);

	public PatchLocation location;
	public ProduceType seed;
	public int weeds;
	public int growthStage;
	public int totalGrowthTicks;
	public boolean diseased;
	public boolean watered;
	public boolean dead;
	public boolean diseaseProtected;
	public int compostLevel;
	public int lives;
	public boolean checkedHealth;

	public FarmPatch(PatchLocation location) {
		this.location = location;
		empty();
		weeds = 3;
	}

	private void handleClick(Player player, GameObject object, String option, ClientPacket opNum) {
		if (location.type == PatchType.COMPOST) {
			if (seed == null) {
				if (lives <= -15) {
					seed = compostLevel == 1 ? ProduceType.Compost : ProduceType.Supercompost;
					lives = 0;
					updateVars(player);
				}
			} else if (lives > 0) {
				if (!checkedHealth) {
					checkedHealth = true;
					updateVars(player);
					return;
				}
				player.getActionManager().setAction(new HarvestPatch(this));
			} else
				player.sendMessage("The compost doesn't smell ready to be collected yet.");
			return;
		}
		switch(option) {
		case "Rake":
			player.getActionManager().setAction(new RakeAction(this));
			return;
		case "Inspect":
			if (player.hasRights(Rights.DEVELOPER))
				player.sendMessage(this.toString());
			player.startConversation(new Dialogue().addSimple("Yep! Looks like a " + object.getDefinitions(player).getName().toLowerCase() + " to me!"));
			return;
		case "Guide":
			return;
		case "Check-health":
			if (checkedHealth)
				return;
			player.getSkills().addXp(Constants.FARMING, seed.experience);
			checkedHealth = true;
			updateVars(player);
			return;
		case "Prune":
			if (diseased) {
				player.setNextAnimation(PRUNING_ANIMATION);
				player.sendMessage("You prune the diseased leaves from the plant.");
				player.lock(6);
				WorldTasks.delay(5, () -> {
					diseased = false;
					updateVars(player);
					player.setNextAnimation(new Animation(-1));
				});
			}
			return;
		case "Clear":
			if (!player.getInventory().containsItem(952)) {
				player.sendMessage("You need a spade to clear the patch with.");
				return;
			}
			promptClear(player);
			return;
		case "Gather-Branches":
			player.getActionManager().setAction(new HarvestPatch(this));
			return;
		}
		switch(opNum) {
		case OBJECT_OP1:
			if (option.contains("Pick") || option.contains("Harvest"))
				player.getActionManager().setAction(new HarvestPatch(this));
			else if (option.contains("Chop")) {
				TreeType type = TreeType.FRUIT_TREE;
				type = switch (seed) {
				case Oak -> TreeType.OAK;
				case Willow -> TreeType.WILLOW;
				case Maple -> TreeType.MAPLE;
				case Yew -> TreeType.YEW;
				case Magic -> TreeType.MAGIC;
				default -> TreeType.FRUIT_TREE;
				};
				player.getActionManager().setAction(new Woodcutting(object, type) {
					@Override
					public void fellTree() {
						lives = -1;
						updateVars(player);
					}

					@Override
					public boolean checkTree() {
						return lives == 0;
					}
				});
			}
			return;
		default:
			return;
		}
	}

	private void useItem(Player player, GameObject object, Item item) {
		if (location.type == PatchType.COMPOST) {
			if (seed == null)
				player.getActionManager().setAction(new FillCompostBin(this, item));
			return;
		}
		ProduceType produce = ProduceType.forSeed(item.getId());
		if (produce != null) {
			if (weeds > 0) {
				player.sendMessage("You need to rake the patch first.");
				return;
			}
			if (seed != null) {
				player.sendMessage("You already have something growing here.");
				return;
			}
			if (produce.type != location.type || (location == PatchLocation.Wilderness_flower && produce != ProduceType.Limpwurt) || (location == PatchLocation.Burthorpe_potato_patch && produce != ProduceType.Potato)) {
				player.sendMessage("You can't grow that here.");
				return;
			}
			if (player.getSkills().getLevel(Constants.FARMING) < produce.level) {
				player.sendMessage("You need a Farming level of " + produce.level + " to plant that.");
				return;
			}
			if (!player.getInventory().containsItem(5343)) {
				player.sendMessage("You need a seed dibber to plant this.");
				return;
			}
			int seedCount = 1;
			switch(produce.type) {
			case ALLOTMENT:
				seedCount = 3;
				break;
			case HOP:
				seedCount = produce == ProduceType.Jute ? 3 : 4;
				break;
			default:
				break;
			}
			if (!player.getInventory().containsItem(produce.seedId, seedCount)) {
				player.sendMessage("You need " + seedCount + " seeds for this patch.");
				return;
			}
			player.getSkills().addXp(Constants.FARMING, produce.plantingExperience);
			player.setNextAnimation(SEED_DIBBING_ANIMATION);
			player.getInventory().deleteItem(produce.seedId, seedCount);
			seed = produce;
			updateVars(player);
			return;
		}
		switch(item.getId()) {
		case 6032:
		case 6034:
			if (fullyGrown()) {
				player.sendMessage("Composting it isn't going to make it get any bigger.");
				return;
			}
			if (compostLevel >= (item.getId() == 6032 ? 1 : 2)) {
				player.sendMessage("This patch has already been treated with "+(item.getId() == 6032 ? "" : "super")+"compost.");
				return;
			}
			player.setNextFaceTile(object.getTile());
			player.getSkills().addXp(Constants.FARMING, 18);
			player.setNextAnimation(COMPOST_ANIMATION);
			item.setId(1925);
			player.getInventory().refresh();
			compostLevel = item.getId() == 6032 ? 1 : 2;
			break;
		case 6036:
			if (dead) {
				player.sendMessage("It says 'Cure' not 'Resurrect'. Although death may arise from disease, it is not in itself a disease and hence cannot be cured. So there.");
				return;
			}
			if (!diseased) {
				player.sendMessage("It is growing just fine.");
				return;
			}
			player.setNextFaceTile(object.getTile());
			player.getSkills().addXp(Constants.FARMING, 90);
			player.setNextAnimation(CURE_PLANT_ANIMATION);
			item.setId(229);
			player.getInventory().refresh();
			diseased = false;
			updateVars(player);
			break;
		case 18682:
			if (seed == null || fullyGrown() || diseased || dead || watered || (location.type != PatchType.ALLOTMENT && location.type != PatchType.FLOWER && location.type != PatchType.HOP && location.type != PatchType.VINE_FLOWER)) {
				player.sendMessage("That patch doesn't need watering.");
				return;
			}
			player.setNextAnimation(WATERING_ANIMATION);
			watered = true;
			updateVars(player);
			break;
		case 952:
			promptClear(player);
			break;
		case 20023:
		case 20024:
		case 20025:
		case 20026:
		case 23161:
		case 23162:
		case 23163:
		case 23164:
		case 23165:
		case 23166:
			if (location != PatchLocation.Herblore_Habitat_flower) {
				player.sendMessage("This potion is only effective when poured on the flower patch in Herblore Habitat.");
				return;
			}
			if (item.getId() == 23166)
				player.getInventory().deleteItem(item);
			else
				item.setId(item.getId() == 20026 ? Potions.JUJU_VIAL : item.getId()+1);
			player.setNextAnimation(CURE_PLANT_ANIMATION);
			player.addEffect(Effect.JUJU_HUNTER, Ticks.fromMinutes(10));
			player.sendMessage("You pour the potion onto the blossom.");
			player.getInventory().refresh();
			break;
		case 5340:
		case 5339:
		case 5338:
		case 5337:
		case 5336:
		case 5335:
		case 5334:
		case 5333:
			if (seed == null || fullyGrown() || diseased || dead || watered || (location.type != PatchType.ALLOTMENT && location.type != PatchType.FLOWER && location.type != PatchType.HOP && location.type != PatchType.VINE_FLOWER)) {
				player.sendMessage("That patch doesn't need watering.");
				return;
			}
			player.setNextAnimation(WATERING_ANIMATION);
			watered = true;
			item.setId(item.getId() == 5333 ? 5331 : item.getId() - 1);
			player.getInventory().refresh();
			updateVars(player);
			break;
		}
	}

	public void updateVars(Player player) {
		player.getVars().setVarBit(location.varBit, location.type.getValue(this));
	}

	public void promptClear(Player player) {
		player.sendOptionDialogue("Do you really want to clear the patch?", ops -> {
			ops.add("Yes, I'd like to clear it.", () -> {
				player.setNextAnimation(SPADE_ANIMATION);
				if (seed != null && seed.type == PatchType.TREE && lives == -1)
					player.getInventory().addItemDrop(seed.productId.getId(), 1);
				empty();
				updateVars(player);
			});
			ops.add("No thanks.");
		});
	}

	public void setInitialLives() {
		if (seed == null)
			return;
		switch(seed.type) {
		case CACTUS:
			lives = 3;
			break;
		case FRUIT_TREE:
		case CALQUAT:
			lives = 6;
			break;
		case ALLOTMENT:
		case HERB:
		case HOP:
		case VINE_HERB:
			lives = 3 + compostLevel;
			break;
		case MUSHROOM:
			lives = seed.productId.getAmount();
			break;
		case BUSH:
		case VINE_BUSH:
			lives = 4;
			break;
		case FLOWER:
		case VINE_FLOWER:
		case BELLADONNA:
		case EVIL_TURNIP:
			lives = 1;
			break;
		case COMPOST:
			lives = 15;
			break;
		default:
			break;
		}
	}

	public void tick(Player player) {
		if (location.type != PatchType.COMPOST) {
			if (weeds >= 3) {
				seed = null;
				return;
			}
			if (seed == null) {
				weeds++;
				return;
			}
		} else if (seed == null)
			return;
		if (dead)
			return;
		totalGrowthTicks++;
		if (fullyGrown()) {
			switch (seed.type) {
				case BUSH:
				case CACTUS:
				case VINE_BUSH:
					if (totalGrowthTicks % 2 == 0 && checkedHealth && lives < (seed.type == PatchType.CACTUS ? 3 : 4))
						lives++;
					break;
				case CALQUAT:
				case FRUIT_TREE:
					if (totalGrowthTicks % 9 == 0 && checkedHealth && lives < 6)
						lives++;
					break;
				case TREE:
					if (seed == ProduceType.Willow) {
						if (checkedHealth && lives < 6)
							lives++;
					} else if (checkedHealth && lives < 0)
						lives++;
					break;
				default:
					break;
			}
		}
		if (totalGrowthTicks % seed.type.getGrowthTicksPerStage() == 0) {
			if (fullyGrown())
				return;
			if (diseased) {
				if (Math.random() < 0.5)
					dead = true;
				return;
			}
			boolean fullGrownBefore = fullyGrown();
			growthStage++;
			boolean hasFullyGrown = !fullGrownBefore && fullyGrown();
			if (hasFullyGrown)
				setInitialLives();
			if (!fullyGrown() && Utils.random(128) < (6 - compostLevel))
				if (!hasFullyGrown && !isDiseaseProtected(player)) {
					diseased = true;
					return;
				}
			watered = false;
		}
	}

	public boolean isDiseaseProtected(Player player) {
		if (seed == null|| watered || diseaseProtected || fullyGrown())
			return true;
		if ((seed == ProduceType.Poison_ivy) || seed == ProduceType.Evil_turnip || location.type == PatchType.COMPOST || location == PatchLocation.Trollheim_herbs || location == PatchLocation.Burthorpe_potato_patch)
			return true;
		if (location == PatchLocation.Canifis_mushrooms && SetReward.MORYTANIA_LEGS.hasRequirements(player, AchievementDef.Area.MORYTANIA, AchievementDef.Difficulty.ELITE, false))
			return true;
		if (seed.type == PatchType.ALLOTMENT) {
			ProduceType flower = seed.getFlowerProtection();
			if (flower == null)
				return false;
			switch(location) {
			case Ardougne_allotment_north:
			case Ardougne_allotment_south:
				if (player.isGrowing(PatchLocation.Ardougne_flower, flower) || player.isGrowing(PatchLocation.Ardougne_flower, ProduceType.White_lily))
					return true;
				break;
			case Canifis_allotment_north:
			case Canifis_allotment_south:
				if (player.isGrowing(PatchLocation.Canifis_flower, flower) || player.isGrowing(PatchLocation.Canifis_flower, ProduceType.White_lily))
					return true;
				break;
			case Catherby_allotment_north:
			case Catherby_allotment_south:
				if (player.isGrowing(PatchLocation.Catherby_flower, flower) || player.isGrowing(PatchLocation.Catherby_flower, ProduceType.White_lily))
					return true;
				break;
			case Falador_allotment_north:
			case Falador_allotment_south:
				if (player.isGrowing(PatchLocation.Falador_flower, flower) || player.isGrowing(PatchLocation.Falador_flower, ProduceType.White_lily))
					return true;
				break;
			default:
				break;
			}
		}
		return false;
	}

	public void empty() {
		seed = null;
		weeds = 0;
		diseased = false;
		dead = false;
		compostLevel = 0;
		lives = 0;
		checkedHealth = false;
		totalGrowthTicks = 0;
		watered = false;
		growthStage = 0;
		diseaseProtected = location == PatchLocation.Trollheim_herbs;
	}

	public boolean needsRemove() {
		return seed == null && weeds >= 3 && compostLevel == 0;
	}

	public boolean fullyGrown() {
		if (seed == null)
			return false;
		return growthStage == seed.stages;
	}

	public static ObjectClickHandler handlePatches = new ObjectClickHandler(PatchLocation.MAP.keySet().toArray(), e -> {
		PatchLocation loc = PatchLocation.forObject(e.getObjectId());
		if (loc == null)
			return;

		FarmPatch patch = e.getPlayer().getPatch(loc);
		if (patch == null)
			patch = new FarmPatch(loc);
		patch.handleClick(e.getPlayer(), e.getObject(), e.getOption(), e.getOpNum());
		e.getPlayer().putPatch(patch);
	});

	public static ItemOnObjectHandler handleItemOnPatch = new ItemOnObjectHandler(PatchLocation.MAP.keySet().toArray(), null, e -> {
		PatchLocation loc = PatchLocation.forObject(e.getObjectId());
		if (loc == null)
			return;

		FarmPatch patch = e.getPlayer().getPatch(loc);
		if (patch == null)
			patch = new FarmPatch(loc);
		patch.useItem(e.getPlayer(), e.getObject(), e.getItem());
		e.getPlayer().putPatch(patch);
	});
	
	@Override
	public String toString() {
		return "{ loc: " + location + ", seed: " + seed + ", weeds: " + weeds + ", growth: " + growthStage + ", totalGrow: " + totalGrowthTicks + ", disease: " + diseased + ", water: " + watered + ", dead: " + dead + ", prot: " + diseaseProtected + ", compost: " + compostLevel + ", lives: " + lives + ", checked: " + checkedHealth + " }";
	}
}