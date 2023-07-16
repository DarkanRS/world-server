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
package com.rs.engine.cutscene.actions;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

import java.util.Map;

public class NPCForceTalkAction extends CutsceneAction {

	private String text;

	public NPCForceTalkAction(String key, String text, int actionDelay) {
		super(key, actionDelay);
		this.text = text;
	}

	@Override
	public void process(Player player, Map<String, Object> objects) {
		NPC npc = (NPC) objects.get(getObjectKey());
		npc.setNextForceTalk(new ForceTalk(text));
	}

}
