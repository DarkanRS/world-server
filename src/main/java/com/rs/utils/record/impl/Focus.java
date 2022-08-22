package com.rs.utils.record.impl;

import com.rs.utils.record.RecordedAction;

public class Focus extends RecordedAction {
	
	private boolean focused;

	public Focus(long timeLogged, boolean focused) {
		super(timeLogged, 0);
		this.focused = focused;
	}

	public boolean isFocused() {
		return focused;
	}

	@Override
	public String toString() {
		return super.toString() + " " + focused;
	}
}
