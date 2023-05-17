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

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.content.minigames.pest.npcs.*;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PestControl {

	private final static int[][] PORTAL_LOCATIONS = { { 4, 56, 45, 21, 32 }, { 31, 28, 10, 9, 32 } };
	private final static int[] KNIGHT_IDS = { 3782, 3784, 3785 };

	private Instance region;
	private int[] pestCounts = new int[5];

	private List<Player> team;
	private List<NPC> brawlers = new LinkedList<>();
	private PestPortal[] portals = new PestPortal[4];

	private PestPortal knight;
	private PestData data;

	private byte portalCount = 5;

	private class PestGameTimer extends WorldTask {

		int seconds = 1200;

		@Override
		public void run() {
			try {
				updateTime(seconds / 60);
				if (seconds == 0 || canFinish()) {
					endGame();
					stop();
					return;
				}
				if (seconds % 10 == 0)
					sendPortalInterfaces();
				seconds--;
			} catch (Exception e) {
				Logger.handle(PestGameTimer.class, "run", e);
			}
		}
	}

	public PestControl(List<Player> team, PestData data) {
		this.team = Collections.synchronizedList(team);
		this.data = data;
	}

	public PestControl create() {
		region = Instance.of(Lander.LanderRequirement.VETERAN.exit, 8, 8);
		region.copyMapAllPlanes(328, 320).thenAccept(e -> {
			sendBeginningWave();
			unlockPortal();
			for (Player player : team) {
				player.getControllerManager().removeControllerWithoutCheck();
				player.useStairs(-1, getTile(35 - Utils.random(4), 54 - (Utils.random(3))), 1, 2);
				player.getControllerManager().startController(new PestControlGameController(this));
			}

			WorldTasks.schedule(new PestGameTimer(), 2, 2);
		});
		return this;
	}

	private void sendBeginningWave() {
		knight = new PestPortal(KNIGHT_IDS[Utils.random(KNIGHT_IDS.length)], true, getTile(32, 32), this);
		knight.unlock();
		for (int index = 0; index < portals.length; index++) {
			PestPortal portal = portals[index] = new PestPortal(6146 + index, true, getTile(PORTAL_LOCATIONS[0][index], PORTAL_LOCATIONS[1][index]), this);
			portal.setHitpoints(data.ordinal() == 0 ? 2000 : 2500);
		}
	}

	public boolean createPestNPC(int index) {
		if (region == null || pestCounts[index] >= (index == 4 ? 4 : (portals[index] != null && portals[index].isLocked()) ? 5 : 15))
			return false;
		pestCounts[index]++;
		Tile baseTile = getTile(PORTAL_LOCATIONS[0][index], PORTAL_LOCATIONS[1][index]);
		Tile teleTile = baseTile;
		int npcId = index == 4 ? data.getShifters()[Utils.random(data.getShifters().length)] : data.getPests()[Utils.random(data.getPests().length)];
		NPCDefinitions defs = NPCDefinitions.getDefs(npcId);
		for (int trycount = 0; trycount < 10; trycount++) {
			teleTile = Tile.of(baseTile, 5);
			if (World.floorAndWallsFree(teleTile, defs.size))
				break;
			teleTile = baseTile;
		}
		String name = defs.getName().toLowerCase();
		if (name.contains("shifter"))
			new Shifter(npcId, teleTile, -1, true, true, index, this);
		else if (name.contains("splatter"))
			new Splatter(npcId, teleTile, -1, true, true, index, this);
		else if (name.contains("spinner"))
			new Spinner(npcId, teleTile, -1, true, true, index, this);
		else if (name.contains("brawler"))
			brawlers.add(new PestMonsters(npcId, teleTile, -1, true, true, index, this));
		else
			new PestMonsters(npcId, teleTile, -1, true, true, index, this);
		return true;
	}

	public void endGame() {
		final List<Player> team = new LinkedList<>();
		team.addAll(this.team);
		this.team.clear();
		for (final Player player : team) {
			final int knightZeal = (int) ((PestControlGameController) player.getControllerManager().getController()).getPoints();
			player.getControllerManager().forceStop();
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					sendFinalReward(player, knightZeal);
				}
			}, 1);
		}
		region.destroy();
		region = null;
	}

	private void sendFinalReward(Player player, int knightZeal) {
		if (knight.isDead())
			player.simpleDialogue("You failed to protect the void knight and have not been awarded any points.");
		else if (knightZeal < 750)
			player.simpleDialogue("The knights notice your lack of zeal in that battle and have not presented you with any points.");
		else {
			int coinsAmount = player.getSkills().getCombatLevel() * 100;
			int pointsAmount = data.getReward();
			player.simpleDialogue("Congratulations! You have successfully kept the lander safe and have been awarded: " + coinsAmount + " gold coins and " + pointsAmount + " commendation points.");
			player.getInventory().addCoins(coinsAmount);
			player.setPestPoints(player.getPestPoints() + pointsAmount);
		}
	}

	private void sendPortalInterfaces() {
		for (Player player : team) {
			for (int i = 13; i < 17; i++) {
				PestPortal npc = portals[i - 13];
				if (npc != null)
					player.getPackets().setIFText(408, i, npc.getHitpoints() + "");
			}
			player.getPackets().setIFText(408, 1, "" + knight.getHitpoints());
		}
	}

	public void unlockPortal() {
		if (portalCount == 0)
			return;
		if (portalCount == 1) {
			portalCount--;
			return;
		}
		final int index = Utils.random(portals.length);
		if (portals[index] == null || portals[index].isDead())
			unlockPortal();
		else {
			portalCount--;
			WorldTasks.schedule(new WorldTask() {

				@Override
				public void run() {
					portals[index].unlock();
				}
			}, 30);
		}
	}

	public boolean isBrawlerAt(Tile tile) {
		for (Iterator<NPC> it = brawlers.iterator(); it.hasNext();) {
			NPC npc = it.next();
			if (npc.isDead() || npc.hasFinished()) {
				it.remove();
				continue;
			}
			if (npc.getX() == tile.getX() && npc.getY() == tile.getY() && tile.getPlane() == tile.getPlane())
				return true;
		}
		return false;
	}

	private void updateTime(int minutes) {
		for (Player player : team)
			player.getPackets().setIFText(408, 0, minutes + " min");
	}

	public void sendTeamMessage(String message) {
		for (Player player : team)
			player.sendMessage(message, true);
	}

	private boolean canFinish() {
		if (knight == null || knight.isDead())
			return true;
		return portalCount == 0;
	}

	public Tile getTile(int mapX, int mapY) {
		return region.getLocalTile(mapX, mapY);
	}

	public PestPortal[] getPortals() {
		return portals;
	}

	public List<Player> getPlayers() {
		return team;
	}

	public NPC getKnight() {
		return knight;
	}

	public enum PestData {

		NOVICE(new int[] { /* Shifters */3732, 3733, 3734, 3735, /* Ravagers */3742, 3743, 3744, /* Brawler */3772, 3773, /* Splatter */3727, 3728, 3729, /* Spinner */3747, 3748, 3749, /* Torcher */3752, 3753, 3754, 3755, /* Defiler */3762, 3763,
				3764, 3765 }, new int[] { 3732, 3733, 3734, 3735 }, 3),

		INTERMEDIATE(new int[] { /* Shifters */3734, 3735, 3736, 3737, 3738, 3739/* Ravagers */, 3744, 3743, 3745, /* Brawler */3773, 3775, 3776, /* Splatter */3728, 3729, 3730, /* Spinner */3748, 3749, 3750, 3751, /* Torcher */3754, 3755, 3756, 3757,
				3758, 3759, /* Defiler */3764, 3765, 3766, 3768, 3769 }, new int[] { 3734, 3735, 3736, 3737, 3738, 3739 }, 5),

		VETERAN(new int[] { /* Shifters */3736, 3737, 3738, 3739, 3740, 3741 /* Ravagers */, 3744, 3745, 3746, /* Brawler */3776, 3774,/* Splatter */3729, 3730, 3731, /* Spinner */3749, 3750, 3751, /* Torcher */3758, 3759, 3760, 3761,/* Defiler */
				3770, 3771 }, new int[] { 3736, 3737, 3738, 3739, 3740, 3741 }, 7);

		private int[] pests, shifters;
		private int reward;

		private PestData(int[] pests, int[] shifters, int reward) {
			this.pests = pests;
			this.shifters = shifters;
			this.reward = reward;
		}

		public int[] getShifters() {
			return shifters;
		}

		public int[] getPests() {
			return pests;
		}

		public int getReward() {
			return reward;
		}
	}

	public int[] getPestCounts() {
		return pestCounts;
	}

	public PestData getPestData() {
		return data;
	}

	public int getPortalCount() {
		return portalCount;
	}
}
