package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.handlers.ItemEquipHandler;

@PluginEventHandler
public class Eggsterminator {

//    There seems to be some sort of news/announcement event for the first person to crack an egg in a specicific location?
//    On collecting the 5th treat, an xp lamp is instantly placed in the players inventory.

//    "You are x/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."

//    "You shatter the egg with the Eggsterminator. A chocochick appears." SENDS AS GAME MESSAGE AND DIALOGUE
//    "You turn the chick into a chocotreat. The shattered remains of the egg disappear." //TURNS INTO ITEM (looks like an npc/object? dot stays yellow on minimap) AND THEN IS INSTANTLY ADDED TO THE PLAYERS INVENTORY.

    ItemEquipHandler eggsterminator = new ItemEquipHandler(new Object[] { 24145, 24146 }) {
        @Override
        public void handle(ItemEquipEvent e) {
            e.getPlayer().sendMessage("Using the Combat Styles menu, you can choose whether to fire marshmallows (in support of the Chocatrice) or scotch-eggs (in support of the Evil Chicken).");
        }
    };
}
