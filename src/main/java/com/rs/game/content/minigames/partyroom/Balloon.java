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
package com.rs.game.content.minigames.partyroom;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.World.DropMethod;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;

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
			WorldTasks.schedule(new Task() {
				@Override
				public void run() {
					if (item != null)
						World.addGroundItem(item, Tile.of(getX(), getY(), getPlane()), player, true, 60, DropMethod.NORMAL);
					World.removeObject(poppedBalloon);
					player.unlock();
				}
			}, 1);
		}
	}

}
