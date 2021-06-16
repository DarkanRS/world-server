package com.rs.game.npc.dungeoneering;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public final class IcyBones extends DungeonBoss {

	public IcyBones(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10040, 10057), manager.getBossLevel()), tile, manager, reference);
		spikes = new ArrayList<GameObject>();
		setHitpoints(getMaxHitpoints());
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	private List<GameObject> spikes;

	public void removeSpikes() {
		if (spikes.isEmpty())
			return;
		for (GameObject object : spikes)
			World.removeObject(object);
		spikes.clear();
	}

	public boolean sendSpikes() {
		if (!spikes.isEmpty())
			return false;
		int size = getSize();
		for (int x = -1; x < 7; x++) {
			for (int y = -1; y < 7; y++) {
				if (((x != -1 && x != 6) && (y != -1 && y != 6)) || Utils.random(2) != 0)
					continue;
				WorldTile tile = transform(x - size, y - size, 0);
				RoomReference current = getManager().getCurrentRoomReference(tile);
				if (current.getRoomX() != getReference().getRoomX() || current.getRoomY() != getReference().getRoomY() || !World.floorFree(tile.getPlane(), tile.getX(), tile.getY()))
					continue;
				GameObject object = new GameObject(52285 + Utils.random(3), ObjectType.SCENERY_INTERACT, Utils.random(4), tile.getX(), tile.getY(), tile.getPlane());
				spikes.add(object);
				World.spawnObject(object);
				for (Player player : getManager().getParty().getTeam()) {
					if (player.getX() == object.getX() && player.getY() == object.getY())
						player.applyHit(new Hit(this, 1 + Utils.random(getMaxHit()), HitLook.TRUE_DAMAGE));
				}
			}
		}
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				removeSpikes();
			}

		}, 10);
		return true;
	}

}
