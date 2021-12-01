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

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.lib.game.WorldTile;



public class DamonheimController extends Controller {

	private boolean showingOption;

	@Override
	public void start() {
		setInviteOption(true);
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		player.setNextFaceWorldTile(target);
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
		if (!isAtKalaboss(player)) {
			setInviteOption(false);
			player.getDungManager().leaveParty();
			removeController();
		} else
			setInviteOption(true);
	}

	public static boolean isAtKalaboss(WorldTile tile) {
		return tile.getX() >= 3385 && tile.getX() <= 3513 && tile.getY() >= 3605 && tile.getY() <= 3794;
	}

	public void setInviteOption(boolean show) {
		if (show == showingOption)
			return;
		showingOption = show;
		player.setPlayerOption(show ? "Invite" : "null", 1);
	}
}
