package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RutmirArnhold {
    
    public static final int RutmirArnholdID = 15044;

    public static NPCClickHandler RutmirArnhold = new NPCClickHandler(new Object[]{ 15044 }, new String[]{"Talk-to"}, e -> {
        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
            afterAsAFirstResort(e.getPlayer());
        else
            beforeAsAFirstResort(e.getPlayer());
    });

    public static void beforeAsAFirstResort(Player player) {
        player.startConversation(new Dialogue()
                .addPlayer(HeadE.HAPPY_TALKING, "Hi there!")
                .addNPC(RutmirArnholdID, HeadE.SHAKING_HEAD, "I'm ever so busy at the moment trying to get this machine working.")
                .addPlayer(HeadE.CALM_TALK, "Uh, sure.")
        );
    }

    private static void afterAsAFirstResort(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(RutmirArnholdID, HeadE.HAPPY_TALKING, "What are you after, human? I'm busy making robust glass from red sandstone.")
                .addOptions(ops -> {
                    ops.add("What brings you to Oo'glog?")
                            .addPlayer(HeadE.CALM_TALK, "What brings you to Oo'glog?")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK, "There is red sandstone in this area, of the finest quality in all the land.")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK, "At first, it didn't seem safe to venture into a town full of ogresses, but it came to my attention that Oo'glog had become a holiday resort.")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK, "So now I've come to see what I can find.");

                    ops.add("What do you want with red sandstone?")
                            .addPlayer(HeadE.CALM_TALK, "What do you want with red sandstone?")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK, "This special machine I've made will combine red sandstone with special, secret ingredients to produce robust glass.");

                    ops.add("Where can I find red sandstone?")
                            .addPlayer(HeadE.CALM_TALK, "Where can I find red sandstone?")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK, "The very walls of this town are made from the stuff.")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK,"The ogresses don't like me chipping away at their walls, but there is a large separate piece near the outside of the north wall.")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK, "Big piece of rock that it is, you should be able to mine about fifty pieces of red sandstone a day from it.")
                            .addPlayer(HeadE.CALM_TALK, "Well, I'll leave you to it then.");

                    ops.add("What is that machine?")
                            .addPlayer(HeadE.CALM_TALK, "What is that machine?")
                            .addNPC(RutmirArnholdID, HeadE.CALM_TALK, "This special machine I've made will combine red sandstone with special, secret ingredients to produce robust glass.");

                    ops.add("Nothing, sorry to bother you.")
                            .addPlayer(HeadE.CALM_TALK, "Nothing, sorry to bother you.")
                            .addNPC(RutmirArnholdID, HeadE.HAPPY_TALKING, "Okay bye.");
                })
        );
    }
}
