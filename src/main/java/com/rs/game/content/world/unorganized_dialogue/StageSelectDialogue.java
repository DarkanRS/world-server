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

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StageSelectDialogue extends Dialogue {

	private String stageName;
	private Map<String, Dialogue> markedStages;
	private Dialogue directNextReference;

	public StageSelectDialogue(String stageName, Map<String, Dialogue> markedStages) {
		this.stageName = stageName;
		this.markedStages = markedStages;
	}

	public StageSelectDialogue(Dialogue directNextReference) {
		this.directNextReference = directNextReference;
	}

	public String getStageName() {
		return stageName;
	}

	public Map<String, Dialogue> getStages() {
		return markedStages;
	}

	public Dialogue getDirectNextReference() {
		return directNextReference;
	}

}
