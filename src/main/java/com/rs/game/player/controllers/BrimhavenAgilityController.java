package com.rs.game.player.controllers;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public final class BrimhavenAgilityController extends Controller {

	private static final List<Player> players = new ArrayList<Player>();
	private static PlayingGame currentGame;
	private static BladesManager bladesManager;

	private static void removePlayer(Player player) {
		synchronized (players) {
			players.remove(player);
			if (player.getSize() == 0)
				cancelGame();
		}
		player.getHintIconsManager().removeUnsavedHintIcon();
		if (player.getTemporaryAttributes().remove("BrimhavenAgility") != null)
			player.getVars().setVarBit(4456, 0);
		player.getInterfaceManager().removeWindowInterface(player.getInterfaceManager().hasRezizableScreen() ? 1 : 11);
	}

	private void addPlayer(Player player) {
		synchronized (players) {
			players.add(player);
			if (players.size() == 1)
				startGame();
			else
				PlayingGame.addIcon(player);
		}
		sendInterfaces();
	}

	private static void startGame() {
		WorldTasksManager.schedule(currentGame = new PlayingGame(), 0, Ticks.fromMinutes(1));
		WorldTasksManager.schedule(bladesManager = new BladesManager(), 9, 9);
	}

	private static void cancelGame() {
		currentGame.stop();
		bladesManager.stop();
		PlayingGame.taggedDispenser = null;
		currentGame = null;
		bladesManager = null;
	}

	private static class BladesManager extends WorldTask {

		@Override
		public void run() {

		}
	}

	private static class PlayingGame extends WorldTask {

		private static WorldTile taggedDispenser;

		private static WorldTile getNextDispenser() {
			while (true) {
				WorldTile tile = new WorldTile(2761 + 11 * Utils.random(5), 9546 + 11 * Utils.random(5), 3);
				if (!(tile.getX() == 2805 && tile.getY() == 9590) && !(taggedDispenser != null && tile.equals(taggedDispenser)))
					return tile;
			}
		}

		private static void addIcon(Player player) {
			Integer stage = (Integer) player.getTemporaryAttributes().get("BrimhavenAgility");
			if (stage != null)
				if (stage == -1) {
					player.getTemporaryAttributes().remove("BrimhavenAgility"); // didnt
					// click
					player.getVars().setVarBit(4456, 0);
				} else
					player.getTemporaryAttributes().put("BrimhavenAgility", -1); // clicked
			if (taggedDispenser == null)
				return;
			player.getHintIconsManager().addHintIcon(taggedDispenser.getX(), taggedDispenser.getY(), taggedDispenser.getPlane(), 65, 2, 0, -1, false);
		}

		@Override
		public void run() { // selects dispenser
			try {
				taggedDispenser = getNextDispenser();
				synchronized (players) {
					for (Player player : players)
						addIcon(player);
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}

	}

	@Override
	public boolean processObjectClick1(final GameObject object) {
		if (object.getId() == 3581 || object.getId() == 3608) {
			if (PlayingGame.taggedDispenser == null || PlayingGame.taggedDispenser.getTileHash() != object.getTileHash()) {
				return false;
			}
			Integer stage = (Integer) player.getTemporaryAttributes().get("BrimhavenAgility");
			if (stage == null) {
				player.getTemporaryAttributes().put("BrimhavenAgility", 0); // clicked
				player.getVars().setVarBit(4456, 1); // ready to get
				// tickets
				player.sendMessage("You get tickets by tagging more than one pillar in a row. Tag the next pillar!");
			} else if (stage == 0) {
				player.sendMessage("You have already tagged this pillar, wait until the arrow moves again.");
			} else {
				if (!player.getInventory().hasFreeSlots() && !player.getInventory().containsOneItem(2996)) {
					player.sendMessage("Not enough space in your inventory.");
					return false;
				}
				player.getTemporaryAttributes().put("BrimhavenAgility", 0); // clicked
				player.getInventory().addItem(2996, 1);
			}
			return false;
		}
		return true;
	}

	@Override
	public void start() {
		addPlayer(player);
	}

	@Override
	public boolean logout() {
		removePlayer(player);
		return false;
	}

	@Override
	public boolean login() {
		addPlayer(player);
		return false; // so doesnt remove script
	}

	@Override
	public void magicTeleported(int type) {
		removePlayer(player);
		removeController();
	}

	@Override
	public void forceClose() {
		removePlayer(player);
	}

	@Override
	public boolean sendDeath() {
		removePlayer(player);
		removeController();
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().setWindowInterface(player.getInterfaceManager().hasRezizableScreen() ? 1 : 11, 5);
	}
}
