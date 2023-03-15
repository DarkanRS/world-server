package com.rs.game.content.quests.merlinscrystal;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.BREAK_MERLIN_CRYSTAL;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.CONFRONT_KEEP_LA_FAYE;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.NOT_STARTED;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.OBTAINING_EXCALIBUR;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.PERFORM_RITUAL;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.QUEST_COMPLETE;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.TALK_TO_ARTHUR;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.TALK_TO_KNIGHTS;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.THE_BLACK_CANDLE;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class KingArthurMerlinsCrystalD extends Conversation {
	private final static int NPC = 251;
	public KingArthurMerlinsCrystalD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CALM_TALK, "Welcome to my court. I am King Arthur.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I want to become a knight of the round table!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I want to become a knight of the round table!")
								.addNPC(NPC, HeadE.CALM_TALK, "Well, in that case I think you need to go on a quest to prove yourself worthy. " +
										"My knights all appreciate a good quest. Unfortunately, our current quest is to rescue Merlin. ")
								.addNPC(NPC, HeadE.CALM_TALK, "Back in England, he got himself trapped in some sort of magical Crystal. We've moved him from " +
										"the cave we found him in and now he's upstairs in his tower.")
								.addOptions("Start Merlin's Crystal?", new Options() {
									@Override
									public void create() {
										option("Yes", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "I will see what I can do then.", ()->{
													player.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, TALK_TO_KNIGHTS);
												})
												.addNPC(NPC, HeadE.CALM_TALK, "Talk to my knights if you need any help.")
												.addNPC(NPC, HeadE.CALM_TALK, "You will need to find a way into Morgan LeFaye's stronghold.")
												);
										option("No", new Dialogue());
									}
								})
								);
						option("So what are you doing in Runescape?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "So what are you doing in Runescape?")
								.addNPC(NPC, HeadE.CALM_TALK, "Well legend says we will return to Britain in its time of greatest need. But that's not for quite a while yet")
								.addNPC(NPC, HeadE.CALM_TALK, "So we've moved the whole outfit here for now. We're passing the time in Runescape!")

								);
						option("Thank you very much", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Thank you very much")
								);
					}
				});
			}
			case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Talk to my knights about breaking the crystal.");
				addPlayer(HeadE.HAPPY_TALKING, "Okay");
			}
			case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
				addNPC(NPC, HeadE.CALM_TALK, "I am amazed you broke into Morgan La Faye's stronghold!");
			}

			case TALK_TO_ARTHUR -> {
				addPlayer(HeadE.HAPPY_TALKING, "I have freed Merlin from his crystal!");
				addNPC(NPC, HeadE.CALM_TALK, "Ah. A good job, well done. I dub thee a Knight Of The Round Table. You are now an honorary knight.");
				addNext(()->{player.getQuestManager().completeQuest(Quest.MERLINS_CRYSTAL);});

			}
			case QUEST_COMPLETE ->  {
				addNPC(NPC, HeadE.CALM_TALK, "Thank you for freeing Merlin!");
				addPlayer(HeadE.HAPPY_TALKING, "You are welcome.");
			}
		}
	}
}
