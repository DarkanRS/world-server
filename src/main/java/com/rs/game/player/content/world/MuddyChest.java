package com.rs.game.player.content.world;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
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
				WorldTasksManager.schedule(new WorldTask() {
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
			} else {
				e.getPlayer().sendMessage("The chest is locked.");
			}
		}
	};
}
