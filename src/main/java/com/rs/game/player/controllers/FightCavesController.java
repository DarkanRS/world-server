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
package com.rs.game.player.controllers;

import java.util.Set;

import com.rs.game.World;
import com.rs.game.npc.fightcaves.FightCavesNPC;
import com.rs.game.npc.fightcaves.TzKekCaves;
import com.rs.game.npc.fightcaves.TzTok_Jad;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.content.skills.summoning.Summoning;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class FightCavesController extends Controller {

	public static final WorldTile OUTSIDE = new WorldTile(4610, 5130, 0);

	private static final int THHAAR_MEJ_JAL = 2617;

	private static final int[] MUSICS = { 1088, 1082, 1086 };

	public void playMusic() {
		player.getMusicsManager().playMusic(selectedMusic);
	}

	private final int[][] WAVES = { { 2734 }, { 2734, 2734 }, { 2736 }, { 2736, 2734 }, { 2736, 2734, 2734 }, { 2736, 2736 }, { 2739 }, { 2739, 2734 }, { 2739, 2734, 2734 }, { 2739, 2736 }, { 2739, 2736, 2734 }, { 2739, 2736, 2734, 2734 }, { 2739, 2736, 2736 }, { 2739, 2739 }, { 2741 }, { 2741, 2734 }, { 2741, 2734, 2734 }, { 2741, 2736 }, { 2741, 2736, 2734 }, { 2741, 2736, 2734, 2734 }, { 2741, 2736, 2736 }, { 2741, 2739 }, { 2741, 2739, 2734 }, { 2741, 2739, 2734, 2734 },
			{ 2741, 2739, 2736 }, { 2741, 2739, 2736, 2734 }, { 2741, 2739, 2736, 2734, 2734 }, { 2741, 2739, 2736, 2736 }, { 2741, 2739, 2739 }, { 2741, 2741 }, { 2743 }, { 2743, 2734 }, { 2743, 2734, 2734 }, { 2743, 2736 }, { 2743, 2736, 2734 }, { 2743, 2736, 2734, 2734 }, { 2743, 2736, 2736 }, { 2743, 2739 }, { 2743, 2739, 2734 }, { 2743, 2739, 2734, 2734 }, { 2743, 2739, 2736 }, { 2743, 2739, 2736, 2734 }, { 2743, 2739, 2736, 2734, 2734 }, { 2743, 2739, 2736, 2736 }, { 2743, 2739, 2739 },
			{ 2743, 2741 }, { 2743, 2741, 2734 }, { 2743, 2741, 2734, 2734 }, { 2743, 2741, 2736 }, { 2743, 2741, 2736, 2734 }, { 2743, 2741, 2736, 2734, 2734 }, { 2743, 2741, 2736, 2736 }, { 2743, 2741, 2739 }, { 2743, 2741, 2739, 2734 }, { 2743, 2741, 2739, 2734, 2734 }, { 2743, 2741, 2739, 2736 }, { 2743, 2741, 2739, 2736, 2734 }, { 2743, 2741, 2739, 2736, 2734, 2734 }, { 2743, 2741, 2739, 2736, 2736 }, { 2743, 2741, 2739, 2739 }, { 2743, 2741, 2741 }, { 2743, 2743 }, { 2745 } };

	private transient DynamicRegionReference region;
	private transient WorldTile center;
	private transient Stages stage;
	public transient boolean spawned;
	private transient boolean logoutAtEnd;

	private int wave;
	private boolean login;
	public int selectedMusic;

	public FightCavesController(int wave) {
		this.wave = wave;
	}

	public static void enterFightCaves(Player player) {
		if (player.getFamiliar() != null || player.getPet() != null || Summoning.hasPouch(player) || Pets.hasPet(player)) {
			player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_JAL, "No Kimit-Zil in the pits! This is a fight for YOU, not your friends!");
			return;
		}
		player.getControllerManager().startController(new FightCavesController(1));
	}

	private static enum Stages {
		LOADING, RUNNING, DESTROYING
	}

	@Override
	public void start() {
		loadCave(false);
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		if (stage != Stages.RUNNING)
			return false;
		if (interfaceId == 182 && (componentId == 6 || componentId == 13)) {
			if (!logoutAtEnd) {
				logoutAtEnd = true;
				player.sendMessage("<col=ff0000>You will be logged out automatically at the end of this wave.");
				player.sendMessage("<col=ff0000>If you log out sooner, you will have to repeat this wave.");
			} else
				player.forceLogout();
			return false;
		}
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 9357) {
			if (stage != Stages.RUNNING)
				return false;
			exitCave(1);
			return false;
		}
		return true;
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean login() {
		loadCave(true);
		return false;
	}

	public void loadCave(final boolean login) {
		this.login = login;
		stage = Stages.LOADING;
		player.lock(); // locks player
		region = new DynamicRegionReference(8, 8);
		region.copyMapAllPlanes(552, 640, () -> {
			selectedMusic = MUSICS[Utils.random(MUSICS.length)];
			player.setNextWorldTile(!login ? getWorldTile(46, 61) : getWorldTile(32, 32));
			stage = Stages.RUNNING;
			WorldTasks.delay(1, () -> {
				if (!login) {
					WorldTile walkTo = getWorldTile(32, 32);
					player.addWalkSteps(walkTo.getX(), walkTo.getY());
				}
				center = new WorldTile(player);
				player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_JAL, "You're on your own now, JalYt.<br>Prepare to fight for your life!");
				player.setForceMultiArea(true);
				playMusic();
				player.unlock(); // unlocks player
			});
			if (!login)
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						if (stage != Stages.RUNNING)
							return;
						try {
							startWave();
						} catch (Throwable t) {
							Logger.handle(t);
						}
					}
				}, Ticks.fromSeconds(30));
		});
	}

	public WorldTile getSpawnTile() {
		switch (Utils.random(5)) {
		case 0:
			return getWorldTile(11, 16);
		case 1:
			return getWorldTile(51, 25);
		case 2:
			return getWorldTile(10, 50);
		case 3:
			return getWorldTile(46, 49);
		case 4:
		default:
			return getWorldTile(32, 30);
		}
	}

	@Override
	public void moved() {
		if (stage != Stages.RUNNING || !login)
			return;
		login = false;
		setWaveEvent();
	}

	public void startWave() {
		int currentWave = getCurrentWave();
		if (currentWave > WAVES.length) {
			win();
			return;
		}
		player.getInterfaceManager().setOverlay(316);
		player.getVars().setVar(639, currentWave);
		if (stage != Stages.RUNNING)
			return;
		for (int id : WAVES[currentWave - 1])
			if (id == 2736)
				new TzKekCaves(id, getSpawnTile());
			else if (id == 2745)
				new TzTok_Jad(id, getSpawnTile(), this);
			else
				new FightCavesNPC(id, getSpawnTile());
		spawned = true;
	}

	public void spawnHealers() {
		if (stage != Stages.RUNNING)
			return;
		for (int i = 0; i < 4; i++)
			new FightCavesNPC(2746, getSpawnTile());
	}

	public void win() {
		if (stage != Stages.RUNNING)
			return;
		exitCave(4);
	}

	public void nextWave() {
		playMusic();
        if(getCurrentWave() == 2)
            setCurrentWave(60);
        else
		    setCurrentWave(getCurrentWave() + 1);
		if (logoutAtEnd) {
			player.forceLogout();
			return;
		}
		setWaveEvent();
	}

	public void setWaveEvent() {
		if (getCurrentWave() == 63)
			player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_JAL, "Look out, here comes TzTok-Jad!");
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					if (stage != Stages.RUNNING)
						return;
					startWave();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1);
	}

	@Override
	public void process() {
		if (spawned) {
			Set<Integer> npcs = World.getRegion(center.getRegionId()).getNPCsIndexes();
			if (npcs == null || npcs.isEmpty()) {
				spawned = false;
				nextWave();
			}
		}
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasks.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					player.setNextAnimation(new Animation(836));
				else if (loop == 1)
					player.sendMessage("You have been defeated!");
				else if (loop == 3) {
					player.reset();
					exitCave(1);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		exitCave(2);
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.sendMessage("A mysterious force prevents you from teleporting.");
		return false;
	}

	/*
	 * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele
	 */
	public void exitCave(int type) {
		stage = Stages.DESTROYING;
		WorldTile outside = new WorldTile(OUTSIDE, 2); // radomizes alil
		if (type == 0 || type == 2)
			player.setLocation(outside);
		else {
			player.setForceMultiArea(false);
			player.getInterfaceManager().removeOverlay();
			if (type == 1 || type == 4) {
				player.setNextWorldTile(outside);
				if (type == 4) {
					player.incrementCount("Fight Caves clears");
					player.refreshFightKilnEntrance();
					player.reset();
					player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_JAL, "You even defeated Tz Tok-Jad, I am most impressed! Please accept this gift as a reward.");
					player.sendMessage("You were victorious!!");
					for (Player p : World.getPlayers()) {
						if (p == null || p.hasFinished())
							continue;
						p.sendMessage("<img=6><col=ff0000>" + player.getDisplayName() + " has just completed the fight caves!", true);
					}
					if (!player.getInventory().addItem(6570, 1)) {
						World.addGroundItem(new Item(6570, 1), new WorldTile(player), player, true, 180);
						World.addGroundItem(new Item(6529, 16064), new WorldTile(player), player, true, 180);
					} else if (!player.getInventory().addItem(6529, 16064))
						World.addGroundItem(new Item(6529, 16064), new WorldTile(player), player, true, 180);
				} else if (getCurrentWave() == 1)
					player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_JAL, "Well I suppose you tried... better luck next time.");
				else {
					int tokkul = getCurrentWave() * 8032 / WAVES.length;
					if (!player.getInventory().addItem(6529, tokkul))
						World.addGroundItem(new Item(6529, tokkul), new WorldTile(player), player, true, 180);
					player.getDialogueManager().execute(new SimpleNPCMessage(), THHAAR_MEJ_JAL, "Well done in the cave, here, take TokKul as reward.");
					// TODO tokens
				}
			}
			removeController();
		}
		region.destroy();
	}

	public WorldTile getWorldTile(int mapX, int mapY) {
		return region.getLocalTile(mapX, mapY);
	}

	@Override
	public boolean logout() {
		if (stage != Stages.RUNNING)
			return false;
		exitCave(0);
		return false;

	}

	public int getCurrentWave() {
		return wave;
	}

	public void setCurrentWave(int wave) {
		this.wave = wave;
	}

	@Override
	public void forceClose() {
		/*
		 * shouldnt happen
		 */
		if (stage != Stages.RUNNING)
			return;
		exitCave(2);
	}
}
