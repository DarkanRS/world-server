package com.rs.game.player.quests.handlers.knightssword;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.player.quests.handlers.knightssword.KnightsSword.*;

@PluginEventHandler
public class ReldoKnightsSwordD extends Conversation {
    public ReldoKnightsSwordD(Player p) {
        super(p);
        addPlayer(HeadE.HAPPY_TALKING, "What do you know about the Imcando dwarves?");
        addNPC(RELDO, HeadE.CALM_TALK, "The Imcando dwarves, you say?");
        addNPC(RELDO, HeadE.CALM_TALK, "Ah yes... for many hundreds of years they were the world's most skilled smiths. They used secret smithing knowledge " +
                "passed down from generation to generation.");
        addNPC(RELDO, HeadE.CALM_TALK, "Unfortunately, about a century ago, the once thriving race was wiped out during the barbarian invasions of that time.");
        addPlayer(HeadE.HAPPY_TALKING, "So are there any Imcando left at all?");
        addNPC(RELDO, HeadE.CALM_TALK, "I believe a few of them survived, but with the bulk of their population destroyed their numbers have dwindled even further.");
        addNPC(RELDO, HeadE.CALM_TALK, "They tend to keep to themselves, and they tend not to tell people they're descendants of the Imcando, which is why " +
                "people think the tribe is extinct. However...");
        addNPC(RELDO, HeadE.CALM_TALK, "... you could try taking them some redberry pie. They REALLY like redberry pie. I believe I remember a couple living in " +
                "Asgarnia near the cliffs on the Asgarnian southern peninsula.", ()->{
            if(p.getQuestManager().getStage(Quest.KNIGHTS_SWORD) == TALK_TO_RELDO)
                p.getQuestManager().setStage(Quest.KNIGHTS_SWORD, FIND_DWARF, true);
        });
    }
}
