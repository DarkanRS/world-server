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
package com.rs.game.content.quests.goblindiplomacy;

import java.util.ArrayList;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@QuestHandler(Quest.GOBLIN_DIPLOMACY)
@PluginEventHandler
public class GoblinDiplomacy extends QuestOutline {

	static final int WARTFACE = 4494;
	static final int BENTNOZE = 4493;
	static final int ORANGE_GOBLIN_MAIL = 286;
	static final int BLUE_GOBLIN_MAIL = 287;
	static final int GOBLIN_MAIL = 288;
	static final int RED_DYE = 1763;
	static final int YELLOW_DYE = 1765;
	static final int BLUE_DYE = 1767;
	static final int ORANGE_DYE = 1769;

	@Override
	public int getCompletedStage() {
		return 4;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
		case 0:
			lines.add("I can start this quest by speaking to General Bentnoze or");
			lines.add("General Wartface in the Goblin Village north of Falador.");
			break;
		case 1:
			lines.add("");
			lines.add("The Goblin generals need orange armor");
			lines.add("I can make orange armor by taking orange dye and using");
			lines.add("it on regular goblin mail.");
			lines.add("");
			lines.add("I can make orange dye by mixing red and yellow dye");
			lines.add("");
			lines.add("I can find goblin mail behind the generals hut");
			lines.add("");
			lines.add("Red dye is made from 3 red berries");
			lines.add("I could pay Aggie 5 coins in Draynor village for it");
			lines.add("");
			lines.add("I can find red berries by Varrock's south east mine");
			lines.add("");
			lines.add("Yellow dye can be made from 2 onions");
			lines.add("I could pay Aggie 5 coins in Draynor village for it");
			lines.add("");
			break;
		case 2:
			lines.add("");
			lines.add("The Goblin generals need blue armor");
			lines.add("I can make blue armor by taking blue dye and using");
			lines.add("it on regular goblin mail.");
			lines.add("");
			lines.add("Blue dye can be made from 2 woad leaves");
			lines.add("I could pay Aggie 5 coins in Draynor village for it");
			lines.add("");
			lines.add("I can grow woad leaves from woad seeds or buy them");
			lines.add("from Wyson the gardner in Falador park");
			break;
		case 3:
			lines.add("");
			lines.add("The Goblin generals need brown armor...");
			lines.add("That is just regular goblin mail...");
			lines.add("");
			break;
		case 4:
			lines.add("");
			lines.add("");
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
		player.getSkills().addXpQuest(Skills.CRAFTING, 200);
		getQuest().sendQuestCompleteInterface(player, 288, "Gold bar", "200 Crafting XP");
	}

	static class GeneralsD extends Conversation {

