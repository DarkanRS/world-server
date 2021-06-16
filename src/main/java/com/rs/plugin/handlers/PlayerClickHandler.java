package com.rs.plugin.handlers;

import com.rs.plugin.events.PlayerClickEvent;

public abstract class PlayerClickHandler extends PluginHandler<PlayerClickEvent> {

	private boolean checkDistance = true;
	
	public PlayerClickHandler(boolean checkDistance, String option) {
		super(new Object[] { option });
		this.checkDistance = checkDistance;
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}
	
}
