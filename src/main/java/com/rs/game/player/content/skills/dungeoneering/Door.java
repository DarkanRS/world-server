package com.rs.game.player.content.skills.dungeoneering;

public class Door {

	private int type, id, level;

	public Door(int type, int id, int level) {
		this.type = type;
		this.id = id;
		this.level = level;
	}

	public Door(int type, int id) {
		this(type, id, 0);
	}

	public Door(int type) {
		this(type, 0);
	}

	public int getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}
}
