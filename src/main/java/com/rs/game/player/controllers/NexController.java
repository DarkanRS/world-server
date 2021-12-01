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

import com.rs.game.npc.godwars.zaros.NexArena;

public class NexController extends Controller {
	
	private transient NexArena arena;
	
	public NexController(NexArena arena) {
		this.arena = arena;
		if (arena == null)
			this.arena = NexArena.getGlobalInstance();
	}
	
	@Override
	public void start() {
		arena.addPlayer(player);
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		arena.removePlayer(player);
		return false; // so doesnt remove script
	}

	@Override
	public boolean login() {
		if (arena == null)
			arena = NexArena.getGlobalInstance();
		arena.addPlayer(player);
		sendInterfaces();
		return false; // so doesnt remove script
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().setOverlay(601);
		player.getPackets().sendRunScriptReverse(1171);
	}

	@Override
	public boolean sendDeath() {
		remove();
		removeController();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		remove();
		removeController();
	}

	@Override
	public void forceClose() {
		remove();
	}

	public void remove() {
		arena.removePlayer(player);
		player.getInterfaceManager().removeOverlay();
	}
}
