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

import com.rs.Settings;
import com.rs.game.map.instance.InstanceBuilder;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.thread.CatchExceptionRunnable;
import com.rs.lib.util.Logger;
import com.rs.utils.Ticks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AsyncTaskExecutor {
	public static volatile boolean SHUTDOWN;

	private static ScheduledExecutorService WORLD_THREAD_EXECUTOR;
	private static ScheduledExecutorService ASYNC_VTHREAD_EXECUTOR;
	private static final List<Future<?>> PENDING_FUTURES = new ObjectArrayList<>();

	public static void initExecutors() {
		Logger.info(AsyncTaskExecutor.class, "startThreads", "Initializing world threads...");
		WORLD_THREAD_EXECUTOR = Executors.newSingleThreadScheduledExecutor(new WorldThreadFactory());
		ASYNC_VTHREAD_EXECUTOR = Executors.newScheduledThreadPool(16, Thread.ofVirtual().factory());
	}

	public static void execute(Runnable command) {
		synchronized(PENDING_FUTURES) {
			Future<?> future = ASYNC_VTHREAD_EXECUTOR.submit(new CatchExceptionRunnable(command));
			PENDING_FUTURES.add(future);
			List<Future<?>> finished = new ArrayList<>();
			for (Future<?> f : PENDING_FUTURES)
				if (f.isDone())
					finished.add(f);
			for (Future<?> f : finished)
				PENDING_FUTURES.remove(f);
		}
	}

	public static void executeWorldThreadSafe(String methodName, CompletableFuture<Boolean> future, int timeoutSeconds, ThrowableRunnable operation) {
		AtomicBoolean completed = new AtomicBoolean(false);
		final int maxTicks = Ticks.fromSeconds(timeoutSeconds);
		AsyncTaskExecutor.execute(() -> {
			try {
				operation.run();
			} catch (Throwable e) {
				Logger.handle(AsyncTaskExecutor.class, "executeWorldThreadSafe:" + methodName, e);
			} finally {
				completed.set(true);
			}
		});
		WorldTasks.scheduleTimer(0, 0, tick -> {
			if (completed.get()) {
				future.complete(true);
				return false;
			}
			if (tick >= maxTicks) {
				future.completeExceptionally(new TimeoutException("Operation timed out."));
				return false;
			}
			return true;
		});
	}

	@FunctionalInterface
	public interface ThrowableRunnable {
		void run() throws Throwable;
	}

	public static void schedule(Runnable command, int delay) {
		ASYNC_VTHREAD_EXECUTOR.schedule(new CatchExceptionRunnable(command), delay*Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}

	public static void schedule(Runnable command, int startDelay, int delay) {
		ASYNC_VTHREAD_EXECUTOR.scheduleWithFixedDelay(new CatchExceptionRunnable(command), startDelay*Settings.WORLD_CYCLE_MS, delay*Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
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
		ASYNC_VTHREAD_EXECUTOR.shutdown();
		SHUTDOWN = true;
	}

	public static ScheduledExecutorService getWorldThreadExecutor() {
		return WORLD_THREAD_EXECUTOR;
	}
}
