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
package com.rs.game.content.world.areas.karamja;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.dragonslayer.DragonSlayer;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Karamja  {

	public static NPCClickHandler handleGabooty = new NPCClickHandler(new Object[] { 2520 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.JUNGLE_POTION))
			return;
		switch (e.getOption())
		{
			case "Trade-Co-op" -> ShopsHandler.openShop(e.getPlayer(), "gabootys_tai_bwo_wannai_cooperative");
			case "Trade-Drinks" -> ShopsHandler.openShop(e.getPlayer(), "gabootys_tai_bwo_wannai_drinky_store");
		}
	});

	public static NPCClickHandler handlePirateJackieFruit = new NPCClickHandler(new Object[] { 1055 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
					}
				});
                create();
			}
		});
	});

	public static NPCClickHandler handleSaniBoch = new NPCClickHandler(new Object[] { 1595 }, e -> {
		if(e.getOption().equalsIgnoreCase("Talk-to")) {
			int NPC = e.getNPCId();
			if(e.getPlayer().getTempAttribs().getB("paid_brimhaven_entrance_fee")) {
				e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.HAPPY_TALKING, "Thank you for your payment, bwana."));
				return;
			}
			e.getPlayer().startConversation(new Dialogue()
					.addNPC(NPC, HeadE.HAPPY_TALKING, "Good day to you bwana")
					.addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							if(e.getPlayer().getInventory().hasCoins(875)) {
								option("Can I go through that door please?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can I go through that door please?")
									.addNPC(NPC, HeadE.HAPPY_TALKING, "Most certainly, but I must charge you the sum of 875 coins first")
									.addOptions("Choose an option:", new Options() {
										@Override
										public void create() {
											option("Ok, here's 875 coins", new Dialogue()
													.addPlayer(HeadE.CALM_TALK, "Ok, here's 875 coins")
													.addItem(6964, "You give SaniBoch 875 coins.", ()->{
														e.getPlayer().getInventory().removeCoins(875);
														e.getPlayer().getTempAttribs().setB("paid_brimhaven_entrance_fee", true);
													})
													.addNPC(NPC, HeadE.HAPPY_TALKING, "Many thanks. You may now pass the door. May your death be a glorious one!")
											);
											option("Never mind.", new Dialogue()
													.addPlayer(HeadE.HAPPY_TALKING, "Never mind.")
											);
											option("Why is it worth the entry cost?", new Dialogue()
													.addPlayer(HeadE.HAPPY_TALKING, "Why is it worth the entry cost?")
													.addNPC(NPC, HeadE.CALM_TALK, "It leads to a huge fearsome dungeon, populated by giants and strange dogs. Adventurers come from all around to explore its depths.")
													.addNPC(NPC, HeadE.CALM_TALK, "I know not what lies deeper in myself, for my skills in agility and woodcutting are inadequate, but I hear tell of even greater dangers deeper in.")
													.addPlayer(HeadE.HAPPY_TALKING, "That's nice.")


											);
										}
									})
								);
							} else {
								option("Can I go through that door please?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Can I go through that door please?")
										.addNPC(NPC, HeadE.HAPPY_TALKING, "Most certainly, but I must charge you the sum of 875 coins first")
										.addPlayer(HeadE.SAD, "I don't have that...")
										.addNPC(NPC, HeadE.FRUSTRATED, "Well this is a dungeon for the more wealthy discerning adventurer, be gone with you riff raff.")
										.addPlayer(HeadE.HAPPY_TALKING, "But you don't even have clothes, how can you seriously call anyone riff raff.")
										.addNPC(NPC, HeadE.FRUSTRATED, "Hummph.")
								);
							}
							option("Where does this strange entrance lead?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Where does this strange entrance lead?")
									.addNPC(NPC, HeadE.CALM_TALK, "To a huge fearsome dungeon, populated by giants and strange dogs. Adventurers come from all around to explore its depths.")
									.addNPC(NPC, HeadE.CALM_TALK, "I know not what lies deeper in myself, for my skills in agility and woodcutting are inadequate.")
									.addPlayer(HeadE.HAPPY_TALKING, "That's nice.")
							);
							option("Good day to you too", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Good day to you too")
									.addNPC(NPC, HeadE.CALM_TALK, "...")
							);
							option("I'm impressed, that tree is growing on that shed.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I'm impressed, that tree is growing on that shed.")
									.addNPC(NPC, HeadE.CALM_TALK, "My employer tells me it is an uncommon sort of tree called the Fyburglars tree.")
									.addPlayer(HeadE.HAPPY_TALKING, "That's nice.")
							);
						}
					})
			);
		}
		if(e.getOption().equalsIgnoreCase("pay")) {
			if(e.getPlayer().getTempAttribs().getB("paid_brimhaven_entrance_fee")) {
				e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "You already paid, bwana."));
				return;
			}
			e.getPlayer().startConversation(new Dialogue()
					.addItem(6964, "You give SaniBoch 875 coins.", ()->{
						e.getPlayer().getInventory().removeCoins(875);
						e.getPlayer().getTempAttribs().setB("paid_brimhaven_entrance_fee", true);
					})
					.addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Many thanks. You may now pass the door. May your death be a glorious one!")
			);
		}
	});

    public static NPCClickHandler handleRumDealer = new NPCClickHandler(new Object[] { 568 }, e -> {
    	 if(e.getOption().equalsIgnoreCase("talk-to"))
             e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                 {
                     addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, welcome to my store!");
                     addNext(()->{ShopsHandler.openShop(e.getPlayer(), "karamja_wines_spirits_and_beers");});
                     create();
                 }
             });
         if(e.getOption().equalsIgnoreCase("trade"))
             ShopsHandler.openShop(e.getPlayer(), "karamja_wines_spirits_and_beers");
    });

	public static NPCClickHandler handleKalebParamaya = new NPCClickHandler(new Object[] { 512 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
					}
				});
                create();
			}
		});
	});

	public static NPCClickHandler handleJungleForesters = new NPCClickHandler(new Object[] { 401, 402 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.KARAMJA_GLOVES).getStart());
					}
				});
                create();
			}
		});
	});

	public static ObjectClickHandler handleBrimhavenDungeonEntrance = new ObjectClickHandler(new Object[] { 5083 }, e -> {
		if(e.getPlayer().getTempAttribs().getB("paid_brimhaven_entrance_fee")) {//12 hours
			e.getPlayer().setNextTile(Tile.of(2713, 9564, 0));
			return;
		}
		e.getPlayer().startConversation(new Dialogue().addNPC(1595, HeadE.FRUSTRATED, "You can't go in there without paying!"));
	});

	public static ObjectClickHandler handleBoatLadder = new ObjectClickHandler(new Object[] { 273 }, new Tile[] { Tile.of(2847, 3235, 1) }, e -> {
		e.getPlayer().useStairs(e.getPlayer().transform(0, 0, -1));
	});

	public static ObjectClickHandler handleBrimhavenDungeonExit = new ObjectClickHandler(new Object[] { 5084 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2745, 3152, 0));
	});

	public static ObjectClickHandler handleJogreLogWalk = new ObjectClickHandler(new Object[] { 2332 }, e -> {
		if (e.getPlayer().getX() > 2908)
			Agility.walkToAgility(e.getPlayer(), 155, Direction.WEST, 4, 4);
		else
			Agility.walkToAgility(e.getPlayer(), 155, Direction.EAST, 4, 4);
	});

	public static ObjectClickHandler handleMossGiantRopeSwings = new ObjectClickHandler(new Object[] { 2322, 2323 }, e -> {
		final Tile toTile = e.getObjectId() == 2322 ? Tile.of(2704, 3209, 0) : Tile.of(2709, 3205, 0);
		final Tile fromTile = e.getObjectId() == 2323 ? Tile.of(2704, 3209, 0) : Tile.of(2709, 3205, 0);
		if (!Agility.hasLevel(e.getPlayer(), 10))
			return;
		if (e.getObjectId() == 2322 ? e.getPlayer().getX() == 2704 : e.getPlayer().getX() == 2709) {
			e.getPlayer().sendMessage("You can't reach that.", true);
			return;
		}
		Agility.swingOnRopeSwing(e.getPlayer(), fromTile, toTile, e.getObject(), 0.1);
	});

	public static ObjectClickHandler handleJogreWaterfallSteppingStones = new ObjectClickHandler(new Object[] { 2333, 2334, 2335 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 30))
			return;
		e.getPlayer().forceMove(e.getObject().getTile(), 741, 0, 30);
	});

	public static ObjectClickHandler handleRareTreeDoors = new ObjectClickHandler(new Object[] { 9038, 9039 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP2) {
			if (e.getPlayer().getX() >= e.getObject().getX())
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
			else if (e.getPlayer().getInventory().containsItem(6306, 100)) {
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
				e.getPlayer().getInventory().deleteItem(6306, 100);
			} else
				e.getPlayer().sendMessage("You need 100 trading sticks to use this door.");
		} else if (e.getOpNum() == ClientPacket.OBJECT_OP1)
			if (e.getPlayer().getX() >= e.getObject().getX())
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
			else
				e.getPlayer().sendOptionDialogue("Pay 100 trading sticks to enter?", ops -> {
					ops.add("Yes", () -> {
						if (e.getPlayer().getInventory().containsItem(6306, 100)) {
							Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
							e.getPlayer().getInventory().deleteItem(6306, 100);
						} else
							e.getPlayer().sendMessage("You need 100 trading sticks to use this door.");
					});
					ops.add("No");
				});
	});

	public static ObjectClickHandler handleCrandorVolcanoCrater = new ObjectClickHandler(new Object[] { 25154 }, e -> {
		Player p = e.getPlayer();
		if (p.getQuestManager().getStage(Quest.DRAGON_SLAYER) == DragonSlayer.PREPARE_FOR_CRANDOR) {
			if (!p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(DragonSlayer.INTRODUCED_ELVARG_ATTR)) {
				DragonSlayer.introduceElvarg(p);
				return;
			}
		}

		e.getPlayer().setNextTile(Tile.of(2834, 9657, 0));
	});

	public static ObjectClickHandler handleCrandorVolcanoRope = new ObjectClickHandler(new Object[] { 25213 }, e -> {
		e.getPlayer().ladder(Tile.of(2832, 3255, 0));
	});

	public static ObjectClickHandler handleKaramjaVolcanoRocks = new ObjectClickHandler(new Object[] { 492 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2857, 9569, 0));
	});

	public static ObjectClickHandler handleKaramjaVolcanoRope = new ObjectClickHandler(new Object[] { 1764 }, e -> {
		e.getPlayer().ladder(Tile.of(2855, 3169, 0));
	});

	public static ObjectClickHandler handleElvargHiddenWall = new ObjectClickHandler(new Object[] { 2606 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.DRAGON_SLAYER) || e.getPlayer().getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(DragonSlayer.FINISHED_BOAT_SCENE_ATTR)) {
			e.getPlayer().sendMessage("You know from your boat accident there is more behind this wall...");
			Doors.handleDoor(e.getPlayer(), e.getObject());
		} else
			e.getPlayer().sendMessage("You see nothing but a wall...");
	});

	public static ObjectClickHandler handleShiloFurnaceDoor = new ObjectClickHandler(new Object[] { 2266, 2267 }, e -> {
		if (e.getObjectId() == 2267)
			return;
		if (e.getPlayer().getY() > e.getObject().getY())
			Doors.handleDoor(e.getPlayer(), e.getObject());
		else if (e.getPlayer().getInventory().hasCoins(20)) {
			e.getPlayer().getInventory().removeCoins(20);
			Doors.handleDoor(e.getPlayer(), e.getObject());
		} else
			e.getPlayer().sendMessage("You need 20 gold to use this furnace.");
	});

	public static ObjectClickHandler handleTzhaarEnter = new ObjectClickHandler(new Object[] { 68134 }, e -> {
		e.getPlayer().setNextTile(Tile.of(4667, 5059, 0));
	});

	public static ObjectClickHandler handleTzhaarExit = new ObjectClickHandler(new Object[] { 68135 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2845, 3170, 0));
	});

	public static ObjectClickHandler handleJogreCaveEnter = new ObjectClickHandler(new Object[] { 2584 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2830, 9522, 0));
	});

	public static ObjectClickHandler handleJogreCaveExit = new ObjectClickHandler(new Object[] { 2585 }, e -> {
		e.getPlayer().ladder(Tile.of(2824, 3120, 0));
	});

	public static ObjectClickHandler handleShiloEnter = new ObjectClickHandler(new Object[] { 2216 }, e -> {
		e.getPlayer().sendMessage("You quickly climb over the cart.");
		e.getPlayer().ladder(e.getPlayer().getX() > e.getObject().getX() ? e.getPlayer().transform(-4, 0, 0) : e.getPlayer().transform(4, 0, 0));
	});

	public static ObjectClickHandler handleShiloCartEnter = new ObjectClickHandler(new Object[] { 2230 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2833, 2954, 0));
	});

	public static ObjectClickHandler handleShiloCartExit = new ObjectClickHandler(new Object[] { 2265 }, e -> {
		e.getPlayer().setNextTile(Tile.of(2778, 3210, 0));
	});

	public static ObjectClickHandler handleElvargEntrance = new ObjectClickHandler(new Object[] { 25161 }, e -> {
		Player p = e.getPlayer();

		WorldTasks.schedule(new Task() {
			int ticks = 0;
			boolean goingEast = true;

			@Override
			public void run() {
				if (ticks == 0) {
					if (p.getX() == 2845) {
						p.setFaceAngle(Direction.getAngleTo(Direction.EAST));
						p.setNextAnimation(new Animation(839));
						goingEast = true;
					} else if (p.getX() == 2847) {
						p.setFaceAngle(Direction.getAngleTo(Direction.WEST));
						p.setNextAnimation(new Animation(839));
						goingEast = false;
					} else
						return;
				} else if (ticks >= 1) {
					if (goingEast)
						p.setNextTile(Tile.of(2847, p.getY(), 0));
					if (!goingEast)
						p.setNextTile(Tile.of(2845, p.getY(), 0));
					stop();
				}
				ticks++;
			}
		}, 0, 1);
	});
}
