package com.rs.game.pathing;

public class WalkStep {

	private Direction dir;
	private int x, y;
	private boolean clip;
	
	public WalkStep(Direction dir, int x, int y, boolean clip) {
		this.dir = dir;
		this.x = x;
		this.y = y;
		this.clip = clip;
	}
	
	public boolean checkClip() {
		return clip;
	}

	public void setCheckClip(boolean clip) {
		this.clip = clip;
	}

	public Direction getDir() {
		return dir;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
