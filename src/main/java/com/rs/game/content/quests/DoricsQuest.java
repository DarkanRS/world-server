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
package com.rs.game.content.quests;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.content.skills.smithing.ForgingInterface;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.DORICS_QUEST)
@PluginEventHandler
public class DoricsQuest extends QuestOutline {

	private final static int DORIC = 284;
	private final static int CLAY = 434;
	private final static int COPPER_ORE = 436;
	private final static int IRON_ORE = 440;
	private final static int BRONZE_PICKAXE = 1265;

	@Override
	public int getCompletedStage() {
		return 2;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Doric who is");
			lines.add("North of Falador.");
			lines.add("<br>");
			lines.add("There aren't any requirements but level 15 Mining will help");
			break;
		case 1:
			lines.add("<str>I have spoken to Doric.");
			lines.add("<br>");
			lines.add("I need to collect some items and bring them to Doric.");
			lines.add((player.getInventory().containsItem(CLAY, 6) ? "<str>":"")+"6 Clay");
			lines.add((player.getInventory().containsItem(COPPER_ORE, 4) ? "<str>":"")+"4 Copper Ore");
			lines.add((player.getInventory().containsItem(IRON_ORE, 2) ? "<str>":"")+"2 Iron Ore");
			break;
		case 2:
			lines.add("");
			lines.add("<str>Doric rewarded me for all my hard work");
			lines.add("<str>I can now use Doric's Anvils whenever I want");
			lines.add("QUEST COMPLETE!");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.MINING, 1300);
		player.getInventory().addCoins(180);
		getQuest().sendQuestCompleteInterface(player, 1891, "1300 Mining XP", "180 coins", "Use of Doric's Anvils");
	}

	static class DoricD extends Conversation {
		public DoricD(Player player) {
			super(player);

			switch(player.getQuestManager().getStage(Quest.DORICS_QUEST)) {
			case 0:

				Dialogue notSkilled = new Dialogue()
				.addPlayer(HeadE.CALM_TALK, "But I'm not good enough miner to get iron ore.")
				.addNPC(DORIC, HeadE.CALM_TALK, "Oh well, you could practice mining until you can. Can't beat a bit of mining - it's a useful skill Failing that, you might be able to find a more experienced adventurer to buy the iron ore off.").getHead();

				Dialogue startQuestOptions = new Dialogue().addOptions(new Options() {
					@Override
					public void create() {
						option("Yes, I will get you the materials.", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Yes, I will get you the materials.")
								.addNPC(DORIC, HeadE.CALM_TALK, "Clay is what I use more than anything, to make casts. Could you get me 6 clay, 4 copper ore, and 2 iron ore, please? I could pay a little, and let you use my anvils. Take this pickaxe with you just in case you need it.", () -> {
									player.getInventory().addItemDrop(BRONZE_PICKAXE, 1);
									player.getQuestManager().setStage(Quest.DORICS_QUEST,  1);
								})
								.addOption("What would you like to say?", "Where can I find those?", "Certainly, I'll be right back!")
								.addPlayer(HeadE.CALM_TALK, "Where can I find those?")
								.addNPC(DORIC, HeadE.CALM_TALK, "You'll be able to find all those ores in the rocks just inside the Dwarven Mine. Head east from here and you'll find the entrance in the side of Ice Mountain.")
								.addNext((player.getSkills().getLevel(Constants.MINING) < 15 ? notSkilled.cutPrev()
										: new Dialogue().addNPC(DORIC, HeadE.CALM_TALK, "Off you go then."))));
						option("No, hitting rocks is for the boring people, sorry.", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "No, hitting rocks is for the boring people, sorry.")
								.addNPC(DORIC, HeadE.CALM_TALK, "That is your choice. Nice to meet you anyway."));
					}
				}).getHead();

				addNPC(DORIC, HeadE.SAD, "Hello traveller, what brings you to my humble smithy?");
				addOptions(new Options() {
					@Override
					public void create() {
						option("I wanted to use your anvils.", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I wanted to use your anvils.")
								.addNPC(DORIC, HeadE.CALM_TALK, "My anvils get enough work with my own use. I make pickaxes, and it takes a lot of hard work. If you could get me some more materials, then I could let you use them.")
								.addNext(startQuestOptions.cutPrev()));
						option("I want to use your whetstone.", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I want to use your whetstone.")
								.addNPC(DORIC, HeadE.CALM_TALK, "The whetstone is for more advanced smithing, but I could let you use it as well as my anvils if you could get me some more materials.")
								.addNext(startQuestOptions.cutPrev()));
						option("Mind your own business, shortstuff!", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Mind your own business, shortstuff!")
								.addNPC(DORIC, HeadE.CALM_TALK, "How nice to meet someone with such pleasant manners. Do come again when you need to shout at someone smaller than you!"));
						option("I was just checking out the landscape.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I was just checking out the landscape.")
								.addNPC(DORIC, HeadE.CALM_TALK, "Hope you like it. I do enjoy the solitude of my little home. If you get time, please say hi to my friends in the Dwarven Mine.")
								.addOption("What would you like to say?", "Dwarven Mine?", "Will do!")
								.addPlayer(HeadE.CALM_TALK, "Dwarven Mine?")
								.addNPC(DORIC, HeadE.CALM_TALK, "Yep, the entrance is in the side of Ice Mountain just to the east of here. They're a friendly bunch. Stop in at Nurmof's store and buy one of my pickaxes!"));
						option("What do you make here?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What do you make here?")
								.addNPC(DORIC, HeadE.CALM_TALK, "I make pickaxes. I am the best maker of pickaxes in the whole of Gielinor.")
								.addPlayer(HeadE.HAPPY_TALKING, "Do you have any to sell?")
								.addNPC(DORIC, HeadE.CALM_TALK, "Sorry, but I've got a running order with Nurmof.")
								.addOption("What would you like to say?", "Who's Nurmof?", "Ah, fair enough.")
								.addPlayer(HeadE.CALM_TALK, "Who's Nurmof?")
								.addNPC(DORIC, HeadE.CALM_TALK, "Nurmof has a store over in the Dwarven Mine. You can find the entrance on the side of Ice Mountain to the east of here."));
					}
				});
				break;
			case 1:
				addNPC(DORIC, HeadE.CALM_TALK, "Have you got my materials yet, traveller?");
				if (player.getInventory().containsItems(new int[] { CLAY, COPPER_ORE, IRON_ORE }, new int[] { 6, 4, 2 })) {
					addPlayer(HeadE.CALM_TALK, "I have everything you need.");
					addNPC(DORIC, HeadE.CALM_TALK, "Many thanks! Pass them here, please. I can spare you some coins for your trouble, and please use my anvils any time you want.");
					addItem(COPPER_ORE, "You hand the clay, copper, and iron to Doric.");
					addNext(() -> {
						player.getInventory().deleteItem(CLAY, 6);
						player.getInventory().deleteItem(COPPER_ORE, 4);
						player.getInventory().deleteItem(IRON_ORE, 2);
						player.getQuestManager().completeQuest(Quest.DORICS_QUEST);
					});
				} else {
					addPlayer(HeadE.CALM_TALK, "Sorry, I don't have them all yet.");
					addNPC(DORIC, HeadE.CALM_TALK, "Not to worry, stick at it. Remember, I need 6 clay, 4 copper ore, and 2 iron ore.");
					addPlayer(HeadE.CALM_TALK, "Where can I find those?");
					addNPC(DORIC, HeadE.CALM_TALK, "You'll be able to find all those ores in the rocks just inside the Dwarven Mine. Head east from here and you'll find the entrance in the side of Ice Mountain.");
				}
				break;
			case 2:
				addNPC(DORIC, HeadE.CALM_TALK, "Hello traveller, how is your metalworking coming along?");
				addPlayer(HeadE.CALM_TALK, "Not too bad, Doric.");
				addNPC(DORIC, HeadE.CALM_TALK, "Good, the love of metal is a thing close to my heart.");
				break;
			}
			create();
		}
	}

	public static NPCClickHandler doricHandler = new NPCClickHandler(new Object[] { DORIC }, e -> {
		if (e.isAtNPC())
			if (e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new DoricD(e.getPlayer()));
	});

	public static ObjectClickHandler handleDoricsAnvil = new ObjectClickHandler(new Object[] { 2782, 10641 }, e -> {
		if (!e.getPlayer().isQuestComplete(Quest.DORICS_QUEST))
			e.getPlayer().startConversation(new DoricD(e.getPlayer()));
		else if (e.getObject().getDefinitions().containsOption(0, "Smith"))
			ForgingInterface.openSmithingInterfaceForHighestBar(e.getPlayer());
	});
}
