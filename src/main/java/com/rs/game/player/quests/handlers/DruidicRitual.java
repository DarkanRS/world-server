package com.rs.game.player.quests.handlers;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestOutline;
import com.rs.lib.Constants;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

//@QuestHandler(Quest.DORICS_QUEST)
//@PluginEventHandler
public class DruidicRitual extends QuestOutline {
	
	private final static int KAQEMEEX = 284;
	private final static int DRUID = 434;

	@Override
	public int getCompletedStage() {
		return 2;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<String>();
		switch(stage) {
		case 0:
			lines.add("I can start this quest by speaking to Kaqemeex who is at");
			lines.add("the Druids Circle just North of Taverly.");
			lines.add("<br>");
			break;
		case 1:
			lines.add("<str>I have spoken to Kaqemeex.");
			lines.add("<br>");
			lines.add("I need to collect some items and bring them to Doric.");
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

	static class KaqemeexD extends Conversation {
		public KaqemeexD(Player player) {
			super(player);
			
			Dialogue startQuest = new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "I'm in search of a quest")
					.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Hmm. I think I may have a worthwhile quest for you actually. I don't know if you are familiar with the stone circle south of Varrock or not, but...");
			
			Dialogue darkWizard = new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "What about the stone circle full of dark wizards?")
					.addNPC(KAQEMEEX, HeadE.CALM_TALK, "That used to be OUR stone circle. Unfortunately, many many years ago, dark wizards cast a wicked spell upon it so that they could corrupt its power for their own evil ends.")
					.addNPC(KAQEMEEX, HeadE.CALM_TALK, "When they cursed the rocks for their rituals they made them useless to us and our magics. We require a brave adventurer to go on a quest for us to help purify the circle of Varrock.")
					.addOptions(new Options() {
						@Override
						public void create() {
							option("Okay, I will try and help.", startQuest);
							option("No, that doesn't sound very interesting.", ()->{});
							option("So... is there anything in this for me?", new Dialogue()												
									.addPlayer(HeadE.CALM_TALK, "So... is there anything in this for me?")
									.addNPC(KAQEMEEX, HeadE.CALM_TALK, "We druids value wisdom over wealth, so if you expect material gain, you will be dissapointed. We are, however, very skilled in the art of Herblore, which we will share with you")
									.addNPC(KAQEMEEX, HeadE.CALM_TALK, "if you can assist us in this task. You may find such wisdom a greater reward than mere money.")
									.addOption("Ok, I will try and help.", "No, that doesn't sound very interesting.")
									.addNext(startQuest));
						}
					});
			
			Dialogue exitConvo = new Dialogue()
					.addPlayer(HeadE.CALM_TALK, "Well, I'll be on my way now.")
					.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Goodbye adventurer. I feel we shall meet again.");

			switch(player.getQuestManager().getStage(Quest.DORICS_QUEST)) {
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
										option("What about the stone circle full of dark wizards?", darkWizard);
										option("So what's so good about Guthix?", new Dialogue()
												.addPlayer(HeadE.CALM_TALK, "So what's so good about Guthix?")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Guthix is the oldest and most powerful god in Gielinor. His existence is vital to this world. He is the god of balance, and nature; he is also a very part of this world.")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "He exists in the trees, and the flowers, the water and the rocks. He is everywhere. His purpose is to ensure balance in everything in this world, and as such we worship him.")
												.addPlayer(HeadE.CALM_TALK, "He sounds kind of boring...")
												.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Some day when your mind achieves enlightenment you will see the true beauty of his power."));
										option("Well, I'll be on my way now.");
									}
								}));
						option("I'm in search of a quest.", startQuest);
						option("Did you build this?", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "Did you build this?")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "What, personally? No, of course I didn't. However, our forefathers did. The first Druids of Guthix built many stone circles across these lands over eight hundred years ago.")
								.addNPC(KAQEMEEX, HeadE.CALM_TALK, "Unfortunately we only know of two remaining, and of those only one is usable by us anymore.")
								.addOptions(new Options() {
									@Override
									public void create() {
										option("What about the stone circle full of dark wizards?", darkWizard);
										option("I'm in search of a quest", new Dialogue()
												.addNext(startQuest));
										option("Well, I'll be on my way now.", exitConvo); 
									}
								}));
					}
				});
				break;
//			case 1:
//				addNPC(DORIC, HeadE.CALM_TALK, "Have you got my materials yet, traveller?");
//				if (player.getInventory().containsItems(new int[] { CLAY, COPPER_ORE, IRON_ORE }, new int[] { 6, 4, 2 })) {
//					addPlayer(HeadE.CALM_TALK, "I have everything you need.");
//					addNPC(DORIC, HeadE.CALM_TALK, "Many thanks! Pass them here, please. I can spare you some coins for your trouble, and please use my anvils any time you want.");
//					addItem(COPPER_ORE, "You hand the clay, copper, and iron to Doric.");
//					addNext(() -> {
//						player.getInventory().deleteItem(CLAY, 6);
//						player.getInventory().deleteItem(COPPER_ORE, 4);
//						player.getInventory().deleteItem(IRON_ORE, 2);
//						player.getQuestManager().completeQuest(Quest.DORICS_QUEST);
//					});
//				} else {
//					addPlayer(HeadE.CALM_TALK, "Sorry, I don't have them all yet.");
//					addNPC(DORIC, HeadE.CALM_TALK, "Not to worry, stick at it. Remember, I need 6 clay, 4 copper ore, and 2 iron ore.");
//					addPlayer(HeadE.CALM_TALK, "Where can I find those?");
//					addNPC(DORIC, HeadE.CALM_TALK, "You'll be able to find all those ores in the rocks just inside the Dwarven Mine. Head east from here and you'll find the entrance in the side of Ice Mountain.");
//				}
//				break;
//			case 2:
//				addNPC(DORIC, HeadE.CALM_TALK, "Hello traveller, how is your metalworking coming along?");
//				addPlayer(HeadE.CALM_TALK, "Not too bad, Doric.");
//				addNPC(DORIC, HeadE.CALM_TALK, "Good, the love of metal is a thing close to my heart.");
//				break;
			}
			create();
		}
	}
	
	public static NPCClickHandler kaqemeexHandler = new NPCClickHandler(KAQEMEEX) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new KaqemeexD(e.getPlayer()));
		}
	};
	
	public static NPCClickHandler druidHandler = new NPCClickHandler(DRUID) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().equalsIgnoreCase("talk-to"))
				e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.CALM_TALK, "Hello.").addNPC(DRUID, HeadE.CALM_TALK, "Good day to you, may nature smile upon you."));
		}
	};
}
