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
package com.rs.game.content.skills.woodcutting;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.util.Utils;
import com.sun.source.tree.Tree;

public enum TreeType {
	FRUIT_TREE(1, 0, -1, 60, 200, 8, false),
	NORMAL(1, 25, 1511, 60, 200, 8, false),
	ACHEY(1, 25, 2862, 60, 200, 8, false),
	OAK(15, 37.5, 1521, 60, 180, 15, true),
	WILLOW(30, 67.5, 1519, 35, 155, 51, true),
	TEAK(35, 105, 6333, 1, 130, 51, true),
	DRAMEN(36, 1, 771, 1, 130, -1, false),
	SWAYING(40, 1, 3692, 45, 65, -1, false),
	MAPLE(45, 100, 1517, 45, 65, 72, true),
	MAHOGANY(50, 125, 6332, 20, 55, 51, true),
	ARCTIC_PINE(54, 125, 10810, 20, 55, 51, true),
	EUCALYPTUS(58, 165, 12581, 25, 55, 148, true),
	YEW(60, 175, 1515, 6, 35, 94, true),
	IVY(68, 332.5, -1, 16, 30, 58, true),
	MAGIC(75, 250, 1513, -10, 24, 121, true),
	BLISTERWOOD(76, 200, 21600, -10, 24, 8, true),
	BLOODWOOD(85, 100, 24121, -10, 24, 121, true),

	MUTATED_VINE(83, 140, 21358, 6, 35, 72, false),
	CURLY_VINE(83, 140, null, 6, 35, 72, false),
	CURLY_VINE_COLLECTABLE(83, 140, new int[] { 21350, 21350, 21350, 21350 }, 6, 35, 72, false),
	STRAIGHT_VINE(83, 140, null, 6, 35, 72, false),
	STRAIGHT_VINE_COLLECTABLE(83, 140, new int[] { 21349, 21349, 21349, 21349 }, 6, 35, 72, false);

	private final int level;
	private final double xp;
	private final int[] logsId;
	private final int rate1;
	private final int rate99;
	private final int respawnDelay;
	private final boolean persistent;

	private TreeType(int level, double xp, int[] logsId, int rate1, int rate99, int respawnDelay, boolean persistent) {
		this.level = level;
		this.xp = xp;
		this.logsId = logsId;
		this.rate1 = rate1;
		this.rate99 = rate99;
		this.respawnDelay = respawnDelay;
		this.persistent = persistent;
	}

	private TreeType(int level, double xp, int logsId, int rate1, int rate99, int respawnDelay, boolean persistent) {
		this(level, xp, new int[] { logsId }, rate1, rate99, respawnDelay, persistent);
	}

	public int getLevel() {
		return level;
	}

	public double getXp() {
		return xp;
	}

	public int[] getLogsId() {
		return logsId;
	}

	public int getRespawnDelay() {
		return respawnDelay;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public boolean rollSuccess(double mul, int level, Hatchet hatchet) {
		return Utils.skillSuccess((int) (level * mul), hatchet.getToolMod(), rate1, rate99);
	}
	
	public static TreeType forObject(Player player, GameObject object) {
		switch(object.getId()) {
		case 46274, 46275, 46277, 15062 -> { return null; }
		}
		
		return switch(object.getDefinitions(player).getName()) {
		case "Tree", "Swamp tree", "Dead tree", "Evergreen", "Dying tree", "Jungle Tree" -> TreeType.NORMAL;
		case "Achey", "Achey Tree" -> TreeType.ACHEY;
		case "Oak", "Oak tree" -> TreeType.OAK;
		case "Willow", "Willow tree" -> TreeType.WILLOW;
		case "Maple", "Maple tree", "Maple Tree" -> TreeType.MAPLE;
		case "Teak", "Teak tree" -> TreeType.TEAK;
		case "Mahogany", "Mahogany tree" -> TreeType.MAHOGANY;
		case "Arctic Pine" -> TreeType.ARCTIC_PINE;
		case "Eucalyptus", "Eucalyptus tree" -> TreeType.EUCALYPTUS;
		case "Yew", "Yew tree" -> TreeType.YEW;
		case "Magic tree", "Cursed magic tree" -> TreeType.MAGIC;
		default -> null;
		};
	}
}
