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
package com.rs.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.rs.Settings;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class ItemExamines {
	private static Map<Integer, String> EXAMINES = new HashMap<>();
	private final static String PATH = "./data/items/examines.json";

	@ServerStartupEvent
	public static final void init() throws JsonIOException, IOException {
		if (new File(PATH).exists())
			EXAMINES = JsonFileManager.loadJsonFile(new File(PATH), new TypeToken<Map<Integer, String>>(){}.getType());
		else
			System.err.println("No item examines file found at " + PATH + "!");
	}

	public static final String getExamine(Item item) {
		if (item.getAmount() >= 100000)
			return item.getAmount() + " x " + item.getDefinitions().getName() + ".";
		if (item.getDefinitions().isNoted())
			return "Swap this note at any bank for the equivalent item.";
		String examine = EXAMINES.get(item.getId());
		if (examine != null)
			return examine + (Settings.getConfig().isDebug() ? " Tradable: " + item.getDefinitions().canExchange() : "");
		return "It's an " + item.getDefinitions().getName() + "." + (Settings.getConfig().isDebug() ? " Tradable: " + item.getDefinitions().canExchange() : "");
	}
}
