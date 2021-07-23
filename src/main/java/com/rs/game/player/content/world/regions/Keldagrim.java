package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Keldagrim {

    public static NPCClickHandler handleHirko = new NPCClickHandler(4558) {
        @Override
        public void handle(NPCClickEvent e) {
            int option = e.getOpNum();
            if (option == 1) {
                e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                    {
                        addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Can I help you at all?");
                        addNext(() -> {
                            ShopsHandler.openShop(e.getPlayer(), "keldagrim_crossbow_shop");
                        });
                        create();
                    }
                });
            }
            if (option == 3) {
                ShopsHandler.openShop(e.getPlayer(), "keldagrim_crossbow_shop");
            }
        }
    };

    public static NPCClickHandler handleNolar = new NPCClickHandler(2158) {
        @Override
        public void handle(NPCClickEvent e) {
            int option = e.getOpNum();
            if (option == 1) {
                e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                    {
                        addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Can I help you at all?");
                        addNext(() -> {
                            ShopsHandler.openShop(e.getPlayer(), "carefree_crafting_stall");
                        });
                        create();
                    }
                });

            }
            if (option == 3) {
                ShopsHandler.openShop(e.getPlayer(), "carefree_crafting_stall");
            }
        }
    };

    public static ObjectClickHandler handleRellekkaEntrance = new ObjectClickHandler(new Object[] { 5973 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer().setNextWorldTile(new WorldTile(2838, 10124, 0));
        }
    };

    public static ObjectClickHandler handleRellekkaExit = new ObjectClickHandler(new Object[] { 5998 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer().setNextWorldTile(new WorldTile(2780, 10161, 0));
        }
    };

    public static ObjectClickHandler handleChaosDwarfBattlefieldEnter = new ObjectClickHandler(new Object[] { 45060 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer().setNextWorldTile(new WorldTile(1520, 4704, 0));
        }
    };

    public static ObjectClickHandler handleChaosDwarfBattlefieldExit = new ObjectClickHandler(new Object[] { 45008 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer().setNextWorldTile(new WorldTile(2817, 10155, 0));
        }
    };

    public static ObjectClickHandler handleBlastFurnaceEntrances = new ObjectClickHandler(new Object[] { 9084, 9138 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer()
            .useStairs(e.getObjectId() == 9084 ? new WorldTile(1939, 4958, 0) : new WorldTile(2931, 10196, 0));
        }
    };

    public static ObjectClickHandler handleBreweryStairCase = new ObjectClickHandler(new Object[] { 6085, 6086 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            if (e.getPlayer().getPlane() == 0)
                e.getPlayer().setNextWorldTile(new WorldTile(2914, 10196, 1));
            else if (e.getPlayer().getPlane() == 1) {
                e.getPlayer().setNextWorldTile(new WorldTile(2917, 10196, 0));
            }
        }
    };
}
