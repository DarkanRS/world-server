package com.rs.game.content.world.areas.oo_glog;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
@PluginEventHandler
public class AcheyTree {
    public static ObjectClickHandler tree = new ObjectClickHandler(new Object[] { 29089, 29088 }, e -> {
        if (e.getOption().equalsIgnoreCase("Chop")) {
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(7055, HeadE.CHILD_ANGRY, "Hey! You no cut down those treesies! Me needs dem for de spit!"));
        }
    });
}
