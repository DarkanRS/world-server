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
package com.rs.game.content.world.areas.dungeons;

import com.rs.game.content.skills.slayer.Slayer;
import com.rs.game.content.world.LightSource;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class UndergroundDungeonController extends Controller {

	private transient int ticks;
	private transient boolean initial;

	private boolean hasStench, requiresLightSource;

	public UndergroundDungeonController(boolean hasStench, boolean requiresLightSource) {
		this.hasStench = hasStench;
		this.requiresLightSource = requiresLightSource;
	}

	@Override
	public void start() {
		init();
	}

	private void init() {
		ticks = 0;
		initial = true;
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		if (requiresLightSource) {
			boolean hasLight = LightSource.hasLightSource(player);
			player.getInterfaceManager().sendOverlay(hasLight ? (LightSource.hasExplosiveSource(player) ? 98 : 97) : 96, true);
			if (!hasLight)
				player.getPackets().setBlockMinimapState(2);
			else
				player.getPackets().setBlockMinimapState(0);
		}
	}

	private void checkRequriments() {
		boolean lastInitial = initial;
		if (hasStench)
			if (!Slayer.hasNosepeg(player) && !Slayer.hasMask(player)) {
				if (initial)
					player.sendMessage("The stench of the monsters begins to reach your nose..");
				initial = false;
			} else
				initial = true;
		if (requiresLightSource)
			if (!LightSource.hasLightSource(player)) {
				if (initial)
					player.sendMessage("You hear tiny insects skittering over the ground...");
				initial = false;
			} else
				initial = true;
		if (lastInitial != initial)
			sendInterfaces();
	}

	@Override
	public void process() {
		checkRequriments();
		if (initial)
			return;
		ticks++;
		if (hasStench)
			if (ticks % 12 == 0) {
				player.sendMessage("The strench of the monsters burns your innards.");
				player.applyHit(new Hit(player, 200, HitLook.TRUE_DAMAGE));
			}
		if (requiresLightSource)
			if (ticks % 2 == 0)
				if (!LightSource.hasLightSource(player))
					if (!player.isLocked())
						player.applyHit(new Hit(player, Utils.random(10, 100), HitLook.TRUE_DAMAGE));
	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (object.getId() == 31316) {
			player.useStairs(-1, Tile.of(3360, 2971, 0), 1, 2);
			player.getControllerManager().forceStop();
			return false;
		}
		if (object.getId() == 5946) {
			player.useStairs(828, Tile.of(3168, 3171, 0), 1, 2);
			player.getControllerManager().forceStop();
			return false;
		}
		if (object.getId() == 15811)
			player.getControllerManager().forceStop();
		else if (object.getId() == 32944) {
			player.useStairs(-1, Tile.of(3219, 9532, 2), 1, 2);
			player.getControllerManager().forceStop();
			return false;
		} else if (object.getId() == 31435)
			return false;
		else if (object.getId() == 15811) {
			player.useStairs(-1, Tile.of(3749, 2973, 0), 1, 2);
			return false;
		} else if (object.getId() == 15790) {
			if (object.getX() == 3829)
				player.useStairs(-1, Tile.of(3831, 3062, 0), 1, 2);
			if (object.getX() == 3814)
				player.useStairs(-1, Tile.of(3816, 3062, 0), 1, 2);
			player.getControllerManager().forceStop();
			return false;
		} else if (object.getId() == 32953 || object.getId() == 32952) {
			player.useStairs(-1, Tile.of(2747, 5374, 0), 0, 1);
			player.getControllerManager().forceStop();
			return false;
		} else if (object.getId() == 15812) {
			player.useStairs(-1, Tile.of(3749, 2973, 0), 1, 2);
			player.getControllerManager().forceStop();
			return false;
		} else if (object.getId() == 6912) {
			player.setNextAnimation(new Animation(10578));
			player.useStairs(-1, object.getTile(), 1, 2);
			player.useStairs(10579, Tile.of(player.getX(), player.getY() == 9601 ? player.getY() + 2 : player.getY() - 2, 0), 1, 2);
			return false;
		} else if (object.getId() == 6899) {
			player.setNextAnimation(new Animation(10578));
			player.useStairs(-1, object.getTile(), 1, 2);
			player.useStairs(10579, Tile.of(3219, 9618, 0), 1, 2);
			player.getControllerManager().forceStop();
			player.sendMessage("You squeeze through the hole.");
			return false;
		} else if (object.getId() == 6439) {
			player.useStairs(828, Tile.of(3310, 2961, 0), 1, 2);
			player.getControllerManager().forceStop();
			return false;
		} else if (object.getId() == 31390) {
			player.useStairs(-1, Tile.of(3318, 9355, 0), 1, 2, "You tumble into the darkness, arriving on a different cave level.");
			return false;
		} else if (object.getId() == 31367) {
			player.useStairs(-1, Tile.of(3338, 9350, 0), 1, 2, "You tumble into the darkness, arriving on a different cave level.");
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public void forceClose() {
		player.getPackets().setBlockMinimapState(0);
		player.getInterfaceManager().removeOverlay(true);
	}

	@Override
	public boolean sendDeath() {
		player.getPackets().setBlockMinimapState(0);
		player.getInterfaceManager().removeOverlay(true);
		removeController();
		return true;
	}

	@Override
	public boolean login() {
		init();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}
}