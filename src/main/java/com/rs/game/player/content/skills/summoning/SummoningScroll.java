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
package com.rs.game.player.content.skills.summoning;

import java.util.HashMap;
import java.util.Map;

import com.rs.lib.game.Item;

/**
 * Represents all of the summoning scrolls.
 *
 * @author Byte Me
 */

public enum SummoningScroll {

	/**
	 * Represents the all the scrolls.
	 */
	HOWL_SCROLL(12425, 1, 0.1, new Item(12047)), DREADFOWL_STRIKE_SCROLL(12445, 4, 0.1, new Item(12043)), FETCH_CASKET_SCROLL(19621, 4, 0.0, new Item(-1)), EGG_SPAWN_SCROLL(12428, 4, 10.2, new Item(12059)), SLIME_SPRAY_SCROLL(12459, 13, 0.2,
			new Item(12019)), STONY_SHELL_SCROLL(12533, 16, 0.2, new Item(12009)), PESTER_SCROLL(12838, 17, 0.5, new Item(12778)), ELECTRIC_LASH_SCROLL(12460, 18, 0.4, new Item(12049)), VENOM_SHOT_SCROLL(12432, 19, 0.9, new Item(12055)), FIREBALL_ASSAULT_SCROLL(
					12839, 22, 1.1, new Item(12808)), CHEESE_FEAST_SCROLL(12430, 23, 2.3, new Item(12067)), SANDSTORM_SCROLL(12446, 25, 2.5, new Item(12064)), GENERATE_COMPOST_SCROLL(12440, 28, 0.6, new Item(12091)), EXPLODE_SCROLL(12834, 29, 2.9, new Item(
							12800)), VAMPIRE_TOUCH_SCROLL(12447, 31, 1.5, new Item(12053)), INSANE_FEROCITY_SCROLL(12433, 32, 1.6, new Item(12065)), MULTICHOP_SCROLL(12429, 33, 0.7, new Item(12021)), CALL_TO_ARMS_SCROLL(12443, 34, 0.7, new Item(12818)), // Need
	// To
	// Add
	// 12781,
	// 12798,
	// 12814
	BRONZE_BULL_RUSH_SCROLL(12461, 36, 3.6, new Item(12073)), UNBURDEN_SCROLL(12431, 40, 0.6, new Item(12087)), HERBCALL_SCROLL(12422, 41, 0.8, new Item(12071)), EVIL_FLAMES_SCROLL(12448, 42, 2.1, new Item(12051)), PETRIFYING_GAZE_SCROLL(12458, 43,
			0.9, new Item(12095)), // TODO need to add 12097, 12099, 12101,
	// 12103, 12105,
	// 12107
	IRON_BULL_RUSH_SCROLL(12462, 46, 4.6, new Item(12075)), IMMENSE_HEAT_SCROLL(12829, 46, 2.3, new Item(12816)), THIEVING_FINGERS_SCROLL(12426, 47, 0.9, new Item(12041)), BLOOD_DRAIN_SCROLL(12444, 49, 2.4, new Item(12061)), TIRELESS_RUN_SCROLL(
			12441, 52, 0.8, new Item(12007)), ABYSSAL_DRAIN_SCROLL(12454, 54, 1.1, new Item(12035)), DISSOLVE_SCROLL(12453, 55, 5.5, new Item(12027)), FISH_RAIN_SCROLL(12424, 56, 1.1, new Item(12531)), STEEL_BULL_RUSH_SCROLL(12463, 56, 5.6,
					new Item(12077)), AMBUSH_SCROLL(12836, 57, 5.7, new Item(12812)), RENDING_SCROLL(12840, 57, 5.7, new Item(12784)), GOAD_SCROLL(12835, 57, 5.7, new Item(12710)), DOOMSPHERE_SCROLL(12455, 58, 5.8, new Item(12023)), DUST_CLOUD_SCROLL(12468,
							61, 3.1, new Item(12085)), ABYSSAL_STEALTH_SCROLL(12427, 62, 1.9, new Item(12037)), OPHIDIAN_INCUBATION_SCROLL(12436, 63, 3.2, new Item(12015)), POISONOUS_BLAST_SCROLL(12467, 64, 3.2, new Item(12045)), MITHRIL_BULL_RUSH_SCROLL(12464, 66,
									6.6, new Item(12079)), TOAD_BARK_SCROLL(12452, 66, 1.0, new Item(12123)), TESTUDO_SCROLL(12439, 67, 0.7, new Item(12031)), SWALLOW_WHOLE_SCROLL(12438, 68, 1.4, new Item(12029)), FRUITFALL_SCROLL(12423, 69, 1.4, new Item(12033)), FAMINE_SCROLL(
											12830, 70, 1.4, new Item(12820)), ARCTIC_BLAST_SCROLL(12451, 71, 1.1, new Item(12057)), RISE_FROM_THE_ASHES_SCROLL(14622, 72, 8.0, new Item(14623)), VOLCANIC_STRENGTH_SCROLL(12826, 73, 7.3, new Item(12792)), CRUSHING_CLAW_SCROLL(12449,
													74, 3.7, new Item(12069)), MANTIS_STRIKE_SCROLL(12459, 75, 3.7, new Item(12011)), INFERNO_SCROLL(12841, 76, 1.5, new Item(12782)), ADAMANT_BULL_RUSH_SCROLL(12465, 76, 7.6, new Item(12081)), DEADLY_CLAW_SCROLL(12831, 77, 11.4, new Item(
															12794)), ACORN_MISSILE_SCROLL(12457, 78, 1.6, new Item(12013)), TITANS_CONSTITUTION_SCROLL(12824, 79, 7.9, new Item(12802)), // TODO
	// add
	// 12806,
	// and
	// 12804
	REGROWTH_SCROLL(12442, 80, 1.6, new Item(12025)), SPIKE_SHOT_SCROLL(12456, 83, 4.1, new Item(12017)), EBON_THUNDER_SCROLL(12837, 83, 8.3, new Item(12788)), SWAMP_PLAGUE_SCROLL(12832, 85, 4.1, new Item(12776)), RUNE_BULL_RUSH_SCROLL(12466, 86,
			8.6, new Item(12083)), HEALING_AURA_SCROLL(12434, 88, 1.8, new Item(12039)), BOIL_SCROLL(12833, 89, 8.9, new Item(12786)), MAGIC_FOCUS_SCROLL(12437, 92, 4.6, new Item(12089)), ESSENCE_SHIPMENT_SCROLL(12827, 93, 1.9, new Item(12796)), IRON_WITHIN_SCROLL(
					12828, 95, 4.7, new Item(12822)), WINTER_STORAGE_SCROLL(12435, 96, 4.8, new Item(12093)), STEEL_OF_LEGENDS_SCROLL(12825, 99, 4.9, new Item(12790));

