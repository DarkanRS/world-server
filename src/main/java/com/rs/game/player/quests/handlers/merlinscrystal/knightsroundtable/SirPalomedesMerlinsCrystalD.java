package com.rs.game.player.quests.handlers.merlinscrystal.knightsroundtable;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.*;

@PluginEventHandler
public class SirPalomedesMerlinsCrystalD extends Conversation {
    private final static int NPC = 3787;
    public SirPalomedesMerlinsCrystalD(Player p) {
        super(p);
        switch(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL)) {
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

    public static NPCClickHandler handleDialogue = new NPCClickHandler(NPC) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().startConversation(new SirPalomedesMerlinsCrystalD(e.getPlayer()).getStart());
        }
    };
}
