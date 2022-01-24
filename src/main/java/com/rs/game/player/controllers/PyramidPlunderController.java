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
package com.rs.game.player.controllers;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.utils.Ticks;

public class PyramidPlunderController extends Controller {
    final static int PLUNDER_INTERFACE = 428;

	@Override
	public void start() { //600ms
		startMinigame();


	}

	@Override
	public boolean login() {
		exitMinigame();
		forceClose();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean sendDeath() {
		removeController();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
	}

	@Override
	public void forceClose() {
		removeController();
	}

	public void startMinigame() {
		player.lock(11);
		WorldTasks.schedule(new WorldTask() {
			int tick;
			@Override
			public void run() {
				if(tick == 0)
					player.getInterfaceManager().setFadingInterface(115);
				if(tick == 2) {
					player.faceSouth();
					player.setNextWorldTile(new WorldTile(1927, 4477, 0));
				}
				if(tick == 5)
					player.getInterfaceManager().setFadingInterface(170);
                if(tick == 6) {
                    updatePlunderInterface(player);
                }

				tick++;
			}
		}, 0, 1);
	}

    private static void updatePlunderInterface(Player player) {
        if(player.getInterfaceManager().hasRezizableScreen())
            player.getInterfaceManager().setOverlay(PLUNDER_INTERFACE);
        else
            player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(PLUNDER_INTERFACE);
        player.getVars().setVar(822, 21);
    }

	public void exitMinigame() {
		player.setNextWorldTile(new WorldTile(3288, 2801, 0));
	}
}
