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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.drop.Drop;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class MuddyChest {

	private final static DropSet REWARDS = new DropSet(
			new DropTable(995, 5000),
			new DropTable(75, 100, new Drop(995, 10000), new Drop(1392, 2), new Drop(565, 20), new Drop(5300, 1)),
			new DropTable(20, 100, 1305, 1),
			new DropTable(5, 100, 989, 1));

	public static ObjectClickHandler openChest = new ObjectClickHandler(new Object[] { 170 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().containsItem(991)) {
				e.getPlayer().getInventory().deleteItem(991, 1);
				Item[] loot = DropTable.calculateDrops(e.getPlayer(), REWARDS);
				e.getPlayer().setNextAnimation(new Animation(536));
				e.getPlayer().lock(2);
				e.getPlayer().sendMessage("You unlock the chest with your key.");
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						GameObject openedChest = new GameObject(e.getObject().getId() + 1, e.getObject().getType(), e.getObject().getRotation(), e.getObject().getX(), e.getObject().getY(), e.getObject().getPlane());
						World.spawnObjectTemporary(openedChest, 1);
						e.getPlayer().incrementCount("Muddy chests opened");
						e.getPlayer().sendMessage("You find some treasure in the chest!");
						for (Item item : loot)
							e.getPlayer().getInventory().addItem(item.getId(), item.getAmount(), true);
					}
				}, 0);
			} else
				e.getPlayer().sendMessage("The chest is locked.");
		}
	};
}
