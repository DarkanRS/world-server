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
package com.rs.game.content.minigames.duel;

import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class DuelController extends Controller {

	public static ButtonClickHandler handleDuelRequest = new ButtonClickHandler(640, e -> {
		if (e.getComponentId() == 18 || e.getComponentId() == 22) {
			e.getPlayer().getTempAttribs().setB("WillDuelFriendly", true);
			e.getPlayer().getVars().setVar(283, 67108864);
		} else if (e.getComponentId() == 19 || e.getComponentId() == 21) {
			e.getPlayer().getTempAttribs().setB("WillDuelFriendly", false);
			e.getPlayer().getVars().setVar(283, 134217728);
		} else if (e.getComponentId() == 20)
			challenge(e.getPlayer());
	});

	@Override
	public void start() {
		sendInterfaces();
		player.getAppearance().generateAppearanceData();
		player.setPlayerOption("Challenge", 1);
		moved();
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void forceClose() {
		remove();
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		return true;
	}

	@Override
	public boolean processItemTeleport(Tile toTile) {
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
		remove();
	}

	@Override
	public void moved() {
		if (!isAtDuelArena(player.getTile())) {
			removeController();
			remove();
		}
	}

	@Override
	public boolean canPlayerOption1(final Player target) {
		player.stopAll();
		if (target.getInterfaceManager().containsScreenInter()) {
			player.sendMessage("The other player is busy.");
			return false;
		}
		if (target.getTempAttribs().getO("DuelChallenged") == player) {
			player.getControllerManager().removeControllerWithoutCheck();
			target.getControllerManager().removeControllerWithoutCheck();
			target.getTempAttribs().removeO("DuelChallenged");
			player.setLastDuelRules(new DuelRules(player, target));
			target.setLastDuelRules(new DuelRules(target, player));
			player.getControllerManager().startController(new DuelArenaController(target, target.getTempAttribs().getB("DuelFriendly")));
			target.getControllerManager().startController(new DuelArenaController(player, target.getTempAttribs().removeB("DuelFriendly")));
			return false;
		}
		player.getTempAttribs().setO("DuelTarget", target);
		player.getInterfaceManager().sendInterface(640);
		player.getTempAttribs().setB("WillDuelFriendly", true);
		player.getVars().setVar(283, 67108864);
		return false;
	}

	public static void challenge(Player player) {
		player.closeInterfaces();
		Player target = player.getTempAttribs().getO("DuelTarget");
		if (target == null || target.hasFinished() || !target.withinDistance(player.getTile(), 14) || !(target.getControllerManager().getController() instanceof DuelController)) {
			player.sendMessage("Unable to find " + (target == null ? "your target" : target.getDisplayName()));
			return;
		}
		player.getTempAttribs().setO("DuelChallenged", target);
		player.getTempAttribs().setB("DuelFriendly", player.getTempAttribs().getB("WillDuelFriendly"));
		player.sendMessage("Sending " + target.getDisplayName() + " a request...");
		target.getPackets().sendDuelChallengeRequestMessage(player, player.getTempAttribs().getB("WillDuelFriendly"));
	}

	public void remove() {
		player.getInterfaceManager().removeOverlay();
		player.getAppearance().generateAppearanceData();
		player.setPlayerOption("null", 1);
	}

	@Override
	public void sendInterfaces() {
		if (isAtDuelArena(player.getTile()))
			player.getInterfaceManager().sendOverlay(638);
	}

	public static boolean isAtDuelArena(Tile player) {
		return (player.getX() >= 3355 && player.getX() <= 3360 && player.getY() >= 3267 && player.getY() <= 3279) || (player.getX() >= 3355 && player.getX() <= 3379 && player.getY() >= 3272 && player.getY() <= 3279)
				|| (player.getX() >= 3374 && player.getX() <= 3379 && player.getY() >= 3267 && player.getY() <= 3271);
	}
}
