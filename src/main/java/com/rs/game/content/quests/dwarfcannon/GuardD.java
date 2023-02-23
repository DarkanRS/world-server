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
package com.rs.game.content.quests.dwarfcannon;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GuardD extends Conversation {

	private static final int GUARD = 206;

	public static NPCClickHandler talkToLawgof = new NPCClickHandler(new Object[] { GUARD }, e -> e.getPlayer().startConversation(new GuardD(e.getPlayer())));

	public GuardD(Player player) {
		super(player);
		int currentStage = player.getQuestManager().getStage(Quest.DWARF_CANNON);
		switch(currentStage) {
		case 0:
			addPlayer(HeadE.NO_EXPRESSION, "Hello.");
			addNPC(GUARD, HeadE.ANGRY, "Don't distract me while I'm on duty! This mine has to be protected!");
			addPlayer(HeadE.CONFUSED, "What's going to attack a mine?");
			addNPC(GUARD, HeadE.ANGRY, "Goblins! They wander everywhere, attacking anyone they think is small enough to be an easy victim. We need more cannons to fight them off properly.");
			addPlayer(HeadE.NO_EXPRESSION, "Cannons? Those sound expensive.");
			addNPC(GUARD, HeadE.CALM, "A new cannon can cost 750,000 coins, and the ammo isn't easy to get, but they can do an enormous amount of damage with each shot. When you've got an important mine like this one to protect, it's worth the expense.");
			addPlayer(HeadE.HAPPY_TALKING, "Thanks for the information.");
			addNPC(GUARD, HeadE.CALM, "You're welcome. Now please let me get on with my guard duties.");
			addPlayer(HeadE.NO_EXPRESSION, "Alright, I'll leave you alone now.");
			break;
		default:
			addPlayer(HeadE.NERVOUS, "I should probably leave him to his duties.");
		}
		create();
	}

}
