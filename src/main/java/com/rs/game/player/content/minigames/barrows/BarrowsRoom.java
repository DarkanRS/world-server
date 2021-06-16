package com.rs.game.player.content.minigames.barrows;

import java.util.ArrayList;

public class BarrowsRoom {

	public enum RoomType {
		CORNER, EDGE, TREASURE;
	}

	protected ArrayList<Link> links;

	private RoomType type;

	private String name;

	public BarrowsRoom(RoomType type, String name) {
		this.name = name;
		this.type = type;
		this.links = new ArrayList<Link>();
	}

	public String getName() {
		return name;
	}

	public RoomType getType() {
		return type;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void addLink(BarrowsRoom roomB) {
		Link roomLink = new Link(this, roomB);
		links.add(roomLink);
		roomB.links.add(roomLink);
	}
}