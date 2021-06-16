package com.rs.game.player.content.commands;

import com.rs.game.player.Player;

public interface CommandExecution {
	void run(Player p, String[] args);
}
