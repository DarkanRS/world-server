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
package com.rs.game.tasks;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.rs.lib.util.Logger;

public class WorldTasks {

	private static final List<WorldTaskInformation> TASKS = Collections.synchronizedList(new LinkedList<>());

	public static void processTasks() {
		for (WorldTaskInformation task : TASKS.toArray(new WorldTaskInformation[TASKS.size()]))
			try {
				if (task.continueCount > 0) {
					task.continueCount--;
					continue;
				}
				task.task.run();
				if (task.task.needRemove)
					TASKS.remove(task);
				else
					task.continueCount = task.continueMaxCount;
			} catch (Throwable e) {
				Logger.handle(e);
			}
	}

	public static void schedule(WorldTask task, int delayCount, int periodCount) {
		if (task == null || delayCount < 0 || periodCount < 0)
			return;
		TASKS.add(new WorldTaskInformation(task, delayCount, periodCount));
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
		TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), 0, 1));
	}
	
	public static void scheduleTimer(int startDelay, Function<Integer, Boolean> task) {
		if (task == null || startDelay < 0)
			return;
		TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), startDelay, 1));
	}

	public static int getTasksCount() {
		return TASKS.size();
	}

	private WorldTasks() {

	}

	private static final class WorldTaskInformation {

		private WorldTask task;
		private int continueMaxCount;
		private int continueCount;

		public WorldTaskInformation(WorldTask task, int continueCount, int continueMaxCount) {
			this.task = task;
			this.continueCount = continueCount;
			this.continueMaxCount = continueMaxCount;
			if (continueMaxCount == -1)
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
