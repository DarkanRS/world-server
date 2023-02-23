package com.rs.game.content.items;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.Statement;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Mindspike {
	
	public static ItemClickHandler changeElement = new ItemClickHandler(new Object[] { 23044, 23045, 23046, 23047 }, new String[] { "Change element" }, e -> {
		Dialogue selectElement = new Dialogue(new Statement() {
			@Override
			public void send(Player player) {
				player.getInterfaceManager().sendChatBoxInterface(1235);
			}

			@Override
			public int getOptionId(int componentId) {
				return switch(componentId) {
				case 2 -> 0;
				case 5 -> 1;
				case 8 -> 2;
				case 11 -> 3;
				default -> -1;
				};
			}

			@Override
			public void close(Player player) {
				player.getInterfaceManager().closeChatBoxInterface();
			}
		});
		for (int itemId : new int[] { 23044, 23045, 23046, 23047 })
			selectElement.addNext(() -> {
				e.getItem().setId(itemId);
				e.getPlayer().getInventory().refresh(e.getItem().getSlot());
			});
		e.getPlayer().startConversation(selectElement);
	});
	
}
