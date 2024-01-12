package com.rs.game.content.skills.thieving;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;
@PluginEventHandler
public class Blackjacking {
    public static NPCClickHandler loothandler = new NPCClickHandler(new Object[] { 1903 }, new String[] {"Pickpocket"}, e -> {
        if(e.getNPC().getTempAttribs().getO("K.O") == null) {
            e.getPlayer().sendMessage("You should knock the Menaphite Thug out first.");
            return;
        }
        if(e.getNPC().getTempAttribs().getO("K.O") == e.getPlayer()) {
            e.getPlayer().getActionManager().setAction(new LootThug());
        }
        else
            e.getPlayer().sendMessage("Someone else knocked out that target.");
    });

    public static NPCClickHandler lure = new NPCClickHandler(new Object[] { 1903 }, new String[] {"Lure"}, e -> {
        String[] responses = new String[]{
                "Watch out! The fellow behind you has a club!",
                "Behind you! A three-headed monkey!",
                "That's the third biggest platypus I've ever seen!",
                "Look over THERE!",
                "Look! An eagle!",
                "Your shoelace is untied."
        };
        e.getPlayer().startConversation(new Dialogue()
                .addPlayer(HeadE.SKEPTICAL, responses[(Utils.random(5))])
                .addNPC(e.getNPCId(), HeadE.SCARED, "Oh nooooo!")
                .addNext(() -> {
                    e.getNPC().follow(e.getPlayer());
                    e.getNPC().getTempAttribs().setO("lured", e.getPlayer());
                    e.getPlayer().sendMessage();
                    WorldTasks.delay(Ticks.fromSeconds(10), () -> {
                                e.getNPC().getTempAttribs().setO("lured", e.getPlayer());
                                e.getNPC().getActionManager().forceStop();
                            }
                    );
                })
        );
    });
}
