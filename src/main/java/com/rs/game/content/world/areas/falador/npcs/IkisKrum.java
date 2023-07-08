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
package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.skills.thieving.PickPocketAction;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class IkisKrum extends Conversation {
	private static final int npcId = 12237;

	public static NPCClickHandler IkisKrum = new NPCClickHandler(new Object[]{ npcId }, e -> {
		switch (e.getOption()) {
			
			case "Talk-to" -> e.getPlayer().startConversation(new IkisKrum(e.getPlayer()));
		}
	});

	public IkisKrum(Player player) {
		super(player);
		addPlayer(HeadE.HAPPY_TALKING, "Hello.");
		addNPC(npcId, HeadE.HAPPY_TALKING, "Good day, " + player.getPronoun("sir", "madam") + ". What brings you to this end of town?");
		addPlayer(HeadE.HAPPY_TALKING, "Well, what is there to do around here?");
		addNPC(npcId, HeadE.HAPPY_TALKING, "If you're into Mining, plenty! The dwarves have one of the largest mines in the world just under our feet. There's an entrance in the building just north-east of my house.");
		addNPC(npcId, HeadE.FRUSTRATED, "If you're one of these young, loud, hipster sorts you could visit the Party Room, just north of here. That blasted Pete parties all night and I never get any sleep!");
		addPlayer(HeadE.HAPPY_TALKING, "Thanks.");
	}
}
