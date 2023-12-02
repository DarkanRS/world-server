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
package com.rs.game.tasks;

import java.util.function.Function;

public class TaskTimerLambda extends Task {

	int tick = 0;
	private Function<Integer, Boolean> task;

	public TaskTimerLambda(Function<Integer, Boolean> task) {
		this.task = task;
	}

	@Override
	public void run() {
		if (!task.apply(tick))
			stop();
		tick++;
	}

}
