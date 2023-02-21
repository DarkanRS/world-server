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
package com.rs.utils;

import com.rs.game.World;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AccountLimiter {

	private static Map<String, Integer> CONNECTIONS = new ConcurrentHashMap<>();

	public static void add(String ip) {
		Integer amount = CONNECTIONS.get(ip);
		if (amount != null)
			CONNECTIONS.put(ip, amount+1);
		else
			CONNECTIONS.put(ip, 1);
	}

	public static void remove(String ip) {
		Integer amount = CONNECTIONS.get(ip);
		if (amount == null)
			amount = 0;
		if (amount <= 1)
			CONNECTIONS.remove(ip);
		else
			CONNECTIONS.put(ip, amount-1);
	}

	public static int getSessionsIP(String ip) {
		Integer connections = CONNECTIONS.get(ip);
		if (connections == null)
			return 1;
		return connections;
	}
}