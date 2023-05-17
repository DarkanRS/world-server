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
import com.rs.game.World;
import com.rs.game.content.skills.runecrafting.RunecraftingAltar;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(Quest.RUNE_MYSTERIES)
@PluginEventHandler
public class RuneMysteries extends QuestOutline {
	final static int DUKEHORACIO = 741;
	final static int SEDRIDOR = 300;
	final static int AUBURY = 5913;
	@Override
	public int getCompletedStage() {
		return 4;
	}
	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
		case 0:
			lines.add("I can start this quest by speaking to Duke Horacio");
			lines.add("on the second floor of Lumbridge");
			lines.add("");
			break;
		case 1:
			lines.add("Duke Horacio gave me a talisman to be investigated.");
			lines.add("I should take this talisman to Sedridor in the");
			lines.add("Wizard tower basement");
			lines.add("");
			break;
		case 2:
			lines.add("Sedridor gave me a research package to be investigated");
			lines.add("I should take this talisman to Aubury in his");
			lines.add("rune shop in Varrock");
			lines.add("");
			break;
		case 3:
			lines.add("Aubury gave me research notes to be investigated");
			lines.add("I should take these notes to Sedridor in the");
			lines.add("Wizard tower basement");
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
		getQuest().sendQuestCompleteInterface(player, 1438, "Access to the rune essence mine", "Air talisman");
	}

	public static class DukeHoracioRuneMysteriesD extends Conversation {
		public DukeHoracioRuneMysteriesD(Player player) {
			super(player);

			switch(player.getQuestManager().getStage(Quest.RUNE_MYSTERIES)) {
			case 0:
				addPlayer(HeadE.CALM_TALK, "Have you any quests for me?");
				addNPC(DUKEHORACIO, HeadE.CALM_TALK, "Well, it's not really a quest, but I recently discovered this strange talisman. It's not like " +
						"anything I have seen before.");
				addNPC(DUKEHORACIO, HeadE.CALM_TALK, "Would you take it to the head wizard in the basement of the Wizards' Tower for me? It should not " +
						"take you very long at all and I would be awfully grateful");
				addOptions("Start Rune Mysteries?", new Options() {
					@Override
					public void create() {
						option("Sure, i'll do it", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Sure, i'll do it")
								.addNPC(DUKEHORACIO, HeadE.CALM_TALK, "Thank you very much, stranger. I am sure the head wizard will reward you for such " +
										"an interesting find.")
								.addSimple("The duke hands you a talisman")
								.addNext(()->{
									player.getInventory().addItem(new Item(15361, 1), true);
									player.getQuestManager().setStage(Quest.RUNE_MYSTERIES, 1, true);
								}));
						option("Not right now", () -> {

						});
					}
				});
			case 1:
				if(!player.getInventory().containsItem(15361)) {
					addNPC(DUKEHORACIO, HeadE.CALM_TALK, "Have you lost the talisman?");
					addPlayer(HeadE.SAD, "Yes...");
					addSimple("The Duke hands you another talisman");
					addNext(() -> {
						player.getInventory().addItem(new Item(15361, 1), true);
					});
				} else {
					addNPC(DUKEHORACIO, HeadE.CALM_TALK, "Take this talisman to wizard's tower");
					addPlayer(HeadE.HAPPY_TALKING, "Of course!");
				}
				break;
			default:
				addNPC(DUKEHORACIO, HeadE.CALM_TALK, "Thank you for your help adventurer!");
				addPlayer(HeadE.CALM_TALK, "No problem");
				break;

			}
			create();
		}
	}

	public static NPCClickHandler handleSedridor = new NPCClickHandler(new Object[]{300}, new String[]{"Talk-to"}, e -> {
		e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
			{
				addNPC(e.getNPCId(), HeadE.CALM, "Welcome, adventurer, to the world-renowned Wizard's Tower. How may I help you?");
				addOptions(new Options() {
					@Override
					public void create() {
						if (player.getQuestManager().getStage(Quest.RUNE_MYSTERIES) == 1)
							if (player.getInventory().containsItem(15361))
								option(" I'm looking for the head wizard.", new Dialogue()
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "That's me, but why would you be doing that?")
										.addPlayer(HeadE.CALM_TALK, "The Duke of Lumbridge sent me to find him...er, you. I have a weird talisman that the " +
												"Duke found. He said the head wizard would be interested in it.")
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "Did he now? Well, that IS interesting, Hand it over, then, adventurer – let me see what " +
												"all the hubbub is about. Just some crude amulet, I'll wager.")
										.addPlayer(HeadE.CALM_TALK, "Okay, here you go.")
										.addSimple("You give the talisman to the wizard.")
										.addNPC(SEDRIDOR, HeadE.AMAZED, "Wow! This is incredible! Th-this talisman you brought me...it is the last piece of the " +
												"puzzle. Finally!")
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "I require a specialist to investigate the talisman. Will you take it to him?")
										.addOptions(new Options() {
											@Override
											public void create() {
												option("Yes, certainly", new Dialogue()
														.addNPC(SEDRIDOR, HeadE.CALM_TALK, "Take this package to Varrock, the large city north of Lumbridge. " +
																"Aubury's rune shop is in the south-east quarter. ")
														.addNPC(SEDRIDOR, HeadE.CALM_TALK, "He will give you a special item. Bring it back to me and I will " +
																"show you the mystery of runes.")
														.addSimple("The head wizard gives you a research package.")
														.addNPC(SEDRIDOR, HeadE.CALM_TALK, "Best of luck with your quest")
														.addNext(() -> {
															player.getInventory().deleteItem(15361, 1);
															player.getInventory().addItem(new Item(290, 1), true);
															player.getQuestManager().setStage(Quest.RUNE_MYSTERIES, 2, true);
														}));
												option("No, i'm busy", () -> {
												});
											}
										}));
							else
								option(" I'm looking for the head wizard.", new Dialogue()
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "That's me, but why would you be doing that?")
										.addPlayer(HeadE.CALM_TALK, "The Duke of Lumbridge sent me to find him...er, you. I have a weird talisman that the " +
												"Duke found. He said the head wizard would be interested in it.")
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "Did he now? Well, that IS interesting, Hand it over, then, adventurer – let me see what " +
												"all the hubbub is about. Just some crude amulet, I'll wager.")
										.addPlayer(HeadE.CALM_TALK, "Darn, I don't have it")
										.addNPC(SEDRIDOR, HeadE.SKEPTICAL_THINKING, "...")
										.addPlayer(HeadE.NO_EXPRESSION, "... ..."));
						if (player.getQuestManager().getStage(Quest.RUNE_MYSTERIES) == 2)
							if (!player.getInventory().containsItem(290))
								option("About that package...", new Dialogue()
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "You lost it?")
										.addPlayer(HeadE.SAD, "Yes...")
										.addSimple("The head wizard gives you a research package.")
										.addNext(() -> {
											player.getInventory().addItem(new Item(290, 1), true);
										}));
						if (player.getQuestManager().getStage(Quest.RUNE_MYSTERIES) == 3)
							if (player.getInventory().containsItem(291))
								option("About Aubury's research notes", new Dialogue()
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "What did Aubury say?")
										.addPlayer(HeadE.SAD, "He gave me some research notes to pass on to you.")
										.addSimple("You give him the notes")
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "You have been nothing but helpful, adventurer. In return, I can let you " +
												"in on the secret of our research. ")
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "we are developing a way to create runes from raw material")
										.addPlayer(HeadE.HAPPY_TALKING, "Oh neat-o!")
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "Yes, we are the only ones to know where the materials are")
										.addPlayer(HeadE.CALM_TALK, "Does that mean that you are the only once who know the teleport spell?")
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "Yes, also, i'm willing to take you there")
										.addSimple("He hands you a air talisman")
										.addNext(() -> {
											player.getInventory().deleteItem(291, 1);
											player.getInventory().addItem(1438, 1);
											player.getQuestManager().completeQuest(Quest.RUNE_MYSTERIES);
										})
								);
							else
								option("About Aubury's research notes", new Dialogue()
										.addNPC(SEDRIDOR, HeadE.CALM_TALK, "What did Aubury say?")
										.addPlayer(HeadE.SAD, "He said to bring research notes but I lost them...")
										.addNPC(SEDRIDOR, HeadE.NO_EXPRESSION, "..."));

						option("What are you doing down here?", new Dialogue()
								.addNPC(SEDRIDOR, HeadE.CALM_TALK, "That is, indeed, a good question. Here in the cellar of the Wizard's Tower " +
										"you find the remains of the old Wizards' Tower,")
								.addNPC(SEDRIDOR, HeadE.CALM_TALK, "destroyed by fire many years past by the treachery of the Zamorakians. " +
										"Many mysteries were lost, which we are trying to rediscover.")
								.addNPC(SEDRIDOR, HeadE.CALM_TALK, " By building this tower on the remains of the old, we seek to show the world " +
										"our dedication to the mysteries of magic.")
								.addNPC(SEDRIDOR, HeadE.CALM_TALK, " I am here sifting through fragments for knowledge of artefacts of our past.")
								.addPlayer(HeadE.CALM_TALK, "Have you found anything useful?")
								.addNPC(SEDRIDOR, HeadE.CALM_TALK, " Ah, that would be telling, adventurer. Anything I have found I cannot speak " +
										"freely of, for fear of the treachery we have already seen once in the past.")
								.addPlayer(HeadE.CALM_TALK, "Okay, well, I'll leave you to it"));
						option("Nothing, thanks. I'm just looking around", () -> {
						});
					}
				});
				create();
			}
		});
	});

	public static NPCClickHandler handleAubury= new NPCClickHandler(new Object[] { 5913 }, new String[] { "Talk-to", "Trade" }, e -> {
		switch(e.getOption()) {
		case "Trade":
			ShopsHandler.openShop(e.getPlayer(), "auburys_rune_shop");
			break;
		case "Talk-to":
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					switch(player.getQuestManager().getStage(Quest.RUNE_MYSTERIES)) {
					case 2:
						addPlayer(HeadE.SAD, "I've been sent here with a package for you. It's from Sedridor, the head wizard at the Wizards' Tower.");
						addNPC(AUBURY, HeadE.CALM_TALK, " Really? Surely he can't have...? Please, let me have it.");
						if(!player.getInventory().containsItem(290)) {
							addPlayer(HeadE.CALM_TALK, "Darn, I don't have it");
							addNPC(AUBURY, HeadE.SKEPTICAL_THINKING, "...");
							addPlayer(HeadE.NO_EXPRESSION, "... ...");
						} else {
							addSimple("You hand Aubury the research package.");
							addNPC(AUBURY, HeadE.CALM_TALK, "My gratitude, adventurer, for bringing me this research package. Combined with the information " +
									"I have already collated regarding rune essence, I think we have finally unlocked the power to... ");
							addNPC(AUBURY, HeadE.CALM_TALK, "No, I'm getting ahead of myself. Take this summary of my research back to Sedridor in " +
									"the basement of the Wizards' Tower. He will know whether or not to let you in on our little secret.");
							addSimple("Aubury gives you his research notes.");
							addNPC(AUBURY, HeadE.CALM_TALK, "Now, I'm sure I can spare a couple of runes for such a worthy cause as these notes. " +
									"Do you want me to teleport you back?");

							addPlayer(HeadE.SAD, "Yes...");

							addOptions("Teleport to Wizard's Tower?", new Options() {
								@Override
								public void create() {
									option("Yes please", new Dialogue()
											.addNext(() -> {
												e.getNPC().setNextForceTalk(new ForceTalk("Senventior Disthine Molenko!"));
												World.sendProjectile(e.getNPC(), e.getPlayer(), 50, 5, 5, 5, 1, 5, 0);
												WorldTasks.schedule(new WorldTask() {
													@Override
													public void run() {
														e.getPlayer().setNextTile(Tile.of(3113, 3175, 0, 0));
													}
												}, 2);
												player.getInventory().deleteItem(290, 1);
												player.getInventory().addItem(new Item(291, 1), true);
												player.getQuestManager().setStage(Quest.RUNE_MYSTERIES, 3, true);
											}));
									option("No thanks", () -> {
										player.getInventory().deleteItem(290, 1);
										player.getInventory().addItem(new Item(291, 1), true);
										player.getQuestManager().setStage(Quest.RUNE_MYSTERIES, 3, true);
									});
								}
							});
						}
						break;
					case 3:
						if(!player.getInventory().containsItem(291)) {
							addPlayer(HeadE.CALM_TALK, "I lost the research notes");
							addNPC(AUBURY, HeadE.CALM_TALK, "I have another");
							addPlayer(HeadE.CALM_TALK, "Thank you!");
						} else {
							addNPC(AUBURY, HeadE.CALM_TALK, "Please get these research notes to Sedridor at the wizards tower");
							addPlayer(HeadE.CALM_TALK, "Of course!");
						}
						break;
					default:
						addNPC(AUBURY, HeadE.CALM_TALK, "Do you want to buy some runes?");
						addOptions("Do you want to buy some runes?", new Options() {
							@Override
							public void create() {
								option("Yes please", new Dialogue()
										.addNext(()->{
											ShopsHandler.openShop(e.getPlayer(), "auburys_rune_shop");
										}));
								option("No thanks", () -> {

								});
								if (e.getPlayer().isQuestComplete(Quest.RUNE_MYSTERIES)) {
									option("Can you teleport me to the rune essence?", () -> {
										if (!e.getPlayer().isQuestComplete(Quest.RUNE_MYSTERIES)) {
											e.getPlayer().sendMessage("You have no idea where this mage might take you if you try that.");
											return;
										}
										RunecraftingAltar.handleEssTele(e.getPlayer(), e.getNPC());
									});
								}
							}
						});
						break;
					}
					create();
				}

			});
			break;
		}
	});
}
