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
package com.rs.game.content.skills.firemaking;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.Effect;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.ArrayList;

public class Bonfire extends PlayerAction {

	public static enum Log {
		LOG(1511, 3098, 1, 50, 6),
		ACHEY(2862, 3098, 1, 50, 6),
		OAK(1521, 3099, 15, 75, 12),
		WILLOW(1519, 3101, 30, 112.5, 18),
		TEAK(6333, 3098, 35, 120, 18),
		ARCTIC_PINE(10810, 3098, 42, 135, 18),
		MAPLE(1517, 3100, 45, 157, 36),
		MAHOGANY(6332, 3098, 50, 180, 36),
		EUCALYPTUS(12581, 3112, 58, 241, 48),
		YEWS(1515, 3111, 60, 252, 54),
		MAGIC(1513, 3135, 75, 378, 60),
		BLISTERWOOD(21600, 3113, 76, 378, 60),
		CURSED_MAGIC(13567, 3116, 82, 378, 60);

		private int logId, gfxId, level, boostTime;
		private double xp;

		private Log(int logId, int gfxId, int level, double xp, int boostTime) {
			this.logId = logId;
			this.gfxId = gfxId;
			this.level = level;
			this.xp = xp;
			this.boostTime = boostTime;
		}

		public int getLogId() {
			return logId;
		}

	}

	private Log log;
	private GameObject object;
	private int count;

	public Bonfire(Log log, GameObject object) {
		this.log = log;
		this.object = object;
	}

	private boolean checkAll(Player player) {
		if (!ChunkManager.getChunk(object.getTile().getChunkId()).objectExists(object) || !player.getInventory().containsItem(log.logId, 1))
			return false;
		if (player.getSkills().getLevel(Constants.FIREMAKING) < log.level) {
			player.simpleDialogue("You need level " + log.level + " Firemaking to add these logs to a bonfire.");
			return false;
		}
		return true;
	}

	public static boolean addLog(Player player, GameObject object, Item item) {
		for (Log log : Log.values())
			if (log.logId == item.getId()) {
				player.getActionManager().setAction(new Bonfire(log, object));
				return true;
			}
		return false;
	}

	public static void addLogs(Player player, GameObject object) {
		ArrayList<Log> possiblities = new ArrayList<>();
		for (Log log : Log.values())
			if (player.getInventory().containsItem(log.logId, 1))
				possiblities.add(log);
		Log[] logs = possiblities.toArray(new Log[possiblities.size()]);
		if (logs.length == 0)
			player.sendMessage("You do not have any logs to add to this fire.");
		else if (logs.length == 1)
			player.getActionManager().setAction(new Bonfire(logs[0], object));
		else
			player.startConversation(new BonfireD(player, object, logs));
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			player.getAppearance().setBAS(2498);
			return true;
		}
		return false;

	}

	@Override
	public boolean process(Player player) {
		if (checkAll(player)) {
			if (Utils.random(500) == 0) {
				Tile tile = player.getNearestTeleTile(1);
				if (tile != null) {
					new FireSpirit(tile, player);
					player.sendMessage("<col=ff0000>A fire spirit emerges from the bonfire.");
				} else
					player.sendMessage("<col=ff0000>A fire spirit struggles to escape the bonfire. Try moving elsewhere.");
			}
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		player.incrementCount(ItemDefinitions.getDefs(log.logId).getName()+" burned in bonfire");
		player.getInventory().deleteItem(log.logId, 1);
		player.getSkills().addXp(Constants.FIREMAKING, Firemaking.increasedExperience(player, log.xp));
		player.setNextAnimation(new Animation(16703));
		player.setNextSpotAnim(new SpotAnim(log.gfxId));
		player.sendMessage("You add a log to the fire.", true);
		if (count++ == 4 && !player.hasEffect(Effect.BONFIRE)) {
			player.addEffect(Effect.BONFIRE, log.boostTime * 100);
			int percentage = (int) (getBonfireBoostMultiplier(player) * 100 - 100);
			player.sendMessage("<col=00ff00>The bonfire's warmth increases your maximum health by " + percentage + "%. This will last " + log.boostTime + " minutes.");
		}
		return 5;
	}

	public static double getBonfireBoostMultiplier(Player player) {
		int fmLvl = player.getSkills().getLevel(Constants.FIREMAKING);
		if (fmLvl >= 90)
			return 1.1;
		if (fmLvl >= 80)
			return 1.09;
		if (fmLvl >= 70)
			return 1.08;
		if (fmLvl >= 60)
			return 1.07;
		if (fmLvl >= 50)
			return 1.06;
		if (fmLvl >= 40)
			return 1.05;
		if (fmLvl >= 30)
			return 1.04;
		if (fmLvl >= 20)
			return 1.03;
		if (fmLvl >= 10)
			return 1.02;
		return 1.01;

	}

	@Override
	public void stop(final Player player) {
		player.getEmotesManager().setNextEmoteEnd(4);
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(16702));
				player.getAppearance().setBAS(-1);
			}

		}, 3);
	}

}
