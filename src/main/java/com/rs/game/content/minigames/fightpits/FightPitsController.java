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
package com.rs.game.content.minigames.fightpits;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;

public class FightPitsController extends Controller {

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 68222) {
			FightPits.leaveArena(player, 1);
			return false;
		}
		return true;
	}

	// fuck it dont dare touching here again or dragonkk(me) kills u irl :D btw
	// nice code it keeps nulling, fixed

	@Override
	public boolean logout() {
		FightPits.leaveArena(player, 0);
		return false;
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.sendMessage("You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.sendMessage("You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(Tile toTile) {
		player.sendMessage("You can't teleport out of the arena!");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		FightPits.leaveArena(player, 3); // teled out somehow, impossible usualy
	}

	@Override
	public boolean login() { // shouldnt happen
		removeController();
		FightPits.leaveArena(player, 2);
		return false;
	}

	@Override
	public void forceClose() {
		FightPits.leaveArena(player, 3);
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			if (canHit(target))
				return true;
			player.sendMessage("You're not allowed to attack yet!");
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		return FightPits.canFight();
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasks.schedule(new Task() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("You have been defeated!");
				else if (loop == 3) {
					player.reset();
					FightPits.leaveArena(player, 2);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.jingle(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(373);
		if (FightPits.currentChampion != null)
			player.getPackets().setIFText(373, 10, "Current Champion: JaLYt-Ket-" + FightPits.currentChampion);
	}
}
