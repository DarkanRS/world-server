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
package com.rs.game.content.skills.dungeoneering.rooms.puzzles;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;

public class LeverRoom extends PuzzleRoom {

	/*
	private static final int[] SWITCH_UP = {
	49381, 49382, 49383, 54333, 33675
	};
	 */

	private static final int[] SWITCH_DOWN =
		{ 49384, 49385, 49386, 49386, 49386 //TODO find down of 54333, 33675
		};

	private int leverCount, leverTicks, maxTicks;
	private WorldTask resetTask;

	@Override
	public void openRoom() {
		manager.spawnRandomNPCS(reference);
	}

	@Override
	public boolean processObjectClick1(Player player, GameObject object) {
		if (object.getDefinitions().getName().equals("Switch")) {
			player.setNextAnimation(new Animation(3611));
			if (isComplete()) {
				player.sendMessage("The lever doesn't seem to respond.");
				return false;
			}
			if (resetTask == null)
				addResetTask();
			GameObject down = new GameObject(object);
			down.setId(SWITCH_DOWN[type]);
			World.spawnObjectTemporary(down, (maxTicks - leverTicks));
			leverCount++;
			return false;
		}
		return true;
	}

	private void addResetTask() {
		//Still want it to be possible when people leave a 5:5 (4:5), and very easy on a 5:1
		int size =  manager.getParty().getTeam().size();
		int difficulty = Math.min(manager.getParty().getDificulty(), size);
		//5 - 2.4 seconds
		//4 - 3.6 seconds
		//3 - 5.4 seconds
		//2 - 7.2 seconds
		//1 - 14.4 seconds
		maxTicks = (6 - difficulty) + ((size == 1 ? 23 : 20) / difficulty);
		resetTask = new ResetTask();
		WorldTasks.schedule(resetTask, 0, 0);
	}

	private void resetTask() {
		leverTicks = 0;
		maxTicks = 0;
		resetTask = null;
	}

	private class ResetTask extends WorldTask {

		@Override
		public void run() {

			if (leverCount == 5) {
				setComplete();
				resetTask();
				stop();
				return;
			}

			leverTicks++;
			if (leverTicks == maxTicks) {
				resetTask();
				if (leverCount != 5) {
					leverCount = 0;

					for (Player player : manager.getParty().getTeam()) {
						player.sendMessage("You hear a loud noise and all the switches toggle back off.");
						if (player.withinDistance(manager.getTile(reference, 7, 8), 2) || !manager.getCurrentRoomReference(Tile.of(player.getTile())).equals(reference))
							continue;
						World.sendSpotAnim(Tile.of(player.getTile()), new SpotAnim(2759));
						player.setNextAnimation(new Animation(13694));
						player.applyHit(new Hit(player, (int) (player.getMaxHitpoints() * .3), HitLook.TRUE_DAMAGE));
					}
					stop();
					return;
				}
			}
		}
	}

	@Override
	public String getCompleteMessage() {
		return "As the last lever is pulled, you hear a click. All the doors in the room are now unlocked.";
	}
}
