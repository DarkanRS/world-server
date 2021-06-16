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

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class NPCExamines {

	private final static HashMap<Integer, String> itemExamines = new HashMap<Integer, String>();
	private final static String PACKED_PATH = "data/npcs/packedExamines.e";
	private final static String UNPACKED_PATH = "data/npcs/npcExamines.txt";

	@ServerStartupEvent
	public static final void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedItemExamines();
		else
			loadUnpackedItemExamines();
	}

	public static final String getExamine(NPC npc, Player player) {
		String examine = itemExamines.get(npc.getId());
		if (examine != null)
			return examine;
		return "It's " + Utils.addArticle(npc.getDefinitions().getName(player.getVars())).toLowerCase() + ".";
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
				String[] splitedLine = line.split("-", 2);
				String examine = line.substring(line.indexOf("-")+1, line.length());
				if (splitedLine.length < 2)
					throw new RuntimeException("Invalid list for item examine line: " + line);
				int itemId = Integer.valueOf(splitedLine[0]);
				if (examine.length() > 255)
					continue;
				out.writeShort(itemId);
				writeAlexString(out, examine);
				itemExamines.put(itemId, examine);
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
