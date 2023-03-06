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
package com.rs.game.content.minigames.pyramidplunder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Rights;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class PyramidPlunderController extends Controller {
	final static int PLUNDER_INTERFACE = 428;

	private int tick = 500;
	private int currentRoom = 0;

	private int correctDoor = 0;
	private List<Integer> checkedDoors = new ArrayList<>();
	private Map<Integer, Integer> varbits = new HashMap<>();

	@Override
	public void start() {
		player.getInterfaceManager().sendOverlay(PLUNDER_INTERFACE);
		updatePlunderInterface();
		nextRoom();
	}

	@Override
	public void process() {
		if (tick == 0) {
			kickPlayer();
			tick = -1;
			return;
		}
		if (!player.hasRights(Rights.ADMIN))
			tick--;
		if (tick % 5 == 0)
			updatePlunderInterface();
	}

	private void updatePlunderInterface() {
		player.getVars().setVar(822, (currentRoom + 1) * 10 + 1);
		player.getVars().setVarBit(2377, currentRoom);
		player.getVars().setVarBit(2375, 500-tick);
	}

	private void kickPlayer() {
		player.lock();
		player.startConversation(new Dialogue()
				.addNPC(4476, HeadE.CHILD_FRUSTRATED, "You've had your five minutes of plundering! Now be off with you!"));
		WorldTasks.scheduleTimer(i -> {
			if (i == 1)
				player.getInterfaceManager().setFadingInterface(115);
			if (i == 3)
				exitMinigame();
			if (i == 6) {
				player.getInterfaceManager().setFadingInterface(170);
				player.unlock();
				return false;
			}
			return true;
		});

	}

	@Override
	public boolean login() {
		player.getInterfaceManager().sendOverlay(PLUNDER_INTERFACE);
		for (Integer vb : varbits.keySet())
			player.getVars().setVarBit(vb.intValue(), varbits.get(vb).intValue());
		updatePlunderInterface();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean sendDeath() {
		forceClose();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		forceClose();
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeOverlay();
		removeController();
	}

	public void exitMinigame() {
		player.setNextTile(PyramidPlunder.EXIT_TILE);
		forceClose();
	}

	public void nextRoom() {
		switch(currentRoom) {
			case 0 -> player.setNextTile(Tile.of(1927, 4477, 0));
			case 1 -> player.setNextTile(Tile.of(1977, 4471, 0));
			case 2 -> player.setNextTile(Tile.of(1954, 4477, 0));
			case 3 -> player.setNextTile(Tile.of(1927, 4453, 0));
			case 4 -> player.setNextTile(Tile.of(1965, 4444, 0));
			case 5 -> player.setNextTile(Tile.of(1927, 4424, 0));
			case 6 -> player.setNextTile(Tile.of(1943, 4421, 0));
			case 7 -> player.setNextTile(Tile.of(1974, 4420, 0));
			case 8 -> {
				player.startConversation(new Dialogue()
						.addSimple("Opening this door will cause you to leave the pyramid.")
						.addOptions("Would you like to exit?", new Options() {
							@Override
							public void create() {
								option("Yes", new Dialogue().addNext(() -> exitMinigame()));
								option("No", new Dialogue());
							}
						}));
			}
		}
		if (currentRoom < 8) {
			correctDoor = PyramidPlunder.DOORS[Utils.random(PyramidPlunder.DOORS.length)];
			varbits.clear();
			checkedDoors.clear();
			for(int i = 2346; i <= 2363; i++)
				player.getVars().setVarBit(i, 0);
			for(int i = 2366; i <= 2369; i++)
				player.getVars().setVarBit(i, 0);
			player.getVars().setVarBit(3422, 0);
			currentRoom++;
		}
	}
	
	public void updateObject(GameObject object, int value) {
		varbits.put(object.getDefinitions().varpBit, value);
		player.getVars().setVarBit(object.getDefinitions().varpBit, value);
	}

	public void setCurrentRoom(int room) {
		this.currentRoom = room;
	}

	public int getCurrentRoom() {
		return this.currentRoom;
	}

	public int getCorrectDoor() {
		return correctDoor;
	}
}
