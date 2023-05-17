package com.rs.game.content.quests.dragonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.*;

@PluginEventHandler
public class CabinBoyJenkinsDragonSlayerD extends Conversation {
	public CabinBoyJenkinsDragonSlayerD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.DRAGON_SLAYER)) {
		case NOT_STARTED, TALK_TO_OZIACH, TALK_TO_GUILDMASTER, PREPARE_FOR_CRANDOR -> {
			if(player.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(OWNS_BOAT_ATTR)) {
				addNPC(JENKINS, HeadE.CALM_TALK, "Ahoy! What d'ye think of yer ship then?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I'd like to inspect her some more.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'd like to inspect her some more.")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Aye."));
						if(player.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(NED_IS_CAPTAIN_ATTR))
							option("Can you sail this ship to Crandor?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can you sail this ship to Crandor?")
									.addNPC(JENKINS, HeadE.CALM_TALK, "Not me, sir! I'm just an 'umble cabin boy. You'll need to talk to Captain Ned."));
						else
							option("Can you sail this ship to Crandor?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can you sail this ship to Crandor?")
									.addNPC(JENKINS, HeadE.CALM_TALK, "Not me, sir! I'm just an 'umble cabin boy. You'll need a proper cap'n.")
									.addPlayer(HeadE.HAPPY_TALKING, "Where can I find a captain?")
									.addNPC(JENKINS, HeadE.CALM_TALK, "The cap'ns round 'ere seem to be a mite scared of Crandor. I ask 'em why and they " +
											"just say it was afore my time, but there is one cap'n I reckon might 'elp.")
									.addNPC(JENKINS, HeadE.CALM_TALK, "I 'eard there's a retured 'un who lives in Draynor Village who's so desperate to sail again " +
											"'e'd take any job. I can't remember 'is name, but 'e lives in Draynor Village an' makes rope.")
									);
					}
				});
			}
			else {
				addNPC(JENKINS, HeadE.CALM_TALK, "Ahoy! What d'ye think of the ship then?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I might buy her!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I might buy her!")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Great!"));
						option("I'd like to inspect her some more.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'd like to inspect her some more.")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Aye."));
						option("Can you sail this ship?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Can you sail this ship?")
								.addNPC(JENKINS, HeadE.CALM_TALK, "Not me, sir! I'm just an 'umble cabin boy. You'll need a proper cap'n.")
								);
					}
				});

			}
		}
		case REPORT_TO_OZIACH, QUEST_COMPLETE ->  {
			addNPC(JENKINS, HeadE.FRUSTRATED, "You are the worst captain ever! I could have died!");
			addPlayer(HeadE.CALM_TALK, "But you didn't...");
			addNPC(JENKINS, HeadE.FRUSTRATED, "Aye, I didn't.");
			addPlayer(HeadE.CALM_TALK, "Would you take me to Crandor again?");
			addNPC(JENKINS, HeadE.FRUSTRATED, "Now that the dragon is dead I guess I would.");
			addPlayer(HeadE.HAPPY_TALKING, "Great!");
		}
		}


	}

	public static NPCClickHandler handleJenkinsDialogue = new NPCClickHandler(new Object[] { 748 }, e -> e.getPlayer().startConversation(new CabinBoyJenkinsDragonSlayerD(e.getPlayer()).getStart()));
}
