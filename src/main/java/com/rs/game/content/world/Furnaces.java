package com.rs.game.content.world;

import com.rs.game.content.skills.smithing.MoltenGlassMaking;
import com.rs.game.content.skills.smithing.SmeltingD;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Furnaces {

    public static void useFurnace(Player player, GameObject furnace) {
        if (player.getInventory().containsItems(new Item(MoltenGlassMaking.SODA_ASH), new Item(MoltenGlassMaking.BUCKET_OF_SAND))) {
            MoltenGlassMaking.openDialogue(player);
            return;
        }
        player.startConversation(new SmeltingD(player, furnace));
    }

    public static ObjectClickHandler furnaceOps = new ObjectClickHandler(new Object[] { "Furnace", "Small furnace", "Clay forge", "Lava Furnace" }, e -> useFurnace(e.getPlayer(), e.getObject()));

}
