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
package com.rs.game.content.world.areas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.InventoryDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.achievements.AchievementDef;
import com.rs.game.content.achievements.AchievementDef.Area;
import com.rs.game.content.achievements.AchievementDef.Difficulty;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.combat.XPType;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.handlers.dragonslayer.GuildMasterDragonSlayerD;
import com.rs.game.content.quests.handlers.heroesquest.dialogues.KatrineHeroesQuestD;
import com.rs.game.content.quests.handlers.heroesquest.dialogues.StravenHeroesQuestD;
import com.rs.game.content.quests.handlers.knightssword.KnightsSword;
import com.rs.game.content.quests.handlers.knightssword.ReldoKnightsSwordD;
import com.rs.game.content.quests.handlers.scorpioncatcher.ScorpionCatcher;
import com.rs.game.content.quests.handlers.shieldofarrav.BaraekShieldOfArravD;
import com.rs.game.content.quests.handlers.shieldofarrav.CharlieTheTrampArravD;
import com.rs.game.content.quests.handlers.shieldofarrav.KatrineShieldOfArravD;
import com.rs.game.content.quests.handlers.shieldofarrav.KingRoaldShieldOfArravD;
import com.rs.game.content.quests.handlers.shieldofarrav.MuseumCuratorArravD;
import com.rs.game.content.quests.handlers.shieldofarrav.ReldoShieldOfArravD;
import com.rs.game.content.quests.handlers.shieldofarrav.ShieldOfArrav;
import com.rs.game.content.quests.handlers.shieldofarrav.StravenShieldOfArravD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Varrock {
	public static PlayerStepHandler musicBlueMoonInn = new PlayerStepHandler(WorldTile.of(3215, 3395, 0), WorldTile.of(3216, 3395, 0), WorldTile.of(3233, 3396, 0)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getTile().getX() <= 3216 && e.getStep().getDir() == Direction.WEST)
				if(e.getPlayer().getMusicsManager().isPlaying(716))
					e.getPlayer().getMusicsManager().nextAmbientSong();
			if(e.getTile().getX() == 3216 && e.getStep().getDir() == Direction.EAST)
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(716, true);

			if(e.getTile().getX() == 3233 && e.getStep().getDir() == Direction.WEST)
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(716, true);
			if(e.getTile().getX() == 3233 && e.getStep().getDir() == Direction.EAST) {
				if(e.getPlayer().getMusicsManager().isPlaying(716))
					e.getPlayer().getMusicsManager().nextAmbientSong();
			}
		}
	};

	public static PlayerStepHandler musicDancingDonkeyInn = new PlayerStepHandler(WorldTile.of(3274, 3389, 0), WorldTile.of(3275, 3389, 0)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getTile().getX() <= 3275 && e.getStep().getDir() == Direction.EAST)
				if(e.getPlayer().getMusicsManager().isPlaying(721))
					e.getPlayer().getMusicsManager().nextAmbientSong();
			if(e.getTile().getX() == 3274 && e.getStep().getDir() == Direction.WEST)
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(721, true);

		}
	};

	public static PlayerStepHandler musicBoarsHeadInn = new PlayerStepHandler(WorldTile.of(3281, 3506, 0), WorldTile.of(3280, 3506, 0)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getStep().getDir() == Direction.NORTH)
				if(e.getPlayer().getMusicsManager().isPlaying(720))
					e.getPlayer().getMusicsManager().nextAmbientSong();
			if(e.getStep().getDir() == Direction.SOUTH)
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(720, true);
		}
	};


	public static NPCClickHandler handleBlueMoonBartender = new NPCClickHandler(new Object[] { 733 }) {
		@Override
		public void handle(NPCClickEvent e) {
			Player p = e.getPlayer();

			p.setRouteEvent(new RouteEvent(WorldTile.of(3224, 3397, 0), () -> {
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



	public static NPCClickHandler handlePeskaBarbarianVillage = new NPCClickHandler(new Object[] { 538 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int NPC= e.getNPCId();
			if(e.getOption().equalsIgnoreCase("talk-to")) {
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(NPC, HeadE.CALM_TALK, "Are you interested in buying or selling a helmet?")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("I could be, yes.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I could be, yes.")
										.addNPC(NPC, HeadE.CALM_TALK, "Let me show you my inventory then...")
										.addNext(()->{ShopsHandler.openShop(e.getPlayer(), "helmet_shop");})
								);
								option("No, I'll pass on that.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "No, I'll pass on that.")
										.addNPC(NPC, HeadE.CALM_TALK, "Well, alright.")
								);
								if(e.getPlayer().getQuestManager().getStage(Quest.SCORPION_CATCHER) == ScorpionCatcher.LOOK_FOR_SCORPIONS
									&& e.getPlayer().getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp2LocKnown"))
									option("I've heard you have a small scorpion in your possession.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I've heard you have a small scorpion in your possession.")
											.addNPC(NPC, HeadE.CALM_TALK, "Now how could you know about that, I wonder? Mind you, I don't have it anymore.")
											.addNPC(NPC, HeadE.CALM_TALK, "I gave it as a present to my brother Ivor when I visited our outpost northwest of Camelot.")
											.addNPC(NPC, HeadE.CALM_TALK, "Well, actually I hid it in his bed so it would nip him. It was a bit of a surprise gift.")
											.addPlayer(HeadE.HAPPY_TALKING, "Okay ill look at the barbarian outpost, perhaps you mean the barbarian agility area?")
											.addNPC(NPC, HeadE.SECRETIVE, "Perhaps...")
									);
							}
						})
				);


			}
			if(e.getOption().equalsIgnoreCase("trade"))
				ShopsHandler.openShop(e.getPlayer(), "helmet_shop");
		}
	};

	public static ObjectClickHandler varrockCenterStairs = new ObjectClickHandler(new Object[] { 24367 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, WorldTile.of(e.getObject().getX(), 3476, 1), 1, 2);
		}
	};

	public static ObjectClickHandler blueMoonStairs = new ObjectClickHandler(new Object[] { 37117 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(-1, WorldTile.of(e.getObject().getX()-2, e.getPlayer().getY(), 0), 1, 2);
		}
	};

	public static ObjectClickHandler handleVariousStaircases = new ObjectClickHandler(new Object[] { 24356 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(obj.getRotation() == 0)
				p.useStairs(-1, WorldTile.of(p.getX(), obj.getY()+3, p.getPlane() + 1), 0, 1);
			else if (obj.getRotation() == 1)
				p.useStairs(-1, WorldTile.of(p.getX()+4, p.getY(), p.getPlane() + 1), 0, 1);
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

	public static NPCClickHandler handleReldo = new NPCClickHandler(new Object[] { 647 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							if(!e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
								option("About Shield Of Arrav...", new ReldoShieldOfArravD(player).getStart());
							if(e.getPlayer().getQuestManager().getStage(Quest.KNIGHTS_SWORD) >= KnightsSword.TALK_TO_RELDO
									&& !e.getPlayer().isQuestComplete(Quest.KNIGHTS_SWORD))
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
				if(obj.getTile().matches(WorldTile.of(3237, 3458, 0)))
					p.useStairs(833, WorldTile.of(3237, 9858, 0), 1, 2);
		}
	};
	
	public static ButtonClickHandler furclothingShop = new ButtonClickHandler(477) {
		enum FurItem {
			POLAR_TOP(10065, 20, 2, 10117),
			POLAR_BOT(10067, 20, 2, 10117),
			WOODS_TOP(10053, 20, 2, 10121),
			WOODS_BOT(10055, 20, 2, 10121),
			FELDI_TOP(10057, 20, 2, 10119),
			FELDI_BOT(10059, 20, 2, 10119),
			DESER_TOP(10061, 20, 2, 10123),
			DESER_BOT(10063, 20, 2, 10123),
			LARUP_HAT(10045, 500, 1, 10095),
			LARUP_TOP(10043, 100, 1, 10093, 10095),
			LARUP_BOT(10041, 100, 1, 10093, 10095),
			GRAAH_HAT(10051, 750, 1, 10099),
			GRAAH_TOP(10049, 150, 1, 10097, 10099),
			GRAAH_BOT(10047, 150, 1, 10097, 10099),
			KYATT_HAT(10039, 1000, 1, 10103),
			KYATT_TOP(10037, 200, 1, 10101, 10103),
			KYATT_BOT(10035, 200, 1, 10101, 10103),
			GLOVES_SI(10075, 600, 2, 10115),
			SPOT_CAPE(10069, 400, 2, 10125),
			SPOTICAPE(10071, 800, 2, 10127);
			
			private static Map<Integer, FurItem> BY_ITEMID = new HashMap<>();
			
			static {
				for (FurItem item : FurItem.values())
					BY_ITEMID.put(item.id, item);
			}
			
			private final int id;
			private final int gpCost;
			private final int furCost;
			private final int[] furIds;
			
			FurItem(int itemId, int gpCost, int furCost, int... furIds) {
				this.id = itemId;
				this.gpCost = gpCost;
				this.furCost = furCost;
				this.furIds = furIds;
			}
			
			private static FurItem forId(int item) {
				return BY_ITEMID.get(item);
			}
		}
		
		@Override
		public void handle(ButtonClickEvent e) {
			FurItem item = FurItem.forId(e.getSlotId2());
			if (item == null)
				return;
			String name = ItemDefinitions.getDefs(e.getSlotId2()).name;
			switch(e.getPacket()) {
			case IF_OP1 -> e.getPlayer().sendMessage(name + " costs " + Utils.formatNumber(item.gpCost) + "gp and " + item.furCost + " " + ItemDefinitions.getDefs(item.furIds[0]).name.toLowerCase() + ".");
			case IF_OP2 -> buy(e.getPlayer(), item, 1);
			case IF_OP3 -> buy(e.getPlayer(), item, 5);
			case IF_OP4 -> buy(e.getPlayer(), item, 10);
			default -> {}
			}
		}

		private void buy(Player player, FurItem item, int amount) {
			player.sendOptionDialogue("Are you sure you'd like to buy " + amount + " " + ItemDefinitions.getDefs(item.id).name + "?", ops -> {
				ops.add("Yes, I am sure ("+Utils.formatNumber(item.gpCost*amount) + " coins)", () -> {
					if (item.furCost*amount > player.getInventory().getTotalNumberOf(item.furIds)) {
						player.sendMessage("You don't have enough furs to exchange for that.");
						return;
					}
					int paid = 0;
					for (int i = 0;i < amount*2;i++) {
						for (int furId : item.furIds) {
							if (player.getInventory().containsItem(furId)) {
								player.getInventory().deleteItem(furId, 1);
								if (++paid == (item.furCost*amount))
									break;
							}
						}
					}
					player.getInventory().addItemDrop(item.id, amount);
				});
				ops.add("No thanks.");
			});
		}
	};
	
	public static NPCClickHandler fancyShopOwner = new NPCClickHandler(new Object[] { 554 }) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Talk-to" -> e.getPlayer().sendMessage("Lmao you think I'm gonna write dialogue?");
			case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "fancy_clothes_store");
			case "Fur-shop" -> {
				e.getPlayer().getPackets().sendItems(482, Arrays.stream(InventoryDefinitions.getContainer(482).ids).mapToObj(id -> new Item(id, 1)).toArray(Item[]::new));
				e.getPlayer().getInterfaceManager().sendInterface(477);
				e.getPlayer().getPackets().setIFRightClickOps(477, 26, 0, 20, 0, 1, 2, 3);
			}
			}
		}
	};

	public static NPCClickHandler handleBaraek = new NPCClickHandler(new Object[] { 547 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
				e.getPlayer().sendMessage("Nothing interesting happens");
			else
				e.getPlayer().startConversation(new BaraekShieldOfArravD(e.getPlayer()).getStart());
		}
	};

	public static NPCClickHandler handleCharlie = new NPCClickHandler(new Object[] { 641 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
				e.getPlayer().sendMessage("Nothing interesting happens");
			else
				e.getPlayer().startConversation(new CharlieTheTrampArravD(e.getPlayer()).getStart());
		}
	};

	public static NPCClickHandler handleKatrine = new NPCClickHandler(new Object[] { 642 }) {
		@Override
		public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
			if(p.isQuestComplete(Quest.SHIELD_OF_ARRAV )
                    && ShieldOfArrav.isBlackArmGang(p) && p.getQuestManager().getStage(Quest.HEROES_QUEST) > 0)
				p.startConversation(new KatrineHeroesQuestD(p).getStart());
			else
				p.startConversation(new KatrineShieldOfArravD(p).getStart());
		}
	};

	public static NPCClickHandler handleStraven = new NPCClickHandler(new Object[] { 644 }) {
		@Override
		public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
			if(p.isQuestComplete(Quest.SHIELD_OF_ARRAV )
                    && ShieldOfArrav.isPhoenixGang(p) && p.getQuestManager().getStage(Quest.HEROES_QUEST) > 0) //started
                p.startConversation(new StravenHeroesQuestD(p).getStart());
            else
				p.startConversation(new StravenShieldOfArravD(p).getStart());
		}
	};

	public static NPCClickHandler handleJohnnyTheBeard = new NPCClickHandler(new Object[] { 645 }, new String[] { "Talk-to" }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().sendMessage("Johnny the beard is not interested in talking.");
		}
	};

	public static NPCClickHandler handleKingRoald = new NPCClickHandler(new Object[] { 648 }, new String[] { "Talk-to" }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.CHEERFUL, "Hello.");
					if (!e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
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

	public static NPCClickHandler handleMuseumCurator = new NPCClickHandler(new Object[] { 646 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addOptions("What would you like to say?", new Options() {
							@Override
							public void create() {
								option("About Shield Of Arrav...", new MuseumCuratorArravD(player).getStart());
								option("Farewell.", new Dialogue());
							}
						});
						create();
					}
				});
		}
	};

	public static NPCClickHandler handleHistorianMinas = new NPCClickHandler(new Object[] { 5931 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.CHEERFUL, "Hello.");
						addOptions("What would you like to say?", new Options() {
							@Override
							public void create() {
								if (e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV) && !((boolean)e.getPlayer().get("claimedArravLamp")))
									option("About Shield Of Arrav...", new Dialogue()
											.addNPC(5931, HeadE.HAPPY_TALKING, "Thank you for returning the shield")
											.addSimple("A lamp is placed in your hand")
											.addNext(() -> {
												e.getPlayer().getInventory().addItem(4447, 1);
												e.getPlayer().save("claimedArravLamp", true);
											}));
								else if(e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
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

	public static NPCClickHandler handleRatBurgiss = new NPCClickHandler(new Object[] { 5833 }) {
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

	public static NPCClickHandler handleNaff = new NPCClickHandler(new Object[] { 359 }) {
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
				e.getPlayer().sendOptionDialogue("Buy " + amount + " battlestaves for " + Utils.formatNumber(cost) + " coins?", ops -> {
					ops.add("Yes", () -> {
						if (!e.getPlayer().getInventory().containsItem(995, cost)) {
							e.getPlayer().sendMessage("You don't have enough money for that.");
							return;
						}
						e.getPlayer().getInventory().deleteItem(995, cost);
						e.getPlayer().getInventory().addItemDrop(1392, finalAmount);
						e.getPlayer().setDailyI("naffStavesBought", e.getPlayer().getDailyI("naffStavesBought") + finalAmount);
					});
					ops.add("Not thanks.");
				});
			});
		}
	};

	public static NPCClickHandler handleDealga = new NPCClickHandler(new Object[] { 11475 }) {
		@Override
		public void handle(NPCClickEvent e) {
			ShopsHandler.openShop(e.getPlayer(), "dealgas_scimitar_emporium");
		}
	};

	public static ObjectClickHandler handleKeldagrimTrapdoor = new ObjectClickHandler(new Object[] { 28094 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(WorldTile.of(2911, 10176, 0));
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
					WorldTile tile = withinGE ? WorldTile.of(3139, 3516, 0) : WorldTile.of(3143, 3514, 0);
					e.getPlayer().lock();
					ticks++;
					if (ticks == 1) {
						e.getPlayer().setNextAnimation(new Animation(2589));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject().getTile(), 1, withinGE ? Direction.WEST : Direction.EAST));
					} else if (ticks == 3) {
						e.getPlayer().setNextWorldTile(WorldTile.of(3141, 3515, 0));
						e.getPlayer().setNextAnimation(new Animation(2590));
					} else if (ticks == 5) {
						e.getPlayer().setNextAnimation(new Animation(2591));
						e.getPlayer().setNextWorldTile(tile);
					} else if (ticks == 6) {
						e.getPlayer().setNextWorldTile(WorldTile.of(tile.getX() + (withinGE ? -1 : 1), tile.getY(), tile.getPlane()));
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
			if(e.getObject().getTile().matches(WorldTile.of(3244, 3383, 0)) && e.getOption().equalsIgnoreCase("climb-down"))
				e.getPlayer().ladder(WorldTile.of(3245, 9783, 0));
		}
	};

	public static ObjectClickHandler handlePhoenixGangVarrockLadder = new ObjectClickHandler(new Object[] { 2405 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getObject().getTile().matches(WorldTile.of(3244, 9783, 0)) && e.getOption().equalsIgnoreCase("climb-up"))
				e.getPlayer().ladder(WorldTile.of(3243, 3383, 0));
		}
	};

	public static NPCClickHandler handleGuildMaster = new NPCClickHandler(new Object[] { 198 }) {
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
							if(!e.getPlayer().isQuestComplete(Quest.DRAGON_SLAYER))
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
				p.npcDialogue(198, HeadE.CHEERFUL, "Greetings bold adventurer. Welcome to the guild of Champions.");
			} else
				Doors.handleDoor(p, obj);
		}
	};

	public static NPCClickHandler handleValaineChampsGuild = new NPCClickHandler(new Object[] { 536 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int NPC = e.getNPCId();
			if(e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(NPC, HeadE.CALM_TALK, "Hello there. Want to have a look at what we're selling today?")
						.addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("Yes, please.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Yes, please.")
									.addNext(()->{ShopsHandler.openShop(e.getPlayer(), "valaines_shop_of_champions");})
							);
							option("How should I use your shop?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "How should I use your shop?")
									.addNPC(NPC, HeadE.CALM_TALK, "I'm glad you ask! You can buy as many of the items stocked as you wish. You can also sell most items to the shop.")
									.addNext(()->{ShopsHandler.openShop(e.getPlayer(), "valaines_shop_of_champions");})
							);
							option("No, thank you.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "No, thank you.")
									.addNPC(NPC, HeadE.CALM_TALK, "Well, alright.")
							);
						}
					})
				);
			if(e.getOption().equalsIgnoreCase("trade"))
				ShopsHandler.openShop(e.getPlayer(), "valaines_shop_of_champions");
		}
	};


}
