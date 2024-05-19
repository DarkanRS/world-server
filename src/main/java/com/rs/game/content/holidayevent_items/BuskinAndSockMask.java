package com.rs.game.content.holidayevent_items;

import com.rs.game.model.entity.player.Equipment;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class BuskinAndSockMask {

    public static ItemClickHandler BuskinAndSockMask = new ItemClickHandler(new Object[] { 22322, 22323 }, new String[] { "Flip" }, e -> {
        int swap = e.getItem().getId() == 22322 ? 22323 : 22322;
        if (e.isEquipped()) {
            e.getPlayer().getEquipment().setSlot(Equipment.HEAD, new Item(swap));
            e.getPlayer().getAppearance().generateAppearanceData();
        }
        else
            e.getPlayer().getInventory().replace(e.getItem(), new Item(swap));
    });
}
