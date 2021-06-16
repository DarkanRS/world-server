package com.rs.game.tasks;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.rs.lib.util.Logger;

public class WorldTasksManager {

	private static final List<WorldTaskInformation> TASKS = Collections.synchronizedList(new LinkedList<>());

	public static void processTasks() {
		for (WorldTaskInformation task : TASKS.toArray(new WorldTaskInformation[TASKS.size()])) {
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

	public static int getTasksCount() {
		return TASKS.size();
	}

	private WorldTasksManager() {

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
