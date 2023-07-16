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
package com.rs.game.content.skills.farming;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;

import java.util.HashMap;
import java.util.Map;

public enum StorableItem {
	RAKE(1, 5341) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_RAKE, player.getLeprechaunStorage().containsKey(RAKE) ? 1 : 0);
		}
	},
	SEED_DIBBER(1, 5343) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_SEED_DIBBER, player.getLeprechaunStorage().containsKey(SEED_DIBBER) ? 1 : 0);
		}
	},
	SPADE(1, 952) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_SPADE, player.getLeprechaunStorage().containsKey(SPADE) ? 1 : 0);
		}
	},
	TROWEL(1, 5325) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_TROWEL, player.getLeprechaunStorage().containsKey(TROWEL) ? 1 : 0);
		}
	},
	SCARECROW(7, 6059) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_SCARECROW, player.getNumInLeprechaun(SCARECROW));
		}
	},
	BUCKET(255, 1925) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_BUCKET, player.getNumInLeprechaun(BUCKET) % 32);
			player.getVars().setVarBit(VB_EXTRA_BUCKETS, player.getNumInLeprechaun(BUCKET) / 32);
		}
	},
	COMPOST(255, 6032) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_COMPOST, player.getNumInLeprechaun(COMPOST));
		}
	},
	SUPERCOMPOST(255, 6034) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_SUPERCOMPOST, player.getNumInLeprechaun(SUPERCOMPOST));
		}
	},
	SECATEURS(1, 5329, 7409) {
		@Override
		public void updateVars(Player player) {
			Item item = player.getLeprechaunStorage().get(SECATEURS);
			player.getVars().setVarBit(VB_SECATEURS, player.getLeprechaunStorage().containsKey(SECATEURS) ? 1 : 0);
			player.getVars().setVarBit(VB_IS_MAGIC_SECATEURS, (item == null || item.getId() == 5329) ? 0 : 1);
		}
	},
	WATERING_CAN(1, 5331, 5333, 5334, 5335, 5336, 5337, 5338, 5339, 5340, 18682) {
		@Override
		public void updateVars(Player player) {
			if (!player.getLeprechaunStorage().containsKey(WATERING_CAN)) {
				player.getVars().setVarBit(VB_WATERING_CAN, 0);
				return;
			}
			int index = 0;
			for (int i = 0;i < validIds.length;i++)
				if (player.getLeprechaunStorage().get(WATERING_CAN).getId() == validIds[i]) {
					index = i;
					break;
				}
			player.getVars().setVarBit(VB_WATERING_CAN, index+1);
		}
	},
	PLANT_CURE(255, 6036) {
		@Override
		public void updateVars(Player player) {
			player.getVars().setVarBit(VB_PLANT_CURE, player.getNumInLeprechaun(PLANT_CURE));
		}
	};

	private static final int VB_RAKE = 1435, VB_SEED_DIBBER = 1436, VB_SPADE = 1437, VB_TROWEL = 1440, VB_SCARECROW = 1778,
			VB_BUCKET = 1441, VB_COMPOST = 1442, VB_SUPERCOMPOST = 1443, VB_SECATEURS = 1438, VB_IS_MAGIC_SECATEURS = 1848,
			VB_WATERING_CAN = 1439, VB_EXTRA_BUCKETS = 10204, VB_PLANT_CURE = 10205;

	private static Map<Integer, StorableItem> MAP = new HashMap<>();

	static {
		for (StorableItem item : StorableItem.values())
			for (int id : item.validIds)
				MAP.put(id, item);
	}

	public static StorableItem forId(int itemId) {
		return MAP.get(itemId);
	}

	public int maxAmount;
	public int[] validIds;
	public abstract void updateVars(Player player);

	private StorableItem(int maxAmount, int... validIds) {
		this.maxAmount = maxAmount;
		this.validIds = validIds;
	}
}
