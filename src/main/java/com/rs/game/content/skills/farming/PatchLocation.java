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
package com.rs.game.content.skills.farming;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ObjectDefinitions;

public enum PatchLocation {
	Taverly_tree(8388, PatchType.TREE),
	Falador_tree(8389, PatchType.TREE),
	Varrock_tree(8390, PatchType.TREE),
	Lumbridge_tree(8391, PatchType.TREE),
	Gnome_Stronghold_tree(19147, PatchType.TREE),

	Gnome_Stronghold_fruit_tree(7962, PatchType.FRUIT_TREE),
	Tree_Gnome_Village_fruit_tree(7963, PatchType.FRUIT_TREE),
	Brimhaven_fruit_tree(7964, PatchType.FRUIT_TREE),
	Catherby_fruit_tree(7965, PatchType.FRUIT_TREE),
	Lletya_fruit_tree(28919, PatchType.FRUIT_TREE),
	Herblore_Habitat_fruit_tree(56667, PatchType.FRUIT_TREE),

	Falador_allotment_north(8550, PatchType.ALLOTMENT),
	Falador_allotment_south(8551, PatchType.ALLOTMENT),
	Catherby_allotment_north(8552, PatchType.ALLOTMENT),
	Catherby_allotment_south(8553, PatchType.ALLOTMENT),
	Ardougne_allotment_north(8554, PatchType.ALLOTMENT),
	Ardougne_allotment_south(8555, PatchType.ALLOTMENT),
	Canifis_allotment_north(8556, PatchType.ALLOTMENT),
	Canifis_allotment_south(8557, PatchType.ALLOTMENT),
	Harmony_allotment(21950, PatchType.ALLOTMENT),
	Burthorpe_potato_patch(66572, PatchType.ALLOTMENT),

	Yanille_hops(8173, PatchType.HOP),
	Entrana_hops(8174, PatchType.HOP),
	Lumbridge_hops(8175, PatchType.HOP),
	Seers_Village_hops(8176, PatchType.HOP),

	Falador_flower(7847, PatchType.FLOWER),
	Catherby_flower(7848, PatchType.FLOWER),
	Ardougne_flower(7849, PatchType.FLOWER),
	Canifis_flower(7850, PatchType.FLOWER),
	Wilderness_flower(37988, PatchType.FLOWER),

	Champions_Guild_bush(7577, PatchType.BUSH),
	Rimmington_bush(7578, PatchType.BUSH),
	Etceteria_bush(7579, PatchType.BUSH),
	Ardougne_bush(7580, PatchType.BUSH),

	Falador_herbs(8150, PatchType.HERB),
	Catherby_herbs(8151, PatchType.HERB),
	Ardougne_herbs(8152, PatchType.HERB),
	Canifis_herbs(8153, PatchType.HERB),
	Trollheim_herbs(18816, PatchType.HERB),

	Canifis_mushrooms(8337, PatchType.MUSHROOM),

	Draynor_belladonna(7572, PatchType.BELLADONNA),

	Herblore_Habitat_west_herbs(56682, PatchType.VINE_HERB),
	Herblore_Habitat_east_herbs(56683, PatchType.VINE_HERB),
	Herblore_Habitat_bush(56562, PatchType.VINE_BUSH),
	Herblore_Habitat_flower(56685, PatchType.VINE_FLOWER),
	
	Draynor_manor_evil_turnip(23760, PatchType.EVIL_TURNIP),

	Al_Kharid_cactus(7771, PatchType.CACTUS),

	Karamja_calquat(7807, PatchType.CALQUAT),

	Etceteria_spirit_tree(8382, PatchType.SPIRIT),
	Brimhaven_spirit_tree(8383, PatchType.SPIRIT),
	Port_Sarim_spirit_tree(8338, PatchType.SPIRIT),

	Falador_compost(7836, PatchType.COMPOST),
	Canifis_compost(7838, PatchType.COMPOST),
	Ardougne_compost(7839, PatchType.COMPOST),
	Catherby_compost(7837, PatchType.COMPOST),
	Taverly_compost(66577, PatchType.COMPOST),
	Herblore_habitat_compost(56684, PatchType.COMPOST);

	//TODO
	//Jade vine

	public static Map<Integer, PatchLocation> MAP = new HashMap<>();

	public static PatchLocation forObject(int objectId) {
		return MAP.get(objectId);
	}

	static {
		for (PatchLocation information : PatchLocation.values())
			MAP.put(information.objectId, information);
	}

	public int objectId;
	public int varBit;
	public PatchType type;

	private PatchLocation(int objectId, PatchType type) {
		this.objectId = objectId;
		varBit = ObjectDefinitions.getDefs(objectId).varpBit;
		this.type = type;
	}
}
