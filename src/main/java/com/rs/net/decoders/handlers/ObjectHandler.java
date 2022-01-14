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
package com.rs.net.decoders.handlers;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.others.PolyporeNPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.impl.StrongholdRewardD;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.minigames.FightPits;
import com.rs.game.player.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.player.content.minigames.partyroom.PartyRoom;
import com.rs.game.player.content.minigames.pest.Lander;
import com.rs.game.player.content.pet.Incubator;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.skills.agility.WildernessAgility;
import com.rs.game.player.content.skills.construction.EnterHouse;
import com.rs.game.player.content.skills.cooking.Cooking;
import com.rs.game.player.content.skills.cooking.Cooking.Cookables;
import com.rs.game.player.content.skills.cooking.CowMilkingAction;
import com.rs.game.player.content.skills.crafting.Jewelry;
import com.rs.game.player.content.skills.crafting.SandBucketFill;
import com.rs.game.player.content.skills.crafting.Silver;
import com.rs.game.player.content.skills.dungeoneering.rooms.puzzles.FishingFerretRoom;
import com.rs.game.player.content.skills.firemaking.Bonfire;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.content.skills.runecrafting.Abyss;
import com.rs.game.player.content.skills.runecrafting.Runecrafting;
import com.rs.game.player.content.skills.runecrafting.Runecrafting.RCRune;
import com.rs.game.player.content.skills.runecrafting.RunecraftingAltar.Altar;
import com.rs.game.player.content.skills.smithing.ForgingInterface;
import com.rs.game.player.content.skills.smithing.Smithing.ForgingBar;
import com.rs.game.player.content.skills.summoning.Summoning;
import com.rs.game.player.content.skills.thieving.Thieving;
import com.rs.game.player.content.transportation.SpiritTree;
import com.rs.game.player.content.transportation.WildernessObelisk;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.controllers.AgilityPyramidController;
import com.rs.game.player.controllers.DamonheimController;
import com.rs.game.player.controllers.DungeonController;
import com.rs.game.player.controllers.FalconryController;
import com.rs.game.player.controllers.FightCavesController;
import com.rs.game.player.controllers.FightKilnController;
import com.rs.game.player.controllers.PestControlLobbyController;
import com.rs.game.player.controllers.PuroPuroController;
import com.rs.game.player.controllers.StealingCreationLobbyController;
import com.rs.game.player.controllers.UndergroundDungeonController;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.WildernessController;
import com.rs.game.player.cutscenes.DTPreview;
import com.rs.game.player.dialogues.AncientAltar;
import com.rs.game.player.dialogues.ClimbEmoteStairs;
import com.rs.game.player.dialogues.ClimbNoEmoteStairs;
import com.rs.game.player.dialogues.CookingD;
import com.rs.game.player.dialogues.DTClaimRewards;
import com.rs.game.player.dialogues.GrotwormLairD;
import com.rs.game.player.dialogues.LunarAltar;
import com.rs.game.player.dialogues.MagicPortal;
import com.rs.game.player.dialogues.MiningGuildDwarf;
import com.rs.game.player.dialogues.PartyRoomLever;
import com.rs.game.player.dialogues.RunespanPortalD;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.player.dialogues.SimplePlayerMessage;
import com.rs.game.player.dialogues.SmeltingD;
import com.rs.game.player.dialogues.SpiritTreeD;
import com.rs.game.player.dialogues.WildernessDitch;
import com.rs.game.player.dialogues.ZarosAltar;
import com.rs.game.player.managers.EmotesManager.Emote;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

public final class ObjectHandler {

