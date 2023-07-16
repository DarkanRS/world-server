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

import com.rs.Settings;
import com.rs.game.content.minigames.pest.PestControl.PestData;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;

import java.util.*;

public class Lander {

	public static Lander[] landers = new Lander[3];
	private static final int AUTO_GAME = Settings.getConfig().isDebug() ? 2 : 15;
	private static final int TIME = 30;

	private List<Player> lobby = Collections.synchronizedList(new LinkedList<Player>());
	private LobbyTimer timer;
	private LanderRequirement landerRequirement;

	public Lander(LanderRequirement landerRequirement) {
		this.landerRequirement = landerRequirement;
	}

	public class LobbyTimer extends WorldTask {

		private int seconds = TIME;

		@Override
		public void run() {
			if ((seconds == 0 && lobby.size() >= 5) || lobby.size() >= AUTO_GAME)
				passPlayersToGame();
			else if (seconds == 0)
				seconds = TIME;
			else if (lobby.size() == 0) {
				stop();
				return;
			}
			seconds--;
			if (seconds % 30 == 0)
				refreshLanderInterface();
		}

		public int getMinutes() {
			return seconds / 60;
		}
	}

	private void passPlayersToGame() {
		final List<Player> playerList = new LinkedList<>();
		playerList.addAll(Collections.synchronizedList(lobby));
		lobby.clear();
		if (playerList.size() > AUTO_GAME)
			for (int index = AUTO_GAME; index < playerList.size(); index++) {
				Player player = playerList.get(index);
				if (player == null) {
					playerList.remove(index);
					continue;
				}
				player.sendMessage("You have received priority over other players.");
				playerList.remove(index);
				lobby.add(player);
			}
		new PestControl(playerList, PestData.valueOf(landerRequirement.name())).create();
	}

	public void enterLander(Player player) {
		if (lobby.size() == 0)
			WorldTasks.schedule(timer = new LobbyTimer(), 2, 2);
		player.getControllerManager().startController(new PestControlLobbyController(landerRequirement.getId()));
		add(player);
		player.useStairs(-1, landerRequirement.getTile(), 1, 2, "You board the lander.");
	}

	public void exitLander(Player player) {
		player.useStairs(-1, landerRequirement.getExitTile(), 1, 2, "You leave the lander.");
		remove(player);
	}

	public void add(Player player) {
		lobby.add(player);
		refreshLanderInterface();
	}

	private void refreshLanderInterface() {
		for (Player teamPlayer : lobby)
			teamPlayer.getControllerManager().getController().sendInterfaces();
	}

	public void remove(Player player) {
		lobby.remove(player);
		refreshLanderInterface();
	}

	public List<Player> getPlayers() {
		return lobby;
	}

	public static enum LanderRequirement {

		NOVICE(0, 40, Tile.of(2661, 2639, 0), Tile.of(2657, 2639, 0)),

		INTERMEDIATE(1, 70, Tile.of(2641, 2644, 0), Tile.of(2644, 2644, 0)),

		VETERAN(2, 100, Tile.of(2635, 2653, 0), Tile.of(2638, 2653, 0));

		private static Map<Integer, LanderRequirement> landers = new HashMap<>();

		public static LanderRequirement forId(int id) {
			return landers.get(id);
		}

		static {
			for (LanderRequirement lander : LanderRequirement.values())
				landers.put(lander.getId(), lander);
		}

		int id, requirement, reward;
		int[] pests;
		Tile tile, exit;

		private LanderRequirement(int id, int requirement, Tile tile, Tile exit) {
			this.id = id;
			this.requirement = requirement;
			this.tile = tile;
			this.exit = exit;
		}

		public int getId() {
			return id;
		}

		public int getRequirement() {
			return requirement;
		}

		public Tile getTile() {
			return tile;
		}

		public Tile getExitTile() {
			return exit;
		}
	}

	public static Lander[] getLanders() {
		return landers;
	}

	public LanderRequirement getLanderRequirement() {
		return landerRequirement;
	}

	static {
		for (int i = 0; i < landers.length; i++)
			landers[i] = new Lander(LanderRequirement.forId(i));
	}

	@Override
	public String toString() {
		return landerRequirement.name().toLowerCase();
	}

	public static boolean canEnter(Player player, int landerIndex) {
		Lander lander = landers[landerIndex];
		if (player.getSkills().getCombatLevelWithSummoning() < lander.getLanderRequirement().requirement) {
			player.simpleDialogue("You need a combat level of " + lander.getLanderRequirement().getRequirement() + " or more to enter in boat.");
			return false;
		}
		if (player.getPet() != null || player.getFamiliar() != null) {
			player.sendMessage("You can't take a follower into the lander, there isn't enough room!");
			return false;
		}
		lander.enterLander(player);
		return true;
	}

	public LobbyTimer getTimer() {
		return timer;
	}
}
