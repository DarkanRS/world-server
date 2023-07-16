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
package com.rs.tools;

import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.InventoryDefinitions;
import com.rs.cache.loaders.cs2.CS2Definitions;
import com.rs.cache.loaders.cs2.CS2Script;
import com.rs.game.model.entity.player.Bank;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.utils.json.ControllerAdapter;

import java.io.IOException;
import java.util.Date;

public class BankSpaceIncreaser {

	public static void main(String[] args) throws IOException {
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());

		final int MAX_SIZE = Bank.MAX_BANK_SIZE;
		final int MEMS_SIZE = MAX_SIZE-87;

		InventoryDefinitions def = InventoryDefinitions.getContainer(95);
		int old = def.maxSize;
		def.maxSize = MAX_SIZE;
		def.write(Cache.STORE);
		System.out.println("Replaced container max size from " + old + " to " + MAX_SIZE);

		CS2Script script = CS2Definitions.getScript(1465);
		replaceOpValue(script.intOpValues, 40, MEMS_SIZE);
		replaceOpValue(script.intOpValues, 59, MEMS_SIZE);
		script.write(Cache.STORE);

		script = CS2Definitions.getScript(1467);
		replaceOpValue(script.intOpValues, 28, MAX_SIZE);
		script.write(Cache.STORE);

		script = CS2Definitions.getScript(1665);
		replaceOpValue(script.intOpValues, 19, MEMS_SIZE);
		replaceOpValue(script.intOpValues, 32, MEMS_SIZE);
		script.write(Cache.STORE);

		script = CS2Definitions.getScript(1329);
		replaceOpValue(script.intOpValues, 0, MAX_SIZE);
		replaceOpValue(script.intOpValues, 1, MEMS_SIZE);
		script.write(Cache.STORE);

		script = CS2Definitions.getScript(1248);
		replaceOpValue(script.intOpValues, 0, MAX_SIZE);
		script.write(Cache.STORE);


		Cache.STORE.getIndex(IndexType.CONFIG).rewriteTable();
		Cache.STORE.getIndex(IndexType.CS2_SCRIPTS).rewriteTable();
	}

	public static void replaceOpValue(int[] values, int index, int value) {
		int old = values[index];
		values[index] = value;
		System.out.println("Replaced intOpValue#"+index+" (" + old + ") with " + value);
	}
}
