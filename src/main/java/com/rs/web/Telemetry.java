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
package com.rs.web;

import java.util.LinkedList;
import java.util.Queue;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.lib.web.APIUtil;
import com.rs.lib.web.Route;

import io.undertow.server.RoutingHandler;
import io.undertow.util.StatusCodes;

public class Telemetry implements Route {

	private static int MINUTE_COUNTER = 0;
	private static int HOUR_COUNTER = 0;
	private static int DAY_COUNTER = 0;

	private static int SIZE_TICKS = 100;
	private static int SIZE_MINUTES = 60;
	private static int SIZE_HOURS = 24;

	public static Queue<Telemetry> TELEMETRY_TICKS = new LinkedList<>();
	public static Queue<Telemetry> TELEMETRY_MINUTES = new LinkedList<>();
	public static Queue<Telemetry> TELEMETRY_HOURS = new LinkedList<>();
	public static Queue<Telemetry> TELEMETRY_DAYS = new LinkedList<>();

	private int playersOnline;
	private long time;
	private double memoryLoad;
	private long tickMs;

	Telemetry() {
	}

	@Override
	public void build(RoutingHandler route) {
		route.get("/telemetry/lastcommit", ex -> {
			APIUtil.sendResponse(ex, StatusCodes.OK, Settings.COMMIT_HISTORY);
		});

		route.get("/telemetry/ticks", ex -> {
			APIUtil.sendResponse(ex, StatusCodes.OK, Telemetry.TELEMETRY_TICKS);
		});

		route.get("/telemetry/minutes", ex -> {
			APIUtil.sendResponse(ex, StatusCodes.OK, Telemetry.TELEMETRY_MINUTES);
		});

		route.get("/telemetry/hours", ex -> {
			APIUtil.sendResponse(ex, StatusCodes.OK, Telemetry.TELEMETRY_HOURS);
		});

		route.get("/telemetry/days", ex -> {
			APIUtil.sendResponse(ex, StatusCodes.OK, Telemetry.TELEMETRY_DAYS);
		});
	}

	public Telemetry(long time, double memoryLoad, long tickMs, int playersOnline) {
		this.time = time;
		this.memoryLoad = memoryLoad;
		this.tickMs = tickMs;
		this.playersOnline = playersOnline;
	}

	public long getTime() {
		return time;
	}

	public double getMemoryLoad() {
		return memoryLoad;
	}

	public long getTickMs() {
		return tickMs;
	}

	public int getPlayersOnline() {
		return playersOnline;
	}

	public static void queueTelemetryTick(long tickTime) {
		TELEMETRY_TICKS.add(getTelemetryNow(tickTime));
		if (TELEMETRY_TICKS.size() > SIZE_TICKS)
			TELEMETRY_TICKS.poll();
		if (MINUTE_COUNTER++ >= SIZE_TICKS) {
			queueTelemetryMinute();
			MINUTE_COUNTER = 0;
		}
	}

	public static void queueTelemetryMinute() {
		TELEMETRY_MINUTES.add(getAverage(TELEMETRY_TICKS));
		if (TELEMETRY_MINUTES.size() >= SIZE_MINUTES)
			TELEMETRY_MINUTES.poll();
		if (HOUR_COUNTER++ >= SIZE_MINUTES) {
			queueTelemetryHour();
			HOUR_COUNTER = 0;
		}
	}

	public static void queueTelemetryHour() {
		TELEMETRY_HOURS.add(getAverage(TELEMETRY_MINUTES));
		if (TELEMETRY_HOURS.size() >= SIZE_HOURS)
			TELEMETRY_HOURS.poll();
		if (DAY_COUNTER++ >= SIZE_HOURS) {
			queueTelemetryDay();
			DAY_COUNTER = 0;
		}
	}

	public static void queueTelemetryDay() {
		TELEMETRY_DAYS.add(getAverage(TELEMETRY_HOURS));
	}

	public static Telemetry getAverage(Queue<Telemetry> list) {
		double memoryLoadTotal = 0.0;
		long tickTotal = 0;
		int playersTotal = 0;
		int num = 0;
		for (Telemetry t : list) {
			num++;
			memoryLoadTotal += t.getMemoryLoad();
			tickTotal += t.getTickMs();
			playersTotal += t.getPlayersOnline();
		}
		return new Telemetry(System.currentTimeMillis(), memoryLoadTotal/num, tickTotal/num, playersTotal/num);
	}

	private static Telemetry getTelemetryNow(long tickTime) {
		Runtime rt = Runtime.getRuntime();
		long used = rt.totalMemory() - rt.freeMemory();
		long max = rt.maxMemory();
		return new Telemetry(System.currentTimeMillis(), ((double) used / max)*100.0, tickTime, World.getPlayers().size());
	}

}
