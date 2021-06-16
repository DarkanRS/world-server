package com.rs.game.player.controllers;

import com.rs.game.player.Player;
import com.rs.game.player.content.minigames.duel.DuelRules;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class DuelController extends Controller {
	
	public static ButtonClickHandler handleDuelRequest = new ButtonClickHandler(640) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 18 || e.getComponentId() == 22) {
				e.getPlayer().getTemporaryAttributes().put("WillDuelFriendly", true);
				e.getPlayer().getVars().setVar(283, 67108864);
			} else if (e.getComponentId() == 19 || e.getComponentId() == 21) {
				e.getPlayer().getTemporaryAttributes().put("WillDuelFriendly", false);
				e.getPlayer().getVars().setVar(283, 134217728);
			} else if (e.getComponentId() == 20) {
				challenge(e.getPlayer());
			}
		}
	};

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
	public boolean processMagicTeleport(WorldTile toTile) {
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeController();
		remove();
	}

	@Override
	public void moved() {
		if (!isAtDuelArena(player)) {
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
		if (target.getTemporaryAttributes().get("DuelChallenged") == player) {
			player.getControllerManager().removeControllerWithoutCheck();
			target.getControllerManager().removeControllerWithoutCheck();
			target.getTemporaryAttributes().remove("DuelChallenged");
			player.setLastDuelRules(new DuelRules(player, target));
			target.setLastDuelRules(new DuelRules(target, player));
			player.getControllerManager().startController(new DuelArenaController(target, (boolean) target.getTemporaryAttributes().get("DuelFriendly")));
			target.getControllerManager().startController(new DuelArenaController(player, (boolean) target.getTemporaryAttributes().remove("DuelFriendly")));
			return false;
		}
		player.getTemporaryAttributes().put("DuelTarget", target);
		player.getInterfaceManager().sendInterface(640);
		player.getTemporaryAttributes().put("WillDuelFriendly", true);
		player.getVars().setVar(283, 67108864);
		return false;
	}

	public static void challenge(Player player) {
		player.closeInterfaces();
		Boolean friendly = (Boolean) player.getTemporaryAttributes().remove("WillDuelFriendly");
		if (friendly == null)
			return;
		Player target = (Player) player.getTemporaryAttributes().remove("DuelTarget");
		if (target == null || target.hasFinished() || !target.withinDistance(player, 14) || !(target.getControllerManager().getController() instanceof DuelController)) {
			player.sendMessage("Unable to find " + (target == null ? "your target" : target.getDisplayName()));
			return;
		}
		player.getTemporaryAttributes().put("DuelChallenged", target);
		player.getTemporaryAttributes().put("DuelFriendly", friendly);
		player.sendMessage("Sending " + target.getDisplayName() + " a request...");
		target.getPackets().sendDuelChallengeRequestMessage(player, friendly);
	}

	public void remove() {
		player.getInterfaceManager().removeOverlay();
		player.getAppearance().generateAppearanceData();
		player.setPlayerOption("null", 1);
	}

	@Override
	public void sendInterfaces() {
		if (isAtDuelArena(player)) {
			player.getInterfaceManager().setOverlay(638);
		}
	}

	public static boolean isAtDuelArena(WorldTile player) {
		return (player.getX() >= 3355 && player.getX() <= 3360 && player.getY() >= 3267 && player.getY() <= 3279) || (player.getX() >= 3355 && player.getX() <= 3379 && player.getY() >= 3272 && player.getY() <= 3279)
				|| (player.getX() >= 3374 && player.getX() <= 3379 && player.getY() >= 3267 && player.getY() <= 3271);
	}
}
