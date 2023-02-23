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
package com.rs.game.content.skills.firemaking;

import java.util.Arrays;

import com.rs.game.content.skills.firemaking.Bonfire.Log;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.engine.dialogue.statements.MakeXStatement.MakeXType;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;

public class BonfireD extends Conversation {
	public BonfireD(Player player, GameObject object, Log[] logs) {
		super(player);
		
		Dialogue makeX = addNext(new MakeXStatement(MakeXType.SELECT, "Which logs do you want to add to the bonfire?", Arrays.stream(logs).mapToInt(log -> log.getLogId()).toArray(), -1));
		for (Log log : logs)
			makeX.addNext(() -> player.getActionManager().setAction(new Bonfire(log, object)));
	}
}
