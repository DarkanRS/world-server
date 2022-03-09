package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerClickEvent;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerClickHandler;

@PluginEventHandler
public class Eggsterminator {

    public static PlayerClickHandler handleSplatter = new PlayerClickHandler(false, "Splatter") {
        @Override
        public void handle(PlayerClickEvent e) {
//            e.getPlayer().getActionManager().setAction(new Eggsterminator(e.getTarget()));
        }
    };

    ItemEquipHandler eggsterminator = new ItemEquipHandler(new Object[] { Easter2022.EGGSTERMINATOR, Easter2022.PERMANENT_EGGSTERMINATOR }) {
        @Override
        public void handle(ItemEquipEvent e) {
            e.getPlayer().sendMessage("Using the Combat Styles menu, you can choose whether to fire marshmallows (in support of the Chocatrice) or scotch-eggs (in support of the Evil Chicken).");
        }
    };

    ObjectClickHandler crackEgg = new ObjectClickHandler(new Object[ Easter2022.UNCRACKED_EGG ]) {
        @Override
        public void handle(ObjectClickEvent e) {
//            player.sendMessage("You shatter the egg with the Eggsterminator. A chocochick appears.");
//            player.sendMessage("You shatter the egg with the Eggsterminator. A chick appears.");
//            "" SENDS AS GAME MESSAGE AND DIALOGUE

//    "You are x/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."
//    There seems to be some sort of news/announcement event for the first person to crack an egg in a specicific location?
        }
    };

    NPCClickHandler easterChick = new NPCClickHandler(new Object[] { Easter2022.CHICK }) {
        @Override
        public void handle(NPCClickEvent e) {
//            player.sendMessage("You turn the chick into a chocotreat. The shattered remains of the egg disappear.");
//            player.sendMessage("You turn the chick into a drumstick. The shattered remains of the egg disappear.");
//            On collecting the 5th treat, an xp lamp is instantly placed in the players inventory.
        }
    };

}
