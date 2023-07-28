package com.rs.game.content;

import com.google.gson.internal.LinkedTreeMap;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.items.LootInterface;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.utils.ItemConfig;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@PluginEventHandler
public class TrophyBones {

    public static ItemOnObjectHandler onBank = new ItemOnObjectHandler(new Object[] { "Bank booth", "Bank", "Bank chest", "Bank table", "Counter", "Shantay chest", "Darkmeyer Treasury" }, new Object[] { 24444 }, e -> {
        if (!e.getObject().getDefinitions(e.getPlayer()).containsOption("Bank") && !e.getObject().getDefinitions(e.getPlayer()).containsOption("Use")) {
            e.getPlayer().sendMessage("This isn't a proper bank.");
            return;
        }
        e.getPlayer().getInventory().getItems().set(e.getItem().getSlot(), null);
        e.getPlayer().getInventory().refresh(e.getItem().getSlot());

        ItemsContainer<Item> items = trophyBoneToContainer(e.getItem());
        if (items == null) {
            e.getPlayer().sendMessage("Your trophy bones don't contain anything.");
            return;
        }
        String name = e.getItem().getMetaDataO("trophyBoneOriginator");
        LootInterface.open((name == null ? "Unknown" : name) + "'s Corpse", e.getPlayer(), items);
    });

    public static ItemsContainer<Item> trophyBoneToContainer(Item bones) {
        Object itemsObj = bones.getMetaDataO("trophyBoneItems");
        if (itemsObj == null)
            return null;
        ItemsContainer<Item> container = null;
        if (itemsObj instanceof List<?> list) {
            container = new ItemsContainer<>(list.size(), false);
            for (Object itemObj : list) {
                if (itemObj instanceof Item item)
                    container.add(item);
                else if (itemObj instanceof LinkedTreeMap<?,?> item) {
                    if (item.get("metadata") != null)
                        container.add(new Item(((Double) item.get("id")).intValue(), ((Double) item.get("amount")).intValue(), (Map<String, Object>) item.get("metadata")));
                    else
                        container.add(new Item(((Double) item.get("id")).intValue(), ((Double) item.get("amount")).intValue()));
                }
            }
        }
        return container.isEmpty() ? null : container;
    }
}
