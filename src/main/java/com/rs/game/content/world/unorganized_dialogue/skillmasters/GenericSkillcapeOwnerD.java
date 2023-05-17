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
package com.rs.game.content.world.unorganized_dialogue.skillmasters;

import com.rs.engine.dialogue.Conversation;
import com.rs.game.content.Skillcapes;
import com.rs.game.model.entity.player.Player;

public class GenericSkillcapeOwnerD extends Conversation {

	public GenericSkillcapeOwnerD(Player player, int npcId, Skillcapes cape) {
		super(player);

		addOption("Choose an option", "What is that cape you're wearing?", "Bye.")
		.addNext(cape.getOffer99CapeDialogue(player, npcId));

		create();
	}
}
