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

import com.rs.game.content.ItemConstants;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.utils.drop.DropTable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

@PluginEventHandler
public class CharmDrop {

	private final static String PACKED_PATH = "data/npcs/charmDrops.txt";
	private static HashMap<String, int[]> charmDrops;
	final static Charset ENCODING = StandardCharsets.UTF_8;

	public static int getCharmAmount(String npcName) {
		switch(npcName) {
		case "brutal green dragon":
		case "iron dragon":
		case "rock lobster":
		case "skeletal wyvern":
			return 2;
		case "black dragon":
		case "giant rock crab":
		case "glacor":
		case "steel dragon":
		case "tormented demon":
			return 3;
		case "king black dragon":
		case "mithril dragon":
			return 4;
		case "corporeal beast":
			return 13;
		case "nex":
			return 20;
		default:
			return 1;
		}
	}

	public static int getCharmType(int[] chances) {
		int goldRate = chances[0];
		int greenRate = chances[1];
		int crimRate = chances[2];
		int blueRate = chances[3];

		ArrayList<Integer> possibleCharms = new ArrayList<>();

		int rand = Utils.getRandomInclusive(100);
		if (rand <= (blueRate) && blueRate != 0)
			possibleCharms.add(3);
		if (rand <= (crimRate) && crimRate != 0)
			possibleCharms.add(2);
		if (rand <= (greenRate) && greenRate != 0)
			possibleCharms.add(1);
		if (rand <= (goldRate) && goldRate != 0)
			possibleCharms.add(0);
		if (possibleCharms.isEmpty())
			return -1;
		Collections.shuffle(possibleCharms);
		return possibleCharms.get(Utils.random(possibleCharms.size()));
	}

	public static DropTable getCharmDrop(String npcName) {
		int[] chances = charmDrops.get(npcName.toLowerCase().replace(" ", "_"));
		if (chances == null)
			return null;
		int charmIndex = getCharmType(chances);
		int amount = getCharmAmount(npcName.toLowerCase());

		if (charmIndex == -1)
			return null;

		DropTable charm = new DropTable(0, 0, ItemConstants.CHARM_IDS[charmIndex], amount, amount);
		return charm;
	}

	@ServerStartupEvent(Priority.FILE_IO)
	public static void loadCharmDrops() {
		try {
			charmDrops = new HashMap<>();
			Path path = Paths.get(PACKED_PATH);
			try (Scanner scanner = new Scanner(path, ENCODING.name())) {
				int lineNumber = 0;

				String npcName = null;
				int[] charmPerc = new int[4];

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					lineNumber++;

					if (line.startsWith("//") || line.isEmpty())
						continue;

					String[] subs = line.split(":");
					String[] info = subs[1].split("-");

					npcName = subs[0];
					charmPerc[0] = Integer.parseInt(info[0]);
					charmPerc[1] = Integer.parseInt(info[1]);
					charmPerc[2] = Integer.parseInt(info[2]);
					charmPerc[3] = Integer.parseInt(info[3]);

					charmDrops.put(npcName, new int[] {charmPerc[0], charmPerc[1], charmPerc[2], charmPerc[3]});
				}
				Logger.info(CharmDrop.class, "loadCharmDrops", "Parsed " + lineNumber + " lines of NPC charm drops.");

			}
		} catch (Throwable e) {
			Logger.handle(CharmDrop.class, "loadCharmDrops", e);
		}
	}

}
