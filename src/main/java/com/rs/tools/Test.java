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
import com.rs.cache.loaders.IdentiKitDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
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
import java.util.Arrays;
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

		short pin = -26352;
		System.out.println(getPin((byte) 0, (byte) 1, (byte) 9, (byte) 9));
		System.out.println(Arrays.toString(decodePin(getPin((byte) 0, (byte) 1, (byte) 9, (byte) 9))));
		System.out.println(Arrays.toString(decodePin(pin)));
	}

	public static short getPin(byte num1, byte num2, byte num3, byte num4) {
		return (short) ((num1 << 12) + (num2 << 8) + (num3 << 4) + num4);
	}

	public static byte[] decodePin(short encodedPin) {
		byte num4 = (byte) (encodedPin & 0x000F);
		byte num3 = (byte) ((encodedPin >> 4) & 0x000F);
		byte num2 = (byte) ((encodedPin >> 8) & 0x000F);
		byte num1 = (byte) ((encodedPin >> 12) & 0x000F);

		return new byte[]{num1, num2, num3, num4};
	}

}
