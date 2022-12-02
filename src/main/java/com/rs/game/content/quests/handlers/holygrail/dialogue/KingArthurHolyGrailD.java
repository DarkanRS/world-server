package com.rs.game.content.quests.handlers.holygrail.dialogue;

import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GIVE_AURTHUR_HOLY_GRAIL;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_ENTRANA;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.GO_TO_MCGRUBOR;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.NOT_STARTED;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.QUEST_COMPLETE;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_FISHER_KING;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.SPEAK_TO_PERCIVAL;
import static com.rs.game.content.quests.handlers.holygrail.HolyGrail.TALK_TO_MERLIN;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.handlers.holygrail.HolyGrail;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class KingArthurHolyGrailD extends Conversation {
	private static final int NPC = 251;
	public KingArthurHolyGrailD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.HOLY_GRAIL)) {
			case NOT_STARTED -> {
				addPlayer(HeadE.HAPPY_TALKING, "Now I am a knight of the round table, do you have any more quests for me?");
				addNPC(NPC, HeadE.CALM_TALK, "Aha! I'm glad you are here! I am sending out various knights on an important quest. I was wondering if you " +
						"too would like to take up this quest?");
				if(HolyGrail.meetsRequirements(p)) {
					addOptions("Start Holy Grail?", new Options() {
						@Override
						public void create() {
							option("Yes.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Tell me of this quest.")
								.addNPC(NPC, HeadE.CALM_TALK, "Well, we recently found out that the Holy Grail has passed into the Runescape world. This is most fortuitous!")
								.addNPC(NPC, HeadE.CALM_TALK, "None of my knights ever did return with it last time. Now we have the opportunity to give it another go, maybe this time we will have more luck!")
								.addPlayer(HeadE.HAPPY_TALKING, "I'd enjoy trying that.")
								.addNPC(NPC, HeadE.CALM_TALK, "Go speak to Merlin. He may be able to give a better clue as to where it is now you have freed him from that crystal. " +
									"He has set up his workshop in the room next to the library.", () -> {
									p.getQuestManager().setStage(Quest.HOLY_GRAIL, TALK_TO_MERLIN);
								})
							);
							option("No.", new Dialogue());
						}
					});
					return;
				}
				addNext(()->{p.getQuestManager().showQuestDetailInterface(Quest.HOLY_GRAIL);});
			}
			case TALK_TO_MERLIN, GO_TO_ENTRANA -> {
				addNPC(NPC, HeadE.CALM_TALK, "How goes thy quest?");
				addPlayer(HeadE.HAPPY_TALKING, "I have made progress, but I have not recovered the Grail yet.");
				addNPC(NPC, HeadE.CALM_TALK, "Well, the Grail IS very elusive, it may take some perseverance. As I said before, speak to Merlin in the workshop by the library.");
			}
			case GO_TO_MCGRUBOR -> {
				addNPC(NPC, HeadE.CALM_TALK, "How goes thy quest?");
				addPlayer(HeadE.HAPPY_TALKING, "I have made progress, but I have not recovered the Grail yet.");
				addNPC(NPC, HeadE.CALM_TALK, "My knight, Sir Ghalad, is in Mcgrubor woods. he may have information you need.");
			}
			case SPEAK_TO_FISHER_KING -> {
				addNPC(NPC, HeadE.CALM_TALK, "How goes thy quest?");
				addPlayer(HeadE.HAPPY_TALKING, "I have made progress, but I have not recovered the Grail yet.");
				addNPC(NPC, HeadE.CALM_TALK, "This is a good time to seek the Fisher Realm.");
			}
			case SPEAK_TO_PERCIVAL -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello, do you have a knight named Sir Percival?");
				addNPC(NPC, HeadE.CALM_TALK, "Ah yes. I remember young Percival. He rode off on a quest a couple of months ago. We are getting a bit worried, he's not back yet...");
				addNPC(NPC, HeadE.CALM_TALK, "he was going to try and recover the golden boots of Arkaneeses.");
				addPlayer(HeadE.HAPPY_TALKING, "Any idea which way that would be?");
				addNPC(NPC, HeadE.CALM_TALK, "Not exactly. We discovered some magic golden feathers that are said to point the way to the boots... ");
				addNPC(NPC, HeadE.CALM_TALK, "They certainly point somewhere. Just blowing on them gently will supposedly show the way to go.");
				addSimple("King Arthur gives you a feather.", ()->{
					p.getInventory().addItem(new Item(18, 1), true);
				});
			}
			case GIVE_AURTHUR_HOLY_GRAIL -> {
				addNPC(NPC, HeadE.CALM_TALK, "How goes thy quest?");
				if(p.getInventory().containsItem(19)) {
					addPlayer(HeadE.HAPPY_TALKING, "I have retrieved the Grail!");
					addNPC(NPC, HeadE.CALM_TALK, "Wow! Incredible! You truly are a splendid knight!");
					addNext(()->{
						p.getInventory().deleteItem(new Item(19, 1));
						p.getQuestManager().completeQuest(Quest.HOLY_GRAIL);
					});
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "I still need to get the Holy Grail.");
				addPlayer(HeadE.HAPPY_TALKING, "I believe it is somewhere in the Fisher Realm");
				addNPC(NPC, HeadE.CALM_TALK, "Okay, carry on then.");
			}
			case QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.HAPPY_TALKING, "Thank you for retrieving the Grail! You shall long be remembered as one of the greatest heroes amongst" +
						" the Knights of the Round Table!");
			}
		}
	}
}
