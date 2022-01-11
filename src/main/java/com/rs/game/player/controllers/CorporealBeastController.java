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

import com.rs.Settings;
import com.rs.game.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class CorporealBeastController extends Controller {

	public static ButtonClickHandler handleEnterWarning = new ButtonClickHandler(650) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 15) {
				e.getPlayer().stopAll();
				e.getPlayer().setNextWorldTile(new WorldTile(2974, 4384, e.getPlayer().getPlane()));
				e.getPlayer().getControllerManager().startController(new CorporealBeastController());
			} else if (e.getComponentId() == 16)
				e.getPlayer().closeInterfaces();
		}
	};

	@Override
	public void start() {

	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 37929 || object.getId() == 38811) {
			removeController();
			player.stopAll();
			player.setNextWorldTile(new WorldTile(2970, 4384, player.getPlane()));
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
	}

	@Override
	public boolean sendDeath() {
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("Oh dear, you have died.");
				else if (loop == 3) {
					player.sendItemsOnDeath(null, false);
					player.reset();
					player.setNextWorldTile(new WorldTile(Settings.getConfig().getPlayerRespawnTile()));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeController();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

}
