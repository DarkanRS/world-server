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
