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
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;

/**
 * Handles an interface showing up cutscene action.
 *
 * @author Emperor
 *
 */
public final class InterfaceAction extends CutsceneAction {

	/**
	 * The interface id.
	 */
	private final int interfaceId;

	/**
	 * The delay.
	 */
	private final int delay;

	/**
	 * Constructs a new {@code InterfaceAction} {@code Object}.
	 *
	 * @param interfaceId
	 *            The interface id.
	 * @param actionDelay
	 *            The action delay.
	 */
	public InterfaceAction(int interfaceId, int actionDelay) {
		super(-1, actionDelay);
		this.interfaceId = interfaceId;
		delay = actionDelay;
	}

	@Override
	public void process(final Player player, Object[] cache) {
		player.getInterfaceManager().sendInterface(interfaceId);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getInterfaceManager().removeScreenInterface();
			}
		}, delay);
	}

}