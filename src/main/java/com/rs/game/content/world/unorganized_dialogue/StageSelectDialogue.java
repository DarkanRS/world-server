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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;

public class StageSelectDialogue extends Dialogue {

	private String stageName;
	private Conversation conversation;

	public StageSelectDialogue(String stageName, Conversation conversation) {
		this.stageName = stageName;
		this.conversation = conversation;
	}

	public String getStageName() {
		return stageName;
	}

	public Conversation getConversation() {
		return conversation;
	}

}
