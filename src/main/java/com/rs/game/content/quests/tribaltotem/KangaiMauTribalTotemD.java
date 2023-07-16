package com.rs.game.content.quests.tribaltotem;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.tribaltotem.TribalTotem.*;

@PluginEventHandler
public class KangaiMauTribalTotemD extends Conversation {
	private static final int NPC = 846;
	public KangaiMauTribalTotemD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.TRIBAL_TOTEM)) {
		case NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "Hello. I Kangai Mau of the Rantuki tribe");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("And what are you doing here in Brimhaven?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "And what are you doing here in Brimhaven?")
							.addNPC(NPC, HeadE.CALM_TALK, "I'm looking for someone brave to go on important mission. Someone skilled in thievery and " +
									"sneaking about. I am told I can find such people in Brimhaven.")
							.addPlayer(HeadE.HAPPY_TALKING, "Yep. I have heard there are many of that type here.")
							.addNPC(NPC, HeadE.CALM_TALK, "Let's hope I find them.")
							);
					if(KangaiMauTribalTotemD.this.player.getSkills().getLevel(Constants.THIEVING) < 21)
						option("I'm in search of adventure!", new Dialogue()
								.addSimple("You need 21 thieving to start Tribal Totem.."));
					else
						option("I'm in search of adventure!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'm in search of adventure!")
								.addNPC(NPC, HeadE.CALM_TALK, "Adventure is something I may be able to give. I need someone to go on a mission to the city of Ardougne. ")
								.addNPC(NPC, HeadE.CALM_TALK, "There you will find the house of Lord Handlemort. In his house he has our tribal totem. We need it back.")
								.addPlayer(HeadE.HAPPY_TALKING, "Why does he have it?")
								.addNPC(NPC, HeadE.CALM_TALK, "Lord Handlemort is an Ardougnese explorer which means he think he have the right to come to my " +
										"tribal home, steal our stuff and put in his private museum.")
								.addOptions("Start Tribal Totem?", new Options() {
									@Override
									public void create() {
										option("Yes", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Ok, I will get it back", ()-> {
													player.getQuestManager().setStage(Quest.TRIBAL_TOTEM, TALK_TO_WIZARD);
												})
												.addNPC(NPC, HeadE.CALM_TALK, "Best of luck with that adventurer.")
												);
										option("No", new Dialogue());
									}
								})
								);
					option("Who are the Rantuki tribe?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Who are the Rantuki tribe?")
							.addNPC(NPC, HeadE.CALM_TALK, "A proud and noble tribe of Karamja. But now we are few, as men come from across, steal our " +
									"land, and settle on our hunting grounds.")
							);
				}
			});
		}
		case TALK_TO_WIZARD, REDIRECT_TELE_STONE -> {
			addNPC(NPC, HeadE.CALM_TALK, "Have you got our totem back?");
			addPlayer(HeadE.HAPPY_TALKING, "No, it's not that easy.");
			addNPC(NPC, HeadE.CALM_TALK, "Bah, you no good.");
		}
		case GET_TOTEM -> {
			if(player.getInventory().containsItem(TOTEM, 1)) {
				addNPC(NPC, HeadE.CALM_TALK, "Have you got our totem back?");
				addPlayer(HeadE.HAPPY_TALKING, "Yes I have.");
				addNPC(NPC, HeadE.CALM_TALK, "You have??? Many thanks brave adventurer! Here, have some freshly cooked Karamjan fish, caught specially by my tribe.");
				addSimple("You hand over the Tribal Totem.");
				addNext(()->{
					player.getInventory().removeItems(new Item(TOTEM, 1));
					player.getQuestManager().completeQuest(Quest.TRIBAL_TOTEM);
				});
			} else {
				addNPC(NPC, HeadE.CALM_TALK, "Have you got our totem back?");
				addPlayer(HeadE.HAPPY_TALKING, "No, it's not that easy.");
				addNPC(NPC, HeadE.CALM_TALK, "Bah, you no good.");
			}
		}
		case QUEST_COMPLETE ->  {
			addNPC(NPC, HeadE.CALM_TALK, "Thank you for helping my tribe!");
			addPlayer(HeadE.HAPPY_TALKING, "You are welcome!");
		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new KangaiMauTribalTotemD(e.getPlayer()).getStart()));
}
