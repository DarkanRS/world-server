package com.rs.game.player.controllers;

import com.rs.game.object.GameObject;
import com.rs.game.player.content.minigames.pest.Lander;
import com.rs.game.player.dialogues.LanderDialouge;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public final class PestControlLobbyController extends Controller {

	private int landerId;

	public PestControlLobbyController(int landerId) {
		this.landerId = landerId;
	}

	@Override
	public void start() {

	}

	@Override
	public void sendInterfaces() {
		player.getPackets().setIFText(407, 3, Utils.fixChatMessage(Lander.getLanders()[landerId].toString()));
		int minutesLeft = (Lander.getLanders()[landerId].getTimer().getMinutes());
		player.getPackets().setIFText(407, 13, "Next Departure: " + minutesLeft + " minutes " + (!(minutesLeft % 2 == 0) ? " 30 seconds" : ""));
		player.getPackets().setIFText(407, 14, "Player's Ready: " + Lander.getLanders()[landerId].getPlayers().size());
		player.getPackets().setIFText(407, 16, "Commendations: " + player.getPestPoints());
		player.getInterfaceManager().setOverlay(407);
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControllerManager().forceStop();
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getControllerManager().forceStop();
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getControllerManager().forceStop();
		return true;
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeOverlay();
		Lander.getLanders()[landerId].exitLander(player);
	}

	@Override
	public boolean logout() {
		Lander.getLanders()[landerId].remove(player);// to stop the timer in the
														// lander and prevent
														// future errors
		return false;
	}

	@Override
	public boolean canSummonFamiliar() {
		player.sendMessage("You feel it's best to keep your Familiar away during this game.");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
		case 14314:
		case 25629:
		case 25630:
			player.getDialogueManager().execute(new LanderDialouge());
			return true;
		}
		return true;
	}
}