	public static void handleOption1(final Player player, final GameObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions(player);
		final int id = object.getId();
		final int x = object.getX();
		final int y = object.getY();

		if (!objectDef.containsOption(0) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP1, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);

			if (!player.getControllerManager().processObjectClick1(object))
				return;
			if (player.getTreasureTrailsManager().useObject(object))
				return;
			//				if (PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP1, false)))
			//					return;


			if (object.getId() == 29355) {
				player.useStairs(828, new WorldTile(player.getX(), player.getY() - 6400, 0), 1, 2);
				return;
			}
			if(object.getId() == 37117) {
				player.useStairs(-1, new WorldTile(object.getX()-2, player.getY(), 0), 1, 2);
				return;
			}
			else if (object.getId() == 7434) {
				player.useStairs(828, new WorldTile(3682, 9961, 0), 1, 2);
				return;
			} else if (object.getId() == 7433) {
				player.useStairs(828, new WorldTile(3681, 3497, 0), 1, 2);
				return;
			} else if (object.getId() == 25337) {
				player.setNextWorldTile(new WorldTile(1744, 5321, 1));
				return;
			} else if (object.getId() == 39468) {
				player.setNextWorldTile(new WorldTile(1745, 5325, 0));
				return;
			} else if (id == 50552) {
				if (player.getControllerManager().getController() instanceof DungeonController)
					player.getControllerManager().removeControllerWithoutCheck();
				player.setNextForceMovement(new ForceMovement(object, 1, Direction.NORTH));
				player.getPackets().sendVarc(234, 0);// Party Config Interface
				player.getControllerManager().startController(new DamonheimController());
				player.useStairs(13760, new WorldTile(3454, 3725, 0), 2, 3);
			} else if (object.getId() == 68) {
				if (player.getInventory().containsItem(28)) {
					if (player.getInventory().containsItem(1925)) {
						player.setNextAnimation(new Animation(833));
						player.lock(1);
						player.getInventory().deleteItem(1925, 1);
						player.getInventory().addItem(30, 1);
					} else
						player.sendMessage("You need a bucket to gather the wax into.");
				} else {
					player.setNextAnimation(new Animation(833));
					player.lock(1);
					player.setNextForceTalk(new ForceTalk("Ouch!"));
					player.applyHit(new Hit(10, HitLook.TRUE_DAMAGE));
					player.sendMessage("The bees sting your hands as you reach inside!");
				}
			} else if (object.getId() == 5259) {
				if (player.getY() == 3507)
					player.setNextWorldTile(new WorldTile(player.getX(), player.getY() + 2, 0));
				else if (player.getY() == 3509)
					player.setNextWorldTile(new WorldTile(player.getX(), player.getY() - 2, 0));
				return;
			}else if (object.getId() == 29099) {
				if (player.getY() > object.getY())
					player.setNextWorldTile(object.transform(1, -1, 0));
				else
					player.setNextWorldTile(object.transform(1, 1, 0));
			} else if (object.getId() == 2331) {
				int amount = player.getInventory().getNumberOf(23194);
				if (amount > 0) {
					player.getInventory().deleteItem(23194, amount);
					player.getInventory().addItem(23193, amount);
					player.sendMessage("You magically turn the sandstone into glass without moving since Trent can't find the object and player animations!");
				} else
					player.sendMessage("You do not have any sandstone to turn into glass!");
			}

			if (object.getId() == 36687) {
				player.useStairs(828, new WorldTile(player.getX(), player.getY() + 6400, 0), 1, 2);
				return;
			}
			if (object.getId() == 16535) {
				player.getControllerManager().startController(new AgilityPyramidController());
				AgilityPyramidController.climbRocks(player, object);
			} else if (object.getId() == 11739) {
				player.useStairs(828, player.transform(0, -2, 1), 1, 2);
				return;
			} else if (object.getId() == 11741) {
				player.useStairs(828, player.transform(0, 2, -1), 1, 2);
				return;
			} else if (object.getId() == 24357 || object.getId() == 24358 || object.getId() == 11734) {
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, 4, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(4, 0, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, -4, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(-4, 0, 1), 1, 1);
					break;
				}
				return;
			} else if (object.getId() == 24359 || object.getId() == 24360 || object.getId() == 35783) {
				if (object.isAt(3189, 3432)) {
					player.useStairs(-1, player.transform(2, 6400, 0), 1, 1);
					return;
				}
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, -4, -1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(-4, 0, -1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, 4, -1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(4, 0, -1), 1, 1);
					break;
				}
				return;
			} else if (object.getId() == 66518) {
				player.useStairs(828, new WorldTile(3047, 4971, 0), 1, 2);
				return;
			} else if (object.getId() == 7258) {
				player.useStairs(-1, new WorldTile(2896, 3447, 0), 1, 1);
				return;
			}

			if (object.getId() == 15468) {
				if (!player.getInventory().containsItem(2347, 1)) {
					player.getInventory().addItem(2347, 1);
					player.sendMessage("You take a hammer.");
					return;
				}
				player.sendMessage("You don't need another one right now.");
				return;
			}

			if (object.getId() == 48496)
				//					if (player.getInventory().getFreeSlots() == 28 && !player.getEquipment().wearingArmour() && !player.hasFamiliar()) {
				//						player.getDialogueManager().execute(new DungFloorSelectD", player);
				//					} else {
				//						player.sendMessage("You cannot bring familiars, armour, or items into dungeoneering.");
				//					}
				//					return;
				player.getDungManager().enterDungeon(true);
			else if (id == 31149) {
				boolean isEntering = player.getX() <= 3295;
				player.useStairs(isEntering ? 9221 : 9220, new WorldTile(x + (isEntering ? 1 : 0), y, 0), 1, 2);
			} else if (id == 2350 && (object.getX() == 3352 && object.getY() == 3417 && object.getPlane() == 0))
				player.useStairs(832, new WorldTile(3177, 5731, 0), 1, 2);
			else if (id >= 65616 && id <= 65622)
				WildernessObelisk.activateObelisk(id, player);
			else if (id == 10229) { // dag up ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextWorldTile(new WorldTile(1910, 4367, 0));
					}
				}, 1);
				return;
			} else if (id == 17757) {
				Agility.handleObstacle(player, 3303, 1, player.transform(0, player.getY() < object.getY() ? 2 : -2, 0), 0);
				return;
			} else if (id == 56805) { // HARBLORE HARBITAT
				if (object.getRotation() == 3 || object.getRotation() == 1)
					Agility.handleObstacle(player, 3303, 1, player.transform(player.getX() < object.getX() ? 2 : -2, 0, 0), 0);
				else
					Agility.handleObstacle(player, 3303, 1, player.transform(0, player.getY() < object.getY() ? 2 : -2, 0), 0);
				return;
			} else if (id == 27126) { // HARBLORE HARBITAT
				if (player.getX() >= 2961 && player.getX() <= 2964)
					player.setNextWorldTile(new WorldTile(player.getX() + 12, player.getY() + 2, player.getPlane()));
				else if (player.getX() >= 2975 && player.getX() <= 2979)
					player.setNextWorldTile(new WorldTile(player.getX() - 12, player.getY() - 2, player.getPlane()));
				return;
			} else if (id == 10230) { // dag down ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextWorldTile(new WorldTile(2900, 4449, 0));
					}
				}, 1);
				return;
			} else if (id == 26849) { // ZMI Altar down ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextWorldTile(new WorldTile(3271, 4861, 0));
					}
				}, 1);
				return;
			} else if (id == 26850) { // ZMI Altar up ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextWorldTile(new WorldTile(2452, 3232, 0));
					}
				}, 1);
				return;
			} else if (id == 15653) {
				if (World.isSpawnedObject(object) || !WarriorsGuild.canEnter(player))
					return;
				player.lock(2);
				GameObject opened = new GameObject(object.getId(), object.getType(), object.getRotation() - 1, object.getX(), object.getY(), object.getPlane());
				World.spawnObjectTemporary(opened, 1);
				player.addWalkSteps(2876, 3542, 2, false);
			} else if (id == 14315) {
				if (Lander.canEnter(player, 0))
					return;
			} else if (id == 25631) {
				if (Lander.canEnter(player, 1))
					return;
			} else if (id == 25632) {
				if (Lander.canEnter(player, 2))
					return;
			} else if (id == 24991) {
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.getControllerManager().startController(new PuroPuroController());
					}
				}, 10);
				Magic.sendTeleportSpell(player, 6601, -1, 1118, -1, 0, 0, new WorldTile(2591, 4320, 0), 9, false, Magic.OBJECT_TELEPORT);
			} else if (id == 26847)
				Runecrafting.craftZMIAltar(player);
			else if (id == 35391 || id == 2832) {
				if (!Agility.hasLevel(player, id == 2832 ? 20 : 41))
					return;
				player.addWalkSteps(x, y);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						boolean isTravelingWest = id == 2832 ? player.getX() >= 2508 : (x == 2834 && y == 3626) ? player.getX() >= 2834 : player.getX() >= 2900;
						player.useStairs(3303, new WorldTile((isTravelingWest ? -2 : 2) + player.getX(), player.getY(), 0), 2, 3, null, true);
					}
				});
			} else if (id == 7143 || id == 7153)
				Abyss.clearRocks(player, object);
			else if (id == 7152 || id == 7144)
				Abyss.clearTendrills(player, object, new WorldTile(id == 7144 ? 3028 : 3051, 4824, 0));
			else if (id == 7150 || id == 7146)
				Abyss.clearEyes(player, object, new WorldTile(object.getX() == 3021 ? 3028 : 3050, 4839, 0));
			else if (id == 7147)
				Abyss.clearGap(player, object, new WorldTile(3030, 4843, 0), false);
			else if (id == 7148)
				Abyss.clearGap(player, object, new WorldTile(3040, 4845, 0), true);
			else if (id == 7149)
				Abyss.clearGap(player, object, new WorldTile(3048, 4842, 0), false);
			else if (id == 7151)
				Abyss.burnGout(player, object, new WorldTile(3053, 4831, 0));
			else if (id == 7145)
				Abyss.burnGout(player, object, new WorldTile(3024, 4834, 0));
			else if (id == 7137)
				player.setNextWorldTile(Altar.WATER.getInside());
			else if (id == 7139)
				player.setNextWorldTile(Altar.AIR.getInside());
			else if (id == 7140)
				player.setNextWorldTile(Altar.MIND.getInside());
			else if (id == 7131)
				player.setNextWorldTile(Altar.BODY.getInside());
			else if (id == 7130)
				player.setNextWorldTile(Altar.EARTH.getInside());
			else if (id == 7129)
				player.setNextWorldTile(Altar.FIRE.getInside());
			else if (id == 7136)
				player.setNextWorldTile(Altar.DEATH.getInside());
			else if (id == 7135)
				player.setNextWorldTile(Altar.LAW.getInside());
			else if (id == 7133)
				player.setNextWorldTile(Altar.NATURE.getInside());
			else if (id == 7132)
				player.setNextWorldTile(Altar.COSMIC.getInside());
			else if (id == 7141)
				player.setNextWorldTile(Altar.BLOOD.getInside());
			else if (id == 7134)
				player.setNextWorldTile(Altar.CHAOS.getInside());
			else if (id == 7138)
				player.sendMessage("A strange power blocks your exit..");
			else if (id == 65371) { // Chaos altar (armored zombie)
				final int maxPrayer1 = player.getSkills().getLevelForXp(Constants.PRAYER) * 10;
				if (player.getPrayer().getPoints() < maxPrayer1) {
					player.lock(5);
					player.sendMessage("You pray to the gods...", true);
					player.setNextAnimation(new Animation(645));
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							player.getPrayer().restorePrayer(maxPrayer1);
							player.sendMessage("...and recharged your prayer.", true);
						}
					}, 2);
				} else
					player.sendMessage("You already have full prayer.");
				return;
			} else if (id == 65715) { // Armored zombie trapdoor
				player.setNextWorldTile(new WorldTile(3241, 9991, 0));
				return;
			} else if (id == 12328) { // Jadinko lair
				player.setNextWorldTile(new WorldTile(3011, 9276, 0));
				return;
			}

			else if (id == 66533)
				player.useStairs(-1, new WorldTile(2208, 4364, 0), 0, 1);
			else if (id == 66534)
				player.useStairs(-1, new WorldTile(2878, 3573, 0), 0, 1);

			else if (id == 11209)
				player.useStairs(-1, player.transform(3, 0, 1), 0, 1);
			else if (id == 11210)
				player.useStairs(-1, player.transform(-3, 0, -1), 0, 1);

			else if (id == 11212)
				player.useStairs(-1, player.transform(0, 3, -1), 0, 1);
			else if (id == 11211)
				player.useStairs(-1, player.transform(0, -3, 1), 0, 1);

			else if (id == 38279 && x == 3107 && y == 3160)
				player.getDialogueManager().execute(new RunespanPortalD());
			else if (id == 38279 && x == 1696 && y == 5460)
				player.useStairs(-1, new WorldTile(3106, 3160, 1), 0, 1);

			/*
			 * START POLYPORE DUNGEON
			 */
			else if (id == 64360 && x == 4629 && y == 5453)
				PolyporeNPC.useStairs(player, new WorldTile(4629, 5451, 2), true);
			else if (id == 64361 && x == 4629 && y == 5452)
				PolyporeNPC.useStairs(player, new WorldTile(4629, 5454, 3), false);
			else if (id == 64359 && x == 4632 && y == 5443)
				PolyporeNPC.useStairs(player, new WorldTile(4632, 5443, 1), true);
			else if (id == 64361 && x == 4632 && y == 5442)
				PolyporeNPC.useStairs(player, new WorldTile(4632, 5444, 2), false);
			else if (id == 64359 && x == 4632 && y == 5409)
				PolyporeNPC.useStairs(player, new WorldTile(4632, 5409, 2), true);
			else if (id == 64361 && x == 4633 && y == 5409)
				PolyporeNPC.useStairs(player, new WorldTile(4631, 5409, 3), false);
			else if (id == 64359 && x == 4642 && y == 5389)
				PolyporeNPC.useStairs(player, new WorldTile(4642, 5389, 1), true);
			else if (id == 64361 && x == 4643 && y == 5389)
				PolyporeNPC.useStairs(player, new WorldTile(4641, 5389, 2), false);
			else if (id == 64359 && x == 4652 && y == 5388)
				PolyporeNPC.useStairs(player, new WorldTile(4652, 5388, 0), true);
			else if (id == 64362 && x == 4652 && y == 5387)
				PolyporeNPC.useStairs(player, new WorldTile(4652, 5389, 1), false);
			else if (id == 64359 && x == 4691 && y == 5469)
				PolyporeNPC.useStairs(player, new WorldTile(4691, 5469, 2), true);
			else if (id == 64361 && x == 4691 && y == 5468)
				PolyporeNPC.useStairs(player, new WorldTile(4691, 5470, 3), false);
			else if (id == 64359 && x == 4689 && y == 5479)
				PolyporeNPC.useStairs(player, new WorldTile(4689, 5479, 1), true);
			else if (id == 64361 && x == 4689 && y == 5480)
				PolyporeNPC.useStairs(player, new WorldTile(4689, 5478, 2), false);
			else if (id == 64359 && x == 4698 && y == 5459)
				PolyporeNPC.useStairs(player, new WorldTile(4698, 5459, 2), true);
			else if (id == 64361 && x == 4699 && y == 5459)
				PolyporeNPC.useStairs(player, new WorldTile(4697, 5459, 3), false);
			else if (id == 64359 && x == 4705 && y == 5460)
				PolyporeNPC.useStairs(player, new WorldTile(4704, 5461, 1), true);
			else if (id == 64361 && x == 4705 && y == 5461)
				PolyporeNPC.useStairs(player, new WorldTile(4705, 5459, 2), false);
			else if (id == 64359 && x == 4718 && y == 5467)
				PolyporeNPC.useStairs(player, new WorldTile(4718, 5467, 0), true);
			else if (id == 64361 && x == 4718 && y == 5466)
				PolyporeNPC.useStairs(player, new WorldTile(4718, 5468, 1), false);
			/*
			 * END POLYPORE DUNGEON
			 */

			else if (id == 12327) { // jadinko lair out
				player.setNextWorldTile(new WorldTile(2948, 2955, 0));
				return;
			} else if (id == 4495) { // Slayer tower stairs up
				player.setNextWorldTile(new WorldTile(3417, 3541, 2));
				return;
			} else if (id == 4496) { // Slayer tower stairs down
				player.setNextWorldTile(new WorldTile(3412, 3540, 1));
				return;
			} else if (id == 39191) { // Armored zombie up ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.setNextWorldTile(new WorldTile(3240, 3607, 0));
						player.getControllerManager().startController(new WildernessController());
					}
				}, 1);
				return;
			} else if (id == 2353 && (object.getX() == 3177 && object.getY() == 5730 && object.getPlane() == 0))
				player.useStairs(828, new WorldTile(3353, 3416, 0), 1, 2);
			else if (id == 66115 || id == 66116)
				InventoryOptionsHandler.dig(player);
			else if (id == 2478)
				Runecrafting.runecraft(player, RCRune.AIR);
			else if (id == 2479)
				Runecrafting.runecraft(player, RCRune.MIND);
			else if (id == 2480)
				Runecrafting.runecraft(player, RCRune.WATER);
			else if (id == 2481)
				Runecrafting.runecraft(player, RCRune.EARTH);
			else if (id == 2482)
				Runecrafting.runecraft(player, RCRune.FIRE);
			else if (id == 2483)
				Runecrafting.runecraft(player, RCRune.BODY);
			else if (id == 2484)
				Runecrafting.runecraft(player, RCRune.COSMIC);
			else if (id == 2487)
				Runecrafting.runecraft(player, RCRune.CHAOS);
			else if (id == 17010)
				Runecrafting.runecraft(player, RCRune.ASTRAL);
			else if (id == 2486)
				Runecrafting.runecraft(player, RCRune.NATURE);
			else if (id == 2485)
				Runecrafting.runecraft(player, RCRune.LAW);
			else if (id == 2488)
				Runecrafting.runecraft(player, RCRune.DEATH);
			else if (id == 30624)
				Runecrafting.runecraft(player, RCRune.BLOOD);
			else if (id == 26723)
				player.getDialogueManager().execute(new SpiritTreeD(), (object.getId() == 68973 && object.getId() == 68974) ? 3637 : 3636);
			else if (id == 4019 || id == 67036)
				Summoning.openInfusionInterface(player);
			else if (id == 20604)
				player.useStairs(-1, new WorldTile(3018, 3404, 0), 0, 1);
			else if (object.getId() == 39508 || object.getId() == 39509)
				StealingCreationLobbyController.climbOverStile(player, object, true);
			else if (id == 29734) {
				if (player.getEmotesManager().unlockedEmote(Emote.SAFETY_FIRST)) {
					if (player.containsItem(12629))
						player.sendMessage("You find nothing inside the chest.");
					else
						player.getInventory().addItem(12629, 1, true);
					return;
				}
				player.save("sopsRew", true);
				player.getInventory().addItem(995, 10000, true);
				player.getInventory().addItem(12629, 1, true);
				player.getInventory().addItem(12628, 2, true);
				player.getEmotesManager().unlockEmote(Emote.SAFETY_FIRST);
				player.getDialogueManager().execute(new SimpleMessage(), "You open the chest and find a large pile of gold, along with a pair", "of safety gloves and two antique lamps. Also in the chest is the", "secret of the 'Safety First' emote.");
			} else if (id == 16135) {
				if (player.getEmotesManager().unlockedEmote(Emote.FLAP)) {
					player.sendMessage("You have already claimed your reward from this level.");
					return;
				}
				player.startConversation(new StrongholdRewardD(player, 0));
			} else if (id == 16077) {
				if (player.getEmotesManager().unlockedEmote(Emote.SLAP_HEAD)) {
					player.sendMessage("You have already claimed your reward from this level.");
					return;
				}
				player.startConversation(new StrongholdRewardD(player, 1));
			} else if (id == 16118) {
				if (player.getEmotesManager().unlockedEmote(Emote.IDEA)) {
					player.sendMessage("You have already claimed your reward from this level.");
					return;
				}
				player.startConversation(new StrongholdRewardD(player, 2));
			} else if (id == 16047)
				player.startConversation(new StrongholdRewardD(player, 3));
			else if (id == 47120) { // zaros altar
				// recharge if needed
				if (player.getPrayer().getPoints() < player.getSkills().getLevelForXp(Constants.PRAYER) * 10) {
					player.lock(12);
					player.setNextAnimation(new Animation(12563));
					player.getPrayer().setPoints(((player.getSkills().getLevelForXp(Constants.PRAYER) * 10) * 1.15));
					player.getPrayer().refreshPoints();
				}
				player.getDialogueManager().execute(new ZarosAltar());
			} else if (id == 19222)
				FalconryController.beginFalconry(player);
			else if (id == 42425 && object.getX() == 3220 && object.getY() == 3222) { // zaros
				// portal
				player.useStairs(10256, new WorldTile(3353, 3416, 0), 4, 5, "And you find yourself into a digsite.");
				player.addWalkSteps(3222, 3223, -1, false);
				player.sendMessage("You examine portal and it absorbs you...");
			} else if (id == 9356)
				FightCavesController.enterFightCaves(player);
			else if (id == 68107)
				FightKilnController.enterFightKiln(player, false);
			else if (id == 68223)
				FightPits.enterLobby(player, false);
			else if (id == 26684 || id == 26685 || id == 26686) // poison
				// waste
				// cave
				player.useStairs(-1, new WorldTile(1989, 4174, 0), 1, 2, "You enter the murky cave...");
			else if (id == 26571 || id == 26572 || id == 26573 || id == 26574)
				player.useStairs(-1, new WorldTile(2321, 3100, 0), 1, 2);
			else if (id == 26560 && x == 2015 && y == 4255)
				player.getDialogueManager().execute(new SimpleMessage(), "The room beyond the door is covred in gas, it is probably dangerous to go in there.");
			else if (id == 26519) {
				if (x == 1991 && y == 4175)
					player.useStairs(827, new WorldTile(1991, 4175, 0), 1, 2);
				else if (x == 1998 && y == 4218)
					player.useStairs(827, new WorldTile(1998, 4218, 0), 1, 2);
				else if (x == 2011 && y == 4218)
					player.useStairs(827, new WorldTile(2011, 4218, 0), 1, 2);
				else
					player.useStairs(827, new WorldTile(x - 1, y, 0), 1, 2);
			} else if (id == 19171) {
				if (!Agility.hasLevel(player, 20))
					return;
				player.useStairs(-1, new WorldTile(player.getX() >= 2523 ? 2522 : 2523, 3375, 0), 1, 2, "You easily squeeze through the railing.");
			} else if (id == 22945) {
				player.useStairs(-1, new WorldTile(3318, 9602, 0), 0, 1);
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (id == 15767) {
				player.useStairs(-1, new WorldTile(3748, 9373, 0), 0, 1);
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (object.getId() == 15791) {
				if (object.getX() == 3829)
					player.useStairs(-1, new WorldTile(3830, 9461, 0), 1, 2);
				if (object.getX() == 3814)
					player.useStairs(-1, new WorldTile(3815, 9461, 0), 1, 2);
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (id == 5947) {
				player.useStairs(540, new WorldTile(3170, 9571, 0), 8, 9);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						player.getControllerManager().startController(new UndergroundDungeonController(false, true));
						player.setNextAnimation(new Animation(-1));
					}
				}, 8);
				return;
			} else if (object.getId() == 6658) {
				player.useStairs(-1, new WorldTile(3226, 9542, 0), 1, 2);
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (object.getId() == 6898) {
				player.setNextAnimation(new Animation(10578));
				player.useStairs(-1, object, 1, 2);
				player.useStairs(10579, new WorldTile(3221, 9618, 0), 1, 2);
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
				player.sendMessage("You squeeze through the hole.");
				return;
			} else if (id == 36002) {
				player.getControllerManager().startController(new UndergroundDungeonController(true, false));
				player.useStairs(833, new WorldTile(3206, 9379, 0), 1, 2);
			} else if (id == 31359) {
				player.useStairs(-1, new WorldTile(3360, 9352, 0), 1, 2);
				player.getControllerManager().startController(new UndergroundDungeonController(true, true));
			} else if (id == 69197 || id == 69198) {
				Doors.handleInPlaceDoubleDoor(player, object);
				player.resetWalkSteps();
				player.addWalkSteps(object.getX(), player.getY() <= 3491 ? player.getY() + 2 : player.getY() - 2, -1, false);
			} else if (id == 4756)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, 4, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(-4, 0, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, -4, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(4, 0, 1), 1, 1);
					break;
				}
			else if (id == 4755)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, -4, -1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(4, 0, -1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, 4, -1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(-4, 0, -1), 1, 1);
					break;
				}
			else if (id == 16960 || id == 16959)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(828, player.transform(2, 0, 1), 1, 1);
					break;
				case 1:
					player.useStairs(828, player.transform(0, -2, 1), 1, 1);
					break;
				case 2:
					player.useStairs(828, player.transform(0, 2, 1), 1, 1);
					break;
				case 3:
					player.useStairs(828, player.transform(0, 2, 1), 1, 1);
					break;
				}
			else if (id == 16962 || id == 16961)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(827, player.transform(-2, 0, -1), 1, 1);
					break;
				case 1:
					player.useStairs(827, player.transform(0, 2, -1), 1, 1);
					break;
				case 2:
					player.useStairs(827, player.transform(0, -2, -1), 1, 1);
					break;
				case 3:
					player.useStairs(827, player.transform(0, -2, -1), 1, 1);
					break;
				}
			else if (id == 1759 || id == 4780)
				player.useStairs(827, player.transform(0, 6400, 0), 1, 1);
			else if (id == 4781)
				player.useStairs(828, player.transform(0, -6400, 0), 1, 1);
			else if (id == 29392)
				player.useStairs(-1, new WorldTile(3061, 3335, 0), 1, 1);
			else if (id == 29386 || id == 29385)
				player.useStairs(-1, new WorldTile(3067, 9710, 0), 1, 1);
			else if (id == 29391)
				player.useStairs(-1, new WorldTile(3037, 3342, 0), 1, 1);
			else if (id == 29387)
				player.useStairs(-1, new WorldTile(3035, 9713, 0), 1, 1);
			else if (id == 7104)
				player.useStairs(-1, player.transform(4, -2, 1), 1, 1);
			else if (id == 7107)
				player.useStairs(-1, player.transform(-4, 2, -1), 1, 1);
			else if (id == 4772)
				player.useStairs(828, player.transform(0, 0, 1), 1, 1);
			else if (id == 4778)
				player.useStairs(827, player.transform(0, 0, -1), 1, 1);
			else if (id == 4622)
				player.useStairs(-1, player.transform(0, object.getRotation() == 2 ? -4 : 4, 1), 1, 1);
			else if (id == 4620)
				player.useStairs(-1, player.transform(0, object.getRotation() == 2 ? 4 : -4, -1), 1, 1);
			else if (id == 73681)
				player.useStairs(-1, player.transform(player.getX() < x ? 3 : -3, 0, 0), 1, 1);
			else if (id == 2712) {
				if (player.getSkills().getLevel(Constants.COOKING) < 32 && player.getY() < 3444) {
					player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(847, HeadE.ANGRY, "I can't allow someone as novice as you into my kitchen!"))));
					return;
				}
				if (player.getEquipment().getHatId() != 1949 && player.getY() < 3444) {
					player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(847, HeadE.ANGRY, "You sure don't look much like a chef!"))));
					return;
				}
				Doors.handleDoor(player, object);
			} else if (id == 2647) {
				if (player.getSkills().getLevel(Constants.CRAFTING) < 40 && player.getY() < 3288) {
					player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(805, HeadE.CHEERFUL, "I'm sorry, you need to have a crafting level of 40 to use my facilities."))));
					return;
				}
				if (player.getEquipment().getChestId() != 1757 && player.getY() > 3288) {
					player.startConversation(new Conversation(player, new Dialogue(new NPCStatement(805, HeadE.CHEERFUL, "Don't forget to put on your brown apron! It can be a little messy in here."))));
					return;
				}
				Doors.handleDoor(player, object);
			} else if (id == 64890)
				player.useLadder(player.transform(0, 0, 1)); //bottom floor ladders of dark warriors fortress.
			else if (id == 71921)
				player.useStairs(828, player.transform(0, 0, 1), 1, 2);
			else if (id == 20602)
				player.useStairs(-1, new WorldTile(2969, 9672, 0), 1, 1);
			else if (id == 20608)
				player.useStairs(-1, new WorldTile(3018, 3403, 0), 1, 1);
			else if (id == 4627)
				player.useStairs(-1, new WorldTile(2893, 3567, 0), 1, 1);
			else if (id == 66973)
				player.useStairs(-1, new WorldTile(2206, 4934, 1), 1, 1);
			else if (id == 22666) {
				player.useStairs(-1, player.transform(-5, 0, -3), 1, 1);
				return;
			} else if (id == 22600) {
				player.useStairs(-1, player.transform(5, 0, 3), 1, 1);
				return;
			} else if (id == 6087)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, -3, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(3, 0, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(-3, 0, 1), 1, 1);
					break;
				}
			else if (id == 6088)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, 3, -1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(-3, 0, -1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(3, 0, -1), 1, 1);
					break;
				}
			else if (id == 22937)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(4, 0, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(0, -4, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(-4, 0, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(0, 4, 1), 1, 1);
					break;
				}
			else if (id == 22938)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(-4, 0, -1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(0, 4, -1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(4, 0, -1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(0, -4, -1), 1, 1);
					break;
				}
			else if (id == 22931)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(3, 0, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, -3, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(-3, 0, 1), 1, 1);
					break;
				}
			else if (id == 22932)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(-3, 0, -1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, 3, -1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(3, 0, -1), 1, 1);
					break;
				}
			else if (id == 34548 || id == 22941 || id == 22939)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(3, 0, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(0, -3, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(-3, 0, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
					break;
				}
			else if (id == 34550 || id == 22942 || id == 22940)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(-3, 0, -1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(0, 3, -1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(3, 0, -1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
					break;
				}
			else if (id == 34567)
				player.useStairs(-1, player.transform(object.getRotation() == 3 ? -3 : 3, 0, 1), 1, 1);
			else if (id == 34568)
				player.useStairs(-1, player.transform(object.getRotation() == 3 ? 3 : -3, 0, -1), 1, 1);
			else if (id == 69505)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, 0, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(1, 0, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, 0, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(-1, 0, 1), 1, 1);
					break;
				}
			else if (id == 34498)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, 3, 1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(3, 0, 1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, -3, 1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(-3, 0, 1), 1, 1);
					break;
				}
			else if (id == 34499)
				switch(object.getRotation()) {
				case 0:
					player.useStairs(-1, player.transform(0, -3, -1), 1, 1);
					break;
				case 1:
					player.useStairs(-1, player.transform(-3, 0, -1), 1, 1);
					break;
				case 2:
					player.useStairs(-1, player.transform(0, 3, -1), 1, 1);
					break;
				case 3:
					player.useStairs(-1, player.transform(3, 0, -1), 1, 1);
					break;
				}
			else if (id == 71902)
				player.useStairs(-1, player.transform(object.getRotation() == 1 ? 4 : 0, object.getRotation() == 0 ? 4 : 0, 1), 1, 1);
			else if (id == 71903)
				player.useStairs(-1, player.transform(object.getRotation() == 1 ? -4 : 0, object.getRotation() == 0 ? -4 : 0, -1), 1, 1);
			else if (id == 26518) {
				if (x == 1991 && y == 4175)
					player.useStairs(828, new WorldTile(1991, 4176, 1), 1, 2);
				else if (x == 1998 && y == 4218)
					player.useStairs(828, new WorldTile(1998, 4219, 1), 1, 2);
				else if (x == 2011 && y == 4218)
					player.useStairs(828, new WorldTile(2011, 4219, 1), 1, 2);
				else if (x == 3118 && y == 9643)
					player.useStairs(828, new WorldTile(3118, player.getY() - 6400, 0), 1, 2);
				else
					player.useStairs(828, new WorldTile(x + 1, y, 1), 1, 2);
			} else if (id == 46500 && object.getX() == 3351 && object.getY() == 3415) { // zaros
				// portal
				player.useStairs(-1, new WorldTile(Settings.getConfig().getPlayerRespawnTile().getX(), Settings.getConfig().getPlayerRespawnTile().getY(), Settings.getConfig().getPlayerRespawnTile().getPlane()), 2, 3, "You found your way back to home.");
				player.addWalkSteps(3351, 3415, -1, false);
			} else if (id == 29370 && (object.getX() == 3150 || object.getX() == 3153) && object.getY() == 9906) { // edgeville
				// dungeon
				// cut
				if (player.getSkills().getLevel(Constants.AGILITY) < 53) {
					player.sendMessage("You need an agility level of 53 to use this obstacle.");
					return;
				}
				final boolean running = player.getRun();
				player.setRunHidden(false);
				player.lock(8);
				player.addWalkSteps(x == 3150 ? 3155 : 3149, 9906, -1, false);
				player.sendMessage("You pulled yourself through the pipes.", true);
				WorldTasks.schedule(new WorldTask() {
					boolean secondloop;

					@Override
					public void run() {
						if (!secondloop) {
							secondloop = true;
							player.getAppearance().setBAS(295);
						} else {
							player.getAppearance().setBAS(-1);
							player.setRunHidden(running);
							player.getSkills().addXp(Constants.AGILITY, 7);
							stop();
						}
					}
				}, 0, 5);
			}
			else if (id == 17222 || id == 17223)
				player.useStairs(-1, new WorldTile(2402, 3419, 0), 0, 1);
			else if (id == 17209)
				player.useStairs(-1, new WorldTile(2408, 9812, 0), 0, 1);
			else if (id == 1754 && x == 2594 && y == 3085)
				player.useStairs(827, new WorldTile(2594, 9486, 0), 1, 2);
			else if (id == 1757 && x == 2594 && y == 9485)
				player.useStairs(828, new WorldTile(2594, 3086, 0), 1, 2);
			else if (id == 2811 || id == 2812) {
				player.useStairs(id == 2812 ? 827 : -1, id == 2812 ? new WorldTile(2501, 2989, 0) : new WorldTile(2574, 3029, 0), 1, 2);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						player.getDialogueManager().execute(new SimplePlayerMessage(), "Wow! That tunnel went a long way.");
					}
				});
			} else if (id == 2890 || id == 2892 || id == 2893) {

				if (player.getEquipment().getWeaponId() != 975 && !player.getInventory().containsItem(975, 1) &&
						player.getEquipment().getWeaponId() != 6313 && !player.getInventory().containsItem(6313, 1) &&
						player.getEquipment().getWeaponId() != 6315 && !player.getInventory().containsItem(6315, 1) &&
						player.getEquipment().getWeaponId() != 6317 && !player.getInventory().containsItem(6317, 1)) {
					player.sendMessage("You need a machete in order to cutt through the terrain.");
					return;
				}
				player.setNextAnimation(new Animation(910));
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						if (Utils.random(3) == 0) {
							player.sendMessage("You fail to slash through the terrain.");
							return;
						}
						GameObject o = new GameObject(object);
						o.setId(id + 1);
						World.spawnObjectTemporary(o, 8);
						player.addWalkSteps(object.getX(), object.getY(), 0, false);
					}
				});
			} else if (id == 2231)
				player.useStairs(-1, new WorldTile(x == 2792 ? 2795 : 2791, 2979, 0), 1, 2, x == 2792 ? "You climb down the slope." : "You climb up the slope.");

			// start forinthry dungeon
			else if (id == 18341 && object.getX() == 3036 && object.getY() == 10172)
				player.useStairs(-1, new WorldTile(3039, 3765, 0), 0, 1);
			else if (id == 20599 && object.getX() == 3038 && object.getY() == 3761)
				player.useStairs(-1, new WorldTile(3037, 10171, 0), 0, 1);
			else if (id == 18342 && object.getX() == 3075 && object.getY() == 10057)
				player.useStairs(-1, new WorldTile(3071, 3649, 0), 0, 1);
			else if (id == 20600 && object.getX() == 3072 && object.getY() == 3648)
				player.useStairs(-1, new WorldTile(3077, 10058, 0), 0, 1);
			else if (id == 42219)
				player.useStairs(-1, new WorldTile(1886, 3178, 0), 0, 1);
			else if (id == 8689)
				player.getActionManager().setAction(new CowMilkingAction());
			else if (id == 42220)
				player.useStairs(-1, new WorldTile(3082, 3475, 0), 0, 1);
			// start falador mininig
			else if (id == 30942 && object.getX() == 3019 && object.getY() == 3450)
				player.useStairs(828, new WorldTile(3020, 9850, 0), 1, 2);
			else if (id == 6226 && object.getX() == 3019 && object.getY() == 9850)
				player.useStairs(833, new WorldTile(3018, 3450, 0), 1, 2);
			else if (id == 30943 && object.getX() == 3059 && object.getY() == 9776)
				player.useStairs(-1, new WorldTile(3061, 3376, 0), 0, 1);
			else if (id == 30944 && object.getX() == 3059 && object.getY() == 3376)
				player.useStairs(-1, new WorldTile(3058, 9776, 0), 0, 1);
			else if (id == 2112 && object.getX() == 3046 && object.getY() == 9756) {
				if (player.getSkills().getLevelForXp(Constants.MINING) < 60) {
					player.getDialogueManager().execute(new SimpleNPCMessage(), MiningGuildDwarf.getClosestDwarfID(player), "Sorry, but you need level 60 Mining to go in there.");
					return;
				}
				Doors.handleDoor(player, object);
			} else if (id == 2113) {
				if (player.getSkills().getLevelForXp(Constants.MINING) < 60) {
					player.getDialogueManager().execute(new SimpleNPCMessage(), MiningGuildDwarf.getClosestDwarfID(player), "Sorry, but you need level 60 Mining to go in there.");
					return;
				}
				player.useStairs(-1, new WorldTile(3021, 9739, 0), 0, 1);
			} else if (id == 6226 && object.getX() == 3019 && object.getY() == 9740)
				player.useStairs(828, new WorldTile(3019, 3341, 0), 1, 2);
			else if (id == 6226 && object.getX() == 3019 && object.getY() == 9738)
				player.useStairs(828, new WorldTile(3019, 3337, 0), 1, 2);
			else if (id == 6226 && object.getX() == 3018 && object.getY() == 9739)
				player.useStairs(828, new WorldTile(3017, 3339, 0), 1, 2);
			else if (id == 6226 && object.getX() == 3020 && object.getY() == 9739)
				player.useStairs(828, new WorldTile(3021, 3339, 0), 1, 2);
			else if (id == 46250)
				player.getInventory().addItem(new Item(1550, 1));
			else if (id == 6045)
				player.sendMessage("You search the cart but find nothing.");
			else if (id == 5906) {
				if (player.getSkills().getLevel(Constants.AGILITY) < 42) {
					player.sendMessage("You need an agility level of 42 to use this obstacle.");
					return;
				}
				player.lock();
				WorldTasks.schedule(new WorldTask() {
					int count = 0;

					@Override
					public void run() {
						if (count == 0) {
							player.setNextAnimation(new Animation(2594));
							WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -2 : +2), object.getY(), 0);
							player.setNextForceMovement(new ForceMovement(tile, 4, Direction.forDelta(tile.getX() - player.getX(), tile.getY() - player.getY())));
						} else if (count == 2) {
							WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -2 : +2), object.getY(), 0);
							player.setNextWorldTile(tile);
						} else if (count == 5) {
							player.setNextAnimation(new Animation(2590));
							WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -5 : +5), object.getY(), 0);
							player.setNextForceMovement(new ForceMovement(tile, 4, Direction.forDelta(tile.getX() - player.getX(), tile.getY() - player.getY())));
						} else if (count == 7) {
							WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -5 : +5), object.getY(), 0);
							player.setNextWorldTile(tile);
						} else if (count == 10) {
							player.setNextAnimation(new Animation(2595));
							WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -6 : +6), object.getY(), 0);
							player.setNextForceMovement(new ForceMovement(tile, 4, Direction.forDelta(tile.getX() - player.getX(), tile.getY() - player.getY())));
						} else if (count == 12) {
							WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -6 : +6), object.getY(), 0);
							player.setNextWorldTile(tile);
						} else if (count == 14) {
							stop();
							player.unlock();
						}
						count++;
					}

				}, 0, 0);
			} else if (id == 15478 || id == 15477 || id == 15481 || id == 15479 || id == 15482 || id == 15480)
				player.getDialogueManager().execute(new EnterHouse());
			// rock living caverns
			else if (id == 45077) {
				player.lock();
				if (player.getX() != object.getX() || player.getY() != object.getY())
					player.addWalkSteps(object.getX(), object.getY(), -1, false);
				WorldTasks.schedule(new WorldTask() {

					private int count;

					@Override
					public void run() {
						if (count == 0) {
							player.setNextFaceWorldTile(new WorldTile(object.getX() - 1, object.getY(), 0));
							player.setNextAnimation(new Animation(12216));
							player.unlock();
						} else if (count == 2) {
							player.setNextWorldTile(new WorldTile(3651, 5122, 0));
							player.setNextFaceWorldTile(new WorldTile(3651, 5121, 0));
							player.setNextAnimation(new Animation(12217));
						} else if (count == 3) {
							// TODO find emote
							// player.getPackets().sendObjectAnimation(new
							// WorldObject(45078, 0, 3, 3651, 5123, 0), new
							// Animation(12220));
						} else if (count == 5) {
							player.unlock();
							stop();
						}
						count++;
					}

				}, 1, 0);
			} else if (id == 65367)
				WildernessAgility.GateWalk2(player, object);
			else if (id == 65365)
				WildernessAgility.GateWalk(player, object);
			else if (id == 65734)
				WildernessAgility.climbCliff(player, object);
			else if (id == 65362)
				WildernessAgility.enterObstaclePipe(player, object.getX(), object.getY());
			else if (id == 64696)
				WildernessAgility.swingOnRopeSwing(player, object);
			else if (id == 64698)
				WildernessAgility.walkLog(player);
			else if (id == 64699)
				WildernessAgility.crossSteppingPalletes(player, object);
			else if (id == 45078)
				player.useStairs(2413, new WorldTile(3012, 9832, 0), 2, 2);
			// champion guild
			else if (id == 24357 && object.getX() == 3188 && object.getY() == 3355)
				player.useStairs(-1, new WorldTile(3189, 3354, 1), 0, 1);
			else if (id == 24359 && object.getX() == 3188 && object.getY() == 3355)
				player.useStairs(-1, new WorldTile(3189, 3358, 0), 0, 1);
			// start of varrock dungeon
			else if (id == 29355 && object.getX() == 3230 && object.getY() == 9904) // varrock
				// dungeon
				// climb
				// to
				// bear
				player.useStairs(828, new WorldTile(3229, 3503, 0), 1, 2);
			else if (id == 24264)
				player.useStairs(833, new WorldTile(3229, 9904, 0), 1, 2);
			else if (id == 24366)
				player.useStairs(828, new WorldTile(3237, 3459, 0), 1, 2);
			else if (id == 29355 && object.getX() == 3097 && object.getY() == 9867) // edge
				// dungeon
				// climb
				player.useStairs(828, new WorldTile(3096, 3468, 0), 1, 2);
			else if (id == 26934)
				player.useStairs(833, new WorldTile(3097, 9868, 0), 1, 2);
			else if (id == 29355 && object.getX() == 3088 && object.getY() == 9971)
				player.useStairs(828, new WorldTile(3087, 3571, 0), 1, 2);
			else if (id == 65453)
				player.useStairs(833, new WorldTile(3089, 9971, 0), 1, 2);
			else if (id == 12389 && object.getX() == 3116 && object.getY() == 3452)
				player.useStairs(833, new WorldTile(3117, 9852, 0), 1, 2);
			else if (id == 29355 && object.getX() == 3116 && object.getY() == 9852)
				player.useStairs(833, new WorldTile(3115, 3452, 0), 1, 2);
			else if (WildernessController.isDitch(id))
				player.getDialogueManager().execute(new WildernessDitch(), object);
			else if (id == 42611)
				player.getDialogueManager().execute(new MagicPortal());
			else if (object.getDefinitions(player).getName().equalsIgnoreCase("Obelisk") && object.getY() > 3525)
				// Who the fuck removed the controller class and the code
				// from SONIC!!!!!!!!!!
				// That was an hour of collecting coords :fp: Now ima kill
				// myself.
				WildernessObelisk.activateObelisk(object.getId(), player);
			else if (id >= 8958 && id <= 8960)
				//						List<Integer> pIndex = World.getRegion(object.getRegionId()).getPlayerIndexes();
				//						if (pIndex != null) {
				//							for (Integer i : pIndex) {
				//								Player p = World.getPlayers().get(i);
				//								if (p == null || p == player || !Utils.isOnRange(p.getX(), p.getY(), p.getSize(), object.getX(), object.getY(), 3, 0))
				//									continue;
				//								player.lock(1);
				World.removeObjectTemporary(object, Ticks.fromMinutes(1));
			//								return;
			//							}
			//						}
			//						player.sendMessage("You cannot see a way to open this door...");
			else if (id == 10177 && x == 2546 && y == 10143)
				player.getDialogueManager().execute(new ClimbEmoteStairs(), new WorldTile(2544, 3741, 0), new WorldTile(1798, 4407, 3), "Go up the stairs.", "Go down the stairs.", 828);
			else if ((id == 10193 && x == 1798 && y == 4406) || (id == 8930 && x == 2542 && y == 3740))
				player.useStairs(-1, new WorldTile(2545, 10143, 0), 0, 1);
			else if (id == 10195 && x == 1808 && y == 4405)
				player.useStairs(-1, new WorldTile(1810, 4405, 2), 0, 1);
			else if (id == 10196 && x == 1809 && y == 4405)
				player.useStairs(-1, new WorldTile(1807, 4405, 3), 0, 1);
			else if (id == 10198 && x == 1823 && y == 4404)
				player.useStairs(-1, new WorldTile(1825, 4404, 3), 0, 1);
			else if (id == 10197 && x == 1824 && y == 4404)
				player.useStairs(-1, new WorldTile(1823, 4404, 2), 0, 1);
			else if (id == 10199 && x == 1834 && y == 4389)
				player.useStairs(-1, new WorldTile(1834, 4388, 2), 0, 1);
			else if (id == 10200 && x == 1834 && y == 4388)
				player.useStairs(-1, new WorldTile(1834, 4390, 3), 0, 1);
			else if (id == 10201 && x == 1811 && y == 4394)
				player.useStairs(-1, new WorldTile(1810, 4394, 1), 0, 1);
			else if (id == 10202 && x == 1810 && y == 4394)
				player.useStairs(-1, new WorldTile(1812, 4394, 2), 0, 1);
			else if (id == 10203 && x == 1799 && y == 4388)
				player.useStairs(-1, new WorldTile(1799, 4386, 2), 0, 1);
			else if (id == 10204 && x == 1799 && y == 4387)
				player.useStairs(-1, new WorldTile(1799, 4389, 1), 0, 1);
			else if (id == 10205 && x == 1797 && y == 4382)
				player.useStairs(-1, new WorldTile(1797, 4382, 1), 0, 1);
			else if (id == 10206 && x == 1798 && y == 4382)
				player.useStairs(-1, new WorldTile(1796, 4382, 2), 0, 1);
			else if (id == 10207 && x == 1802 && y == 4369)
				player.useStairs(-1, new WorldTile(1800, 4369, 2), 0, 1);
			else if (id == 10208 && x == 1801 && y == 4369)
				player.useStairs(-1, new WorldTile(1802, 4369, 1), 0, 1);
			else if (id == 10209 && x == 1826 && y == 4362)
				player.useStairs(-1, new WorldTile(1828, 4362, 1), 0, 1);
			else if (id == 10210 && x == 1827 && y == 4362)
				player.useStairs(-1, new WorldTile(1825, 4362, 2), 0, 1);
			else if (id == 10211 && x == 1863 && y == 4371)
				player.useStairs(-1, new WorldTile(1863, 4373, 2), 0, 1);
			else if (id == 10212 && x == 1863 && y == 4372)
				player.useStairs(-1, new WorldTile(1863, 4370, 1), 0, 1);
			else if (id == 10213 && x == 1864 && y == 4388)
				player.useStairs(-1, new WorldTile(1864, 4389, 1), 0, 1);
			else if (id == 10214 && x == 1864 && y == 4389)
				player.useStairs(-1, new WorldTile(1864, 4387, 2), 0, 1);
			else if (id == 10215 && x == 1890 && y == 4407)
				player.useStairs(-1, new WorldTile(1890, 4408, 0), 0, 1);
			else if (id == 10216 && x == 1890 && y == 4408)
				player.useStairs(-1, new WorldTile(1890, 4406, 1), 0, 1);
			else if (id == 10230 && x == 1911 && y == 4367)
				// kings
				// entrance
				//BossInstanceHandler.enterInstance(player, Boss.Dagannoth_Kings);
				player.useStairs(-1, new WorldTile(2900, 4449, 0), 0, 1);
			else if (id == 10229 && x == 2899 && y == 4449)
				player.useStairs(-1, new WorldTile(1912, 4367, 0), 0, 1);
			else if (id == 10217 && x == 1957 && y == 4371)
				player.useStairs(-1, new WorldTile(1957, 4373, 1), 0, 1);
			else if (id == 10218 && x == 1957 && y == 4372)
				player.useStairs(-1, new WorldTile(1957, 4370, 0), 0, 1);
			else if (id == 10226 && x == 1932 && y == 4378)
				player.useStairs(-1, new WorldTile(1932, 4380, 2), 0, 1);
			else if (id == 10225 && x == 1932 && y == 4379)
				player.useStairs(-1, new WorldTile(1932, 4377, 1), 0, 1);
			else if (id == 10228 && x == 1961 && y == 4391)
				player.useStairs(-1, new WorldTile(1961, 4393, 3), 0, 1);
			else if (id == 10227 && x == 1961 && y == 4392)
				player.useStairs(-1, new WorldTile(1961, 4392, 2), 0, 1);
			else if (id == 10194 && x == 1975 && y == 4408)
				player.useStairs(-1, new WorldTile(2501, 3636, 0), 0, 1);
			else if (id == 10219 && x == 1824 && y == 4381)
				player.useStairs(-1, new WorldTile(1824, 4379, 3), 0, 1);
			else if (id == 10220 && x == 1824 && y == 4380)
				player.useStairs(-1, new WorldTile(1824, 4382, 2), 0, 1);
			else if (id == 10221 && x == 1838 && y == 4376)
				player.useStairs(-1, new WorldTile(1838, 4374, 2), 0, 1);
			else if (id == 10222 && x == 1838 && y == 4375)
				player.useStairs(-1, new WorldTile(1838, 4377, 3), 0, 1);
			else if (id == 10223 && x == 1850 && y == 4386)
				player.useStairs(-1, new WorldTile(1850, 4385, 1), 0, 1);
			else if (id == 10224 && x == 1850 && y == 4385)
				player.useStairs(-1, new WorldTile(1850, 4387, 2), 0, 1);
			// White Wolf Mountain cut
			else if (id == 56 && x == 2876 && y == 9880)
				player.useStairs(-1, new WorldTile(2879, 3465, 0), 0, 1);
			else if (id == 66990 && x == 2876 && y == 3462)
				player.useStairs(-1, new WorldTile(2875, 9880, 0), 0, 1);
			else if (id == 54 && x == 2820 && y == 9883)
				player.useStairs(-1, new WorldTile(2820, 3486, 0), 0, 1);
			else if (id == 55 && x == 2820 && y == 3484)
				player.useStairs(-1, new WorldTile(2821, 9882, 0), 0, 1);
			// sabbot lair
			else if (id == 19690)
				player.useStairs(-1, player.transform(0, 4, 1), 0, 1);
			else if (id == 19691)
				player.useStairs(-1, player.transform(0, -4, -1), 0, 1);
			else if (id == 61336) {
				final int maxPrayer2 = player.getSkills().getLevelForXp(Constants.PRAYER) * 10;
				if (player.getPrayer().getPoints() < maxPrayer2) {
					player.lock(5);
					player.sendMessage("You pray to the gods...", true);
					player.setNextAnimation(new Animation(645));
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							player.getPrayer().restorePrayer(maxPrayer2);
							player.sendMessage("...and recharged your prayer.", true);
						}
					}, 2);
				} else
					player.sendMessage("You already have full prayer.");

			} else if (id == 2878 || id == 2879) {
				player.getDialogueManager().execute(new SimpleMessage(), "You step into the pool of sparkling water. You feel the energy rush through your veins.");
				final boolean isLeaving = id == 2879;
				final WorldTile tile = isLeaving ? new WorldTile(2509, 4687, 0) : new WorldTile(2542, 4720, 0);
				player.setNextForceMovement(new ForceMovement(player, 1, tile, 2, isLeaving ? Direction.SOUTH : Direction.NORTH));
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						player.setNextAnimation(new Animation(13842));
						WorldTasks.schedule(new WorldTask() {

							@Override
							public void run() {
								player.setNextAnimation(new Animation(-1));
								player.setNextWorldTile(isLeaving ? new WorldTile(2542, 4718, 0) : new WorldTile(2509, 4689, 0));
							}
						}, 2);
					}
				});

			} else if (id == 2873 || id == 2874 || id == 2875) {
				player.sendMessage("You kneel and begin to chant to " + objectDef.getName().replace("Statue of ", "") + "...");
				player.setNextAnimation(new Animation(645));
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						player.getDialogueManager().execute(new SimpleMessage(), "You feel a rush of energy charge through your veins. Suddenly a cape appears before you.");
						World.sendSpotAnim(player, new SpotAnim(1605), new WorldTile(object.getX(), object.getY() - 1, 0));
						World.addGroundItem(new Item(id == 2873 ? 2412 : id == 2874 ? 2414 : 2413), new WorldTile(object.getX(), object.getY() - 1, 0));
					}
				}, 3);
			} else if (id == 49016 || id == 49014) {
				if (player.getSkills().getLevel(Constants.FISHING) < 68) {
					player.sendMessage("You need a Fishing level of 68 in order to pass through this gate.");
					return;
				}
				Doors.handleDoubleDoor(player, object);
			} else if (id == 9738 || id == 9330) {
				boolean rightDoor = object.getId() == 9330;
				GameObject o = new GameObject(object);
				o.setRotation(rightDoor ? -1 : 1);
				World.spawnObjectTemporary(o, 2);
				GameObject o2 = new GameObject(rightDoor ? 9738 : 9330, object.getType(), object.getRotation(), 2558, rightDoor ? 3299 : 3300, object.getPlane());
				o2.setRotation(rightDoor ? 1 : 3);
				World.spawnObjectTemporary(o2, 2);
				player.addWalkSteps(player.getX() + (player.getX() >= 2559 ? -3 : 3), y, -1, false);
			} else if (id == 70794)
				player.useStairs(-1, new WorldTile(1340, 6488, 0), 1, 2);
			else if (id == 70795) {
				if (!Agility.hasLevel(player, 50))
					return;
				player.getDialogueManager().execute(new GrotwormLairD(), true);
			} else if (id == 70812)
				player.getDialogueManager().execute(new GrotwormLairD(), false);
			else if (id == 70799)
				player.useStairs(-1, new WorldTile(1178, 6355, 0), 1, 2);
			else if (id == 70796)
				player.useStairs(-1, new WorldTile(1090, 6360, 0), 1, 2);
			else if (id == 70798)
				player.useStairs(-1, new WorldTile(1340, 6380, 0), 1, 2);
			else if (id == 70797)
				player.useStairs(-1, new WorldTile(1090, 6497, 0), 1, 2);
			else if (id == 70792)
				player.useStairs(-1, new WorldTile(1206, 6371, 0), 1, 2);
			else if (id == 70793)
				player.useStairs(-1, new WorldTile(2989, 3237, 0), 1, 2);
			else if (id == 12202) {// mole entrance
				if (!player.getInventory().containsItem(952, 1)) {
					player.sendMessage("You need a spade to dig this.");
					return;
				}
				if (player.getX() != object.getX() || player.getY() != object.getY()) {
					player.lock();
					player.addWalkSteps(object.getX(), object.getY());
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							InventoryOptionsHandler.dig(player);
						}

					}, 1);
				} else
					InventoryOptionsHandler.dig(player);
			} else if (id == 11724)
				player.useStairs(-1, new WorldTile(2968, 3348, 1), 0, 1);
			else if (id == 11725)
				player.useStairs(-1, new WorldTile(2971, 3347, 0), 0, 1);
			else if (id == 8929)
				player.useStairs(-1, new WorldTile(2442, 10147, 0), 0, 1);
			else if (id == 8966)
				player.useStairs(-1, new WorldTile(2523, 3740, 0), 0, 1);
			else if (id == 29728)
				player.useStairs(-1, new WorldTile(3158, 4280, 3), 0, 1);
			else if (id == 29729)
				player.useStairs(-1, new WorldTile(3078, 3463, 0), 0, 1);
			else if (id == 29672)
				player.useStairs(-1, new WorldTile(3171, 4271, 3), 0, 1);
			else if (id == 29671)
				player.useStairs(-1, new WorldTile(3174, 4273, 2), 0, 1);
			else if (id == 23158)
				player.useStairs(-1, new WorldTile(2730, 3734, 0), 0, 1);
			else if (id == 11355)
				player.useStairs(-1, new WorldTile(2677, 5214, 2), 0, 1);
			else if (id == 11356)
				player.useStairs(-1, new WorldTile(3110, 3363, 2), 0, 1);
			else if (id == 15811 || id == 15812)
				player.useStairs(-1, new WorldTile(3749, 2973, 0), 0, 1);
			else if (id == 63093)
				player.useStairs(-1, new WorldTile(4620, 5458, 3), 0, 1);
			else if (id == 63094)
				player.useStairs(-1, new WorldTile(3410, 3329, 0), 0, 1);
			else if (id == 2147)
				player.ladder(new WorldTile(3104, 9576, 0));
			else if (id == 5492)
				player.ladder(new WorldTile(3149, 9652, 0));
			else if (id == 5493)
				player.ladder(new WorldTile(3165, 3251, 0));
			else if (id == 68983) {
				Doors.handleInPlaceSingleDoor(player, object);
				player.resetWalkSteps();
				player.addWalkSteps(2461, player.getY() > object.getY() ? object.getY() - 1 : object.getY() + 3, -1, false);
			} else if (id == 12230 && object.getX() == 1752 && object.getY() == 5136)
				player.setNextWorldTile(new WorldTile(2996, 3378, 0));
			else if (id == 38811 || id == 37929) {// corp beast
				if (object.getX() == 2971 && object.getY() == 4382)
					player.getInterfaceManager().sendInterface(650);
				else if (object.getX() == 2918 && object.getY() == 4382) {
					player.stopAll();
					player.setNextWorldTile(new WorldTile(player.getX() == 2921 ? 2917 : 2921, player.getY(), player.getPlane()));
				}
			} else if (id == 37928 && object.getX() == 2883 && object.getY() == 4370) {
				player.stopAll();
				player.setNextWorldTile(new WorldTile(3214, 3782, 0));
				player.getControllerManager().startController(new WildernessController());
			} else if (id == 38815 && object.getX() == 3209 && object.getY() == 3780 && object.getPlane() == 0) {
				if (player.getSkills().getLevelForXp(Constants.WOODCUTTING) < 37 || player.getSkills().getLevelForXp(Constants.MINING) < 45 || player.getSkills().getLevelForXp(Constants.SUMMONING) < 23
						|| player.getSkills().getLevelForXp(Constants.FIREMAKING) < 47 || player.getSkills().getLevelForXp(Constants.PRAYER) < 55) {
					player.sendMessage("You need 23 Summoning, 37 Woodcutting, 45 Mining, 47 Firemaking and 55 Prayer to enter this dungeon.");
					return;
				}
				player.stopAll();
				player.setNextWorldTile(new WorldTile(2885, 4372, 2));
				player.getControllerManager().forceStop();
				// TODO all reqs, skills not added
			} else if (id == 48803 && player.isKalphiteLairSetted())
				player.setNextWorldTile(new WorldTile(3508, 9494, 0));
			else if (id == 48802 && player.isKalphiteLairEntranceSetted())
				player.setNextWorldTile(new WorldTile(3483, 9510, 2));
			else if (id == 3829) {
				if (object.getX() == 3483 && object.getY() == 9510)
					player.useStairs(828, new WorldTile(3226, 3108, 0), 1, 2);
			} else if (id == 3832) {
				if (object.getX() == 3508 && object.getY() == 9494)
					player.useStairs(828, new WorldTile(3509, 9496, 2), 1, 2);
			} else if (id == 14315)
				player.getControllerManager().startController(new PestControlLobbyController(1));
			else if (id == 5959)
				Magic.pushLeverTeleport(player, new WorldTile(2539, 4712, 0));
			else if (id == 5960)
				Magic.pushLeverTeleport(player, new WorldTile(3089, 3957, 0));
			else if (id == 1814)
				Magic.pushLeverTeleport(player, new WorldTile(3155, 3923, 0));
			else if (id == 1815)
				Magic.pushLeverTeleport(player, new WorldTile(2561, 3311, 0));
			else if (id == 62675)
				player.getCutscenesManager().play(new DTPreview());
			else if (id == 62678 || id == 62679)
				player.getDominionTower().openModes();
			else if (id == 62688)
				player.getDialogueManager().execute(new DTClaimRewards());
			else if (id == 62677)
				player.getDominionTower().talkToFace();
			else if (id == 62680)
				player.getDominionTower().openBankChest();
			else if (id == 48797)
				player.useStairs(-1, new WorldTile(3877, 5526, 1), 0, 1);
			else if (id == 48798)
				player.useStairs(-1, new WorldTile(3246, 3198, 0), 0, 1);
			else if (id == 48678 && x == 3858 && y == 5533)
				player.useStairs(-1, new WorldTile(3861, 5533, 0), 0, 1);
			else if (id == 48678 && x == 3858 && y == 5543)
				player.useStairs(-1, new WorldTile(3861, 5543, 0), 0, 1);
			else if (id == 48678 && x == 3858 && y == 5533)
				player.useStairs(-1, new WorldTile(3861, 5533, 0), 0, 1);
			else if (id == 48677 && x == 3858 && y == 5543)
				player.useStairs(-1, new WorldTile(3856, 5543, 1), 0, 1);
			else if (id == 48677 && x == 3858 && y == 5533)
				player.useStairs(-1, new WorldTile(3856, 5533, 1), 0, 1);
			else if (id == 48679)
				player.useStairs(-1, new WorldTile(3875, 5527, 1), 0, 1);
			else if (id == 48688)
				player.useStairs(-1, new WorldTile(3972, 5565, 0), 0, 1);
			else if (id == 48683)
				player.useStairs(-1, new WorldTile(3868, 5524, 0), 0, 1);
			else if (id == 48682)
				player.useStairs(-1, new WorldTile(3869, 5524, 0), 0, 1);
			else if (id == 62676)
				player.useStairs(-1, new WorldTile(3374, 3093, 0), 0, 1);
			else if (id == 62674)
				player.useStairs(-1, new WorldTile(3744, 6405, 0), 0, 1);
			else if (id == 65349)
				player.useStairs(-1, new WorldTile(3044, 10325, 0), 0, 1);
			else if (id == 32048 && object.getX() == 3043 && object.getY() == 10328)
				player.useStairs(-1, new WorldTile(3045, 3927, 0), 0, 1);
			else if (id == 2348)
				player.setNextWorldTile(player.transform(object.getRotation() == 3 ? -3 : 3, 0, -1));
			else if (id == 2347)
				player.setNextWorldTile(player.transform(object.getRotation() == 3 ? 3 : -3, 0, 1));
			else if (id == 26194)
				player.getDialogueManager().execute(new PartyRoomLever());
			//start chaos tunnels
			else if (id == 77745 || id == 28779) {
				if(x == 3254 && y == 5451)
					player.setNextWorldTile(new WorldTile(3250, 5448, 0));
				if(x == 3250 && y == 5448)
					player.setNextWorldTile(new WorldTile(3254, 5451, 0));
				if(x == 3241 && y == 5445)
					player.setNextWorldTile(new WorldTile(3233, 5445, 0));
				if(x == 3233 && y == 5445)
					player.setNextWorldTile(new WorldTile(3241, 5445, 0));
				if(x == 3259 && y == 5446)
					player.setNextWorldTile(new WorldTile(3265, 5491, 0));
				if(x == 3265 && y == 5491)
					player.setNextWorldTile(new WorldTile(3259, 5446, 0));
				if(x == 3260 && y == 5491)
					player.setNextWorldTile(new WorldTile(3266, 5446, 0));
				if(x == 3266 && y == 5446)
					player.setNextWorldTile(new WorldTile(3260, 5491, 0));
				if(x == 3241 && y == 5469)
					player.setNextWorldTile(new WorldTile(3233, 5470, 0));
				if(x == 3233 && y == 5470)
					player.setNextWorldTile(new WorldTile(3241, 5469, 0));
				if(x == 3235 && y == 5457)
					player.setNextWorldTile(new WorldTile(3229, 5454, 0));
				if(x == 3229 && y == 5454)
					player.setNextWorldTile(new WorldTile(3235, 5457, 0));
				if(x == 3280 && y == 5460)
					player.setNextWorldTile(new WorldTile(3273, 5460, 0));
				if(x == 3273 && y == 5460)
					player.setNextWorldTile(new WorldTile(3280, 5460, 0));
				if(x == 3283 && y == 5448)
					player.setNextWorldTile(new WorldTile(3287, 5448, 0));
				if(x == 3287 && y == 5448)
					player.setNextWorldTile(new WorldTile(3283, 5448, 0));
				if(x == 3244 && y == 5495)
					player.setNextWorldTile(new WorldTile(3239, 5498, 0));
				if(x == 3239 && y == 5498)
					player.setNextWorldTile(new WorldTile(3244, 5495, 0));
				if(x == 3232 && y == 5501)
					player.setNextWorldTile(new WorldTile(3238, 5507, 0));
				if(x == 3238 && y == 5507)
					player.setNextWorldTile(new WorldTile(3232, 5501, 0));
				if(x == 3218 && y == 5497)
					player.setNextWorldTile(new WorldTile(3222, 5488, 0));
				if(x == 3222 && y == 5488)
					player.setNextWorldTile(new WorldTile(3218, 5497, 0));
				if(x == 3218 && y == 5478)
					player.setNextWorldTile(new WorldTile(3215, 5475, 0));
				if(x == 3215 && y == 5475)
					player.setNextWorldTile(new WorldTile(3218, 5478, 0));
				if(x == 3224 && y == 5479)
					player.setNextWorldTile(new WorldTile(3222, 5474, 0));
				if(x == 3222 && y == 5474)
					player.setNextWorldTile(new WorldTile(3224, 5479, 0));
				if(x == 3208 && y == 5471)
					player.setNextWorldTile(new WorldTile(3210, 5477, 0));
				if(x == 3210 && y == 5477)
					player.setNextWorldTile(new WorldTile(3208, 5471, 0));
				if(x == 3214 && y == 5456)
					player.setNextWorldTile(new WorldTile(3212, 5452, 0));
				if(x == 3212 && y == 5452)
					player.setNextWorldTile(new WorldTile(3214, 5456, 0));
				if(x == 3204 && y == 5445)
					player.setNextWorldTile(new WorldTile(3197, 5448, 0));
				if(x == 3197 && y == 5448)
					player.setNextWorldTile(new WorldTile(3204, 5445, 0));
				if(x == 3189 && y == 5444)
					player.setNextWorldTile(new WorldTile(3187, 5460, 0));
				if(x == 3187 && y == 5460)
					player.setNextWorldTile(new WorldTile(3189, 5444, 0));
				if(x == 3192 && y == 5472)
					player.setNextWorldTile(new WorldTile(3186, 5472, 0));
				if(x == 3186 && y == 5472)
					player.setNextWorldTile(new WorldTile(3192, 5472, 0));
				if(x == 3185 && y == 5478)
					player.setNextWorldTile(new WorldTile(3191, 5482, 0));
				if(x == 3191 && y == 5482)
					player.setNextWorldTile(new WorldTile(3185, 5478, 0));
				if(x == 3171 && y == 5473)
					player.setNextWorldTile(new WorldTile(3167, 5471, 0));
				if(x == 3167 && y == 5471)
					player.setNextWorldTile(new WorldTile(3171, 5473, 0));
				if(x == 3171 && y == 5478)
					player.setNextWorldTile(new WorldTile(3167, 5478, 0));
				if(x == 3167 && y == 5478)
					player.setNextWorldTile(new WorldTile(3171, 5478, 0));
				if(x == 3168 && y == 5456)
					player.setNextWorldTile(new WorldTile(3178, 5460, 0));
				if(x == 3178 && y == 5460)
					player.setNextWorldTile(new WorldTile(3168, 5456, 0));
				if(x == 3191 && y == 5495)
					player.setNextWorldTile(new WorldTile(3194, 5490, 0));
				if(x == 3194 && y == 5490)
					player.setNextWorldTile(new WorldTile(3191, 5495, 0));
				if(x == 3141 && y == 5480)
					player.setNextWorldTile(new WorldTile(3142, 5489, 0));
				if(x == 3142 && y == 5489)
					player.setNextWorldTile(new WorldTile(3141, 5480, 0));
				if(x == 3142 && y == 5462)
					player.setNextWorldTile(new WorldTile(3154, 5462, 0));
				if(x == 3154 && y == 5462)
					player.setNextWorldTile(new WorldTile(3142, 5462, 0));
				if(x == 3143 && y == 5443)
					player.setNextWorldTile(new WorldTile(3155, 5449, 0));
				if(x == 3155 && y == 5449)
					player.setNextWorldTile(new WorldTile(3143, 5443, 0));
				if(x == 3307 && y == 5496)
					player.setNextWorldTile(new WorldTile(3317, 5496, 0));
				if(x == 3317 && y == 5496)
					player.setNextWorldTile(new WorldTile(3307, 5496, 0));
				if(x == 3318 && y == 5481)
					player.setNextWorldTile(new WorldTile(3322, 5480, 0));
				if(x == 3322 && y == 5480)
					player.setNextWorldTile(new WorldTile(3318, 5481, 0));
				if(x == 3299 && y == 5484)
					player.setNextWorldTile(new WorldTile(3303, 5477, 0));
				if(x == 3303 && y == 5477)
					player.setNextWorldTile(new WorldTile(3299, 5484, 0));
				if(x == 3286 && y == 5470)
					player.setNextWorldTile(new WorldTile(3285, 5474, 0));
				if(x == 3285 && y == 5474)
					player.setNextWorldTile(new WorldTile(3286, 5470, 0));
				if(x == 3290 && y == 5463)
					player.setNextWorldTile(new WorldTile(3302, 5469, 0));
				if(x == 3302 && y == 5469)
					player.setNextWorldTile(new WorldTile(3290, 5463, 0));
				if(x == 3296 && y == 5455)
					player.setNextWorldTile(new WorldTile(3299, 5450, 0));
				if(x == 3299 && y == 5450)
					player.setNextWorldTile(new WorldTile(3296, 5455, 0));
				if(x == 3280 && y == 5501)
					player.setNextWorldTile(new WorldTile(3285, 5508, 0));
				if(x == 3285 && y == 5508)
					player.setNextWorldTile(new WorldTile(3280, 5501, 0));
				if(x == 3300 && y == 5514)
					player.setNextWorldTile(new WorldTile(3297, 5510, 0));
				if(x == 3297 && y == 5510)
					player.setNextWorldTile(new WorldTile(3300, 5514, 0));
				if(x == 3289 && y == 5533)
					player.setNextWorldTile(new WorldTile(3288, 5536, 0));
				if(x == 3288 && y == 5536)
					player.setNextWorldTile(new WorldTile(3289, 5533, 0));
				if(x == 3285 && y == 5527)
					player.setNextWorldTile(new WorldTile(3282, 5531, 0));
				if(x == 3282 && y == 5531)
					player.setNextWorldTile(new WorldTile(3285, 5527, 0));
				if(x == 3325 && y == 5518)
					player.setNextWorldTile(new WorldTile(3323, 5531, 0));
				if(x == 3323 && y == 5531)
					player.setNextWorldTile(new WorldTile(3325, 5518, 0));
				if(x == 3299 && y == 5533)
					player.setNextWorldTile(new WorldTile(3297, 5536, 0));
				if(x == 3297 && y == 5538)
					player.setNextWorldTile(new WorldTile(3299, 5533, 0));
				if(x == 3321 && y == 5554)
					player.setNextWorldTile(new WorldTile(3315, 5552, 0));
				if(x == 3315 && y == 5552)
					player.setNextWorldTile(new WorldTile(3321, 5554, 0));
				if(x == 3291 && y == 5555)
					player.setNextWorldTile(new WorldTile(3285, 5556, 0));
				if(x == 3285 && y == 5556)
					player.setNextWorldTile(new WorldTile(3291, 5555, 0));
				if(x == 3266 && y == 5552)
					player.setNextWorldTile(new WorldTile(3262, 5552, 0));
				if(x == 3262 && y == 5552)
					player.setNextWorldTile(new WorldTile(3266, 5552, 0));
				if(x == 3256 && y == 5561)
					player.setNextWorldTile(new WorldTile(3253, 5561, 0));
				if(x == 3253 && y == 5561)
					player.setNextWorldTile(new WorldTile(3256, 5561, 0));
				if(x == 3249 && y == 5546)
					player.setNextWorldTile(new WorldTile(3252, 5543, 0));
				if(x == 3252 && y == 5543)
					player.setNextWorldTile(new WorldTile(3249, 5546, 0));
				if(x == 3261 && y == 5536)
					player.setNextWorldTile(new WorldTile(3268, 5534, 0));
				if(x == 3268 && y == 5534)
					player.setNextWorldTile(new WorldTile(3261, 5536, 0));
				if(x == 3243 && y == 5526)
					player.setNextWorldTile(new WorldTile(3241, 5529, 0));
				if(x == 3241 && y == 5529)
					player.setNextWorldTile(new WorldTile(3243, 5526, 0));
				if(x == 3230 && y == 5547)
					player.setNextWorldTile(new WorldTile(3226, 5553, 0));
				if(x == 3226 && y == 5553)
					player.setNextWorldTile(new WorldTile(3230, 5547, 0));
				if(x == 3206 && y == 5553)
					player.setNextWorldTile(new WorldTile(3204, 5546, 0));
				if(x == 3204 && y == 5546)
					player.setNextWorldTile(new WorldTile(3206, 5553, 0));
				if(x == 3211 && y == 5533)
					player.setNextWorldTile(new WorldTile(3214, 5533, 0));
				if(x == 3214 && y == 5533)
					player.setNextWorldTile(new WorldTile(3211, 5533, 0));
				if(x == 3208 && y == 5527)
					player.setNextWorldTile(new WorldTile(3211, 5523, 0));
				if(x == 3211 && y == 5523)
					player.setNextWorldTile(new WorldTile(3208, 5527, 0));
				if(x == 3201 && y == 5531)
					player.setNextWorldTile(new WorldTile(3197, 5529, 0));
				if(x == 3197 && y == 5529)
					player.setNextWorldTile(new WorldTile(3201, 5531, 0));
				if(x == 3202 && y == 5515)
					player.setNextWorldTile(new WorldTile(3196, 5512, 0));
				if(x == 3196 && y == 5512)
					player.setNextWorldTile(new WorldTile(3202, 5515, 0));
				if(x == 3190 && y == 5515)
					player.setNextWorldTile(new WorldTile(3190, 5519, 0));
				if(x == 3190 && y == 5519)
					player.setNextWorldTile(new WorldTile(3190, 5515, 0));
				if(x == 3185 && y == 5518)
					player.setNextWorldTile(new WorldTile(3181, 5517, 0));
				if(x == 3181 && y == 5517)
					player.setNextWorldTile(new WorldTile(3185, 5518, 0));
				if(x == 3187 && y == 5531)
					player.setNextWorldTile(new WorldTile(3182, 5530, 0));
				if(x == 3182 && y == 5530)
					player.setNextWorldTile(new WorldTile(3187, 5531, 0));
				if(x == 3169 && y == 5510)
					player.setNextWorldTile(new WorldTile(3159, 5501, 0));
				if(x == 3159 && y == 5501)
					player.setNextWorldTile(new WorldTile(3169, 5510, 0));
				if(x == 3165 && y == 5515)
					player.setNextWorldTile(new WorldTile(3173, 5530, 0));
				if(x == 3173 && y == 5530)
					player.setNextWorldTile(new WorldTile(3165, 5515, 0));
				if(x == 3156 && y == 5523)
					player.setNextWorldTile(new WorldTile(3152, 5520, 0));
				if(x == 3152 && y == 5520)
					player.setNextWorldTile(new WorldTile(3156, 5523, 0));
				if(x == 3148 && y == 5533)
					player.setNextWorldTile(new WorldTile(3153, 5537, 0));
				if(x == 3153 && y == 5537)
					player.setNextWorldTile(new WorldTile(3148, 5533, 0));
				if(x == 3143 && y == 5535)
					player.setNextWorldTile(new WorldTile(3147, 5541, 0));
				if(x == 3147 && y == 5541)
					player.setNextWorldTile(new WorldTile(3143, 5535, 0));
				if(x == 3168 && y == 5541)
					player.setNextWorldTile(new WorldTile(3171, 5542, 0));
				if(x == 3171 && y == 5542)
					player.setNextWorldTile(new WorldTile(3168, 5541, 0));
				if(x == 3190 && y == 5549)
					player.setNextWorldTile(new WorldTile(3190, 5554, 0));
				if(x == 3190 && y == 5554)
					player.setNextWorldTile(new WorldTile(3190, 5549, 0));
				if(x == 3180 && y == 5557)
					player.setNextWorldTile(new WorldTile(3174, 5558, 0));
				if(x == 3174 && y == 5558)
					player.setNextWorldTile(new WorldTile(3180, 5557, 0));
				if(x == 3162 && y == 5557)
					player.setNextWorldTile(new WorldTile(3158, 5561, 0));
				if(x == 3158 && y == 5561)
					player.setNextWorldTile(new WorldTile(3162, 5557, 0));
				if(x == 3166 && y == 5553)
					player.setNextWorldTile(new WorldTile(3162, 5545, 0));
				if(x == 3162 && y == 5545)
					player.setNextWorldTile(new WorldTile(3166, 5553, 0));
				if(x == 3142 && y == 5545)
					player.setNextWorldTile(new WorldTile(3115, 5528, 0));
				if(x == 3115 && y == 5528)
					player.setNextWorldTile(new WorldTile(3142, 5545, 0));
			} else if (id == 65203) {
				if (player.inCombat(10000)) {
					player.sendMessage("You cannot enter the rift while you're under attack.");
					return;
				}
				if (x == 3058 && y == 3550)
					player.setNextWorldTile(player.transform(125, 1920, 0));
				if (x == 3118 && y == 3570)
					player.setNextWorldTile(player.transform(130, 1920, 0));
				if (x == 3129 && y == 3587)
					player.setNextWorldTile(player.transform(105, 1972, 0));
				if (x == 3164 && y == 3561)
					player.setNextWorldTile(player.transform(128, 1918, 0));
				if (x == 3176 && y == 3585)
					player.setNextWorldTile(new WorldTile(3290, 5539, 0));
			} else if (id == 28782) {
				if (x == 3183 && y == 5470)
					player.setNextWorldTile(player.transform(-125, -1920, 0));
				if (x == 3248 && y == 5490)
					player.setNextWorldTile(player.transform(-130, -1920, 0));
				if (x == 3234 && y == 5559)
					player.setNextWorldTile(player.transform(-105, -1972, 0));
				if (x == 3292 && y == 5479)
					player.setNextWorldTile(player.transform(-128, -1918, 0));
				if (x == 3291 && y == 5538)
					player.setNextWorldTile(player.transform(-115, -1953, 0));
			} else if (id == 26193)
				PartyRoom.openChest(player);
			else if (id == 67050 || id == 6282)
				player.useStairs(-1, new WorldTile(3359, 6110, 0), 0, 1);
			else if (id == 67053)
				player.useStairs(-1, new WorldTile(3120, 3519, 0), 0, 1);
			else if (PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP1, true)))
				return;
			else
				switch (objectDef.getName().toLowerCase()) {
				case "obelisk":
					if (objectDef.getOption(1).equalsIgnoreCase("Renew-points")) {
						if (player.getSkills().getLevel(Constants.SUMMONING) < player.getSkills().getLevelForXp(Constants.SUMMONING)) {
							player.sendMessage("You touch the obelisk", true);
							player.setNextAnimation(new Animation(8502));
							World.sendSpotAnim(null, new SpotAnim(1308), object);
							WorldTasks.schedule(new WorldTask() {

								@Override
								public void run() {
									player.getSkills().set(Constants.SUMMONING, player.getSkills().getLevelForXp(Constants.SUMMONING));
									player.sendMessage("...and recharge your summoning points.", true);
								}
							}, 2);
						}
						return;
					}
					break;
				case "trapdoor":
				case "closed chest":
					if (objectDef.containsOption(0, "Open")) {
						player.setNextAnimation(new Animation(536));
						player.lock(2);
						GameObject openedChest = new GameObject(object.getId() + 1, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane());
						// if (World.removeTemporaryObject(object, 60000,
						// true)) {
						player.faceObject(openedChest);
						World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
						// }
					}
					break;
				case "open chest":
					if (objectDef.containsOption(0, "Search"))
						player.sendMessage("You search the chest but find nothing.");
					break;
				case "spirit tree":
					player.getDialogueManager().execute(new SpiritTreeD(), (object.getId() == 68973 && object.getId() == 68974) ? 3637 : 3636);
					break;
				case "spiderweb":
					if (object.getRotation() == 2) {
						player.lock(2);
						if (Utils.getRandomInclusive(1) == 0) {
							player.addWalkSteps(player.getX(), player.getY() < y ? object.getY() + 2 : object.getY() - 1, -1, false);
							player.sendMessage("You squeeze though the web.");
						} else
							player.sendMessage("You fail to squeeze though the web; perhaps you should try again.");
					}
					break;
				case "web":
					if (objectDef.containsOption(0, "Slash")) {
						player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle())));
						slashWeb(player, object);
					}
					break;
				case "anvil":
					if (objectDef.containsOption(0, "Smith")) {
						ForgingBar bar = ForgingBar.getBar(player);
						if (bar != null)
							ForgingInterface.sendSmithingInterface(player, bar);
						else
							player.sendMessage("You have no bars which you have smithing level to use.");
					}
					break;
					//					case "gate":
					//					case "large door":
					//					case "metal door":
					//					case "city gate":
					//						if (object.getType() == 0 && objectDef.containsOption(0, "Open"))
					//							if (!handleGate(player, object))
					//								handleDoor(player, object);
					//						break;
					//					case "door":
					//					case "long hall door":
					//					case "castle door":
					//					case "heavy door":
					//						if (object.getType() == 0 && (objectDef.containsOption(0, "Open") || objectDef.containsOption(0, "Unlock")))
					//							handleDoor(player, object);
					//						break;
				case "ladder":
					handleLadder(player, object, 1);
					break;
				case "staircase":
					handleStaircases(player, object, 1);
					break;
				case "small obelisk":
					if (objectDef.containsOption(0, "Renew-points")) {
						int summonLevel = player.getSkills().getLevelForXp(Constants.SUMMONING);
						if (player.getSkills().getLevel(Constants.SUMMONING) < summonLevel) {
							player.lock(3);
							player.setNextAnimation(new Animation(8502));
							player.getSkills().set(Constants.SUMMONING, summonLevel);
							player.sendMessage("You have recharged your Summoning points.", true);
						} else
							player.sendMessage("You already have full Summoning points.");
					}
					break;
				case "altar":
				case "chaos altar":
				case "altar of guthix":
					if (objectDef.containsOption(0, "Pray") || objectDef.containsOption(0, "Pray-at") || objectDef.containsOption(0, "Recharge")) {
						final int maxPrayer3 = player.getSkills().getLevelForXp(Constants.PRAYER) * 10;
						if (player.getPrayer().getPoints() < maxPrayer3) {
							player.lock(5);
							player.sendMessage("You pray to the gods...", true);
							player.setNextAnimation(new Animation(645));
							WorldTasks.schedule(new WorldTask() {
								@Override
								public void run() {
									player.getPrayer().restorePrayer(maxPrayer3);
									player.sendMessage("...and recharged your prayer.", true);
								}
							}, 2);
						} else
							player.sendMessage("You already have full prayer.");
						if (id == 6552)
							player.getDialogueManager().execute(new AncientAltar());
					}
					break;
				default:

					break;
				}
		}));
		if (Settings.getConfig().isDebug())
			Logger.log("ObjectHandler", "clicked 1 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane());
	}

	public static void handleOption2(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);
		final int id = object.getId();

		if (!def.containsOption(1) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP2, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick2(object))
				return;
			if (player.getTreasureTrailsManager().useObject(object))
				return;
			if (object.getDefinitions(player).getName().equalsIgnoreCase("furnace") || object.getDefinitions(player).getName().equalsIgnoreCase("clay forge") || object.getDefinitions(player).getName().equalsIgnoreCase("lava furnace"))
				player.getDialogueManager().execute(new SmeltingD(), object);
			else if (id == 17010)
				player.getDialogueManager().execute(new LunarAltar());
			else if (id == 62677)
				player.getDominionTower().openRewards();
			else if (id == 62688)
				player.getDialogueManager().execute(new SimpleMessage(), "You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".");
			else if (id == 68107)
				FightKilnController.enterFightKiln(player, true);
			else if (id == 70795) {
				if (!Agility.hasLevel(player, 50))
					return;
				player.useStairs(-1, new WorldTile(1206, 6506, 0), 1, 2);
			} else if (object.getId() == 68 && player.getInventory().hasFreeSlots()) {
				player.setNextAnimation(new Animation(833));
				player.lock(1);
				if (player.getInventory().containsItem(28))
					player.getInventory().addItem(12156, 1);
				else {
					player.setNextForceTalk(new ForceTalk("Ouch!"));
					player.applyHit(new Hit(10, HitLook.TRUE_DAMAGE));
					player.sendMessage("The bees sting your hands as you reach inside!");
				}
			} else if (object.getDefinitions(player).getName().toLowerCase().contains(" stall"))
				Thieving.handleStalls(player, object);
			else if (object.getId() == 26723)
				SpiritTree.openInterface(player, object.getId() != 68973 && object.getId() != 68974);
			else if (id == 2646 || object.getDefinitions(player).getName().equals("Flax")) {
				if (Utils.random(5) == 0)
					World.removeObjectTemporary(object, Ticks.fromMinutes(1));
				player.getInventory().addItem(1779, 1);
				// crucible
			} else if (id == 22697)
				Thieving.checkTrapsChest(player, object, 22683, 52, 210, 200, new DropSet(
						new DropTable(1, 5, 995, 1, 200),
						new DropTable(1, 5, 4537, 1),
						new DropTable(1, 5, 4546, 1),
						new DropTable(1, 5, 5014, 1),
						new DropTable(1, 5, 10981, 1)));
			else if (id == 22681)
				Thieving.checkTrapsChest(player, object, 22683, 78, 300, 650, new DropSet(
						new DropTable(1, 15, 1623, 1),
						new DropTable(1, 15, 1621, 1),
						new DropTable(1, 15, 1619, 1),
						new DropTable(1, 15, 1617, 1),
						new DropTable(1, 15, 1625, 1),
						new DropTable(1, 15, 1627, 1),
						new DropTable(1, 15, 1629, 1),
						new DropTable(1, 15, 4546, 1),
						new DropTable(1, 15, 5014, 1),
						new DropTable(1, 15, 10954, 1),
						new DropTable(1, 15, 10956, 1),
						new DropTable(1, 15, 2351, 1),
						new DropTable(1, 15, 10981, 1),
						new DropTable(1, 15, 10973, 1),
						new DropTable(1, 15, 10980, 1)
						));
			else if (PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP2, true)))
				return;
			else
				switch (def.getName().toLowerCase()) {
				case "cabbage":
					if (def.containsOption(1, "Pick") && player.getInventory().addItem(1965, 1)) {
						player.setNextAnimation(new Animation(827));
						player.lock(2);
						World.removeObjectTemporary(object, Ticks.fromMinutes(1));
					}
					break;
				case "potato":
					if (def.containsOption(1, "Pick") && player.getInventory().addItem(1942, 1)) {
						player.setNextAnimation(new Animation(827));
						player.lock(2);
						World.removeObjectTemporary(object, Ticks.fromMinutes(1));
					}
					break;
				case "wheat":
					if (def.containsOption(1, "Pick") && player.getInventory().addItem(1947, 1)) {
						player.setNextAnimation(new Animation(827));
						player.lock(2);
						World.removeObjectTemporary(object, Ticks.fromMinutes(1));
					}
					break;
				case "onion":
					if (def.containsOption(1, "Pick") && player.getInventory().addItem(1957, 1)) {
						player.setNextAnimation(new Animation(827));
						player.lock(2);
						World.removeObjectTemporary(object, Ticks.fromMinutes(1));
					}
					break;
				case "spirit tree":
					SpiritTree.openInterface(player, object.getId() != 68973 && object.getId() != 68974);
					break;
				case "ladder":
					handleLadder(player, object, 2);
					break;
				case "staircase":
					handleStaircases(player, object, 2);
					break;
				default:

					break;
				}
			if (Settings.getConfig().isDebug())
				Logger.log("ObjectHandler", "clicked 2 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane());
		}));
	}

	public static void handleOption3(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);
		final int id = object.getId();

		if (!def.containsOption(2) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP3, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick3(object))
				return;

			if (PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP3, true)))
				return;

			switch (def.getName().toLowerCase()) {
			case "bank":
			case "bank chest":
			case "bank booth":
			case "bank table":
			case "counter":
				if (def.containsOption(2, "Collect") || def.containsOption(2, "Use")) {
					//						player.sendOptionDialogue("What would you like to do?", new String[] { "Loadouts", "Clan Bank" }, new DialogueOptionEvent() {
					//							@Override
					//							public void run(Player player) {
					//								if (option == 1) {
					//									player.openLoadouts();
					//								} else {
					//									player.openClanBank();
					//								}
					//							}
					//						});
				}
				break;
			case "ladder":
				handleLadder(player, object, 3);
				break;
			case "staircase":
				handleStaircases(player, object, 3);
				break;
			default:
				break;
			}
			if (Settings.getConfig().isDebug())
				Logger.log("ObjectHandler", "cliked 3 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane() + ", ");
		}));
	}

	public static void handleOption4(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);
		final int id = object.getId();

		if (!def.containsOption(3) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP4, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick4(object))
				return;
			if (PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP4, true)))
				return;
			switch (def.getName().toLowerCase()) {
			default:
				player.sendMessage("Nothing interesting happens.");
				break;
			}
			if (Settings.getConfig().isDebug())
				Logger.log("ObjectHandler", "cliked 4 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane() + ", ");
		}));
	}

	public static void handleOption5(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);
		final int id = object.getId();

		if (!def.containsOption(4) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP5, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick5(object))
				return;
			if (PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP5, true)))
				return;
			if (id == -1) {
				// unused
			} else
				switch (def.getName().toLowerCase()) {
				case "fire":
					if (def.containsOption(4, "Add-logs"))
						Bonfire.addLogs(player, object);
					break;
				case "magical wheat":
					PuroPuroController.pushThrough(player, object);
					break;
				default:
					player.sendMessage("Nothing interesting happens.");
					break;
				}
			if (Settings.getConfig().isDebug())
				Logger.log("ObjectHandler", "cliked 5 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", " + object.getPlane() + ", ");
		}));
	}

	public static void handleOptionExamine(final Player player, final GameObject object) {
		player.getPackets().sendObjectMessage(player, 0, 0xFFFFFF, object, "It's " + Utils.addArticle(object.getDefinitions(player).getName()).toLowerCase() + ".");
		if (player.hasRights(Rights.DEVELOPER) || player.hasRights(Rights.ADMIN)) {
			player.sendMessage(object.toString());
			if (object.getDefinitions().varpBit != -1)
				player.sendMessage("Transforms with varbit " + object.getDefinitions().varpBit);
			if (object.getDefinitions().varp != -1)
				player.sendMessage("Transforms with var " + object.getDefinitions().varp);
		}
	}

	private static void slashWeb(Player player, GameObject object) {
		if (Utils.getRandomInclusive(1) == 0) {
			if (World.removeObjectTemporary(object, Ticks.fromMinutes(1)))
				player.sendMessage("You slash through the web!");
		} else
			player.sendMessage("You fail to cut through the web.");
	}

	public static boolean handleStaircases(Player player, GameObject object, int optionId) {
		String option = object.getDefinitions(player).getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			player.useStairs(-1, new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			player.useStairs(-1, new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.getDialogueManager().execute(new ClimbNoEmoteStairs(), new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), "Go up the stairs.",
					"Go down the stairs.");
		} else
			return false;
		return false;
	}

	private static boolean handleLadder(Player player, GameObject object, int optionId) {
		String option = object.getDefinitions(player).getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			player.useStairs(828, new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			player.useStairs(828, new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.getDialogueManager().execute(new ClimbEmoteStairs(), new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), "Climb up the ladder.",
					"Climb down the ladder.", 828);
		} else
			return false;
		return true;
	}

	public static void handleItemOnObject(final Player player, final GameObject object, final int interfaceId, final Item item, final int slot) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions(player);

		PluginManager.handle(new ItemOnObjectEvent(player, item, object, false));

		if (FishingFerretRoom.handleFerretThrow(player, object, item))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.faceObject(object);

			if (!player.getControllerManager().handleItemOnObject(object, item) || Ectofuntus.handleItemOnObject(player, itemId, object.getId()))
				return;
			if (itemId == Ectofuntus.EMPTY_BUCKET && objectDef.getName().toLowerCase().contains("sand") && objectDef.getName().toLowerCase().contains("pit")) {
				player.getActionManager().setAction(new SandBucketFill());
				return;
			}
			if (itemId == Jewelry.GOLD_BAR && (objectDef.getName().toLowerCase().contains("furnace") || object.getDefinitions(player).getName().equalsIgnoreCase("clay forge") || object.getDefinitions(player).getName().equalsIgnoreCase("lava furnace"))) {
				Jewelry.openJewelryInterface(player);
				player.getTempAttribs().setO("jewelryObject", object);
				return;
			}
			if (itemId == Silver.SILVER_BAR && (objectDef.getName().toLowerCase().contains("furnace") || object.getDefinitions(player).getName().equalsIgnoreCase("clay forge") || object.getDefinitions(player).getName().equalsIgnoreCase("lava furnace"))) {
				Silver.openSilverInterface(player);
				player.getTempAttribs().setO("silverObject", object);
				return;
			}
			if (object.getId() == 13715)
				ItemConstants.handleRepairs(player, item, true, slot);
			if (object.getId() == 2478 && itemId == 1438)
				Runecrafting.craftTalisman(player, 1438, 5527, 13630, 25); //air
			else if (object.getId() == 2479 && itemId == 1448)
				Runecrafting.craftTalisman(player, 1448, 5529, 13631, 27); //mind
			else if (object.getId() == 2480 && itemId == 1444)
				Runecrafting.craftTalisman(player, 1444, 5531, 13632, 30); //water
			else if (object.getId() == 2481 && itemId == 1440)
				Runecrafting.craftTalisman(player, 1440, 5535, 13633, 32); //earth
			else if (object.getId() == 2482 && itemId == 1442)
				Runecrafting.craftTalisman(player, 1442, 5537, 13634, 35); //fire
			else if (object.getId() == 2483 && itemId == 1446)
				Runecrafting.craftTalisman(player, 1446, 5533, 13635, 37); //body
			else if (object.getId() == 2484 && itemId == 1454)
				Runecrafting.craftTalisman(player, 1454, 5539, 13636, 40); //cosmic
			else if (object.getId() == 2487 && itemId == 1452)
				Runecrafting.craftTalisman(player, 1452, 5543, 13637, 42); //chaos
			else if (object.getId() == 2486 && itemId == 1462)
				Runecrafting.craftTalisman(player, 1462, 5541, 13638, 45); //nature
			else if (object.getId() == 2485 && itemId == 1458)
				Runecrafting.craftTalisman(player, 1458, 5545, 13639, 47); //law
			else if (object.getId() == 2488 && itemId == 1456)
				Runecrafting.craftTalisman(player, 1456, 5547, 13640, 50); //death
			else if (object.getId() == 30624 && itemId == 1450)
				Runecrafting.craftTalisman(player, 1450, 5549, 13641, 52); //blood
			else if (object.getId() == 28352 || object.getId() == 28550)
				Incubator.useEgg(player, itemId);
			else if (object.getId() == 733 || object.getId() == 64729) {
				player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle())));
				slashWeb(player, object);
			} else if (object.getId() == 48803 && itemId == 954) {
				if (player.isKalphiteLairSetted())
					return;
				player.getInventory().deleteItem(954, 1);
				player.setKalphiteLair();
			} else if (object.getId() == 48802 && itemId == 954) {
				if (player.isKalphiteLairEntranceSetted())
					return;
				player.getInventory().deleteItem(954, 1);
				player.setKalphiteLairEntrance();
			} else {
				if (PluginManager.handle(new ItemOnObjectEvent(player, item, object, true)))
					return;
				switch (objectDef.getName().toLowerCase()) {
				case "anvil":
					ForgingBar bar = ForgingBar.forId(itemId);
					if (bar != null)
						ForgingInterface.sendSmithingInterface(player, bar);
					break;
				case "fire":
					if (objectDef.containsOption(4, "Add-logs") && Bonfire.addLog(player, object, item))
						return;
				case "range":
				case "campfire":
				case "oven":
				case "cooking range":
				case "sulphur pit":
				case "stove":
				case "clay oven":
				case "fireplace":
					Cookables cook = Cooking.isCookingSkill(item);
					if (cook != null) {
						player.getDialogueManager().execute(new CookingD(), cook, object);
						return;
					}
					player.getDialogueManager().execute(new SimpleMessage(), "You can't cook that on a " + (objectDef.getName().contains("Fire") ? "fire" : "range") + ".");
					break;
				}
				if (Settings.getConfig().isDebug())
					System.out.println("Item on object: " + object.getId());
			}
		}));
	}
}
