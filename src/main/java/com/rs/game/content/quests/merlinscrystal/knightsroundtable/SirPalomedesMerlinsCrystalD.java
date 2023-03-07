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
public class SirPalomedesMerlinsCrystalD extends Conversation {
	private final static int NPC = 3787;
	public SirPalomedesMerlinsCrystalD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hello there adventurer, what do you want of me?");
				addPlayer(HeadE.HAPPY_TALKING, "I'd like some advice on finding a quest.");
				addNPC(NPC, HeadE.CALM_TALK, "I do not know of any myself... but it would perhaps be worth your while asking the King if he has any tasks for you.");
			}
			case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
				addPlayer(HeadE.HAPPY_TALKING, "I'd like some advice on breaking into Mordred's fort.");
				addNPC(NPC, HeadE.CALM_TALK, "Sorry, I cannot help you with that.");

			}
			case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Sorry I can't help you much more.");
				addPlayer(HeadE.HAPPY_TALKING, "Okay.");
			}
			case TALK_TO_ARTHUR, QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "Thanks again for freeing Merlin.");
				addPlayer(HeadE.HAPPY_TALKING, "Of course!");
			}
		}
	}
}
