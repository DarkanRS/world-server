package com.rs.game.content.quests.familycrest;

import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import static com.rs.game.content.world.doors.Doors.handleDoor;

/**
 * Puzzle remake from this picture: https://gyazo.com/305b717ded771ca352b7cb50d51a91b8
 */
@PluginEventHandler
public class WitchHavenPuzzleFamilyCrest {
	private static boolean northNorthLeverUp = false;
	private static boolean northLeverUp = false;
	private static boolean southLeverUp = false;
	private static int TICKS_UP = 400;

	public static ObjectClickHandler handleNorthDoor = new ObjectClickHandler(new Object[] { 2431 }, e -> {
		GameObject obj = e.getObject();
		if(southLeverUp && !northLeverUp) {
			handleDoor(e.getPlayer(), obj);
			return;
		}
		e.getPlayer().sendMessage("Seems locked...");
	});

	public static ObjectClickHandler handleEastDoor = new ObjectClickHandler(new Object[] { 2430 }, e -> {
		GameObject obj = e.getObject();
		if(northLeverUp && northNorthLeverUp && !southLeverUp) {
			handleDoor(e.getPlayer(), obj);
			return;
		}
		e.getPlayer().sendMessage("Seems locked...");
	});

	public static ObjectClickHandler handleSouthEastDoor = new ObjectClickHandler(new Object[] { 2429 }, e -> {
		GameObject obj = e.getObject();
		if((northLeverUp && !southLeverUp) || (northNorthLeverUp && southLeverUp)) {
			handleDoor(e.getPlayer(), obj);
			return;
		}
		e.getPlayer().sendMessage("Seems locked...");
	});

	public static ObjectClickHandler handleSouthWestDoor = new ObjectClickHandler(new Object[] { 2427 }, e -> {
		GameObject obj = e.getObject();
		if(obj.getY() > e.getPlayer().getY())
			handleDoor(e.getPlayer(), obj);
		else
			e.getPlayer().sendMessage("It is locked from the other side...");
	});

	public static ObjectClickHandler handleNorthNorthLever = new ObjectClickHandler(new Object[] { 2425, 2426 }, e -> {;
		GameObject obj = e.getObject();
		if(e.getObjectId() == 2426) {
			obj.setId(2425);
			northNorthLeverUp = false;
			e.getPlayer().sendMessage("The lever is now down.");
			return;
		}
		obj.setIdTemporary(e.getObjectId()+1, TICKS_UP);
		northNorthLeverUp = true;
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				northNorthLeverUp = false;
			}
		}, TICKS_UP);
		e.getPlayer().sendMessage("The lever is now up.");
	});

	public static ObjectClickHandler handleNorthLever = new ObjectClickHandler(new Object[] { 2421, 2422 }, e -> {
		GameObject obj = e.getObject();
		if(e.getObjectId() == 2422) {
			obj.setId(2421);
			northLeverUp = false;
			e.getPlayer().sendMessage("The lever is now down.");
			return;
		}
		obj.setIdTemporary(e.getObjectId()+1, TICKS_UP);
		northLeverUp = true;
		WorldTasks.schedule(TICKS_UP, () -> northLeverUp = false);
		e.getPlayer().sendMessage("The lever is now up.");
	});

	public static ObjectClickHandler handleSouthLever = new ObjectClickHandler(new Object[] { 2423, 2424 }, e -> {
		GameObject obj = e.getObject();
		if(e.getObjectId() == 2424) {
			obj.setId(2423);
			southLeverUp = false;
			e.getPlayer().sendMessage("The lever is now down.");
			return;
		}
		obj.setIdTemporary(e.getObjectId()+1, TICKS_UP);
		southLeverUp = true;
		WorldTasks.schedule(TICKS_UP, () -> southLeverUp = false);
		e.getPlayer().sendMessage("The lever is now up.");
	});
}
