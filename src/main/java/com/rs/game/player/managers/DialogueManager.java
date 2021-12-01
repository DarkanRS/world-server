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
package com.rs.game.player.managers;

import com.rs.game.player.Player;
import com.rs.game.player.dialogues.Dialogue;

public class DialogueManager {

	private Player player;
	private Dialogue lastDialogue;

	public DialogueManager(Player player) {
		this.player = player;
	}

	public void continueDialogue(int interfaceId, int componentId) {
		if (player.getTempAttribs().getB("staticDialogue"))
			finishDialogue();
		if (lastDialogue == null)
			return;
		lastDialogue.run(interfaceId, componentId);
	}

	public void finishDialogue() {
		player.getTempAttribs().removeB("staticDialogue");
		if (player.getInterfaceManager().containsChatBoxInter())
			player.getInterfaceManager().closeChatBoxInterface();
		if (lastDialogue == null)
			return;
		lastDialogue.finish();
		lastDialogue = null;
	}

	public boolean execute(Dialogue dialogue, Object... params) {
		if (!player.getControllerManager().useDialogueScript(dialogue.getClass().getName()))
			return false;
		if (lastDialogue != null)
			lastDialogue.finish();
		lastDialogue = dialogue;
		if (lastDialogue == null)
			return false;
		lastDialogue.parameters = params;
		lastDialogue.setPlayer(player);
		lastDialogue.start();
		return true;
	}

}