		public GeneralsD(Player player, int npcId) {
			super(player);

			Options startOptions = new Options() {
				@Override
				public void create() {
					option("Why are you arguing about the color of your armor?", new Dialogue().addPlayer(HeadE.CALM_TALK, "Why are you arguing about the color of your armor?").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "We decide to celebrate goblin new century").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "By changing the color of our armor").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Light blue get boring after a bit").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "And we want change").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Problem is they want different changed to us"));
					option("Wouldn't you prefer peace?", new Dialogue().addPlayer(HeadE.CALM_TALK, "Wouldn't you prefer peace?").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Yeah peace is good as long as it's peace wearing green armor").addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "But green too much like skin!").addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "Nearly make you look naked!"));
					option("Do you want me to pick enter an armor color for you?", new Dialogue().addPlayer(HeadE.CALM_TALK, "Do you want me to pick an armor color for you?").addPlayer(HeadE.CALM_TALK, "Different to either green or red").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Hmm me dunno what that'd look like").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "You'd have to bring me some, so us could decide").addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "Yep bring us orange armor").addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Yep orange might be good", () -> {
						player.getQuestManager().setStage(Quest.GOBLIN_DIPLOMACY, 1, true);
					}));
				}
			};

			if (player.getQuestManager().getStage(Quest.GOBLIN_DIPLOMACY) == 0) {
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Green armor best");
				addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "No no red every time");
				addNPC(WARTFACE, HeadE.CHILD_ANGRY, "Go away human, we busy");
				addOptions(startOptions);
			}
			if (player.getQuestManager().getStage(Quest.GOBLIN_DIPLOMACY) == 1) {
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Green armor best");
				addNPC(BENTNOZE, HeadE.CHILD_ANGRY, "No no red every time");
				addNPC(WARTFACE, HeadE.CHILD_ANGRY, "Go away human,we busy");
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Oh it you");
				if (player.getInventory().containsItems(new Item(286, 1))) {
					addPlayer(HeadE.CALM_TALK, "I have some orange armor");
					addSimple("You give the armor to the goblins");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "No I don't like that much");
					addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "It clashes with my skin color");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Try bringing us blue armor", () -> {
						player.getInventory().deleteItem(new Item(286, 1));
						player.getQuestManager().setStage(Quest.GOBLIN_DIPLOMACY, 2, true);
					});
				} else {
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Have you got some orange goblin armor yet?");
					addPlayer(HeadE.CALM_TALK, "Err no");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Come back when you have some");
				}
			}
			if (player.getQuestManager().getStage(Quest.GOBLIN_DIPLOMACY) == 2) {
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Green armor best");
				addNPC(BENTNOZE, HeadE.CHILD_ANGRY, "No no red every time");
				addNPC(WARTFACE, HeadE.CHILD_ANGRY, "Go away human,we busy");
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Oh it you");
				if (player.getInventory().containsItems(new Item(287, 1))) {
					addPlayer(HeadE.CALM_TALK, "I have some blue armor");
					addSimple("You give the armor to the goblins");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "No I don't like that much");
					addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "It clashes with my skin color");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Try bringing us brown armor", () -> {
						player.getInventory().deleteItem(new Item(287, 1));
						player.getQuestManager().setStage(Quest.GOBLIN_DIPLOMACY, 3, true);
					});
				} else {
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Have you got some blue goblin armor yet?");
					addPlayer(HeadE.CALM_TALK, "Err no");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Come back when you have some");
				}
			}
			if (player.getQuestManager().getStage(Quest.GOBLIN_DIPLOMACY) == 3) {
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Green armor best");
				addNPC(BENTNOZE, HeadE.CHILD_ANGRY, "No no red every time");
				addNPC(WARTFACE, HeadE.CHILD_ANGRY, "Go away human,we busy");
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Oh it you");
				if (player.getInventory().containsItems(new Item(288, 1))) {
					addPlayer(HeadE.CALM_TALK, "I have some brown armor");
					addSimple("You give the armor to the goblins");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "That color quiet nice. Me can see myself wearing this");
					addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "It a deal then. Brown armor it is");
					addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Thank you for sorting out our argument. Take this gold bar as reward!");
					addNext(() -> {
						player.getInventory().deleteItem(new Item(288, 1));
						player.getInventory().addItem(new Item(2357, 1), true);
						player.getQuestManager().completeQuest(Quest.GOBLIN_DIPLOMACY);
					});
				} else {
					addPlayer(HeadE.CALM_TALK, "Err no");
					addNPC(WARTFACE, HeadE.CHILD_ANGRY, "Come back when you have some");
				}
			}
			if (player.getQuestManager().getStage(Quest.GOBLIN_DIPLOMACY) == 4) {
				addNPC(WARTFACE, HeadE.CHILD_CALM_TALK, "Now you've solved our argument we gotta think of something else to do");
				addNPC(BENTNOZE, HeadE.CHILD_CALM_TALK, "Yep, we bored now");
			}
			create();
		}
	}

	public static NPCClickHandler talkGoblinGenerals = new NPCClickHandler(new Object[] { 4494, 4493 }, e -> e.getPlayer().startConversation(new GeneralsD(e.getPlayer(), e.getNPC().getId())));

	public static ObjectClickHandler handleGoblinVillageLadder = new ObjectClickHandler(new Object[] { 16450, 16556 }, e -> {
		if (e.getObjectId() == 16450 && e.getPlayer().getPlane() == 0)
			e.getPlayer().ladder(Tile.of(2953, 3497, 2));
		if (e.getObjectId() == 16556 && e.getPlayer().getPlane() == 2)
			e.getPlayer().ladder(Tile.of(2953, 3497, 0));
	});

	public static ObjectClickHandler handleGoblinMailChests = new ObjectClickHandler(new Object[] { 16559, 16560, 16561 }, e -> {
		if(!e.getPlayer().getQuestManager().getAttribs(Quest.GOBLIN_DIPLOMACY).getB("isGoblinMateCrateBlocked")) {
			e.getPlayer().getQuestManager().getAttribs(Quest.GOBLIN_DIPLOMACY).setB("isGoblinMateCrateBlocked", true);
			e.getPlayer().getInventory().addItem(288, 1);
			e.getPlayer().startConversation(new Dialogue().addItem(288, "You find goblin mail."));
			return;
		}
		e.getPlayer().sendMessage("You search the crate but find nothing.");
	});

	public static ItemOnItemHandler handleColorGoblinMail = new ItemOnItemHandler(GOBLIN_MAIL, new int[] { ORANGE_DYE, BLUE_DYE }, e -> {
		if (e.usedWith(ORANGE_DYE, GOBLIN_MAIL)) {
			e.getPlayer().getInventory().replace(e.getUsedWith(ORANGE_DYE), new Item(ORANGE_GOBLIN_MAIL, 1));
			e.getPlayer().getInventory().deleteItem(ORANGE_DYE, 1);
		}
		if (e.usedWith(BLUE_DYE, GOBLIN_MAIL)) {
			e.getPlayer().getInventory().replace(e.getUsedWith(BLUE_DYE), new Item(BLUE_GOBLIN_MAIL, 1));
			e.getPlayer().getInventory().deleteItem(BLUE_DYE, 1);
		}
	});

	public static ItemOnItemHandler handleRedYellowDyes = new ItemOnItemHandler(RED_DYE, new int[] { YELLOW_DYE }, e -> {
		if (e.usedWith(RED_DYE, YELLOW_DYE)) {
			e.getPlayer().getInventory().replace(e.getItem2(), new Item(ORANGE_DYE, 1));
			e.getPlayer().getInventory().deleteItem(e.getItem1().getSlot(), e.getItem1());
		}
	});

}
