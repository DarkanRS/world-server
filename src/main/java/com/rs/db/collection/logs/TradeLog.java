package com.rs.db.collection.logs;

import com.rs.game.player.Player;
import com.rs.lib.game.Item;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class TradeLog {
	private String uuid;
	private String player1;
	private Item[] p1Items;
	private String player2;
	private Item[] p2Items;

	public TradeLog(Player p1, Item[] p1Items, Player p2, Item[] p2Items) {
		this.player1 = p1.getUsername();
		this.p1Items = p1Items;
		this.player2 = p2.getUsername();
		this.p2Items = p2Items;
		this.uuid = UUID.randomUUID().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TradeLog tradeLog = (TradeLog) o;
		return Objects.equals(uuid, tradeLog.uuid) && Objects.equals(player1, tradeLog.player1) && Arrays.equals(p1Items, tradeLog.p1Items) && Objects.equals(player2, tradeLog.player2) && Arrays.equals(p2Items, tradeLog.p2Items);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(uuid, player1, player2);
		result = 31 * result + Arrays.hashCode(p1Items);
		result = 31 * result + Arrays.hashCode(p2Items);
		return result;
	}
}
