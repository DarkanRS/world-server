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

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.fightkiln.FightKilnNPC;
import com.rs.game.npc.fightkiln.HarAken;
import com.rs.game.npc.fightkiln.TokHaarKetDill;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.transportation.FadingScreen;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.game.player.dialogues.FightKilnDialogue;
import com.rs.game.player.dialogues.TokHaarHok;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

/**
 * T70+
 * Copy this: https://www.youtube.com/watch?v=gY0CwX27Css&t=1340s&ab_channel=Smokey9112Films
 */
public class FightKilnController extends Controller {

	public static final WorldTile OUTSIDE = new WorldTile(4744, 5172, 0);

	private static final int TOKHAAR_HOK = 15195, TOKHAAR_HOK_SCENE = 15200;

	private static final int[] MUSICS = { 1088, 1082, 1086 };

	public void playMusic() {
		player.getMusicsManager().playMusic(selectedMusic);
	}

	/*
	 * 0 - south east 1 - south west 2 - north west 3 - north east TokHaar-Hur -
	 * 15201 TokHaar-Xil - 15202 TokHaar-Mej - 15203 TokHaar-Ket - 15204
	 * TokHaar-Tok-Xil - 15205 TokHaar-Yt-Mejkot - 15206 TokHaar-Ket-Zek - 15207
	 * TokHaar-Jad - 15208 TokHaar-Ket-Dill - 15213
	 */
	private int Hur = 15201, Xil = 15202, Mej = 15203,
			Ket = 15204, Tok_Xil = 15205, Yt_Mejkot = 15206,
			Ket_Zek = 15207, Jad = 15208, Ket_Dill = 15213;


	private final int[][] WAVES = { { Xil, Xil, Tok_Xil, Hur, Hur }, // 1
			{ Xil, Xil, Tok_Xil, Tok_Xil, Hur }, // 2
			{ Xil, Tok_Xil, Tok_Xil, Tok_Xil, Hur }, // 3
			{ Tok_Xil, Tok_Xil, Tok_Xil, Mej, Mej }, // 4
			{ Xil, Tok_Xil, Tok_Xil, Tok_Xil, Ket_Dill }, // 5
			{ Xil, Tok_Xil, Mej, Mej, Tok_Xil, Tok_Xil }, // 6
			{ Mej, Tok_Xil, Tok_Xil, Tok_Xil, Xil, Tok_Xil }, // 7
			{ Ket_Zek, Tok_Xil, Tok_Xil }, // 8
			{ Tok_Xil, Tok_Xil, Tok_Xil, Tok_Xil, Tok_Xil, Tok_Xil }, // 9
			{ Tok_Xil, Jad }, // 10
			{ Mej, Mej, Mej, Mej }, // 11
			{ Mej, Tok_Xil, Tok_Xil, Mej }, // 12
			{ Mej, Ket_Zek, Mej }, // 13
			{ Ket_Zek, Ket_Zek, Mej, Mej }, // 14
			{ Ket_Zek, Ket_Zek, Tok_Xil }, // 15
			{ Ket_Zek, Ket_Zek, Tok_Xil, Mej, Mej }, // 16
			{ Ket_Zek, Ket_Zek, Tok_Xil, Mej, Yt_Mejkot }, // 17
			{ Ket_Zek, Ket_Zek, Tok_Xil, Yt_Mejkot, Tok_Xil, Tok_Xil }, // 18
			{ Mej, Mej, Mej, Mej, Mej, Mej, Mej, Mej, Ket_Dill}, // 19
			{ Ket_Zek, Jad }, // 20
			{ Hur, Hur, Hur, Hur, Hur, Hur, Hur, Hur, Hur, Hur, Hur, Hur }, // 21
			{ Yt_Mejkot, Hur, Ket, Ket, Hur }, // 22
			{ Yt_Mejkot, Yt_Mejkot, Ket, Hur, Hur }, // 23
			{ Yt_Mejkot, Yt_Mejkot, Yt_Mejkot, Tok_Xil, Yt_Mejkot }, // 24
			{ Yt_Mejkot, Yt_Mejkot, Tok_Xil, Tok_Xil, Ket_Zek }, // 25
			{ Yt_Mejkot, Yt_Mejkot, Tok_Xil, Ket_Zek, Ket_Zek }, // 26
			{ Ket_Zek, Ket, Yt_Mejkot, Tok_Xil, Ket, Yt_Mejkot }, // 27
			{ Ket_Dill, Ket_Dill, Ket_Zek, Ket_Dill, Ket_Dill, Ket_Dill, Ket_Dill }, // 28
			{ Yt_Mejkot, Yt_Mejkot, Yt_Mejkot, Yt_Mejkot, Yt_Mejkot, Yt_Mejkot }, // 29
			{ Yt_Mejkot, Jad, Yt_Mejkot, Yt_Mejkot }, // 30
			{ Tok_Xil, Tok_Xil, Tok_Xil, Tok_Xil }, // 31
			{ Yt_Mejkot, Yt_Mejkot, Yt_Mejkot, Yt_Mejkot }, // 32
			{ Ket_Zek, Ket_Zek, Ket_Zek, Ket_Zek }, // 33
			{ Tok_Xil, Jad, Yt_Mejkot }, // 34
			{ Ket_Zek, Yt_Mejkot, Tok_Xil, Jad }, // 35
			{ Jad, Jad } // 36
	};

