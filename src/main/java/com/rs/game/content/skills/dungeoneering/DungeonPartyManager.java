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
package com.rs.game.content.skills.dungeoneering;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;

import java.util.concurrent.CopyOnWriteArrayList;



public final class DungeonPartyManager {

	private String leader; // username
	private int floor;
	private long seed;
	public boolean customSeed;
	private int complexity;
	private int size;
	private int difficulty;
	private boolean guideMode;

	private final CopyOnWriteArrayList<Player> team;
	private DungeonManager dungeon;

	public DungeonPartyManager() {
		team = new CopyOnWriteArrayList<>();
	}

	public void setDefaults() {
		floor = 1;
		complexity = 6;
		difficulty = team.size();
		guideMode = false;
	}

	public void leaveParty(Player player, boolean logout) {
		if (dungeon != null)
			dungeon.exitDungeon(player, logout);
		else {
			player.setForceMultiArea(false);
			player.stopAll();
			remove(player, logout);
		}
		player.getDungManager().refresh();
		player.getInterfaceManager().removeOverlay();
	}

	public void remove(Player player, boolean logout) {
		team.remove(player);
		player.getDungManager().setParty(null);
		player.getDungManager().expireInvitation();
		player.getDungManager().refreshPartyDetailsComponents();
		player.sendMessage("You leave the party.");
		player.getDungManager().refreshNames();
		if (dungeon != null && team.isEmpty()) {
			if (dungeon.hasLoadedNoRewardScreen() && logout) // destroy timer cant exist with a party member on anyway, team must be 0
				dungeon.setDestroyTimer();
			else
				dungeon.destroy();
		} else
			for (Player p2 : team) {
				p2.sendMessage(player.getDisplayName() + " has left the party.");
				if (isLeader(player))
					setLeader(p2);
				refreshPartyDetails(p2);
			}
		player.getDungManager().refresh();
	}

	public void refreshPartyDetails(Player player) {
		player.getDungManager().refreshPartyDetailsComponents();
		player.getDungManager().refresh();
	}

	public void add(Player player) {
		for (Player p2 : team)
			p2.sendMessage(player.getDisplayName() + " has joined the party.");
		team.add(player);
		player.getDungManager().setParty(this);
		if (team.size() == 1) {
			setLeader(player);
			if (dungeon != null)
				dungeon.endDestroyTimer();
		} else
			player.sendMessage("You join the party.");
		player.getDungManager().refresh();
	}

	public boolean isLeader(Player player) {
		return player.getUsername().equals(leader);
	}

	public void setLeader(Player player) {
		leader = player.getUsername();
		if (team.size() > 1)
			if (team.get(0).getUsername().equals(leader)) {
				Player positionZero = team.get(0);
				team.remove(player);
				team.set(0, player);
				team.add(positionZero);
			}
		player.sendMessage("You have been set as the party leader.");
		player.getDungManager().refresh();
	}

	public void lockParty() {
		for (Player player : team) {
			player.stopAll();
			player.lock();
		}
	}

	public void start() {
		if (dungeon != null)
			return;
		dungeon = new DungeonManager(this);
	}

	public long getStartingSeed() {
		return seed;
	}

	/**
	 * Creates a seed for a dungeon to run off of
	 * @param seed starting seed
	 */
	public void setStartingSeed(long seed) {
		customSeed = true;
		this.seed = seed;
	}

	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
		for (Player player : team)
			player.getDungManager().refreshComplexity();
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setDificulty(int dificulty) {
		if (dificulty > team.size())
			dificulty = team.size();
		difficulty = dificulty;
	}

	public int getFloor() {
		return floor;
	}

	public String getLeader() {
		return leader;
	}

	public Player getLeaderPlayer() {
		for (Player player : team)
			if (player.getUsername().equals(leader))
				return player;
		return null;
	}

	public Player getGateStonePlayer() {
		for (Player player : team)
			if (player.getInventory().containsItem(DungeonConstants.GROUP_GATESTONE, 1))
				return player;
		return null;
	}

	public void setFloor(int floor) {
		this.floor = floor;
		for (Player player : team)
			player.getDungManager().refresh();
	}

