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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;

import com.rs.Settings;
import com.rs.lib.game.Item;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class ItemExamines {

	private final static HashMap<Integer, String> itemExamines = new HashMap<Integer, String>();
	private final static String PACKED_PATH = "data/items/packedExamines.e";
	private final static String UNPACKED_PATH = "data/items/unpackedExamines.txt";

	@ServerStartupEvent
	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedItemExamines();
		else
			loadUnpackedItemExamines();
	}

	public static final String getExamine(Item item) {
		if (item.getAmount() >= 100000)
			return item.getAmount() + " x " + item.getDefinitions().getName() + ".";
		if (item.getDefinitions().isNoted())
			return "Swap this note at any bank for the equivalent item.";
		String examine = itemExamines.get(item.getId());
		if (examine != null)
			return examine + (Settings.getConfig().isDebug() ? " Tradable: " + item.getDefinitions().canExchange() : "");
		return "It's an " + item.getDefinitions().getName() + "." + (Settings.getConfig().isDebug() ? " Tradable: " + item.getDefinitions().canExchange() : "");
	}

	private static void loadPackedItemExamines() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining())
				itemExamines.put(buffer.getShort() & 0xffff, readAlexString(buffer));
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	@SuppressWarnings("resource")
	private static void loadUnpackedItemExamines() {
		Logger.log("ItemExamines", "Packing item examines...");
		try {
			BufferedReader in = new BufferedReader(new FileReader(UNPACKED_PATH));
			DataOutputStream out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				line = line.replace("﻿", "");
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length < 2)
					throw new RuntimeException("Invalid list for item examine line: " + line);
				int itemId = Integer.valueOf(splitedLine[0]);
				if (splitedLine[1].length() > 255)
					continue;
				out.writeShort(itemId);
				writeAlexString(out, splitedLine[1]);
				itemExamines.put(itemId, splitedLine[1]);
			}
			in.close();
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String readAlexString(ByteBuffer buffer) {
		int count = buffer.get() & 0xff;
		byte[] bytes = new byte[count];
		buffer.get(bytes, 0, count);
		return new String(bytes);
	}

	public static void writeAlexString(DataOutputStream out, String string) throws IOException {
		byte[] bytes = string.getBytes();
		out.writeByte(bytes.length);
		out.write(bytes);
	}
}
