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
package com.rs.game.content.minigames.pest;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public class PestControlGameController extends Controller {

	private transient PestControl control;
	private double points;

	@Override
	public boolean playAmbientOnControllerRegionEnter() {
		return false;
	}
	@Override
	public boolean playAmbientStrictlyBackgroundMusic() {
		return true;
	}

	public PestControlGameController(PestControl control) {
		this.control = control;
	}

	@Override
	public void start() {
		setPoints(0.0D);
		sendInterfaces();
		player.setForceMultiArea(true);
		player.getMusicsManager().playSongAndUnlock(588);
	}

	@Override
	public void sendInterfaces() {
		updatePestPoints();
		player.getInterfaceManager().sendOverlay(408);
	}

	private void updatePestPoints() {
		boolean isGreen = getPoints() > 750;
		player.getPackets().setIFText(408, 11, (isGreen ? "<col=75AE49>" : "") + (int) getPoints() + "</col>");
	}

	@Override
	public void forceClose() {
		if (control != null) {
			if (control.getPortalCount() != 0)
				if (control.getPlayers().contains(player))
					control.getPlayers().remove(player);
			player.useStairs(-1, Lander.getLanders()[control.getPestData().ordinal()].getLanderRequirement().getExitTile(), 1, 2);
		} else
			player.useStairs(-1, Tile.of(2657, 2639, 0), 1, 2);
		player.setForceMultiArea(false);
		player.getInterfaceManager().removeOverlay();
		player.reset();
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		player.simpleDialogue("You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		player.simpleDialogue("You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean canMove(Direction dir) {
		Tile toTile = Tile.of(player.getX() + dir.getDx(), player.getY() + dir.getDy(), player.getPlane());
		return !control.isBrawlerAt(toTile);
	}

	@Override
	public boolean login() {
		return true;
	}

	@Override
	public boolean logout() {
		if (control != null)
			control.getPlayers().remove(player);
		return false;
	}

	@Override
	public boolean canSummonFamiliar() {
		player.sendMessage("You feel it's best to keep your Familiar away during this game.");
		return false;
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
					player.reset();
					player.setNextTile(control.getTile(35 - Utils.random(4), 54 - (Utils.random(3))));
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
	public void processOutgoingHit(final Hit hit, Entity target) {
		setPoints(getPoints() + hit.getDamage());
		updatePestPoints();
	}

	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
	}
}
