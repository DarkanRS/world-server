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
package com.rs.game.content.minigames.wguild;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class KamfreendaDefender extends Conversation {
	private int NPC = 4289;
	public KamfreendaDefender(Player player) {
		super(player);
		if (WarriorsGuild.getBestDefender(player) == 8844)
			addNPC(NPC, HeadE.CALM_TALK, "It seems that you do not have a defender.");
		if (WarriorsGuild.getBestDefender(player) != 8844)
			addNPC(NPC, HeadE.CALM_TALK, "Ah, I see that you have one of the defenders already! Well done.");
		addNPC(NPC, HeadE.CALM_TALK, "I'll release some cyclopses that might drop the next defender for you. Have fun in there.");
		addNPC(NPC, HeadE.CALM_TALK, "Oh, and be careful; the cyclopses will occasionally summon a cyclossus. They are rather mean and can only be hurt with a rune or dragon defender.");
		addNext(()->{player.getInterfaceManager().sendInterface(1058);});
		create();
	}

}
