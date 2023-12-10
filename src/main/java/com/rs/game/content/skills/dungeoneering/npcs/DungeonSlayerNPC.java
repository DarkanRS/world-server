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
package com.rs.game.content.skills.dungeoneering.npcs;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonPartyManager;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.utils.drop.DropSet;
import com.rs.utils.drop.DropTable;

import java.util.*;

public class DungeonSlayerNPC extends DungeonNPC {

	public enum DungeonSlayerType {
		CRAWLING_HAND(10694, 5, 20, new DropTable(1, 3, 17261, 1), new DropTable(1, 10, 17263, 1)),
		CAVE_CRAWLER(10695, 10, new int[] { DungeonConstants.ABANDONED_FLOORS, DungeonConstants.FURNISHED_FLOORS }, 20, new DropTable(1, 10, 17265, 1), new DropTable(1, 10, 17267, 1)),
		CAVE_SLIME(10696, 17, 20, new DropTable(1, 10, 17269, 1), new DropTable(1, 10, 17271, 1)),
		PYREFIEND(10697, 30, 18, new DropTable(1, 10, 17273, 1)),
		NIGHT_SPIDER(10698, 41, 20, new DropTable(1, 30, 17279, 1)),
		JELLY(10699, 52, 15, new DropTable(1, 10, 17281, 1), new DropTable(1, 10, 17283, 1)),
		SPIRITUAL_GUARDIAN(10700, 63, 15, new DropTable(1, 10, 17285, 1), new DropTable(1, 10, 17287, 1)),
		SEEKER(10701, 71, new int[] { DungeonConstants.ABANDONED_FLOORS, DungeonConstants.OCCULT_FLOORS, DungeonConstants.WARPED_FLOORS }, 10, new DropTable(1, 10, 17289, 1)),
		NECHRYAEL(10702, 80, new int[] { DungeonConstants.ABANDONED_FLOORS, DungeonConstants.OCCULT_FLOORS, DungeonConstants.WARPED_FLOORS }, 15, new DropTable(1, 30, 17283, 1)),
		EDIMMU(10703, 90, new int[] { DungeonConstants.OCCULT_FLOORS, DungeonConstants.WARPED_FLOORS }, 5, new DropTable(1, 20, 17291, 1)), //40 damage every 15 seconds
		SOULGAZER(10704, 99, new int[] { DungeonConstants.OCCULT_FLOORS, DungeonConstants.WARPED_FLOORS }, 5, new DropTable(1, 20, 17295, 1));

		private static final Map<Integer, DungeonSlayerType> MAP = new HashMap<>();

		static {
			for (DungeonSlayerType type : DungeonSlayerType.values())
				MAP.put(type.id, type);
		}

		public static DungeonSlayerType forId(int id) {
			return MAP.get(id);
		}

		private final int id, req, weight;
		private int[] floors;
		private final DropSet drops;

		DungeonSlayerType(int id, int req, int[] floors, int weight, DropTable... drops) {
			this.id = id;
			this.req = req;
			this.floors = floors;
			this.weight = weight;
			this.drops = new DropSet(drops);
		}

		DungeonSlayerType(int id, int req, int weight, DropTable... drops) {
			this.id = id;
			this.req = req;
			this.weight = weight;
			this.drops = new DropSet(drops);
		}

		public int getReq() {
			return req;
		}

		private boolean containsFloor(int checkFloor) {
			if (floors == null)
				return true;
			for (int floor : floors)
				if (floor == checkFloor)
					return true;
			return false;
		}
	}

	private final DungeonSlayerType type;

	public DungeonSlayerNPC(int id, Tile tile, DungeonManager manager) {
		super(id, tile, manager);
		type = DungeonSlayerType.forId(id);
	}

	@Override
	public void drop() {
		super.drop();
		int size = getSize();
		List<Item> drops = type.drops.getDropList().genDrop();
		for (Item item : drops)
			World.addGroundItem(item, Tile.of(getCoordFaceX(size), getCoordFaceY(size), getPlane()));
	}

	public DungeonSlayerType getType() {
		return type;
	}

	public static int getSlayerCreature(DungeonPartyManager party) {
		List<Integer> monsters = new ArrayList<>();
		for (DungeonSlayerType type : DungeonSlayerType.values())
			for (Player player : party.getTeam())
				if (player.getSkills().getLevel(Constants.SLAYER) >= type.req && type.containsFloor(party.getFloorType())) {
					for (int i = 0;i < type.weight;i++)
						monsters.add(type.id);
				}
		Collections.shuffle(monsters);
		return !monsters.isEmpty() ? monsters.get(0) : -1;
	}

}
