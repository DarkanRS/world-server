package com.rs.game.player.controllers;

import com.rs.game.object.GameObject;
import com.rs.game.player.Equipment;
import com.rs.game.player.content.minigames.CastleWars;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;

public class CastleWarsWaitingController extends Controller {

	private int team;
	
	public CastleWarsWaitingController(int team) {
		this.team = team;
	}

	@Override
	public void start() {
		sendInterfaces();
	}

	// You can't leave just like that!

	public void leave() {
		player.getInterfaceManager().removeOverlay();
		CastleWars.removeWaitingPlayer(player, team);
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().setOverlay(57);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (interfaceId == 387) {
			if (componentId == 9 || componentId == 6) {
				player.sendMessage("You can't remove your team's colours.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == Equipment.CAPE || slotId == Equipment.HEAD) {
			player.sendMessage("You can't remove your team's colours.");
			return false;
		}
		return true;
	}

	@Override
	public boolean sendDeath() {
		removeController();
		leave();
		return true;
	}

	@Override
	public boolean logout() {
		player.setLocation(new WorldTile(CastleWars.LOBBY, 2));
		return true;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getDialogueManager().execute(new SimpleMessage(), "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		int id = object.getId();
		if (id == 4389 || id == 4390) {
			removeController();
			leave();
			return false;
		}
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
		leave();
	}

	@Override
	public void forceClose() {
		leave();
	}
}
