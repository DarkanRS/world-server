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
package com.rs.game.content.world.areas.burthorpe.npcs.announcers;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;


@PluginEventHandler
public class ShanomiWorld extends NPC {

	private static final int npcId = 4290;

	public ShanomiWorld(int id, Tile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC(){
		super.processNPC();
		if(Utils.random(0, 100) == 0) { //TODO add a lock to ensure this cannot collide with itself.
			WorldTasks.scheduleTimer(1, tick -> {
				//8 seconds based on 0.6 TPS
				if (tick >= 10 * 13)
					return false;
				if (tick == 0) {
					setNextForceTalk(new ForceTalk("Think not dishonestly."));
				} else if (tick == 1 * 13) {
					setNextForceTalk(new ForceTalk("The Way in training is."));
				} else if (tick == 2 * 13) {
					setNextForceTalk(new ForceTalk("Acquainted with every art become."));
				} else if (tick == 3 * 13) {
					setNextForceTalk(new ForceTalk("Ways of all professions know you."));
				} else if (tick == 4 * 13) {
					setNextForceTalk(new ForceTalk("Gain and loss between you must distinguish."));
				} else if (tick == 5 * 13) {
					setNextForceTalk(new ForceTalk("Judgment and understanding for everything develop you must."));
				} else if (tick == 6 * 13) {
					setNextForceTalk(new ForceTalk("Those things which cannot be seen, perceive them."));
				} else if (tick == 7 * 13) {
					setNextForceTalk(new ForceTalk("Trifles pay attention even to."));
				} else if (tick == 8 * 13) {
					setNextForceTalk(new ForceTalk("Do nothing which is of no use."));
				} else if (tick == 9 * 13) {
					setNextForceTalk(new ForceTalk("Way of the Warrior this is."));
				}
				return true;
			});
		}
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { npcId }, (npcId, tile) -> new ShanomiWorld(npcId, tile));
}
