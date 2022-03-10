package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.game.player.Equipment;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.*;
import com.rs.plugin.handlers.*;

@PluginEventHandler
public class Eggsterminator {

    //TODO Login event should remove the eggsterminator and not trigger a item click event? sends additional messages to chat that player shouldnt see.
    public static LoginHandler removeTempEggsterminator = new LoginHandler() {
        @Override
        public void handle(LoginEvent e) {
            if (e.getPlayer().getEquipment().getWeaponId() == Easter2022.EGGSTERMINATOR) {
                e.getPlayer().getEquipment().deleteItem(Easter2022.EGGSTERMINATOR, 1);
                e.getPlayer().getEquipment().refresh(Equipment.WEAPON);
                e.getPlayer().getAppearance().generateAppearanceData();
                e.getPlayer().sendMessage("Your Eggsterminator has vanished. Start a hunt to obtain a new one and even unlock an enchanted permanent version.");
            }
        }
    };

    public static PlayerClickHandler handleSplatter = new PlayerClickHandler(false, "Splatter") {
        @Override
        public void handle(PlayerClickEvent e) {
//            e.getPlayer().getActionManager().setAction(new Eggsterminator(e.getTarget()));
        }
    };

    public static ItemEquipHandler eggsterminator = new ItemEquipHandler(new Object[] { Easter2022.EGGSTERMINATOR, Easter2022.PERMANENT_EGGSTERMINATOR}) {
        @Override
        public void handle(ItemEquipEvent e) {
            //TODO Prevent item from unequipping. It should be handled by this conversation.
            System.out.println("Event triggered." + e.dequip() + " " + e.getItem().getId());
            if (e.dequip() && e.getItem().getId() == Easter2022.EGGSTERMINATOR) {
                e.getPlayer().startConversation(new Dialogue().addOptions("Destroy the Eggsterminator?", new Options() {
                    @Override
                    public void create() {
                        option("Yes", () -> {
                            e.getPlayer().getEquipment().deleteItem(Easter2022.EGGSTERMINATOR, 1);
                            e.getPlayer().getEquipment().refresh(Equipment.WEAPON);
                            e.getPlayer().getAppearance().generateAppearanceData();
                        });
                        option("No");
                    }
                }));
                return;
            }
            e.getPlayer().sendMessage("Using the Combat Styles menu, you can choose whether to fire marshmallows (in support of the Chocatrice) or scotch-eggs (in support of the Evil Chicken).");
        }
    };

    public static ObjectClickHandler crackEgg = new ObjectClickHandler(new Object[ Easter2022.UNCRACKED_EGG ]) {
        @Override
        public void handle(ObjectClickEvent e) {
//            player.sendMessage("You shatter the egg with the Eggsterminator. A chocochick appears.");
//            player.sendMessage("You shatter the egg with the Eggsterminator. A chick appears.");
//            "" SENDS AS GAME MESSAGE AND DIALOGUE

//    "You are x/3 of the way to claiming a permanent version of the Eggsterminator, for finding 5 eggs this hunt."
//    There seems to be some sort of news/announcement event for the first person to crack an egg in a specicific location?
        }
    };

    public static NPCClickHandler easterChick = new NPCClickHandler(new Object[] { Easter2022.CHICK }) {
        @Override
        public void handle(NPCClickEvent e) {
//            player.sendMessage("You turn the chick into a chocotreat. The shattered remains of the egg disappear.");
//            player.sendMessage("You turn the chick into a drumstick. The shattered remains of the egg disappear.");
//            On collecting the 5th treat, an xp lamp is instantly placed in the players inventory.
        }
    };

}
