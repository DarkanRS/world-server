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
package com.rs.game.content.skills.slayer;

import com.rs.cache.loaders.NPCDefinitions;

import java.util.HashMap;
import java.util.Map;

public enum SlayerMonsters {
	CRAWLING_HAND(1648, 5),
	CAVE_BUG(1832, 7),
	CAVE_CRAWLER(7787, 10),
	BANSHEE(1612, 15),
	CAVE_SLIME(1831, 17),
	ROCKSLUG(1631, 20),
	DESERT_LIZARD(2804, 22),
	COCKATRICE(1620, 25),
	PYREFIED(1633, 30),
	MOGRE(114, 32),
	HARPIE_BUG_SWARM(3153, 33),
	WALL_BEAST(7823, 35),
	KILLERWATT(3201, 37),
	MOLANISK(5751, 39),
	TERROR_DOG(5417, 40),
	BASILISK(1616, 40),
	FEVER_SPIDER(2850, 42),
	INFERNAL_MAGE(1643, 45),
	BRINE_RAT(3707, 47),
	BLOODVELD(1618, 50),
	JELLY(1637, 52),
	TUROTH(1623, 55),
	WARPED_TERRORBIRD(6285, 56),
	WARPED_TORTOISE(6296, 56),
	MUTATED_ZYGOMITE(3346, 57),
	MUTATED_ZYGOMITE2(3347, 57),
	CAVE_HORROR(4353, 58),
	ABERRANT_SPECTRE(1604, 60),
	RUM_PUMPED_CRAB(13603, 61),
	SPIRITUAL_RANGER(6256, 63),
	DUST_DEVIL(1624, 65),
	SPIRITUAL_WARRIOR(6255, 68),
	KURASK(1608, 70),
	SKELETAL_WYVERN(3068, 72),
	JUNGLE_STRYKEWYRM(9467, 73),
	GARGOYLE(1610, 75),
	DESERT_STRYKEWYRM(9465, 77),
	AQUANITE(9172, 78),
	NECHRYAEL(1613, 80),
	SPIRITUAL_MAGE(6221, 83),
	SPIRITUAL_MAGE2(6231, 83),
	SPIRITUAL_MAGE3(6257, 83),
	SPIRITUAL_MAGE4(6278, 83),
	ABYSSAL_DEMON(1615, 85),
	JADINKO_GUARD(13821, 86),
	GRIFOLAPINE(14688, 88),
	DARK_BEAST(2783, 90),
	JADINKO_MALE(13822, 91),
	ICE_STRYKEWYRM(9463, 93),
	GANODERMIC_BEAST(14696, 95);

	private static Map<String, SlayerMonsters> monsters = new HashMap<>();

	public static SlayerMonsters forId(int id) {
		return monsters.get(NPCDefinitions.getDefs(id).getName());
	}

	static {
		for (SlayerMonsters monster : SlayerMonsters.values())
			monsters.put(NPCDefinitions.getDefs(monster.id).getName(), monster);
	}

	private int id;
	private int req;

	private SlayerMonsters(int id, int req) {
		this.id = id;
		this.req = req;
	}

	public int getId() {
		return id;
	}

	public int getRequirement() {
		return req;
	}
}