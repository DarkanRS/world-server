package com.rs.game.player.quests.handlers.merlinscrystal;

import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.LADY_LAKE_TEST_ATTR;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class LadyOfLakeMerlinsCrystalD extends Conversation {
	final static int NPC=250;
	public LadyOfLakeMerlinsCrystalD(Player p) {
		super(p);
		addNPC(NPC, HeadE.CALM_TALK, "Good day to you");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Who are you?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Who are you?")
						.addNPC(NPC, HeadE.CALM_TALK, "I am the Lady of the Lake.")
						);
				option("I seek the sword Excalibur", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I seek the sword Excalibur")
						.addNPC(NPC, HeadE.CALM_TALK, "Aye, I have that artefact in my possession. 'Tis very valuable, and not an artefact to be given " +
								"away lightly. I would want to give it away only to the one who is worthy and good.")
						.addPlayer(HeadE.HAPPY_TALKING, "And how am I meant to prove that?")
						.addNPC(NPC, HeadE.CALM_TALK, "I shall set a test for you. First I need you to travel to Port Sarim. Then go to the upstairs room of the " +
								"jeweller' shop there.", ()->{
									p.getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).setB(LADY_LAKE_TEST_ATTR, true);
								})
						.addPlayer(HeadE.HAPPY_TALKING, "Okay, that seems easy enough.")
						);
				option("Good day.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Good day.")
						);
			}
		});

	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(NPC) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new LadyOfLakeMerlinsCrystalD(e.getPlayer()).getStart());
		}
	};

}
