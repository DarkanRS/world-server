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
package com.rs.tools.old;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class NPCDropConverter {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		RandomAccessFile in = new RandomAccessFile("./data/npcs/npcDrops.bin", "r");
		FileChannel channel = in.getChannel();
		ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());

		BufferedWriter writer = new BufferedWriter(new FileWriter("npcDrops.txt", true));

		int dropMapSize = buffer.getShort();
		/**
		 * At this point we are going through the drop tables
		 */
		for (int i = 0; i < dropMapSize; i++) {
			int npcId = buffer.getShort();
			short dropAmount = buffer.getShort();
			for (int x = 0; x < dropAmount; x++) {
				/**
				 * At this point we are reading the drops from the table
				 */
				short itemId = buffer.getShort();
				int minItemAmount = buffer.getInt();
				int maxItemAmount = buffer.getInt();
				int chanceTypeOrdinal = buffer.getInt(); // ChanceType Enum
				// Ordinal

				String rarity = "";
				switch (chanceTypeOrdinal) {
				case 0:
					rarity = "ALWAYS";
					break;
				case 1:
					rarity = "COMMON";
					break;
				case 2:
					rarity = "UNCOMMON";
					break;
				case 3:
					rarity = "RARE";
					break;
				case 4:
					rarity = "VERYRARE";
					break;
				} // TODO organize the drops in rarity order
				writer.write(npcId + ":");
				writer.write(itemId + "-" + rarity + "-" + minItemAmount + "-" + maxItemAmount + ":");
				writer.newLine();
				writer.flush();
			}
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

}
