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
package com.rs.game.content.skills.dungeoneering;

import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DamonheimController extends Controller {

	private boolean showingOption;
	
	public static ObjectClickHandler handleJumpDownExit = new ObjectClickHandler(new Object[] { 50552 }, e -> {
		if (e.getPlayer().getControllerManager().getController() instanceof DungeonController)
			e.getPlayer().getControllerManager().removeControllerWithoutCheck();
		e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject().getTile(), 1, Direction.NORTH));
		e.getPlayer().getPackets().sendVarc(234, 0);// Party Config Interface
		e.getPlayer().getControllerManager().startController(new DamonheimController());
		e.getPlayer().useStairs(13760, Tile.of(3454, 3725, 0), 2, 3);
	});
	
	@Override
	public void start() {
		setInviteOption(true);
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		player.setNextFaceTile(target.getTile());
		player.getDungManager().invite(target.getDisplayName());
		return false;
	}

	@Override
	public boolean login() {
		moved();
		DungeonManager.checkRejoin(player);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		setInviteOption(false);
		player.getDungManager().leaveParty();
		removeController();
	}

	@Override
	public boolean sendDeath() {
		setInviteOption(false);
		player.getDungManager().leaveParty();
		removeController();
		return true;
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void forceClose() {
		setInviteOption(false);
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 9707)
			player.getDungManager().leaveParty();
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick2(NPC npc) {
		if (npc.getId() == 9707)
			player.getDungManager().leaveParty();
		return true;
	}

	@Override
	public void moved() {
		if (player.getDungManager().isInsideDungeon())
			return;
		if (!isAtKalaboss(player.getTile())) {
			setInviteOption(false);
			player.getDungManager().leaveParty();
			removeController();
		} else
			setInviteOption(true);
	}

	public static boolean isAtKalaboss(Tile tile) {
		return tile.getX() >= 3385 && tile.getX() <= 3513 && tile.getY() >= 3605 && tile.getY() <= 3794;
	}

	public void setInviteOption(boolean show) {
		if (show == showingOption)
			return;
		showingOption = show;
		player.setPlayerOption(show ? "Invite" : "null", 1);
	}
}
