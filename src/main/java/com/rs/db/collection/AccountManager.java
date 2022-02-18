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

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.Indexes;
import com.rs.lib.db.DBItemManager;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.model.Account;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class AccountManager extends DBItemManager {
	
	public AccountManager() {
		super("accounts");
	}

	@Override
	public void initCollection() {
		getDocs().createIndex(Indexes.compoundIndex(Indexes.text("username"), Indexes.text("displayName"), Indexes.text("email"), Indexes.text("recoveryEmail")));
	}
	
	public void save(Account account) {
		save(account, null);
	}

	public void save(Account account, Runnable done) {
		execute(() -> {
			saveSync(account);
			if (done != null)
				done.run();
		});
	}

	public void saveSync(Account account) {
		getDocs().findOneAndReplace(eq("username", account.getUsername()), Document.parse(JsonFileManager.toJson(account)), new FindOneAndReplaceOptions().upsert(true));
	}

	public void updateUsernameSync(Account account) {
		getDocs().findOneAndReplace(eq("email", account.getEmail()), Document.parse(JsonFileManager.toJson(account)));
	}
	
	public void getDisplayNameBatch(Collection<String> usernames, Consumer<Map<String, String>> cb) {
		execute(() -> cb.accept(getDisplayNameBatchSync(usernames)));
	}
	
	private Map<String, String> getDisplayNameBatchSync(Collection<String> usernames) {
		Map<String, String> displayNameMap = new HashMap<>();
		FindIterable<Document> docs = getDocs().find(in("username", usernames));
		for (Document d : docs) {
			if (d == null)
				continue;
			try {
				displayNameMap.put((String) d.get("displayName"), (String) d.get("prevDisplayName"));
			} catch (Throwable e) {
				
			}
		}
		return displayNameMap;
	}
	
	public void getPrevDisplayNameBatch(Collection<String> displayNames, Consumer<Map<String, String>> cb) {
		execute(() -> cb.accept(getPrevDisplayNameBatchSync(displayNames)));
	}
	
	private Map<String, String> getPrevDisplayNameBatchSync(Collection<String> displayNames) {
		Map<String, String> displayNameMap = new HashMap<>();
		FindIterable<Document> docs = getDocs().find(in("displayName", displayNames));
		for (Document d : docs) {
			if (d == null)
				continue;
			try {
				displayNameMap.put((String) d.get("displayName"), (String) d.get("prevDisplayName"));
			} catch (Throwable e) {
				
			}
		}
		return displayNameMap;
	}
}
