package com.rs.game.content.holidayevent_items;


import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;


@PluginEventHandler
public class BoneBrooch {
    public static ItemClickHandler handleItemOption = new ItemClickHandler(new Object[] {"Bone brooch" }, new String[] { "Wear" }, e -> {
        if (e.getPlayer().inCombat(10000) || e.getPlayer().hasBeenHit(10000)) {
            e.getPlayer().sendMessage("You wouldn't want to use that right now.");
            return;
        }


        if (e.getItem().getName().equals("Bone brooch")) {
            e.getPlayer().stopAll(true, true, true);
            e.getPlayer().lock();
            e.getPlayer().setNextAnimation(new Animation(14870));
            e.getPlayer().setNextSpotAnim(new SpotAnim(2838));
            WorldTasks.schedule(new Task() {
                @Override
                public void run() {
                    transformInto(e.getPlayer(), 12373);
                }
            }, 1);
        }
    });



public static void transformInto(Player player, int npcId) {
    player.stopAll(true, true, true);
    player.lock();
    player.getAppearance().transformIntoNPC(npcId);
    player.getInterfaceManager().sendSub(Sub.TAB_INVENTORY, 375);
    player.getTempAttribs().setB("TransformationRing", true);
}


}

