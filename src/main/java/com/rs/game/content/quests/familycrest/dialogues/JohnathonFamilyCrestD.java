package com.rs.game.content.quests.familycrest.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.familycrest.FamilyCrest.*;

@PluginEventHandler
public class JohnathonFamilyCrestD extends Conversation {
	private final int DEMON_INQUIRY = 0;
	private final int SECOND = 1;
	private static final int NPC = 668;
	public JohnathonFamilyCrestD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.FAMILY_CREST)) {
		case TALK_TO_JOHNATHAN -> {
			if(player.getQuestManager().getAttribs(Quest.FAMILY_CREST).getB("USED_ANTIPOISEN_ON_JOHN")) {
				addNPC(NPC, HeadE.CALM_TALK, "Ooooh... thank you... Wow! I'm feeling a lot better now! That potion really seems to have done the trick!");
				addNPC(NPC, HeadE.CALM_TALK, "How can I reward you?");
				addPlayer(HeadE.HAPPY_TALKING, "I've come here for your piece of the Fitzharmon family crest.");
				addNPC(NPC, HeadE.CALM_TALK, "You have? Unfortunately I don't have it any more... in my attempts to slay the fiendish Chronozon, the blood demon,");
				addNPC(NPC, HeadE.CALM_TALK, "I lost a lot of equipment in our last battle when he bested me and forced me away from his den. He probably still has it now.");
				addNext(()->{
					player.getQuestManager().setStage(Quest.FAMILY_CREST, KILL_CHRONOZON);
					player.startConversation(new JohnathonFamilyCrestD(player, DEMON_INQUIRY).getStart());
				});
			} else {
				addPlayer(HeadE.HAPPY_TALKING, "Greetings. Would you happen to be Johnathon Fitzharmon?");
				addNPC(NPC, HeadE.CALM_TALK, "That... I am...");
				addPlayer(HeadE.HAPPY_TALKING, "I am here to retrieve your fragment of the Fitzharmon family crest.");
				addNPC(NPC, HeadE.DIZZY, "The... poison... it is all... too much... My head... will not... stop spinning...");
				addSimple("Sweat is pouring down Jonathons's face");
				addNPC(NPC, HeadE.DIZZY, "What... what did that spider... DO to me? I... I feel so weak... I can hardly... think at all...");
				addSimple("He appears in need of antipoison");
			}
		}
		case KILL_CHRONOZON -> {
			if(player.getInventory().containsItem(JOHNATHAN_CREST, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "I have your piece of the crest!");
				addNPC(NPC, HeadE.CALM_TALK, "Well done! Now return it to my father!");
				return;
			}
			addPlayer(HeadE.HAPPY_TALKING, "I'm trying to kill this demon Chronozon that you mentioned...");
			addNext(()->{player.startConversation(new JohnathonFamilyCrestD(player, DEMON_INQUIRY).getStart());});
		}
		case QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "I have heard word from my father, thank you for helping to restore our family honour");
			if(player.getInventory().containsItem(FAMILY_GAUNTLETS, 1)) {
				addPlayer(HeadE.HAPPY_TALKING, "Your father said that you could improve these Gauntlets in some way for me");
				addNPC(NPC, HeadE.CALM_TALK, "Yes, I can. In my travels I have studied the arcane arts and know a way to empower bolt spells" +
						" with our family gauntlets");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("That sounds good, improve them for me", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "That sounds good, improve them for me")
								.addSimple("Johnathan takes out a wand and crumples it into pieces onto the gloves")
								.addSimple("He burns them with a bolt spell onto the gauntlet")
								.addSimple("Johnathan hands the gauntlets to you", () -> {
									player.getInventory().removeItems(new Item(FAMILY_GAUNTLETS, 1));
									player.getInventory().addItem(CHAOS_GAUNTLETS, 1);
								})
								);
						option("I think I'll check my other options with your brothers", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I think I'll check my other options with your brothers")
								.addNPC(NPC, HeadE.CALM_TALK, "Ok, give them my regards.")
								);
					}
				});
			} else
				addPlayer(HeadE.HAPPY_TALKING, "You're welcome!");

		}
		default -> {
			addNPC(NPC, HeadE.DIZZY, "I am so very tired... Leave me be... to rest...");
		}
		}
	}

	public JohnathonFamilyCrestD(Player p, int id) {
		super(p);
		switch(id) {
		case DEMON_INQUIRY -> {
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("So is this Chronozon hard to defeat?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "So is this Chronozon hard to defeat?")
							.addNPC(NPC, HeadE.CALM_TALK, "Well... you will have to be a skilled Mage to defeat him, and my powers are not good enough" +
									" yet. You will need to hit him once with each of the four elemental spells of death before he will be defeated.")
							);
					option("Where can I find Chronozon?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Where can I find Chronozon?")
							.addNPC(NPC, HeadE.CALM_TALK, "The fiend has made his lair in Edgeville Dungeon. When you come in down the ladder in Edgeville, " +
									"follow the corridor north until you reach a room with skeletons. That passageway to the left will lead you to him.")
							);
					option("So how did you end up getting poisoned?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "So how did you end up getting poisoned?")
							.addNPC(NPC, HeadE.CALM_TALK, "Those accursed poison spiders that surround the entrance to Chronozon's lair... I must have taken a nip from one of them as I attempted to make my escape.")
							);
					option("I will be on my way now.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I will be on my way now.")
							.addNPC(NPC, HeadE.CALM_TALK, "My thanks for the assistance adventurer.")
							);
				}
			});
		}
		case SECOND -> {

		}

		}
	}

	public static ItemOnNPCHandler itemOnJohn = new ItemOnNPCHandler(true, new Object[] { 668 }, e -> {
		Player p = e.getPlayer();
		if(p.getQuestManager().getStage(Quest.FAMILY_CREST) == TALK_TO_JOHNATHAN)
			if(e.getItem().getName().contains("Antipoison") || e.getItem().getName().contains("antipoison"))
				if(e.getItem().getDefinitions().isNoted())
					p.startConversation(new Conversation(p) {
						{
							addPlayer(HeadE.HAPPY_TALKING, "It must be unnoted silly!");
							create();
						}
					});
				else if(!p.getQuestManager().getAttribs(Quest.FAMILY_CREST).getB("USED_ANTIPOISEN_ON_JOHN")) {
					p.getQuestManager().getAttribs(Quest.FAMILY_CREST).setB("USED_ANTIPOISEN_ON_JOHN", true);
					p.startConversation(new Conversation(p) {
						{
							addSimple("You give Johnathon a small sip of antipoison, not a full dose...");
							create();
						}
					});
				}
	});

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new JohnathonFamilyCrestD(e.getPlayer()).getStart()));
}
