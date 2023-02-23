package com.rs.game.content.world.areas.death_plateau.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Freda extends Conversation {

	//Identify NPC by ID
	private static final int npcId = 15099;

	public static NPCClickHandler Freda = new NPCClickHandler(new Object[]{npcId}, e -> {
		switch (e.getOption()) {
		//Start Conversation
		case "Talk-to" -> e.getPlayer().startConversation(new Freda(e.getPlayer()));
		case "Trade" -> ShopsHandler.openShop(e.getPlayer(), "fredas_boots");
		}
	});

	public Freda(Player player) {
		super(player);

		addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Can I buy some climbing boots?"));
		addNext(new Dialogue(new NPCStatement(npcId, HeadE.CHEERFUL, "I don't see why not. Let me see what I've got in your size."), () -> {
			ShopsHandler.openShop(player, "fredas_boots");
		}));

		create();
	}
}

