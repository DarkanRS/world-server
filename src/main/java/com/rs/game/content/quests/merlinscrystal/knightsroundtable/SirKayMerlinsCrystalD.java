package com.rs.game.content.quests.merlinscrystal.knightsroundtable;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.BREAK_MERLIN_CRYSTAL;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.CONFRONT_KEEP_LA_FAYE;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.NOT_STARTED;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.OBTAINING_EXCALIBUR;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.PERFORM_RITUAL;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.TALK_TO_ARTHUR;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.TALK_TO_KNIGHTS;
import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.THE_BLACK_CANDLE;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SirKayMerlinsCrystalD extends Conversation {
	private final static int NPC = 241;
	public SirKayMerlinsCrystalD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
		case NOT_STARTED -> {
			addPlayer(HeadE.HAPPY_TALKING, "Morning. Know where an adventurer has to go to find a quest around here?");
			addNPC(NPC, HeadE.CALM_TALK, "An adventurer eh? There is no service finer than serving the bountiful King Arthur, and I happen to know" +
					" there's an important quest to fulfill.");
		}
		case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
			addPlayer(HeadE.HAPPY_TALKING, "Any ideas on getting into Mordred's fort?");
			addNPC(NPC, HeadE.CALM_TALK, "Mordred... So you think he may be involved with the curse upon Merlin?");
			addPlayer(HeadE.HAPPY_TALKING, "Good a guess as any right?");
			addNPC(NPC, HeadE.CALM_TALK, "I think that you may be onto something there. Unfortunately his fortress is impregnable!");
			addPlayer(HeadE.HAPPY_TALKING, "...I'll figure something out.");
		}
		case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
			addNPC(NPC, HeadE.CALM_TALK, "Are you figuring out the curse on Merlin?");
			addPlayer(HeadE.HAPPY_TALKING, "Yes, I am looking into it now.");
		}
		case TALK_TO_ARTHUR -> {
			addNPC(NPC, HeadE.CALM_TALK, "Thank you for freeing Merlin!");
			addPlayer(HeadE.HAPPY_TALKING, "You're welcome.");
		}
		}
	}
}
