package com.rs.game.content.quests.merlinscrystal;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.EXCALIBUR;

@PluginEventHandler
public class LadyOfLakeMerlinsCrystalD extends Conversation {
	final static int NPC=250;
	public LadyOfLakeMerlinsCrystalD(Player player) {
		super(player);
		addNPC(NPC, HeadE.CALM_TALK, "Good day to you");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Who are you?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Who are you?")
						.addNPC(NPC, HeadE.CALM_TALK, "I am the Lady of the Lake.")
						);
                if(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == MerlinsCrystal.THE_BLACK_CANDLE) {
                    option("About Merlin's Crystal", new Dialogue()
                            .addPlayer(HeadE.AMAZED, "I should probably get the black candle before the excalibur...")
                            .addNPC(NPC, HeadE.CALM_TALK, "What?")
                            .addPlayer(HeadE.HAPPY_TALKING, "Oh nothing, just notes to myself...")
                    );
                }

                if(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == MerlinsCrystal.OBTAINING_EXCALIBUR)
                    option("I seek the sword Excalibur", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "I seek the sword Excalibur")
                            .addNPC(NPC, HeadE.CALM_TALK, "Aye, I have that artefact in my possession. 'Tis very valuable, and not an artefact to be given " +
                                    "away lightly. I would want to give it away only to the one who is worthy and good.")
                            .addPlayer(HeadE.HAPPY_TALKING, "And how am I meant to prove that?")
                            .addNPC(NPC, HeadE.CALM_TALK, "I shall set a test for you. First I need you to travel to Port Sarim. Then go to the upstairs room of the " +
                                    "jeweller' shop there.", ()->{
                                        player.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).setB("LADY_TEST", true);
                                    })
                            .addPlayer(HeadE.HAPPY_TALKING, "Okay, that seems easy enough.")
                            );
                if(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) >= MerlinsCrystal.PERFORM_RITUAL && !player.getInventory().containsItem(EXCALIBUR))
                    option("I lost Excalibur...", new Dialogue()
                            .addPlayer(HeadE.SAD, "I lost Excalibur...")
                            .addNPC(NPC, HeadE.CALM_TALK, "I found it!")
                            .addPlayer(HeadE.AMAZED, "Really!?")
                            .addNPC(NPC, HeadE.FRUSTRATED, "Yes, please be worthy of it.")
                            .addItem(EXCALIBUR, "She gives you the sword", ()-> {
                                player.getInventory().addItem(EXCALIBUR, 1);
                            }));
				option("Good day.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Good day.")
						);
			}
		});

	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }, e -> e.getPlayer().startConversation(new LadyOfLakeMerlinsCrystalD(e.getPlayer()).getStart()));

}
