package com.rs.game.content.items;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.ItemConstants;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.item.ItemsContainer;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.ItemConfig;

@PluginEventHandler
public class LootInterface {
    public static ButtonClickHandler handleLootInterfaceButtons = new ButtonClickHandler(1284, e -> {
        ItemsContainer<Item> container = e.getPlayer().getTempAttribs().getO("lootInterfaceContainer");
        if (container == null) {
            e.getPlayer().getTempAttribs().removeO("lootInterfaceContainer");
            e.getPlayer().closeInterfaces();
            e.getPlayer().sendMessage("Loot interface container was null. Aborting.");
            return;
        }
        if (e.getComponentId() == 7) {
            Item item = container.get(e.getSlotId());
            if (item == null)
                return;
            switch (e.getPacket()) {
                case IF_OP1 -> {
                    if (e.getPlayer().getInventory().addItem(item)) {
                        container.set(e.getSlotId(), null);
                        e.getPlayer().getPackets().sendUpdateItems(100, container, e.getSlotId());
                    }
                }
                case IF_OP2 -> {
                    if (e.getPlayer().getBank().addItem(item, true)) {
                        container.set(e.getSlotId(), null);
                        e.getPlayer().getPackets().sendUpdateItems(100, container, e.getSlotId());
                    }
                }
                case IF_OP3 -> {
                    container.set(e.getSlotId(), null);
                    e.getPlayer().getPackets().sendUpdateItems(100, container, e.getSlotId());
                }
                case IF_OP4 -> sendExamine(e.getPlayer(), item);
            }
        } else if (e.getComponentId() == 8) {
            for (int slot = 0;slot < container.getSize();slot++) {
                if (container.get(slot) == null)
                    continue;
                if (e.getPlayer().getBank().addItem(container.get(slot), true))
                    container.set(slot, null);
            }
            if (!container.isEmpty())
                e.getPlayer().getPackets().sendItems(100, container);
        } else if (e.getComponentId() == 9) {
            e.getPlayer().sendMessage("You abandon all the items.");
            container.clear();
        } else if (e.getComponentId() == 10) {
            for (int slot = 0;slot < container.getSize();slot++) {
                if (container.get(slot) == null)
                    continue;
                if (e.getPlayer().getInventory().addItemDrop(container.get(slot)))
                    container.set(slot, null);
            }
            if (!container.isEmpty())
                e.getPlayer().getPackets().sendItems(100, container);
        }
        if (container.isEmpty()) {
            e.getPlayer().sendMessage("You've finished looting everything available.");
            e.getPlayer().closeInterfaces();
        }
    });

    public static void open(String title, Player player, ItemsContainer<Item> container, Runnable onClose) {
        player.getPackets().setIFText(1284, 28, title);
        player.getInterfaceManager().sendInterface(1284);
        player.getPackets().sendInterSetItemsOptionsScript(1284, 7, 100, 7, 4, "Take", "Bank", "Discard", "Examine");
        player.getPackets().setIFRightClickOps(1284, 7, 0, 10, 0, 1, 2, 3);
        player.getPackets().sendItems(100, container);
        player.getTempAttribs().setO("lootInterfaceContainer", container);
        player.setCloseInterfacesEvent(() -> {
            player.getTempAttribs().removeO("lootInterfaceContainer");
            if (onClose != null)
                onClose.run();
            for (Item item : container.toArray())
                if (item != null)
                    player.getInventory().addItemDrop(item);

        });
    }

    public static void open(String title, Player player, ItemsContainer<Item> container) {
        open(title, player, container, null);
    }

    public static void sendExamine(Player player, Item item) {
        ItemDefinitions def = ItemDefinitions.getDefs(item.getId());
        player.sendMessage(ItemConfig.get(item.getId()).getExamine(item) + (ItemConstants.isTradeable(item) ? (" General store: " + Utils.formatTypicalInteger(item.getDefinitions().getSellPrice()) + " High Alchemy: " + Utils.formatTypicalInteger(def.getHighAlchPrice())) : ""));
        if (item.getMetaData("combatCharges") != null)
            player.sendMessage("<col=FF0000>It looks like it will last another " + Utils.ticksToTime(item.getMetaDataI("combatCharges")));
        else if (item.getMetaData("brawlerCharges") != null)
            player.sendMessage("These gloves have " + item.getMetaDataI("brawlerCharges") + " charges left.");
    }
}
