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
package com.rs.game.content.minigames.creations;

import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

public class StealingCreationLobby {

	private static final int[] TOTAL_SKILL_IDS = { Constants.WOODCUTTING, Constants.MINING, Constants.FISHING, Constants.HUNTER, Constants.COOKING, Constants.HERBLORE, Constants.CRAFTING, Constants.SMITHING, Constants.FLETCHING, Constants.RUNECRAFTING, Constants.CONSTRUCTION };
	private static final int[] TOTAL_COMBAT_IDS = { Constants.ATTACK, Constants.STRENGTH, Constants.DEFENSE, Constants.HITPOINTS, Constants.RANGE, Constants.MAGIC, Constants.PRAYER, Constants.SUMMONING };
	private static List<Player> redTeam, blueTeam;
	private static LobbyTimer lobbyTask;

	static {
		reset();
	}

	private static class LobbyTimer extends Task {

		public int minutes;

		public LobbyTimer() {
			minutes = 2;
		}

		@Override
		public void run() {
			minutes--;
			if (minutes == 0) {
				passToGame(); // cancel since all players left
				removeTask();
				return;
			}
			updateInterfaces();
		}

		public int getMinutes() {
			return minutes;
		}

		private void passToGame() { // should be safe right?
			StealingCreationManager.createGame(8, blueTeam, redTeam);
			reset();
		}
	}

	public static void reset() {
		redTeam = new ArrayList<>();
		blueTeam = new ArrayList<>();
	}

	private static boolean hasRequiredPlayers() {
		if (redTeam.size() >= 1 && blueTeam.size() >= 1)
			return true;
		return false;
	}

	public static boolean enterTeamLobby(Player player, boolean onRedTeam) {
		if (!canEnter(player, onRedTeam))
			return false;
		if (onRedTeam) {
			if (!redTeam.contains(player))
				redTeam.add(player);
		} else if (!blueTeam.contains(player))
			blueTeam.add(player);
		if (hasRequiredPlayers() && lobbyTask == null)// saves performance
			WorldTasks.schedule(lobbyTask = new LobbyTimer(), Ticks.fromMinutes(1), Ticks.fromMinutes(1));
		player.getControllerManager().startController(new StealingCreationLobbyController());
		updateInterfaces();
		return true;
	}

	public static void removePlayer(Player player) {
		if (redTeam.contains(player))
			redTeam.remove(player);
		else if (blueTeam.contains(player))
			blueTeam.remove(player);
		if (!hasRequiredPlayers())
			removeTask();
		player.getInterfaceManager().removeOverlay(false);
		player.getControllerManager().removeControllerWithoutCheck();
		updateInterfaces();
	}

	public static void removeTask() {
		if (lobbyTask == null)
			return;
		lobbyTask.stop();
		lobbyTask = null;
	}

	public static void updateInterfaces() {
		for (Player player : redTeam)
			updateTeamInterface(player, true);
		for (Player player : blueTeam)
			updateTeamInterface(player, false);
	}

	public static void updateTeamInterface(Player player, boolean inRedTeam) {
		int skillTotal = getTotalLevel(TOTAL_SKILL_IDS, inRedTeam);
		int combatTotal = getTotalLevel(TOTAL_COMBAT_IDS, inRedTeam);
		int otherSkillTotal = getTotalLevel(TOTAL_SKILL_IDS, !inRedTeam);
		int otherCombatTotal = getTotalLevel(TOTAL_COMBAT_IDS, !inRedTeam);
		if (lobbyTask != null) {
			player.getPackets().setIFHidden(804, 2, true);
			player.getPackets().setIFText(804, 1, "Game Start : " + lobbyTask.getMinutes() + " mins");
		} else {
			player.getPackets().setIFHidden(804, 2, false);
			int players = 5 - (inRedTeam ? redTeam.size() : blueTeam.size());
			player.getPackets().setIFText(804, 34, String.valueOf(players < 0 ? 0 : players));
			players = 5 - (inRedTeam ? blueTeam.size() : redTeam.size());
			player.getPackets().setIFText(804, 33, String.valueOf(players < 0 ? 0 : players));
		}
		player.getPackets().setIFText(804, 4, "" + skillTotal);
		player.getPackets().setIFText(804, 5, "" + combatTotal);
		player.getPackets().setIFText(804, 6, "" + otherCombatTotal);
		player.getPackets().setIFText(804, 7, "" + otherSkillTotal);
	}

	private static boolean canEnter(Player player, boolean inRedTeam) {
		int skillTotal = getTotalLevel(TOTAL_SKILL_IDS, inRedTeam);
		int combatTotal = getTotalLevel(TOTAL_COMBAT_IDS, inRedTeam);
		int otherSkillTotal = getTotalLevel(TOTAL_SKILL_IDS, !inRedTeam);
		int otherCombatTotal = getTotalLevel(TOTAL_COMBAT_IDS, !inRedTeam);
		if ((skillTotal + combatTotal) > (otherSkillTotal + otherCombatTotal)) {
			player.sendMessage("This team is too strong for you to join at present.");
			return false;
		}
		if (player.getEquipment().wearingArmour() || player.getInventory().getFreeSlots() != 28 || player.getFamiliar() != null || player.getPet() != null) {
			player.sendMessage("You may not take any items into Stealing Creation. You can use the nearby bank deposit bank to empty your inventory and storn wore items.");
			return false;
			// } else if (player.getMoneyPouch().getCoinsAmount() != 0) {
			// player.sendMessage("The mystics sneer at your greed, as you
			// try to smuggle coins in.");
			// player.sendMessage("Deposite your money pouch's coins at the
			// local deposite box near you.");
			// return false;
		}
		if (player.getTempAttribs().getL("SC_PENALTY") >= System.currentTimeMillis()) {
			player.simpleDialogue("You have betrayed the mystics and must wait " + (int) (player.getTempAttribs().getL("SC_PENALTY") / 60000) + "minutes.");
			return false;
		}
		return true;
	}

	private static int getTotalLevel(int[] ids, boolean inRedTeam) {
		int skillTotal = 0;
		for (Player player : inRedTeam ? redTeam : blueTeam) {
			if (player == null)
				continue;
			for (int skillRequested : ids)
				skillTotal += player.getSkills().getLevel(skillRequested);
		}
		return skillTotal;
	}

	public static List<Player> getRedTeam() {
		return redTeam;
	}

	public static List<Player> getBlueTeam() {
		return blueTeam;
	}
}
