package com.rs.db.model;

import com.rs.game.player.Player;

public class Highscore {

	private String username;
	private boolean ironman;
	private int totalLevel;
	private long totalXp;
	private int[] xp;
	
	public Highscore(Player player) {
		this.username = player.getUsername();
		this.ironman = player.isIronMan();
		this.totalLevel = player.getSkills().getTotalLevel();
		this.totalXp = player.getSkills().getTotalXp();
		this.xp = player.getSkills().getXpInt();
	}

	public boolean isIronman() {
		return ironman;
	}

	public int getTotalLevel() {
		return totalLevel;
	}

	public long getTotalXp() {
		return totalXp;
	}

	public int[] getXp() {
		return xp;
	}

	public String getUsername() {
		return username;
	}

}
