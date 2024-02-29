package com.rs.game.content.world.areas.taverly.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Rohak {

	public static NPCClickHandler handleRohak = new NPCClickHandler(new Object[] { "Rohak" }, e -> e.getPlayer().startConversation(new Dialogue().addOptions((ops) -> {
        ops.add("Hot Dwarven Rock Cake", () -> e.getPlayer().getInventory().addItemDrop(7509, 1));
        ops.add("Cool Dwarven Rock Cake", () -> e.getPlayer().getInventory().addItemDrop(7510, 1));
    })));

}
