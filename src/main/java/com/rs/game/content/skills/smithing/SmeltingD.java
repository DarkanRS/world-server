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
package com.rs.game.content.skills.smithing;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.content.skills.smithing.Smelting.SmeltingBar;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.MakeXStatement;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;

public class SmeltingD extends Conversation {
	
	public SmeltingD(Player player, GameObject object) {
		super(player);
		
		List<SmeltingBar> bars = new ArrayList<>();
		for (SmeltingBar bar : SmeltingBar.values())
			if (player.getInventory().containsItems(bar.getItemsRequired()) && player.getSkills().getLevel(Skills.SMITHING) >= bar.getLevelRequired())
				bars.add(bar);
		
		if (bars.isEmpty()) {
			addSimple("You don't have any ores that you are skilled enough to smelt.");
			create();
			return;
		}
		
		Dialogue makeX = addNext(new MakeXStatement(bars.stream().mapToInt(bar -> bar.getProducedBar().getId()).toArray()));
		for (SmeltingBar bar : bars)
			makeX.addNext(() -> player.getActionManager().setAction(new Smelting(bar, object, MakeXStatement.getQuantity(player))));
		
		create();
	}
}
