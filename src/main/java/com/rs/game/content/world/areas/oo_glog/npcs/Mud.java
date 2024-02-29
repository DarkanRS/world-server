package com.rs.game.content.world.areas.oo_glog.npcs;

import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
@PluginEventHandler
public class Mud  {

    public static ItemClickHandler applyMud = new ItemClickHandler(new Object[] { 12558 }, e -> {
        Player player = e.getPlayer();
        if(e.getOption().equalsIgnoreCase("Apply")) {
            if (player.getEquipment().getHatId() != -1) {
                player.getInventory().replace(12558, player.getEquipment().getHatId());
                player.getEquipment().setSlot(Equipment.HEAD, new Item(12558));
            } else {
                player.getInventory().deleteItem(12558, 1);
                player.getEquipment().setSlot(Equipment.HEAD, new Item(12558));
            }
            player.getEquipment().refresh(Equipment.HEAD);
            player.getAppearance().generateAppearanceData();
        }
        //TODO DOESN'T RESPOND IN INV?
//        if(e.getOption().equalsIgnoreCase("Remove")) {
//            player.sendMessage("The mud mask crumbles as you rub it off your face.");
//            player.getEquipment().deleteSlot(Equipment.HEAD);
//            player.getEquipment().refresh(Equipment.HEAD);
//            player.getAppearance().generateAppearanceData();
//        }

    });
}
