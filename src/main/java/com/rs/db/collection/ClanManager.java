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
package com.rs.db.collection;

import com.google.gson.JsonIOException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.model.Clan;
import com.rs.lib.util.Logger;
import org.bson.Document;

import java.io.IOException;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

public class ClanManager extends DBItemManager {

	public ClanManager() {
		super("clans");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.compoundIndex(Indexes.text("name"), Indexes.text("clanLeaderUsername")));
	}

	public void get(String name, Consumer<Clan> func) {
		execute(() -> {
			Clan account = getSync(name);
			if (account == null)
				func.accept(null);
			else
				func.accept(account);
		});
	}

	public void save(Clan clan, Runnable done) {
		execute(() -> {
			saveSync(clan);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(Clan clan) {
		getDocs().findOneAndReplace(eq("name", clan.getName()), Document.parse(JsonFileManager.toJson(clan)), new FindOneAndReplaceOptions().upsert(true));
	}

	public Clan getSync(String name) {
		Document accDoc = getDocs().find(eq("name", name)).first();
		if (accDoc == null)
			return null;
		else {
			try {
				return JsonFileManager.fromJSONString(JsonFileManager.toJson(accDoc), Clan.class);
			} catch (JsonIOException | IOException e) {
				Logger.handle(e);
				return null;
			}
		}
	}

}
