package com.rs.tools.old;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.lib.io.InputStream;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapXTEAs;

public class NPCSpawnsDumper {

	private static int writtenCount;

	public static final void main(String[] args) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter("data/npcs/unpackedSpawnsList.txt", true));
		Logger.log("Launcher", "Initing Cache...");
		//Cache.init();
		Logger.log("Launcher", "Initing Data File...");
		MapXTEAs.loadKeys();
		for (int regionId = 0; regionId < 20000; regionId++) {
			if (new File("data/npcs/packedSpawns/" + regionId + ".ns").exists())
				continue;
			dumpRegionNPCs(regionId, out);
		}
		out.close();
		System.out.println("found " + writtenCount + " npc spawns on cache.");

	}

	public static final void dumpRegionNPCs(int regionId, BufferedWriter writer) throws IOException {
		writer.flush();
		int regionX = (regionId >> 8) * 64;
		int regionY = (regionId & 0xff) * 64;
		int npcSpawnsContainerId = Cache.STORE.getIndex(IndexType.MAPS).getArchiveId("n" + ((regionX >> 3) / 8) + "_" + ((regionY >> 3) / 8));
		if (npcSpawnsContainerId == -1)
			return;
		byte[] npcSpawnsContainerData = Cache.STORE.getIndex(IndexType.MAPS).getFile(npcSpawnsContainerId, 0, MapXTEAs.getMapKeys(regionId));
		if (npcSpawnsContainerData == null)
			return;
		System.out.println(regionId);
		InputStream stream = new InputStream(npcSpawnsContainerData);
		while (stream.getRemaining() > 0) {
			int hash = stream.readUnsignedShort();
			int npcId = stream.readUnsignedShort();
			int plane = hash >> 758085070;
			int localX = (0x1f92 & hash) >> -585992921;
			int x = regionX + localX;
			int localY = 0x3f & hash;
			int y = regionY + localY;
			writer.newLine();
			writer.write(npcId + " - " + x + " " + y + " " + plane);
			writer.flush();
			writtenCount++;
			System.out.println("123");
		}
	}

}
