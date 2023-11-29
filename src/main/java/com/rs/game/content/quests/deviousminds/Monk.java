package com.rs.game.content.quests.deviousminds;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class Monk {
    private static final int MonkDeviousMinds = 3074;

    public static NPCClickHandler handleMonkDeviousMinds = new NPCClickHandler(new Object[]{ MonkDeviousMinds }, new String[]{"Talk-to"}, e -> {
        e.getPlayer().startConversation(new Dialogue()
                .addNPC(MonkDeviousMinds, HeadE.CONFUSED,"Excuse me...oh, wait, I thought you were someone else.")
                .addPlayer(HeadE.CONFUSED,"No problem. Have a good day!")
                .addNPC(MonkDeviousMinds, HeadE.CONFUSED,"And yourself."));
    });
}
