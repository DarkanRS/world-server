package com.rs.utils;

import java.util.ArrayList;

import com.rs.Settings;

/**
 * Anti Flood
 * 
 * @Author Apache Ah64
 */
public final class AntiFlood {

	private static ArrayList<String> CONNECTIONS = new ArrayList<String>(Settings.PLAYERS_LIMIT * 3);

	public static void add(String ip) {
		CONNECTIONS.add(ip);
	}

	public static void remove(String ip) {
		CONNECTIONS.remove(ip);
	}

	public static int getSessionsIP(String ip) {
		int amount = 1;
		for (int i = 0; i < CONNECTIONS.size(); i++) {
			if (CONNECTIONS.get(i).equalsIgnoreCase(ip))
				amount++;
		}
		return amount;
	}
}