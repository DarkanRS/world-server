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
package com.rs.game.content.world.areas.morytania;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.TeleType;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Morytania  {

	public static NPCClickHandler handleHiylikMyna = new NPCClickHandler(new Object[] { 1514 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
					}
				});
			}
		});
	});

	public static NPCClickHandler handleRobin = new NPCClickHandler(new Object[] { 1694 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
					}
				});
			}
		});
	});

	public static NPCClickHandler handleStrangeOldManBarrows = new NPCClickHandler(new Object[] { 2024 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
					}
				});
			}
		});
	});

	public static NPCClickHandler handleOldManRal = new NPCClickHandler(new Object[] { 4708 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.MORYTANIA_LEGS).getStart());
					}
				});
			}
		});
	});

	public static ObjectClickHandler handleSlayerTowerChains = new ObjectClickHandler(new Object[] { 9319, 9320 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), e.getObject().getX() < 3447 ? 61 : 71))
			return;
		e.getPlayer().useStairs(828, e.getPlayer().transform(0, 0, e.getObjectId() == 9319 ? 1 : -1), 1, 2);
	});

	public static ObjectClickHandler handleLabEntrance = new ObjectClickHandler(new Object[] { 18049 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(3637, 9695, 0), 1, 1);
	});

	public static ObjectClickHandler handleLabExit = new ObjectClickHandler(new Object[] { 18050 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(3643, 3306, 0), 1, 1);
	});

	public static ObjectClickHandler handleBurghDeRottToMineFence = new ObjectClickHandler(new Object[] { 12776 }, e -> {
		AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 1 : -1, 0, 0), 2923);
	});

	public static ObjectClickHandler handleBrokenFence = new ObjectClickHandler(new Object[] { 18411 }, e -> {
		AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -1 : 1, 0));
	});

	public static ObjectClickHandler handleTempleTrapdoor = new ObjectClickHandler(new Object[] { 30572 }, e -> {
		e.getPlayer().ladder(Tile.of(3405, 9906, 0));
	});

	public static ObjectClickHandler handleTempleTrapdoorCanifisSide = new ObjectClickHandler(new Object[] { 30574 }, e -> {
		e.getPlayer().ladder(Tile.of(3440, 9887, 0));
	});

	public static ObjectClickHandler handleTempleLadder = new ObjectClickHandler(new Object[] { 30575 }, e -> {
		e.getPlayer().ladder(Tile.of(3405, 3506, 0));
	});

	public static ObjectClickHandler handleSwampTrapdoorShortcut = new ObjectClickHandler(new Object[] { 5055, 5054 }, e -> {
		e.getPlayer().ladder(e.getObjectId() == 5055 ? Tile.of(3477, 9845, 0) : Tile.of(3495, 3466, 0));
	});

	public static ObjectClickHandler handleMyrequeWall = new ObjectClickHandler(new Object[] { 5052 }, e -> {
		Doors.handleOneWayDoor(e.getPlayer(), e.getObject(), 1);
	});

	public static ObjectClickHandler handleSwampWoodenDoors = new ObjectClickHandler(new Object[] { 30261, 30262, 30265 }, e -> {
		e.getPlayer().useStairs(e.getObjectId() == 30265 ? Tile.of(3500, 9812, 0) : Tile.of(3510, 3448, 0));
	});

	public static ObjectClickHandler handleTreeBridgeShortcut = new ObjectClickHandler(new Object[] { 5005 }, e -> {
		if (e.getObject().getTile().isAt(3502, 3431))
			e.getPlayer().ladder(Tile.of(3502, 3425, 0));
		else
			e.getPlayer().ladder(Tile.of(3502, 3432, 0));
	});

	public static ObjectClickHandler handleSwampBoatFromMorton = new ObjectClickHandler(new Object[] { 6969 }, e -> {
		e.getPlayer().tele(Tile.of(3500, 3380, 0));
	});

	public static ObjectClickHandler handleSwampBoatToMorton = new ObjectClickHandler(new Object[] { 6970 }, e -> {
		e.getPlayer().tele(Tile.of(3521, 3284, 0));
	});

	public static ObjectClickHandler handleGrottoBridge = new ObjectClickHandler(new Object[] { 3522 }, e -> {
		int jumpTo = ((e.getObject().getY() <= 3329) ? e.getObject().getY()+2 : e.getObject().getY()-2);
		Tile endTile = Tile.of(e.getObject().getX(), jumpTo, e.getObject().getPlane());
		e.getPlayer().lock();
		e.getPlayer().setNextFaceTile(endTile);
		e.getPlayer().setNextAnimation(new Animation(769));
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				e.getPlayer().unlockNextTick();
				e.getPlayer().tele(endTile);
				e.getPlayer().setNextAnimation(new Animation(-1));
			}
		}, 1);
	});

	public static ItemClickHandler handleBonesackTele = new ItemClickHandler(new Object[] { 15215 }, new String[] { "Teleport" }, e -> {
		Magic.sendTeleportSpell(e.getPlayer(), 12055, 12057, 2133, 2134, 0, 0, Tile.of(3362, 3504, 0), 3, true, TeleType.ITEM, null);
	});

	private static void drakanTeleport(Player player, Item item, Tile location) {
		if (item.getMetaDataI("drakanCharges") <= 0) {
			player.sendMessage("The medallion seems unresponsive. It probably needs recharging.");
			return;
		}
		Magic.sendTeleportSpell(player, 8939, 8941, 1864, 1864, 0, 0, location, 2, true, TeleType.ITEM, () -> {
			if (player.getX() >= 3398 && player.getX() <= 3841 && player.getY() >= 3161 && player.getY() <= 3586)
				player.sendMessage("Due to the short nature of your teleport, the medallion does not use a charge.");
			else
				player.sendMessage("Your medallion has " + item.decMetaDataI("drakanCharges") + " charges left.");
		});
	}

	public static ItemClickHandler handleDrakansMedallion = new ItemClickHandler(new Object[] { 21576 }, new String[] { "Teleport", "Check-charges" }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.BRANCHES_OF_DARKMEYER, "to use the medallion."))
			return;
		if (e.getOption().equals("Teleport")) {
			if (e.getItem().getMetaDataI("drakanCharges") <= 0) {
				e.getPlayer().sendMessage("The medallion seems unresponsive. It probably needs recharging.");
				return;
			}
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addOptions("Where would you like to teleport?", new Options() {
						@Override
						public void create() {
							option("Barrows", () -> drakanTeleport(e.getPlayer(), e.getItem(), Tile.of(3565, 3312, 0)));
							option("Burgh de Rott", () -> drakanTeleport(e.getPlayer(), e.getItem(), Tile.of(3491, 3199, 0)));
							option("Meiyerditch", () -> drakanTeleport(e.getPlayer(), e.getItem(), Tile.of(3626, 9618, 0)));
							option("Darkmeyer", () -> drakanTeleport(e.getPlayer(), e.getItem(), Tile.of(3628, 3364, 00)));
							option("Meiyerditch Laboratories", () -> drakanTeleport(e.getPlayer(), e.getItem(), Tile.of(3633, 9696, 0)));
						}
					});
				}
			});
		} else
			e.getPlayer().sendMessage("It looks like it has another " + e.getItem().getMetaDataI("drakanCharges", 0) + " charges left.");
	});

	public static ObjectClickHandler handleMedallionRecharge = new ObjectClickHandler(new Object[] { 61094 }, e -> {
		Item medal = e.getPlayer().getItemWithPlayer(21576);
		if (medal == null) {
			e.getPlayer().sendMessage("You don't have a medallion with you.");
			return;
		}
		medal.addMetaData("drakanCharges", 10);
		e.getPlayer().sendMessage("You dip the medallion into the blood. Eww. It feels heartily recharged, though.");
	});

	public static ObjectClickHandler handleDrakanMedallionCave = new ObjectClickHandler(new Object[] { 61091, 59921 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.BRANCHES_OF_DARKMEYER, "to enter the cave."))
			return;
		e.getPlayer().fadeScreen(() -> {
			e.getPlayer().tele(e.getObjectId() == 59921 ? Tile.of(2273, 5152, 0) : Tile.of(3498, 3204, 0));
		});
	});

	public static ObjectClickHandler handleClaimDrakanMedallion = new ObjectClickHandler(new Object[] { 61092 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.BRANCHES_OF_DARKMEYER, "to search this."))
			return;
		if (e.getPlayer().containsItem(21576)) {
			e.getPlayer().sendMessage("You have already retrieved the medallion from here.");
			return;
		}
		e.getPlayer().sendOptionDialogue("Are you sure you want to inspect the coffin?", ops -> {
			ops.add("Yes.", () -> {
				e.getPlayer().setNextAnimation(new Animation(14745));
				e.getPlayer().lock();
				e.getPlayer().applyHit(new Hit(null, 10, HitLook.TRUE_DAMAGE), 0, () -> {
					e.getPlayer().getInventory().addItem(new Item(21576, 1).addMetaData("drakanCharges", 10));
					e.getObject().setIdTemporary(61093, 20);
					e.getPlayer().unlock();
				});
			});
			ops.add("No, I'm scared!", new Dialogue().addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Veliaf did ask me to search everywhere."));
		});
	});

	//Fenkenstraincastle
	public static ObjectClickHandler handleFenkenstraincastlestairs = new ObjectClickHandler(new Object[] { 5206, 5207 }, e -> {
		if (e.getObjectId() == 5206)
			e.getPlayer().tele(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 1 ? -0 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 1 ? -0 : 0,  1));
		else if (e.getObjectId() == 5207)
			e.getPlayer().tele(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 1 ? -0 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 1 ? -0 : 0, -1));
	});
	public static ObjectClickHandler experimentcavegraveentrance = new ObjectClickHandler(new Object[] { 5167 }, e -> {
		e.getPlayer().tele(Tile.of(3577, 9927, 0));
	});

	//TakenTemple
	public static ObjectClickHandler handleTemplespiralstairsup = new ObjectClickHandler(new Object[] { 30722 }, e -> {
		e.getPlayer().tele(Tile.of(3415, 3485, 1));
	});

	public static ObjectClickHandler handleTemplespiralstairsdown = new ObjectClickHandler(new Object[] { 30723 }, e -> {
		e.getPlayer().tele(Tile.of(3414, 3486, 0));
	});

	public static ObjectClickHandler handleTemplespiralstairsup2 = new ObjectClickHandler(new Object[] { 30724 }, e -> {
		e.getPlayer().tele(Tile.of(3415, 3492, 1));
	});

	public static ObjectClickHandler handleTemplespiralstairsdown2 = new ObjectClickHandler(new Object[] { 30725  }, e -> {
		e.getPlayer().tele(Tile.of(3414, 3491, 0));
	});

}
