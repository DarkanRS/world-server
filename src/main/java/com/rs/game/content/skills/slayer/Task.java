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

public enum Task {

	BANSHEES_T(Master.Turael, TaskMonster.BANSHEES, 30, 15, 50),
	BATS_T(Master.Turael, TaskMonster.BATS, 10, 15, 50),
	BEARS_T(Master.Turael, TaskMonster.BEARS, 10, 15, 50),
	BIRDS_T(Master.Turael, TaskMonster.BIRDS, 10, 15, 50),
	CAVE_BUGS_T(Master.Turael, TaskMonster.CAVE_BUGS, 10, 15, 50),
	CAVE_SLIMES_T(Master.Turael, TaskMonster.CAVE_SLIMES, 30, 15, 50),
	COWS_T(Master.Turael, TaskMonster.COWS, 10, 15, 50),
	CRAWLING_HANDS_T(Master.Turael, TaskMonster.CRAWLING_HANDS, 30, 15, 50),
	DESERT_LIZARDS_T(Master.Turael, TaskMonster.DESERT_LIZARDS, 30, 15, 50),
	DOGS_T(Master.Turael, TaskMonster.DOGS, 15, 15, 50),
	DWARVES_T(Master.Turael, TaskMonster.DWARVES, 10, 15, 50),
	GHOSTS_T(Master.Turael, TaskMonster.GHOSTS, 15, 15, 50),
	GOBLINS_T(Master.Turael, TaskMonster.GOBLINS, 10, 15, 50),
	GROTWORMS_T(Master.Turael, TaskMonster.GROTWORMS, 15, 15, 30),
	ICEFIENDS_T(Master.Turael, TaskMonster.ICEFIENDS, 10, 10, 20),
	MINOTAURS_T(Master.Turael, TaskMonster.MINOTAURS, 10, 15, 50),
	MONKEYS_T(Master.Turael, TaskMonster.MONKEYS, 10, 15, 50),
	SCORPIONS_T(Master.Turael, TaskMonster.SCORPIONS, 10, 15, 50),
	SKELETONS_T(Master.Turael, TaskMonster.SKELETONS, 15, 15, 50),
	SPIDERS_T(Master.Turael, TaskMonster.SPIDERS, 10, 15, 50),
	WOLVES_T(Master.Turael, TaskMonster.WOLVES, 15, 15, 50),
	ZOMBIES_T(Master.Turael, TaskMonster.ZOMBIES, 10, 15, 50),


