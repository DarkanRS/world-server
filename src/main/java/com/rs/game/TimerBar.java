package com.rs.game;

public class TimerBar extends HitBar {
	
	private int timer;
	
	public TimerBar(int timer) {
		this.timer = timer;
	}

	@Override
	public int getType() {
		return 2;
	}
	
	@Override
	public int getTimer() {
		return timer;
	}
	
	@Override
	public int getToPercentage() {
		return 255;
	}

	@Override
	public int getPercentage() {
		return 0;
	}
}
