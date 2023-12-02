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
package com.rs.game.content.minigames.castlewars;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.skills.cooking.Foods.Food;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@PluginEventHandler
public final class CastleWars {
	public static final int CW_TICKET = 4067;
	public static final int SARADOMIN = 0;
	public static final int ZAMORAK = 1;
	private static final int GUTHIX = 2;
	@SuppressWarnings("unchecked")
	private static final List<Player>[] waiting = new List[2];
	@SuppressWarnings("unchecked")
	private static final List<Player>[] playing = new List[2];
	private static int[] seasonWins = new int[2];
	public static final Tile LOBBY = Tile.of(2442, 3090, 0), SARA_WAITING = Tile.of(2381, 9489, 0), ZAMO_WAITING = Tile.of(2421, 9523, 0), SARA_BASE = Tile.of(2426, 3076, 1), ZAMO_BASE = Tile.of(2373, 3131, 1);

	private static PlayingGame playingGame;

	static {
		init();
	}

	public static void init() {
		for (int i = 0; i < waiting.length; i++)
			waiting[i] = Collections.synchronizedList(new LinkedList<Player>());
		for (int i = 0; i < playing.length; i++)
			playing[i] = Collections.synchronizedList(new LinkedList<Player>());
	}

	public static void viewScoreBoard(Player player) {
		player.getInterfaceManager().sendChatBoxInterface(55);
		player.getPackets().setIFText(55, 1, "Saradomin: " + seasonWins[SARADOMIN]);
		player.getPackets().setIFText(55, 2, "Zamorak: " + seasonWins[ZAMORAK]);
	}

	public static int getPowerfullestTeam() {
		int zamorak = waiting[ZAMORAK].size() + playing[ZAMORAK].size();
		int saradomin = waiting[SARADOMIN].size() + playing[SARADOMIN].size();
		if (saradomin == zamorak)
			return GUTHIX;
		if (zamorak > saradomin)
			return ZAMORAK;
		return SARADOMIN;
	}

	public static void joinPortal(Player player, int team) {
		if (player.getEquipment().getHatId() != -1 || player.getEquipment().getCapeId() != -1) {
			player.sendMessage("You cannot wear hats, capes or helms in the arena.");
			return;
		}
		for (Item item : player.getInventory().getItems().array()) {
			if (item == null)
				continue;
			if (Food.forId(item.getId()) != null) {
				player.sendMessage("You cannot bring food into the arena.");
				return;
			}
		}
		int powerfullestTeam = getPowerfullestTeam();
		if (team == GUTHIX)
			team = powerfullestTeam == ZAMORAK ? SARADOMIN : ZAMORAK;
		else if (team == powerfullestTeam) {
			if (team == ZAMORAK)
				player.sendMessage("The Zamorak team is powerful enough already! Guthix demands balance - join the Saradomin team instead!");
			else if (team == SARADOMIN)
				player.sendMessage("The Saradomin team is powerful enough already! Guthix demands balance - join the Zamorak team instead!");
			return;
		}
		player.lock(2);
		waiting[team].add(player);
		setCape(player, new Item(team == ZAMORAK ? 4042 : 4041));
		setHood(player, new Item(team == ZAMORAK ? 4515 : 4513));
		player.getControllerManager().startController(new CastleWarsWaitingController(team));
		player.setNextTile(Tile.of(team == ZAMORAK ? ZAMO_WAITING : SARA_WAITING, 1));
		player.getMusicsManager().playSongAndUnlock(318); // 5 players to start a game
		if (playingGame == null && waiting[team].size() >= 5)
			createPlayingGame();
		else
			refreshTimeLeft(player);
		// You cannot take non-combat items into the arena
	}

	public static void setHood(Player player, Item hood) {
		player.getEquipment().setSlot(Equipment.HEAD, hood);
		player.getEquipment().refresh(Equipment.HEAD);
		player.getAppearance().generateAppearanceData();
	}

