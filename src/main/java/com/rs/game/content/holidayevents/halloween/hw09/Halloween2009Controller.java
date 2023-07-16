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
package com.rs.game.content.holidayevents.halloween.hw09;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.engine.dialogue.Dialogue;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;

import java.util.HashSet;
import java.util.Set;


public class Halloween2009Controller extends Controller {

	private Set<Integer> path;
	private Set<Integer> webbedUp;
	private static int WEBS_TOTAL = 63;

	public Halloween2009Controller() {
		path = Halloween2009.getRandomPath();
		webbedUp = new HashSet<>();
	}

	@Override
	public void start() {
		player.setNextTile(Halloween2009.START_LOCATION);
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("Oh dear, you have died.");
				else if (loop == 3) {
					player.setNextTile(player.getI(Halloween2009.STAGE_KEY) < 10 ? Halloween2009.START_LOCATION : Tile.of(3211, 3424, 0));
					player.reset();
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					if (player.getI(Halloween2009.STAGE_KEY) >= 10)
						player.getControllerManager().forceStop();
					player.jingle(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch(object.getId()) {
		case 46935:
			player.sendOptionDialogue("Are you sure you want to leave? Any maze/webbing progress will be reset!", ops -> {
				ops.add("Yes, I understand.", () -> {
					player.getControllerManager().forceStop();
					player.useStairs(Tile.of(3211, 3424, 0));
				});
				ops.add("Nevermind.");
			});
			break;
		}
		return true;
	}

	@Override
	public void process() {
		if (player.getI(Halloween2009.STAGE_KEY) >= 3)
			refreshVars(player);
	}

	@Override
	public boolean login() {
		Halloween2009.refreshWebbables(player, player.getEquipment().getWeaponId() == 15353);
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void magicTeleported(int type) {

	}

	@Override
	public void forceClose() {

	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	private void refreshVars(Player player) {
		if (player.getI(Halloween2009.STAGE_KEY) >= 10)
			player.getVars().setVarBit(4883, 50);
		else if (player.getI(Halloween2009.STAGE_KEY) >= 3)
			player.getVars().setVarBit(4883, 20);
	}

	public Set<Integer> getPath() {
		return path;
	}

	public void web(int objectId) {
		webbedUp.add(objectId);
		Halloween2009.refreshWebbables(player, player.getEquipment().getWeaponId() == 15353);
		int numLeft = WEBS_TOTAL-webbedUp.size();
		if (numLeft > 0)
			player.sendMessage("You web up the " + ObjectDefinitions.getDefs(objectId, player.getVars()).getName().toLowerCase() + ". Only <col=FF0000>" + numLeft + "</col> more to go!");
		else {
			player.save(Halloween2009.STAGE_KEY, 8);
			player.sendMessage("<col=FF0000>That's all of them! Return to the Grim Reaper.");
			player.startConversation(new Dialogue().addNext(new SpiderStatement("That's it! We've done them all! Now we should talk to the scary skeleton guy.")));
		}
	}

	public Set<Integer> getWebbedUp() {
		return webbedUp;
	}
}
