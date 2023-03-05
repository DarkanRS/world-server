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
package com.rs.game.content.quests.scorpioncatcher;

import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KhazScorp extends NPC {

	public KhazScorp(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public boolean withinDistance(Player p, int distance) {
		boolean known = switch(getId()) {
			case ScorpionCatcher.SCORP_1 -> p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp1LocKnown");
			case ScorpionCatcher.SCORP_2 -> p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp2LocKnown");
			case ScorpionCatcher.SCORP_3 -> p.getQuestManager().getAttribs(Quest.SCORPION_CATCHER).getB("scorp3LocKnown");
			default -> true;
		};
		return p.getQuestManager().getStage(Quest.SCORPION_CATCHER) == ScorpionCatcher.LOOK_FOR_SCORPIONS && 
				known && !ScorpionCatcher.caughtScorp(p, getId()) && super.withinDistance(p, distance) ;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { ScorpionCatcher.SCORP_1, ScorpionCatcher.SCORP_2, ScorpionCatcher.SCORP_3 }, (npcId, tile) -> new KhazScorp(npcId, tile));
}
