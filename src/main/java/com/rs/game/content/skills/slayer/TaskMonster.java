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

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.engine.quest.Quest;

import java.util.HashMap;

public enum TaskMonster {
	MONKEYS(1, 1, "monkey"),
	GOBLINS(2, 1, "goblin"),
	SPIDERS(4, 1, "spider"),
	BIRDS(5, 1, "chicken", "bird"),
	COWS(6, 1, "cow"),
	SCORPIONS(7, 1, "scorpion"),
	BATS(8, 1, "bat", " bat"),
	WOLVES(9, 1, "wolf"),
	ZOMBIES(10, 1, "zombie"),
	SKELETONS(11, 1, "skeleton"),
	GHOSTS(12, 1, "ghost", "revenant"),
	BEARS(13, 1, " bear", "bear cub"),
	HILL_GIANTS(14, 1, "hill giant"),
	ICE_GIANTS(15, 1, "ice giant"),
	FIRE_GIANTS(16, 1, "fire giant"),
	MOSS_GIANTS(17, 1, "moss giant"),
	TROLLS(18, 1, "troll"),
	ICE_WARRIORS(19, 1, "ice warrior"),
	OGRES(20, 1, "ogre"),
	HOBGOBLINS(21, 1, "hobgoblin"),
	DOGS(22, 1, "jackal", " dog"),
	GHOULS(23, 1, "ghoul"),
	GREEN_DRAGONS(24, 1, "green dragon"),
	BLUE_DRAGONS(25, 1, "blue dragon"),
	RED_DRAGONS(26, 1, "red dragon"),
	BLACK_DRAGONS(27, 1, "black dragon"),
	LESSER_DEMONS(28, 1, "lesser demon"),
	GREATER_DEMONS(29, 1, "greater demon", "tsutsaroth", "tstanon"),
	BLACK_DEMONS(30, 1, "black demon", "kreeyath"),
	HELLHOUNDS(31, 1, "hellhound"),
	SHADOW_WARRIORS(32, 1, Quest.LEGENDS_QUEST, "shadow warrior"),
	WEREWOLVES(33, 1, Quest.PRIEST_IN_PERIL, "werewolf", "boris", "eduard", "galina", "georgy", "imre", "irina", "jeroen", "joseph", "ksenia", "liliya", "lev", "milla", "nikita", "nikolai", "sofiya", "svetlana", "vera", "yadviga", "yuri", "zoja"),
	VAMPYRES(34, 1, Quest.PRIEST_IN_PERIL, "vampyre"),
	DAGANNOTH(35, 1, "dagannoth"),
	TUROTH(36, 55, "turoth"),
	CAVE_CRAWLERS(37, 10, "cave crawler"),
	BANSHEES(38, 15, "banshee"),
	CRAWLING_HANDS(39, 5, " hand"),
	INFERNAL_MAGES(40, 45, "infernal mage"),
	ABERRANT_SPECTRES(41, 60, "aberrant spectre"),
	ABYSSAL_DEMONS(42, 85, "abyssal demon"),
	BASILISKS(43, 40, "basilisk"),
	COCKATRICE(44, 25, "cockatrice"),
	KURASK(45, 70, "kurask"),
	GARGOYLES(46, 75, "gargoyle"),
	PYREFIENDS(47, 30, "pyrefiend"),
	BLOODVELD(48, 50, "bloodveld"),
	DUST_DEVILS(49, 65, "dust devil"),
	JELLIES(50, 52, "jelly"),
	ROCKSLUGS(51, 20, "rockslug"),
	NECHRYAEL(52, 80, "nechryael"),
	KALPHITE(53, 1, "kalphite"),
	EARTH_WARRIORS(54, 1, "earth warrior"),
	OTHERWORLDLY_BEINGS(55, 1, Quest.LOST_CITY, "otherworldly being"),
	ELVES(56, 1, Quest.MOURNINGS_ENDS_PART_I, "elf"),
	DWARVES(57, 1, "dwarf"),
	BRONZE_DRAGONS(58, 1, "bronze dragon"),
	IRON_DRAGONS(59, 1, "iron dragon"),
	STEEL_DRAGONS(60, 1, "steel dragon"),
	//61 WALL_BEASTS
	CAVE_SLIMES(62, 17, "cave slime"),
	CAVE_BUGS(63, 7, "cave bug"),
	SHADES(64, 1, "shade", "loar shadow"),
	CROCODILES(65, 1, "crocodile"),
	DARK_BEASTS(66, 90, "dark beast"),
	//67 MOGRES
	DESERT_LIZARDS(68, 22, "lizard"),
	FEVER_SPIDERS(69, 42, Quest.CABIN_FEVER, "fever spider"),
	HARPIE_BUG_SWARMS(70, 33, "harpie bug swarm"),
	//71 SEA_SNAKES
	SKELETAL_WYVERNS(72, 72, "skeletal wyvern"),
	KILLERWATTS(73, 37, "killerwatt"),
	//74 MUTATED_ZYGOMITES
	ICEFIENDS(75, 1, "icefiend"),
	MINOTAURS(76, 1, "minotaur"),
	FLESHCRAWLERS(77, 1, "flesh crawler"),
	CATABLEPON(78, 1, "catablepon"),
	ANKOU(79, 1, "ankou"),
	CAVE_HORRORS(80, 58, Quest.CABIN_FEVER, "cave horror"),
	JUNGLE_HORRORS(81, 1, Quest.CABIN_FEVER, "jungle horror"),
	GORAKS(82, 1, Quest.FAIRY_TALE_II_CURE_A_QUEEN, "gorak"),
	SUQAHS(83, 1, Quest.LUNAR_DIPLOMACY, "suqah"),
	BRINE_RATS(84, 47, Quest.OLAFS_QUEST, "brine rat"),
	//85 SCABARITES
	//86 TERROR_DOGS
	MOLANISKS(87, 39, "molanisk"),
	WATERFIENDS(88, 1, "waterfiend"),
	SPIRITUAL_WARRIORS(89, 68, "spiritual warrior"),
	//90 SPIRITUAL_RANGERS
	SPIRITUAL_MAGES(91, 83, "spiritual mage"),
	WARPED_TORTOISES(92, 56, Quest.PATH_OF_GLOUPHRIE, "warped tortoise"),
	WARPED_TERRORBIRDS(93, 56, Quest.PATH_OF_GLOUPHRIE, "warped terrorbird"),
	MITHRIL_DRAGONS(94, 1, "mithril dragon"),
	AQUANITES(95, 78, "aquanite"),
	GANODERMIC_CREATURES(96, 95, "ganodermic"),
	GRIFOLAPINES(97, 88, "grifolapine"),
	GRIFOLAROOS(98, 82, "grifolaroo"),
	FUNGAL_MAGI(99, 1, "fungal mage"),
	//100 POLYPORE_CREATURES
	//101 TZHAAR
	VOLCANIC_CREATURES(102, 1, "tz", "tok-"),
	JUNGLE_STRYKEWYRMS(103, 73, "jungle strykewyrm"),
	DESERT_STRYKEWYRMS(104, 77, "desert strykewyrm"),
	ICE_STRYKEWYRMS(105, 93, "ice strykewyrm"),
	LIVING_ROCK_CREATURES(106, 1, "living rock"),
	//107 NOTHING
	CYCLOPES(108, 1, "cyclops"),
	MUTATED_JADINKOS(109, 80, "mutated jadinko"),
	VYREWATCH(110, 1, Quest.BRANCHES_OF_DARKMEYER, "vyrewatch", "vyrelord", "vyrelady"),
	//111 GELATINOUS_ABOMINATIONS
	GROTWORMS(112, 1, "grotworm");

	private static HashMap<Integer, TaskMonster> MAP = new HashMap<>();

	static {
		for (TaskMonster creature : TaskMonster.values())
			MAP.put(creature.enumId, creature);
	}

	public static TaskMonster forEnum(int enumId) {
		return MAP.get(enumId);
	}

	private int enumId, level;
	private String[] monsterNames;
	private Quest questReq;

	private TaskMonster(int enumId, int level, Quest questReq, String... monsterNames) {
		this.enumId = enumId;
		this.level = level;
		this.questReq = questReq;
		this.monsterNames = monsterNames;
	}

	private TaskMonster(int enumId, int level, String... monsterNames) {
		this.enumId = enumId;
		this.level = level;
		this.monsterNames = monsterNames;
	}

	public int getEnumId() {
		return enumId;
	}

	public int getLevel() {
		return level;
	}

	public Quest getQuestReq() {
		return questReq;
	}

	public String[] getMonsterNames() {
		return monsterNames;
	}

	public String getName() {
		return EnumDefinitions.getEnum(1563).getStringValue(enumId);
	}
}
