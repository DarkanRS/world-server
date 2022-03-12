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
package com.rs.game.player.managers;

import com.rs.game.Entity;
import com.rs.game.player.actions.interactions.Interaction;

public final class InteractionManager {

	private Entity player;
	private Interaction interaction;

	public InteractionManager(Entity player) {
		this.player = player;
	}

	public void process() {
		if (interaction != null && (interaction.isStopped() || player.isDead() || !interaction.process(player)))
			forceStop();
	}

	public boolean setInteraction(Interaction skill) {
		forceStop();
		if (!skill.start(player))
			return false;
		interaction = skill;
		return true;
	}

	public void forceStop() {
		if (interaction == null)
			return;
		player.setNextFaceEntity(null);
		interaction.stop(player);
		interaction = null;
	}

	public Interaction getInteraction() {
		return interaction;
	}

	public boolean doingInteraction(Class<?> type) {
		if (interaction == null)
			return false;
		return type.isInstance(interaction);
	}
}
