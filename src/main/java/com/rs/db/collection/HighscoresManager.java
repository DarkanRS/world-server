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

import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.rs.db.model.Highscore;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Logger;
import org.bson.Document;

import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;

public class HighscoresManager extends DBItemManager {

	public HighscoresManager() {
		super("highscores");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.compoundIndex(Indexes.text("displayName"), Indexes.text("username")));
		getDocs().createIndex(Indexes.descending("totalLevel", "totalXp"));
	}

	public void save(Player player) {
		save(player, null);
	}

	public void save(Player player, Runnable done) {
		execute(() -> {
			saveSync(player);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(Player player) {
		if (player.hasRights(Rights.OWNER) || player.hasRights(Rights.DEVELOPER) || player.hasRights(Rights.ADMIN) || player.getSkills().getTotalXp() < 50000)
			return;
		try {
			getDocs().findOneAndReplace(eq("username", player.getUsername()), Document.parse(JsonFileManager.toJson(new Highscore(player))), new FindOneAndReplaceOptions().upsert(true));
		} catch (Exception e) {
			Logger.handle(HighscoresManager.class, "saveSync", e);
		}
	}

	public void getPlayerAtPosition(int rank, Consumer<Highscore> top) {
		execute(() -> {
			try {
				top.accept(JsonFileManager.fromJSONString(getDocs().find().sort(Sorts.descending("totalLevel", "totalXp")).skip(rank).limit(1).first().toJson(), Highscore.class));
			} catch(Throwable e) {
				top.accept(null);
			}
		});
	}
}
