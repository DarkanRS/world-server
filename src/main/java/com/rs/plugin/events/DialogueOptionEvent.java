package com.rs.plugin.events;

import com.rs.game.player.Player;

public abstract class DialogueOptionEvent {

	protected int option;
	private String[] options;
	private String optionStr = "";

	public abstract void run(Player player);

	public int getOption() {
		return option;
	}
	
	public String getOptionString() {
		return optionStr;
	}
	
	public void setOptions(String[] options) {
		this.options = options;
	}

	public void setOption(int integer) {
		this.option = integer;
		if ((integer-1) >= 0)
			this.optionStr = options[integer-1];
	}
}
