package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.shieldofarrav.CharlieTheTrampArravD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Charlie {
    public static NPCClickHandler handleCharlie = new NPCClickHandler(new Object[] { 641 }, e -> {
        if(e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
            e.getPlayer().sendMessage("Nothing interesting happens");
        else
            e.getPlayer().startConversation(new CharlieTheTrampArravD(e.getPlayer()).getStart());
    });
}
