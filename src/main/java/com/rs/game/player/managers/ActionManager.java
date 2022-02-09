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

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.randomevents.RandomEvents;
import com.rs.lib.util.Utils;

public final class ActionManager {

	private Player player;
	private Action action;
	private int actionDelay;

	public ActionManager(Player player) {
		this.player = player;
	}

	public void process() {
		if (action != null)
			if (player.isDead())
				forceStop();
			else if (!action.process(player))
				forceStop();
		if (actionDelay > 0) {
			actionDelay--;
			return;
		}
		if (action == null || player == null)
			return;
		if (!action.isNoRandoms() && Utils.random(2000) == 0)
			RandomEvents.attemptSpawnRandom(player);
		int delay = action.processWithDelay(player);
		if (delay == -1) {
			forceStop();
			return;
		}
		actionDelay += delay;
	}

	public boolean setAction(Action skill) {
		forceStop();
		if (!skill.start(player))
			return false;
		action = skill;
		return true;
	}

	public void forceStop() {
		if (action == null)
			return;
		action.stop(player);
		action = null;
	}

	public int getActionDelay() {
		return actionDelay;
	}

	public void addActionDelay(int skillDelay) {
		actionDelay += skillDelay;
	}

	public void setActionDelay(int skillDelay) {
		actionDelay = skillDelay;
	}

	public boolean hasSkillWorking() {
		return action != null;
	}

	public Action getAction() {
		return action;
	}

	public boolean doingAction(Class<?> type) {
		if (action == null)
			return false;
		return type.isInstance(action);
	}
}
