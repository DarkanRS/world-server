package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PortKhazard {
	public static NPCClickHandler handleKhazardShopkeeper = new NPCClickHandler(555) {
		@Override
		public void handle(NPCClickEvent e) {
			int option = e.getOpNum();
			if (option == 1)
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {{
					addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Can I help you at all?");
					addNext(() -> {
                        ShopsHandler.openShop(e.getPlayer(), "khazard_general_store");
                    });
                    create();
				}});
			if (option == 3)
				ShopsHandler.openShop(e.getPlayer(), "khazard_general_store");

		}};
}
