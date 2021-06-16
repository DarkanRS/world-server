package com.rs.utils;

public class Click {
	
	private int x;
	private int y;
	private int time;
	private long timeMillis;
	
	public Click(int x, int y, int time, long timeMillis) {
		this.x = x;
		this.y = y;
		this.time = time;
		this.timeMillis = timeMillis;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTime() {
		return time;
	}
	
	public boolean sameSpot(Click click2) {
		return click2.x == this.x && click2.y == this.y;
	}
	
	public boolean sameTime(Click click2) {
		return click2.time == this.time;
	}
	
	public long getTimeMillis() {
		return timeMillis;
	}
	
	@Override
	public String toString() {
		return "["+x+", "+y+"]: " + time+"ms";
	}
	
}
