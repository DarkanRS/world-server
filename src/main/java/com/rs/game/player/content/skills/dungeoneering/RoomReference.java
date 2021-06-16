package com.rs.game.player.content.skills.dungeoneering;

public class RoomReference {

	private int x, y;

	public RoomReference(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getRoomX() {
		return x;
	}

	public int getRoomY() {
		return y;
	}
	
	public int getBaseX() {
		return x * 16;
	}
	
	public int getBaseY() {
		return y * 16;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof RoomReference) {
			RoomReference rRef = (RoomReference) object;
			return x == rRef.x && y == rRef.y;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[RoomReference][" + x + "][" + y + "]";
	}
}
