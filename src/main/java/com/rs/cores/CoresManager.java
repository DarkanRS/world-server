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
package com.rs.cores;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.lib.thread.CatchExceptionRunnable;
import com.rs.lib.util.Logger;

public final class CoresManager {

	public static volatile boolean SHUTDOWN;

	private static ScheduledExecutorService worldExecutor;
	private static ScheduledExecutorService worldExemptExecutor;
	private static List<Future<?>> PENDING_FUTURES = new ArrayList<>();

	public static void startThreads() {
		Logger.info(CoresManager.class, "startThreads", "Initializing world threads...");
		worldExemptExecutor = Executors.newSingleThreadScheduledExecutor(new SlowThreadFactory());
		worldExecutor = Executors.newSingleThreadScheduledExecutor(new WorldThreadFactory());
	}

	public static void execute(Runnable command) {
		synchronized(PENDING_FUTURES) {
			Future<?> future = worldExemptExecutor.submit(new CatchExceptionRunnable(command));
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
		worldExemptExecutor.schedule(new CatchExceptionRunnable(command), delay*Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
	}

	public static void schedule(Runnable command, int startDelay, int delay) {
		worldExemptExecutor.scheduleWithFixedDelay(new CatchExceptionRunnable(command), startDelay*Settings.WORLD_CYCLE_MS, delay*Settings.WORLD_CYCLE_MS, TimeUnit.MILLISECONDS);
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
		worldExemptExecutor.shutdown();
		SHUTDOWN = true;
	}

	public static ScheduledExecutorService getWorldExecutor() {
		return worldExecutor;
	}
}
