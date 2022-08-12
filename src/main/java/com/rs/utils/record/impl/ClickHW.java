package com.rs.utils.record.impl;

public class ClickHW extends Click {

	private boolean hardware;
	
	public ClickHW(long timeLogged, int time, int x, int y, int button, boolean hardware) {
		super(timeLogged, time, x, y, button);
		this.hardware = hardware;
	}

	public boolean isHardware() {
		return hardware;
	}

	@Override
	public String toString() {
		return super.toString() + " hw: " + hardware;
	}
}
