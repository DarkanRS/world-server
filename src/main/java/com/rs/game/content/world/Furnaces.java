package com.rs.game.content.world;

import com.rs.game.content.skills.smithing.MoltenGlassMaking;
import com.rs.game.content.skills.smithing.SmeltingD;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Furnaces {
    public static ObjectClickHandler furnaceOps = new ObjectClickHandler(new Object[] { "Furnace", "Small furnace", "Clay forge", "Lava Furnace" }, e -> {
        if (e.getPlayer().getInventory().containsItems(new Item(MoltenGlassMaking.SODA_ASH), new Item(MoltenGlassMaking.BUCKET_OF_SAND))) {
            MoltenGlassMaking.openDialogue(e.getPlayer());
            return;
        }
        e.getPlayer().startConversation(new SmeltingD(e.getPlayer(), e.getObject()));
    });
}
