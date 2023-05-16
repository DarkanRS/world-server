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
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.IMP_CATCHER)
@PluginEventHandler
public class ImpCatcher extends QuestOutline {

	private final static int WIZARD_MIZGOG = 706;
	private final static int RED_BEAD = 1470;
	private final static int YELLOW_BEAD = 1472;
	private final static int BLACK_BEAD = 1474;
	private final static int WHITE_BEAD = 1476;

	@Override
	public int getCompletedStage() {
		return 2;
	}

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Wizard Mizgog who is");
			lines.add("in the Wizard's Tower.");
			break;
		case 1:
			lines.add("<str>I have spoken to Wizard Mizgog.");
			lines.add("<br>");
			lines.add("I need to collect some items by killing Imps.");
			lines.add((player.getInventory().containsItem(BLACK_BEAD, 1) ? "<str>":"")+"1 Black Bead.");
			lines.add((player.getInventory().containsItem(RED_BEAD, 1) ? "<str>":"")+"1 Red Bead.");
			lines.add((player.getInventory().containsItem(WHITE_BEAD, 1) ? "<str>":"")+"1 White Bead.");
			lines.add((player.getInventory().containsItem(YELLOW_BEAD, 1) ? "<str>":"")+"1 Yellow Bead.");
			break;
		case 2:
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
		player.getSkills().addXpQuest(Constants.MAGIC, 875);
		player.getInventory().addItemDrop(1478, 1);
		getQuest().sendQuestCompleteInterface(player, 1891, "875 Magic XP", "Amulet of Accuracy");
	}

	static class MizgogD extends Conversation {
		public MizgogD(Player player) {
			super(player);

			switch(player.getQuestManager().getStage(Quest.IMP_CATCHER)) {
			case 0:
				addNPC(WIZARD_MIZGOG, HeadE.SAD, "My beads! Where are my beads?");
				addOption("What would you like to say?", "Can I help you?", "Goodbye.");
				addPlayer(HeadE.CALM_TALK, "Can I help you?");
				addNPC(WIZARD_MIZGOG, HeadE.FRUSTRATED, "Wizard Grayzag next door decided that he didn't like me for some reason, so he enlisted an army of imps.");
				addNPC(WIZARD_MIZGOG, HeadE.FRUSTRATED, " The imps stole all sorts of things. Most of them were things I don't really care about like eggs, balls of wool, things like that... ");
				addNPC(WIZARD_MIZGOG, HeadE.FRUSTRATED, "But they stole my magic beads! There was a red one, a yellow one, a black one, and a white one. The imps have spread out all over the kingdom by now. Could you get my beads back for me?");
				addOption("Do you want to start a quest?", "Accept quest.", "Not right now.");
				addNPC(WIZARD_MIZGOG, HeadE.CALM_TALK, "The imps will be all over the kingdom by now. You should kill any imps you find and collect any beads that they drop. I need a red one, a yellow one, a black one, and a white one.", () -> {
					player.getQuestManager().setStage(Quest.IMP_CATCHER, 1);
				});
				break;
			case 1:
				addNPC(WIZARD_MIZGOG, HeadE.SKEPTICAL, "How are you doing finding my beads?");
				addOptions(new Options() {
					@Override
					public void create() {
						if (player.getInventory().containsItems(new int[] { RED_BEAD, YELLOW_BEAD, BLACK_BEAD, WHITE_BEAD }, new int[] { 1, 1, 1, 1 }))
							option("I've got all four beads.", new Dialogue().addPlayer(HeadE.CALM_TALK, "I've got all four beads.")
									.addNPC(WIZARD_MIZGOG, HeadE.HAPPY_TALKING, "Thank you! Give them here and I'll check that they really are my beads, before I give you your reward. You'll like it, it's an amulet of accuracy.")
									.addSimple("You give four coloured beads to Wizard Mizgog.")
									.addNext(() -> {
										player.getInventory().deleteItem(RED_BEAD, 1);
										player.getInventory().deleteItem(YELLOW_BEAD, 1);
										player.getInventory().deleteItem(BLACK_BEAD, 1);
										player.getInventory().deleteItem(WHITE_BEAD, 1);
										player.getQuestManager().completeQuest(Quest.IMP_CATCHER);
									}));
						else
							option("I don't have them all yet.", new Dialogue()
									.addPlayer(HeadE.CALM_TALK, "I don't have them all yet.")
									.addNPC(WIZARD_MIZGOG, HeadE.CALM_TALK, "Come back when you have them all. I've lost a white bead, a red bead, a black bead, and a yellow bead. Go kill some imps!"));
						option("Goodbye.");
					}
				});
				break;
			case 2:
				addOptions(new Options() {
					@Override
					public void create() {
						option("Got any more quests?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Got any more quests?")
								.addNPC(WIZARD_MIZGOG, HeadE.CALM_TALK, "No, everything is good with the world today."));
						option("Do you know any interesting spells you could teach me?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Do you know any interesting spells you could teach me?")
								.addNPC(WIZARD_MIZGOG, HeadE.CALM_TALK, "I don't think so, the type of magic I study involves years of meditation and research."));
						option("Have you got another one of those fancy schmancy amulets?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Have you got another one of those fancy schmancy amulets?")
								.addNPC(WIZARD_MIZGOG, HeadE.CALM_TALK, "I have a few spare.")
								.addNext(() -> ShopsHandler.openShop(player, "wizard_mizgog") ));
					}
				});
				break;
			}
			create();
		}
	}

	public static NPCClickHandler mizgogHandler = new NPCClickHandler(new Object[] { WIZARD_MIZGOG }, e -> {
		if (e.getOption().equalsIgnoreCase("talk-to"))
			e.getPlayer().startConversation(new MizgogD(e.getPlayer()));
		else if (e.getOption().equalsIgnoreCase("trade") && e.getPlayer().isQuestComplete(Quest.IMP_CATCHER))
			ShopsHandler.openShop(e.getPlayer(), "wizard_mizgog");
		else
			e.getPlayer().startConversation(new MizgogD(e.getPlayer()));
	});
}
