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

import java.time.DayOfWeek;
import java.util.function.Function;

public class WorldTasks {

	private static final TaskManager TASKS = new TaskManager();

	public static void processTasks() {
		TASKS.processTasks();
	}

	/**
	 * Please use scheduleTimer for this sparingly and be absurdly careful to end the loops
	 * when they are supposed to end.
	 */
	@Deprecated
	public static TaskInformation scheduleLooping(Task task, int startDelay, int loopDelay) {
		return TASKS.scheduleLooping(task, startDelay, loopDelay);
	}

	public static TaskInformation schedule(Task task, int delayCount) {
		return TASKS.schedule(task, delayCount);
	}

	public static TaskInformation schedule(Task task) {
		return TASKS.schedule(task);
	}

	/**
	 * Please use scheduleTimer for this sparingly and be absurdly careful to end the loops
	 * when they are supposed to end.
	 */
	@Deprecated
	public static TaskInformation scheduleLooping(int startDelay, int loopDelay, Runnable task) {
		return TASKS.scheduleLooping(startDelay, loopDelay, task);
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

	public static TaskInformation schedule(String mapping, int startDelay, Runnable task) {
		return TASKS.schedule(mapping, startDelay, task);
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

	public static boolean hasTask(String mapping) {
		return TASKS.hasTask(mapping);
	}

	public static void remove(String mapping) {
		TASKS.remove(mapping);
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

	public static int getSize() {
		return TASKS.getSize();
	}

}
