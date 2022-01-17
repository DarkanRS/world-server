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

import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.player.content.achievements.AchievementDef;
import com.rs.game.player.content.achievements.AchievementDef.Area;
import com.rs.game.player.content.achievements.AchievementDef.Difficulty;
import com.rs.game.player.content.achievements.AchievementSystemDialogue;
import com.rs.game.player.content.achievements.SetReward;
import com.rs.game.player.content.combat.XPType;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.agility.Agility;
import com.rs.game.player.content.world.AgilityShortcuts;
import com.rs.game.player.content.world.doors.Doors;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.dragonslayer.GuildMasterDragonSlayerD;
import com.rs.game.player.quests.handlers.knightssword.KnightsSword;
import com.rs.game.player.quests.handlers.knightssword.ReldoKnightsSwordD;
import com.rs.game.player.quests.handlers.shieldofarrav.BaraekShieldOfArravD;
import com.rs.game.player.quests.handlers.shieldofarrav.CharlieTheTrampArravD;
import com.rs.game.player.quests.handlers.shieldofarrav.KatrineShieldOfArravD;
import com.rs.game.player.quests.handlers.shieldofarrav.KingRoaldShieldOfArravD;
import com.rs.game.player.quests.handlers.shieldofarrav.MuseumCuratorArravD;
import com.rs.game.player.quests.handlers.shieldofarrav.ReldoShieldOfArravD;
import com.rs.game.player.quests.handlers.shieldofarrav.StravenShieldOfArravD;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;



@PluginEventHandler
public class Varrock {

