package com.rs.game.content.quests.familycrest.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.familycrest.FamilyCrest.*;

@PluginEventHandler
public class DimintheisFamilyCrestD extends Conversation {
	private final int QUEST_INQUIRY = 0;
	private static final int NPC = 664;
	public DimintheisFamilyCrestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello. My name is Dimintheis, of the noble family Fitzharmon.");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Why would a nobleman live in a dump like this?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Why would a nobleman live in a dump like this?")
							.addNPC(NPC, HeadE.CALM_TALK, "The King has taken my estate from me until such time as I can show my family crest to him.")
							.addPlayer(HeadE.HAPPY_TALKING, "Why would he do that?")
							.addNPC(NPC, HeadE.CALM_TALK, "Well, there is a long standing rule of chivalry amongst the Varrockian aristocracy, " +
									"where each noble family is in possession of a unique crest, which signifies the honour and lineage of the family.")
							.addNPC(NPC, HeadE.CALM_TALK, "More than this however, it also represents the lawful rights of each family to " +
									"prove their ownership of their wealth and lands. ")
							.addNPC(NPC, HeadE.CALM_TALK, "If the family crest is lost, then the family's estate is handed over to the " +
									"current monarch until the crest is restored.")
							.addNPC(NPC, HeadE.CALM_TALK, "This dates back to the times when there was much in-fighting amongst the noble " +
									"families and their clans")
							.addNPC(NPC, HeadE.CALM_TALK, "and was introduced as a way of reducing the bloodshed that was devastating the " +
									"ranks of the ruling classes at that time.")
							.addNPC(NPC, HeadE.CALM_TALK, "When you captured a rival family's clan, you also captured their lands and wealth.")
							.addNext(()->{
								player.startConversation(new DimintheisFamilyCrestD(player, QUEST_INQUIRY).getStart());
							})

							);
					option("You're rich then? Can I have some money?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "You're rich then? Can I have some money?")
							.addNPC(NPC, HeadE.CALM_TALK, "Gah! Lousy beggar! Your sort is what's ruining this great land! Why don't you just go and get a " +
									"job if you need money so badly?")
							);
					option("Hi, I am a bold adventurer", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Hi, I am a bold adventurer")
							.addNPC(NPC, HeadE.CALM_TALK, "An adventurer hmmm? How lucky. I may have an adventure for you. I desperately need my family " +
									"crest returning to me. It is of utmost importance.")
							.addOptions("Choose an option:", new Options() {
								@Override
								public void create() {
									option("Why are you so desperate for it?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Why are you so desperate for it?")
											.addNPC(NPC, HeadE.CALM_TALK, "Well, there is a long standing rule of chivalry amongst the Varrockian aristocracy, " +
													"where each noble family is in possession of a unique crest, which signifies the honour and lineage of the family.")
											.addNPC(NPC, HeadE.CALM_TALK, "More than this however, it also represents the lawful rights of each family to " +
													"prove their ownership of their wealth and lands. ")
											.addNPC(NPC, HeadE.CALM_TALK, "If the family crest is lost, then the family's estate is handed over to the " +
													"current monarch until the crest is restored.")
											.addNPC(NPC, HeadE.CALM_TALK, "This dates back to the times when there was much in-fighting amongst the noble " +
													"families and their clans")
											.addNPC(NPC, HeadE.CALM_TALK, "and was introduced as a way of reducing the bloodshed that was devastating the " +
													"ranks of the ruling classes at that time.")
											.addNPC(NPC, HeadE.CALM_TALK, "When you captured a rival family's clan, you also captured their lands and wealth.")
											.addNext(()->{
												player.startConversation(new DimintheisFamilyCrestD(player, QUEST_INQUIRY).getStart());
											})
											);
									option("So where is this crest?", new Dialogue()
											.addNext(()->{
												player.startConversation(new DimintheisFamilyCrestD(player, QUEST_INQUIRY).getStart());
											})
											);
									option("I'm not interested in that adventure right now", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "I'm not interested in that adventure right now")
											.addNPC(NPC, HeadE.CALM_TALK, "I realise it was a lot to ask of a stranger")
											);
								}
							})
							);
				}
			});

		}
		case TALK_TO_CALEB, TALK_TO_GEM_TRADER, TALK_TO_AVAN, TALK_TO_BOOT, GIVE_AVAN_JEWLERY, TALK_TO_JOHNATHAN -> {
			addNPC(NPC, HeadE.CALM_TALK, "If you find Caleb, or my other sons... please... let them know their father still loves them...");
			addPlayer(HeadE.HAPPY_TALKING, "Okay.");
		}
		case KILL_CHRONOZON -> {
			if(player.getInventory().containsItem(FAMILY_CREST, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "I have retrieved your crest.");
				addNPC(NPC, HeadE.CALM_TALK, "Adventurer... I can only thank you for your kindness, although the words are insufficient to express the gratitude I feel!");
				addNPC(NPC, HeadE.CALM_TALK, "You are truly a hero in every sense, and perhaps your efforts can begin to patch the wounds that have torn this family apart...");
				addNPC(NPC, HeadE.CALM_TALK, "I know not how I can adequately reward you for your efforts... although I do have these mystical gauntlets, a family heirloom that through some power unknown to me, have always returned to the head of the family whenever lost, or if the owner has died.");
				addNPC(NPC, HeadE.CALM_TALK, "I will pledge these to you, and if you should lose them return to me, and they will be here.");
				addNPC(NPC, HeadE.CALM_TALK, "They can also be granted extra powers. Take them to one of my sons, they should be able to imbue them with a skill for you.");
				addNext(()-> {
					player.getInventory().removeItems(new Item(FAMILY_CREST,1));
					player.getQuestManager().completeQuest(Quest.FAMILY_CREST);
				});
				return;
			}
			addNPC(NPC, HeadE.CALM_TALK, "If you find Caleb, or my other sons... please... let them know their father still loves them...");
			addPlayer(HeadE.HAPPY_TALKING, "Okay.");
		}
		case QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "Adventurer... I can only thank you for your kindness, although the words are insufficient to express the gratitude I feel!");
			addNPC(NPC, HeadE.CALM_TALK, "You are truly a hero in every sense, and perhaps your efforts can begin to patch the wounds that have torn this family apart...");
			if(player.getInventory().containsItem(FAMILY_GAUNTLETS, 1)) {
				addNPC(NPC, HeadE.CALM_TALK, "Please, enjoy those gauntlets.");
				addPlayer(HeadE.HAPPY_TALKING, "..");
				return;
			}
			if(player.getInventory().hasFreeSlots()) {
				addNPC(NPC, HeadE.CALM_TALK, "I found the family gauntlets, please take them...");
				addSimple("He hands you the gauntlets.", () -> {
					player.getInventory().addItem(FAMILY_GAUNTLETS, 1);
				});
				addPlayer(HeadE.HAPPY_TALKING, "...");
			} else {
				addNPC(NPC, HeadE.CALM_TALK, "I found the family gauntlets, if you had room for them...");
				addPlayer(HeadE.HAPPY_TALKING, "...");
			}
		}
		}
	}

	public DimintheisFamilyCrestD(Player p, int id) {
		super(p);
		switch(id) {
		case QUEST_INQUIRY -> {
			if(meetsRequirements(p)) {
				addPlayer(HeadE.HAPPY_TALKING, "So where is this crest?");
				addNPC(NPC, HeadE.CALM_TALK, "Well, my three sons took it with them many years ago when they rode out to fight in the war against the undead " +
						"necromancer and his army in the battle to save Varrock.");
				addNPC(NPC, HeadE.CALM_TALK, "For many years I had assumed them all dead, as I had heard no word from them.");
				addNPC(NPC, HeadE.CALM_TALK, "Recently I heard that my son Caleb is alive and well, trying to earn his fortune as a great fish chef.");
				addNPC(NPC, HeadE.CALM_TALK, "I believe he is staying with a friend who lives just outside the west gates of Varrock.");
				addOptions("Start Family Crest?", new Options() {
					@Override
					public void create() {
						option("Yes", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Ok, I will help you", () -> {
									p.getQuestManager().setStage(Quest.FAMILY_CREST, TALK_TO_CALEB);
								})
								.addNPC(NPC, HeadE.CALM_TALK, "I thank you greatly adventurer!")
								.addNPC(NPC, HeadE.CALM_TALK, "If you find Caleb, or my other sons... please... let them know their father still loves them...")
								);
						option("No", new Dialogue()
								.addPlayer(HeadE.CALM_TALK, "I'm not interested in that adventure right now.")
								.addNPC(NPC, HeadE.SAD, "I realise it was a lot to ask of a stranger.")
								);
					}
				});
			} else {
				addSimple("You do not meet the requirements for Family Crest...");
				addSimple("You need 40 mining, 40 smithing, 59 magic and 40 crafting.");
			}

		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new DimintheisFamilyCrestD(e.getPlayer()).getStart()));
}
