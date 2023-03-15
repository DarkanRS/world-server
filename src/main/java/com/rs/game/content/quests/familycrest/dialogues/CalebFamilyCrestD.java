package com.rs.game.content.quests.familycrest.dialogues;

import static com.rs.game.content.quests.familycrest.FamilyCrest.*;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class CalebFamilyCrestD extends Conversation {
	private static final int NPC = 666;

	private static final int SWORDFISH = 373;
	private static final int BASS = 365;
	private static final int TUNA = 361;
	private static final int SALMON = 329;
	private static final int SHRIMP = 315;

	private boolean hasFish() {
		if(player.getInventory().containsItems(new int[]{SWORDFISH, BASS, TUNA, SALMON, SHRIMP}, new int[]{1, 1, 1, 1, 1}))
			return true;
		return false;
	}
	private void removeFish() {
		player.getInventory().removeItems(new Item(SWORDFISH, 1), new Item(BASS, 1), new Item(TUNA, 1), new Item(SALMON, 1),
				new Item(SHRIMP, 1));
	}

	public CalebFamilyCrestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "Who are you? What are you after?");
			addPlayer(HeadE.HAPPY_TALKING, "I see you are a chef... could you cook me anything?");
			addNPC(NPC, HeadE.CALM_TALK, "I would, but I am very busy. I am trying to increase my renown as one of the world's leading chefs by preparing " +
					"a special and unique fish salad.");
		}
		case TALK_TO_CALEB -> {
			if(player.getQuestManager().getAttribs(Quest.FAMILY_CREST).getB("CALEB_ASKED")) {
				if(hasFish()) {
					addNPC(NPC, HeadE.CALM_TALK, "How is the fish collecting going?");
					addPlayer(HeadE.HAPPY_TALKING, "Got them all with me.");
					addSimple("You exchange the fish for Caleb's piece of the crest.", ()-> {
						removeFish();
						player.getInventory().addItem(CALEB_CREST, 1);
						player.getQuestManager().setStage(Quest.FAMILY_CREST, TALK_TO_GEM_TRADER);
					});
					addPlayer(HeadE.HAPPY_TALKING, "Uh... what happened to the rest of it?");
					addNPC(NPC, HeadE.CALM_TALK, "Well... my brothers and I had a slight disagreement about it... we all wanted to be the heir of my " +
							"father's lands, and we each ended up with a piece of the crest.");
					addNPC(NPC, HeadE.CALM_TALK, "None of us wanted to give up our rights to our brothers, so we didn't want to give up our pieces of the " +
							"crest, but none of us wanted to face our father by returning to him with an incomplete crest. ");
					addNPC(NPC, HeadE.CALM_TALK, "We each went our separate ways many years past, none of us seeing our father or willing to give up our fragments.");
					addPlayer(HeadE.HAPPY_TALKING, "So do you know where I could find any of your brothers?");
					addNPC(NPC, HeadE.CALM_TALK, "Well, we haven't really kept in touch... what with the dispute over the crest and all... I did hear from" +
							" my brother Avan a while ago though..");
					addNPC(NPC, HeadE.CALM_TALK, "He said he was on some kind of search for treasure, or gold, or something out near Al Kharid somewhere. " +
							"Avan always had expensive tastes, so you might try asking the gem trader for his wherebouts.");
					addNPC(NPC, HeadE.CALM_TALK, "Be warned though. Avan is quite greedy, and you may find he is not prepared to hand over his crest piece " +
							"to you as easily as I have.");
				}
				else {
					addNPC(NPC, HeadE.CALM_TALK, "How is the fish collecting going?");
					addPlayer(HeadE.HAPPY_TALKING, "I didn't manage to get them all yet...");
					addNPC(NPC, HeadE.CALM_TALK, "Remember, I want the following cooked fish: Swordfish, Bass, Tuna, Salmon and Shrimp.");
				}
				return;
			}
			addNPC(NPC, HeadE.CALM_TALK, "Who are you? What are you after?");
			addPlayer(HeadE.HAPPY_TALKING, "Are you Caleb Fitzharmon?");
			addNPC(NPC, HeadE.CALM_TALK, "Why... yes I am, but I don't believe I know you... how did you know my name?");
			addPlayer(HeadE.HAPPY_TALKING, "I have been sent by your father. He wishes the Fitzharmon Crest to be restored.");
			addNPC(NPC, HeadE.CALM_TALK, "Ah... well... hmmm... yes... I do have a piece of it anyway...");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Uh... what happened to the rest of it?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Uh... what happened to the rest of it?")
							.addNPC(NPC, HeadE.CALM_TALK, "Well... my brothers and I had a slight disagreement about it... we all wanted to be heir to " +
									"my fathers' lands, and we each ended up with a piece of the crest.")
							.addNPC(NPC, HeadE.CALM_TALK, "None of us wanted to give up our rights to our brothers, so we didn't want to give up our pieces " +
									"of the crest, but none of us wanted to face our father by returning to him with an incomplete crest...")
							.addNPC(NPC, HeadE.CALM_TALK, "We each went our separate ways many years past, none of us seeing our father or willing to give " +
									"up our fragments.")
							);
					option("So can I have your bit?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "So can I have your bit?")
							.addNPC(NPC, HeadE.CALM_TALK, "Well, I am the oldest son, so by the rules of chivalry, I am most entitled to be the rightful " +
									"bearer of the crest.")
							.addPlayer(HeadE.HAPPY_TALKING, "It's not really much use without the other fragments is it though?")
							.addNPC(NPC, HeadE.CALM_TALK, "Well that is true... perhaps it is time to put my pride aside... I'll tell you what: I'm struggling " +
									"to complete this fish salad of mine")
							.addNPC(NPC, HeadE.CALM_TALK, "so if you will assist me in my search for the ingredients, then I will let you take my piece as " +
									"reward for your assistance.")
							.addPlayer(HeadE.HAPPY_TALKING, "So what ingredients are you missing?")
							.addNPC(NPC, HeadE.CALM_TALK, "I require the following cooked fish: Swordfish, Bass, Tuna, Salmon and Shrimp.")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("Ok, I will get those", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Ok, I will get those", ()->{
												player.getQuestManager().getAttribs(Quest.FAMILY_CREST).setB("CALEB_ASKED", true);
											})
											.addNPC(NPC, HeadE.CALM_TALK, "You will? It would help me a lot!")
											);
									option("Why don't you just give me the crest?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Why don't you just give me the crest?")
											.addNPC(NPC, HeadE.CALM_TALK, "It's a valuable family heirloom. I think the least you can do is prove you're worthy of it before I hand it over.")
											);
								}
							})
							);
				}
			});
		}
		case TALK_TO_GEM_TRADER -> {
			if(!player.getInventory().containsItem(CALEB_CREST, 1) && !player.getInventory().containsItem(FAMILY_CREST)) {
				if(player.getInventory().hasFreeSlots()) {
					addNPC(NPC, HeadE.CALM_TALK, "You lost this.");
					addSimple("Caleb gives you back his crest...", () -> {
						player.getInventory().addItem(CALEB_CREST, 1);
					});
				} else {
					addNPC(NPC, HeadE.CALM_TALK, "I have my crest if you had room for it");
					addPlayer(HeadE.HAPPY_TALKING, "Okay.");
				}
				return;
			}
			addPlayer(HeadE.HAPPY_TALKING, "Where did you say I could find Avan again?");
			addNPC(NPC, HeadE.CALM_TALK, "Last I heard he was on some stupid treasure hunt out in the desert somewhere. Your best bet is asking around there.");
			addNPC(NPC, HeadE.CALM_TALK, "Try asking a gem trader.");
			addPlayer(HeadE.CALM_TALK, "Okay.");
		}
		case TALK_TO_AVAN, TALK_TO_BOOT, GIVE_AVAN_JEWLERY, TALK_TO_JOHNATHAN, KILL_CHRONOZON -> {
			if(!player.getInventory().containsItem(CALEB_CREST, 1)) {
				if(player.getInventory().hasFreeSlots()) {
					addNPC(NPC, HeadE.CALM_TALK, "You lost this.");
					addSimple("Caleb gives you back his crest...", () -> {
						player.getInventory().addItem(CALEB_CREST, 1);
					});
				} else {
					addNPC(NPC, HeadE.CALM_TALK, "I have my crest if you had room for it");
					addPlayer(HeadE.HAPPY_TALKING, "Okay.");
				}
				return;
			}
			addNPC(NPC, HeadE.CALM_TALK, "How are you doing getting the crest pieces?");
			addPlayer(HeadE.HAPPY_TALKING, "I am still working on it.");
			addNPC(NPC, HeadE.CALM_TALK, "Then why are you wasting your time here?");
		}
		case QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "I hear you brought the completed crest to my father. I must say, that was awfully impressive work.");
			if(player.getInventory().containsItem(FAMILY_GAUNTLETS, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "I believe your father mentioned you would be able to improve these gauntlets for me...");
				addNPC(NPC, HeadE.CALM_TALK, "Yes, that is correct. I can slightly alter these gauntlets to allow you some of my skill at preparing seafood by " +
						"making them cooking gauntlets.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Yes, please do that for me", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Yes, please do that for me.")
								.addSimple("Caleb takes the gauntlets from you, pours some herbs and seasonings on them, bakes them on his range " +
										"for a short time, then hands them back to you.", () -> {
											player.getInventory().removeItems(new Item(FAMILY_GAUNTLETS, 1));
											player.getInventory().addItem(COOKING_GAUNTLETS, 1);
										})
								);
						option("I'll see what your brothers have to offer", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'll see what your brothers have to offer")
								.addNPC(NPC, HeadE.CALM_TALK, "As you wish. Let me know if you change your mind.")
								);
					}
				});
			} else
				addPlayer(HeadE.HAPPY_TALKING, "Thanks!");

		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new CalebFamilyCrestD(e.getPlayer()).getStart()));
}
