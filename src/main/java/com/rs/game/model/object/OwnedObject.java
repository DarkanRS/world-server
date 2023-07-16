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
package com.rs.game.model.object;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class OwnedObject extends GameObject {

	private static Map<Integer, OwnedObject> OBJECTS = new ConcurrentHashMap<>();
	private static Map<String, Map<Integer, OwnedObject>> OWNER_MAP = new ConcurrentHashMap<>();

	private String owner;
	private boolean destroyed;

	public OwnedObject(Player player, GameObject object) {
		this(player, object.getId(), object.getType(), object.getRotation(), object.getTile());
	}

	public OwnedObject(Player player, int id, ObjectType type, int rotation, Tile tile) {
		super(id, type, rotation, tile);
		owner = player.getUsername();
	}

	public void tick(Player owner) {

	}

	public void onDestroy() {

	}

	public void onCreate() {

	}

	public Player getOwner() {
		return World.getPlayerByUsername(owner);
	}

	public boolean ownedBy(Player player) {
		return player.getUsername().equals(owner);
	}

	public static void processAll() {
		try {
			Iterator<Integer> it = OBJECTS.keySet().iterator();
			while (it.hasNext()) {
				Integer key = it.next();
				OwnedObject o = OBJECTS.get(key);
				if (o == null || o.destroyed)
					continue;
				o.tick(World.getPlayerByUsername(o.owner));
			}
		} catch(Throwable e) {
			Logger.handle(OwnedObject.class, "process", e);
		}
	}

	public boolean overlapsExisting() {
		return World.getObject(this.tile, getType()) != null;
	}

	public final boolean createNoReplace() {
		if (World.getObject(this.tile, getType()) != null)
			return false;
		World.spawnObject(this);
		OBJECTS.put(tile.getTileHash(), this);
		onCreate();
		Map<Integer, OwnedObject> ownedBy = OWNER_MAP.get(owner);
		if (ownedBy == null)
			ownedBy = new ConcurrentHashMap<>();
		ownedBy.put(tile.getTileHash(), this);
		OWNER_MAP.put(owner, ownedBy);
		return true;
	}

	public final void createReplace() {
		World.spawnObject(this);
		OBJECTS.put(tile.getTileHash(), this);
		onCreate();
		Map<Integer, OwnedObject> ownedBy = OWNER_MAP.get(owner);
		if (ownedBy == null)
			ownedBy = new ConcurrentHashMap<>();
		ownedBy.put(tile.getTileHash(), this);
		OWNER_MAP.put(owner, ownedBy);
	}

	public final void destroy() {
		World.removeObject(this);
		OBJECTS.remove(tile.getTileHash());
		onDestroy();
		Map<Integer, OwnedObject> ownedBy = OWNER_MAP.get(owner);
		if (ownedBy != null) {
			ownedBy.remove(tile.getTileHash());
			if (ownedBy.size() == 0)
				OWNER_MAP.remove(owner);
			destroyed = true;
		}
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public static Collection<OwnedObject> getOwnedBy(Player owner) {
		if (OWNER_MAP.get(owner.getUsername()) == null)
			return new CopyOnWriteArrayList<>();
		return OWNER_MAP.get(owner.getUsername()).values();
	}

	public static int getNumOwned(Player owner, Class<?> type) {
		int count = 0;
		Collection<OwnedObject> objs = getOwnedBy(owner);
		if (objs == null)
			return 0;
		for (OwnedObject obj : objs)
			if (type.isInstance(obj))
				count++;
		return count;
	}

}