	public static void setCape(Player player, Item cape) {
		player.getEquipment().setSlot(Equipment.CAPE, cape);
		player.getEquipment().refresh(Equipment.CAPE);
		player.getAppearance().generateAppearanceData();
	}

	public static void setWeapon(Player player, Item weapon) {
		player.getEquipment().setSlot(Equipment.WEAPON, weapon);
		player.getEquipment().refresh(Equipment.WEAPON);
		player.getAppearance().generateAppearanceData();
	}

	public static void createPlayingGame() {
		playingGame = new PlayingGame();
		WorldTasks.schedule(playingGame, Ticks.fromMinutes(1), Ticks.fromMinutes(1));
		refreshAllPlayersTime();
	}

	public static void destroyPlayingGame() {
		playingGame.stop();
		playingGame = null;
		refreshAllPlayersTime();
		leavePlayersSafely();
	}

	public static void leavePlayersSafely() {
		leavePlayersSafely(-1);
	}

	public static void leavePlayersSafely(final int winner) {
		for (List<Player> element : playing)
			for (final Player player : element) {
				player.lock(7);
				player.stopAll();
			}
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				for (int i = 0; i < playing.length; i++)
					for (Player player : playing[i].toArray(new Player[playing[i].size()])) {
						forceRemovePlayingPlayer(player);
						if (winner != -1)
							if (winner == -2) {
								player.sendMessage("You draw.");
								player.getInventory().addItem(CW_TICKET, 1);
							} else if (winner == i) {
								player.sendMessage("You won.");
								player.getInventory().addItem(CW_TICKET, 2);
							} else
								player.sendMessage("You lost.");
					}
			}
		}, 6);
	}

	// unused
	public static void forceRemoveWaitingPlayer(Player player) {
		player.getControllerManager().forceStop();
	}

	public static void removeWaitingPlayer(Player player, int team) {
		waiting[team].remove(player);
		setCape(player, null);
		setHood(player, null);
		player.setNextTile(Tile.of(LOBBY, 2));
		if (playingGame != null && waiting[team].size() == 0 && playing[team].size() == 0)
			destroyPlayingGame(); // cancels if 0 players playing/waiting on any
		// of the tea
	}

	public static void refreshTimeLeft(Player player) {
		player.getVars().setVar(380, playingGame == null ? 0 : playingGame.minutesLeft - (player.getControllerManager().getController() instanceof CastleWarsPlayingController ? 5 : 0));
	}

	public static void startGame() {
		for (int i = 0; i < waiting.length; i++)
			for (Player player : waiting[i].toArray(new Player[waiting[i].size()]))
				joinPlayingGame(player, i);
	}

	public static void forceRemovePlayingPlayer(Player player) {
		player.getControllerManager().forceStop();
	}

	public static void removePlayingPlayer(Player player, int team) {
		playing[team].remove(player);
		player.reset();
		player.setCanPvp(false);
		// remove the items
		setCape(player, null);
		setHood(player, null);
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId == 4037 || weaponId == 4039) {
			CastleWars.setWeapon(player, null);
			CastleWars.dropFlag(player.getLastTile(), weaponId == 4037 ? CastleWars.SARADOMIN : CastleWars.ZAMORAK);
		}
		player.closeInterfaces();
		player.getInventory().deleteItem(4049, Integer.MAX_VALUE); // bandages
		player.getInventory().deleteItem(4053, Integer.MAX_VALUE); // barricades

		player.getHintIconsManager().removeUnsavedHintIcon();
		player.getMusicsManager().reset();
		player.setNextTile(Tile.of(LOBBY, 2));
		if (playingGame != null && waiting[team].size() == 0 && playing[team].size() == 0)
			destroyPlayingGame(); // cancels if 0 players playing/waiting on any
		// of the tea
	}

	public static void joinPlayingGame(Player player, int team) {
		playingGame.refresh(player);
		waiting[team].remove(player);
		player.getControllerManager().removeControllerWithoutCheck();
		player.getInterfaceManager().removeOverlay();
		playing[team].add(player);
		player.setCanPvp(true);
		player.getControllerManager().startController(new CastleWarsPlayingController(team));
		player.setNextTile(Tile.of(team == ZAMORAK ? ZAMO_BASE : SARA_BASE, 1));
	}

	public static void endGame(int winner) {
		if (winner != -2)
			seasonWins[winner]++;
		leavePlayersSafely(winner);
	}

	public static void refreshAllPlayersTime() {
		for (List<Player> element : waiting)
			for (Player player : element)
				refreshTimeLeft(player);
		for (int i = 0; i < playing.length; i++)
			for (Player player : playing[i]) {
				player.getMusicsManager().playSongAndUnlock(i == ZAMORAK ? 845 : 314);
				refreshTimeLeft(player);
			}
	}

	public static void refreshAllPlayersPlaying() {
		for (List<Player> element : playing)
			for (Player player : element)
				playingGame.refresh(player);
	}

	public static void addHintIcon(int team, Player target) {
		for (Player player : playing[team])
			player.getHintIconsManager().addHintIcon(target, 0, -1, false);
	}

	public static void removeHintIcon(int team) {
		for (Player player : playing[team])
			player.getHintIconsManager().removeUnsavedHintIcon();
	}

	public static void addScore(Player player, int team, int flagTeam) {
		if (playingGame == null)
			return;
		playingGame.addScore(player, team, flagTeam);
	}

	public static void takeFlag(Player player, int team, int flagTeam, GameObject object, boolean droped) {
		if (playingGame == null)
			return;
		playingGame.takeFlag(player, team, flagTeam, object, droped);
	}

	public static void dropFlag(Tile tile, int flagTeam) {
		if (playingGame == null)
			return;
		playingGame.dropFlag(tile, flagTeam);
	}

	public static void removeBarricade(int team, CastleWarBarricade npc) {
		if (playingGame == null)
			return;
		playingGame.removeBarricade(team, npc);
	}

	public static void addBarricade(int team, Player player) {
		if (playingGame == null)
			return;
		playingGame.addBarricade(team, player);
	}

	public static boolean isBarricadeAt(Tile tile) {
		if (playingGame == null)
			return false;
		return playingGame.isBarricadeAt(tile);
	}

	private static class PlayingGame extends Task {

		private static final int SAFE = 0, TAKEN = 1, DROPPED = 2;
		private int minutesLeft;
		private int[] score;
		private int[] flagStatus;
		private int[] barricadesCount;
		private final LinkedList<GameObject> spawnedObjects = new LinkedList<>();
		private final LinkedList<CastleWarBarricade> barricades = new LinkedList<>();

		public PlayingGame() {
			reset();
		}

		public void reset() {
			minutesLeft = 5; // temp testing else 5
			score = new int[2];
			flagStatus = new int[2];
			barricadesCount = new int[2];
			for (GameObject object : spawnedObjects)
				World.removeObject(object);
			spawnedObjects.clear();
			for (CastleWarBarricade npc : barricades)
				npc.finish();
			barricades.clear();
		}

		public boolean isBarricadeAt(Tile tile) {
			for (Iterator<CastleWarBarricade> it = barricades.iterator(); it.hasNext();) {
				CastleWarBarricade npc = it.next();
				if (npc.isDead() || npc.hasFinished()) {
					it.remove();
					continue;
				}
				if (npc.getX() == tile.getX() && npc.getY() == tile.getY() && tile.getPlane() == tile.getPlane())
					return true;
			}
			return false;
		}

		public void addBarricade(int team, Player player) {
			if (barricadesCount[team] >= 10) {
				player.sendMessage("Each team in the activity can have a maximum of 10 barricades set up.");
				return;
			}
			player.getInventory().deleteItem(new Item(4053, 1));
			barricadesCount[team]++;
			barricades.add(new CastleWarBarricade(team, Tile.of(player.getTile())));
		}

		public void removeBarricade(int team, CastleWarBarricade npc) {
			barricadesCount[team]--;
			barricades.remove(npc);
		}

		public void takeFlag(Player player, int team, int flagTeam, GameObject object, boolean droped) {
			if ((!droped && team == flagTeam) || (droped && flagStatus[flagTeam] != DROPPED))
				return;
			if (!droped && flagStatus[flagTeam] != SAFE)
				return;

			if (flagTeam != team && (player.getEquipment().getWeaponId() != -1 || player.getEquipment().getShieldId() != -1)) {
				// TODO no space message
				player.sendMessage("You can't take flag while wearing something in your hands.");
				return;
			}
			if (!droped) {
				GameObject flagStand = new GameObject(flagTeam == SARADOMIN ? 4377 : 4378, object.getType(), object.getRotation(), object.getX(), object.getY(), object.getPlane());
				spawnedObjects.add(flagStand);
				World.spawnObject(flagStand);
			} else {
				spawnedObjects.remove(object);
				World.removeObject(object);
				if (flagTeam == team) {
					makeSafe(flagTeam);
					return;
				}
			}
			addHintIcon(flagTeam, player);
			flagStatus[flagTeam] = TAKEN;
			setWeapon(player, new Item(flagTeam == SARADOMIN ? 4037 : 4039, 1));
			refreshAllPlayersPlaying();
		}

		public void addScore(Player player, int team, int flagTeam) {
			setWeapon(player, null);
			score[team] += 1;
			makeSafe(flagTeam);
		}

		private void makeSafe(int flagTeam) {
			GameObject flagStand = null;
			for (GameObject object : spawnedObjects)
				if (object.getId() == (flagTeam == SARADOMIN ? 4377 : 4378)) {
					flagStand = object;
					break;
				}
			if (flagStand == null)
				return;
			World.removeObject(flagStand);
			flagStatus[flagTeam] = SAFE;
			refreshAllPlayersPlaying();
		}

		public void dropFlag(Tile tile, int flagTeam) {
			removeHintIcon(flagTeam);
			GameObject flagDroped = new GameObject(flagTeam == SARADOMIN ? 4900 : 4901, ObjectType.SCENERY_INTERACT, 0, tile.getX(), tile.getY(), tile.getPlane());
			spawnedObjects.add(flagDroped);
			World.spawnObject(flagDroped);
			flagStatus[flagTeam] = DROPPED;
			refreshAllPlayersPlaying();
		}

		public void refresh(Player player) {
			player.getVars().setVarBit(143, flagStatus[SARADOMIN]);
			player.getVars().setVarBit(145, score[SARADOMIN]);
			player.getVars().setVarBit(153, flagStatus[ZAMORAK]);
			player.getVars().setVarBit(155, score[ZAMORAK]);
		}

		@Override
		public void run() {
			minutesLeft--;
			if (minutesLeft == 5) {
				endGame(score[SARADOMIN] == score[ZAMORAK] ? -2 : score[SARADOMIN] > score[ZAMORAK] ? SARADOMIN : ZAMORAK);
				reset();
			} else if (minutesLeft == 0) {
				minutesLeft = 25;
				startGame();
			} else if (minutesLeft > 6) // adds ppl waiting on lobby
				startGame();
			refreshAllPlayersTime();
		}
	}

	public static void handleInterfaces(Player player, int interfaceId, int componentId) {
		if (interfaceId == 55)
			if (componentId == 9)
				player.closeInterfaces();
	}

	public static ObjectClickHandler handleScoreboard = new ObjectClickHandler(new Object[] { 4484 }, e -> CastleWars.viewScoreBoard(e.getPlayer()));
	public static ObjectClickHandler handleJoinZamorak = new ObjectClickHandler(new Object[] { 4388 }, e -> joinPortal(e.getPlayer(), ZAMORAK));
	public static ObjectClickHandler handleJoinGuthix = new ObjectClickHandler(new Object[] { 4408 }, e -> joinPortal(e.getPlayer(), GUTHIX));
	public static ObjectClickHandler handleJoinSaradomin = new ObjectClickHandler(new Object[] { 4387 }, e -> joinPortal(e.getPlayer(), SARADOMIN));

	public static List<Player>[] getPlaying() {
		return playing;
	}
}