	private transient DynamicRegionReference region;
	private transient Stages stage;
	private transient NPC tokHaarHok;
	private transient HarAken harAken;
	private transient int aliveNPCSCount;

	private boolean logoutAtEnd;
	private boolean login;
	public int selectedMusic;
	private int wave;

	public FightKilnController(int wave) {
		this.wave = wave;
	}

	public static void enterFightKiln(Player player, boolean quickEnter) {
		if (player.getCounterValue("Fight Caves clears") <= 0) {
			player.sendMessage("You need to have cleared the Fight Caves to enter the Kiln.");
			return;
		}
		if (player.getInventory().containsOneItem(23653, 23654, 23655, 23656, 23657, 23658))
			return;
		Familiar familiar = player.getFamiliar();
		if (familiar != null && ((familiar.getBob() != null && familiar.getBob().containsOneItem(23653, 23654, 23655, 23656, 23657, 23658)) || familiar.isFinished()))
			return;
		if (!quickEnter)
			player.getDialogueManager().execute(new FightKilnDialogue());
		else
			player.getControllerManager().startController(new FightKilnController(1));
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
		if (object.getId() == 68111) {
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
		logoutAtEnd = false;
		loadCave(true);
		return false;
	}

	public int[] getMap() {
		int wave = getCurrentWave();
		if (wave < 11)
			return new int[] { 504, 632 };
		if (wave < 21)
			return new int[] { 512, 632 };
		if (wave < 31)
			return new int[] { 520, 632 };
		if (wave < 34)
			return new int[] { 528, 632 };
		return new int[] { 536, 632 };
	}

	public void loadCave(final boolean login) {
		stage = Stages.LOADING;
		player.lock(); // locks player
		int currentWave = getCurrentWave();
		Runnable event = () -> {
			// selects a music
			selectedMusic = MUSICS[Utils.random(MUSICS.length)];
			playMusic();
			player.setForceMultiArea(true);
			player.stopAll();
			if (currentWave == 0) { // SCENE 0
				player.getInventory().removeItems(new Item(23653, Integer.MAX_VALUE), new Item(23654, Integer.MAX_VALUE), new Item(23655, Integer.MAX_VALUE), new Item(23656, Integer.MAX_VALUE), new Item(23657, Integer.MAX_VALUE), new Item(23658, Integer.MAX_VALUE));
				player.setNextWorldTile(getWorldTile(31, 51));
				tokHaarHok = new NPC(TOKHAAR_HOK, getWorldTile(30, 36), true);
				tokHaarHok.setFaceAngle(Utils.getAngleTo(0, 1));
				// 1delay because player cant walk while teleing :p,
				// + possible issues avoid
				WorldTasks.schedule(new WorldTask() {
					int count = 0;
					boolean run;

					@Override
					public void run() {
						if (count == 0) {
							WorldTile lookTo = getWorldTile(29, 39);
							player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 3500);
							WorldTile posTile = getWorldTile(27, 30);
							player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 3500);
							run = player.getRun();
							player.setRun(false);
							WorldTile walkTo = getWorldTile(31, 39);
							player.addWalkSteps(walkTo.getX(), walkTo.getY(), -1, false);
						} else if (count == 1)
							player.getPackets().sendResetCamera();
						else if (count == 2) {
							player.getDialogueManager().execute(new TokHaarHok(), 0, TOKHAAR_HOK, FightKilnController.this);
							player.setRun(run);
							stage = Stages.RUNNING;
							player.unlock(); // unlocks player
							stop();
						}
						count++;
					}

				}, 1, 6);
			} else if (currentWave == 38) { // SCENE 7, WIN
				player.setNextWorldTile(getWorldTile(38, 25));
				player.setNextFaceWorldTile(getWorldTile(38, 26));
				tokHaarHok = new NPC(TOKHAAR_HOK_SCENE, getWorldTile(37, 30), true);
				tokHaarHok.setFaceAngle(Utils.getAngleTo(0, -1));
				player.getPackets().setBlockMinimapState(2);
				player.getVars().setVar(1241, 1);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						WorldTile lookTo = getWorldTile(40, 28);
						player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 2200);
						WorldTile posTile = getWorldTile(29, 28);
						player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 2500);
						HarAken harAken = new HarAken(15211, getWorldTile(45, 26), FightKilnController.this);
						harAken.spawn();
						harAken.sendDeath(player);
						WorldTasks.schedule(new WorldTask() {
							@Override
							public void run() {
								try {
									player.getDialogueManager().execute(new TokHaarHok(), 6, TOKHAAR_HOK_SCENE, FightKilnController.this);
								} catch (Throwable e) {
									Logger.handle(e);
								}
							}
						}, Ticks.fromSeconds(5));
					}
				}, 1);
			} else if (currentWave == 37) { // SCENE 6
				player.setNextWorldTile(getWorldTile(38, 25));
				player.setNextFaceWorldTile(getWorldTile(38, 26));
				tokHaarHok = new NPC(TOKHAAR_HOK_SCENE, getWorldTile(37, 30), true);
				tokHaarHok.setFaceAngle(Utils.getAngleTo(0, -1));
				player.getPackets().setBlockMinimapState(2);
				player.getVars().setVar(1241, 1);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						WorldTile lookTo = getWorldTile(40, 28);
						player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 2200);
						WorldTile posTile = getWorldTile(29, 28);
						player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 2500);
						player.getDialogueManager().execute(new TokHaarHok(), 5, TOKHAAR_HOK_SCENE, FightKilnController.this);
					}

				}, 1);
			} else if (currentWave == 34) { // SCENE 5
				teleportPlayerToMiddle();
				player.getPackets().setBlockMinimapState(2);
				player.getVars().setVar(1241, 1);
				WorldTasks.schedule(new WorldTask() {

					int count = 0;

					@Override
					public void run() {
						if (count == 0) {
							WorldTile lookTo = getWorldTile(32, 41);
							player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 1000);
							WorldTile posTile = getWorldTile(32, 38);
							player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 1200);
						} else if (count == 6) {
							WorldTile lookTo = getWorldTile(64, 30);
							player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 3000);
							WorldTile posTile = getWorldTile(42, 36);
							player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 3000);
							player.setNextWorldTile(getWorldTile(33, 39));
							player.setNextFaceWorldTile(getWorldTile(32, 39));
						} else if (count == 12) {
							stop();
							tokHaarHok = new NPC(TOKHAAR_HOK_SCENE, getWorldTile(28, 38), true);
							tokHaarHok.setFaceAngle(Utils.getAngleTo(1, 0));

							WorldTile lookTo = getWorldTile(30, 38);
							player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 2500);
							WorldTile posTile = getWorldTile(30, 30);
							player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 3000);
							player.getDialogueManager().execute(new TokHaarHok(), 7, TOKHAAR_HOK_SCENE, FightKilnController.this);
						}
						count++;

					}

				}, 1, 0);
			} else if (currentWave == 31) { // SCENE 4
				player.setNextWorldTile(getWorldTile(21, 21));
				player.setNextFaceWorldTile(getWorldTile(20, 20));
				player.getPackets().setBlockMinimapState(2);
				player.getVars().setVar(1241, 1);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						WorldTile lookTo = getWorldTile(20, 17);
						player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 2500);
						WorldTile posTile = getWorldTile(25, 26);
						player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 3000);
						player.getDialogueManager().execute(new TokHaarHok(), 4, TOKHAAR_HOK_SCENE, FightKilnController.this);
					}

				}, 1);
			} else if (currentWave == 21) { // SCENE 3
				tokHaarHok = new NPC(TOKHAAR_HOK_SCENE, getWorldTile(30, 43), true);
				tokHaarHok.setFaceAngle(Utils.getAngleTo(0, -1));
				teleportPlayerToMiddle();
				player.getPackets().setBlockMinimapState(2);
				player.getVars().setVar(1241, 1);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						WorldTile lookTo = getWorldTile(31, 43);
						player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 2500);
						WorldTile posTile = getWorldTile(31, 34);
						player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 4000);
						player.getDialogueManager().execute(new TokHaarHok(), 3, TOKHAAR_HOK_SCENE, FightKilnController.this);
					}

				}, 1);
			} else if (currentWave == 11) { // SCENE 2
				tokHaarHok = new NPC(TOKHAAR_HOK_SCENE, getWorldTile(45, 45), true);
				tokHaarHok.setFaceAngle(Utils.getAngleTo(-1, -1));
				teleportPlayerToMiddle();
				player.getPackets().setBlockMinimapState(2);
				player.getVars().setVar(1241, 1);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						WorldTile lookTo = getWorldTile(45, 45);
						player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 1000);
						WorldTile posTile = getWorldTile(38, 37);
						player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 3000);
						player.getDialogueManager().execute(new TokHaarHok(), 2, TOKHAAR_HOK_SCENE, FightKilnController.this);
					}

				}, 1);
			} else if (currentWave == 1) { // SCENE 1
				player.getInventory().removeItems(new Item(23653, Integer.MAX_VALUE), new Item(23654, Integer.MAX_VALUE), new Item(23655, Integer.MAX_VALUE), new Item(23656, Integer.MAX_VALUE), new Item(23657, Integer.MAX_VALUE), new Item(23658, Integer.MAX_VALUE));
				tokHaarHok = new NPC(TOKHAAR_HOK_SCENE, getWorldTile(30, 36), true);
				tokHaarHok.setFaceAngle(Utils.getAngleTo(0, 1));
				player.setNextWorldTile(getWorldTile(31, 39));
				player.getPackets().setBlockMinimapState(2);
				player.getVars().setVar(1241, 1);
				player.setNextFaceWorldTile(tokHaarHok);
				WorldTasks.schedule(new WorldTask() {

					@Override
					public void run() {
						WorldTile lookTo = getWorldTile(31, 40);
						player.getPackets().sendCameraLook(Cutscene.getX(player, lookTo.getX()), Cutscene.getY(player, lookTo.getY()), 1000);
						WorldTile posTile = getWorldTile(31, 50);
						player.getPackets().sendCameraPos(Cutscene.getX(player, posTile.getX()), Cutscene.getY(player, posTile.getY()), 3000);
						player.getDialogueManager().execute(new TokHaarHok(), 1, TOKHAAR_HOK_SCENE, FightKilnController.this);
						stage = Stages.RUNNING;
						player.unlock();
					}

				}, 1);
			} else if (login) { // LOGIN during
				FightKilnController.this.login = login;
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						stage = Stages.RUNNING;
						teleportPlayerToMiddle();
						player.unlock();
					}
				}, 1);
			}
		};
		// finds empty map bounds
		if (region == null) {
			region = new DynamicRegionReference(8, 8);
			region.copyMapAllPlanes(getMap()[0], getMap()[1], () -> {
				event.run();
				player.setForceNextMapLoadRefresh(true);
				player.loadMapRegions();
			});
		} else if (!login && (currentWave == 11 || currentWave == 21 || currentWave == 31 || currentWave == 34))
			region.copyMapAllPlanes(getMap()[0], getMap()[1], () -> {
				player.setForceNextMapLoadRefresh(true);
				player.loadMapRegions();
				event.run();
			});
		else
			player.fadeScreen(event);
	}

	public WorldTile getMaxTile() {
		if (getCurrentWave() < 11)
			return getWorldTile(49, 49);
		if (getCurrentWave() < 21)
			return getWorldTile(47, 47);
		if (getCurrentWave() < 31)
			return getWorldTile(45, 45);
		if (getCurrentWave() < 34)
			return getWorldTile(43, 43);
		return getWorldTile(41, 41);
	}

	public WorldTile getMinTile() {
		if (getCurrentWave() < 11)
			return getWorldTile(14, 14);
		if (getCurrentWave() < 21)
			return getWorldTile(16, 16);
		if (getCurrentWave() < 31)
			return getWorldTile(18, 18);
		if (getCurrentWave() < 34)
			return getWorldTile(20, 20);
		return getWorldTile(22, 22);
	}

	/*
	 * 20, 20 min X, min Y 42 42 maxX, maxY
	 */
	/*
	 * 0 - north 1 - south 2 - east 3 - west
	 */
	public WorldTile getTentacleTile() {
		int corner = Utils.random(4);
		int position = Utils.random(5);
		while (corner != 0 && position == 2)
			position = Utils.random(5);
		switch (corner) {
		case 0: // north
			return getWorldTile(21 + (position * 5), 42);
		case 1: // south
			return getWorldTile(21 + (position * 5), 20);
		case 2: // east
			return getWorldTile(42, 21 + (position * 5));
		case 3: // west
		default:
			return getWorldTile(20, 21 + (position * 5));
		}
	}

	public WorldTile getSpawnTile(int count, int size) {
		int position = count % 4;
		switch (position) {
		case 0: // east south
			WorldTile maxTile = getMaxTile();
			WorldTile minTile = getMinTile();
			return new WorldTile(maxTile.getX() - 1 - size, minTile.getY() + 2, 1);
		case 1: // west south
			return getMinTile().transform(2, 2, 0);
		case 2: // west north
			maxTile = getMaxTile();
			minTile = getMinTile();
			return new WorldTile(minTile.getX() + 2, maxTile.getY() - 1 - size, 1);
		case 3: // east north
		default:
			return getMaxTile().transform(-1 - size, -1 - size, 0);
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
		if (stage != Stages.RUNNING)
			return;
		int currentWave = getCurrentWave();
		player.getInterfaceManager().setOverlay(316);
		player.getVars().setVar(639, currentWave);
		if (currentWave > WAVES.length) {
			if (currentWave == 37)
				aliveNPCSCount = 1;
			return;
		}
		if (currentWave == 0) {
			exitCave(1);
			return;
		}
		aliveNPCSCount = WAVES[currentWave - 1].length;
		for (int i = 0; i < WAVES[currentWave - 1].length; i += 4) {
			final int next = i;
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					try {
						if (stage != Stages.RUNNING)
							return;
						spawn(next);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, (next / 4) * Ticks.fromSeconds(4));
		}
	}

	public void spawn(int index) {
		int currentWave = getCurrentWave();
		for (int i = index; i < (index + 4 > WAVES[currentWave - 1].length ? WAVES[currentWave - 1].length : index + 4); i++) {
			int npcId = WAVES[currentWave - 1][i];
			if (npcId == 15213)
				new TokHaarKetDill(WAVES[currentWave - 1][i], getSpawnTile(i, NPCDefinitions.getDefs(npcId).size), this);
			else
				new FightKilnNPC(WAVES[currentWave - 1][i], getSpawnTile(i, NPCDefinitions.getDefs(npcId).size), this);

		}
	}

	private int[] getLavaCrystal() {
		switch (getCurrentWave()) {
		case 1:
		case 13:
		case 25:
			return new int[] { 23653 };
		case 3:
		case 15:
		case 27:
			return new int[] { 23654 };
		case 5:
		case 18:
		case 29:
			return new int[] { 23655 };
		case 7:
		case 19:
		case 31:
			return new int[] { 23656 };
		case 9:
		case 21:
			return new int[] { 23657 };
		case 11:
		case 23:
			return new int[] { 23658 };
		case 35:
			return new int[] { 23657, 23658 };
		default:
			return null;
		}
	}

	public void checkCrystal() {
		if (stage != Stages.RUNNING)
			return;
		if (aliveNPCSCount == 1) {
			int[] crystals = getLavaCrystal();
			if (crystals != null)
				for (int crystal : crystals)
					World.addGroundItem(new Item(crystal), getWorldTile(32, 32));
		}
	}

	public void removeNPC() {
		if (stage != Stages.RUNNING)
			return;
		aliveNPCSCount--;
		if (aliveNPCSCount == 0)
			nextWave();
	}

	public void win() {
		if (stage != Stages.RUNNING)
			return;
		exitCave(4);
	}

	public void unlockPlayer() {
		stage = Stages.RUNNING;
		player.unlock(); // unlocks player
	}

	public void removeScene() {
		FadingScreen.fade(player, () -> {
			if (stage != Stages.RUNNING)
				unlockPlayer();
			removeTokHaarTok();
			player.getPackets().sendResetCamera();
			player.getPackets().setBlockMinimapState(0);
			player.getVars().setVar(1241, 0);
			if (getCurrentWave() == 38) {
				int reward = player.getTempAttribs().getI("FightKilnReward");
				if (reward != -1) {
					win();
					for (Player p : World.getPlayers()) {
						if (p == null || p.hasFinished())
							continue;
						p.sendMessage("<img=6><col=ff0000>" + player.getDisplayName() + " has just completed the fight kiln!", true);
					}
				}
			} else {
				teleportPlayerToMiddle();
				setWaveEvent();
			}
		});
	}

	public void teleportPlayerToMiddle() {
		player.setNextWorldTile(getWorldTile(31, 32));
	}

	public void removeTokHaarTok() {
		if (tokHaarHok != null)
			tokHaarHok.finish();
	}

	public void nextWave() {
		if (stage != Stages.RUNNING)
			return;
		playMusic();
		int nextWave = getCurrentWave() + 1;
		setCurrentWave(nextWave);
		if (logoutAtEnd) {
			player.forceLogout();
			return;
		}
		if (nextWave == 1 || nextWave == 11 || nextWave == 21 || nextWave == 31 || nextWave == 34 || nextWave == 37 || nextWave == 38) {
			harAken = null;
			player.stopAll();
			loadCave(false);
			return;
		}
		setWaveEvent();
	}

	public void setWaveEvent() {
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
		}, Ticks.fromSeconds(6));
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

	/*
	 * logout or not. if didnt logout means lost, 0 logout, 1, normal, 2 tele
	 */
	public void exitCave(int type) {
		stage = Stages.DESTROYING;
		WorldTile outside = new WorldTile(OUTSIDE, 2); // radomizes alil
		if (type == 0) {
			player.setLocation(outside);
			if (getCurrentWave() == 0) // leaves if didnt start
				removeController();
		} else if (type == 2) {
			player.setLocation(outside);
			removeController();
		} else {
			player.setForceMultiArea(false);
			player.getInterfaceManager().removeOverlay();
			if (type == 1 || type == 4) {
				player.setNextWorldTile(outside);
				if (type == 4) {
					player.incrementCount("Fight Kiln clears");
					player.sendMessage("You were victorious!!");
					int reward = player.getTempAttribs().getI("FightKilnReward");
					int itemId = reward != -1 && reward == 1 ? 6571 : 23659;
					if (!player.getInventory().addItem(itemId, 1))
						World.addGroundItem(new Item(itemId, 1), new WorldTile(player), player, true, 180);
					player.reset();
				}
			}
			player.getInventory().removeItems(new Item(23653, Integer.MAX_VALUE), new Item(23654, Integer.MAX_VALUE), new Item(23655, Integer.MAX_VALUE), new Item(23656, Integer.MAX_VALUE), new Item(23657, Integer.MAX_VALUE), new Item(23658, Integer.MAX_VALUE));
			removeCrystalEffects();
			removeController();
		}
		region.destroy();
	}

	private void removeCrystalEffects() {
		player.setInvulnerable(false);
		player.getSkills().restoreSkills();
		player.setHpBoostMultiplier(0);
		player.getEquipment().refreshConfigs(false);
		player.getTempAttribs().removeB("FightKilnCrystal");
	}

	/*
	 * gets worldtile inside the map
	 */
	public WorldTile getWorldTile(int mapX, int mapY) {
		return region.getLocalTile(mapX, mapY).transform(0, 0, 1);
	}

	/*
	 * return false so wont remove script
	 */
	@Override
	public boolean logout() {
		/*
		 * only can happen if dungeon is loading and system update happens
		 */
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

	public void showHarAken() {
		if (harAken == null) {
			harAken = new HarAken(15211, getWorldTile(45, 26), this);
			harAken.setFaceAngle(Utils.getAngleTo(-1, -1));
		} else {
			if (stage != Stages.RUNNING)
				return;
			switch (Utils.random(3)) {
			case 0:
				harAken.setLocation(getWorldTile(29, 17));
				harAken.setFaceAngle(Utils.getAngleTo(0, 1));
				break;
			case 1:
				harAken.setLocation(getWorldTile(17, 30));
				harAken.setFaceAngle(Utils.getAngleTo(1, 0));
				break;
			case 2:
				harAken.setLocation(getWorldTile(42, 30));
				harAken.setFaceAngle(Utils.getAngleTo(-1, 0));
				break;
			}
			harAken.spawn();
			// TODO set worldtile
		}
		harAken.setCantInteract(false);
		harAken.setNextAnimation(new Animation(16232));
	}

	public static void useCrystal(final Player player, int id) {
		if (!(player.getControllerManager().getController() instanceof FightKilnController) || player.getTempAttribs().getB("FightKilnCrystal"))
			return;
		player.getInventory().deleteItem(new Item(id, 1));
		switch (id) {
		case 23653: // invulnerability
			player.sendMessage("<col=7E2217>>The power of this crystal makes you invulnerable.");
			player.getTempAttribs().setB("FightKilnCrystal", true);
			player.setInvulnerable(true);
			CoresManager.schedule(() -> {
				try {
					player.getTempAttribs().removeB("FightKilnCrystal");
					player.sendMessage("<col=7E2217>The power of the crystal dwindles and you're vulnerable once more.");
					player.setInvulnerable(false);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}, Ticks.fromSeconds(15));
			break;
		case 23654: // RESTORATION
			player.heal(player.getMaxHitpoints());
			player.getPrayer().restorePrayer(player.getSkills().getLevelForXp(Constants.PRAYER) * 10);
			player.sendMessage("<col=7E2217>The power of this crystal heals you fully.");
			break;
		case 23655: // MAGIC
			boostCrystal(player, Constants.MAGIC);
			break;
		case 23656: // RANGED
			boostCrystal(player, Constants.RANGE);
			break;
		case 23657: // STRENGTH
			boostCrystal(player, Constants.STRENGTH);
			break;
		case 23658: // CONSTITUTION
			player.getTempAttribs().setB("FightKilnCrystal", true);
			player.setHpBoostMultiplier(0.5);
			player.getEquipment().refreshConfigs(false);
			player.heal(player.getSkills().getLevelForXp(Constants.HITPOINTS) * 5);
			player.sendMessage("<col=7E2217>The power of this crystal improves your Constitution.");
			CoresManager.schedule(() -> {
				try {
					player.getTempAttribs().removeB("FightKilnCrystal");
					player.sendMessage("<col=7E2217>The power of the crystal dwindles and your constitution prowess returns to normal.");
					player.setHpBoostMultiplier(0);
					player.getEquipment().refreshConfigs(false);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}, Ticks.fromSeconds(210));
			break;
		}
	}

	private static void boostCrystal(final Player player, final int skill) {
		player.getTempAttribs().setB("FightKilnCrystal", true);
		if (skill == Constants.RANGE)
			player.sendMessage("<col=7E2217>The power of the crystal improves your Ranged prowess, at the expense of your Defence, Strength and Magical ability.");
		else if (skill == Constants.MAGIC)
			player.sendMessage("<col=7E2217>The power of the crystal improves your Magic prowess, at the expense of your Defence, Strength and Ranged ability.");
		else if (skill == Constants.STRENGTH)
			player.sendMessage("<col=7E2217>The power of the crystal improves your Strength prowess, at the expense of your Defence, Ranged and Magical ability.");
		WorldTasks.schedule(new WorldTask() {

			private int count;

			@Override
			public void run() {
				try {
					if (count++ == 7 || !(player.getControllerManager().getController() instanceof FightKilnController)) {
						player.getTempAttribs().removeB("FightKilnCrystal");
						player.sendMessage("<col=7E2217>The power of the crystal dwindles and your " + Constants.SKILL_NAME[skill] + " prowess returns to normal.");
						player.getSkills().set(Constants.DEFENSE, player.getSkills().getLevelForXp(Constants.DEFENSE));
						player.getSkills().set(Constants.STRENGTH, player.getSkills().getLevelForXp(Constants.STRENGTH));
						player.getSkills().set(Constants.RANGE, player.getSkills().getLevelForXp(Constants.RANGE));
						player.getSkills().set(Constants.MAGIC, player.getSkills().getLevelForXp(Constants.MAGIC));
						stop();
					} else {
						for (int i = 1; i < 7; i++) {
							if (i == skill || i == 3 || i == 5)
								continue;
							player.getSkills().set(i, player.getSkills().getLevelForXp(i) / 2);
						}
						player.getSkills().set(skill, (int) (player.getSkills().getLevelForXp(skill) * 1.5));
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, Ticks.fromSeconds(30));
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 15195 && getCurrentWave() == 0) {
			player.getDialogueManager().execute(new TokHaarHok(), 0, TOKHAAR_HOK, this);
			return false;
		}
		return true;

	}

	@Override
	public void process() {
		if (harAken != null)
			harAken.process();
	}

	public void hideHarAken() {
		if (stage != Stages.RUNNING)
			return;
		harAken.resetTimer();
		harAken.setCantInteract(true);
		harAken.setNextAnimation(new Animation(16234));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					if (stage != Stages.RUNNING)
						return;
					harAken.finish();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, Ticks.fromSeconds(3));
	}
}