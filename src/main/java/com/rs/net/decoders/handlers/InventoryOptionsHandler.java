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
package com.rs.net.decoders.handlers;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.Lamps;
import com.rs.game.content.minigames.fightkiln.FightKilnController;
import com.rs.game.content.minigames.sorcgarden.SorceressGardenController;
import com.rs.game.content.skills.cooking.CookingCombos;
import com.rs.game.content.skills.cooking.FruitCutting.CuttableFruit;
import com.rs.game.content.skills.cooking.FruitCuttingD;
import com.rs.game.content.skills.crafting.GemCutting;
import com.rs.game.content.skills.crafting.GemCutting.Gem;
import com.rs.game.content.skills.crafting.GemTipCutting;
import com.rs.game.content.skills.crafting.GemTipCutting.GemTips;
import com.rs.game.content.skills.dungeoneering.DungeonRewards;
import com.rs.game.content.skills.farming.TreeSaplings;
import com.rs.game.content.skills.firemaking.Firemaking;
import com.rs.game.content.skills.fletching.Fletching;
import com.rs.game.content.skills.fletching.Fletching.Fletch;
import com.rs.game.content.skills.fletching.FletchingD;
import com.rs.game.content.skills.herblore.CoconutCracking;
import com.rs.game.content.skills.herblore.HerbCleaning;
import com.rs.game.content.skills.magic.Lunars;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.prayer.Burying.Bone;
import com.rs.game.content.skills.prayer.PrayerBooks;
import com.rs.game.content.skills.runecrafting.Runecrafting;
import com.rs.game.content.skills.runecrafting.RunecraftingAltar.WickedHoodRune;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.content.transportation.ItemTeleports;
import com.rs.game.content.world.unorganized_dialogue.DestroyItem;
import com.rs.game.content.world.unorganized_dialogue.FlowerPickup;
import com.rs.game.content.world.unorganized_dialogue.LeatherCraftingD;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.DropItemEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.utils.DropSets;
import com.rs.utils.ItemConfig;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropTable;

public class InventoryOptionsHandler {
		