	/**
	 * Gets a summoning pouch object from the mapping.
	 *
	 * @param pouchId
	 *            The pouch item id.
	 * @return The {@code SummoningPouch} {@code Object}, <br>
	 *         or {@code null} if the pouch didn't exist.
	 */
	public static SummoningScroll get(int itemId) {
		return SCROLLS.get(itemId);
	}

	/**
	 * The mapping.
	 */
	private static final Map<Integer, SummoningScroll> SCROLLS = new HashMap<>();

	/**
	 * Populate the mapping.
	 */
	static {
		for (SummoningScroll scroll : SummoningScroll.values())
			SCROLLS.put(scroll.itemId, scroll);
	}

	/**
	 * The item id.
	 */
	private final int itemId;

	/**
	 * The level required.
	 */
	private final int levelRequired;

	/**
	 * The experience gained.
	 */
	private final double experience;

	/**
	 * The pouch to transform.
	 */
	private final Item pouch;

	/**
	 * Constructs a new {@code SummoningScroll} {@code Object}.
	 *
	 * @param itemId
	 *            The item id.
	 * @param levelRequired
	 *            The level required.
	 * @param experience
	 *            The experience gained.
	 * @param pouch
	 *            The pouch item to transform.
	 */
	private SummoningScroll(int itemId, int levelRequired, double experience, Item pouch) {
		this.itemId = itemId;
		this.levelRequired = levelRequired;
		this.experience = experience;
		this.pouch = pouch;
	}

	/**
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * @return the levelRequired
	 */
	public int getLevelRequired() {
		return levelRequired;
	}

	/**
	 * @return the experience
	 */
	public double getExperience() {
		return experience;
	}

	/**
	 * @return the pouch
	 */
	public Item getPouch() {
		return pouch;
	}

}
