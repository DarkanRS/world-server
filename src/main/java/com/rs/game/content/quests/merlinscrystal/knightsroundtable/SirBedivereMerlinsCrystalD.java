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
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class SirBedivereMerlinsCrystalD extends Conversation {
	private final static int NPC = 242;
	public SirBedivereMerlinsCrystalD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
		case NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "May I help you?");
			addPlayer(HeadE.HAPPY_TALKING, "I'm really just looking for a quest...");
			addNPC(NPC, HeadE.CALM_TALK, "Fortune favours us both then adventurer. I suggest you go and speak to King Arthur.");
		}
		case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
			addPlayer(HeadE.HAPPY_TALKING, "I don't suppose you have any idea how to break into Mordred's fort do you?");
			addNPC(NPC, HeadE.CALM_TALK, "I am afraid not. Would that we could! Mordred and his cronies have been thorns in our side for far too long already!");
			addPlayer(HeadE.HAPPY_TALKING, "Ok. Thanks. See you later!");
			addNPC(NPC, HeadE.CALM_TALK, "Take care adventurer, Mordred is an evil and powerful foe.");
		}
		case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
			addNPC(NPC, HeadE.CALM_TALK, "Good luck breaking Merlin free adventurer.");
			addPlayer(HeadE.HAPPY_TALKING, "Thank you.");

		}
		case TALK_TO_ARTHUR, QUEST_COMPLETE -> {
			addNPC(NPC, HeadE.CALM_TALK, "You are an excellent knight indeed to have freed Merlin");
			addPlayer(HeadE.HAPPY_TALKING, "Thanks.");
		}
		}
	}
}
