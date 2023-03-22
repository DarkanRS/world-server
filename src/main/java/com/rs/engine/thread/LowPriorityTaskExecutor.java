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
package com.rs.engine.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.stream.Stream;

import com.rs.Settings;
import com.rs.lib.thread.CatchExceptionRunnable;
import com.rs.lib.util.Logger;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import jdk.incubator.concurrent.StructuredTaskScope;

public final class LowPriorityTaskExecutor {
	public static volatile boolean SHUTDOWN;

	private static ScheduledExecutorService WORLD_EXECUTOR;
	private static ScheduledExecutorService LOW_PRIORITY_EXECUTOR;
	private static List<Future<?>> PENDING_FUTURES = new ObjectArrayList<>();

	public static void initExecutors() {
		Logger.info(LowPriorityTaskExecutor.class, "startThreads", "Initializing world threads...");
		WORLD_EXECUTOR = Executors.newSingleThreadScheduledExecutor(new WorldThreadFactory());
		LOW_PRIORITY_EXECUTOR = Executors.newScheduledThreadPool(16, Thread.ofVirtual().factory());
	}

	public class LowPriorityTaskScope<T> extends StructuredTaskScope<T> {
		private final Queue<T> results = new ConcurrentLinkedQueue<>();

		LowPriorityTaskScope() {
			super("Low Priority Task Scope", Thread.ofVirtual().factory());
		}

		@Override
		protected void handleComplete(Future<T> future) {
			switch(future.state()) {
				case SUCCESS -> {
					T result = future.resultNow();
					results.add(result);
				}
				case FAILED -> Logger.handle(LowPriorityTaskScope.class, "handleComplete", future.exceptionNow());
			}
		}

		public Stream<T> results() {
			return results.stream();
		}
	}

	public static void execute(Runnable command) {
		synchronized(PENDING_FUTURES) {
			Future<?> future = LOW_PRIORITY_EXECUTOR.submit(new CatchExceptionRunnable(command));
			PENDING_FUTURES.add(future);
			List<Future<?>> finished = new ArrayList<>();
			for (Future<?> f : PENDING_FUTURES)
				if (f.isDone())
					finished.add(f);
			for (Future<?> f : finished)
				PENDING_FUTURES.remove(f);
		}
	}

	public static void schedule(Runnable command, int delay) {
		LOW_PRIORITY_EXECUTOR.schedule(new CatchExceptionRunnable(command), delay*Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}

	public static void schedule(Runnable command, int startDelay, int delay) {
		LOW_PRIORITY_EXECUTOR.scheduleWithFixedDelay(new CatchExceptionRunnable(command), startDelay*Settings.WORLD_CYCLE_MS, delay*Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}

	public static boolean pendingTasks() {
		synchronized(PENDING_FUTURES) {
			List<Future<?>> finished = new ArrayList<>();
			for (Future<?> f : PENDING_FUTURES)
				if (f.isDone())
					finished.add(f);
			for (Future<?> f : finished)
				PENDING_FUTURES.remove(f);

			boolean allDone = true;
			for(Future<?> future : PENDING_FUTURES)
				if (!future.isDone())
					allDone = false;
			return !allDone;
		}
	}

	public static void shutdown() {
		LOW_PRIORITY_EXECUTOR.shutdown();
		SHUTDOWN = true;
	}

	public static ScheduledExecutorService getWorldExecutor() {
		return WORLD_EXECUTOR;
	}
}