	BANSHEES_M(Master.Mazchna, TaskMonster.BANSHEES, 20, 40, 70),
	BATS_M(Master.Mazchna, TaskMonster.BATS, 10, 40, 70),
	BEARS_M(Master.Mazchna, TaskMonster.BEARS, 10, 40, 70),
	CATABLEPON_M(Master.Mazchna, TaskMonster.CATABLEPON, 10, 40, 70),
	CAVE_CRAWLERS_M(Master.Mazchna, TaskMonster.CAVE_CRAWLERS, 20, 40, 70),
	CAVE_SLIMES_M(Master.Mazchna, TaskMonster.CAVE_SLIMES, 10, 40, 70),
	COCKATRICE_M(Master.Mazchna, TaskMonster.COCKATRICE, 20, 40, 70),
	CRAWLING_HANDS_M(Master.Mazchna, TaskMonster.CRAWLING_HANDS, 20, 40, 70),
	CYCLOPES_M(Master.Mazchna, TaskMonster.CYCLOPES, 10, 30, 60),
	DESERT_LIZARDS_M(Master.Mazchna, TaskMonster.DESERT_LIZARDS, 10, 40, 70),
	DOGS_M(Master.Mazchna, TaskMonster.DOGS, 10, 40, 70),
	FLESHCRAWLERS_M(Master.Mazchna, TaskMonster.FLESHCRAWLERS, 10, 40, 70),
	GHOULS_M(Master.Mazchna, TaskMonster.GHOULS, 10, 40, 70),
	GHOSTS_M(Master.Mazchna, TaskMonster.GHOSTS, 10, 40, 70),
	GROTWORMS_M(Master.Mazchna, TaskMonster.GROTWORMS, 10, 20, 40),
	HILL_GIANTS_M(Master.Mazchna, TaskMonster.HILL_GIANTS, 10, 40, 70),
	HOBGOBLINS_M(Master.Mazchna, TaskMonster.HOBGOBLINS, 10, 40, 70),
	ICE_WARRIORS_M(Master.Mazchna, TaskMonster.ICE_WARRIORS, 10, 40, 70),
	KALPHITE_M(Master.Mazchna, TaskMonster.KALPHITE, 10, 40, 70),
	PYREFIENDS_M(Master.Mazchna, TaskMonster.PYREFIENDS, 20, 40, 70),
	ROCKSLUGS_M(Master.Mazchna, TaskMonster.ROCKSLUGS, 15, 40, 70),
	SKELETONS_M(Master.Mazchna, TaskMonster.SKELETONS, 10, 40, 70),
	VAMPYRES_M(Master.Mazchna, TaskMonster.VAMPYRES, 10, 40, 70),
	WOLVES_M(Master.Mazchna, TaskMonster.WOLVES, 10, 40, 70),
	ZOMBIES_M(Master.Mazchna, TaskMonster.ZOMBIES, 10, 40, 70),


	ABERRANT_SPECTRES_V(Master.Vannaka, TaskMonster.ABERRANT_SPECTRES, 25, 60, 120),
	ANKOU_V(Master.Vannaka, TaskMonster.ANKOU, 10, 60, 120),
	BANSHEES_V(Master.Vannaka, TaskMonster.BANSHEES, 15, 60, 120),
	BASILISKS_V(Master.Vannaka, TaskMonster.BASILISKS, 10, 60, 120),
	BLOODVELD_V(Master.Vannaka, TaskMonster.BLOODVELD, 20, 60, 120),
	BRINE_RATS_V(Master.Vannaka, TaskMonster.BRINE_RATS, 20, 60, 120),
	COCKATRICE_V(Master.Vannaka, TaskMonster.COCKATRICE, 10, 60, 120),
	CROCODILES_V(Master.Vannaka, TaskMonster.CROCODILES, 10, 30, 60),
	CYCLOPES_V(Master.Vannaka, TaskMonster.CYCLOPES, 10, 60, 120),
	DUST_DEVILS_V(Master.Vannaka, TaskMonster.DUST_DEVILS, 25, 60, 120),
	EARTH_WARRIORS_V(Master.Vannaka, TaskMonster.EARTH_WARRIORS, 15, 30, 60),
	GHOULS_V(Master.Vannaka, TaskMonster.GHOULS, 10, 60, 120),
	GREEN_DRAGONS_V(Master.Vannaka, TaskMonster.GREEN_DRAGONS, 10, 30, 60),
	GROTWORMS_V(Master.Vannaka, TaskMonster.GROTWORMS, 15, 60, 120),
	HARPIE_BUG_SWARMS_V(Master.Vannaka, TaskMonster.HARPIE_BUG_SWARMS, 20, 60, 120),
	HILL_GIANTS_V(Master.Vannaka, TaskMonster.HILL_GIANTS, 10, 60, 120),
	ICE_GIANTS_V(Master.Vannaka, TaskMonster.ICE_GIANTS, 10, 60, 120),
	ICE_WARRIORS_V(Master.Vannaka, TaskMonster.ICE_WARRIORS, 10, 60, 120),
	INFERNAL_MAGES_V(Master.Vannaka, TaskMonster.INFERNAL_MAGES, 10, 60, 120),
	JELLIES_V(Master.Vannaka, TaskMonster.JELLIES, 20, 60, 120),
	JUNGLE_HORRORS_V(Master.Vannaka, TaskMonster.JUNGLE_HORRORS, 10, 60, 120),
	KILLERWATTS_V(Master.Vannaka, TaskMonster.KILLERWATTS, 20, 60, 120),
	LESSER_DEMONS_V(Master.Vannaka, TaskMonster.LESSER_DEMONS, 10, 60, 120),
	MOLANISKS_V(Master.Vannaka, TaskMonster.MOLANISKS, 10, 60, 120),
	MOSS_GIANTS_V(Master.Vannaka, TaskMonster.MOSS_GIANTS, 10, 60, 120),
	OGRES_V(Master.Vannaka, TaskMonster.OGRES, 10, 60, 120),
	OTHERWORLDLY_BEINGS_V(Master.Vannaka, TaskMonster.OTHERWORLDLY_BEINGS, 10, 60, 120),
	PYREFIENDS_V(Master.Vannaka, TaskMonster.PYREFIENDS, 10, 60, 120),
	SHADES_V(Master.Vannaka, TaskMonster.SHADES, 10, 60, 120),
	SHADOW_WARRIORS_V(Master.Vannaka, TaskMonster.SHADOW_WARRIORS, 10, 60, 120),
	TROLLS_V(Master.Vannaka, TaskMonster.TROLLS, 10, 60, 120),
	TUROTH_V(Master.Vannaka, TaskMonster.TUROTH, 20, 60, 120),
	VAMPYRES_V(Master.Vannaka, TaskMonster.VAMPYRES, 15, 60, 120),
	WEREWOLVES_V(Master.Vannaka, TaskMonster.WEREWOLVES, 10, 60, 120),


