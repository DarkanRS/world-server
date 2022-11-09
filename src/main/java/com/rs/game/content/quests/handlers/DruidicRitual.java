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
package com.rs.game.content.quests.handlers;

import java.util.ArrayList;

import com.rs.game.content.Skillcapes;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@QuestHandler(Quest.DRUIDIC_RITUAL)
@PluginEventHandler
public class DruidicRitual extends QuestOutline {
	private final static int KAQEMEEX = 455;
	private final static int SANFEW = 454;

	final static int RAW_BEAR_MEAT = 2136;
	final static int RAW_RAT_MEAT = 2134;
	final static int RAW_CHICKEN = 2138;
	final static int RAW_BEEF = 2132;

	final static int ENCHANTED_RAW_BEAR_MEAT = 524;
	final static int ENCHANTED_RAW_RAT_MEAT = 523;
	final static int ENCHANTED_RAW_CHICKEN = 525;
	final static int ENCHANTED_RAW_BEEF = 522;

	@Override
	public int getCompletedStage() {
		return 4;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Kaqemeex who is at");
			lines.add("the Druids Circle just North of Taverly.");
			lines.add("");
			break;
		case 1:
			lines.add("I must speak to Sanfew south of the monument.");
			lines.add("");
			break;
		case 2:
			lines.add("Sanfew told me to get raw rat, chicken, beef and bear meat");
			lines.add("Im to take them to the cauldron of thunder and dip them in.");
			lines.add("Afterwards I should take all four back to Sanfew");
			lines.add("");
			lines.add("I can find the cauldron of thunder in Taverly Dungeon");
			lines.add("It is at the first gate northeast of the dungeon entrance");
			lines.add("");
			break;
		case 3:
			lines.add("I must speak to Kaqemeex at the monument");
			break;
		case 4:
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
		player.getSkills().addXpQuest(Skills.HERBLORE, 250);
		player.getInventory().addItem(200, 15);
		player.getInventory().addItem(222, 15);
		getQuest().sendQuestCompleteInterface(player, 195, "250 Herblore XP", "15 Grimy Guam and 15 eye of newt");
	}



