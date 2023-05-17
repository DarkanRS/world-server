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

import com.rs.engine.cutscene.Cutscene;
import com.rs.engine.dialogue.Dialogue;
import com.rs.game.model.entity.player.Player;

import java.util.Map;

public class DialogueAction extends CutsceneAction {
	
	private Dialogue dialogue;
	private boolean pause;

	public DialogueAction(Dialogue dialogue, int delay, boolean pause) {
		super(null, delay);
		this.dialogue = dialogue;
		this.pause = pause;
	}

	@Override
	public void process(Player player, Map<String, Object> objects) {
		if (pause)
			((Cutscene) objects.get("cutscene")).setDialoguePause(true);
		player.startConversation(dialogue);
	}

}
