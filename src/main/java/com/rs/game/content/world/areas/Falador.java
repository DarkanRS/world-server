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

import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.handlers.knightssword.SquireKnightsSwordD;
import com.rs.game.content.quests.handlers.piratestreasure.RedbeardFrankPiratesTreasureD;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.AgilityShortcuts;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Falador {

	public static PlayerStepHandler musicArtisansWorkshop = new PlayerStepHandler(new WorldTile(3035, 3339, 0), new WorldTile(3035, 3338, 0),
			new WorldTile(3034, 3339, 0), new WorldTile(3034, 3338, 0)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getTile().getX() == 3035 && e.getStep().getDir() == Direction.EAST) {
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(582, true);
				return;
			}
			if(e.getTile().getX() == 3034 && e.getPlayer().getMusicsManager().isPlaying(582))
				e.getPlayer().getMusicsManager().nextAmbientSong();
		}
	};

	public static PlayerStepHandler musicRisingSunInn = new PlayerStepHandler(new WorldTile(2956, 3378, 0), new WorldTile(2956, 3379, 0),
			new WorldTile(2961, 3372, 0), new WorldTile(2962, 3372, 0)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getTile().getY() == 3378 && e.getStep().getDir() == Direction.SOUTH) {
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(718, true);
				return;
			}
			if(e.getTile().getX() == 2961 && e.getStep().getDir() == Direction.WEST) {
				e.getPlayer().getMusicsManager().playSpecificAmbientSong(718, true);
				return;
			}
			if((e.getTile().getY() == 3379 || e.getTile().getX() == 2961) && e.getPlayer().getMusicsManager().isPlaying(718))
				e.getPlayer().getMusicsManager().nextAmbientSong();
		}
	};

	public static NPCClickHandler handleRedBeardFrank = new NPCClickHandler(new Object[] { 375 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Arr, Matey!");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							if(!player.isQuestComplete(Quest.PIRATES_TREASURE))
								option("About Pirate's Treasure", new Dialogue()
										.addNext(()->{
											e.getPlayer().startConversation(new RedbeardFrankPiratesTreasureD(e.getPlayer()).getStart());
										}));

							option("About the Achievement System...",
									new AchievementSystemDialogue(player, e.getNPCId(), SetReward.FALADOR_SHIELD)
									.getStart());
						}
					});
					create();
				}
			});
		}
	};

	public static NPCClickHandler handleChemist = new NPCClickHandler(new Object[] { 367 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("About the Achievement System...",
									new AchievementSystemDialogue(player, e.getNPCId(), SetReward.FALADOR_SHIELD)
									.getStart());
						}
					});
				}
			});
		}
	};

	public static NPCClickHandler handleSquireAsrol = new NPCClickHandler(new Object[] { 606 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							if(!player.isQuestComplete(Quest.KNIGHTS_SWORD))
								option("About Knight's Sword.", new Dialogue()
										.addNext(()->{e.getPlayer().startConversation(new SquireKnightsSwordD(e.getPlayer()).getStart());}));
							option("About the Achievement System...",
									new AchievementSystemDialogue(player, e.getNPCId(), SetReward.FALADOR_SHIELD)
									.getStart());
						}
					});
					create();
				}
			});
		}
	};

	public static NPCClickHandler handleSirTiffy = new NPCClickHandler(false, new Object[] { 2290 }) {
		@Override
		public void handle(NPCClickEvent e) {
			ShopsHandler.openShop(e.getPlayer(), "initiate_rank_armory");
		}
	};

	public static NPCClickHandler handleQuarterMaster = new NPCClickHandler(new Object[] { 1208 }) {
		@Override
		public void handle(NPCClickEvent e) {
			ShopsHandler.openShop(e.getPlayer(), "quartermasters_stores");
		}
	};

	public static NPCClickHandler handleWysonTheGardener = new NPCClickHandler(new Object[] { 36 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{

					addNPC(e.getNPCId(), HeadE.CALM,
							"I'm the head gardener around here. If you're looking for woad leaves, or if you need help with owt, I'm yer man.");
					addOptions(new Options() {
						@Override
						public void create() {
							option("Ok, I will trade my mole parts.",
									new Dialogue().addPlayer(HeadE.CALM_TALK, "ok, I will trade my mole parts.", () -> {
										int numNests = player.getInventory().getNumberOf(7416) + player.getInventory().getNumberOf(7418);
										player.getInventory().deleteItem(7416, Integer.MAX_VALUE);
										player.getInventory().deleteItem(7418, Integer.MAX_VALUE);
										for (int i = 0;i < numNests;i++)
											player.getInventory().addItem(Utils.random(0, 100) <= 7 ? 5075 : 7413, 1);
									}));
							option("Yes please, I need woad leaves.",
									new Dialogue()
									.addNPC(e.getNPCId(), HeadE.AMAZED, "How much are you willing to pay?")
									.addOptions(new Options() {
										@Override
										public void create() {
											option("How about 5 coins?", new Dialogue()
													.addPlayer(HeadE.CALM, "How about 5 coins?")
													.addNPC(e.getNPCId(), HeadE.FRUSTRATED,
															"No no, that's far too little. Woad leaves are hard to get. I used to have plenty but someone kept"
																	+ " stealing them off me."));
											option("How about 10 coins?", new Dialogue()
													.addPlayer(HeadE.CALM, "How about 10 coins?")
													.addNPC(e.getNPCId(), HeadE.FRUSTRATED,
															"No no, that's far too little. Woad leaves are hard to get. I used to have plenty but someone kept "
																	+ "stealing them off me."));
											option("How about 15 coins?",
													new Dialogue().addPlayer(HeadE.CALM, "How about 15 coins?")
													.addNPC(e.getNPCId(), HeadE.FRUSTRATED,
															" Mmmm... okay, that sounds fair.", () -> {
																if (player.getInventory()
																		.containsItem(995, 15)) {
																	player.getInventory()
																	.deleteItem(995, 15);
																	player.getInventory().addItem(1793,
																			1);
																} else
																	player.getPackets().sendGameMessage(
																			"You need 15 coins for this transaction");
															}));
											option("How about 20 coins?",
													new Dialogue().addPlayer(HeadE.CALM, "How about 20 coins?")
													.addNPC(e.getNPCId(), HeadE.FRUSTRATED,
															"Okay, that's more than fair", () -> {
																if (player.getInventory()
																		.containsItem(995, 20)) {
																	player.getInventory()
																	.deleteItem(995, 20);
																	player.getInventory().addItem(1793,
																			2);
																} else
																	player.getPackets().sendGameMessage(
																			"You need 20 coins for this transaction");
															}));
										}
									}));
							option("How about ME helping YOU instead?", new Dialogue()
									.addPlayer(HeadE.CALM_TALK, "How about ME helping YOU instead?")
									.addNPC(e.getNPCId(), HeadE.HAPPY_TALKING,
											"That's a nice thing to say. I do need a hand, now you mention it. You see, there's some stupid mole "
													+ "digging up my lovely garden.")
									.addPlayer(HeadE.CALM_TALK, "A mole? Surely you've dealt with moles in the past?")
									.addNPC(e.getNPCId(), HeadE.AMAZED,
											"Ah, well this is no ordinary mole! He's a big'un for sure. Ya see... I'm always relied upon to make the"
													+ " most of this 'ere garden - the faster and bigger I can grow plants the better!")
									.addNPC(e.getNPCId(), HeadE.WORRIED,
											"In my quest for perfection I looked into 'Malignius- Mortifer's-Super-Ultra-Flora-Growth-Potion'. "
													+ "It worked well on my plants, no doubt about it! But it had the same effect on a nearby mole. Ya can imagine the")
									.addNPC(e.getNPCId(), HeadE.FRUSTRATED,
											"havoc he causes to my patches of sunflowers! Why, if any of the other gardeners knew about this mole,"
													+ " I'd be looking for a new job in no time!")
									.addPlayer(HeadE.CALM_TALK, "I see. What do you need me to do?")
									.addNPC(e.getNPCId(), HeadE.HAPPY_TALKING,
											"If ya are willing maybe yer wouldn't mind killing it for me? Take a spade and use it to shake up them "
													+ "mole hills. Be careful though, he really is big!")
									.addPlayer(HeadE.CALM_TALK, "Is there anything in this for me?")
									.addNPC(e.getNPCId(), HeadE.HAPPY_TALKING,
											"Well, if yer gets any mole skin or mole claws off 'un, I'd trade 'em for bird nests if ye brings 'em here to me.")
									.addPlayer(HeadE.CALM_TALK, "Right, I'll bear it in mind."));
							option("Sorry, but I'm not interested.",
									new Dialogue().addPlayer(HeadE.CALM_TALK, "Sorry, but I'm not interested.")
									.addNPC(e.getNPCId(), HeadE.CALM, "Fair enough."));
						}
					});
					create();
				}
			});
		}
	};

	public static ObjectClickHandler handleUnderwallTunnelShortcut = new ObjectClickHandler(
			new Object[] { 9309, 9310 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 26))
				return;
			AgilityShortcuts.crawlUnder(e.getPlayer(), e.getPlayer().transform(0, e.getObjectId() == 9310 ? -4 : 4, 0));
		}
	};

	public static ObjectClickHandler handleFistOfGuthixEntrance = new ObjectClickHandler(new Object[] { 20608, 30203 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if(e.getObjectId() == 20608)
				e.getPlayer().useStairs(-1, new WorldTile(1677, 5598, 0), 1, 1);
			if(e.getObjectId() == 30203)
				e.getPlayer().useStairs(-1, new WorldTile(2969, 9672, 0), 1, 1);
		}
	};

	public static ObjectClickHandler handleCrumblingWallShortcut = new ObjectClickHandler(new Object[] { 11844 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 5))
				return;
			AgilityShortcuts.climbOver(e.getPlayer(),
					e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 2 : -2, 0, 0));
		}
	};

	public static ObjectClickHandler handleCabbagePatchStile = new ObjectClickHandler(new Object[] { 7527 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			WorldObject obj = e.getObject();
			if(!obj.matches(new WorldTile(3063, 3282, 0)))
				return;
			if(p.getY() > obj.getY())
				AgilityShortcuts.climbOver(p, new WorldTile(obj.getX(), obj.getY()-1, obj.getPlane()));
			if(p.getY() < obj.getY())
				AgilityShortcuts.climbOver(p, new WorldTile(obj.getX(), obj.getY()+1, obj.getPlane()));
		}
	};
}
