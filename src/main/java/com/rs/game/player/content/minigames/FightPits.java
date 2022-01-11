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
package com.rs.game.player.content.minigames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.fightpits.FightPitsNPC;
import com.rs.game.npc.fightpits.TzKekPits;
import com.rs.game.player.Player;
import com.rs.game.player.actions.FightPitsViewingOrb;
import com.rs.game.player.controllers.FightPitsController;
import com.rs.game.player.controllers.FightPitsLobbyController;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public final class FightPits {

	private static final int THHAAR_MEJ_KAH = 2618;

	private static final List<Player> lobby = new ArrayList<>();
	public static final List<Player> arena = new ArrayList<>();
	public static final Object lock = new Object();
	private static GameTask gameTask;

	private static boolean startedGame;
	public static String currentChampion;

	private static WorldTile[] GAME_TELEPORTS = { new WorldTile(4577, 5086, 0), new WorldTile(4571, 5083, 0), new WorldTile(4564, 5086, 0), new WorldTile(4564, 5097, 0), new WorldTile(4571, 5101, 0), new WorldTile(4578, 5097, 0) };

	public static ButtonClickHandler handleFightPitsViewingOrbButtons = new ButtonClickHandler(374) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 5 && e.getComponentId() <= 9)
				e.getPlayer().setNextWorldTile(new WorldTile(FightPitsViewingOrb.ORB_TELEPORTS[e.getComponentId() - 5]));
			else if (e.getComponentId() == 15)
				e.getPlayer().stopAll();
		}
	};

	private static class GameTask extends WorldTask {

		private int minutes;
		private List<NPC> spawns;

		@Override
		public void run() {
			try {
				synchronized (lock) {
					if (!startedGame) {
						startedGame = true;
						passPlayersToArena();
					} else {
						if (minutes == 0)
							for (Player player : arena)
								player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_KAH, "FIGHT!");
						else if (minutes == 5) { // spawn tz-kih
							// spawns
							spawns = new ArrayList<>();
							for (int i = 0; i < 10; i++)
								spawns.add(new FightPitsNPC(2734, new WorldTile(GAME_TELEPORTS[Utils.random(GAME_TELEPORTS.length)], 3)));
						} else if (minutes == 6)
							for (int i = 0; i < 10; i++)
								spawns.add(new TzKekPits(2736, new WorldTile(GAME_TELEPORTS[Utils.random(GAME_TELEPORTS.length)], 3)));
						else if (minutes == 7)
							for (int i = 0; i < 10; i++)
								spawns.add(new FightPitsNPC(2739, new WorldTile(GAME_TELEPORTS[Utils.random(GAME_TELEPORTS.length)], 3)));
						else if (minutes == 10)
							// alot hits appears on players
							WorldTasks.schedule(new WorldTask() {

								@Override
								public void run() {
									if (!startedGame) {
										stop();
										return;
									}
									for (Player player : arena)
										player.applyHit(new Hit(player, 150, HitLook.TRUE_DAMAGE));
								}

							}, 0, 0);
						minutes++;
					}
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}

		public void removeNPCs() {
			if (spawns == null)
				return;
			for (NPC n : spawns)
				n.finish();
		}

	}

	/*
	 * because of the lvl 22s
	 */
	public static void addNPC(NPC n) {
		synchronized (lock) {
			if (gameTask == null || gameTask.spawns == null)
				return;
			gameTask.spawns.add(n);
		}
	}

	public static boolean canFight() {
		synchronized (lock) {
			if (gameTask == null)
				return false;
			return gameTask.minutes > 0;
		}
	}

	public static void passPlayersToArena() {
		for (Iterator<Player> it = lobby.iterator(); it.hasNext();) {
			Player player = it.next();
			player.stopAll();
			player.getControllerManager().removeControllerWithoutCheck();
			enterArena(player);
			it.remove();
		}
		refreshFoes();
	}

	public static void refreshFoes() {
		int foes = arena.size() - 1;
		for (Player player : arena)
			player.getVars().setVar(560, foes);

	}

	public static void enterArena(Player player) {
		player.lock(5);
		player.getControllerManager().startController(new FightPitsController());
		player.setNextWorldTile(new WorldTile(GAME_TELEPORTS[Utils.random(GAME_TELEPORTS.length)], 3));
		player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_KAH, "Please wait for the signal before fight.");
		player.setCanPvp(true);
		player.setCantTrade(true);
		arena.add(player);
	}

	/*
	 * 0 - logout, 1 - walk, 2 - dead, 3 - teled
	 */
	public static void leaveArena(Player player, int type) {
		synchronized (lock) {
			arena.remove(player);
			player.reset();
			player.getControllerManager().removeControllerWithoutCheck();
			if (type != 3)
				player.getControllerManager().startController(new FightPitsLobbyController());
			if (type == 0)
				player.setLocation(4592, 5073, 0);
			else {
				if (type != 3)
					lobby.add(player);
				player.setCanPvp(false);
				player.setCantTrade(false);
				player.getInterfaceManager().removeOverlay();
				if (player.hasSkull() && player.getSkullId() == 1) {// if has
					// champion
					// skull
					player.removeSkull();
					player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_KAH, "Well done in the pit, here take TokKul as reward.");
					int tokkul = (lobby.size() + arena.size()) * 100;
					if (!player.getInventory().addItem(6529, tokkul) && type == 1)
						World.addGroundItem(new Item(6529, tokkul), new WorldTile(4585, 5076, 0), player, true, 180);
				}
				if (type == 1) {
					player.lock(5);
					player.addWalkSteps(4585, 5076, 5, false);
				} else if (type == 2)
					player.setNextWorldTile(new WorldTile(new WorldTile(4592, 5073, 0), 2));
			}
			refreshFoes();
			checkPlayersAmmount();
			if (startedGame && arena.size() <= 1)
				endGame();
		}
	}

	public static void enterLobby(Player player, boolean login) {
		synchronized (lock) {
			if (!login) {
				player.lock(5);
				player.addWalkSteps(4595, 5066, 5, false);
				player.getControllerManager().startController(new FightPitsLobbyController());
			}
			lobby.add(player);
			checkPlayersAmmount();
		}
	}

	/*
	 * 0 - logout, 1 normal, 2 death/tele
	 */
	public static void leaveLobby(Player player, int type) {
		synchronized (lock) {
			if (type != 0) {
				if (type == 1) {
					player.lock(5);
					player.addWalkSteps(4597, 5064, 5, false);
				}
				player.getControllerManager().removeControllerWithoutCheck();
			}
			lobby.remove(player);
			checkPlayersAmmount();
		}
	}

	public static void checkPlayersAmmount() {
		if (gameTask == null) {
			if (lobby.size() + arena.size() >= 2) // 2players in
				startGame(false);
		} else if (lobby.size() + arena.size() < 2)
			cancelGame();
	}

	public static void startGame(boolean end) {
		if (end) {
			gameTask.stop();
			gameTask.removeNPCs();
			setChampion();
			startedGame = false;
		}
		gameTask = new GameTask();
		WorldTasks.schedule(gameTask, end ? Ticks.fromSeconds(60) : Ticks.fromSeconds(10), Ticks.fromSeconds(60));

	}

	public static void cancelGame() {
		gameTask.stop();
		gameTask.removeNPCs();
		gameTask = null;
		if (startedGame)
			setChampion();
		startedGame = false;
	}

	public static void setChampion() {
		if (arena.isEmpty())
			return;
		Player champion = arena.get(0);
		currentChampion = champion.getDisplayName();
		champion.getPackets().setIFText(373, 10, "Current Champion: JaLYt-Ket-" + currentChampion);
		champion.setFightPitsSkull();
		champion.incrementCount("Fight Pits victories");
		champion.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_KAH, "Well done, you were the last person in the pit and won that fight! The next round will start soon, wait for my signal before fighting.");
	}

	public static void endGame() {
		startGame(true);
	}
}
