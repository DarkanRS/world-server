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

import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.Statement;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.ItemConstants;
import com.rs.game.content.combat.CombatDefinitions.Spellbook;
import com.rs.game.content.combat.PlayerCombatKt;
import com.rs.game.content.items.Spade;
import com.rs.game.content.minigames.creations.StealingCreationLobbyController;
import com.rs.game.content.minigames.domtower.DTPreview;
import com.rs.game.content.minigames.ectofuntus.Ectofuntus;
import com.rs.game.content.minigames.fightcaves.FightCavesController;
import com.rs.game.content.minigames.fightkiln.FightKilnController;
import com.rs.game.content.minigames.fightpits.FightPits;
import com.rs.game.content.minigames.partyroom.PartyRoom;
import com.rs.game.content.minigames.pest.Lander;
import com.rs.game.content.minigames.pest.PestControlLobbyController;
import com.rs.game.content.pets.Incubator;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.agility.WildernessAgility;
import com.rs.game.content.skills.agility.agilitypyramid.AgilityPyramidController;
import com.rs.game.content.skills.cooking.CowMilkingAction;
import com.rs.game.content.skills.crafting.JewelryCraftingKt;
import com.rs.game.content.skills.crafting.SandBucketFill;
import com.rs.game.content.skills.crafting.Silver;
import com.rs.game.content.skills.dungeoneering.rooms.puzzles.FishingFerretRoom;
import com.rs.game.content.skills.firemaking.Bonfire;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.thieving.Thieving;
import com.rs.game.content.transportation.WildernessObelisk;
import com.rs.game.content.world.areas.dungeons.UndergroundDungeonController;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.content.world.unorganized_dialogue.StrongholdRewardD;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.engine.pathfinder.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.PluginManager;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

