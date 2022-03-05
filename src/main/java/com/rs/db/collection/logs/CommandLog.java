package com.rs.db.collection.logs;

import java.util.Objects;
import java.util.UUID;

public class CommandLog {
	private String uuid;
	private String player;
	private String command;

	public CommandLog(String player, String command) {
		this.player = player;
		this.command = command;
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommandLog that = (CommandLog) o;
		return Objects.equals(uuid, that.uuid) && Objects.equals(player, that.player) && Objects.equals(command, that.command);
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid, player, command);
	}
}
