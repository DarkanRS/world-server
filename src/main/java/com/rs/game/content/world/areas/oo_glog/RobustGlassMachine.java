package com.rs.game.content.world.areas.oo_glog;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
@PluginEventHandler
public class RobustGlassMachine {

    public static final int RutmirArnholdID = 15044;


    public static ObjectClickHandler RobustGlassMachine = new ObjectClickHandler(new Object[] { 2331 }, e -> {
        if (e.getOption().equalsIgnoreCase("Fill")) {
                        if (e.getPlayer().isQuestComplete(Quest.AS_A_FIRST_RESORT))
                afterAsAFirstResort(e.getPlayer());
            else
                beforeAsAFirstResort(e.getPlayer());
        }
    });

    public static void afterAsAFirstResort(Player player){
        int amount = player.getInventory().getNumberOf(23194);
        if (amount > 0) {
            player.getInventory().deleteItem(23194, amount);
            player.getInventory().addItem(23193, amount);
            player.sendMessage("You magically turn the sandstone into glass without moving since Trent can't find the object and player animations!");
        } else
            player.sendMessage("You do not have any sandstone to turn into glass!");
    }

    public static void beforeAsAFirstResort(Player player) {
        player.startConversation(new Dialogue()
                .addNPC(RutmirArnholdID, HeadE.SHAKING_HEAD, "Do you always use other people's things without asking?.")
                .addPlayer(HeadE.CALM_TALK, "Uh, sorry.")
                .addNPC(RutmirArnholdID, HeadE.SHAKING_HEAD, "I'm ever so busy at the moment trying to get this machine working.")
                .addPlayer(HeadE.CALM_TALK, "Uh, sure.")
        );
    }

}
