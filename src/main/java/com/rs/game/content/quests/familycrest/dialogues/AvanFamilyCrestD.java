package com.rs.game.content.quests.familycrest.dialogues;

import static com.rs.game.content.quests.familycrest.FamilyCrest.AVAN_CREST;
import static com.rs.game.content.quests.familycrest.FamilyCrest.FAMILY_CREST;
import static com.rs.game.content.quests.familycrest.FamilyCrest.FAMILY_GAUNTLETS;
import static com.rs.game.content.quests.familycrest.FamilyCrest.GIVE_AVAN_JEWLERY;
import static com.rs.game.content.quests.familycrest.FamilyCrest.GOLDSMITH_GAUNTLETS;
import static com.rs.game.content.quests.familycrest.FamilyCrest.KILL_CHRONOZON;
import static com.rs.game.content.quests.familycrest.FamilyCrest.NOT_STARTED;
import static com.rs.game.content.quests.familycrest.FamilyCrest.PERFECT_RUBY_NECKLACE;
import static com.rs.game.content.quests.familycrest.FamilyCrest.PERFECT_RUBY_RING;
import static com.rs.game.content.quests.familycrest.FamilyCrest.QUEST_COMPLETE;
import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_AVAN;
import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_BOOT;
import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_CALEB;
import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_GEM_TRADER;
import static com.rs.game.content.quests.familycrest.FamilyCrest.TALK_TO_JOHNATHAN;

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
public class AvanFamilyCrestD extends Conversation {
	private static final int NPC = 663;
	public AvanFamilyCrestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case NOT_STARTED, TALK_TO_CALEB, TALK_TO_GEM_TRADER -> {
			addNPC(NPC, HeadE.CALM_TALK, "What? Can't you see I'm busy?");
			addPlayer(HeadE.HAPPY_TALKING, "Well, sooooorry...");
		}
		case TALK_TO_AVAN -> {
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Why are you lurking around a scorpion pit?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Why are you lurking around a scorpion pit?")
							.addNPC(NPC, HeadE.CALM_TALK, "It's a good place to find gold...")
							);
					option("I'm looking for a man... his name is Avan Fitzharmon.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm looking for a man... his name is Avan Fitzharmon.")
							.addNPC(NPC, HeadE.CALM_TALK, "Then you have found him. My name is Avan Fitzharmon.")
							.addPlayer(HeadE.HAPPY_TALKING, "You have a part of your family crest. I am on a quest to retrieve all of the fragmented " +
									"pieces and restore the crest.")
							.addNPC(NPC, HeadE.CALM_TALK, "Ha! I suppose one of my worthless brothers has sent you on this quest then?")
							.addPlayer(HeadE.HAPPY_TALKING, "No, it was your father who has asked me to do this for him.")
							.addNPC(NPC, HeadE.CALM_TALK, "My... my father wishes this? Then that is a different matter. I will let you have my crest shard, " +
									"adventurer, but you must first do something for me.")
							.addNPC(NPC, HeadE.CALM_TALK, "There is a certain lady I am trying to impress. As a man of noble birth, I can not give her just any gold trinket to show my devotion.")
							.addNPC(NPC, HeadE.CALM_TALK, "What I intend to give her, is a golden ring, embedded with the finest precious red stone available, and a necklace to match this ring")
							.addNPC(NPC, HeadE.CALM_TALK, "The problem however for me, is that not just any old gold will be suitable. I seek only the purest, the most high quality of gold - what I seek, if you will, is perfect gold.")
							.addNPC(NPC, HeadE.CALM_TALK, "None of the gold around here is even remotely suitable in terms of quality. I have searched far and wide for the perfect gold I desire")
							.addNPC(NPC, HeadE.CALM_TALK, "but have had no success so in finding it I am afraid. If you can find me my perfect gold, make a ring and necklace from it,")
							.addNPC(NPC, HeadE.CALM_TALK, "and add rubies to them, I will gladly hand over my fragment of the family crest to you.")
							.addPlayer(HeadE.HAPPY_TALKING, "Can you give me any help on finding this 'perfect gold'?")
							.addNPC(NPC, HeadE.CALM_TALK, "I thought I had found a solid lead on its whereabouts when I heard of a dwarf who is an expert on gold who goes by the name of 'Boot'.")
							.addNPC(NPC, HeadE.CALM_TALK, "Unfortunately he has apparently returned to this home, somewhere in the mountains, and I have no idea how to find him.")
							.addPlayer(HeadE.HAPPY_TALKING, "Well, I'll see what I can do", ()->{
								player.getQuestManager().setStage(Quest.FAMILY_CREST, TALK_TO_BOOT);
							})
							);
				}
			});
		}
		case TALK_TO_BOOT -> {
			addNPC(NPC, HeadE.CALM_TALK, "So how are you doing getting me my perfect gold jewelry?");
			addPlayer(HeadE.HAPPY_TALKING, "I'm still after that 'perfect gold'.");
			addNPC(NPC, HeadE.CALM_TALK, "I know how you feel... for such a long time I have searched and searched for the elusive perfect gold...");
			addNPC(NPC, HeadE.CALM_TALK, "I thought I had gotten a good lead on finding it when I heard talk of a dwarven expert on gold named Boot some time" +
					" back, but unfortunately for me, he has returned to his mountain home, which I cannot find.");
		}
		case GIVE_AVAN_JEWLERY -> {
			addNPC(NPC, HeadE.CALM_TALK, "So how are you doing getting me my perfect gold jewelry?");
			if(player.getInventory().containsItems(new int[]{PERFECT_RUBY_NECKLACE, PERFECT_RUBY_RING}, new int[]{1, 1})) {
				addPlayer(HeadE.HAPPY_TALKING, "I have the ring and necklace right here.");
				addSimple("You show Avan the jewlery");
				addNPC(NPC, HeadE.CALM_TALK, "These... these are exquisite! EXACTLY what I was searching for all of this time! Please, take my crest fragment!");
				addSimple("You exchange the jewlery for the family crest with Avan", ()->{
					player.getInventory().removeItems(new Item(PERFECT_RUBY_RING, 1), new Item(PERFECT_RUBY_NECKLACE));
					player.getInventory().addItem(AVAN_CREST, 1);
					player.getQuestManager().setStage(Quest.FAMILY_CREST, TALK_TO_JOHNATHAN);
				});
				addNPC(NPC, HeadE.CALM_TALK, "Now, I suppose you will be wanting to find my brother Johnathon who is in possession of the final piece of the family's crest?");
				addPlayer(HeadE.HAPPY_TALKING, "That's correct.");
				addNPC(NPC, HeadE.CALM_TALK, "Well, the last I heard of my brother Johnathon, he was studying the magical arts, and trying to hunt some demon or other out in The Wilderness.");
				addNPC(NPC, HeadE.CALM_TALK, "Unsurprisingly, I do not believe he is doing a particularly good job of things, and spends most of his time recovering from his injuries");
				addNPC(NPC, HeadE.CALM_TALK, "in some tavern or other near the eastern edge of The Wilderness. You'll probably find him still there.");
				addPlayer(HeadE.HAPPY_TALKING, "Thanks Avan.");
			} else {
				addPlayer(HeadE.HAPPY_TALKING, "I'm still after that 'perfect gold'.");
				addNPC(NPC, HeadE.CALM_TALK, "I know how you feel... for such a long time I have searched and searched for the elusive perfect gold...");
			}
		}
		case TALK_TO_JOHNATHAN, KILL_CHRONOZON -> {
			if(!player.getInventory().containsItem(AVAN_CREST, 1) && !player.getInventory().containsItem(FAMILY_CREST)) {
				if(player.getInventory().hasFreeSlots()) {
					addNPC(NPC, HeadE.CALM_TALK, "You lost this.");
					addSimple("Avan gives you back his crest...", () -> {
						player.getInventory().addItem(AVAN_CREST, 1);
					});
				} else {
					addNPC(NPC, HeadE.CALM_TALK, "I have my crest if you had room for it");
					addPlayer(HeadE.HAPPY_TALKING, "Okay.");
				}
				return;
			}
			addNPC(NPC, HeadE.CALM_TALK, "Now, I suppose you will be wanting to find my brother Johnathon who is in possession of the final piece of the family's crest?");
			addPlayer(HeadE.HAPPY_TALKING, "That's correct.");
			addNPC(NPC, HeadE.CALM_TALK, "Well, the last I heard of my brother Johnathon, he was studying the magical arts, and trying to hunt some demon or other out in The Wilderness.");
			addNPC(NPC, HeadE.CALM_TALK, "Unsurprisingly, I do not believe he is doing a particularly good job of things, and spends most of his time recovering from his injuries");
			addNPC(NPC, HeadE.CALM_TALK, "in some tavern or other near the eastern edge of The Wilderness. You'll probably find him still there.");
			addPlayer(HeadE.HAPPY_TALKING, "Thanks Avan.");
		}
		case QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "I have heard word from my father, thank you for helping to restore our family honour");
			if(player.getInventory().containsItem(FAMILY_GAUNTLETS, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "Your father said that you could improve these Gauntlets in some way for me");
				addNPC(NPC, HeadE.CALM_TALK, "Yes, I can. In my quest to find the perfect gold I learned a lot; I can make it so when you're wearing these" +
						" you gain more experience when smithing gold.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("That sounds good, improve them for me", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "That sounds good, improve them for me")
								.addSimple("Avan takes out a little hammer")
								.addSimple("He starts pounding on the gauntlets")
								.addSimple("Avan hands the gauntlets to you", () -> {
									player.getInventory().removeItems(new Item(FAMILY_GAUNTLETS, 1));
									player.getInventory().addItem(GOLDSMITH_GAUNTLETS, 1);
								})
								);
						option("I think I'll check my other options with your brothers", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I think I'll check my other options with your brothers")
								.addNPC(NPC, HeadE.CALM_TALK, "Ok if you insist on getting help from the likes of them")
								);
					}
				});
			} else
				addPlayer(HeadE.HAPPY_TALKING, "You're welcome!");

		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new AvanFamilyCrestD(e.getPlayer()).getStart()));
}
