package com.rs.game.player.quests.handlers.merlinscrystal.knightsroundtable;

import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.BREAK_MERLIN_CRYSTAL;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.CONFRONT_KEEP_LA_FAYE;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.NOT_STARTED;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.OBTAINING_EXCALIBUR;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.PERFORM_RITUAL;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.QUEST_COMPLETE;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.TALK_TO_ARTHUR;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.TALK_TO_KNIGHTS;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.THE_BLACK_CANDLE;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SirPelleasMerlinsCrystalD extends Conversation {
	private final static int NPC = 244;
	public SirPelleasMerlinsCrystalD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
		case NOT_STARTED -> {
			addNPC(NPC, HeadE.CALM_TALK, "Greetings to the court of King Arthur!");
			addPlayer(HeadE.HAPPY_TALKING, "Hello. I'm looking for a quest. Who should I talk to?");
			addNPC(NPC, HeadE.CALM_TALK, "King Arthur will let you know. I believe he has a quest at the moment.");
		}
		case TALK_TO_KNIGHTS, CONFRONT_KEEP_LA_FAYE -> {
			addPlayer(HeadE.HAPPY_TALKING, "Any suggestions on getting into Mordred's fort?");
			addNPC(NPC, HeadE.CALM_TALK, "My best guess would be using magic. Unfortunately Merlin is our magic expert.");
			addPlayer(HeadE.HAPPY_TALKING, "Ok, well, thanks anyway.");

		}
		case THE_BLACK_CANDLE, OBTAINING_EXCALIBUR, PERFORM_RITUAL, BREAK_MERLIN_CRYSTAL -> {
			addNPC(NPC, HeadE.CALM_TALK, "Magic or a ritual may be needed to free Merlin.");
			addPlayer(HeadE.HAPPY_TALKING, "Noted");
		}
		case TALK_TO_ARTHUR, QUEST_COMPLETE -> {
			addNPC(NPC, HeadE.CALM_TALK, "We have our wizard back, you are worthy of knighthood.");
			addPlayer(HeadE.HAPPY_TALKING, "Thanks...");
		}
		}
	}

	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { NPC }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new SirPelleasMerlinsCrystalD(e.getPlayer()).getStart());
		}
	};
}
