package com.rs.game.object;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;

public class OwnedObject extends GameObject {

	private static Map<Integer, OwnedObject> OBJECTS = new ConcurrentHashMap<>();
	private static Map<String, Map<Integer, OwnedObject>> OWNER_MAP = new ConcurrentHashMap<>();

	private String owner;
	private boolean destroyed;
	
	public OwnedObject(Player player, GameObject object) {
		this(player, object.getId(), object.getType(), object.getRotation(), new WorldTile(object));
	}

	public OwnedObject(Player player, int id, ObjectType type, int rotation, WorldTile tile) {
		super(id, type, rotation, tile);
		this.owner = player.getUsername();
	}

	public void tick(Player owner) {

	}

	public void onDestroy() {

	}
	
	public void onCreate() {
		
	}

	public Player getOwner() {
		return World.getPlayer(owner);
	}

	public boolean ownedBy(Player player) {
		return player.getUsername().equals(owner);
	}

	public static void process() {
		Iterator<Integer> it = OBJECTS.keySet().iterator();
		while (it.hasNext()) {
			Integer key = it.next();
			OwnedObject o = OBJECTS.get(key);
			if (o == null || o.destroyed)
				continue;
			o.tick(World.getPlayer(o.owner));
		}
	}
	
	public boolean overlapsExisting() {
		return World.getObject(this, getType()) != null;
	}

	public final boolean createNoReplace() {
		if (World.getObject(this, getType()) != null)
			return false;
		World.spawnObject(this);
		OBJECTS.put(getTileHash(), this);
		onCreate();
		Map<Integer, OwnedObject> ownedBy = OWNER_MAP.get(owner);
		if (ownedBy == null)
			ownedBy = new ConcurrentHashMap<>();
		ownedBy.put(getTileHash(), this);
		OWNER_MAP.put(owner, ownedBy);
		return true;
	}
	
	public final void createReplace() {
		World.spawnObject(this);
		OBJECTS.put(getTileHash(), this);
		onCreate();
		Map<Integer, OwnedObject> ownedBy = OWNER_MAP.get(owner);
		if (ownedBy == null)
			ownedBy = new ConcurrentHashMap<>();
		ownedBy.put(getTileHash(), this);
		OWNER_MAP.put(owner, ownedBy);
	}

	public final void destroy() {
		World.removeObject(this);
		OBJECTS.remove(getTileHash());
		onDestroy();
		Map<Integer, OwnedObject> ownedBy = OWNER_MAP.get(owner);
		if (ownedBy != null) {
			ownedBy.remove(getTileHash());
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
		for (OwnedObject obj : objs) {
			if (type.isInstance(obj))
				count++;
		}
		return count;
	}

}