	ABERRANT_SPECTRES_C(Master.Chaeldar, TaskMonster.ABERRANT_SPECTRES, 15, 110, 170),
	BANSHEES_C(Master.Chaeldar, TaskMonster.BANSHEES, 5, 110, 170),
	BASILISKS_C(Master.Chaeldar, TaskMonster.BASILISKS, 15, 110, 170),
	BLOODVELD_C(Master.Chaeldar, TaskMonster.BLOODVELD, 15, 110, 170),
	BLUE_DRAGONS_C(Master.Chaeldar, TaskMonster.BLUE_DRAGONS, 10, 110, 170),
	BRINE_RATS_C(Master.Chaeldar, TaskMonster.BRINE_RATS, 10, 110, 170),
	BRONZE_DRAGONS_C(Master.Chaeldar, TaskMonster.BRONZE_DRAGONS, 10, 30, 60),
	CAVE_CRAWLERS_C(Master.Chaeldar, TaskMonster.CAVE_CRAWLERS, 5, 110, 170),
	CAVE_HORRORS_C(Master.Chaeldar, TaskMonster.CAVE_HORRORS, 15, 110, 170),
	CRAWLING_HANDS_C(Master.Chaeldar, TaskMonster.CRAWLING_HANDS, 5, 110, 170),
	DAGANNOTH_C(Master.Chaeldar, TaskMonster.DAGANNOTH, 10, 110, 170),
	DUST_DEVILS_C(Master.Chaeldar, TaskMonster.DUST_DEVILS, 15, 110, 170),
	ELVES_C(Master.Chaeldar, TaskMonster.ELVES, 10, 60, 150),
	FEVER_SPIDERS_C(Master.Chaeldar, TaskMonster.FEVER_SPIDERS, 10, 110, 170),
	FIRE_GIANTS_C(Master.Chaeldar, TaskMonster.FIRE_GIANTS, 10, 110, 170),
	FUNGAL_MAGI_C(Master.Chaeldar, TaskMonster.FUNGAL_MAGI, 10, 83, 136),
	GARGOYLES_C(Master.Chaeldar, TaskMonster.GARGOYLES, 15, 110, 170),
	GRIFOLAPINES_C(Master.Chaeldar, TaskMonster.GRIFOLAPINES, 8, 60, 100),
	GRIFOLAROOS_C(Master.Chaeldar, TaskMonster.GRIFOLAROOS, 8, 60, 100),
	GROTWORMS_C(Master.Chaeldar, TaskMonster.GROTWORMS, 15, 70, 100),
	HARPIE_BUG_SWARMS_C(Master.Chaeldar, TaskMonster.HARPIE_BUG_SWARMS, 20, 110, 170),
	JUNGLE_STRYKEWYRMS_C(Master.Chaeldar, TaskMonster.JUNGLE_STRYKEWYRMS, 12, 80, 110),
	INFERNAL_MAGES_C(Master.Chaeldar, TaskMonster.INFERNAL_MAGES, 10, 110, 170),
	JELLIES_C(Master.Chaeldar, TaskMonster.JELLIES, 15, 110, 170),
	JUNGLE_HORRORS_C(Master.Chaeldar, TaskMonster.JUNGLE_HORRORS, 15, 110, 170),
	KALPHITE_C(Master.Chaeldar, TaskMonster.KALPHITE, 10, 110, 170),
	KURASK_C(Master.Chaeldar, TaskMonster.KURASK, 15, 110, 170),
	LESSER_DEMONS_C(Master.Chaeldar, TaskMonster.LESSER_DEMONS, 10, 110, 170),
	TROLLS_C(Master.Chaeldar, TaskMonster.TROLLS, 10, 110, 170),
	TUROTH_C(Master.Chaeldar, TaskMonster.TUROTH, 15, 110, 170),
	VYREWATCH_C(Master.Chaeldar, TaskMonster.VYREWATCH, 10, 89, 106),
	WARPED_TORTOISES_C(Master.Chaeldar, TaskMonster.WARPED_TORTOISES, 10, 110, 170),


