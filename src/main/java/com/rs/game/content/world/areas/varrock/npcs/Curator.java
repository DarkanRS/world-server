package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.quests.shieldofarrav.MuseumCuratorArravD;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Curator {
    public static NPCClickHandler handleMuseumCurator = new NPCClickHandler(new Object[] { 646 }, e -> {
        if(e.getOption().equalsIgnoreCase("talk-to"))
            e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                {
                    addOptions("What would you like to say?", new Options() {
                        @Override
                        public void create() {
                            option("About Shield Of Arrav...", new MuseumCuratorArravD(player).getStart());
                            option("Farewell.", new Dialogue());
                        }
                    });
                    create();
                }
            });
    });
}
