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
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
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
