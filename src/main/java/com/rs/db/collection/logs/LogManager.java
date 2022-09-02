// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.db.collection.logs;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.rs.game.content.death.GraveStone;
import com.rs.game.ge.Offer;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Trade;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.utils.ReportsManager;

public class LogManager extends DBItemManager {

	public LogManager() {
		super("logs");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.text("type"));
		getDocs().createIndex(Indexes.descending("hash"));
		getDocs().createIndex(Indexes.ascending("date"), new IndexOptions().expireAfter(180L, TimeUnit.DAYS));
	}

	public void save(LogEntry entry) {
		save(entry, null);
	}

	public void save(LogEntry entry, Runnable done) {
		execute(() -> {
			saveSync(entry);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(LogEntry entry) {
		try {
			getDocs().findOneAndReplace(eq("hash", entry.getHash()), Document.parse(JsonFileManager.toJson(entry)), new FindOneAndReplaceOptions().upsert(true));
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

	public void logGE(Offer offer, Offer other, int num, int price) {
		GELog log = new GELog(offer, other, num, price);
		save(new LogEntry(LogEntry.LogType.GE, log.hashCode(), log));
	}

	public void logTrade(Player player1, Trade p1Trade, Player p2, Trade p2Trade) {
		List<Item> p1Items = new ArrayList<>();
		List<Item> p2Items = new ArrayList<>();
		for (Item item : p1Trade.getItems().array())
			if (item != null)
				p1Items.add(item);
		for (Item item : p2Trade.getItems().array())
			if (item != null)
				p2Items.add(item);
		if (p1Items.size() > 0 || p2Items.size() > 0) {
			TradeLog log = new TradeLog(player1, p1Items, p2, p2Items);
			save(new LogEntry(LogEntry.LogType.TRADE, log.hashCode(), log));
		}
	}

	public void logPickup(Player player, GroundItem item) {
		PickupLog log = new PickupLog(player, item);
		save(new LogEntry(LogEntry.LogType.PICKUP, log.hashCode(), log));
	}
	public void logGrave(String player, GraveStone grave) {
		GraveLog log = new GraveLog(player, grave);
		save(new LogEntry(LogEntry.LogType.GRAVE, log.hashCode(), log));
	}

	public void logReport(Player reporter, Player reported, ReportsManager.Rule rule) {
		if (reporter == null || reported == null)
			return;
		ReportLog log = new ReportLog(reporter, reported, rule);
		save(new LogEntry(LogEntry.LogType.REPORT, log.hashCode(), log));
	}

	public void logCommand(String username, String commandStr) {
		CommandLog log = new CommandLog(username, commandStr);
		save(new LogEntry(LogEntry.LogType.COMMAND, log.hashCode(), log));
	}
}