	ABERRANT_SPECTRES_S(Master.Sumona, TaskMonster.ABERRANT_SPECTRES, 15, 120, 185),
	ABYSSAL_DEMONS_S(Master.Sumona, TaskMonster.ABYSSAL_DEMONS, 10, 120, 185),
	AQUANITES_S(Master.Sumona, TaskMonster.AQUANITES, 10, 120, 185),
	BANSHEES_S(Master.Sumona, TaskMonster.BANSHEES, 15, 120, 185),
	BASILISKS_S(Master.Sumona, TaskMonster.BASILISKS, 15, 120, 185),
	BLACK_DEMONS_S(Master.Sumona, TaskMonster.BLACK_DEMONS, 10, 119, 185),
	BLOODVELD_S(Master.Sumona, TaskMonster.BLOODVELD, 10, 120, 185),
	BLUE_DRAGONS_S(Master.Sumona, TaskMonster.BLUE_DRAGONS, 8, 120, 189),
	CAVE_CRAWLERS_S(Master.Sumona, TaskMonster.CAVE_CRAWLERS, 15, 120, 185),
	CAVE_HORRORS_S(Master.Sumona, TaskMonster.CAVE_HORRORS, 15, 120, 185),
	DAGANNOTH_S(Master.Sumona, TaskMonster.DAGANNOTH, 10, 120, 192),
	DESERT_STRYKEWYRMS_S(Master.Sumona, TaskMonster.DESERT_STRYKEWYRMS, 14, 90, 110),
	DUST_DEVILS_S(Master.Sumona, TaskMonster.DUST_DEVILS, 15, 120, 185),
	ELVES_S(Master.Sumona, TaskMonster.ELVES, 10, 60, 90),
	FIRE_GIANTS_S(Master.Sumona, TaskMonster.FIRE_GIANTS, 10, 120, 185),
	FUNGAL_MAGI_S(Master.Sumona, TaskMonster.FUNGAL_MAGI, 10, 90, 150),
	GARGOYLES_S(Master.Sumona, TaskMonster.GARGOYLES, 10, 120, 195),
	GREATER_DEMONS_S(Master.Sumona, TaskMonster.GREATER_DEMONS, 10, 120, 185),
	GRIFOLAPINES_S(Master.Sumona, TaskMonster.GRIFOLAPINES, 8, 55, 75),
	GRIFOLAROOS_S(Master.Sumona, TaskMonster.GRIFOLAROOS, 8, 55, 75),
	GROTWORMS_S(Master.Sumona, TaskMonster.GROTWORMS, 15, 70, 115),
	HELLHOUNDS_S(Master.Sumona, TaskMonster.HELLHOUNDS, 10, 120, 185),
	IRON_DRAGONS_S(Master.Sumona, TaskMonster.IRON_DRAGONS, 7, 30, 85),
	JUNGLE_STRYKEWYRMS_S(Master.Sumona, TaskMonster.JUNGLE_STRYKEWYRMS, 12, 90, 120),
	KALPHITE_S(Master.Sumona, TaskMonster.KALPHITE, 10, 120, 189),
	KURASK_S(Master.Sumona, TaskMonster.KURASK, 15, 120, 185),
	MUTATED_JADINKOS_S(Master.Sumona, TaskMonster.MUTATED_JADINKOS, 10, 80, 130),
	NECHRYAEL_S(Master.Sumona, TaskMonster.NECHRYAEL, 10, 120, 185),
	RED_DRAGONS_S(Master.Sumona, TaskMonster.RED_DRAGONS, 5, 30, 79),
	SPIRITUAL_MAGES_S(Master.Sumona, TaskMonster.SPIRITUAL_MAGES, 10, 120, 185),
	SPIRITUAL_WARRIORS_S(Master.Sumona, TaskMonster.SPIRITUAL_WARRIORS, 10, 120, 185),
	TROLLS_S(Master.Sumona, TaskMonster.TROLLS, 10, 120, 191),
	TUROTH_S(Master.Sumona, TaskMonster.TUROTH, 15, 120, 185),
	VYREWATCH_S(Master.Sumona, TaskMonster.VYREWATCH, 10, 96, 105),
	WARPED_TORTOISES_S(Master.Sumona, TaskMonster.WARPED_TORTOISES, 10, 120, 185),


