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
package com.rs.game.player.content.world.regions;

import com.rs.game.World;
import com.rs.game.ge.GE;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.FixedTileStrategy;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.pathing.RouteFinder;
import com.rs.game.player.Player;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.skills.thieving.Thieving;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.content.world.npcs.Banker;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Ardougne  {

	public static NPCClickHandler handleTownCrier = new NPCClickHandler(6138) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.ARDOUGNE_CLOAK).getStart());
						}
					});
				}
			});
		}
	};

    public static NPCClickHandler handleZMIBanker = new NPCClickHandler(6362) {
        @Override
        public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
            switch(e.getOption()) {
//                case "Bank":
//                    p.getBank().open();
//                    break;
//                case "Collect":
//                    GE.openCollection(p);
//                    break;
                case "Talk-to":
                    e.getPlayer().startConversation(new Banker(e.getPlayer(), e.getNPC()));
                    break;
            }

        }
    };

	public static NPCClickHandler handleDrOrbon = new NPCClickHandler(290) {
		@Override
		public void handle(NPCClickEvent e) {
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
		}
	};

	public static NPCClickHandler handleAleck = new NPCClickHandler(5110) {
		@Override
		public void handle(NPCClickEvent e) {
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
					}
				});
			else
				ShopsHandler.openShop(e.getPlayer(), "alecks_hunter_emporium");
		}
	};

	public static ObjectClickHandler handleZMIShortcut = new ObjectClickHandler(new Object[] { 26844, 26845 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getId() == 26844)
				e.getPlayer().setNextWorldTile(new WorldTile(3312, 4817, 0));
			else if (e.getObject().getId() == 26845)
				e.getPlayer().setNextWorldTile(new WorldTile(3308, 4819, 0));
		}
	};

	public static ObjectClickHandler handleWitchahavenDungeonEntrance = new ObjectClickHandler(new Object[] { 18266 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			p.setNextWorldTile(new WorldTile(2696, 9683, 0));
		}
	};

	public static ObjectClickHandler handleArdougneSewerEntrance = new ObjectClickHandler(new Object[] { "Manhole" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getOption().equalsIgnoreCase("Open")) {
				GameObject openedHole = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
				p.faceObject(openedHole);
				World.spawnObjectTemporary(openedHole, Ticks.fromMinutes(1));
			}
			if(e.getOption().equalsIgnoreCase("Climb-Down"))
				if(obj.matches(new WorldTile(2632, 3294, 0)))
					p.useStairs(833, new WorldTile(2631, 9694, 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleArdougneSewerExit = new ObjectClickHandler(new Object[] { 32015 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(obj.matches(new WorldTile(2632, 9694, 0)))
				p.ladder(new WorldTile(2633, 3294, 0));
		}
	};

	public static ObjectClickHandler handleWitchahavenDungeonExit = new ObjectClickHandler(new Object[] { 33246 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			p.setNextWorldTile(new WorldTile(2697, 3283, 0));
		}
	};

	public static ObjectClickHandler handleClocktowerDungeonLadders = new ObjectClickHandler(new Object[] { 1755, 1756 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(e.getPlayer().getX(), e.getPlayer().getY() + (e.getObjectId() == 1756 ? 6400 : -6400), 0));
		}
	};

	public static ObjectClickHandler handleWestArdyPrisonStairs = new ObjectClickHandler(new Object[] { 2523, 2522 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(0, e.getPlayer().getY() > 9000 ? -6400 : 6400, 0));
		}
	};

	public static ObjectClickHandler handleLogBalanceShortcut = new ObjectClickHandler(new Object[] { 35997, 35999 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 33))
				return;
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getObjectId() == 35999 ? -4 : 4, 0, 0), 3);
		}
	};

	public static ObjectClickHandler handleEnterUndergroundPass = new ObjectClickHandler(new Object[] { 36000 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2312, 3217, 0));
		}
	};

	public static NPCClickHandler handleDarkMage = new NPCClickHandler(1001) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().sendOptionDialogue("Buy an Iban's staff for 100,000 gold?", new String[] { "Yes, I'll pay 100,000 gold for a staff.", "No, that's too much." }, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (option == 1)
						if (player.getInventory().containsItem(995, 100000)) {
							player.getInventory().deleteItem(995, 100000);
							player.getInventory().addItem(1409, 1);
						} else
							player.sendMessage("You don't have enough money.");
				}
			});
		}
	};

	public static NPCClickHandler handleAemad = new NPCClickHandler(590) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 3)
				ShopsHandler.openShop(e.getPlayer(), "aemads_adventuring_supplies");
		}
	};

	public static NPCClickHandler handleSilkTrader = new NPCClickHandler(574) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOpNum() == 3)
				e.getPlayer().sendOptionDialogue("Exchange your silk for 20gp each?", new String[] { "Yes", "No" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1) {
							int number = player.getInventory().getAmountOf(950);
							player.getInventory().deleteItem(950, number);
							player.getInventory().addItem(995, 20 * number);
						}
					}
				});
		}
	};

	public static ObjectClickHandler handleHouseDoors = new ObjectClickHandler(new Object[] { 34812 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP1)
				e.getPlayer().sendMessage("This door is securely locked.");
			else
				e.getPlayer().handleOneWayDoor(e.getObject(), 1, 3);
		}
	};

	public static ObjectClickHandler handleChaosDruidTowerDoor = new ObjectClickHandler(new Object[] { 2554 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP1)
				e.getPlayer().sendMessage("This door is securely locked.");
			else
				Doors.handleDoor(e.getPlayer(), e.getObject());
		}
	};

	public static ObjectClickHandler handleEnterTempleOfIkov = new ObjectClickHandler(new Object[] { 1754 }, new WorldTile(2677, 3405, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(827, e.getPlayer().transform(0, 6400, 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleExitTempleOfIkov = new ObjectClickHandler(new Object[] { 32015 }, new WorldTile(2677, 9805, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(828, e.getPlayer().transform(0, -6400, 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleEnterBootsofLightnessRoom = new ObjectClickHandler(new Object[] { 35121 }, new WorldTile(2650, 9804, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, e.getPlayer().transform(-8, -41, 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleExitBootsofLightnessRoom = new ObjectClickHandler(new Object[] { 96 }, new WorldTile(2638, 9763, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, e.getPlayer().transform(8, 41, 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleLegendsGuildDoor = new ObjectClickHandler(new Object[] { "Legends Guild door" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().handleOneWayDoor(e.getObject(), 1, 3);
		}
	};

	public static ObjectClickHandler handleEnterLegendsGuildBasement = new ObjectClickHandler(new Object[] { 41425 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getPlayer().transform(-3, 6400, 0));
		}
	};

	public static ObjectClickHandler handleExitLegendsGuildBasement = new ObjectClickHandler(new Object[] { 32048 }, new WorldTile(2717, 9773, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getPlayer().transform(3, -6400, 0));
		}
	};

	public static ObjectClickHandler handleMournerTrapdoor = new ObjectClickHandler(new Object[] { 8783 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2044, 4649, 0));
		}
	};

	public static ObjectClickHandler handleMournerBasementLadder = new ObjectClickHandler(new Object[] { 8785 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().ladder(new WorldTile(2543, 3327, 0));
		}
	};

	public static ObjectClickHandler handleRangeGuildEnter = new ObjectClickHandler(false, new Object[] { 2514 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSkills().getLevelForXp(Constants.RANGE) <= 40) {
				e.getPlayer().sendMessage("You require 40 Ranged to enter the Ranging Guild.");
				return;
			}
			if (RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, 2659, 3437, 0, 1, new FixedTileStrategy(e.getPlayer().getX(), e.getPlayer().getY()), false) != -1)
				e.getPlayer().setRouteEvent(new RouteEvent(new WorldTile(2659, 3437, 0), () -> {
					Doors.handleDoor(e.getPlayer(), e.getObject());
					e.getPlayer().addWalkSteps(new WorldTile(2657, 3439, 0), 5, false);
				}));
			else
				e.getPlayer().setRouteEvent(new RouteEvent(new WorldTile(2657, 3439, 0), () -> {
					Doors.handleDoor(e.getPlayer(), e.getObject());
					e.getPlayer().addWalkSteps(new WorldTile(2659, 3437, 0), 5, false);
				}));
		}
	};

	public static ObjectClickHandler handleNatureRuneChests = new ObjectClickHandler(new Object[] { 2567, 2568 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2)
				Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 28, 21, 25, new Item(995, 3), new Item(561, 1));
		}
	};

	public static ObjectClickHandler handleBloodRuneChests = new ObjectClickHandler(new Object[] { 2569 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2)
				Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 59, 210, 250, new Item(995, 500), new Item(565, 2));
		}
	};

	public static ObjectClickHandler handle50CoinChests = new ObjectClickHandler(new Object[] { 2566 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2)
				Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 43, 50, 125, new Item(995, 50));
		}
	};

	public static ObjectClickHandler handleClosedRangeGuildChests = new ObjectClickHandler(new Object[] { 375 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP1) {
				GameObject openedChest = new GameObject(378, e.getObject().getType(), e.getObject().getRotation(), e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
				e.getPlayer().faceObject(openedChest);
				e.getPlayer().setNextAnimation(new Animation(536));
				e.getPlayer().lock(2);
				World.spawnObjectTemporary(openedChest, Ticks.fromSeconds(30));
			}
		}
	};

	public static ObjectClickHandler handleOpenRangeGuildChests = new ObjectClickHandler(new Object[] { 378 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOpNum() == ClientPacket.OBJECT_OP2) {
				e.getPlayer().sendMessage("You search the chest but find nothing.");
				e.getPlayer().faceObject(e.getObject());
			} else if (e.getOpNum() == ClientPacket.OBJECT_OP3) {
				e.getPlayer().faceObject(e.getObject());
				e.getPlayer().setNextAnimation(new Animation(536));
				e.getPlayer().lock(2);
				World.removeObject(e.getObject());
			}
		}
	};
}