	public static boolean handleItemOnItem(Player player, Item used, Item usedWith, int fromSlot, int toSlot) {
		int usedId = used.getId(), usedWithId = usedWith.getId();

		if (!player.getControllerManager().canUseItemOnItem(used, usedWith))
			return false;

		if (PrayerBooks.isGodBook(usedId, false) || PrayerBooks.isGodBook(usedWithId, false)) {
			PrayerBooks.bindPages(player, used.getName().contains(" page ") ? usedWithId : usedId);
			return true;
		}

		if (CookingCombos.handleCombos(player, used, usedWith))
			return true;

		if (TreeSaplings.hasSaplingRequest(player, usedId, usedWithId)) {
			if (usedId == 5354)
				TreeSaplings.plantSeed(player, usedWithId, fromSlot);
			else
				TreeSaplings.plantSeed(player, usedId, toSlot);
			return true;
		}

		if (usedWithId == 22332) {
			WickedHoodRune rune = null;
			for (WickedHoodRune r : WickedHoodRune.values())
				if (r.getTalismanId() == usedId || r.getTiaraId() == usedId)
					rune = r;
			if (rune != null) {
				if (player.hasWickedHoodTalisman(rune)) {
					//failsafe check for players who manually use all elemental runes so they can still use the talisman to unlock pure ess.
					if (usedId == WickedHoodRune.ELEMENTAL.getTalismanId() && !player.getUsedElementalTalisman()) {
						player.getInventory().deleteItem(usedId, 1);
						player.setUsedElementalTalisman(true);
						player.sendMessage("You unlock the ability to receive pure essence from the wicked hood.");
						return true;
					}
					player.sendMessage("The hood doesn't appear to be interested in that anymore.");
					return true;
				}
				player.getInventory().deleteItem(usedId, 1);
				player.getSkills().addXp(Constants.RUNECRAFTING, 50);
				if (usedId == WickedHoodRune.OMNI.getTalismanId()) {
					for (WickedHoodRune r : WickedHoodRune.values())
						player.unlockWickedHoodRune(r);
					player.setUsedOmniTalisman(true);
				} else if (usedId == WickedHoodRune.ELEMENTAL.getTalismanId()) {
					player.unlockWickedHoodRune(WickedHoodRune.AIR);
					player.unlockWickedHoodRune(WickedHoodRune.WATER);
					player.unlockWickedHoodRune(WickedHoodRune.EARTH);
					player.unlockWickedHoodRune(WickedHoodRune.FIRE);
					player.setUsedElementalTalisman(true);
					player.sendMessage("You unlock the ability to receive pure essence from the wicked hood.");
				}
				player.unlockWickedHoodRune(rune);
			}
			return true;
		}

		if ((usedId == 7225 && usedWithId == 9978) || (usedId == 1391 && usedWithId == 9978)) {
			player.getInventory().deleteItem(7225, 1);
			player.getInventory().deleteItem(9978, 1);
			player.getInventory().addItem(9984, 1);
			return true;
		}

		if (usedId == 1759 && Lunars.getStrungIndex(usedWithId) != -1)
			if (player.getInventory().containsItem(1759, 1) && player.getInventory().containsItem(usedWithId, 1)) {
				player.getInventory().deleteItem(used.getId(), 1);
				player.getInventory().deleteItem(usedWith.getId(), 1);
				player.getInventory().addItem(Lunars.strung[Lunars.getStrungIndex(usedWithId)], 1);
				return true;
			}

		if ((usedId == 21775 && usedWithId == 1391) || (usedId == 1391 && usedWithId == 21775)) {
			if (player.getSkills().getLevel(Constants.CRAFTING) >= 77) {
				if (player.getInventory().containsItem(21775, 1) && player.getInventory().containsItem(1391, 1)) {
					player.getInventory().deleteItem(21775, 1);
					player.getInventory().deleteItem(1391, 1);
					player.getSkills().addXp(Constants.CRAFTING, 150);
					player.getInventory().addItem(21777, 1);
					player.sendMessage("You fuse the orb with the battlestaff.");
				}
			} else
				player.sendMessage("You need 77 crafting to create an armadyl battlestaff.");
			return true;
		}

		if ((usedId >= 20121 && usedId <= 20124) || (usedWithId >= 20121 && usedWithId <= 20124)) {
			if (player.getInventory().containsItem(20121, 1) && player.getInventory().containsItem(20122, 1) && player.getInventory().containsItem(20123, 1) && player.getInventory().containsItem(20124, 1)) {
				player.getInventory().deleteItem(20121, 1);
				player.getInventory().deleteItem(20122, 1);
				player.getInventory().deleteItem(20123, 1);
				player.getInventory().deleteItem(20124, 1);
				player.getInventory().addItem(new Item(20120, 1).addMetaData("frozenKeyCharges", 6.0));
			} else
				player.sendMessage("You need all 4 peices to create a frozen key.");
			return true;
		}

		if (usedId == 12435 && player.getFamiliarPouch() == Pouch.PACK_YAK) {
			usedWith.setSlot(usedWith.getSlot());
			player.getFamiliar().castSpecial(usedWith);
			return true;
		}

		if (usedWith.getId() == 946 || used.getId() == 946) {
			CuttableFruit fruit = CuttableFruit.forId(used.getId());
			if (fruit != null && usedWith.getId() == 946) {
				player.startConversation(new FruitCuttingD(player, fruit));
				return true;
			}

			fruit = CuttableFruit.forId(usedWith.getId());
			if (fruit != null && used.getId() == 946) {
				player.startConversation(new FruitCuttingD(player, fruit));
				return true;
			}
		}

		Fletch fletch = Fletching.isFletching(usedWith, used);
		if (fletch != null) {
			player.startConversation(new FletchingD(player, fletch));
			return true;
		}
		int leatherIndex = LeatherCraftingD.getIndex(usedId) == -1 ? LeatherCraftingD.getIndex(usedWith.getId()) : LeatherCraftingD.getIndex(usedId);
		if (leatherIndex != -1 && ((usedId == 1733 || usedWith.getId() == 1733) || LeatherCraftingD.isExtraItem(usedWith.getId()) || LeatherCraftingD.isExtraItem(usedId))) {
			player.startConversation(new LeatherCraftingD(player, leatherIndex));
			return true;
		}
		if (Firemaking.isFiremaking(player, used, usedWith) || GemCutting.isCutting(player, used, usedWith))
			return true;
		if (contains(1755, Gem.OPAL.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.OPAL);
		else if (contains(1755, Gem.JADE.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.JADE);
		else if (contains(1755, Gem.RED_TOPAZ.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.RED_TOPAZ);
		else if (contains(1755, Gem.SAPPHIRE.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.SAPPHIRE);
		else if (contains(1755, Gem.EMERALD.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.EMERALD);
		else if (contains(1755, Gem.RUBY.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.RUBY);
		else if (contains(1755, Gem.DIAMOND.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.DIAMOND);
		else if (contains(1755, Gem.DRAGONSTONE.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.DRAGONSTONE);
		else if (contains(1755, Gem.ONYX.getCut(), used, usedWith))
			GemTipCutting.cut(player, GemTips.ONYX);
		else if (PluginManager.handle(new ItemOnItemEvent(player, used.setSlot(fromSlot), usedWith.setSlot(toSlot))))
			return true;
		Logger.debug(InventoryOptionsHandler.class, "handleItemOnItem", "ItemOnItem " + used.getId() + " -> " + usedWith.getId());
		return false;
	}
	
	public static void handleItemOption1(Player player, final int slotId, final int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isAnimating())
			return;
		player.stopAll(false);
		if (PluginManager.handle(new ItemClickEvent(player, item, slotId, item.getDefinitions().getInventoryOption(0))))
			return;
		if (itemId == 4155) {
			player.getSlayer().speakToMaster(player, null);
			return;
		}
		if (itemId == CoconutCracking.COCONUT)
			if (player.getInventory().containsItem(CoconutCracking.HAMMER)) {
				player.getInventory().deleteItem(CoconutCracking.COCONUT, 1);
				player.getInventory().addItem(CoconutCracking.OPEN_COCONUT, 1);
				player.sendMessage("You break the coconut open with the hammer.");
			} else
				player.sendMessage("You need a hammer to break this open.");
		if (Lamps.isSelectable(itemId) || Lamps.isSkillLamp(itemId) || Lamps.isOtherSelectableLamp(itemId))
			Lamps.processLampClick(player, slotId, itemId);
		if (item.getId() == 405) {
			Item[] loot = DropTable.calculateDrops(player, DropSets.getDropSet("fishing_casket"));
			player.getInventory().deleteItem(405, 1);
			for (Item l : loot)
				if (item != null)
					player.getInventory().addItem(l);
		}
		if (item.getId() == 20120) {
			player.sendMessage("Your key has " + (item.getMetaDataI("frozenKeyCharges")-1) + " uses left.");
			return;
		}
		if (item.getId() == 20667) {
			player.stopAll(false);
			long lastVecna = player.getTempAttribs().getL("LAST_VECNA");
			if (lastVecna != -1 && lastVecna + 420000 > System.currentTimeMillis()) {
				player.sendMessage("The skull has not yet regained " +
						"its mysterious aura. You will need to wait another " +
						(lastVecna != -1 && lastVecna + 60000 > System.currentTimeMillis() ? "7"
								: (lastVecna != -1 && lastVecna + 120000 > System.currentTimeMillis() ? "6"
										: (lastVecna != -1 && lastVecna + 180000 > System.currentTimeMillis() ? "5"
												: (lastVecna != -1 && lastVecna + 240000 > System.currentTimeMillis() ? "4"
														: (lastVecna != -1 && lastVecna + 300000 > System.currentTimeMillis() ? "3"
																: (lastVecna != -1 && lastVecna + 360000 > System.currentTimeMillis() ? "2"
																		: "1")))))) + " minutes.");
				return;
			}
			player.getTempAttribs().setL("LAST_VECNA", System.currentTimeMillis());
			player.setNextSpotAnim(new SpotAnim(738, 0, 100));
			player.setNextAnimation(new Animation(10530));
			player.sendMessage("The skull feeds off the life around you, boosting your magical ability.");
			int actualLevel = player.getSkills().getLevel(Constants.MAGIC);
			int realLevel = player.getSkills().getLevelForXp(Constants.MAGIC);
			int level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Constants.MAGIC, level + 6);
			return;
		}

		if (item.getId() == 19675) {
			DungeonRewards.openHerbSelection(player);
			return;
		}

		if (itemId == 21776) {
			if (player.getInventory().containsItem(21776, 100)) {
				player.getInventory().deleteItem(21776, 100);
				player.getInventory().addItem(21775, 1);
				player.sendMessage("You combine the shards into an orb.");
			} else
				player.sendMessage("You need 100 shards to create an orb.");
			return;
		}
		if (itemId == 299) {
			if (player.isLocked())
				return;
			if (World.getObject(Tile.of(player.getTile()), ObjectType.SCENERY_INTERACT) != null) {
				player.sendMessage("You cannot plant flowers here..");
				return;
			}
			final double random = Utils.random(100.0);
			final Tile tile = Tile.of(player.getTile());
			int flower = Utils.random(2980, 2987);
			if (random < 0.2)
				flower = Utils.random(2987, 2989);
			if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
				if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
					if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1))
						player.addWalkSteps(player.getX(), player.getY() - 1, 1);
			player.getInventory().deleteItem(299, 1);
			final GameObject flowerObject = new GameObject(2987, ObjectType.SCENERY_INTERACT, Utils.getRandomInclusive(4), tile.getX(), tile.getY(), tile.getPlane());
			final int flowerId = flower;
			World.spawnObjectTemporary(flowerObject, Ticks.fromSeconds(45));
			player.lock();
			WorldTasks.schedule(new Task() {
				int step;

				@Override
				public void run() {
					if (player == null || player.hasFinished())
						stop();
					if (step == 1) {
						player.startConversation(new FlowerPickup(player, flowerObject, flowerId));
						player.setNextFaceTile(tile);
						player.unlock();
						stop();
					}
					step++;
				}
			}, 0, 0);
		}

		if (itemId >= 2520 && itemId <= 2526) {
			String[] phrases = { "Come on Dobbin, we can win the race!", "Hi-ho Silver, and away!", "Neaahhhyyy! Giddy-up horsey!" };
			player.setNextAnimation(new Animation(918+((itemId-2520)/2)));
			player.setNextForceTalk(new ForceTalk(phrases[Utils.random(phrases.length)]));
			return;
		}

		if (itemId == 18336) {
			player.hasScrollOfLife = true;
			player.getInventory().deleteItem(18336, 1);
			player.sendMessage("The secret is yours! You read the scroll and unlock the long lost technique of regaining seeds from dead farming patches.");
			return;
		}

		if (itemId == 19890) {
			player.hasScrollOfCleansing = true;
			player.getInventory().deleteItem(19890, 1);
			player.sendMessage("You read the scroll and unlock the ability to save herblore ingredients!");
			return;
		}

		if (itemId == 19670) {
			player.hasScrollOfEfficiency = true;
			player.getInventory().deleteItem(19670, 1);
			player.sendMessage("You read the scroll and unlock the ability to save bars when smithing!");
			return;
		}

		if (itemId == 18344) {
			player.hasAugury = true;
			player.getInventory().deleteItem(18344, 1);
			player.sendMessage("You read the scroll and unlock the ability to use the Augury prayer!");
			return;
		}

		if (itemId == 18839) {
			player.hasRigour = true;
			player.getInventory().deleteItem(18839, 1);
			player.sendMessage("You read the scroll and unlock the ability to use the Rigour prayer!");
			return;
		}

		if (itemId == 18343) {
			player.hasRenewalPrayer = true;
			player.getInventory().deleteItem(18343, 1);
			player.sendMessage("You read the scroll and unlock the ability to use the Rapid renewal prayer!");
			return;
		}
		if (HerbCleaning.clean(player, item, slotId))
			return;
		if (Lamps.isSelectable(itemId) || Lamps.isSkillLamp(itemId) || Lamps.isOtherSelectableLamp(itemId)) {
			Lamps.processLampClick(player, slotId, itemId);
			return;
		}

		Bone bone = Bone.forId(itemId);
		if (bone != null) {
			Bone.bury(player, slotId);
			return;
		}
		if (ItemTeleports.transportationDialogue(player, item))
			return;
		if (itemId == 19967) {
			if (Magic.sendTeleportSpell(player, 7082, 7084, 1229, 1229, 1, 0, Tile.of(2952, 2933, 0), 4, true, Magic.ITEM_TELEPORT, null))
				player.getInventory().deleteItem(19967, 1);
			return;
		}
		if (itemId >= 23653 && itemId <= 23658)
			FightKilnController.useCrystal(player, itemId);
		else if (player.getTreasureTrailsManager().useItem(item, slotId))
			return;
		else if (itemId == 2574)
			player.getTreasureTrailsManager().useSextant();
		else if (itemId == 2798 || itemId == 3565 || itemId == 3576 || itemId == 19042)
			player.getTreasureTrailsManager().openPuzzle(itemId);
		else if (item.getDefinitions().getName().startsWith("Burnt"))
			player.simpleDialogue("Ugh, this is inedible.");
		if (player.hasRights(Rights.DEVELOPER))
			player.sendMessage("ItemOption1: item: " + itemId + ", slotId: " + slotId);
	}
	
	public static void handleItemOption2(final Player player, final int slotId, final int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isAnimating() || PluginManager.handle(new ItemClickEvent(player, item, slotId, item.getDefinitions().getInventoryOption(1))) || Firemaking.isFiremaking(player, itemId))
			return;
	}

	public static void handleItemOption3(Player player, int slotId, int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isAnimating() || PluginManager.handle(new ItemClickEvent(player, item, slotId, item.getDefinitions().getInventoryOption(2))))
			return;
		player.stopAll(false);
		if (item.getDefinitions().isBindItem())
			player.getDungManager().bind(item, slotId);
		else if (item.getDefinitions().containsOption("Teleport") && ItemTeleports.transportationDialogue(player, item))
			return;
		if (player.hasRights(Rights.DEVELOPER))
			player.sendMessage("ItemOption3: item: " + itemId + ", slotId: " + slotId);
	}

	public static void handleItemOption4(Player player, int slotId, int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isAnimating())
			return;
	}

	public static void handleItemOption5(Player player, int slotId, int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isAnimating())
			return;
	}