	public static NPCClickHandler handleBlueMoonBartender = new NPCClickHandler(733) {
		@Override
		public void handle(NPCClickEvent e) {
			Player p = e.getPlayer();

			p.setRouteEvent(new RouteEvent(new WorldTile(3224, 3397, 0), () -> {
				p.faceEntity(e.getNPC());
				if (p.getTreasureTrailsManager().useNPC(e.getNPC()))
					return;
				p.startConversation(new Conversation(p) {
					int BARTENDER = 733;
					{
						addNPC(BARTENDER, HeadE.HAPPY_TALKING, "What can I do yer for?");
						addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("A glass of your finest ale please.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "A glass of your finest ale please.")
										.addNPC(BARTENDER, HeadE.HAPPY_TALKING, "No problemo. That'll be 2 coins.")
										.addNext(()->{
											if(p.getInventory().containsItem(995, 2)) {
												p.getInventory().deleteItem(995, 2);
												p.getInventory().addItem(1917, 1);
												p.startConversation(new Conversation(p) { {
													addSimple("The bartender hands you a beer...");
													create();
												} });
											} else
												p.startConversation(new Conversation(p) { {
													addNPC(BARTENDER, HeadE.SKEPTICAL_THINKING, "You have 2 coins don't you?");
													addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "No..");
													addNPC(BARTENDER, HeadE.FRUSTRATED, "That's too bad...");
													create();
												} });
										}));
								option("Can you recommend where an adventurer might make his fortune?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Can you recommend where an adventurer might make his fortune?")
										.addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Ooh I don't know if I should be giving away information, makes the game too easy.")
										.addOptions("Choose an option:", new Options() {
											@Override
											public void create() {
												option("Oh ah well...", new Dialogue()
														.addPlayer(HeadE.SAD_MILD, "Oh ah well..."));
												option("Game? What are you talking about?", new Dialogue()
														.addPlayer(HeadE.SKEPTICAL_THINKING, "Game? What are you talking about?")
														.addNPC(BARTENDER, HeadE.TALKING_ALOT, "This world around us... is an online game... called RuneScape.")
														.addPlayer(HeadE.SKEPTICAL_THINKING, "Nope, still don't understand what you are talking about. What does 'online' mean?")
														.addNPC(BARTENDER, HeadE.TALKING_ALOT, "It's a sort of connection between magic boxes across the world, big " +
																"boxes on people's desktops and little ones people can carry. They can talk to each other to play games.")
														.addPlayer(HeadE.AMAZED_MILD, "I give up. You're obviously completely mad!"));
												option("Just a small clue?", new Dialogue()
														.addPlayer(HeadE.HAPPY_TALKING, "Just a small clue?")
														.addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Go and talk to the bartender at the Jolly Boar Inn, he doesn't " +
																"seem to mind giving away clues.")
														);
											}
										}));
								option("Do you know where I can get some good equipment", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Do you know where I can get some good equipment?")
										.addNPC(BARTENDER, HeadE.HAPPY_TALKING, "Well, there's the sword shop across the road, or there's also all sorts of " +
												"shops up around the market."));
							}
						});

						create();
					}
				});

			}, false));
		}
	};

	public static ObjectClickHandler handleVariousStaircases = new ObjectClickHandler(new Object[] { 24356 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(obj.getRotation() == 0)
				p.useStairs(-1, new WorldTile(p.getX(), obj.getY()+3, p.getPlane() + 1), 0, 1);
			else if (obj.getRotation() == 1)
				p.useStairs(-1, new WorldTile(p.getX()+4, p.getY(), p.getPlane() + 1), 0, 1);
			return;
		}
	};

	public static ObjectClickHandler handleChaosAltar = new ObjectClickHandler(new Object[] { 61 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(e.getOption().equalsIgnoreCase("Pray-at")) {
				final int maxPrayer = p.getSkills().getLevelForXp(Constants.PRAYER) * 10;
				if (p.getPrayer().getPoints() < maxPrayer) {
					p.lock(5);
					p.sendMessage("You pray to the gods...", true);
					p.setNextAnimation(new Animation(645));
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							p.getPrayer().restorePrayer(maxPrayer);
							p.sendMessage("...and recharged your prayer.", true);
						}
					}, 2);
				} else
					p.sendMessage("You already have full prayer.");
			} else if(e.getOption().equalsIgnoreCase("Check"))
				p.startConversation(new Conversation(p) {
					{
						addSimple("You find a small inscription at the bottom of the altar. It reads: 'Snarthon Candtrick Termanto'.");
						create();
					}
				});
		}
	};

	public static ObjectClickHandler handleDummies = new ObjectClickHandler(new Object[] { 23921 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSkills().getLevelForXp(Constants.ATTACK) >= 8) {
				e.getPlayer().sendMessage("There is nothing more you can learn from hitting a dummy.");
				return;
			}
			XPType type = e.getPlayer().getCombatDefinitions().getAttackStyle().getXpType();
			if (type != XPType.ACCURATE && type != XPType.AGGRESSIVE && type != XPType.CONTROLLED && type != XPType.DEFENSIVE) {
				e.getPlayer().sendMessage("You can't hit a dummy with that attack style.");
				return;
			}
			e.getPlayer().setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(e.getPlayer().getEquipment().getWeaponId(), e.getPlayer().getCombatDefinitions().getAttackStyle())));
			e.getPlayer().lock(3);
			World.sendObjectAnimation(e.getPlayer(), e.getObject(), new Animation(6482));
			e.getPlayer().getSkills().addXp(Constants.ATTACK, 5);
		}
	};

	public static NPCClickHandler handleReldo = new NPCClickHandler(647) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							if(!e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
								option("About Shield Of Arrav...", new ReldoShieldOfArravD(player).getStart());
							if(e.getPlayer().getQuestManager().getStage(Quest.KNIGHTS_SWORD) >= KnightsSword.TALK_TO_RELDO
									&& !e.getPlayer().getQuestManager().isComplete(Quest.KNIGHTS_SWORD))
								option("About Knight's Sword...", new ReldoKnightsSwordD(player).getStart());
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.VARROCK_ARMOR).getStart());
						}
					});
					create();
				}
			});
		}
	};

	public static ObjectClickHandler handleVarrockSewerEntrance = new ObjectClickHandler(new Object[] { "Manhole" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getOption().equalsIgnoreCase("Climb-Down"))
				if(obj.matches(new WorldTile(3237, 3458, 0)))
					p.useStairs(833, new WorldTile(3237, 9858, 0), 1, 2);
		}
	};

	public static NPCClickHandler handleBaraek = new NPCClickHandler(547) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
				e.getPlayer().sendMessage("Nothing interesting happens");
			else
				e.getPlayer().startConversation(new BaraekShieldOfArravD(e.getPlayer()).getStart());
		}
	};

	public static NPCClickHandler handleCharlie = new NPCClickHandler(641) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
				e.getPlayer().sendMessage("Nothing interesting happens");
			else
				e.getPlayer().startConversation(new CharlieTheTrampArravD(e.getPlayer()).getStart());
		}
	};

	public static NPCClickHandler handleKatrine = new NPCClickHandler(642) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
				e.getPlayer().sendMessage("Nothing interesting happens");
			else
				e.getPlayer().startConversation(new KatrineShieldOfArravD(e.getPlayer()).getStart());
		}
	};

	public static NPCClickHandler handleStraven = new NPCClickHandler(644) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
				e.getPlayer().sendMessage("Nothing interesting happens");
			else
				e.getPlayer().startConversation(new StravenShieldOfArravD(e.getPlayer()).getStart());
		}
	};

	public static NPCClickHandler handleJohnnyTheBeard = new NPCClickHandler(645) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().sendMessage("Johnny the beard is not interested in talking.");
		}
	};

	public static NPCClickHandler handleKingRoald = new NPCClickHandler(648) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CHEERFUL, "Hello.");
						if (!e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
							addOptions("What would you like to say?", new Options() {
								@Override
								public void create() {
									option("About Shield Of Arrav...", new KingRoaldShieldOfArravD(player).getStart());
									option("Farewell.");
								}
							});
						else {
							addNPC(648, HeadE.HAPPY_TALKING, "Thank you for your good work adventurer!");
							addPlayer(HeadE.HAPPY_TALKING, "You are welcome.");
						}
						create();
					}
				});
		}
	};

	public static NPCClickHandler handleMuseumCurator = new NPCClickHandler(646) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addOptions("What would you like to say?", new Options() {
							@Override
							public void create() {
								if (!e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
									option("About Shield Of Arrav...", new MuseumCuratorArravD(player).getStart());
								option("Farewell.");
							}
						});
						create();
					}
				});
		}
	};

	public static NPCClickHandler handleHistorianMinas = new NPCClickHandler(5931) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CHEERFUL, "Hello.");
						addOptions("What would you like to say?", new Options() {
							@Override
							public void create() {
								if (e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV) && !((boolean)e.getPlayer().get("claimedArravLamp")))
									option("About Shield Of Arrav...", new Dialogue()
											.addNPC(5931, HeadE.HAPPY_TALKING, "Thank you for returning the shield")
											.addSimple("A lamp is placed in your hand")
											.addNext(() -> {
												e.getPlayer().getInventory().addItem(4447, 1);
												e.getPlayer().save("claimedArravLamp", true);
											}));
								else if(e.getPlayer().getQuestManager().isComplete(Quest.SHIELD_OF_ARRAV))
									option("About Shield Of Arrav...", new Dialogue()
											.addNPC(5931, HeadE.HAPPY_TALKING, "Thank you for returning the shield"));
								else
									option("About Shield Of Arrav...", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "There is nothing to say."));
								option("Farewell.");
							}
						});
						create();
					}
				});
		}
	};

	public static NPCClickHandler handleRatBurgiss = new NPCClickHandler(5833) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.VARROCK_ARMOR).getStart());
							option("Farewell.");
						}
					});
					create();
				}
			});
		}
	};

	public static NPCClickHandler handleNaff = new NPCClickHandler(359) {
		@Override
		public void handle(NPCClickEvent e) {
			int max = 8;
			if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.ELITE, false))
				max = 80;
			else if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.HARD, false))
				max = 64;
			else if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.MEDIUM, false))
				max = 32;
			else if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.EASY, false))
				max = 16;
			int amountLeft = max - e.getPlayer().getDailyI("naffStavesBought");
			if (amountLeft <= 0) {
				e.getPlayer().sendMessage("Naff has no staves left today.");
				return;
			}
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You don't have enough inventory space to buy any staves.");
				return;
			}
			e.getPlayer().sendInputInteger("How many battlestaves would you like to buy? (" + amountLeft +" available)", amount -> {
				int coinsOnPlayer = e.getPlayer().getInventory().getAmountOf(995);
				int maxBuyable = coinsOnPlayer / 7000;
				if (amount > maxBuyable)
					amount = maxBuyable;
				if (amount > amountLeft)
					amount = amountLeft;
				if (amount <= 0) {
					e.getPlayer().sendMessage("You don't have enough money to buy any staves right now.");
					return;
				}
				final int finalAmount = amount;
				final int cost = 7000 * amount;
				e.getPlayer().sendOptionDialogue("Buy " + amount + " battlestaves for " + Utils.formatNumber(cost) + " coins?", new String[] { "Yes", "No thanks." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 2)
							return;
						if (!e.getPlayer().getInventory().containsItem(995, cost)) {
							e.getPlayer().sendMessage("You don't have enough money for that.");
							return;
						}
						e.getPlayer().getInventory().deleteItem(995, cost);
						e.getPlayer().getInventory().addItemDrop(1392, finalAmount);
						e.getPlayer().setDailyI("naffStavesBought", e.getPlayer().getDailyI("naffStavesBought") + finalAmount);
					}
				});
			});
		}
	};

	public static NPCClickHandler handleDealga = new NPCClickHandler(11475) {
		@Override
		public void handle(NPCClickEvent e) {
			ShopsHandler.openShop(e.getPlayer(), "dealgas_scimitar_emporium");
		}
	};

	public static ObjectClickHandler handleKeldagrimTrapdoor = new ObjectClickHandler(new Object[] { 28094 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(2911, 10176, 0));
		}
	};

	public static ObjectClickHandler handleRiverLumSteppingStones = new ObjectClickHandler(new Object[] { 9315 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 31))
				return;
			AgilityShortcuts.walkLog(e.getPlayer(), e.getPlayer().transform(e.getObject().getRotation() == 1 ? -5 : 5, 0, 0), 4);
		}
	};

	public static ObjectClickHandler handleGrandExchangeShortcut = new ObjectClickHandler(new Object[] { 9311, 9312 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 21))
				return;
			WorldTasks.schedule(new WorldTask() {
				int ticks = 0;

				@Override
				public void run() {
					boolean withinGE = e.getObjectId() == 9312;
					WorldTile tile = withinGE ? new WorldTile(3139, 3516, 0) : new WorldTile(3143, 3514, 0);
					e.getPlayer().lock();
					ticks++;
					if (ticks == 1) {
						e.getPlayer().setNextAnimation(new Animation(2589));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject(), 1, withinGE ? Direction.WEST : Direction.EAST));
					} else if (ticks == 3) {
						e.getPlayer().setNextWorldTile(new WorldTile(3141, 3515, 0));
						e.getPlayer().setNextAnimation(new Animation(2590));
					} else if (ticks == 5) {
						e.getPlayer().setNextAnimation(new Animation(2591));
						e.getPlayer().setNextWorldTile(tile);
					} else if (ticks == 6) {
						e.getPlayer().setNextWorldTile(new WorldTile(tile.getX() + (withinGE ? -1 : 1), tile.getY(), tile.getPlane()));
						e.getPlayer().unlock();
						stop();
					}
				}
			}, 0, 0);
		}
	};
	public static ObjectClickHandler handleFenceShortcut = new ObjectClickHandler(new Object[] { 9300 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!e.isAtObject())
				return;
			switch (e.getObject().getRotation()) {
			case 0:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -1 : 1, 0, 0), 839);
				break;
			case 1:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -1 : 1, 0), 839);
				break;
			case 2:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -1 : 1, 0, 0), 839);
				break;
			case 3:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -1 : 1, 0), 839);
				break;
			}
		}
	};

	public static ObjectClickHandler handleStileShortcuts = new ObjectClickHandler(new Object[] { 45205, 34776, 48208 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!e.isAtObject())
				return;
			switch (e.getObject().getRotation()) {
			case 0:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -2 : 2, 0), 839);
				break;
			case 1:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -2 : 2, 0, 0), 839);
				break;
			case 2:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(0, e.getPlayer().getY() >= e.getObject().getY() ? -2 : 2, 0), 839);
				break;
			case 3:
				AgilityShortcuts.climbOver(e.getPlayer(), e.getPlayer().transform(e.getPlayer().getX() >= e.getObject().getX() ? -2 : 2, 0, 0), 839);
				break;
			}
		}
	};

	public static ObjectClickHandler handlePhoenixGangHideoutLadder = new ObjectClickHandler(new Object[] { 24363 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getObject().matches(new WorldTile(3244, 3383, 0)) && e.getOption().equalsIgnoreCase("climb-down"))
				e.getPlayer().ladder(new WorldTile(3245, 9783, 0));
		}
	};

	public static ObjectClickHandler handlePhoenixGangVarrockLadder = new ObjectClickHandler(new Object[] { 2405 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getObject().matches(new WorldTile(3244, 9783, 0)) && e.getOption().equalsIgnoreCase("climb-up"))
				e.getPlayer().ladder(new WorldTile(3243, 3383, 0));
		}
	};

	public static NPCClickHandler handleGuildMaster = new NPCClickHandler(198) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getPlayer().getQuestManager().getQuestPoints() <= 31) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.FRUSTRATED, "You really shouldn't be in here, but I will let that slide...");
						create();
					}
				});
				return;
			}
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Greetings!");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("What is this place?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "What is this place?")
									.addNPC(198, HeadE.HAPPY_TALKING, "This is the Champions' Guild. Only adventurers who have proved themselves worthy " +
											"by gaining influence from quests are allowed in here."));
							if(!e.getPlayer().getQuestManager().isComplete(Quest.DRAGON_SLAYER))
								option("About Dragon Slayer", new Dialogue()
										.addNext(()->{e.getPlayer().startConversation(new GuildMasterDragonSlayerD(e.getPlayer()).getStart());}));
						}
					});
					create();
				}
			});

		}
	};

	public static ObjectClickHandler handleChampionsGuildFrontDoor = new ObjectClickHandler(new Object[] { 1805 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if (p.getY() >= obj.getY()) {
				if (p.getQuestManager().getQuestPoints() <= 31) {
					e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
						{
							addSimple("You need 32 quest points to enter the champions guild.");
							create();
						}
					});
					return;
				}
				Doors.handleDoor(p, obj);
				p.getDialogueManager().execute(new SimpleNPCMessage(), 198, "Greetings bold adventurer. Welcome to the guild of", "Champions.");
			} else
				Doors.handleDoor(p, obj);
		}
	};


}
