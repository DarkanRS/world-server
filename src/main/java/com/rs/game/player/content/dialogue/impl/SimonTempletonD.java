// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.dialogue.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.statements.NPCStatement;
import com.rs.game.player.content.dialogue.statements.PlayerStatement;

public class SimonTempletonD extends Conversation {

	public SimonTempletonD(Player player, int npcId) {
		super(player);

		if (player.getInventory().containsItem(6970)) {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "I have a pyramid top I can sell you!"));
			addNext(new Dialogue(new NPCStatement(npcId, HeadE.CHEERFUL, "Excellent job mate! Here's your money."), () -> {
				int totalMoney = player.getInventory().getAmountOf(6970) * 10000;
				player.getInventory().deleteItem(6970, Integer.MAX_VALUE);
				player.getInventory().addItem(995, totalMoney);
			}));
		} else {
			addNext(new PlayerStatement(HeadE.CHEERFUL, "Hi, what do you do here?"));
			addNext(new NPCStatement(npcId, HeadE.CHEERFUL, "I'll buy any special artefacts you find here in the desert. If you happen to find any pyramid tops, I'll buy them for 10,000 gold each."));
			addNext(new PlayerStatement(HeadE.CHEERFUL, "Great, I'll be sure to come back if I find any."));
		}

		create();
	}

}
