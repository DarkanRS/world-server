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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import com.rs.lib.util.Logger;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class WorldTasks {

	private static final List<WorldTaskInformation> TASKS = new ObjectArrayList<>();

	public static void processTasks() {
		synchronized(TASKS) {
			List<WorldTaskInformation> toRemove = new ArrayList<>();
			Iterator<WorldTaskInformation> iter = TASKS.iterator();
			while(iter.hasNext()) {
				WorldTaskInformation task = iter.next();
				if (task == null)
					continue;
				try {
					if (task.currDelay > 0) {
						task.currDelay--;
						continue;
					}
					try {
						task.getTask().run();
					} catch (Throwable e) {
						Logger.handle(WorldTasks.class, "processTasksRun:"+(task.getClass().getDeclaringClass() != null ? task.getClass().getDeclaringClass().getSimpleName() : "UnknownSource"), e);
					}
					if (task.getTask().needRemove)
						toRemove.add(task);
					else
						task.currDelay = task.getLoopDelay();
				} catch (Throwable e) {
					Logger.handle(WorldTasks.class, "processTasks:"+(task.getClass().getDeclaringClass() != null ? task.getClass().getDeclaringClass().getSimpleName() : "UnknownSource"), e);
				}
			}
			for (WorldTaskInformation task : toRemove)
				if (task != null)
					TASKS.remove(task);
		}
	}

	public static WorldTaskInformation schedule(WorldTask task, int startDelay, int loopDelay) {
		synchronized(TASKS) {
			if (task == null || startDelay < 0 || loopDelay < 0)
				return null;
			WorldTaskInformation taskInfo = new WorldTaskInformation(task, startDelay, loopDelay);
			TASKS.add(taskInfo);
			return taskInfo;
		}
	}

	public static WorldTaskInformation schedule(WorldTask task, int delayCount) {
		synchronized(TASKS) {
			if (task == null || delayCount < 0)
				return null;
			WorldTaskInformation taskInfo = new WorldTaskInformation(task, delayCount, -1);
			TASKS.add(taskInfo);
			return taskInfo;
		}
	}

	public static WorldTaskInformation schedule(WorldTask task) {
		synchronized(TASKS) {
			if (task == null)
				return null;
			WorldTaskInformation taskInfo = new WorldTaskInformation(task, 0, -1);
			TASKS.add(taskInfo);
			return taskInfo;
		}
	}

	public static WorldTaskInformation schedule(int startDelay, int loopDelay, Runnable task) {
		synchronized(TASKS) {
			if (task == null || startDelay < 0 || loopDelay < 0)
				return null;
			WorldTaskInformation taskInfo = new WorldTaskInformation(new WorldTaskLambda(task), startDelay, loopDelay);
			TASKS.add(taskInfo);
			return taskInfo;
		}
	}

	public static WorldTaskInformation schedule(int startDelay, Runnable task) {
		synchronized(TASKS) {
			if (task == null || startDelay < 0)
				return null;
			WorldTaskInformation taskInfo = new WorldTaskInformation(new WorldTaskLambda(task), startDelay, -1);
			TASKS.add(taskInfo);
			return taskInfo;
		}
	}

	public static WorldTaskInformation schedule(Runnable task) {
		synchronized(TASKS) {
			if (task == null)
				return null;
			WorldTaskInformation taskInfo = new WorldTaskInformation(new WorldTaskLambda(task), 0, -1);
			TASKS.add(taskInfo);
			return taskInfo;
		}
	}

	public static void scheduleTimer(int startDelay, int loopDelay, Function<Integer, Boolean> task) {
		synchronized(TASKS) {
			if (task == null || startDelay < 0 || loopDelay < 0)
				return;
			TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), startDelay, loopDelay));
		}
	}

	public static void scheduleTimer(Function<Integer, Boolean> task) {
		synchronized(TASKS) {
			if (task == null)
				return;
			TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), 0, 0));
		}
	}

	public static void scheduleTimer(int startDelay, Function<Integer, Boolean> task) {
		synchronized(TASKS) {
			if (task == null || startDelay < 0)
				return;
			TASKS.add(new WorldTaskInformation(new WorldTaskTimerLambda(task), startDelay, 0));
		}
	}
	
	public static void remove(WorldTaskInformation task) {
		synchronized(TASKS) {
			if (task == null)
				return;
			TASKS.remove(task);
		}
	}

	private WorldTasks() {

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
