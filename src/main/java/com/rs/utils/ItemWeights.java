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
package com.rs.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class ItemWeights {

	private static Map<Integer, Double> WEIGHTS = new HashMap<>();
	private final static String PATH = "./data/items/weight.json";

	@ServerStartupEvent
	public static final void init() throws JsonIOException, IOException {
		if (new File(PATH).exists())
			WEIGHTS = JsonFileManager.loadJsonFile(new File(PATH), new TypeToken<Map<Integer, Double>>(){}.getType());
		else
			System.err.println("No item weight file found at " + PATH + "!");
	}

	public static final double getWeight(Item item, boolean equipped) {
		if (item.getDefinitions().isNoted())
			return 0;
		Double weight = WEIGHTS.get(item.getId());
		if (weight == null)
			return 0;
		if (weight < 0.0) {
			if (equipped)
				return -weight;
			return 0;
		}
		return weight;
	}
}