	public static void handleItemOption6(Player player, int slotId, int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isAnimating() || PluginManager.handle(new ItemClickEvent(player, item, slotId, item.getDefinitions().getInventoryOption(3))))
			return;
		player.stopAll(false);

		if (itemId == 1438)
			Runecrafting.locate(player, 3127, 3405);
		else if (itemId == 1440)
			Runecrafting.locate(player, 3306, 3474);
		else if (itemId == 1442)
			Runecrafting.locate(player, 3313, 3255);
		else if (itemId == 1444)
			Runecrafting.locate(player, 3185, 3165);
		else if (itemId == 1446)
			Runecrafting.locate(player, 3053, 3445);
		else if (itemId == 1448)
			Runecrafting.locate(player, 2982, 3514);
		else if ((item.getDefinitions().containsOption("Rub") || item.getDefinitions().containsOption("Cabbage-port")) && ItemTeleports.transportationDialogue(player, item))
			return;
		else if (itemId >= 8901 && itemId <= 8920 && !ItemDefinitions.getDefs(itemId).isNoted()) {
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().addItem(8921, 1);
		}
		else if (itemId == 14057)
			SorceressGardenController.teleportToSocreressGarden(player, true);
		if (player.hasRights(Rights.DEVELOPER))
			player.sendMessage("ItemOption6: item: " + itemId + ", slotId: " + slotId);
	}

	public static void handleItemOption7(Player player, int slotId, int itemId, Item item) {
		if (player.isLocked() || player.getEmotesManager().isAnimating() || !player.getBank().checkPin() || !player.getControllerManager().canDropItem(item))
			return;
		if (item.getDefinitions().isDestroyItem()) {
			player.startConversation(new DestroyItem(player, slotId, item));
			return;
		}
		if (PluginManager.handle(new ItemClickEvent(player, item, slotId, item.getDefinitions().getInventoryOption(4))))
			return;
		player.stopAll(false);
		if (player.getPetManager().spawnPet(itemId, true))
			return;
		DropItemEvent event = new DropItemEvent(player, item);
		PluginManager.handle(event);
		if (event.dropCancelled())
			return;
		player.getInventory().deleteItem(slotId, item);
		World.addGroundItem(item, Tile.of(player.getTile()), player);
		player.soundEffect(ItemConfig.get(item.getId()).getDropSound());
	}

	public static void handleItemOption8(Player player, int slotId, int itemId, Item item) {
		player.getInventory().sendExamine(slotId);
	}
	
	public static Item contains(int id1, Item item1, Item item2) {
		if (item1.getId() == id1)
			return item2;
		if (item2.getId() == id1)
			return item1;
		return null;
	}

	public static boolean contains(int id1, int id2, Item... items) {
		boolean containsId1 = false;
		boolean containsId2 = false;
		for (Item item : items)
			if (item.getId() == id1)
				containsId1 = true;
			else if (item.getId() == id2)
				containsId2 = true;
		return containsId1 && containsId2;
	}
}