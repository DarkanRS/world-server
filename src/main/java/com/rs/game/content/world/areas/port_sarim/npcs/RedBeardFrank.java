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
package com.rs.game.content.world.areas.port_sarim.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.achievements.AchievementSystemDialogue;
import com.rs.game.content.achievements.SetReward;
import com.rs.game.content.quests.piratestreasure.RedbeardFrankPiratesTreasureD;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class RedBeardFrank extends Conversation {
	private static final int npcId = 375;

	public static NPCClickHandler RedBeardFrank = new NPCClickHandler(new Object[]{ npcId }, e -> {
		switch (e.getOption()) {
			//Start Conversation
			case "Talk-to" -> e.getPlayer().startConversation(new RedBeardFrank(e.getPlayer()));
		}
	});

	public RedBeardFrank(Player player) {
		super(player);
		addNPC(npcId, HeadE.CHEERFUL, "Arr, Matey!");
		addOptions("What would you like to say?", new Options() {
			@Override
			public void create() {
				if(!player.isQuestComplete(Quest.PIRATES_TREASURE))
					option("About Pirate's Treasure", new Dialogue()
							.addNext(()->{
								player.startConversation(new RedbeardFrankPiratesTreasureD(player));
							}));

				option("About the Achievement System...",
						new AchievementSystemDialogue(player, npcId, SetReward.FALADOR_SHIELD)
								.getStart());
			}
		});
	}
}