	static class KaqemeexD extends Conversation {
		public KaqemeexD(Player player) {
			super(player);

			switch(player.getQuestManager().getStage(Quest.DRUIDIC_RITUAL)) {
			case 0:
				addPlayer(HeadE.CALM_TALK, "Hello there.");
				addNPC(KAQEMEEX, HeadE.CALM_TALK, "What brings you to our holy monument?");
				addOptions(new Options() {
					@Override
					public void create() {
						option("Who are you?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Who are you?")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "We are the druids of Guthix. We worship our god at our famous stone circles. You will find them located throughout these lands.")
								.addOptions(new Options() {
									@Override
									public void create() {
										option("So what's so good about Guthix?", new Dialogue()
												.addPlayer(HeadE.CALM_TALK, "So what's so good about Guthix?")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Guthix is the oldest and most powerful god in Gielinor. His existence is vital to this world. He is the god of balance, and nature; he is also a very part of this world.")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "He exists in the trees, and the flowers, the water and the rocks. He is everywhere. His purpose is to ensure balance in everything in this world, and as such we worship him.")
												.addPlayer(HeadE.CALM_TALK, "He sounds kind of boring...")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Some day when your mind achieves enlightenment you will see the true beauty of his power."));
										option("Well, I'll be on my way now.");
									}
								}));
						option("I'm in search of a quest.",  new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I'm in search of a quest")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Hmm. I think I may have a worthwhile quest for you actually. I don't know if you are familiar with the stone circle south of Varrock or not, but...")
								.addPlayer(HeadE.CALM_TALK, "What about the stone circle full of dark wizards?")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "That used to be OUR stone circle. Unfortunately, many many years ago, dark wizards cast a wicked spell upon it so that they could corrupt its power for their own evil ends.")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "When they cursed the rocks for their rituals they made them useless to us and our magics. We require a brave adventurer to go on a quest for us to help purify the circle of Varrock.")
								.addOptions(new Options() {
									@Override
									public void create() {
										option("Okay, I will try and help.", new Dialogue()
												.addPlayer(HeadE.CALM_TALK, "Okay, I will try and help.")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Excellent. Go to the village south of this place and speak to my fellow " +
														"Sanfew who is working on the purification ritual. He knows better than I what is required to complete it.")
												.addPlayer(HeadE.CALM_TALK, "Will do.")
												.addNext(()->{
													player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 1, true);
												}));
										option("No, that doesn't sound very interesting.", ()->{});
									}
								}));
						option("What is that cape you're wearing?", new Dialogue()
								.addNext(Skillcapes.Herblore.getOffer99CapeDialogue(player, KAQEMEEX)));
					}
				});
				break;
			case 1:
			case 2:
				addPlayer(HeadE.CALM_TALK, "Hello there.");
				addNPC(KAQEMEEX, HeadE.CALM_TALK, "What brings you to our holy monument?");
				addOptions(new Options() {
					@Override
					public void create() {
						option("Who are you?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Who are you?")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "We are the druids of Guthix. We worship our god at our famous stone circles. You will find them located throughout these lands.")
								.addOptions(new Options() {
									@Override
									public void create() {
										option("So what's so good about Guthix?", new Dialogue()
												.addPlayer(HeadE.CALM_TALK, "So what's so good about Guthix?")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Guthix is the oldest and most powerful god in Gielinor. His existence is vital to this world. He is the god of balance, and nature; he is also a very part of this world.")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "He exists in the trees, and the flowers, the water and the rocks. He is everywhere. His purpose is to ensure balance in everything in this world, and as such we worship him.")
												.addPlayer(HeadE.CALM_TALK, "He sounds kind of boring...")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Some day when your mind achieves enlightenment you will see the true beauty of his power."));
										option("Well, I'll be on my way now.");
									}
								}));
						option("About druidic ritual",  new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "What did you want me to do again?")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Go to the village south of this place and speak to my fellow " +
										"Sanfew who is working on the purification ritual. He knows better than I what is required to complete it.")
								.addPlayer(HeadE.CALM_TALK, "Will do."));
						option("What is that cape you're wearing?", new Dialogue()
								.addNext(Skillcapes.Herblore.getOffer99CapeDialogue(player, KAQEMEEX)));
					}
				});
				break;
			case 3:
				addPlayer(HeadE.CALM_TALK, "Hello there.");
				addNPC(KAQEMEEX, HeadE.CALM_TALK, "I have word from Sanfew that you have been very helpful in assisting him with his preparations for " +
						"the purification ritual. As promised I will now teach you the ancient arts of Herblore.");
				addNext(()->{
					player.getQuestManager().completeQuest(Quest.DRUIDIC_RITUAL);
				});
				break;
			default:
				addPlayer(HeadE.CALM_TALK, "Hello there.");
				addOptions(new Options() {
					@Override
					public void create() {
						option("Can you explain herblore?", new Dialogue()
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Herblore is the skill of working with herbs and other ingredients, to make useful potions and poison. ")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "First you will need a vial, which can be found or made with the crafting skill. Then you must gather the herbs needed to make the potion you want.")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Refer to the Council's instructions in the Skills section of the website for the items needed to make a particular kind of potion. You must fill the vial with water and add the ingredients you need.")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "There are normally 2 ingredients to each type of potion. Bear in mind, you must first identify each herb, to see what it is. You may also have to grind some herbs before you can use them.")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "You will need a pestle and mortar in order to do this. Herbs can be found on the ground, and are also dropped by some monsters when you kill them. ")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Let's try an example Attack potion: The first ingredient is Guam leaf; the next is Eye of Newt. Mix these in your water-filled vial and you will produce an Attack potion. ")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Drink this potion to increase your Attack level. Different potions also require different Herblore levels before you can make them. ")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Once again, check the instructions found on the Council's website for the levels needed to make a particular potion.Good luck with your Herblore practices, Good day Adventurer.")
								.addPlayer(HeadE.CALM_TALK, "Thanks for your help."));
						option("What is that cape you're wearing?", new Dialogue()
								.addNext(Skillcapes.Herblore.getOffer99CapeDialogue(player, KAQEMEEX)));
					}
				});
				break;
			}
			create();
		}
	}

	static class SanfewD extends Conversation {
		static boolean hasEnchantedItems(Player player) {
			boolean hasItems = false;
			if(player.getInventory().containsItem(ENCHANTED_RAW_BEAR_MEAT) && player.getInventory().containsItem(ENCHANTED_RAW_RAT_MEAT) &&
					player.getInventory().containsItem(ENCHANTED_RAW_CHICKEN) && player.getInventory().containsItem(ENCHANTED_RAW_BEEF))
				hasItems = true;
			return hasItems;
		}

		public SanfewD(Player player) {
			super(player);

			switch(player.getQuestManager().getStage(Quest.DRUIDIC_RITUAL)) {
			case 1:
				addPlayer(HeadE.CALM_TALK, "Hello there.");
				addNPC(SANFEW, HeadE.CALM_TALK, "What can I do for you young 'un?", () -> {
					player.voiceEffect(77263);
				});
				addPlayer(HeadE.CALM_TALK, "I've been sent to assist you with the ritual to purify the Varrockian stone circle.", ()->{
					player.getPackets().resetSounds();
				});
				addNPC(SANFEW, HeadE.CALM_TALK, "Well, what I'm struggling with right now is the meats needed for the potion to honour Guthix. I need the raw" +
						" meats of four different animals for it, ");
				addNPC(SANFEW, HeadE.CALM_TALK, "but not just any old meats will do. Each meat has to be dipped individually into the Cauldron of Thunder " +
						"for it to work correctly.");
				addNPC(SANFEW, HeadE.CALM_TALK, "I will need 4 raw meats put into the cauldron. They are rat, bear, beef and chicken");
				addOptions(new Options() {
					@Override
					public void create() {
						option("Where can I find this cauldron?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Where can I find this cauldron?")
								.addNPC(SANFEW, HeadE.CALM_TALK, "It is located somewhere in the mysterious underground halls which are located somewhere" +
										" in the woods just South of here. They are too dangerous for me to go myself however.")
								.addPlayer(HeadE.CALM_TALK, "Ok, I'll go do that then.", () -> {
									player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 2, true);
								}));
						option("Ok, I'll go do that then.",  () -> {
							player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 2, true);
						});
					}
				});
				break;
			case 2:
				addNPC(SANFEW, HeadE.CALM_TALK, "Did you bring me the required ingredients for the potion?");
				if (hasEnchantedItems(player)) {
					addPlayer(HeadE.CALM_TALK, "Yes, I have all four now!");
					addNPC(SANFEW, HeadE.CALM_TALK, "Well hand 'em over then lad! Thank you so much adventurer! These meats will allow our potion to honour " +
							"Guthix to be completed, and bring one step closer to reclaiming our stone circle! ");
					addNPC(SANFEW, HeadE.CALM_TALK, "Now go and talk to SANFEW and he will introduce you to the wonderful world of herblore and " +
							"potion making!",  () -> {
								player.getInventory().deleteItem(ENCHANTED_RAW_BEAR_MEAT, 1);
								player.getInventory().deleteItem(ENCHANTED_RAW_CHICKEN, 1);
								player.getInventory().deleteItem(ENCHANTED_RAW_RAT_MEAT, 1);
								player.getInventory().deleteItem(ENCHANTED_RAW_BEEF, 1);
								player.getQuestManager().setStage(Quest.DRUIDIC_RITUAL, 3, true);
							});

				} else {
					addPlayer(HeadE.CALM_TALK, "No, not yet...");
					addNPC(SANFEW, HeadE.CALM_TALK, "Well, let me know when you do young 'un.");
					addPlayer(HeadE.CALM_TALK, "I'll get on with it.");
				}
				break;
			default:
				addNPC(SANFEW, HeadE.CALM_TALK, "What can I do for you young 'un?", ()->{
					player.voiceEffect(77263);
				});
				addPlayer(HeadE.CALM_TALK, "Nothing at the moment.", ()->{

				});
				break;
			}
			create();
		}
	}

	public static NPCClickHandler kaqemeexHandler = new NPCClickHandler(new Object[] { KAQEMEEX }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new KaqemeexD(e.getPlayer()));
		}
	};

	public static NPCClickHandler sanfewHandler = new NPCClickHandler(new Object[] { SANFEW }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new SanfewD(e.getPlayer()));
		}
	};

	public static ItemOnObjectHandler handleCauldron = new ItemOnObjectHandler(new Object[] { 2142 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			Player player = e.getPlayer();
			Item item = e.getItem();
			int itemId = item.getId();
			if (itemId == RAW_BEAR_MEAT) {
				if(player.getInventory().containsItem(RAW_BEAR_MEAT) && !player.getInventory().containsItem(ENCHANTED_RAW_BEAR_MEAT)) {
					player.getInventory().deleteItem(RAW_BEAR_MEAT, 1);
					player.getInventory().addItem(ENCHANTED_RAW_BEAR_MEAT);
					return;
				}
				if(player.getInventory().containsItem(ENCHANTED_RAW_BEAR_MEAT)) {
					player.getPackets().sendGameMessage("Enchanted bear meat is already in your inventory");
					return;
				}
			}
			if (itemId == RAW_RAT_MEAT) {
				if(player.getInventory().containsItem(RAW_RAT_MEAT) && !player.getInventory().containsItem(ENCHANTED_RAW_RAT_MEAT)) {
					player.getInventory().deleteItem(RAW_RAT_MEAT, 1);
					player.getInventory().addItem(ENCHANTED_RAW_RAT_MEAT);
					return;
				}
				if(player.getInventory().containsItem(ENCHANTED_RAW_RAT_MEAT)) {
					player.getPackets().sendGameMessage("Enchanted rat meat is already in your inventory");
					return;
				}
			}
			if (itemId == RAW_CHICKEN) {
				if(player.getInventory().containsItem(RAW_CHICKEN) && !player.getInventory().containsItem(ENCHANTED_RAW_CHICKEN)) {
					player.getInventory().deleteItem(RAW_CHICKEN, 1);
					player.getInventory().addItem(ENCHANTED_RAW_CHICKEN);
					return;
				}
				if(player.getInventory().containsItem(ENCHANTED_RAW_CHICKEN))  {
					player.getPackets().sendGameMessage("Enchanted chicken meat is already in your inventory");
					return;
				}
			}
			if (itemId == RAW_BEEF) {
				if(player.getInventory().containsItem(RAW_BEEF) && !player.getInventory().containsItem(ENCHANTED_RAW_BEEF)) {
					player.getInventory().deleteItem(RAW_BEEF, 1);
					player.getInventory().addItem(ENCHANTED_RAW_BEEF);
					return;
				}
				if(player.getInventory().containsItem(ENCHANTED_RAW_BEEF))  {
					player.getPackets().sendGameMessage("Enchanted beef meat is already in your inventory");
					return;
				}
			}

			player.getPackets().sendGameMessage("There is no reason to put this in the cauldron");
		}
	};
}
