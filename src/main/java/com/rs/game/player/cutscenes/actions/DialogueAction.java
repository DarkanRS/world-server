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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.cutscenes.actions;

import java.util.Map;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Dialogue;

public class DialogueAction extends CutsceneAction {
	
	private Dialogue dialogue;

	public DialogueAction(Dialogue dialogue, int delay) {
		super(null, delay);
		this.dialogue = dialogue;
	}

	@Override
	public void process(Player player, Map<String, Object> objects) {
		player.startConversation(dialogue);
	}

}
