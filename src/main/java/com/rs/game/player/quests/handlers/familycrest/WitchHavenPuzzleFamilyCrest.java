package com.rs.game.player.quests.handlers.familycrest;

import static com.rs.game.player.content.world.doors.Doors.handleDoor;

import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

/**
 * Puzzle remake from this picture: https://gyazo.com/305b717ded771ca352b7cb50d51a91b8
 */
@PluginEventHandler
public class WitchHavenPuzzleFamilyCrest {
	private static boolean northNorthLeverUp = false;
	private static boolean northLeverUp = false;
	private static boolean southLeverUp = false;
	private static int TICKS_UP = 400;

	public static ObjectClickHandler handleNorthDoor = new ObjectClickHandler(new Object[] { 2431 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(southLeverUp && !northLeverUp) {
				handleDoor(p, obj);
				return;
			}
			p.sendMessage("Seems locked...");
		}
	};

	public static ObjectClickHandler handleEastDoor = new ObjectClickHandler(new Object[] { 2430 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(northLeverUp && northNorthLeverUp && !southLeverUp) {
				handleDoor(p, obj);
				return;
			}
			p.sendMessage("Seems locked...");
		}
	};

	public static ObjectClickHandler handleSouthEastDoor = new ObjectClickHandler(new Object[] { 2429 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if((northLeverUp && !southLeverUp) || (northNorthLeverUp && southLeverUp)) {
				handleDoor(p, obj);
				return;
			}
			p.sendMessage("Seems locked...");
		}
	};

	public static ObjectClickHandler handleSouthWestDoor = new ObjectClickHandler(new Object[] { 2427 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(obj.getY() > p.getY())
				handleDoor(p, obj);
			else
				p.sendMessage("It is locked from the other side...");
		}
	};

	public static ObjectClickHandler handleNorthNorthLever = new ObjectClickHandler(new Object[] { 2425, 2426 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getObjectId() == 2426) {
				obj.setId(2425);
				northNorthLeverUp = false;
				p.sendMessage("The lever is now down.");
				return;
			}
			obj.setIdTemporary(e.getObjectId()+1, TICKS_UP);
			northNorthLeverUp = true;
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					northNorthLeverUp = false;
				}
			}, TICKS_UP);
			p.sendMessage("The lever is now up.");
		}
	};

	public static ObjectClickHandler handleNorthLever = new ObjectClickHandler(new Object[] { 2421, 2422 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getObjectId() == 2422) {
				obj.setId(2421);
				northLeverUp = false;
				p.sendMessage("The lever is now down.");
				return;
			}
			obj.setIdTemporary(e.getObjectId()+1, TICKS_UP);
			northLeverUp = true;
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					northLeverUp = false;
				}
			}, TICKS_UP);
			p.sendMessage("The lever is now up.");
		}
	};

	public static ObjectClickHandler handleSouthLever = new ObjectClickHandler(new Object[] { 2423, 2424 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(e.getObjectId() == 2424) {
				obj.setId(2423);
				southLeverUp = false;
				p.sendMessage("The lever is now down.");
				return;
			}
			obj.setIdTemporary(e.getObjectId()+1, TICKS_UP);
			southLeverUp = true;
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					southLeverUp = false;
				}
			}, TICKS_UP);
			p.sendMessage("The lever is now up.");
		}
	};
}
