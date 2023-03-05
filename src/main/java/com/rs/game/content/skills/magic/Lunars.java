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
package com.rs.game.content.skills.magic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.rs.game.World;
import com.rs.game.content.AchievementTitles;
import com.rs.game.content.items.liquid_containers.FillAction.Filler;
import com.rs.game.content.skills.construction.SawmillOperator;
import com.rs.game.content.skills.farming.FarmPatch;
import com.rs.game.content.skills.farming.PatchLocation;
import com.rs.game.content.skills.farming.PatchType;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class Lunars {

	public static int[] unstrung = { 1673, 1675, 1677, 1679, 1681, 1683, 1714, 1720, 6579 };
	public static int[] strung = { 1692, 1694, 1696, 1698, 1700, 1702, 1716, 1722, 6581 };

	public static Player[] getNearPlayers(Player player, int distance, int maxTargets) {
		List<Entity> possibleTargets = new ArrayList<>();
		for (Player p2 : player.queryNearbyPlayersByTileRange(distance, p2 -> !p2.isDead() && p2 != player && p2.withinDistance(player.getTile(), distance))) {
			possibleTargets.add(p2);
			if (possibleTargets.size() == maxTargets)
				break;
		}
		return possibleTargets.toArray(new Player[possibleTargets.size()]);
	}

	public static boolean hasUnstrungs(Player player) {
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			if (getStrungIndex(item.getId()) != -1)
				return true;
		}
		return false;
	}

	public static int getStrungIndex(int ammy) {
		for (int i = 0; i < unstrung.length; i++)
			if (unstrung[i] == ammy)
				return i;
		return -1;
	}

	public static int getPlankIdx(int logId) {
		for (int i = 0; i < SawmillOperator.logs.length; i++)
			if (SawmillOperator.logs[i] == logId)
				return i;
		return -1;
	}

	public static ButtonClickHandler handleRemoteFarmButtons = new ButtonClickHandler(1082, e -> {
		if (e.getPacket() == ClientPacket.IF_OP1)
			if (e.getPlayer().getTempAttribs().getB("RemoteFarm")) {
				//					int[] names = new int[] { 30, 32, 34, 36, 38, 49, 51, 53, 55, 57, 59, 62, 64, 66, 68, 70, 72, 74, 76, 190, 79, 81, 83, 85, 88, 90, 92, 94, 97, 99, 101, 104, 106, 108, 110, 115, 117, 119, 121, 123, 125, 131, 127, 129, 2, 173, 175, 177, 182, 184, 186, 188 };
				//					for (int i = 0; i < names.length; i++) {
				//						if ((names[i]+1) == e.getComponentId()) {
				//							if (e.getPlayer().getFarming().patches[i] != null) {
				//								if (e.getPlayer().getFarming().patches[i].diseased) {
				//									e.getPlayer().getFarming().patches[i].diseased = false;
				//									refreshRemoteFarm(e.getPlayer());
				//								} else {
				//									e.getPlayer().sendMessage("This patch isn't diseased.");
				//								}
				//							}
				//						}
				//					}
			} else
				AchievementTitles.handleButtons(e.getPlayer(), e.getComponentId());
	});

	public static void openRemoteFarm(Player player) {
		if (!player.canCastSpell()) {
			player.sendMessage("There is a 5 second delay to this.");
			return;
		}
		player.addSpellDelay(10);
		player.getTempAttribs().setB("RemoteFarm", true);
		player.getInterfaceManager().sendInterface(1082);
		refreshRemoteFarm(player);
	}

	public static void refreshRemoteFarm(Player player) {
		//		if (!player.getInterfaceManager().containsInterface(1082) || player.getTemporaryAttributes().get("RemoteFarm") == null)
		//			return;
		//		Patch patch = null;
		//		int[] names = new int[] { 30, 32, 34, 36, 38, 49, 51, 53, 55, 57, 59, 62, 64, 66, 68, 70, 72, 74, 76, 190, 79, 81, 83, 85, 88, 90, 92, 94, 97, 99, 101, 104, 106, 108, 110, 115, 117, 119, 121, 123, 125, 131, 127, 129, 2, 173, 175, 177, 182,
		//				184, 186, 188 };
		//
		//		for (int i = 0; i < names.length; i++) {
		//			if (i < PatchConstants.WorldPatches.values().length) {
		//				player.getPackets().sendIComponentText(1082, names[i], PatchConstants.WorldPatches.values()[i].name().replace("_", " ").toLowerCase());
		//			} else {
		//				player.getPackets().sendIComponentText(1082, names[i], "");
		//			}
		//		}
		//		for (int i = 0; i < names.length; i++) {
		//			if (i < player.getFarming().patches.length) {
		//				patch = player.getFarming().patches[i];
		//				if (patch != null) {
		//					if (!patch.raked) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "Full of weeds");
		//					} else if (patch.dead) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "<col=8f13b5>Is dead!");
		//					} else if (patch.diseased) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "<col=FF0000>Is disased!");
		//					} else if (patch.healthChecked) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "<col=00FF00>Is ready for health check");
		//					} else if (patch.grown && patch.yield > 0) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "<col=00FF00>Is fully grown with produce available");
		//					} else if (patch.grown) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "<col=00FF00>Is fully grown with no produce available");
		//					} else if (patch.currentSeed != -1) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "Is growing healthy.");
		//					} else if (patch.raked) {
		//						player.getPackets().sendIComponentText(1082, names[i] + 1, "Is empty");
		//					}
		//				} else {
		//					player.getPackets().sendIComponentText(1082, names[i] + 1, "");
		//				}
		//			} else {
		//				player.getPackets().sendIComponentText(1082, names[i] + 1, "");
		//			}
		//		}
	}

	public static void handlePlankMake(Player player, Item item) {
		player.getInterfaceManager().openTab(Sub.TAB_MAGIC);
		if (!player.canCastSpell())
			return;
		int index = getPlankIdx(item.getId());
		if (index == -1) {
			player.sendMessage("You can only cast this spell on a log.");
			return;
		}
		
		int price = (int) (SawmillOperator.prices[index] * 0.7);
		
		if (!player.getInventory().hasCoins(price)) {
			player.sendMessage("You need " + Utils.formatNumber(price) + " gold to convert this log.");
			return;
		}

		if (!player.getInventory().containsItem(SawmillOperator.logs[index], 1) || !Magic.checkMagicAndRunes(player, 86, true, new RuneSet(Rune.NATURE, 1, Rune.ASTRAL, 2, Rune.EARTH, 15)))
			return;

		player.setNextAnimation(new Animation(6298));
		player.setNextSpotAnim(new SpotAnim(1063, 0, 50));
		player.getInventory().removeCoins(price);
		player.getInventory().deleteItem(SawmillOperator.logs[index], 1);
		player.getInventory().addItem(SawmillOperator.planks[index], 1);
		player.getSkills().addXp(Constants.MAGIC, 90);
		player.addSpellDelay(2);
	}

	public static void handleVengeance(Player player) {
		long lastVeng = player.getTempAttribs().getL("LAST_VENG");
		if (lastVeng != -1 && lastVeng + 30000 > System.currentTimeMillis()) {
			player.sendMessage("You may only cast vengeance once every 30 seconds.");
			return;
		}
		if (!Magic.checkMagicAndRunes(player, 94, true, new RuneSet(Rune.ASTRAL, 4, Rune.DEATH, 2, Rune.EARTH, 10)))
			return;
		player.setNextSpotAnim(new SpotAnim(726, 0, 100));
		player.setNextAnimation(new Animation(4410));
		player.setCastVeng(true);
		player.getSkills().addXp(Constants.MAGIC, 112);
		player.getTempAttribs().setL("LAST_VENG", System.currentTimeMillis());
	}

	public static void handleHumidify(Player player) {
		if (hasFillables(player)) {
			if (Magic.checkMagicAndRunes(player, 68, true, new RuneSet(Rune.ASTRAL, 1, Rune.WATER, 3, Rune.FIRE, 1))) {
				player.setNextSpotAnim(new SpotAnim(1061));
				player.setNextAnimation(new Animation(6294));
				player.getSkills().addXp(Constants.MAGIC, 65);
				fillFillables(player);
			}
		} else
			player.sendMessage("You need to have something to humidify before using this spell.");
	}

	public static void fillFillables(Player player) {
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			Filler fill = Filler.forEmpty((short) item.getId());
			if (fill != null)
				if (player.getInventory().containsItem(fill.getEmptyItem().getId(), 1)) {
					player.getInventory().deleteItem(fill.getEmptyItem());
					player.getInventory().addItem(fill.getFilledItem());
				}
		}
	}

	public static boolean hasFillables(Player player) {
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			Filler fill = Filler.forEmpty((short) item.getId());
			if (fill != null)
				return true;
		}
		return false;
	}

	public static void handleStringJewelry(Player player) {
		if (hasUnstrungs(player)) {
			if (Magic.checkMagicAndRunes(player, 80, true, new RuneSet(Rune.ASTRAL, 2, Rune.EARTH, 10, Rune.WATER, 5))) {
				player.setNextSpotAnim(new SpotAnim(728, 0, 100));
				player.setNextAnimation(new Animation(4412));
				player.getSkills().addXp(Constants.MAGIC, 87);
				for (Item item : player.getInventory().getItems().array()) {
					if (item == null)
						continue;
					int strungId = getStrungIndex(item.getId());
					if (strungId != -1) {
						player.getInventory().deleteItem(item.getId(), 1);
						player.getInventory().addItem(strung[strungId], 1);
					}
				}
			}
		} else
			player.sendMessage("You need to have unstrung jewelry to cast this spell.");
	}

	public static void handleFertileSoil(Player player, GameObject object) {
		PatchLocation loc = PatchLocation.forObject(object.getId());
		if (loc == null) {
			player.sendMessage("Um...I don't want to fertilise that!");
			return;
		}
		if (loc.type == PatchType.COMPOST) {
			player.sendMessage("Composting the compost??");
			return;
		}
		FarmPatch spot = player.getPatch(loc);
		if (spot == null)
			spot = new FarmPatch(loc);
		if (spot.fullyGrown()) {
			player.sendMessage("Composting it isn't going to make it get any bigger.");
			return;
		}
		if (spot.compostLevel == 2) {
			player.sendMessage("This patch has already been treated with supercompost.");
			return;
		}
		if (!Magic.checkMagicAndRunes(player, 83, true, new RuneSet(Rune.ASTRAL, 3, Rune.EARTH, 15, Rune.NATURE, 2)))
			return;
		player.setNextFaceTile(object.getTile());
		player.getSkills().addXp(Constants.FARMING, 18);
		player.getSkills().addXp(Constants.MAGIC, 87);
		player.setNextAnimation(new Animation(4411));
		player.setNextSpotAnim(new SpotAnim(728, 0, 100));
		spot.compostLevel = 2;
		player.putPatch(spot);
	}

	public static void handleCurePlant(Player player, GameObject object) {
		PatchLocation loc = PatchLocation.forObject(object.getId());
		if (loc == null) {
			player.sendMessage("There's nothing there to cure!");
			return;
		}
		FarmPatch spot = player.getPatch(loc);
		if (spot == null)
			spot = new FarmPatch(loc);
		if (spot.dead) {
			player.sendMessage("It says 'Cure' not 'Resurrect'. Although death may arise from disease, it is not in itself a disease and hence cannot be cured. So there.");
			return;
		}
		if (!spot.diseased) {
			player.sendMessage("It is growing just fine.");
			return;
		}
		if (!Magic.checkMagicAndRunes(player, 66, true, new RuneSet(Rune.ASTRAL, 1, Rune.EARTH, 8)))
			return;
		player.setNextFaceTile(object.getTile());
		player.getSkills().addXp(Constants.FARMING, 90);
		player.getSkills().addXp(Constants.MAGIC, 60);
		player.setNextAnimation(new Animation(4411));
		player.setNextSpotAnim(new SpotAnim(728, 0, 100));
		spot.diseased = false;
		spot.updateVars(player);
		player.lock(3);
	}

	public static void handleRestorePotionShare(Player player, Item item) {
		// TODO Auto-generated method stub

	}

	public static void handleLeatherMake(Player player, Item item) {
		// TODO Auto-generated method stub

	}

	public static void handleBoostPotionShare(Player player, Item item) {
		// TODO Auto-generated method stub

	}

	public static void handleBakePie(Player player) {
		// TODO Auto-generated method stub

	}

	public static void handleCureMe(Player player) {
		if (player.getPoison().isPoisoned()) {
			if (Magic.checkMagicAndRunes(player, 71, true, new RuneSet(Rune.ASTRAL, 2, Rune.COSMIC, 2))) {
				player.setNextSpotAnim(new SpotAnim(729, 0, 100));
				player.setNextAnimation(new Animation(4409));
				player.getSkills().addXp(Constants.MAGIC, 69);
				player.getPoison().reset();
			}
		} else
			player.sendMessage("You are not poisoned.");
	}

	public static void handleHunterKit(Player player) {
		// TODO Auto-generated method stub

	}

	public static void handleCureGroup(Player player) {
		if (!player.canCastSpell())
			return;
		if (Magic.checkMagicAndRunes(player, 74, true, new RuneSet(Rune.ASTRAL, 2, Rune.COSMIC, 2))) {
			player.getActionManager().addActionDelay(4);
			player.setNextSpotAnim(new SpotAnim(729, 0, 100));
			player.setNextAnimation(new Animation(4411));
			player.getPoison().reset();
			player.addSpellDelay(2);
			for (Player other : getNearPlayers(player, 1, 10))
				if (other.getPoison().isPoisoned()) {
					player.setNextSpotAnim(new SpotAnim(729, 0, 100));
					player.getPoison().reset();
					player.sendMessage("Your poison has been cured!");
				}
		}
	}

	public static void handleSuperGlassMake(Player player) {
		int secondary = (player.getInventory().containsItem(10978) ? 10978 : (player.getInventory().containsItem(1781)) ? 1781 : 401); //Swamp weed
		int number = Math.min(player.getInventory().getNumberOf(1783), player.getInventory().getNumberOf(secondary));
		if (number <= 0) {
			player.sendMessage("You need seaweed and buckets of sand to make molten glass.");
			return;
		}
		if (Magic.checkMagicAndRunes(player, 77, true, new RuneSet(Rune.ASTRAL, 2, Rune.FIRE, 6, Rune.AIR, 10))) {
			player.setNextSpotAnim(new SpotAnim(729, 0, 100));
			player.setNextAnimation(new Animation(4413));
			player.getSkills().addXp(Constants.MAGIC, 78);
			if (number > 0) {
				double chance = (number*1.30) - Math.floor(number*1.3);
				number *= 1.30;
				player.getInventory().deleteItem(secondary, number);
				player.getInventory().deleteItem(1783, number);
				player.getSkills().addXp(Constants.CRAFTING, 10*number);
				player.getInventory().addItem(1775, number);
				if ((chance > 0.0) && Utils.randomD() <= chance) {
					player.getInventory().addItem(1775, 1);
					player.getSkills().addXp(Constants.CRAFTING, 10);

				}
			}
		}
	}

	public static void handleRemoteFarm(Player player) {
		openRemoteFarm(player);
	}

	public static void handleDream(Player player) {
		// TODO Auto-generated method stub

	}

	public static void handleMagicImbue(Player player) {
		long lastImbue = player.getTempAttribs().getL("LAST_IMBUE");
		if (lastImbue != -1 && lastImbue + 12600 > System.currentTimeMillis()) {
			player.sendMessage("You may only cast magic imbue spells once every 12.6 seconds.");
			return;
		}
		if (Magic.checkMagicAndRunes(player, 82, true, new RuneSet(Rune.ASTRAL, 2, Rune.WATER, 7, Rune.FIRE, 7))) {
			player.setNextSpotAnim(new SpotAnim(141, 0, 100));
			player.setNextAnimation(new Animation(722));
			player.setCastMagicImbue(true);
			player.getSkills().addXp(Constants.MAGIC, 86);
			player.getTempAttribs().setL("LAST_IMBUE", System.currentTimeMillis());
		}
	}

	public static void handleDisruptionShield(Player player) {
		// TODO Auto-generated method stub

	}

	public static void handleGroupVengeance(Player player) {
		long lastVeng = player.getTempAttribs().getL("LAST_VENG");
		if (lastVeng != -1 && lastVeng + 30000 > System.currentTimeMillis()) {
			player.sendMessage("You may only cast vengeance spells once every 30 seconds.");
			return;
		}
		if (Magic.checkMagicAndRunes(player, 95, true, new RuneSet(Rune.ASTRAL, 4, Rune.DEATH, 3, Rune.EARTH, 11))) {
			player.setNextSpotAnim(new SpotAnim(725, 0, 100));
			player.setNextAnimation(new Animation(4411));
			player.setCastVeng(true);
			player.getSkills().addXp(Constants.MAGIC, 112);
			player.getTempAttribs().setL("LAST_VENG", System.currentTimeMillis());
			for (Player other : getNearPlayers(player, 3, 10)) {
				long otherVeng = other.getTempAttribs().getL("LAST_VENG");
				if (otherVeng != -1 && otherVeng + 30000 > System.currentTimeMillis())
					continue;
				other.setNextSpotAnim(new SpotAnim(725, 0, 100));
				other.setCastVeng(true);
				other.getTempAttribs().setL("LAST_VENG", System.currentTimeMillis());
			}
		}
	}

	public static void handleHealGroup(Player player) {
		// TODO Auto-generated method stub

	}

	public static void handleSpellbookSwap(Player player) {
		// TODO Auto-generated method stub

	}

}
