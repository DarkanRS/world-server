package com.rs.game.content.world.areas.lunar_isle;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class LunarIsle {

    public static NPCClickHandler enterBabaYagaHouse = new NPCClickHandler(new Object[] { 4512 }, e -> {
        e.getPlayer().moveTo(Tile.of(3103, 4447, 0));
    });

    public static ObjectClickHandler exitBabaYagaHouse = new ObjectClickHandler(new Object[] { 16774 }, e -> {
        e.getPlayer().moveTo(Tile.of(2087, 3930, 0));
    });

    public static NPCClickHandler babaYaga = new NPCClickHandler(new Object[] { 4513 }, e -> {
        switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Dialogue()
                .addNPC(4513, HeadE.CHEERFUL, "Ah, a visitor from a distant land. How can I help?")
                .addOptions(ops -> {
                    if (e.getPlayer().isQuestComplete(Quest.LUNAR_DIPLOMACY))
                        ops.add("Have you got anything to trade?");

                    ops.add("It's a very interesting house you have here.")
                        .addPlayer(HeadE.CONFUSED, "It's a very interesting house you have here. Does he have a name?")
                        .addNPC(4513, HeadE.CHEERFUL, "Why of course. It's Berty.")
                        .addPlayer(HeadE.CONFUSED, "Berty? Berty the Chicken leg house?")
                        .addNPC(4513, HeadE.CHEERFUL, "Yes.")
                        .addPlayer(HeadE.CONFUSED, "May I ask why?")
                        .addNPC(4513, HeadE.LAUGH, "It just has a certain ring to it, don't you think? Beeerteeee!")
                        .addPlayer(HeadE.SHAKING_HEAD, "You're ins...")
                        .addNPC(4513, HeadE.CHEERFUL, "Insane? Very.");

                    ops.add("I'm good, thanks, bye.")
                        .addPlayer(HeadE.CHEERFUL, "I'm good, thanks, bye.");
                })
            );
            case "Trade" -> {
                if (e.getPlayer().isQuestComplete(Quest.LUNAR_DIPLOMACY))
                    ShopsHandler.openShop(e.getPlayer(), "baba_yagas_magic_shop");
            }
        }
    });
}
