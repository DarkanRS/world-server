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
package com.rs.game.content.skills.runecrafting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.skills.magic.Rune;
import com.rs.game.model.entity.player.Equipment;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class RunecraftingCombinations {

	public static final int BINDING_NECKLACE = 5521;

	public static final int STEAM_RUNE = 4694;
	public static final int MIST_RUNE = 4695;
	public static final int DUST_RUNE = 4696;
	public static final int SMOKE_RUNE = 4697;
	public static final int MUD_RUNE = 4698;
	public static final int LAVA_RUNE = 4699;

	public static final int AIR_ALTAR = 2478;
	public static final int WATER_ALTAR = 2480;
	public static final int EARTH_ALTAR = 2481;
	public static final int FIRE_ALTAR = 2482;

	public enum CombinationRunes {
		MIST(new int[] {AIR_ALTAR, WATER_ALTAR}, new int[] {Runecrafting.WATER_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.WATER.id(), Rune.AIR.id() }, MIST_RUNE, 6, new double[]{8.0, 8.5}),
		DUST(new int[] {AIR_ALTAR, EARTH_ALTAR}, new int[] {Runecrafting.EARTH_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.EARTH.id(), Rune.AIR.id() }, DUST_RUNE, 10, new double[]{8.3, 9.0}),
		MUD(new int[] {WATER_ALTAR, EARTH_ALTAR}, new int[] {Runecrafting.EARTH_TALISMAN, Runecrafting.WATER_TALISMAN}, new int[] { Rune.EARTH.id(), Rune.WATER.id() }, MUD_RUNE, 13, new double[]{9.3, 9.5}),
		SMOKE(new int[] {AIR_ALTAR, FIRE_ALTAR}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.AIR_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.AIR.id() }, SMOKE_RUNE, 15, new double[]{8.5, 9.5}),
		STEAM(new int[] {WATER_ALTAR, FIRE_ALTAR}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.WATER_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.WATER.id() }, STEAM_RUNE, 19, new double[]{9.3, 10.0}),
		LAVA(new int[] {EARTH_ALTAR, FIRE_ALTAR}, new int[] {Runecrafting.FIRE_TALISMAN, Runecrafting.EARTH_TALISMAN}, new int[] { Rune.FIRE.id(), Rune.EARTH.id() }, LAVA_RUNE, 23, new double[]{10.0, 10.5});

		private int[] altars;
		private int[] talismans;
		private int[] runes;
		private int combinationRune;
		private int level;
		private double[] xp;

		private CombinationRunes(int[] altars, int[] talismans, int[] runes, int combinationRune, int level, double[] xp) {
			this.altars = altars;
			this.talismans = talismans;
			this.runes = runes;
			this.combinationRune = combinationRune;
			this.level = level;
			this.xp = xp;
		}

		public int[] getAltars() {
			return altars;
		}

		public int[] getTalismans() {
			return talismans;
		}

		public int[] getRunes() {
			return runes;
		}

		public int getCombinationRune() {
			return combinationRune;
		}

		public int getLevel() {
			return level;
		}

		public double[] getXP() {
			return xp;
		}
	}

	public static ItemOnObjectHandler craft = new ItemOnObjectHandler(new Object[] { AIR_ALTAR, WATER_ALTAR, EARTH_ALTAR, FIRE_ALTAR }, e -> {
		for (CombinationRunes cr : CombinationRunes.values())
			for (int i = 0; i < cr.altars.length; i++)
				if (e.getObject().getId() == cr.altars[i] && (e.getItem().getId() == cr.getTalismans()[i] || e.getItem().getId() == cr.getRunes()[i]))
					if (e.getPlayer().getSkills().getLevel(Constants.RUNECRAFTING) >= cr.getLevel() && e.getPlayer().getInventory().getItems().getNumberOf(cr.getTalismans()[i]) > 0) {
						int maxCraftable = 0;
						int pureEss = e.getPlayer().getInventory().getItems().getNumberOf(Runecrafting.PURE_ESS);
						int inputRune = e.getPlayer().getInventory().getItems().getNumberOf(cr.getRunes()[i]);
						double xp = cr.getXP()[i];

						if (pureEss == 0) {
							e.getPlayer().simpleDialogue("You don't have enough pure essence.");
							return;
						}

						if (inputRune == 0) {
							e.getPlayer().simpleDialogue("You don't have enough " + ItemDefinitions.getDefs(cr.getRunes()[i]).getName() + "s.");
							return;
						}

						if (inputRune >= pureEss)
							maxCraftable = pureEss;
						else
							maxCraftable = inputRune;

						if (!e.getPlayer().isCastMagicImbue())
							e.getPlayer().getInventory().deleteItem(cr.getTalismans()[i], 1);

						e.getPlayer().getInventory().deleteItem(Runecrafting.PURE_ESS, maxCraftable);
						e.getPlayer().getInventory().deleteItem(cr.getRunes()[i], maxCraftable);

						if (Runecrafting.hasRcingSuit(e.getPlayer()))
							xp *= 1.025;

						if (e.getPlayer().getEquipment().getAmuletId() == BINDING_NECKLACE) {
							e.getPlayer().bindingNecklaceCharges--;
							if (e.getPlayer().bindingNecklaceCharges <= 0) {
								e.getPlayer().getEquipment().deleteSlot(Equipment.NECK);
								e.getPlayer().sendMessage("Your binding necklace disintegrates.");
								e.getPlayer().bindingNecklaceCharges = 15;
							};
							e.getPlayer().sendMessage("You bind the temple's power into " + ItemDefinitions.getDefs(cr.getCombinationRune()).getName() + "s.");
							e.getPlayer().getInventory().addItem(cr.getCombinationRune(), maxCraftable);
							e.getPlayer().getSkills().addXp(Constants.RUNECRAFTING, xp*maxCraftable);

						} else {
							e.getPlayer().sendMessage("You attempt to bind " + ItemDefinitions.getDefs(cr.getCombinationRune()).getName() + "s.");
							e.getPlayer().getInventory().addItem(cr.getCombinationRune(), maxCraftable/2);
							e.getPlayer().getSkills().addXp(Constants.RUNECRAFTING, xp*(maxCraftable/2));
						}
						e.getPlayer().setNextSpotAnim(new SpotAnim(186));
						e.getPlayer().setNextAnimation(new Animation(791));
						e.getPlayer().lock(5);
					}
	});
}
