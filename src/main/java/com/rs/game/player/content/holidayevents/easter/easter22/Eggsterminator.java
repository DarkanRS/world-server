package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Eggsterminator {

    private static final int EGGSTERMINATOR = 24145;
    private static final int XP_LAMP = 123;
    private static final int EGG = 1;
    private static final int CHICK = 1;

    ItemEquipHandler eggsterminator = new ItemEquipHandler(new Object[] { 24145, 24146 }) {
        @Override
        public void handle(ItemEquipEvent e) {
            e.getPlayer().sendMessage("Using the Combat Styles menu, you can choose whether to fire marshmallows (in support of the Chocatrice) or scotch-eggs (in support of the Evil Chicken).");
        }
    };

    ObjectClickHandler crackEgg = new ObjectClickHandler(new Object[ EGG ]) {
        @Override
        public void handle(ObjectClickEvent e) {
//            shoot the egg
//            when the egg cracks, send message.
//            "You shatter the egg with the Eggsterminator. A chick appears." SENDS AS GAME MESSAGE AND DIALOGUE

//    "You are x/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."
//    There seems to be some sort of news/announcement event for the first person to crack an egg in a specicific location?

        }
    };

    NPCClickHandler easterChick = new NPCClickHandler(new Object[] { CHICK }) {
        @Override
        public void handle(NPCClickEvent e) {
//            "You turn the chick into a chocotreat. The shattered remains of the egg disappear." //TURNS INTO ITEM (looks like an npc/object? dot stays yellow on minimap) AND THEN IS INSTANTLY ADDED TO THE PLAYERS INVENTORY.
//            "You turn the chick into a drumstick. The shattered remains of the egg disappear." //TURNS INTO ITEM (looks like an npc/object? dot stays yellow on minimap) AND THEN IS INSTANTLY ADDED TO THE PLAYERS INVENTORY.
//            On collecting the 5th treat, an xp lamp is instantly placed in the players inventory.
        }
    };
}
