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

import com.rs.lib.game.Item;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class ItemWeights {

	private final static HashMap<Integer, Double> itemWeights = new HashMap<Integer, Double>();
	private final static String PACKED_PATH = "data/items/packedWeights.dat";
	private final static String UNPACKED_PATH = "data/items/unpackedWeights.txt";

	private static final int[] NEGATIVE_WEIGHT_ITEMS = { 88, 10553, 10069, 10071, 24210, 24208, 24206, 14936, 14938, 24560, 24561, 24562, 24563, 24564 };

	@ServerStartupEvent
	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedItemWeights();
		else
			loadUnpackedItemWeights();
	}

	public static final double getWeight(Item item, boolean equiped) {
		if (item.getDefinitions().isNoted())
			return 0;
		Double weight = itemWeights.get(item.getId());
		if (weight == null)
			return 0;
		if (equiped) {
			for (int i : NEGATIVE_WEIGHT_ITEMS)
				if (i == item.getId())
					return -weight;
		}
		return weight;
	}

	private static void loadPackedItemWeights() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				itemWeights.put(buffer.getShort() & 0xffff, buffer.getDouble());
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadUnpackedItemWeights() {
		Logger.log(ItemWeights.class, "Packing item weights...");
		try {
			BufferedReader in = new BufferedReader(new FileReader(UNPACKED_PATH));
			@SuppressWarnings("resource")
			DataOutputStream out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				line = line.replace("﻿", "");
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length < 2) {
					in.close();
					throw new RuntimeException("Invalid list for item weight line: " + line);
				}
				int itemId = Integer.valueOf(splitedLine[0]);
				double weight = Double.valueOf(splitedLine[1]);
				out.writeShort(itemId);
				out.writeDouble(weight);
				itemWeights.put(itemId, weight);
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
