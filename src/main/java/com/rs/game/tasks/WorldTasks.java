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

import com.rs.lib.util.Logger;
import com.rs.utils.Ticks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class WorldTasks {

	private static final TaskManager TASKS = new TaskManager();

	public static void processTasks() {
		TASKS.processTasks();
	}

	public static TaskInformation schedule(Task task, int startDelay, int loopDelay) {
		return TASKS.schedule(task, startDelay, loopDelay);
	}

	public static TaskInformation schedule(Task task, int delayCount) {
		return TASKS.schedule(task, delayCount);
	}

	public static TaskInformation schedule(Task task) {
		return TASKS.schedule(task);
	}

	public static TaskInformation schedule(int startDelay, int loopDelay, Runnable task) {
		return TASKS.schedule(startDelay, loopDelay, task);
	}

	public static TaskInformation scheduleHalfHourly(Runnable task) {
		return TASKS.scheduleHalfHourly(task);
	}

	public static TaskInformation scheduleNthHourly(int hour, Runnable task) {
		return TASKS.scheduleNthHourly(hour, task);
	}

	public static TaskInformation scheduleHourly(Runnable task) {
		return scheduleNthHourly(1, task);
	}

	public static TaskInformation schedule(int startDelay, Runnable task) {
		return TASKS.schedule(startDelay, task);
	}

	public static TaskInformation schedule(Runnable task) {
		return TASKS.schedule(task);
	}

	public static void scheduleTimer(int startDelay, int loopDelay, Function<Integer, Boolean> task) {
		TASKS.scheduleTimer(startDelay, loopDelay, task);
	}

	public static void scheduleTimer(Function<Integer, Boolean> task) {
		TASKS.scheduleTimer(task);
	}

	public static void scheduleTimer(int startDelay, Function<Integer, Boolean> task) {
		TASKS.scheduleTimer(startDelay, task);
	}

	public static void remove(TaskInformation task) {
		TASKS.remove(task);
	}

	private WorldTasks() {

	}

	public static void delay(int ticks, Runnable task) {
		schedule(new Task() {
			@Override
			public void run() {
				task.run();
			}
		}, ticks);
	}

}
