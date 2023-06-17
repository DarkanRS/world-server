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
import com.rs.game.content.minigames.wguild.WarriorsGuild;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;

public class ShotputD extends Conversation {

	public ShotputD(Player player, boolean is18LB) {
		super(player);
		WarriorsGuild controller = player.getControllerManager().getController(WarriorsGuild.class);
		if (controller == null)
			return;
		player.setNextAnimation(new Animation(827));
		
		addOptions(ops -> {
			ops.add("Standing Throw.", () -> {
				controller.prepareShotput((byte) 0, is18LB);
				player.setNextAnimation(new Animation(15079));
			});
			ops.add("Step and throw.", () -> {
				controller.prepareShotput((byte) 1, is18LB);
				player.setNextAnimation(new Animation(15080));
			});
			ops.add("Spin and throw.", () -> {
				controller.prepareShotput((byte) 2, is18LB);
				player.setNextAnimation(new Animation(15078));
			});
		});
	}
}
