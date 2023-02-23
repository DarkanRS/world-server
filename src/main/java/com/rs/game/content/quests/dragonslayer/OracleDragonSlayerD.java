package com.rs.game.content.quests.dragonslayer;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.ORACLE_DOOR_KNOWLEDGE_ATTR;
import static com.rs.game.content.quests.dragonslayer.DragonSlayer.PREPARE_FOR_CRANDOR;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class OracleDragonSlayerD extends Conversation {
	public OracleDragonSlayerD(Player p) {
		super(p);
		addPlayer(HeadE.AMAZED_MILD, "Can you impart your wise knowledge to me, O Oracle?");
		switch(p.getQuestManager().getStage(Quest.DRAGON_SLAYER)) {
		case PREPARE_FOR_CRANDOR ->  {
			addNPC(746, HeadE.CHEERFUL, "Sure, I see no harm in that.");
			addPlayer(HeadE.AMAZED_MILD, "I seek a piece of the map to the island of Crandor.");
			addNPC(746, HeadE.SECRETIVE, "Really let me think...");
			addNPC(746, HeadE.CALM_TALK, "The map's behind a door below, but entering is rather tough. This is what you need to know: You must " +
					"use the following stuff");
			addNPC(746, HeadE.CALM_TALK, "First, a drink used by a mage. Next, some worm string, changed to sheet. Then, a small crustacean cage. " +
					"Last, a bowl that's not seen heat. Use that stuff on the door...", () -> {
						p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(ORACLE_DOOR_KNOWLEDGE_ATTR, true);
					});
			addPlayer(HeadE.HAPPY_TALKING, "Got it, a crayfish cage, an unfired bowl, silk and a wizard mind bomb. I should use all of them on the door.");

		}
		default -> {
			addNPC(746, HeadE.FRUSTRATED, "I have none for you adventurer");
			addPlayer(HeadE.SAD, "Okay...");
		}
		}


	}

	public static NPCClickHandler handleOracleDialogue = new NPCClickHandler(new Object[] { 746 }, e -> e.getPlayer().startConversation(new OracleDragonSlayerD(e.getPlayer()).getStart()));
}