import java.util.Arrays;

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
			if (!player.getControllerManager().processObjectClick1(object) || player.getTreasureTrailsManager().useObject(object))
				return;

			if (object.getId() == 5259) {
				if (player.getY() == 3507)
					player.tele(Tile.of(player.getX(), player.getY() + 2, 0));
				else if (player.getY() == 3509)
					player.tele(Tile.of(player.getX(), player.getY() - 2, 0));
				return;
			} else if (object.getId() == 29099) {
				if (player.getY() > object.getY())
					player.tele(object.getTile().transform(1, -1, 0));
				else
					player.tele(object.getTile().transform(1, 1, 0));
			}

			if (object.getId() == 36687) {
				player.useStairs(828, Tile.of(player.getX(), player.getY() + 6400, 0));
				return;
			}
			if (object.getId() == 16535) {
				player.getControllerManager().startController(new AgilityPyramidController());
				AgilityPyramidController.climbRocks(player, object);
			} else if (object.getId() == 11739) {
				player.useStairs(828, player.transform(0, -2, 1));
				return;
			} else if (object.getId() == 11741) {
				player.useStairs(828, player.transform(0, 2, -1));
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
				if (object.getTile().isAt(3189, 3432)) {
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
				player.useStairs(828, Tile.of(3047, 4971, 0));
				return;
			} else if (object.getId() == 7258) {
				player.useStairs(-1, Tile.of(2896, 3447, 0), 1, 1);
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
				player.getDungManager().enterDungeon(true);
			else if (id == 31149) {
				boolean isEntering = player.getX() <= 3295;
				player.useStairs(isEntering ? 9221 : 9220, Tile.of(x + (isEntering ? 1 : 0), y, 0));
			} else if (id == 2350 && (object.getX() == 3352 && object.getY() == 3417 && object.getPlane() == 0))
				player.useStairs(832, Tile.of(3177, 5731, 0));
			else if (id >= 65616 && id <= 65622)
				WildernessObelisk.activateObelisk(id, player);
			else if (id == 10229) { // dag up ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(1, () -> player.tele(Tile.of(1910, 4367, 0)));
            } else if (id == 17757) {
				Agility.handleObstacle(player, 3303, 1, player.transform(0, player.getY() < object.getY() ? 2 : -2, 0), 0);
            } else if (id == 56805) { // HARBLORE HARBITAT
				if (object.getRotation() == 3 || object.getRotation() == 1)
					Agility.handleObstacle(player, 3303, 1, player.transform(player.getX() < object.getX() ? 2 : -2, 0, 0), 0);
				else
					Agility.handleObstacle(player, 3303, 1, player.transform(0, player.getY() < object.getY() ? 2 : -2, 0), 0);
            } else if (id == 27126) { // HARBLORE HARBITAT
				if (player.getX() >= 2961 && player.getX() <= 2964)
					player.tele(Tile.of(player.getX() + 12, player.getY() + 2, player.getPlane()));
				else if (player.getX() >= 2975 && player.getX() <= 2979)
					player.tele(Tile.of(player.getX() - 12, player.getY() - 2, player.getPlane()));
            } else if (id == 10230) { // dag down ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						player.tele(Tile.of(2900, 4449, 0));
					}
				}, 1);
			} else if (id == 14315) {
				if (Lander.canEnter(player, 0)) {
                }
			} else if (id == 25631) {
				if (Lander.canEnter(player, 1)) {
                }
			} else if (id == 25632) {
				if (Lander.canEnter(player, 2)) {
                }
			} else if (id == 35391 || id == 2832) {
				if (!Agility.hasLevel(player, id == 2832 ? 20 : 15))
					return;
				player.addWalkSteps(x, y);
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						boolean isTravelingWest = id == 2832 ? player.getX() >= 2508 : (x == 2834 && y == 3626) ? player.getX() >= 2834 : player.getX() >= 2900;
						player.useStairs(3303, Tile.of((isTravelingWest ? -2 : 2) + player.getX(), player.getY(), 0), 2, 3, null, true);
					}
				});
			} else if (id == 65371) // Chaos altar (armored zombie)
				player.getPrayer().worshipAltar();
            else if (id == 65715) // Armored zombie trapdoor
				player.tele(Tile.of(3241, 9991, 0));
			else if (id == 12328) // Jadinko lair
				player.tele(Tile.of(3011, 9276, 0));
			else if (id == 11209)
				player.useStairs(-1, player.transform(3, 0, 1), 0, 1);
			else if (id == 11210)
				player.useStairs(-1, player.transform(-3, 0, -1), 0, 1);
			else if (id == 11212)
				player.useStairs(-1, player.transform(0, 3, -1), 0, 1);
			else if (id == 11211)
				player.useStairs(-1, player.transform(0, -3, 1), 0, 1);
			else if (id == 38279 && x == 1696 && y == 5460)
				player.useStairs(-1, Tile.of(3106, 3160, 1), 0, 1);
			else if (id == 12327) { // jadinko lair out
				player.tele(Tile.of(2948, 2955, 0));
            } else if (id == 4495) { // Slayer tower stairs up
				player.tele(Tile.of(3417, 3541, 2));
            } else if (id == 4496) { // Slayer tower stairs down
				player.tele(Tile.of(3412, 3540, 1));
            } else if (id == 39191) { // Armored zombie up ladder
				player.setNextAnimation(new Animation(828));
				WorldTasks.schedule(1, () -> {
					player.tele(Tile.of(3240, 3607, 0));
					player.getControllerManager().startController(new WildernessController());
				});
            } else if (id == 2353 && (object.getX() == 3177 && object.getY() == 5730 && object.getPlane() == 0))
				player.useStairs(828, Tile.of(3353, 3416, 0));
			else if (id == 66115 || id == 66116)
				Spade.dig(player);
			else if (id == 20604)
				player.useStairs(-1, Tile.of(3018, 3404, 0), 0, 1);
			else if (object.getId() == 39508 || object.getId() == 39509)
				StealingCreationLobbyController.climbOverStile(player, object, true);
			else if (id == 16135) {
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
			else if (id == 47120) { // zaros altar recharge if needed
				if (player.getPrayer().getPoints() < player.getSkills().getLevelForXp(Constants.PRAYER) * 10) {
					player.lock(12);
					player.setNextAnimation(new Animation(12563));
					player.getPrayer().setPoints(((player.getSkills().getLevelForXp(Constants.PRAYER) * 10) * 1.15));
					player.getPrayer().refreshPoints();
				}
				player.startConversation(new Dialogue()
						.addOptions("Change from " + ((player.getPrayer().isCurses() ? "curses" : "prayers")) + "?", ops -> {
							ops.add("Yes, replace my prayer book.", () -> {
								if (player.getPrayer().isCurses())
									player.simpleDialogue("The altar eases its grip on your mid. The curses slip from", "your memory and you recall the prayers you used to know.");
								else
									player.simpleDialogue("The altar fills your head with dark thoughts, purging the", "prayers from your memory and leaving only curses in", " their place.");
								player.getPrayer().setPrayerBook(!player.getPrayer().isCurses());
							});
							ops.add("Nevermind.");
						}));
			} else if (id == 9356)
				FightCavesController.enterFightCaves(player);
			else if (id == 68107)
				FightKilnController.enterFightKiln(player, false);
			else if (id == 68223)
				FightPits.enterLobby(player, false);
			else if (id == 26684 || id == 26685 || id == 26686) // poison waste cave
				player.useStairs(-1, Tile.of(1989, 4174, 0), 1, 2, "You enter the murky cave...");
			else if (id == 26571 || id == 26572 || id == 26573 || id == 26574)
				player.useStairs(-1, Tile.of(2321, 3100, 0));
			else if (id == 26560 && x == 2015 && y == 4255)
				player.simpleDialogue("The room beyond the door is covered in gas, it is probably dangerous to go in there.");
			else if (id == 26519) {
				if (x == 1991 && y == 4175)
					player.useStairs(827, Tile.of(1991, 4175, 0));
				else if (x == 1998 && y == 4218)
					player.useStairs(827, Tile.of(1998, 4218, 0));
				else if (x == 2011 && y == 4218)
					player.useStairs(827, Tile.of(2011, 4218, 0));
				else
					player.useStairs(827, Tile.of(x - 1, y, 0));
			} else if (id == 19171) {
				if (!Agility.hasLevel(player, 20))
					return;
				player.useStairs(-1, Tile.of(player.getX() >= 2523 ? 2522 : 2523, 3375, 0), 1, 2, "You easily squeeze through the railing.");
			} else if (id == 22945) {
				player.useStairs(-1, Tile.of(3318, 9602, 0), 0, 1);
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (id == 15767) {
				player.useStairs(-1, Tile.of(3748, 9373, 0), 0, 1);
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (object.getId() == 15791) {
				if (object.getX() == 3829)
					player.useStairs(-1, Tile.of(3830, 9461, 0));
				if (object.getX() == 3814)
					player.useStairs(-1, Tile.of(3815, 9461, 0));
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (id == 5947) {
				player.useStairs(540, Tile.of(3170, 9571, 0), 8, 9);
				WorldTasks.schedule(new Task() {
					@Override
					public void run() {
						player.getControllerManager().startController(new UndergroundDungeonController(false, true));
						player.setNextAnimation(new Animation(-1));
					}
				}, 8);
            } else if (object.getId() == 6658) {
				player.useStairs(-1, Tile.of(3226, 9542, 0));
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
			} else if (object.getId() == 6898) {
				player.setNextAnimation(new Animation(10578));
				player.useStairs(-1, object.getTile());
				player.useStairs(10579, Tile.of(3221, 9618, 0));
				player.getControllerManager().startController(new UndergroundDungeonController(false, true));
				player.sendMessage("You squeeze through the hole.");
            } else if (id == 36002) {
				player.getControllerManager().startController(new UndergroundDungeonController(true, false));
				player.useStairs(833, Tile.of(3206, 9379, 0));
			} else if (id == 31359) {
				player.useStairs(-1, Tile.of(3360, 9352, 0));
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
				player.useStairs(-1, Tile.of(3061, 3335, 0), 1, 1);
			else if (id == 29386 || id == 29385)
				player.useStairs(-1, Tile.of(3067, 9710, 0), 1, 1);
			else if (id == 29391)
				player.useStairs(-1, Tile.of(3037, 3342, 0), 1, 1);
			else if (id == 29387)
				player.useStairs(-1, Tile.of(3035, 9713, 0), 1, 1);
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
				player.useStairs(828, player.transform(0, 0, 1));
			else if (id == 20602)
				player.useStairs(-1, Tile.of(2969, 9672, 0), 1, 1);
			else if (id == 4627)
				player.useStairs(-1, Tile.of(2893, 3567, 0), 1, 1);
			else if (id == 66973)
				player.useStairs(-1, Tile.of(2206, 4934, 1), 1, 1);
			else if (id == 22666) {
				player.useStairs(-1, player.transform(-5, 0, -3), 1, 1);
            } else if (id == 22600) {
				player.useStairs(-1, player.transform(5, 0, 3), 1, 1);
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
			else if (id == 22933)
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
					player.useStairs(828, Tile.of(1991, 4176, 1));
				else if (x == 1998 && y == 4218)
					player.useStairs(828, Tile.of(1998, 4219, 1));
				else if (x == 2011 && y == 4218)
					player.useStairs(828, Tile.of(2011, 4219, 1));
				else if (x == 3118 && y == 9643)
					player.useStairs(828, Tile.of(3118, player.getY() - 6400, 0));
				else
					player.useStairs(828, Tile.of(x + 1, y, 1));
			} else if (id == 46500 && object.getX() == 3351 && object.getY() == 3415) { // zaros
				// portal
				player.useStairs(-1, Tile.of(Settings.getConfig().getPlayerRespawnTile().getX(), Settings.getConfig().getPlayerRespawnTile().getY(), Settings.getConfig().getPlayerRespawnTile().getPlane()), 2, 3, "You found your way back to home.");
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
				WorldTasks.scheduleLooping(new Task() {
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
			} else if (id == 17222 || id == 17223)
				player.useStairs(-1, Tile.of(2402, 3419, 0), 0, 1);
			else if (id == 17209)
				player.useStairs(-1, Tile.of(2408, 9812, 0), 0, 1);
			else if (id == 1754)
				player.useLadder(player.transform(0, 6400));
			else if (id == 2811 || id == 2812) {
				player.useStairs(id == 2812 ? 827 : -1, id == 2812 ? Tile.of(2501, 2989, 0) : Tile.of(2574, 3029, 0));
				WorldTasks.schedule(() -> player.playerDialogue(HeadE.AMAZED, "Wow! That tunnel went a long way."));
			} else if (id == 2890 || id == 2892 || id == 2893) {

				if (player.getEquipment().getWeaponId() != 975 && !player.getInventory().containsItem(975, 1) &&
						player.getEquipment().getWeaponId() != 6313 && !player.getInventory().containsItem(6313, 1) &&
						player.getEquipment().getWeaponId() != 6315 && !player.getInventory().containsItem(6315, 1) &&
						player.getEquipment().getWeaponId() != 6317 && !player.getInventory().containsItem(6317, 1)) {
					player.sendMessage("You need a machete in order to cutt through the terrain.");
					return;
				}
				player.setNextAnimation(new Animation(910));
				WorldTasks.schedule(new Task() {

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
				player.useStairs(-1, Tile.of(x == 2792 ? 2795 : 2791, 2979, 0), 1, 2, x == 2792 ? "You climb down the slope." : "You climb up the slope.");

				// start forinthry dungeon
			else if (id == 18341 && object.getX() == 3036 && object.getY() == 10172)
				player.useStairs(-1, Tile.of(3039, 3765, 0), 0, 1);
			else if (id == 20599 && object.getX() == 3038 && object.getY() == 3761)
				player.useStairs(-1, Tile.of(3037, 10171, 0), 0, 1);
			else if (id == 18342 && object.getX() == 3075 && object.getY() == 10057)
				player.useStairs(-1, Tile.of(3071, 3649, 0), 0, 1);
			else if (id == 20600 && object.getX() == 3072 && object.getY() == 3648)
				player.useStairs(-1, Tile.of(3077, 10058, 0), 0, 1);
			else if (id == 8689)
				player.getActionManager().setAction(new CowMilkingAction());
				// start falador mininig
			else if (id == 30942 && object.getX() == 3019 && object.getY() == 3450)
				player.useStairs(828, Tile.of(3020, 9850, 0));
			else if (id == 6226 && object.getX() == 3019 && object.getY() == 9850)
				player.useStairs(833, Tile.of(3018, 3450, 0));
			else if (id == 30943 && object.getX() == 3059 && object.getY() == 9776)
				player.useStairs(-1, Tile.of(3061, 3376, 0), 0, 1);
			else if (id == 30944 && object.getX() == 3059 && object.getY() == 3376)
				player.useStairs(-1, Tile.of(3058, 9776, 0), 0, 1);
			else if (id == 2112 && object.getX() == 3046 && object.getY() == 9756) {
				if (player.getSkills().getLevelForXp(Constants.MINING) < 60) {
					player.npcDialogue(3294, HeadE.CHEERFUL, "Sorry, but you need level 60 Mining to go in there.");
					return;
				}
				Doors.handleDoor(player, object);
			} else if (id == 2113) {
				if (player.getSkills().getLevelForXp(Constants.MINING) < 60) {
					player.npcDialogue(3294, HeadE.CHEERFUL, "Sorry, but you need level 60 Mining to go in there.");
					return;
				}
				player.useStairs(-1, Tile.of(3021, 9739, 0), 0, 1);
			} else if (id == 6226 && object.getX() == 3019 && object.getY() == 9740)
				player.useStairs(828, Tile.of(3019, 3341, 0));
			else if (id == 6226 && object.getX() == 3019 && object.getY() == 9738)
				player.useStairs(828, Tile.of(3019, 3337, 0));
			else if (id == 6226 && object.getX() == 3018 && object.getY() == 9739)
				player.useStairs(828, Tile.of(3017, 3339, 0));
			else if (id == 6226 && object.getX() == 3020 && object.getY() == 9739)
				player.useStairs(828, Tile.of(3021, 3339, 0));
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
				WorldTasks.schedule(0, () -> player.forceMove(Tile.of(object.getX() + (object.getRotation() == 2 ? -2 : +2), object.getY(), 0), 2594, 0, 120, false, () -> player.forceMove(Tile.of(object.getX() + (object.getRotation() == 2 ? -5 : +5), object.getY(), 0), 2590, 0, 120, false, () -> player.forceMove(Tile.of(object.getX() + (object.getRotation() == 2 ? -6 : +6), object.getY(), 0), 2595, 0, 75))));

				// rock living caverns
			} else if (id == 45077) {
				player.lock();
				if (player.getX() != object.getX() || player.getY() != object.getY())
					player.addWalkSteps(object.getX(), object.getY(), -1, false);
				WorldTasks.scheduleLooping(new Task() {

					private int count;

					@Override
					public void run() {
						if (count == 0) {
							player.setNextFaceTile(Tile.of(object.getX() - 1, object.getY(), 0));
							player.setNextAnimation(new Animation(12216));
						} else if (count == 2) {
							player.tele(Tile.of(3651, 5122, 0));
							player.setNextFaceTile(Tile.of(3651, 5121, 0));
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
				WildernessAgility.gateWalkOut(player, object);
			else if (id == 65365)
				WildernessAgility.gateWalkIn(player, object);
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
				player.useStairs(2413, Tile.of(3012, 9832, 0), 2, 2);
				// champion guild
			else if (id == 24357 && object.getX() == 3188 && object.getY() == 3355)
				player.useStairs(-1, Tile.of(3189, 3354, 1), 0, 1);
			else if (id == 24359 && object.getX() == 3188 && object.getY() == 3355)
				player.useStairs(-1, Tile.of(3189, 3358, 0), 0, 1);
				// start of varrock dungeon
			else if (id == 29355 && object.getX() == 3230 && object.getY() == 9904) // varrock
				// dungeon
				// climb
				// to
				// bear
				player.useStairs(828, Tile.of(3229, 3503, 0));
			else if (id == 24264)
				player.useStairs(833, Tile.of(3229, 9904, 0));
			else if (id == 24366)
				player.useStairs(828, Tile.of(3237, 3459, 0));
			else if (id == 29355 && object.getX() == 3097 && object.getY() == 9867) // edge
				// dungeon
				// climb
				player.useStairs(828, Tile.of(3096, 3468, 0));
			else if (id == 26934)
				player.useStairs(833, Tile.of(3096, 9868, 0));
			else if (id == 29355 && object.getX() == 3088 && object.getY() == 9971)
				player.useStairs(828, Tile.of(3087, 3571, 0));
			else if (id == 65453)
				player.useStairs(833, Tile.of(3089, 9971, 0));
			else if (id == 12389 && object.getX() == 3116 && object.getY() == 3452)
				player.useStairs(833, Tile.of(3117, 9852, 0));
			else if (id == 29355 && object.getX() == 3116 && object.getY() == 9852)
				player.useStairs(833, Tile.of(3115, 3452, 0));
			else if (WildernessController.isDitch(id)) {
				player.startConversation(new Dialogue().addNext(new Statement() {
					@Override
					public void send(Player player) { player.getInterfaceManager().sendInterface(382); }

					@Override
					public int getOptionId(int componentId) { return componentId == 19 ? 0 : 1; }

					@Override
					public void close(Player player) {

					}
				}).addNext(() -> {
					player.stopAll();
					player.forceMove(Tile.of(object.getRotation() == 3 || object.getRotation() == 1 ? object.getX() - 1 : player.getX(), object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() + 2 : player.getY(), object.getPlane()), 6132, 25, 60, () -> {
						player.faceObject(object);
						player.getControllerManager().startController(new WildernessController());
						player.resetReceivedDamage();
					});
				}));
			} else if (id >= 8958 && id <= 8960)
				World.removeObjectTemporary(object, Ticks.fromMinutes(1));
			else if (id == 10177 && x == 2546 && y == 10143)
				player.promptUpDown(828, "Go up the stairs.", Tile.of(2544, 3741, 0), "Go down the stairs.", Tile.of(1798, 4407, 3));
			else if ((id == 10193 && x == 1798 && y == 4406) || (id == 8930 && x == 2542 && y == 3740))
				player.useStairs(-1, Tile.of(2545, 10143, 0), 0, 1);
			else if (id == 10195 && x == 1808 && y == 4405)
				player.useStairs(-1, Tile.of(1810, 4405, 2), 0, 1);
			else if (id == 10196 && x == 1809 && y == 4405)
				player.useStairs(-1, Tile.of(1807, 4405, 3), 0, 1);
			else if (id == 10198 && x == 1823 && y == 4404)
				player.useStairs(-1, Tile.of(1825, 4404, 3), 0, 1);
			else if (id == 10197 && x == 1824 && y == 4404)
				player.useStairs(-1, Tile.of(1823, 4404, 2), 0, 1);
			else if (id == 10199 && x == 1834 && y == 4389)
				player.useStairs(-1, Tile.of(1834, 4388, 2), 0, 1);
			else if (id == 10200 && x == 1834 && y == 4388)
				player.useStairs(-1, Tile.of(1834, 4390, 3), 0, 1);
			else if (id == 10201 && x == 1811 && y == 4394)
				player.useStairs(-1, Tile.of(1810, 4394, 1), 0, 1);
			else if (id == 10202 && x == 1810 && y == 4394)
				player.useStairs(-1, Tile.of(1812, 4394, 2), 0, 1);
			else if (id == 10203 && x == 1799 && y == 4388)
				player.useStairs(-1, Tile.of(1799, 4386, 2), 0, 1);
			else if (id == 10204 && x == 1799 && y == 4387)
				player.useStairs(-1, Tile.of(1799, 4389, 1), 0, 1);
			else if (id == 10205 && x == 1797 && y == 4382)
				player.useStairs(-1, Tile.of(1797, 4382, 1), 0, 1);
			else if (id == 10206 && x == 1798 && y == 4382)
				player.useStairs(-1, Tile.of(1796, 4382, 2), 0, 1);
			else if (id == 10207 && x == 1802 && y == 4369)
				player.useStairs(-1, Tile.of(1800, 4369, 2), 0, 1);
			else if (id == 10208 && x == 1801 && y == 4369)
				player.useStairs(-1, Tile.of(1802, 4369, 1), 0, 1);
			else if (id == 10209 && x == 1826 && y == 4362)
				player.useStairs(-1, Tile.of(1828, 4362, 1), 0, 1);
			else if (id == 10210 && x == 1827 && y == 4362)
				player.useStairs(-1, Tile.of(1825, 4362, 2), 0, 1);
			else if (id == 10211 && x == 1863 && y == 4371)
				player.useStairs(-1, Tile.of(1863, 4373, 2), 0, 1);
			else if (id == 10212 && x == 1863 && y == 4372)
				player.useStairs(-1, Tile.of(1863, 4370, 1), 0, 1);
			else if (id == 10213 && x == 1864 && y == 4388)
				player.useStairs(-1, Tile.of(1864, 4389, 1), 0, 1);
			else if (id == 10214 && x == 1864 && y == 4389)
				player.useStairs(-1, Tile.of(1864, 4387, 2), 0, 1);
			else if (id == 10215 && x == 1890 && y == 4407)
				player.useStairs(-1, Tile.of(1890, 4408, 0), 0, 1);
			else if (id == 10216 && x == 1890 && y == 4408)
				player.useStairs(-1, Tile.of(1890, 4406, 1), 0, 1);
			else if (id == 10230 && x == 1911 && y == 4367)
				// kings
				// entrance
				//BossInstanceHandler.enterInstance(player, Boss.Dagannoth_Kings);
				player.useStairs(-1, Tile.of(2900, 4449, 0), 0, 1);
			else if (id == 10229 && x == 2899 && y == 4449)
				player.useStairs(-1, Tile.of(1912, 4367, 0), 0, 1);
			else if (id == 10217 && x == 1957 && y == 4371)
				player.useStairs(-1, Tile.of(1957, 4373, 1), 0, 1);
			else if (id == 10218 && x == 1957 && y == 4372)
				player.useStairs(-1, Tile.of(1957, 4370, 0), 0, 1);
			else if (id == 10226 && x == 1932 && y == 4378)
				player.useStairs(-1, Tile.of(1932, 4380, 2), 0, 1);
			else if (id == 10225 && x == 1932 && y == 4379)
				player.useStairs(-1, Tile.of(1932, 4377, 1), 0, 1);
			else if (id == 10228 && x == 1961 && y == 4391)
				player.useStairs(-1, Tile.of(1961, 4393, 3), 0, 1);
			else if (id == 10227 && x == 1961 && y == 4392)
				player.useStairs(-1, Tile.of(1961, 4392, 2), 0, 1);
			else if (id == 10194 && x == 1975 && y == 4408)
				player.useStairs(-1, Tile.of(2501, 3636, 0), 0, 1);
			else if (id == 10219 && x == 1824 && y == 4381)
				player.useStairs(-1, Tile.of(1824, 4379, 3), 0, 1);
			else if (id == 10220 && x == 1824 && y == 4380)
				player.useStairs(-1, Tile.of(1824, 4382, 2), 0, 1);
			else if (id == 10221 && x == 1838 && y == 4376)
				player.useStairs(-1, Tile.of(1838, 4374, 2), 0, 1);
			else if (id == 10222 && x == 1838 && y == 4375)
				player.useStairs(-1, Tile.of(1838, 4377, 3), 0, 1);
			else if (id == 10223 && x == 1850 && y == 4386)
				player.useStairs(-1, Tile.of(1850, 4385, 1), 0, 1);
			else if (id == 10224 && x == 1850 && y == 4385)
				player.useStairs(-1, Tile.of(1850, 4387, 2), 0, 1);
			// sabbot lair
			else if (id == 19690)
				player.useStairs(-1, player.transform(0, 4, 1), 0, 1);
			else if (id == 19691)
				player.useStairs(-1, player.transform(0, -4, -1), 0, 1);
			else if (id == 61336)
				player.getPrayer().worshipAltar();
			else if (id == 2878 || id == 2879) {
				player.simpleDialogue("You step into the pool of sparkling water. You feel the energy rush through your veins.");
				player.forceMove(id == 2879 ? Tile.of(2509, 4687, 0) : Tile.of(2542, 4720, 0), 13842, 0, 60, () -> {
					player.setNextAnimation(new Animation(-1));
					player.tele(id == 2879 ? Tile.of(2542, 4718, 0) : Tile.of(2509, 4689, 0));
				});
			} else if (id == 2873 || id == 2874 || id == 2875) {
				player.sendMessage("You kneel and begin to chant to " + objectDef.getName().replace("Statue of ", "") + "...");
				player.setNextAnimation(new Animation(645));
				WorldTasks.schedule(new Task() {

					@Override
					public void run() {
						player.simpleDialogue("You feel a rush of energy charge through your veins. Suddenly a cape appears before you.");
						World.sendSpotAnim(Tile.of(object.getX(), object.getY() - 1, 0), new SpotAnim(1605));
						World.addGroundItem(new Item(id == 2873 ? 2412 : id == 2874 ? 2414 : 2413), Tile.of(object.getX(), object.getY() - 1, 0));
					}
				}, 3);
			} else if (id == 49016 || id == 49014) {
				if (player.getSkills().getLevel(Constants.FISHING) < 68) {
					player.sendMessage("You need a Fishing level of 68 in order to pass through this gate.");
					return;
				}
				Doors.handleDoubleDoor(player, object);
			} else if (id == 70794)
				player.useStairs(-1, Tile.of(1340, 6488, 0));
			else if (id == 70795) {
				if (!Agility.hasLevel(player, 50))
					return;
				player.startConversation(new Dialogue()
						.addSimple("The shortcut leads to the deepest level of the dungeon. The worms in that area are significantly more dangerous.")
						.addOptions("Slide down the worm burrow?", ops -> {
							ops.add("Yes.", () -> player.useStairs(Tile.of(1206, 6506, 0)));
							ops.add("No.");
						}));
			} else if (id == 70799)
				player.useStairs(-1, Tile.of(1178, 6355, 0));
			else if (id == 70796)
				player.useStairs(-1, Tile.of(1090, 6360, 0));
			else if (id == 70798)
				player.useStairs(-1, Tile.of(1340, 6380, 0));
			else if (id == 70797)
				player.useStairs(-1, Tile.of(1090, 6497, 0));
			else if (id == 70792)
				player.useStairs(-1, Tile.of(1206, 6371, 0));
			else if (id == 70793)
				player.useStairs(-1, Tile.of(2989, 3237, 0));
			else if (id == 12202) {// mole entrance
				if (!player.getInventory().containsItem(952, 1)) {
					player.sendMessage("You need a spade to dig this.");
					return;
				}
				if (player.getX() != object.getX() || player.getY() != object.getY()) {
					player.lock();
					player.addWalkSteps(object.getX(), object.getY());
					WorldTasks.schedule(new Task() {
						@Override
						public void run() {
							Spade.dig(player);
						}

					}, 1);
				} else
					Spade.dig(player);
			} else if (id == 11724)
				player.useStairs(-1, Tile.of(2968, 3348, 1), 0, 1);
			else if (id == 11725)
				player.useStairs(-1, Tile.of(2971, 3347, 0), 0, 1);
			else if (id == 8929)
				player.useStairs(-1, Tile.of(2442, 10147, 0), 0, 1);
			else if (id == 8966)
				player.useStairs(-1, Tile.of(2523, 3740, 0), 0, 1);
			else if (id == 29728)
				player.useStairs(-1, Tile.of(3158, 4280, 3), 0, 1);
			else if (id == 29729)
				player.useStairs(-1, Tile.of(3078, 3463, 0), 0, 1);
			else if (id == 29672)
				player.useStairs(-1, Tile.of(3171, 4271, 3), 0, 1);
			else if (id == 29671)
				player.useStairs(-1, Tile.of(3174, 4273, 2), 0, 1);
			else if (id == 23158)
				player.useStairs(-1, Tile.of(2730, 3734, 0), 0, 1);
			else if (id == 11355)
				player.useStairs(-1, Tile.of(2677, 5214, 2), 0, 1);
			else if (id == 11356)
				player.useStairs(-1, Tile.of(3110, 3363, 2), 0, 1);
			else if (id == 15811 || id == 15812)
				player.useStairs(-1, Tile.of(3749, 2973, 0), 0, 1);
			else if (id == 63093)
				player.useStairs(-1, Tile.of(4620, 5458, 3), 0, 1);
			else if (id == 63094)
				player.useStairs(-1, Tile.of(3410, 3329, 0), 0, 1);
			else if (id == 2147)
				player.ladder(Tile.of(3104, 9576, 0));
			else if (id == 5492)
				player.ladder(Tile.of(3149, 9652, 0));
			else if (id == 5493)
				player.ladder(Tile.of(3165, 3251, 0));
			else if (id == 68983) {
				Doors.handleInPlaceSingleDoor(player, object);
				player.resetWalkSteps();
				player.addWalkSteps(2461, player.getY() > object.getY() ? object.getY() - 1 : object.getY() + 3, -1, false);
			} else if (id == 12230 && object.getX() == 1752 && object.getY() == 5136)
				player.tele(Tile.of(2996, 3378, 0));
			else if (id == 38811 || id == 37929) {// corp beast
				if (object.getX() == 2971 && object.getY() == 4382)
					player.getInterfaceManager().sendInterface(650);
				else if (object.getX() == 2918 && object.getY() == 4382) {
					player.stopAll();
					player.tele(Tile.of(player.getX() == 2921 ? 2917 : 2921, player.getY(), player.getPlane()));
				}
			} else if (id == 37928 && object.getX() == 2883 && object.getY() == 4370) {
				player.stopAll();
				player.tele(Tile.of(3214, 3782, 0));
				player.getControllerManager().startController(new WildernessController());
			} else if (id == 38815 && object.getX() == 3209 && object.getY() == 3780 && object.getPlane() == 0) {
				if (player.getSkills().getLevelForXp(Constants.WOODCUTTING) < 37 || player.getSkills().getLevelForXp(Constants.MINING) < 45 || player.getSkills().getLevelForXp(Constants.SUMMONING) < 23
						|| player.getSkills().getLevelForXp(Constants.FIREMAKING) < 47 || player.getSkills().getLevelForXp(Constants.PRAYER) < 55) {
					player.sendMessage("You need 23 Summoning, 37 Woodcutting, 45 Mining, 47 Firemaking and 55 Prayer to enter this dungeon.");
					return;
				}
				player.stopAll();
				player.tele(Tile.of(2885, 4372, 2));
				player.getControllerManager().forceStop();
				// TODO all reqs, skills not added
			} else if (id == 48803 && player.isKalphiteLairSetted())
				player.tele(Tile.of(3508, 9494, 0));
			else if (id == 48802 && player.isKalphiteLairEntranceSetted())
				player.tele(Tile.of(3483, 9510, 2));
			else if (id == 3829) {
				if (object.getX() == 3483 && object.getY() == 9510)
					player.useStairs(828, Tile.of(3226, 3108, 0));
			} else if (id == 3832) {
				if (object.getX() == 3508 && object.getY() == 9494)
					player.useStairs(828, Tile.of(3509, 9496, 2));
			} else if (id == 14315)
				player.getControllerManager().startController(new PestControlLobbyController(1));
			else if (id == 5959)
				Magic.pushLeverTeleport(player, Tile.of(2539, 4712, 0));
			else if (id == 5960)
				Magic.pushLeverTeleport(player, Tile.of(3089, 3957, 0));
			else if (id == 1814)
				Magic.pushLeverTeleport(player, Tile.of(3155, 3923, 0));
			else if (id == 1815)
				Magic.pushLeverTeleport(player, Tile.of(2561, 3311, 0));
			else if (id == 62675)
				player.getCutsceneManager().play(new DTPreview());
			else if (id == 62678 || id == 62679)
				player.getDominionTower().openModes();
			else if (id == 62688)
				player.startConversation(new Dialogue()
						.addSimple("You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".")
						.addOptions("If you claim your rewards your progress will be reset.", ops -> {
							ops.add("Claim rewards", () -> player.getDominionTower().openRewardsChest());
							ops.add("Nevermind.");
						}));
			else if (id == 62677)
				player.getDominionTower().talkToFace();
			else if (id == 62680)
				player.getDominionTower().openBankChest();
			else if (id == 48797)
				player.useStairs(-1, Tile.of(3877, 5526, 1), 0, 1);
			else if (id == 48798)
				player.useStairs(-1, Tile.of(3246, 3198, 0), 0, 1);
			else if (id == 48678 && x == 3858 && y == 5533)
				player.useStairs(-1, Tile.of(3861, 5533, 0), 0, 1);
			else if (id == 48678 && x == 3858 && y == 5543)
				player.useStairs(-1, Tile.of(3861, 5543, 0), 0, 1);
			else if (id == 48678 && x == 3858 && y == 5533)
				player.useStairs(-1, Tile.of(3861, 5533, 0), 0, 1);
			else if (id == 48677 && x == 3858 && y == 5543)
				player.useStairs(-1, Tile.of(3856, 5543, 1), 0, 1);
			else if (id == 48677 && x == 3858 && y == 5533)
				player.useStairs(-1, Tile.of(3856, 5533, 1), 0, 1);
			else if (id == 48679)
				player.useStairs(-1, Tile.of(3875, 5527, 1), 0, 1);
			else if (id == 48688)
				player.useStairs(-1, Tile.of(3972, 5565, 0), 0, 1);
			else if (id == 48683)
				player.useStairs(-1, Tile.of(3868, 5524, 0), 0, 1);
			else if (id == 48682)
				player.useStairs(-1, Tile.of(3869, 5524, 0), 0, 1);
			else if (id == 62676)
				player.useStairs(-1, Tile.of(3374, 3093, 0), 0, 1);
			else if (id == 62674)
				player.useStairs(-1, Tile.of(3744, 6405, 0), 0, 1);
			else if (id == 65349)
				player.useStairs(-1, Tile.of(3044, 10325, 0), 0, 1);
			else if (id == 32048 && object.getX() == 3043 && object.getY() == 10328)
				player.useStairs(-1, Tile.of(3045, 3927, 0), 0, 1);
			else if (id == 2348)
				player.tele(player.transform(object.getRotation() == 3 ? -3 : 3, 0, -1));
			else if (id == 2347)
				player.tele(player.transform(object.getRotation() == 3 ? 3 : -3, 0, 1));
			else if (id == 26193)
				PartyRoom.openChest(player);
			else if (PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP1, true))) {
            }
			else
				switch (objectDef.getName().toLowerCase()) {
					case "trapdoor":
						if (objectDef.containsOption(0, "Open")) {
							GameObject openedTrapdoor = new GameObject(object.getId() + 1, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane());
							player.setNextAnimation(new Animation(536));
							player.lock(2);
							player.faceObject(openedTrapdoor);
							World.spawnObjectTemporary(openedTrapdoor, Ticks.fromMinutes(1));
						}
						break;
					case "closed chest":
						if (objectDef.containsOption(0, "Open")) {
							GameObject openedChest = new GameObject(object.getId() + 1, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane());
							if (!openedChest.getDefinitions().containsOption("Search") && !openedChest.getDefinitions().containsOption("Close"))
								return;
							player.setNextAnimation(new Animation(536));
							player.lock(2);
							player.faceObject(openedChest);
							World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
						}
						break;
					case "open chest":
						if (objectDef.containsOption(0, "Search"))
							player.sendMessage("You search the chest but find nothing.");
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
							player.setNextAnimation(new Animation(PlayerCombatKt.getWeaponAttackEmote(player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle())));
							slashWeb(player, object);
						}
						break;
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
							player.getPrayer().worshipAltar();
							if (id == 6552) {
								player.startConversation(new Dialogue().addOptions("Change spellbooks?", ops -> {
									ops.add("Yes, replace my spellbook.", () -> {
										if (player.getCombatDefinitions().getSpellbook() != Spellbook.ANCIENT) {
											player.sendMessage("Your mind clears and you switch back to the ancient spellbook.");
											player.getCombatDefinitions().setSpellbook(Spellbook.ANCIENT);
										} else {
											player.sendMessage("Your mind clears and you switch back to the normal spellbook.");
											player.getCombatDefinitions().setSpellbook(Spellbook.MODERN);
										}
									});
									ops.add("Nevermind.");
								}));
							}
						}
						break;
					default:

						break;
				}
		}));
		Logger.debug(ObjectHandler.class, "handleOption1", "Object interaction 1: " + object);
	}

	public static void handleOption2(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);
		final int id = object.getId();

		if (!def.containsOption(1) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP2, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick2(object) || player.getTreasureTrailsManager().useObject(object))
				return;
			else if (id == 17010) {
				player.startConversation(new Dialogue().addOptions("Change spellbooks?", ops -> {
					ops.add("Yes, replace my spellbook.", () -> {
						if (player.getCombatDefinitions().getSpellbook() != Spellbook.LUNAR) {
							if (!player.isQuestComplete(Quest.LUNAR_DIPLOMACY, "to use the Lunar Spellbook."))
								return;
							player.sendMessage("Your mind clears and you switch back to the ancient spellbook.");
							player.getCombatDefinitions().setSpellbook(Spellbook.LUNAR);
						} else {
							player.sendMessage("Your mind clears and you switch back to the normal spellbook.");
							player.getCombatDefinitions().setSpellbook(Spellbook.MODERN);
						}
					});
					ops.add("Nevermind.");
				}));
			} else if (id == 62677)
				player.getDominionTower().openRewards();
			else if (id == 62688)
				player.simpleDialogue("You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".");
			else if (id == 68107)
				FightKilnController.enterFightKiln(player, true);
			else if (id == 70795) {
				if (!Agility.hasLevel(player, 50))
					return;
				player.useStairs(-1, Tile.of(1206, 6506, 0));
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
			else if (id == 22697)
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
					case "ladder":
						handleLadder(player, object, 2);
						break;
					case "staircase":
						handleStaircases(player, object, 2);
						break;
					default:
						break;
				}
			Logger.debug(ObjectHandler.class, "handleOption2", "Object interaction 2: " + object);
		}));
	}

	public static void handleOption3(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);

		if (!def.containsOption(2) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP3, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick3(object) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP3, true)))
				return;

			switch (def.getName().toLowerCase()) {
				case "bank":
				case "bank chest":
				case "bank booth":
				case "bank table":
				case "counter":
					if (def.containsOption(2, "Collect") || def.containsOption(2, "Use")) {
						//						player.sendOptionDialogue("What would you like to do?", new String[] { "Loadouts", "Clan Bank" }, ops -> {
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
			Logger.debug(ObjectHandler.class, "handleOption3", "Object interaction 3: " + object);
		}));
	}

	public static void handleOption4(final Player player, final GameObject object) {
		final ObjectDefinitions def = object.getDefinitions(player);

		if (!def.containsOption(3) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP4, false)))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.stopAll();
			player.faceObject(object);
			if (!player.getControllerManager().processObjectClick4(object) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP4, true)))
				return;
			switch (def.getName().toLowerCase()) {
				default:
					player.sendMessage("Nothing interesting happens.");
					break;
			}
			Logger.debug(ObjectHandler.class, "handleOption4", "Object interaction 4: " + object);
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
			if (!player.getControllerManager().processObjectClick5(object) || PluginManager.handle(new ObjectClickEvent(player, object, ClientPacket.OBJECT_OP5, true)))
				return;
			if (id == -1) {
				// unused
			} else
				switch (def.getName().toLowerCase()) {
					case "fire":
						if (def.containsOption(4, "Add-logs"))
							Bonfire.addLogs(player, object);
						break;
					default:
						player.sendMessage("Nothing interesting happens.");
						break;
				}
			Logger.debug(ObjectHandler.class, "handleOption5", "Object interaction 5: " + object);
		}));
	}

	public static void handleOptionExamine(final Player player, final GameObject object) {
		player.getPackets().sendObjectMessage(0, 0xFFFFFF, object, "It's " + Utils.addArticle(object.getDefinitions(player).getName()).toLowerCase() + ".");
		if (player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage(object.toString());
			if (object.getDefinitions().varpBit != -1)
				player.sendMessage("Transforms with varbit " + object.getDefinitions().varpBit + " - current obj: " + object.getDefinitions(player).id);
			if (object.getDefinitions().varp != -1)
				player.sendMessage("Transforms with var " + object.getDefinitions().varp + " - current obj: " + object.getDefinitions(player).id);
			if (object.getMeshModifier() != null)
				player.sendMessage(Arrays.toString(object.getMeshModifier().getModelIds()));
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
			player.useStairs(-1, Tile.of(player.getX(), player.getY(), player.getPlane() + 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			player.useStairs(-1, Tile.of(player.getX(), player.getY(), player.getPlane() - 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.promptUpDown("Go up the stairs.", player.transform(0, 0, 1), "Go down the stairs.", player.transform(0, 0, -1));
		} else
			return false;
		return false;
	}

	private static boolean handleLadder(Player player, GameObject object, int optionId) {
		String option = object.getDefinitions(player).getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			player.useStairs(828, Tile.of(player.getX(), player.getY(), player.getPlane() + 1));
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			player.useStairs(828, Tile.of(player.getX(), player.getY(), player.getPlane() - 1));
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.promptUpDown(828, "Climb up the ladder.", player.transform(0, 0, 1), "Climb down the ladder.", player.transform(0, 0, -1));
		} else
			return false;
		return true;
	}

	public static void handleItemOnObject(final Player player, final GameObject object, final int interfaceId, final Item item, final int slot) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions(player);

		if (PluginManager.handle(new ItemOnObjectEvent(player, item, object, false)) || FishingFerretRoom.handleFerretThrow(player, object, item))
			return;

		player.setRouteEvent(new RouteEvent(object, () -> {
			player.faceObject(object);

			if (!player.getControllerManager().handleItemOnObject(object, item) || Ectofuntus.handleItemOnObject(player, itemId, object.getId()))
				return;
			if (itemId == 1925 && objectDef.getName().toLowerCase().contains("sand") && objectDef.getName().toLowerCase().contains("pit")) {
				player.getActionManager().setAction(new SandBucketFill());
				return;
			}
			if (itemId == JewelryCraftingKt.GOLD_BAR && (objectDef.getName().toLowerCase().contains("furnace") || object.getDefinitions(player).getName().equalsIgnoreCase("clay forge") || object.getDefinitions(player).getName().equalsIgnoreCase("lava furnace"))) {
				JewelryCraftingKt.openInterface(player, false);
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
			else if (object.getId() == 28352 || object.getId() == 28550)
				Incubator.useEgg(player, itemId);
			else if (object.getId() == 733 || object.getId() == 64729) {
				player.setNextAnimation(new Animation(PlayerCombatKt.getWeaponAttackEmote(player.getEquipment().getWeaponId(), player.getCombatDefinitions().getAttackStyle())));
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
			} else
				PluginManager.handle(new ItemOnObjectEvent(player, item, object, true));
		}));
	}
}