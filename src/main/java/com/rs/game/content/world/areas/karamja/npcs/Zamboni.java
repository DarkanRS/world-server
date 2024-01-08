package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Zamboni {

	public static NPCClickHandler handleZamboni = new NPCClickHandler(new Object[] { 568 }, new String[] { "Talk-to" }, e -> {
		Player player = e.getPlayer();
		player.startConversation(new Dialogue()
				.addNPC(e.getNPC(), HeadE.CHEERFUL, "Hello, welcome to my store!")
				.addNext(() -> ShopsHandler.openShop(player, "karamja_wines_spirits_and_beers")));
	});

}
