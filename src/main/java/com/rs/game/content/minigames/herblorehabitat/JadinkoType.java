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
package com.rs.game.content.minigames.herblorehabitat;

import com.rs.game.content.Effect;
import com.rs.game.content.skills.farming.PatchLocation;
import com.rs.game.content.skills.farming.ProduceType;
import com.rs.game.model.entity.player.Player;

public enum JadinkoType {
	COMMON(13108, 8415, 1, false, null, ProduceType.Torstol, null, null),
	SHADOW(-1, -1, -1, false, HabitatFeature.Abandoned_house, ProduceType.Red_blossom, ProduceType.Torstol, ProduceType.Torstol),
	IGNEOUS(13132, 8435, 2, false, HabitatFeature.Thermal_vent, ProduceType.Blue_blossom, ProduceType.Lergberry, ProduceType.Orange),
	CANNIBAL(13144, 8445, 2, true, HabitatFeature.Tall_grass, ProduceType.Green_blossom, ProduceType.Kalferberry, ProduceType.Torstol),
	AQUATIC(13132, 8435, 1, true, HabitatFeature.Pond, ProduceType.Red_blossom, ProduceType.Kalferberry, ProduceType.Apple),
	AMPHIBIOUS(13120, 8425, 1, false, HabitatFeature.Pond, ProduceType.Blue_blossom, ProduceType.Lergberry, ProduceType.Torstol),
	CARRION(13144, 8445, 1, false, HabitatFeature.Boneyard, ProduceType.Green_blossom, ProduceType.Kalferberry, ProduceType.Torstol),
	DISEASED(-1, -1, -1, false, HabitatFeature.Boneyard, ProduceType.Green_blossom, ProduceType.Torstol, ProduceType.Banana),
	CAMOUFLAGED(-1, -1, -1, true, HabitatFeature.Standing_stones, ProduceType.Green_blossom, ProduceType.Lergberry, ProduceType.Torstol),
	DRACONIC(13120, 8425, 2, true, HabitatFeature.Dark_pit, ProduceType.Red_blossom, ProduceType.Lergberry, ProduceType.Torstol),
	SARADOMIN(13156, 8452, 1, true, HabitatFeature.Pond, ProduceType.Blue_blossom, ProduceType.Lergberry, ProduceType.Torstol),
	GUTHIX(13156, 8452, 2, true, HabitatFeature.Tall_grass, ProduceType.Green_blossom, ProduceType.Lergberry, ProduceType.Torstol),
	ZAMORAK(13156, 8452, 3, true, HabitatFeature.Dark_pit, ProduceType.Red_blossom, ProduceType.Kalferberry, ProduceType.Torstol);

	private int npcId;
	private int varbit;
	private int varbitValue;
	private boolean juju;
	private HabitatFeature feature;
	private ProduceType blossom;
	private ProduceType bush;
	private ProduceType tree;

	private JadinkoType(int npcId, int varbit, int varbitValue, boolean juju, HabitatFeature feature, ProduceType blossom, ProduceType bush, ProduceType tree) {
		this.npcId = npcId;
		this.varbit = varbit;
		this.varbitValue = varbitValue;
		this.juju = juju;
		this.feature = feature;
		this.blossom = blossom;
		this.bush = bush;
		this.tree = tree;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getVarbit() {
		return varbit;
	}

	public int getVarbitValue() {
		return varbitValue;
	}

	public boolean shouldSend(Player player) {
		if (juju && !player.hasEffect(Effect.JUJU_HUNTER))
			return false;

		if (feature == null) {
			if (player.getHabitatFeature() == null)
				return false;
		} else if (player.getHabitatFeature() != feature)
			return false;

		if (!checkProduce(player, blossom, PatchLocation.Herblore_Habitat_flower) || !checkProduce(player, bush, PatchLocation.Herblore_Habitat_bush) || !checkProduce(player, tree, PatchLocation.Herblore_Habitat_fruit_tree))
			return false;

		return true;
	}

	private static boolean checkProduce(Player player, ProduceType type, PatchLocation location) {
		if (type == null)
			return true;
		if (type == ProduceType.Torstol) {
			if (player.isGrowing(location, null))
				return false;
		} else if (!player.isGrowing(location, type))
			return false;
		return true;
	}

	public static void updateGroup(Player player, JadinkoType... types) {
		boolean rendered = false;
		for (JadinkoType type : types)
			if (type.shouldSend(player)) {
				player.getVars().setVarBit(type.varbit, type.varbitValue);
				rendered = true;
			}
		if (!rendered)
			player.getVars().setVarBit(types[0].varbit, 0);
	}

}
