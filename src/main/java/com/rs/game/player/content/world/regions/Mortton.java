package com.rs.game.player.content.world.regions;

import com.rs.game.player.content.Potions;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Mortton {
	
	public static ItemOnNPCHandler handleRazmireCure = new ItemOnNPCHandler(7899) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			if (e.getPlayer().getVars().getVarBit(e.getNPC().getDefinitions().varpBit) == 1) {
				e.getPlayer().sendMessage("He's already cured!");
				return;
			}
			switch(e.getItem().getId()) {
			case 3408:
			case 3410:
			case 3412:
			case 3414:
				e.getItem().setId(e.getItem().getId() == 3414 ? Potions.VIAL : e.getItem().getId()+2);
				e.getPlayer().getVars().setVarBit(e.getNPC().getDefinitions().varpBit, 1);
				e.getPlayer().getInventory().refresh();
				break;
			}
		}
	};
	
	public static NPCClickHandler handleRazmire = new NPCClickHandler(7899) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Talk-to":
				e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
					{
						if (e.getPlayer().getVars().getVarBit(e.getNPC().getDefinitions().varpBit) == 1)
							addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, there!");
						else
							addNPC(e.getNPCId(), HeadE.DIZZY, "Auuhghhhh...");
						create();
					}
				});
				break;
			case "Trade-General-Store":
				ShopsHandler.openShop(e.getPlayer(), "razmire_general");
				break;
			case "Trade-Builders-Store":
				ShopsHandler.openShop(e.getPlayer(), "razmire_builders");
				break;
			}
		}
	};

}
