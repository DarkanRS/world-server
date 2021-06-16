package com.rs.game.player.content.minigames.partyroom;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.World.DropMethod;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;

public class Balloon extends GameObject {

	private Item item;
	private boolean popped = false;

	public Balloon(int id, int rotation, int x, int y, int plane) {
		super(id, ObjectType.SCENERY_INTERACT, rotation, x, y, plane);
	}

	public Item getItem() {
		return item;
	}

	public Balloon setItem(Item item) {
		this.item = item;
		return this;
	}

	public void handlePop(final Player player) {
		if (!popped) {
			player.setNextAnimation(new Animation(794));
			popped = true;
			player.lock();
			World.removeObject(this);
			final GameObject poppedBalloon = new GameObject(getId() + 8, ObjectType.SCENERY_INTERACT, this.getRotation(), getX(), getY(), getPlane());
			World.spawnObject(poppedBalloon);
			player.incrementCount("Party balloons popped");
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (item != null)
						World.addGroundItem(item, new WorldTile(getX(), getY(), getPlane()), player, true, 60, DropMethod.NORMAL);
					World.removeObject(poppedBalloon);
					player.unlock();
				}
			}, 1);
		}
	}

}
