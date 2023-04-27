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
package com.rs.game.content.world.areas.ardougne;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.monksfriend.dialogues.BrotherCedricMonksFriendD;
import com.rs.game.content.quests.monksfriend.dialogues.BrotherOmadMonksFriendD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.thieving.Thieving;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.pathing.FixedTileStrategy;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.pathing.RouteFinder;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Ardougne  {
	
	public static NPCClickHandler handleSpiritOfScorpius = new NPCClickHandler(new Object[] { 492 }, e -> {
		boolean observatoryDone = e.getPlayer().isQuestComplete(Quest.OBSERVATORY_QUEST, "to speak to Scorpius");
		e.getPlayer().startConversation(new Dialogue()
				.addNextIf(() -> e.getPlayer().getEquipment().getAmuletId() != 552, new Dialogue()
					.addSimple("This powerful spirit seems capable of speaking to you even though you are not wearing an Amulet of Ghostspeak."))
				.addNPC(492, HeadE.OLD_CHAT, observatoryDone ? "Who treads upon my grave?" : "How dare you disturb me!")
				.addNextIf(() -> observatoryDone, new Dialogue().addOptions(ops -> {
					if (!e.getPlayer().getBool("spiritOfScorpiusTalkedTo")) {
						ops.add("I seek your wisdom.")
							.addPlayer(HeadE.CALM_TALK, "I seek your wisdom.")
							.addNPC(492, HeadE.OLD_CHAT, "Indeed, I feel you have beheld the far places in the heavens.")
							.addNPC(492, HeadE.OLD_CHAT, "My Lord instructs me to help you.")
							.addNPC(492, HeadE.OLD_CHAT, "Here is a mould to make a token for our Lord;a mould for the unholy symbol of Zamorak.")
							.addItem(1594, "The ghost gives you a casting mould.", () -> {
								e.getPlayer().save("spiritOfScorpiusTalkedTo", true);
								e.getPlayer().getInventory().addItemDrop(1594, 1);
							});
					} else {
						Dialogue blessing = ops.add("I have come to seek a blessing.");
						if (!e.getPlayer().getInventory().containsItem(1722)) {
							blessing.addNPC(492, HeadE.OLD_CHAT, "No blessings will be given to those who have no symbol of our Lord's love.");
						} else {
							blessing.addNPC(492, HeadE.OLD_CHAT, "I see you have the unholy symbol of our Lord. I will bless it for you.")
								.addSimple("The ghost mutters in a strange voice.")
								.addSimple("The unholy symbol throbs with power.", () -> e.getPlayer().getInventory().replace(1722, 1724))
								.addNPC(492, HeadE.OLD_CHAT, "The symbol of our Lord has been blessed with power!")
								.addNPC(492, HeadE.OLD_CHAT, "My master calls.");
						}
						
						Dialogue mould = ops.add("I need another unholy symbol mould.");
						if (e.getPlayer().getInventory().containsItem(1594)) {
							mould.addNPC(492, HeadE.OLD_CHAT, "One you already have, another is not needed.");
						} else {
							mould.addNPC(492, HeadE.OLD_CHAT, "To lose an object is easy to replace. To lose the affections of our Lord is impossible to forgive.")
								.addItem(1594, "The ghost hands you another mould.", () -> e.getPlayer().getInventory().addItemDrop(1594, 1));
						}
						
					}
					ops.add("I have come to kill you.")
						.addPlayer(HeadE.ANGRY, "I have come to kill you.")
						.addNPC(492, HeadE.OLD_CHAT, "The might of mortals, to me, is as the dust to the sea.");
				})));
	});

	public static NPCClickHandler handleBrotherOmad = new NPCClickHandler(new Object[] { 279 }, e -> {
		e.getPlayer().startConversation(new BrotherOmadMonksFriendD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleBrotherCedric = new NPCClickHandler(new Object[] { 280 }, e -> {
		e.getPlayer().startConversation(new BrotherCedricMonksFriendD(e.getPlayer()).getStart());
	});

	public static NPCClickHandler handleTownCrier = new NPCClickHandler(new Object[] { 6138 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.ARDOUGNE_CLOAK).getStart());
					}
				});
				create();
			}
		});
	});
	
	private static void bank(Player p) {
		if(!p.getBank().checkPin())
			return;
		p.getTempAttribs().setB("open_zmi_collect", false);
		p.getInterfaceManager().sendInterface(619);
	}
	private static void collect(Player p) {
		if(!p.getBank().checkPin())
			return;
		p.getTempAttribs().setB("open_zmi_collect", true);
		p.getInterfaceManager().sendInterface(619);
	}

	public static NPCClickHandler handleZMIBanker = new NPCClickHandler(new Object[] { 6362 }, e -> {
		switch(e.getOption()) {
		case "Bank"-> bank(e.getPlayer());
		case "Collect"-> collect(e.getPlayer());
		case "Talk-to"-> {
			int NPC = e.getNPCId();
			e.getPlayer().startConversation(new Dialogue()
					.addNPC(NPC, HeadE.CALM_TALK, "Well met, fellow adventurer! How can I help you?")
					.addPlayer(HeadE.HAPPY_TALKING, "Who are you?")
					.addNPC(NPC, HeadE.CALM_TALK, "How frightfully rude of me, my dear " +e.getPlayer().getPronoun("chap", "lady") + ". My name is Eniola and I work for that most excellent enterprise, the Bank of Gielinor.")
					.addPlayer(HeadE.HAPPY_TALKING, "If you work for the bank, what are you doing here?")
					.addNPC(NPC, HeadE.CALM_TALK, "My presence here is the start of a new enterprise of travelling banks.")
					.addNPC(NPC, HeadE.CALM_TALK, "I can give you bank services for a price...")
					.addPlayer(HeadE.HAPPY_TALKING, "What price?")
					.addNPC(NPC, HeadE.CALM_TALK, "20 of any rune...")
					.addNPC(NPC, HeadE.CALM_TALK, "Would you like to bank or collect?")
					.addOptions("Choose an option:", ops -> {
						ops.add("Bank", () -> {
							e.getPlayer().closeInterfaces(); 
							bank(e.getPlayer());
						});
						ops.add("Collect", () -> {
							 e.getPlayer().closeInterfaces(); 
							 collect(e.getPlayer());
						});
						ops.add("Nevermind");
					}));
		}
		}
	});

	private static void checkRunesBankOrCollect(Player p, int runeId) {
		Item runes = new Item(runeId, 20);
		if(p.getInventory().containsItems(runes)) {
			p.getInventory().removeItems(runes);
			if(p.getTempAttribs().getB("open_zmi_collect"))
				GE.openCollection(p);
			else
				p.getBank().open();
		} else {
			p.sendMessage("You don't have enough " + runes.getName() + "s!");
			p.closeInterfaces();
		}
	}
	
	public static ButtonClickHandler handleZMIRunesInterface = new ButtonClickHandler(619, e -> {
		Player p = e.getPlayer();
		switch(e.getComponentId()) {
			case 28 -> checkRunesBankOrCollect(p, 556);
			case 29 -> checkRunesBankOrCollect(p, 558);
			case 30 -> checkRunesBankOrCollect(p, 555);
			case 31 -> checkRunesBankOrCollect(p, 557);
			case 32 -> checkRunesBankOrCollect(p, 554);
			case 33 -> checkRunesBankOrCollect(p, 559);
			case 34 -> checkRunesBankOrCollect(p, 564);
			case 35 -> checkRunesBankOrCollect(p, 562);
			case 36 -> checkRunesBankOrCollect(p, 9075);
			case 37 -> checkRunesBankOrCollect(p, 563);
			case 38 -> checkRunesBankOrCollect(p, 560);
			case 39 -> checkRunesBankOrCollect(p, 565);
			case 40 -> checkRunesBankOrCollect(p, 561);
			case 41 -> checkRunesBankOrCollect(p, 566);
		}
	});

	public static NPCClickHandler handleDrOrbon = new NPCClickHandler(new Object[] { 290 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.ARDOUGNE_CLOAK).getStart());
					}
				});
				create();
			}
		});
	});

	public static NPCClickHandler handleAleck = new NPCClickHandler(new Object[] { 5110 }, e -> {
		if (e.getOpNum() == 1)
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("Do you have anything for trade?", () -> ShopsHandler.openShop(player, "alecks_hunter_emporium"));
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.ARDOUGNE_CLOAK).getStart());
						}
					});
					create();
				}
			});
		else
			ShopsHandler.openShop(e.getPlayer(), "alecks_hunter_emporium");
	});

	public static ObjectClickHandler handleZMIShortcut = new ObjectClickHandler(new Object[] { 26844, 26845 }, e -> {
		if (e.getObject().getId() == 26844)
			e.getPlayer().setNextTile(Tile.of(3312, 4817, 0));
		else if (e.getObject().getId() == 26845)
			e.getPlayer().setNextTile(Tile.of(3308, 4819, 0));
	});

	public static ObjectClickHandler handleWitchahavenDungeonEntrance = new ObjectClickHandler(new Object[] { 18266 }, e -> {
		Player p = e.getPlayer();
		p.setNextTile(Tile.of(2696, 9683, 0));
	});

	public static ObjectClickHandler handleClockTowerDungeonEntrances = new ObjectClickHandler(new Object[] { 1754, 1756 }, new Tile[] { Tile.of(2566, 3242, 0), Tile.of(2566, 3231, 0), Tile.of(2569, 3231, 0), Tile.of(2566, 3227, 0), Tile.of(2569, 3227, 0), Tile.of(2572, 3227, 0), Tile.of(2621, 3261, 0) }, e -> {
		e.getPlayer().useLadder(Tile.of(e.getObject().getX(), e.getObject().getY()+6399, 0));
	});

	public static ObjectClickHandler handleClockTowerDungeonExits = new ObjectClickHandler(new Object[] { 32015 }, new Tile[] {  Tile.of(2566, 9642, 0), Tile.of(2572, 9631, 0), Tile.of(2566, 9631, 0), Tile.of(2566, 9627, 0), Tile.of(2569, 9627, 0), Tile.of(2572, 9627, 0), Tile.of(2576, 9655, 0), Tile.of(2621, 9661, 0) }, e -> {
		e.getPlayer().useLadder(Tile.of(e.getObject().getX(), e.getObject().getY()-6399, 0));
	});

	public static ObjectClickHandler handleArdougneSewerEntrance = new ObjectClickHandler(new Object[] { "Manhole" }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(e.getOption().equalsIgnoreCase("Open")) {
			GameObject openedHole = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
			p.faceObject(openedHole);
			World.spawnObjectTemporary(openedHole, Ticks.fromMinutes(1));
		}
		if(e.getOption().equalsIgnoreCase("Climb-Down"))
			if(obj.getTile().matches(Tile.of(2632, 3294, 0)))
				p.useStairs(833, Tile.of(2631, 9694, 0), 1, 2);
	});

	public static ObjectClickHandler handleArdougneSewerExit = new ObjectClickHandler(new Object[] { 32015 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(obj.getTile().matches(Tile.of(2632, 9694, 0)))
			p.ladder(Tile.of(2633, 3294, 0));
	});

	public static ObjectClickHandler handleWitchahavenDungeonExit = new ObjectClickHandler(new Object[] { 33246 }, e -> {
		Player p = e.getPlayer();
		p.setNextTile(Tile.of(2697, 3283, 0));
	});

	public static ObjectClickHandler handleClocktowerDungeonLadders = new ObjectClickHandler(new Object[] { 1755, 1756 }, e -> {
		e.getPlayer().ladder(Tile.of(e.getPlayer().getX(), e.getPlayer().getY() + (e.getObjectId() == 1756 ? 6400 : -6400), 0));
	});

	public static ObjectClickHandler handleWestArdyPrisonStairs = new ObjectClickHandler(new Object[] { 2523, 2522 }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(0, e.getPlayer().getY() > 9000 ? -6400 : 6400, 0));
	});

	public static ObjectClickHandler handleLogBalanceShortcut = new ObjectClickHandler(new Object[] { 35997, 35999 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 33))
			return;
		AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getObjectId() == 35999 ? -4 : 4, 0, 0), 3);
	});

	public static ObjectClickHandler handleEnterUndergroundPass = new ObjectClickHandler(new Object[] { 36000 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2312, 3217, 0));
	});

	public static NPCClickHandler handleDarkMage = new NPCClickHandler(new Object[] { 1001 }, e -> {
		e.getPlayer().sendOptionDialogue("Buy an Iban's staff for 100,000 gold?", ops -> {
			ops.add("Yes, I'll pay 100,000 gold for a staff.", () -> {
				if (e.getPlayer().getInventory().hasCoins(100000)) {
					e.getPlayer().getInventory().removeCoins(100000);
					e.getPlayer().getInventory().addItem(1409, 1);
				} else
					e.getPlayer().sendMessage("You don't have enough money.");
			});
			ops.add("No, that's too much.");
		});
	});

	public static NPCClickHandler handleSilkTrader = new NPCClickHandler(new Object[] { 574 }, e -> {
		if (e.getOpNum() == 3) {
			e.getPlayer().sendOptionDialogue("Exchange your silk for 20gp each?", ops -> {
				ops.add("Yes", () -> {
					int number = e.getPlayer().getInventory().getAmountOf(950);
					e.getPlayer().getInventory().deleteItem(950, number);
					e.getPlayer().getInventory().addCoins(20 * number);
				});
				ops.add("No");
			});
		}
	});

	public static ObjectClickHandler handleHouseDoors = new ObjectClickHandler(new Object[] { 34812 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP1)
			e.getPlayer().sendMessage("This door is securely locked.");
		else
			e.getPlayer().handleOneWayDoor(e.getObject(), 1, 3);
	});

	public static ObjectClickHandler handleChaosDruidTowerDoor = new ObjectClickHandler(new Object[] { 2554 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP1)
			e.getPlayer().sendMessage("This door is securely locked.");
		else
			Doors.handleDoor(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleEnterTempleOfIkov = new ObjectClickHandler(new Object[] { 1754 }, Tile.of(2677, 3405, 0), e -> {
		e.getPlayer().useStairs(827, e.getPlayer().transform(0, 6400, 0), 1, 2);
	});

	public static ObjectClickHandler handleExitTempleOfIkov = new ObjectClickHandler(new Object[] { 32015 }, Tile.of(2677, 9805, 0), e -> {
		e.getPlayer().useStairs(828, e.getPlayer().transform(0, -6400, 0), 1, 2);
	});

	public static ObjectClickHandler handleEnterBootsofLightnessRoom = new ObjectClickHandler(new Object[] { 35121 }, Tile.of(2650, 9804, 0), e -> {
		e.getPlayer().useStairs(-1, e.getPlayer().transform(-8, -41, 0), 1, 2);
	});

	public static ObjectClickHandler handleExitBootsofLightnessRoom = new ObjectClickHandler(new Object[] { 96 }, Tile.of(2638, 9763, 0), e -> {
		e.getPlayer().useStairs(-1, e.getPlayer().transform(8, 41, 0), 1, 2);
	});

	public static ObjectClickHandler handleLegendsGuildDoor = new ObjectClickHandler(new Object[] { "Legends Guild door" }, e -> {
		e.getPlayer().handleOneWayDoor(e.getObject(), 1, 3);
	});

	public static ObjectClickHandler handleEnterLegendsGuildBasement = new ObjectClickHandler(new Object[] { 41425 }, e -> {
		e.getPlayer().setNextTile(e.getPlayer().transform(-3, 6400, 0));
	});

	public static ObjectClickHandler handleExitLegendsGuildBasement = new ObjectClickHandler(new Object[] { 32048 }, Tile.of(2717, 9773, 0), e -> {
		e.getPlayer().setNextTile(e.getPlayer().transform(3, -6400, 0));
	});

	public static ObjectClickHandler handleMournerTrapdoor = new ObjectClickHandler(new Object[] { 8783 }, e -> {
		e.getPlayer().ladder(Tile.of(2044, 4649, 0));
	});

	public static ObjectClickHandler handleMournerBasementLadder = new ObjectClickHandler(new Object[] { 8785 }, e -> {
		e.getPlayer().ladder(Tile.of(2543, 3327, 0));
	});

	public static ObjectClickHandler handleRangeGuildEnter = new ObjectClickHandler(false, new Object[] { 2514 }, e -> {
		if (e.getPlayer().getSkills().getLevelForXp(Constants.RANGE) <= 40) {
			e.getPlayer().sendMessage("You require 40 Ranged to enter the Ranging Guild.");
			return;
		}
		if (RouteFinder.find(2659, 3437, 0, 1, new FixedTileStrategy(e.getPlayer().getX(), e.getPlayer().getY()), false).getStepCount() != -1)
			e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(2659, 3437, 0), () -> {
				Doors.handleDoor(e.getPlayer(), e.getObject());
				e.getPlayer().addWalkSteps(Tile.of(2657, 3439, 0), 5, false);
			}));
		else
			e.getPlayer().setRouteEvent(new RouteEvent(Tile.of(2657, 3439, 0), () -> {
				Doors.handleDoor(e.getPlayer(), e.getObject());
				e.getPlayer().addWalkSteps(Tile.of(2659, 3437, 0), 5, false);
			}));
	});

	public static ObjectClickHandler handleNatureRuneChests = new ObjectClickHandler(new Object[] { 2567, 2568 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP2)
			Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 28, 21, 25, new Item(995, 3), new Item(561, 1));
	});

	public static ObjectClickHandler handleBloodRuneChests = new ObjectClickHandler(new Object[] { 2569 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP2)
			Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 59, 210, 250, new Item(995, 500), new Item(565, 2));
	});

	public static ObjectClickHandler handle50CoinChests = new ObjectClickHandler(new Object[] { 2566 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP2)
			Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 43, 50, 125, new Item(995, 50));
	});

	public static ObjectClickHandler handleClosedRangeGuildChests = new ObjectClickHandler(new Object[] { 375 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP1) {
			GameObject openedChest = new GameObject(378, e.getObject().getType(), e.getObject().getRotation(), e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
			e.getPlayer().faceObject(openedChest);
			e.getPlayer().setNextAnimation(new Animation(536));
			e.getPlayer().lock(2);
			World.spawnObjectTemporary(openedChest, Ticks.fromSeconds(30));
		}
	});

	public static ObjectClickHandler handleOpenRangeGuildChests = new ObjectClickHandler(new Object[] { 378 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP2) {
			e.getPlayer().sendMessage("You search the chest but find nothing.");
			e.getPlayer().faceObject(e.getObject());
		} else if (e.getOpNum() == ClientPacket.OBJECT_OP3) {
			e.getPlayer().faceObject(e.getObject());
			e.getPlayer().setNextAnimation(new Animation(536));
			e.getPlayer().lock(2);
			World.removeObject(e.getObject());
		}
	});
}
