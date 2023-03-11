package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.shieldofarrav.BaraekShieldOfArravD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
@PluginEventHandler
public class Baraek {
    public static NPCClickHandler handleBaraek = new NPCClickHandler(new Object[] { 547 }, e -> {
        if(e.getPlayer().isQuestComplete(Quest.SHIELD_OF_ARRAV))
            e.getPlayer().sendMessage("Nothing interesting happens");
        else
            e.getPlayer().startConversation(new BaraekShieldOfArravD(e.getPlayer()).getStart());
    });
}
