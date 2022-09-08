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
package com.rs.game.region;

import java.util.ArrayList;

/**
 * BW = Actually blocks a tile when step is processed
 * BP = Blocks projectiles
 * PF = Pathfinder only takes these flags into consideration when generating a path
 * PFBW = Blocks tiles for both walk steps and the pathfinder
 * @author trent
 */
public enum ClipFlag {
	EMPTY(0xFFFFFFFF), 			// -1

	BW_NW(0x1), 				// 1
	BW_N(0x2), 					// 2
	BW_NE(0x4), 				// 4
	BW_E(0x8), 					// 8
	BW_SE(0x10), 				// 16
	BW_S(0x20), 				// 32
	BW_SW(0x40), 				// 64
	BW_W(0x80), 				// 128
	BW_FULL(0x100), 			// 256

	BP_NW(0x200), 				// 512
	BP_N(0x400), 				// 1024
	BP_NE(0x800), 				// 2048
	BP_E(0x1000), 				// 4096
	BP_SE(0x2000), 				// 8192
	BP_S(0x4000), 				// 16384
	BP_SW(0x8000), 				// 32768
	BP_W(0x10000), 				// 65536
	BP_FULL(0x20000), 			// 131072

	PFBW_GROUND_DECO(0x40000), 	// 262144
	PFBW_FLOOR(0x200000), 		// 2097152

	PF_NW(0x400000), 			// 4194304
	PF_N(0x800000), 			// 8388608
	PF_NE(0x1000000), 			// 16777216
	PF_E(0x2000000), 			// 33554432
	PF_SE(0x4000000),			// 67108864
	PF_S(0x8000000), 			// 134217728
	PF_SW(0x10000000), 			// 268435456
	PF_W(0x20000000), 			// 536870912
	PF_FULL(0x40000000); 		// 1073741824

	public int flag;

	private ClipFlag(int flag) {
		this.flag = flag;
	}

	public static ArrayList<ClipFlag> getFlags(int value) {
		ArrayList<ClipFlag> flags = new ArrayList<>();
		for (ClipFlag f : ClipFlag.values())
			if ((value & f.flag) != 0 && f != ClipFlag.EMPTY)
				flags.add(f);
		return flags;
	}

	public static boolean flagged(int value, ClipFlag... flags) {
		int flag = 0;
		for (ClipFlag f : flags)
			flag |= f.flag;
		return (value & flag) != 0;
	}

	public static int or(ClipFlag... flags) {
		int flag = 0;
		for (ClipFlag f : flags)
			flag |= f.flag;
		return flag;
	}
}
