package com.rs.game.player.content.skills.crafting.urns;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.statements.MakeXStatement;

public class CreateUnfUrnD extends Conversation {

	public CreateUnfUrnD(Player player) {
		super(player);
		addNext(new MakeXStatement(
				new int[] { Urn.DECORATED_MINING.fillId(), Urn.DECORATED_COOKING.fillId(), Urn.DECORATED_FISHING.fillId(), Urn.STRONG_SMELTING.fillId(), Urn.STRONG_WOODCUTTING.fillId(), Urn.INFERNAL.fillId() }, 
				new String[] { "Mining Urns", "Cooking Urns", "Fishing Urns", "Smelting Urns", "Woodcutting Urns", "Prayer Urns" }),
						new MakeUnfOp(player, Urn.CRACKED_MINING, Urn.FRAGILE_MINING, Urn.MINING, Urn.STRONG_MINING, Urn.DECORATED_MINING),
						new MakeUnfOp(player, Urn.CRACKED_COOKING, Urn.FRAGILE_COOKING, Urn.COOKING, Urn.STRONG_COOKING, Urn.DECORATED_COOKING),
						new MakeUnfOp(player, Urn.CRACKED_FISHING, Urn.FRAGILE_FISHING, Urn.FISHING, Urn.STRONG_FISHING, Urn.DECORATED_FISHING),
						new MakeUnfOp(player, Urn.CRACKED_SMELTING, Urn.FRAGILE_SMELTING, Urn.SMELTING, Urn.STRONG_SMELTING),
						new MakeUnfOp(player, Urn.CRACKED_WOODCUTTING, Urn.FRAGILE_WOODCUTTING, Urn.WOODCUTTING, Urn.STRONG_WOODCUTTING),
						new MakeUnfOp(player, Urn.IMPIOUS, Urn.ACCURSED, Urn.INFERNAL));
		create();
	}

}
