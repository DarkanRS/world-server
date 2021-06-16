package com.rs.game.player.content.dialogue.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.dialogue.statements.PlayerStatement;
import com.rs.utils.shop.ShopsHandler;

public class FredaD extends Conversation {
		
	public FredaD(Player player, int npcId) {
		super(player);

			addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Can I buy some climbing boots?"));
			addNext(new Dialogue(new NPCStatement(npcId, HeadE.CHEERFUL, "I don't see why not. Let me see what I've got in your size."), () -> {
				ShopsHandler.openShop(player, "fredas_boots");
			}));

		create();

	}
}
