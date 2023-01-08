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
package com.rs.game.content.world.areas.taverly;

import java.util.List;

import com.rs.game.content.quests.heroesquest.dialogues.AchiettiesHeroesQuestD;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.content.world.unorganized_dialogue.TanningD;
import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.Dialogue;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.dialogue.Options;
import com.rs.game.engine.quest.Quest;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PickupItemEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PickupItemHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Taverly {
	public static NPCClickHandler handleAchietties = new NPCClickHandler(new Object[] { 796 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getPlayer().isQuestComplete(Quest.HEROES_QUEST)) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					int NPC = e.getNPCId();

					{
						addNPC(NPC, HeadE.CALM_TALK, "Greetings, welcome to the heroes guild!");
						addPlayer(HeadE.HAPPY_TALKING, "Thank you...");
						addNPC(NPC, HeadE.CALM_TALK, "You're welcome.");
						create();
					}
				});
			} else {
				e.getPlayer().startConversation(new AchiettiesHeroesQuestD(e.getPlayer()).getStart());
			}
		}
	};

	public static NPCClickHandler handleHeadFarmerJones = new NPCClickHandler(new Object[] { 14860 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.CALM, "Can I help you with your farming troubles?");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need farming supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "head_farmer_jones_shop");
								});
								option("Tell me more about farming", new Dialogue().addNPC(e.getNPCId(),
												HeadE.HAPPY_TALKING,
												"By farming you can grow your own plants. You'll start with simple stuff like potatoes"
														+ " but if you stick at it, you'll even be able to grow trees and the like. Farming's a slow-paced skill. If you want")
										.addNPC(e.getNPCId(), HeadE.CALM,
												" something that only needs checking on occasionally, it'll suit you down to the ground. Plant as many crops as ya can,"
														+ " as often as ya can. It's only through practice that you'll improve."));
								option("farewell");
							}
						});
						create();
					}
				});
			if (e.getOption().equalsIgnoreCase("trade"))
				ShopsHandler.openShop(e.getPlayer(), "head_farmer_jones_shop");

		}
	};

	public static ObjectClickHandler handleTaverlyDungeonOddWall = new ObjectClickHandler(new Object[]{2117}) {
		@Override
		public void handle(ObjectClickEvent e) {
			Doors.handleDoor(e.getPlayer(), e.getObject(), -1);
		}
	};

	public static NPCClickHandler handleNicholasAngle = new NPCClickHandler(new Object[] { 14879 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.CALM, "I'm not surprised you want to fish. Fishing is great!");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need fishing bait", () -> {
									ShopsHandler.openShop(e.getPlayer(), "nicholas_fishing_shop");
								});
								option("Tell me more about fishing", new Dialogue().addNPC(e.getNPCId(),
										HeadE.HAPPY_TALKING,
										"Fishing is more than a method of gathering food. It's more than a profession. "
												+ "It's a way of life! Fish makes good eating, especially when you need to recover after battle."));
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "nicholas_fishing_shop");

		}
	};

	public static NPCClickHandler handleAylethHunterShop = new NPCClickHandler(new Object[] { 14864 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.TALKING_ALOT,
								"I walk the lonely path of the hunter. Will you walk with me? Wait, did that make sense?");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need hunter supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "ayleths_hunting_supplies");
								});
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "ayleths_hunting_supplies");
		}
	};

	public static NPCClickHandler handleFetchShop = new NPCClickHandler(new Object[] { 14858 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.CALM_TALK, "Oh, hello. Need something?");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need fletching supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "alison_fletch_shop");
								});
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "alison_fletch_shop");
		}
	};

	public static NPCClickHandler handleWoodCuttingShop = new NPCClickHandler(new Object[] { 14885 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Want to do some wouldcutting, mate?");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need wouldcutting supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "will_woodcut_shop");
								});
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "will_woodcut_shop");
		}
	};

	public static NPCClickHandler handleFiremakingShop = new NPCClickHandler(new Object[] { 14883 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Hey! Let's SET SOMETHING ON FIRE!");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need firemaking supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "marcus_firemaking_shop");
								});
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "marcus_firemaking_shop");
		}
	};

	public static NPCClickHandler handleHerbloreShop = new NPCClickHandler(new Object[] { 14854 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Are you here to practice your herblore?");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need herblore supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "poletaxs_herblore_shop");
								});
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "poletaxs_herblore_shop");
		}
	};

	public static NPCClickHandler handleSmithingShop = new NPCClickHandler(new Object[] { 14874 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Do you need smithing help?");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need smithing supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "martin_smithing_shop");
								});
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "martin_smithing_shop");
		}
	};

	public static NPCClickHandler handleCraftingShop = new NPCClickHandler(new Object[] { 14877 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING,
								"You need to make things. Leather. Pottery. Every piece you make will increase your skill.");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need crafting supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "jack_crafting_shop");
								});
								option("I need you to tan some leather for me.", () -> e.getPlayer().startConversation(new TanningD(e.getPlayer(), false)));
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 4)
				e.getPlayer().startConversation(new TanningD(e.getPlayer(), false));
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "jack_crafting_shop");
		}
	};

	public static NPCClickHandler handleMiningShop = new NPCClickHandler(new Object[] { 14870 }) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1 || option == 3)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Tobias Bronzearms at your service.");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need mining supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "tobias_mining_shop");
								});
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 5)
				ShopsHandler.openShop(e.getPlayer(), "tobias_mining_shop");
		}
	};

	public static NPCClickHandler handleRunecraftingShop = new NPCClickHandler(false, new Object[]{14906}) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "So many runes...");
						addPlayer(HeadE.ROLL_EYES, "Are you alright?");
						addNPC(e.getNPCId(), HeadE.HAPPY_TALKING,
								"Oh! Don't worry. I'm trying to learn about runes from Mistress Carwen. It's a fascinating subject!");
						addOptions(new Options() {
							@Override
							public void create() {
								option("I need Runecrafting supplies", () -> {
									ShopsHandler.openShop(e.getPlayer(), "carwens_rune_shop");
								});
								option("What can you tell me about Runecrafting?", new Dialogue().addNPC(e.getNPCId(),
												HeadE.HAPPY_TALKING,
												"There's so much to talk about! I'm just learning the ropes though. You'd be"
														+ " better off talking to Mistress Carwen. As part of my duties I do help her by selling runes. Would you like to"
														+ " take a look?")
										.addPlayer(HeadE.CALM, "I'll have a look.").addNext(() -> {
											ShopsHandler.openShop(e.getPlayer(), "carwens_rune_shop");
										}));
								option("farewell");
							}
						});
						create();
					}
				});
			if (option == 3)
				ShopsHandler.openShop(e.getPlayer(), "carwens_rune_shop");
		}
	};

	public static PickupItemHandler zammyWines = new PickupItemHandler(new Object[] { 245 }, WorldTile.of(2946, 3474, 0), WorldTile.of(2946, 3473, 0)) {
		@Override
		public void handle(PickupItemEvent e) {
			if (!e.isTelegrabbed()) {
				e.getPlayer().applyHit(new Hit(e.getPlayer(), 50, Hit.HitLook.TRUE_DAMAGE));
				List<NPC> npcs = e.getPlayer().getNearbyNPCs(true, n -> n.getId() == 189);
				for (NPC n : npcs) {
					if (n.isDead())
						continue;
					n.forceTalk("Hands off Zamorak's wine!");
					n.setTarget(e.getPlayer());
				}
				e.cancelPickup();
			}
		}
	;};
}
