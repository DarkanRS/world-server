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
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.combat.special_attacks.SpecialAttacks;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.lib.util.Utils;
import com.rs.utils.json.ControllerAdapter;

import java.io.IOException;
import java.util.Date;

public class Test {

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

		SpecialAttacks.loadSpecs();
		for (int i = 0;i < Utils.getItemDefinitionsSize();i++) {
			ItemDefinitions def = ItemDefinitions.getDefs(i);
			if (def.getParamVal(687) == 1 && SpecialAttacks.getSpec(i) == null)
				System.out.println("Missing spec coded for: " + i + " - " + def.getName());
		}
	}

}