	public int getFloorType() {
		return DungeonUtils.getFloorType(floor);
	}

	public int getDungeoneeringLevel() {
		int level = 120;
		for (Player player : team) {
			int playerLevel = player.getSkills().getLevelForXp(Constants.DUNGEONEERING);
			if (playerLevel < level)
				level = playerLevel;
		}
		return level;
	}

	public double getLevelDiferencePenalty(Player player) {
		int average = getAverageCombatLevel();
		int cb = player.getSkills().getCombatLevelWithSummoning();
		double diff = Math.abs(cb - average);
		return (diff > 50 ? ((diff - 50) * 0.01) : 0);
	}

	public int getMaxLevelDifference() {
		if (team.size() <= 1)
			return 0;
		int maxLevel = 0;
		int minLevel = 138;
		for (Player player : team) {
			int level = player.getSkills().getCombatLevelWithSummoning();
			if (maxLevel < level)
				maxLevel = level;
			if (minLevel > level)
				minLevel = level;
		}
		return Math.abs(maxLevel - minLevel);
	}

	public DungeonManager getDungeon() {
		return dungeon;
	}

	public int getMaxFloor() {
		int floor = 60;
		for (Player player : team)
			if (player.getDungManager().getMaxFloor() < floor)
				floor = player.getDungManager().getMaxFloor();
		return floor;
	}

	public int getMaxComplexity() {
		int complexity = 6;
		for (Player player : team)
			if (player.getDungManager().getMaxComplexity() < complexity)
				complexity = player.getDungManager().getMaxComplexity();
		return complexity;
	}

	public int getCombatLevel() {
		int level = 0;
		for (Player player : team)
			level += player.getSkills().getCombatLevelWithSummoning();
		return team.isEmpty() ? 138 : level;
	}

	public int getAverageCombatLevel() {
		if (team.isEmpty())
			return 138;
		int level = 0;
		for (Player player : team)
			level += player.getSkills().getCombatLevelWithSummoning();
		return level / team.size();
	}

	public int getDefenceLevel() {
		if (team.isEmpty())
			return 99;
		int level = 0;
		for (Player player : team)
			level += player.getSkills().getLevelForXp(Constants.DEFENSE);
		return level / team.size();
	}

	public double getDifficultyRatio() {
		if (difficulty > team.size())
			return 1;
		return 0.7 + (((double) difficulty / (double) team.size()) * 0.3);
	}

	public int getMaxLevel(int skill) {
		if (team.isEmpty())
			return 1;
		int level = 0;
		for (Player player : team) {
			int lvl = player.getSkills().getLevelForXp(skill);
			if (lvl > level)
				level = lvl;
		}
		return level;
	}

	public int getAttackLevel() {
		if (team.isEmpty())
			return 99;
		int level = 0;
		for (Player player : team)
			level += player.getSkills().getLevelForXp(Constants.ATTACK);
		return level / team.size();
	}

	public int getMagicLevel() {
		if (team.isEmpty())
			return 99;
		int level = 0;
		for (Player player : team)
			level += player.getSkills().getLevelForXp(Constants.MAGIC);
		return level / team.size();
	}

	public int getRangeLevel() {
		if (team.isEmpty())
			return 99;
		int level = 0;
		for (Player player : team)
			level += player.getSkills().getLevelForXp(Constants.RANGE);
		return level / team.size();
	}

	public CopyOnWriteArrayList<Player> getTeam() {
		return team;
	}

	public int getSize() {
		return size;
	}

	public int getIndex(Player player) {
		int index = 0;
		for (Player p2 : team) {
			if (p2 == player)
				return index;
			index++;
		}
		return 0;
	}

	public int getDificulty() {
		if (difficulty > team.size())
			difficulty = team.size();
		return difficulty;
	}

	public boolean isGuideMode() {
		return guideMode || complexity <= 4;
	}

	/*
	 * dont use for dung itself
	 */

	public boolean getGuideMode() {
		return guideMode;
	}

	public void setGuideMode(boolean guideMode) {
		this.guideMode = guideMode;
	}
}
