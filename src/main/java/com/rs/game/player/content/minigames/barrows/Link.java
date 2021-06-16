package com.rs.game.player.content.minigames.barrows;

public class Link {

	public enum RoomStatus {
		CLOSED, OPEN, UNDETERMINED;
	}

	private BarrowsRoom roomA;
	private BarrowsRoom roomB;
	private RoomStatus state;

	public Link(BarrowsRoom a, BarrowsRoom b) {
		this.roomA = a;
		this.roomB = b;
		this.state = RoomStatus.UNDETERMINED;
	}

	public BarrowsRoom getOther(BarrowsRoom current) {
		return (current == roomA ? roomB : roomA);
	}

	public BarrowsRoom getRoomA() {
		return roomA;
	}

	public BarrowsRoom getRoomB() {
		return roomB;
	}

	public RoomStatus getState() {
		return this.state;
	}

	public void setState(RoomStatus state) {
		if (this.state == RoomStatus.UNDETERMINED)
			this.state = state;
	}

	public String toString() {
		return "Room " + roomA.getName() + " to Room " + roomB.getName() + " = " + this.getState();
	}
}
