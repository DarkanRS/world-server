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
package com.rs.game.content.minigames.treasuretrails;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class UgiDialogue extends Conversation {
	
	public static NPCClickHandler talk = new NPCClickHandler(new Object[] { 5141 }, e -> {
		if (e.getNPC() instanceof Ugi ugi)
			e.getPlayer().startConversation(new UgiDialogue(e.getPlayer(), ugi));
	});

	public UgiDialogue(Player player, Ugi ugi) {
		super(player);
		
		if (ugi.getOwner() == player && player.getTreasureTrailsManager().getPhase() == 4) {
			addNPC(ugi.getId(), HeadE.CHEERFUL, TreasureTrailsManager.UGIS_QUOTES[Utils.random(TreasureTrailsManager.UGIS_QUOTES.length)]);
			addPlayer(HeadE.CONFUSED, "What?");
			addNext(() -> {
				ugi.finish();
				player.getTreasureTrailsManager().setPhase(5);
				player.getTreasureTrailsManager().setNextClue(TreasureTrailsManager.SOURCE_EMOTE, false);
			});
		} else
			addNPC(ugi.getId(), HeadE.CONFUSED, TreasureTrailsManager.UGI_BADREQS);
		create();
	}
}
