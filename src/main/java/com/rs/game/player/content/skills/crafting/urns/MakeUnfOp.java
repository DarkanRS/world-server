package com.rs.game.player.content.skills.crafting.urns;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.impl.skilling.MakeXActionD;
import com.rs.game.player.content.dialogue.impl.skilling.MakeXItem;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;

public class MakeUnfOp extends MakeXActionD {

	public MakeUnfOp(Player player, Urn... urns) {
		for (int i = 0;i < urns.length;i++)
			addOption(new MakeXItem(player, new Item(1761, 2), new Item(urns[i].unfId()), urns[i].getUnfXp(), 899, urns[i].getLevel(), Constants.CRAFTING, 4));
	}

}
