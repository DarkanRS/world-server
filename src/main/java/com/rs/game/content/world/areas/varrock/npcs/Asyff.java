package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.cache.loaders.InventoryDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class Asyff extends Conversation {

    public static NPCClickHandler fancyShopOwner = new NPCClickHandler(new Object[] { 554 }, e -> {
        final int npcId = 554;
        switch(e.getOption()) {
            case "Talk-to" -> e.getPlayer().startConversation(new Asyff(e.getPlayer()));
            case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "fancy_clothes_store");
            case "Fur-shop" -> {
                e.getPlayer().getPackets().sendItems(482, Arrays.stream(InventoryDefinitions.getContainer(482).ids).mapToObj(id -> new Item(id, 1)).toArray(Item[]::new));
                e.getPlayer().getInterfaceManager().sendInterface(477);
                e.getPlayer().getPackets().setIFRightClickOps(477, 26, 0, 20, 0, 1, 2, 3);
            }
        }
    });
    public Asyff(Player player) {
        super(player);
        final int npcId = 554;

        addNPC(npcId, HeadE.HAPPY_TALKING,"Now you look like someone who goes to a lot of fancy dress parties.");
        addPlayer(HeadE.CONFUSED,"Errr...what are you saying exactly?");
        addNPC(npcId,HeadE.HAPPY_TALKING,"I'm just saying that perhaps you would like to peruse my selection of garments.");
        addNPC(npcId,HeadE.SKEPTICAL_THINKING,"Or, if that doesn't interest you, then maybe you have something else to offer? I'm always on the look out for interesting or unusual new materials.");
        addOptions(new Options() {
            @Override
            public void create() {
                option("Okay, lets see what you've got then.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Okay, lets see what you've got then.")
                        .addNext(() -> {
                            ShopsHandler.openShop(player, "fancy_clothes_store");
                                }));

                option("Can you make clothing suitable for hunting in?", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Can you make clothing suitable for hunting in?")
                        .addNPC(npcId, HeadE.SKEPTICAL, "Certainly. Take a look at my range of made-to-order items. If you can supply the furs, I'll gladly make any of these for you.")
                                .addNext(() -> {
                                    player.getPackets().sendItems(482, Arrays.stream(InventoryDefinitions.getContainer(482).ids).mapToObj(id -> new Item(id, 1)).toArray(Item[]::new));
                                    player.getInterfaceManager().sendInterface(477);
                                    player.getPackets().setIFRightClickOps(477, 26, 0, 20, 0, 1, 2, 3);
                                        }));

                option("I'm okay thanks.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm okay thanks."));
            }
        });
    }

    public class FurClothingShop {
        enum FurItem {
            POLAR_TOP(10065, 20, 2, 10117),
            POLAR_BOT(10067, 20, 2, 10117),
            WOODS_TOP(10053, 20, 2, 10121),
            WOODS_BOT(10055, 20, 2, 10121),
            FELDI_TOP(10057, 20, 2, 10119),
            FELDI_BOT(10059, 20, 2, 10119),
            DESER_TOP(10061, 20, 2, 10123),
            DESER_BOT(10063, 20, 2, 10123),
            LARUP_HAT(10045, 500, 1, 10095),
            LARUP_TOP(10043, 100, 1, 10093, 10095),
            LARUP_BOT(10041, 100, 1, 10093, 10095),
            GRAAH_HAT(10051, 750, 1, 10099),
            GRAAH_TOP(10049, 150, 1, 10097, 10099),
            GRAAH_BOT(10047, 150, 1, 10097, 10099),
            KYATT_HAT(10039, 1000, 1, 10103),
            KYATT_TOP(10037, 200, 1, 10101, 10103),
            KYATT_BOT(10035, 200, 1, 10101, 10103),
            GLOVES_SI(10075, 600, 2, 10115),
            SPOT_CAPE(10069, 400, 2, 10125),
            SPOTICAPE(10071, 800, 2, 10127);

            private static Map<Integer, FurItem> BY_ITEMID = new HashMap<>();

            static {
                for (FurItem item : FurItem.values())
                    BY_ITEMID.put(item.id, item);
            }

            private final int id;
            private final int gpCost;
            private final int furCost;
            private final int[] furIds;

            FurItem(int itemId, int gpCost, int furCost, int... furIds) {
                this.id = itemId;
                this.gpCost = gpCost;
                this.furCost = furCost;
                this.furIds = furIds;
            }

            private static FurItem forId(int item) {
                return BY_ITEMID.get(item);
            }
        }

        private static void buy(Player player, FurItem item, int amount) {
            player.sendOptionDialogue("Are you sure you'd like to buy " + amount + " " + ItemDefinitions.getDefs(item.id).name + "?", ops -> {
                ops.add("Yes, I am sure ("+ Utils.formatNumber(item.gpCost*amount) + " coins)", () -> {
                    if (item.furCost*amount > player.getInventory().getTotalNumberOf(item.furIds)) {
                        player.sendMessage("You don't have enough furs to exchange for that.");
                        return;
                    }
                    int paid = 0;
                    for (int i = 0;i < amount*2;i++) {
                        for (int furId : item.furIds) {
                            if (player.getInventory().containsItem(furId)) {
                                player.getInventory().deleteItem(furId, 1);
                                if (++paid == (item.furCost*amount))
                                    break;
                            }
                        }
                    }
                    player.getInventory().addItemDrop(item.id, amount);
                });
                ops.add("No thanks.");
            });
        }

        public static ButtonClickHandler furclothingShop = new ButtonClickHandler(477, e -> {
            FurItem item = FurItem.forId(e.getSlotId2());
            if (item == null)
                return;
            String name = ItemDefinitions.getDefs(e.getSlotId2()).name;
            switch(e.getPacket()) {
                case IF_OP1 -> e.getPlayer().sendMessage(name + " costs " + Utils.formatNumber(item.gpCost) + "gp and " + item.furCost + " " + ItemDefinitions.getDefs(item.furIds[0]).name.toLowerCase() + ".");
                case IF_OP2 -> buy(e.getPlayer(), item, 1);
                case IF_OP3 -> buy(e.getPlayer(), item, 5);
                case IF_OP4 -> buy(e.getPlayer(), item, 10);
                default -> {}
            }
        });
    }
}
