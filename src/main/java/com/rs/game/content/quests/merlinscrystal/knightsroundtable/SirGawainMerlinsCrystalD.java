package com.rs.game.content.quests.merlinscrystal.knightsroundtable;

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
public class SirGawainMerlinsCrystalD extends Conversation {
	private final static int NPC = 240;
	public SirGawainMerlinsCrystalD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
		case NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "Good day to you!");
			addPlayer(HeadE.HAPPY_TALKING, "Know you of any quests sir knight?");
			addNPC(NPC, HeadE.CALM_TALK, "The king is the man to talk to if you want a quest.");
		}
		case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
			addNPC(NPC, HeadE.CALM_TALK, "Good day to you");
			addOptions("Choose an option:", new Options() {
				@Override
				public void create() {
					option("Any ideas on how to get Merlin out of that crystal?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Any ideas on how to get Merlin out of that crystal?")
							.addNPC(NPC, HeadE.CALM_TALK, "I'm a little stumped myself. We've tried opening it with anything and everything!")
							);
					option("Do you know how Merlin got trapped?", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Do you know how Merlin got trapped?")
							.addNPC(NPC, HeadE.CALM_TALK, "I would guess this is the work of the evil Morgan Le Faye!")
							.addPlayer(HeadE.HAPPY_TALKING, "And where could I find her?")
							.addNPC(NPC, HeadE.CALM_TALK, "She lives in her stronghold to the south of here, guarded by some renegade knights led by Sir Mordred.")
							.addPlayer(HeadE.HAPPY_TALKING, "Any idea how to get into Morgan LeFaye's stronghold?")
							.addNPC(NPC, HeadE.CALM_TALK, "No, you've got me stumped there...")
							.addPlayer(HeadE.HAPPY_TALKING, "Thank you for the information")
							);
				}
			});

		}
		case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
			addNPC(NPC, HeadE.AMAZED_MILD, "You stormed Morgan La Faye's stronghold?!");
			addPlayer(HeadE.HAPPY_TALKING, "Yup.");

		}
		case TALK_TO_ARTHUR, QUEST_COMPLETE -> {
			addNPC(NPC, HeadE.CALM_TALK, "I am impressed, excalibur is in good hands.");
			addPlayer(HeadE.HAPPY_TALKING, "...");
		}
		}
	}


}