	ABERRANT_SPECTRES_D(Master.Duradel, TaskMonster.ABERRANT_SPECTRES, 10, 130, 200),
	ABYSSAL_DEMONS_D(Master.Duradel, TaskMonster.ABYSSAL_DEMONS, 15, 130, 200),
	AQUANITES_D(Master.Duradel, TaskMonster.AQUANITES, 9, 130, 200),
	BLACK_DEMONS_D(Master.Duradel, TaskMonster.BLACK_DEMONS, 10, 130, 200),
	BLACK_DRAGONS_D(Master.Duradel, TaskMonster.BLACK_DRAGONS, 9, 40, 80),
	BLOODVELD_D(Master.Duradel, TaskMonster.BLOODVELD, 20, 130, 200),
	DAGANNOTH_D(Master.Duradel, TaskMonster.DAGANNOTH, 10, 130, 200),
	DARK_BEASTS_D(Master.Duradel, TaskMonster.DARK_BEASTS, 15, 130, 200),
	DESERT_STRYKEWYRMS_D(Master.Duradel, TaskMonster.DESERT_STRYKEWYRMS, 11, 90, 140),
	DUST_DEVILS_D(Master.Duradel, TaskMonster.DUST_DEVILS, 10, 130, 200),
	FIRE_GIANTS_D(Master.Duradel, TaskMonster.FIRE_GIANTS, 10, 130, 200),
	FUNGAL_MAGI_D(Master.Duradel, TaskMonster.FUNGAL_MAGI, 8, 100, 200),
	GANODERMIC_CREATURES_D(Master.Duradel, TaskMonster.GANODERMIC_CREATURES, 6, 55, 70),
	GARGOYLES_D(Master.Duradel, TaskMonster.GARGOYLES, 10, 130, 200),
	GORAKS_D(Master.Duradel, TaskMonster.GORAKS, 5, 40, 80),
	GREATER_DEMONS_D(Master.Duradel, TaskMonster.GREATER_DEMONS, 11, 130, 200),
	GRIFOLAPINES_D(Master.Duradel, TaskMonster.GRIFOLAPINES, 10, 65, 80),
	GRIFOLAROOS_D(Master.Duradel, TaskMonster.GRIFOLAROOS, 10, 65, 80),
	GROTWORMS_D(Master.Duradel, TaskMonster.GROTWORMS, 10, 80, 120),
	HELLHOUNDS_D(Master.Duradel, TaskMonster.HELLHOUNDS, 9, 130, 200),
	ICE_STRYKEWYRMS_D(Master.Duradel, TaskMonster.ICE_STRYKEWYRMS, 8, 100, 200),
	IRON_DRAGONS_D(Master.Duradel, TaskMonster.IRON_DRAGONS, 9, 40, 80),
	JUNGLE_STRYKEWYRMS_D(Master.Duradel, TaskMonster.JUNGLE_STRYKEWYRMS, 10, 90, 120),
	KALPHITE_D(Master.Duradel, TaskMonster.KALPHITE, 10, 170, 250),
	MITHRIL_DRAGONS_D(Master.Duradel, TaskMonster.MITHRIL_DRAGONS, 7, 4, 11),
	MUTATED_JADINKOS_D(Master.Duradel, TaskMonster.MUTATED_JADINKOS, 8, 120, 200),
	NECHRYAEL_D(Master.Duradel, TaskMonster.NECHRYAEL, 10, 130, 200),
	SKELETAL_WYVERNS_D(Master.Duradel, TaskMonster.SKELETAL_WYVERNS, 5, 40, 80),
	SPIRITUAL_MAGES_D(Master.Duradel, TaskMonster.SPIRITUAL_MAGES, 10, 130, 200),
	STEEL_DRAGONS_D(Master.Duradel, TaskMonster.STEEL_DRAGONS, 7, 40, 80),
	SUQAHS_D(Master.Duradel, TaskMonster.SUQAHS, 5, 40, 80),
	VYREWATCH_D(Master.Duradel, TaskMonster.VYREWATCH, 8, 98, 118),
	WARPED_TERRORBIRDS_D(Master.Duradel, TaskMonster.WARPED_TERRORBIRDS, 9, 130, 200),
	WATERFIENDS_D(Master.Duradel, TaskMonster.WATERFIENDS, 10, 130, 200),


