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
package com.rs.game.content.world.areas.lumbridge;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.KNOWS_ABOUT_DRAGON_BREATH_ATTR;

import com.rs.game.content.ItemConstants;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.RuneMysteries;
import com.rs.game.content.quests.dragonslayer.DragonSlayer;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Lumbridge {

	public static final String WHEAT_DEPOSITED = "wheatInMill";
	public static final String WHEAT_GRINDED = "wheatGrinded";
	
	public static ItemOnNPCHandler handleBobRepairs = new ItemOnNPCHandler(new Object[] { 519 }, e -> {
		ItemConstants.handleRepairs(e.getPlayer(), e.getItem(), false, e.getItem().getSlot());
	});

	public static NPCClickHandler handleExplorerJack = new NPCClickHandler(new Object[] { 7969 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
				addOptions("What would you like to say?", new Options() {
					@Override
					public void create() {
						option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.EXPLORERS_RING).getStart());
					}
				});
			}
		});
	});

	public static NPCClickHandler handleLachtopher = new NPCClickHandler(new Object[] { 7870 }, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addPlayer(HeadE.CALM_TALK, "Hello there");
				addNPC(e.getNPCId(), HeadE.DRUNK, "Hello, I suppose. I'm Lachtopher. Could you lend me some money?");
				addPlayer(HeadE.SKEPTICAL, "Lend you money? I really don't think so. Don't you have any of your own?");
				addNPC(e.getNPCId(), HeadE.DRUNK, "I spend it all and I can't be bothered to earn any more");
				addPlayer(HeadE.VERY_FRUSTRATED, "Right, and you want my hard earned money instead? no chance!");
				addNPC(e.getNPCId(), HeadE.DRUNK, "You are just like my sister, Victoria. She wont give me any money");
				addPlayer(HeadE.VERY_FRUSTRATED, "Your sister sounds like she has the right idea");
				addNPC(e.getNPCId(), HeadE.DRUNK, "Yeah, i've heard it all before. 'Oh', she says, 'it is easy to make money: just complete tasks for cash'");
				addPlayer(HeadE.VERY_FRUSTRATED, "Well, if you want to make money...");
				addNPC(e.getNPCId(), HeadE.DRUNK, "That's just it. I don't want to make money. I just want to have money");
				addPlayer(HeadE.VERY_FRUSTRATED, "I've had it with you!");
				create();
			}
		});
	});

	public static NPCClickHandler handleBob = new NPCClickHandler(new Object[] { 519 }, e -> {
		if (e.getOpNum() == 1)
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what can I do for you?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							option("What do you have for sale?", () -> ShopsHandler.openShop(player, "bobs_brilliant_axes"));
							option("Can you repair my items?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can you repair my items?")
									.addNPC(519, HeadE.CALM_TALK, "Sure just give me the item.")
									.addSimple("(Use the item on Bob)")
							);
							option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.EXPLORERS_RING).getStart());
						}
					});
				}
			});
		else
			ShopsHandler.openShop(e.getPlayer(), "bobs_brilliant_axes");
	});

	public static ItemOnObjectHandler handleWheatDeposit = new ItemOnObjectHandler(new Object[] { 70034 }, e -> {
		if (e.getItem().getId() == 1947)
			if (e.getPlayer().get(WHEAT_DEPOSITED) == Boolean.FALSE) {
				e.getPlayer().getInventory().deleteItem(1947, 1);
				e.getPlayer().setNextAnimation(new Animation(832));
				e.getPlayer().sendMessage("You put the wheat in the hopper.");
				e.getPlayer().save(WHEAT_DEPOSITED, Boolean.TRUE);
			}
	});

	public static void updateWheat(Player player) {
		player.getVars().setVar(695, player.get(WHEAT_GRINDED) == Boolean.TRUE ? 1 : 0);
	}

	public static LoginHandler updateWheatLogin = new LoginHandler(e -> updateWheat(e.getPlayer()));

	public static NPCClickHandler handleDukeHoratio = new NPCClickHandler(new Object[] { 741 }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to")) {
			int NPC = e.getNPCId();
			e.getPlayer().startConversation(new Dialogue()
					.addNPC(NPC, HeadE.CALM_TALK, "Greetings. Welcome to my castle.")
					.addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							if(e.getPlayer().getQuestManager().getStage(Quest.DRAGON_SLAYER) == DragonSlayer.PREPARE_FOR_CRANDOR
									&& e.getPlayer().getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(KNOWS_ABOUT_DRAGON_BREATH_ATTR))
								option("I seek a shield that will protect me from dragonbreath.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I seek a shield that will protect me from dragonbreath.")
										.addNPC(NPC, HeadE.SKEPTICAL_THINKING, "Interesting, how do you know I have a shield?")
										.addPlayer(HeadE.SECRETIVE, "The Champions' Guild, er, guildmaster, told me...")
										.addNPC(NPC, HeadE.HAPPY_TALKING, "The guildmaster eh? What dragon do you intend to slay?")
										.addPlayer(HeadE.HAPPY_TALKING, "Elvarg...")
										.addNPC(NPC, HeadE.AMAZED, "ELVARG!?")
										.addNPC(NPC, HeadE.LAUGH, "He has not given you an easy task has he?")
										.addPlayer(HeadE.CALM, "...")
										.addNPC(NPC, HeadE.CALM_TALK, "Okay, for this specific purpose I can give you a shield.")
										.addSimple("The duke hands you the shield.", ()->{
											e.getPlayer().getInventory().addItem(1540, 1);
										})
								);
							if(e.getPlayer().getQuestManager().getStage(Quest.DRAGON_SLAYER) >= DragonSlayer.REPORT_TO_OZIACH)
								option("I seek a shield that will protect me from dragonbreath.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "I seek a shield that will protect me from dragonbreath.")
										.addNPC(NPC, HeadE.CALM_TALK, "A knight going on a dragon quest, hmm? What dragon do you intend to slay?")
										.addNPC(NPC, HeadE.CALM_TALK, "Ah, well, nvm. Of course, now you've slain Elvarg, you've earned the right to it!")
										.addSimple("The duke hands you the shield.", ()->{
											e.getPlayer().getInventory().addItem(1540, 1);
										})
								);
							option("About Rune Mysteries", new RuneMysteries.DukeHoracioRuneMysteriesD(e.getPlayer()).getCurrent());
							option("Where can I find money?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Where can I find money?")
									.addNPC(NPC, HeadE.CALM_TALK, "I hear many of the local people earn money by learning a skill. Many people get by in" +
											" life by becoming accomplished smiths, cooks, miners and woodcutters.")
							);
							option("Farewell", new Dialogue());
						}
					})
			);
		}
	});

	public static ObjectClickHandler handleCellarLadders = new ObjectClickHandler(new Object[] { 24360, 24365 }, e -> {
		if (e.getObjectId() == 24360)
			e.getPlayer().setNextTile(Tile.of(3190, 9834, 0));
		else if (e.getObjectId() == 24365)
			e.getPlayer().setNextTile(Tile.of(3188, 3433, 0));
	});

	public static ObjectClickHandler handleRFDChest = new ObjectClickHandler(new Object[] { 12308 }, e -> {
		switch (e.getOpNum()) {
		case OBJECT_OP1:
			e.getPlayer().getBank().open();
			break;
		case OBJECT_OP2:
			ShopsHandler.openShop(e.getPlayer(), "culinaromancer_food_10");
			break;
		case OBJECT_OP3:
			ShopsHandler.openShop(e.getPlayer(), "culinaromancer_equipment_10");
			break;
		default:
			break;
		}
	});

	public static ObjectClickHandler handleLadders = new ObjectClickHandler(new Object[] { 36771, 36772 }, e -> {
		if (e.getObjectId() == 36771)
			e.getPlayer().useLadder(Tile.of(3207, 3222, 3));
		else if (e.getObjectId() == 36772)
			e.getPlayer().useLadder(Tile.of(3207, 3224, 2));
	});

	public static ObjectClickHandler handleThievesGuildExitLadder = new ObjectClickHandler(new Object[] { 52308 }, e -> {
		e.getPlayer().useLadder(Tile.of(3223, 3269, 0));
	});

	public static ObjectClickHandler handleThievesGuildEntrance = new ObjectClickHandler(new Object[] { 52309 }, e -> {
		e.getPlayer().useLadder(Tile.of(4762, 5891, 0));
	});

	public static ObjectClickHandler handleTakeFlour = new ObjectClickHandler(new Object[] { 36880 }, e -> {
		if (e.getPlayer().getInventory().containsItem(1931, 1)) {
			if (e.getPlayer().get(WHEAT_GRINDED) == Boolean.TRUE) {
				e.getPlayer().save(WHEAT_GRINDED, Boolean.FALSE);
				e.getPlayer().save(WHEAT_DEPOSITED, Boolean.FALSE);
				e.getPlayer().sendMessage("You take the ground flour.");
				e.getPlayer().setNextAnimation(new Animation(832));
				e.getPlayer().getInventory().deleteItem(1931, 1);
				e.getPlayer().getInventory().addItem(1933, 1);
				updateWheat(e.getPlayer());
			}
		} else
			e.getPlayer().sendMessage("You need an empty pot to gather the flour.");
	});

	public static ObjectClickHandler handleWindmillLever = new ObjectClickHandler(new Object[] { 2718 }, e -> {
		e.getPlayer().sendMessage("You pull the lever.");
		if (e.getPlayer().get(WHEAT_DEPOSITED) == Boolean.TRUE) {
			e.getPlayer().save(WHEAT_GRINDED, Boolean.TRUE);
			e.getPlayer().sendMessage("You hear the grinding of stones and the wheat falls below.");
			updateWheat(e.getPlayer());
		}
	});

	public static ObjectClickHandler handleHatchetStump = new ObjectClickHandler(new Object[] { 36974 }, e -> {
		if (e.getPlayer().getInventory().containsItem(1351, 1))
			e.getPlayer().sendMessage("You have already taken the hatchet.");
		else
			e.getPlayer().getInventory().addItem(1351, 1);
	});

	public static ObjectClickHandler handleStaircases1 = new ObjectClickHandler(new Object[] { 45481, 45482 }, e -> {
		if (e.getObjectId() == 45481)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, 1));
		else if (e.getObjectId() == 45482)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, -1));
	});

	public static ObjectClickHandler handleStaircases2 = new ObjectClickHandler(new Object[] { 45483, 45484 }, e -> {
		if (e.getObjectId() == 45483)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? -3 : e.getObject().getRotation() == 3 ? 3 : 0, e.getObject().getRotation() == 0 ? -3 : e.getObject().getRotation() == 2 ? 3 : 0, 1));
		else if (e.getObjectId() == 45484)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 1 ? 3 : e.getObject().getRotation() == 3 ? -3 : 0, e.getObject().getRotation() == 0 ? 3 : e.getObject().getRotation() == 2 ? -3 : 0, -1));
	});

	public static ObjectClickHandler handleChurchLadders = new ObjectClickHandler(new Object[] { 36984, 36986 }, e -> {
		e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 36984 ? -2 : 2, 0, 1));
	});

	public static ObjectClickHandler handleChurchLaddersT2Up = new ObjectClickHandler(new Object[] { 36988, 36989 }, e -> {
		e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 36988 ? -1 : 1, -1, 1));
	});

	public static ObjectClickHandler handleChurchLaddersT2Down = new ObjectClickHandler(new Object[] { 36990, 36991 }, e -> {
		e.getPlayer().useLadder(e.getPlayer().transform(e.getObjectId() == 36990 ? 1 : -1, 1, -1));
	});
}
