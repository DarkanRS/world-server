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

import com.rs.game.player.Player;
import com.rs.game.player.dialogues.SimpleItemMessage;
import com.rs.game.player.dialogues.SimpleMessage;

public class DialogueAction extends CutsceneAction {

	private int id;
	private String message;

	public DialogueAction(String message) {
		this(-1, message);
	}

	public DialogueAction(int id, String message) {
		super(-1, -1);
		this.id = id;
		this.message = message;
	}

	@Override
	public void process(Player player, Object[] cache) {
		if (id == -1)
			player.getDialogueManager().execute(new SimpleMessage(), message);
		else
			player.getDialogueManager().execute(new SimpleItemMessage(), id, message);
	}

}
