package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Zamboni {

	public static NPCClickHandler handleZamboni = new NPCClickHandler(new Object[] { 568 }, e -> {
		if(e.getOption().equals("Talk-to"))
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(e.getNPCId(), HeadE.CHEERFUL, "Hello, welcome to my store!");
					addNext(()->{
						ShopsHandler.openShop(e.getPlayer(), "karamja_wines_spirits_and_beers");});
					create();
				}
			});
		if(e.getOption().equalsIgnoreCase("trade"))
			ShopsHandler.openShop(e.getPlayer(), "karamja_wines_spirits_and_beers");
	});

}
