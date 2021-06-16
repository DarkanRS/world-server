package com.rs.game.player.content.commands;

import com.rs.game.player.Player;

public class Command {
	
	private String usage;
	private String description;
	private CommandExecution execution;
	
	public Command(String usage, String description, CommandExecution execution) {
		this.description = description;
		this.execution = execution;
		this.usage = usage;
	}
	
	public void execute(Player player, String[] args) {
		execution.run(player, args);
	}

	public String getDescription() {
		return description;
	}

	public String getUsage() {
		return usage;
	}
}
