package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RatBurgiss {
    public static NPCClickHandler handleRatBurgiss = new NPCClickHandler(new Object[] { 5833 }, e -> {
        e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
            {
                addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, what are you after?");
                addOptions("What would you like to say?", new Options() {
                    @Override
                    public void create() {
                        option("About the Achievement System...", new AchievementSystemDialogue(player, e.getNPCId(), SetReward.VARROCK_ARMOR).getStart());
                        option("Farewell.");
                    }
                });
                create();
            }
        });
    });
}
