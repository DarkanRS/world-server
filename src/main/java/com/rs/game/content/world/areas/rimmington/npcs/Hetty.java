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
package com.rs.game.content.world.areas.rimmington.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Hetty extends Conversation {
	private static final int npcId = 307;

	public static NPCClickHandler Hetty = new NPCClickHandler(new Object[]{ npcId }, e -> {
		switch (e.getOption()) {
		//Start Conversation
		case "Talk-to" -> e.getPlayer().startConversation(new Hetty(e.getPlayer()));
		}
	});

	public Hetty(Player player) {
		super(player);
		addNPC(npcId, HeadE.HAPPY_TALKING, "What could you want with an old woman like me?");
		addOptions(new Options() {
			@Override
			public void create() {

				if (Miniquest.WITCHES_POTION.isImplemented()){
					option("I'm looking for work.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "I'm looking for work.")
							.addNPC(npcId, HeadE.HAPPY_TALKING, "Hmmm ... Perhaps you could do something that would help both of us.")
					);
				}
				option("You look like a witch.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "You look like a witch.")
									.addNPC(npcId, HeadE.HAPPY_TALKING, "Yes, I suppose I'm not being very subtle about it. I fear I may get a visit from the witch hunters of Falador before long.")
				);

				option("Nothing, thanks.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Nothing, thanks.")
						.addNPC(npcId, HeadE.FRUSTRATED, "hmph.")
				);

			}
		});
	}
}
