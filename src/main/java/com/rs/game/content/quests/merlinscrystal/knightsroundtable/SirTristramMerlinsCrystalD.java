package com.rs.game.content.quests.merlinscrystal.knightsroundtable;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.merlinscrystal.MerlinsCrystal.*;

@PluginEventHandler
public class SirTristramMerlinsCrystalD extends Conversation {
	private final static int NPC = 243;
	public SirTristramMerlinsCrystalD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
			case NOT_STARTED -> {
				addNPC(NPC, HeadE.CALM_TALK, "Hail Arthur, King of the Britons!");
				addPlayer(HeadE.HAPPY_TALKING, "Um... Hello.I'm looking for adventure! More specifically, some sort of quest.");
				addNPC(NPC, HeadE.CALM_TALK, "... Then hail Arthur, King of the Britons, like I just said.");
				addPlayer(HeadE.HAPPY_TALKING, "Oh. Ok. I thought you just had a weird way of saying hello is all.");

			}
			case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
				addPlayer(HeadE.HAPPY_TALKING, "Um...Hello. I need to get into Mordred's Fort...");
				addNPC(NPC, HeadE.CALM_TALK, "Good luck with that!");
			}
			case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
				addNPC(NPC, HeadE.CALM_TALK, "Good luck adventurer!");
				addPlayer(HeadE.HAPPY_TALKING, "Umm, okay.");

			}
			case TALK_TO_ARTHUR, QUEST_COMPLETE -> {
				addNPC(NPC, HeadE.CALM_TALK, "I am surprised you freed Merlin, luck is on your side!");
				addPlayer(HeadE.FRUSTRATED, "Skill more like it.");
			}
		}
	}
}
