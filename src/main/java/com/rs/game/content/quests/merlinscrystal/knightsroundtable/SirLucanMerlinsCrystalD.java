package com.rs.game.content.quests.merlinscrystal.knightsroundtable;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.*;

@PluginEventHandler
public class SirLucanMerlinsCrystalD extends Conversation {
	private final static int NPC = 245;
	public SirLucanMerlinsCrystalD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hello there adventurer.");
				addPlayer(HeadE.HAPPY_TALKING, "I'm looking for a quest...");
				addNPC(NPC, HeadE.CALM_TALK, "Talk to the King then adventurer. He is always looking for men of bravery to aid him in his actions...");
			}
			case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
				addPlayer(HeadE.HAPPY_TALKING, "Any suggestions on freeing Merlin?");
				addNPC(NPC, HeadE.CALM_TALK, "I've tried all the weapons I can find, yet none are powerful enough to break his crystal prison... " +
						"Perhaps the mighty Excalibur would be strong enough...");
				addPlayer(HeadE.HAPPY_TALKING, "Excalibur eh? Where would I find that?");
				addNPC(NPC, HeadE.CALM_TALK, "The Lady of the Lake is looking after it I believe... but I know not where she resides currently");
				addPlayer(HeadE.HAPPY_TALKING, "Thanks. I'll try and find someone who does.");

			}
			case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Remember the Lady of the Lake has excalibur in southern Taverly.");
				addPlayer(HeadE.HAPPY_TALKING, "Gotcha!");
			}
			case TALK_TO_ARTHUR, QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Excalibur is in good hands!");
				addPlayer(HeadE.HAPPY_TALKING, "...");
			}
		}
	}
}
