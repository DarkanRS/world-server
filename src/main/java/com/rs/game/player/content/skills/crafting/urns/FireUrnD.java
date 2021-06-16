package com.rs.game.player.content.skills.crafting.urns;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.impl.skilling.MakeXActionD;
import com.rs.game.player.content.dialogue.impl.skilling.MakeXItem;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

public class FireUrnD extends Conversation {

	public FireUrnD(Player player) {
		super(player);
		MakeXActionD makeX = new MakeXActionD();
		for (Urn urn : Urn.values()) {
			if (player.getInventory().containsItem(urn.unfId()))
				makeX.addOption(new MakeXItem(player, new Item(urn.unfId()), new Item(urn.nrId()), urn.getFireXp(), 899, urn.getLevel(), Constants.CRAFTING, 2));
		}
		if (!makeX.isEmpty())
			addNext(makeX);
		create();
	}

}