	ABERRANT_SPECTRES_K(Master.Kuradal, TaskMonster.ABERRANT_SPECTRES, 10, 140, 250),
	ABYSSAL_DEMONS_K(Master.Kuradal, TaskMonster.ABYSSAL_DEMONS, 12, 150, 250),
	AQUANITES_K(Master.Kuradal, TaskMonster.AQUANITES, 10, 120, 240),
	BLACK_DEMONS_K(Master.Kuradal, TaskMonster.BLACK_DEMONS, 10, 190, 250),
	BLACK_DRAGONS_K(Master.Kuradal, TaskMonster.BLACK_DRAGONS, 5, 40, 90),
	BLOODVELD_K(Master.Kuradal, TaskMonster.BLOODVELD, 10, 180, 250),
	BLUE_DRAGONS_K(Master.Kuradal, TaskMonster.BLUE_DRAGONS, 7, 120, 200),
	DAGANNOTH_K(Master.Kuradal, TaskMonster.DAGANNOTH, 10, 170, 240),
	DARK_BEASTS_K(Master.Kuradal, TaskMonster.DARK_BEASTS, 12, 150, 250),
	DESERT_STRYKEWYRMS_K(Master.Kuradal, TaskMonster.DESERT_STRYKEWYRMS, 9, 90, 160),
	DUST_DEVILS_K(Master.Kuradal, TaskMonster.DUST_DEVILS, 10, 150, 250),
	FIRE_GIANTS_K(Master.Kuradal, TaskMonster.FIRE_GIANTS, 10, 170, 250),
	GANODERMIC_CREATURES_K(Master.Kuradal, TaskMonster.GANODERMIC_CREATURES, 7, 70, 90),
	GARGOYLES_K(Master.Kuradal, TaskMonster.GARGOYLES, 12, 150, 250),
	GREATER_DEMONS_K(Master.Kuradal, TaskMonster.GREATER_DEMONS, 11, 150, 258),
	GRIFOLAPINES_K(Master.Kuradal, TaskMonster.GRIFOLAPINES, 8, 65, 80),
	GRIFOLAROOS_K(Master.Kuradal, TaskMonster.GRIFOLAROOS, 8, 65, 80),
	GROTWORMS_K(Master.Kuradal, TaskMonster.GROTWORMS, 10, 80, 160),
	HELLHOUNDS_K(Master.Kuradal, TaskMonster.HELLHOUNDS, 10, 130, 230),
	ICE_STRYKEWYRMS_K(Master.Kuradal, TaskMonster.ICE_STRYKEWYRMS, 12, 100, 220),
	IRON_DRAGONS_K(Master.Kuradal, TaskMonster.IRON_DRAGONS, 9, 40, 120),
	JUNGLE_STRYKEWYRMS_K(Master.Kuradal, TaskMonster.JUNGLE_STRYKEWYRMS, 8, 90, 130),
	KALPHITE_K(Master.Kuradal, TaskMonster.KALPHITE, 5, 170, 250),
	LIVING_ROCK_CREATURES_K(Master.Kuradal, TaskMonster.LIVING_ROCK_CREATURES, 10, 110, 185),
	MITHRIL_DRAGONS_K(Master.Kuradal, TaskMonster.MITHRIL_DRAGONS, 8, 20, 35),
	MUTATED_JADINKOS_K(Master.Kuradal, TaskMonster.MUTATED_JADINKOS, 8, 160, 220),
	NECHRYAEL_K(Master.Kuradal, TaskMonster.NECHRYAEL, 10, 140, 220),
	SKELETAL_WYVERNS_K(Master.Kuradal, TaskMonster.SKELETAL_WYVERNS, 5, 40, 90),
	SPIRITUAL_MAGES_K(Master.Kuradal, TaskMonster.SPIRITUAL_MAGES, 10, 150, 240),
	STEEL_DRAGONS_K(Master.Kuradal, TaskMonster.STEEL_DRAGONS, 9, 40, 100),
	SUQAHS_K(Master.Kuradal, TaskMonster.SUQAHS, 5, 50, 100),
	VOLCANIC_CREATURES_K(Master.Kuradal, TaskMonster.VOLCANIC_CREATURES, 7, 70, 110),
	VYREWATCH_K(Master.Kuradal, TaskMonster.VYREWATCH, 8, 90, 130),
	WARPED_TORTOISES_K(Master.Kuradal, TaskMonster.WARPED_TORTOISES, 8, 150, 240),
	WATERFIENDS_K(Master.Kuradal, TaskMonster.WATERFIENDS, 9, 170, 250);

	private Master master;
	private TaskMonster creature;
	private int[] minMax;
	private int weighting;

	private Task(Master master, TaskMonster creature, int weighting, int min, int max) {
		this.master = master;
		this.creature = creature;
		minMax = new int[] { min, max };
		this.weighting = weighting;
	}

	public int getMin() {
		return minMax[0];
	}

	public int getMax() {
		return minMax[1];
	}

	public int getWeighting() {
		return weighting;
	}

	public TaskMonster getMonster() {
		return creature;
	}

	public Master getMaster() {
		return master;
	}
}