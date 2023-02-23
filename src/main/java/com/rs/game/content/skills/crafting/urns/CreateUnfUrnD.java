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
package com.rs.game.content.skills.crafting.urns;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.model.entity.player.Player;

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
