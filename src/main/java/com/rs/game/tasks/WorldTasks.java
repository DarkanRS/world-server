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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class WorldTasks {

	private static final List<WorldTaskInformation> TASKS = new CopyOnWriteArrayList<>();

	public static void processTasks() {
		for (WorldTaskInformation task : TASKS) {
			if (task.currDelay > 0) {
				task.currDelay--;
				continue;
			}
			try {
				task.task.run();
			} catch (Throwable e) {
				Logger.handle(e);
			}
			if (task.task.needRemove)
				TASKS.remove(task);
			else
				task.currDelay = task.loopDelay;
		}
	}

	public static void schedule(WorldTask task, int startDelay, int loopDelay) {
		if (task == null || startDelay < 0 || loopDelay < 0)
			return;
		TASKS.add(new WorldTaskInformation(task, startDelay, loopDelay));
	}

	public static void schedule(WorldTask task, int delayCount) {
		if (task == null || delayCount < 0)
			return;
		TASKS.add(new WorldTaskInformation(task, delayCount, -1));
	}

	public static void schedule(WorldTask task) {
		if (task == null)
			return;
		TASKS.add(new WorldTaskInformation(task, 0, -1));
	}

	public static void schedule(int startDelay, int loopDelay, Runnable task) {
		if (task == null || startDelay < 0 || loopDelay < 0)
			return;
		TASKS.add(new WorldTaskInformation(new WorldTaskLambda(task), startDelay, loopDelay));
	}

	public static void schedule(int startDelay, Runnable task) {
		if (task == null || startDelay < 0)
			return;
		TASKS.add(new WorldTaskInformation(new WorldTaskLambda(task), startDelay, -1));
	}

	public static void schedule(Runnable task) {
		if (task == null)
			return;
		TASKS.add(new WorldTaskInformation(new WorldTaskLambda(task), 0, -1));
	}

	public static void scheduleTimer(int startDelay, int loopDelay, Function<Integer, Boolean> task) {
		if (task == null || startDelay < 0 || loopDelay < 0)
			return;
		TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), startDelay, loopDelay));
	}

	public static void scheduleTimer(Function<Integer, Boolean> task) {
		if (task == null)
			return;
		TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), 0, 0));
	}

	public static void scheduleTimer(int startDelay, Function<Integer, Boolean> task) {
		if (task == null || startDelay < 0)
			return;
		TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), startDelay, 0));
	}

	private WorldTasks() {

	}

	private static final class WorldTaskInformation {

		private WorldTask task;
		private int loopDelay;
		private int currDelay;

		public WorldTaskInformation(WorldTask task, int startDelay, int loopDelay) {
			this.task = task;
			this.currDelay = startDelay;
			this.loopDelay = loopDelay;
			if (loopDelay == -1)
				task.needRemove = true;
		}
	}

	public static void delay(int ticks, Runnable task) {
		schedule(new WorldTask() {
			@Override
			public void run() {
				task.run();
			}
		}, ticks);
	}

}
