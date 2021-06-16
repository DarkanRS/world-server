package com.rs.utils;

public class Millis {
	
	public static long fromHours(int hours) {
		return fromMinutes(hours * 60);
	}
	
	public static long fromMinutes(int minutes) {
		return minutes * 60000L;
	}
	
	public static long fromSeconds(int seconds) {
		return seconds * 1000L;
	}
}
