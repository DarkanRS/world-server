package com.rs.utils.record.impl;

import com.rs.utils.record.RecordedAction;

public class Click extends RecordedAction {
	
	private final int x;
    private final int y;
    private final int button;

	public Click(long timeLogged, int time, int x, int y, int button) {
		super(timeLogged, time);
		this.x = x;
		this.y = y;
		this.button = button;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getButton() {
		return button;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ["+x+","+y+"] b:" + button;
	}

}
