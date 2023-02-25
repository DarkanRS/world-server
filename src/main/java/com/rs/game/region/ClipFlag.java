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
import java.util.stream.Stream;

/**
 * BW = Actually blocks a tile when step is processed
 * BP = Blocks projectiles
 * PF = Pathfinder only takes these flags into consideration when generating a path
 * PFBW = Blocks tiles for both walk steps and the pathfinder
 * @author trent
 */
public enum ClipFlag {
	EMPTY(true, 0xFFFFFFFF),             // -1

	BW_NW(0),                 // 1
	BW_N(1),                     // 2
	BW_NE(2),                 // 4
	BW_E(3),                     // 8
	BW_SE(4),                 // 16
	BW_S(5),                 // 32
	BW_SW(6),                 // 64
	BW_W(7),                 // 128
	BW_FULL(8),             // 256

	BP_NW(9),                 // 512
	BP_N(10),                 // 1024
	BP_NE(11),                 // 2048
	BP_E(12),                 // 4096
	BP_SE(13),                 // 8192
	BP_S(14),                 // 16384
	BP_SW(15),                 // 32768
	BP_W(16),                 // 65536
	BP_FULL(17),             // 131072

	PFBW_GROUND_DECO(18),     // 262144

	BW_NPC(19),         	// 524288
	BW_PLAYER(20),         	// 1048576

	PFBW_FLOOR(22),         // 2097152

	PF_NW(22),             	// 4194304
	PF_N(23),             	// 8388608
	PF_NE(24),             	// 16777216
	PF_E(25),             	// 33554432
	PF_SE(26),            	// 67108864
	PF_S(27),             	// 134217728
	PF_SW(28),             	// 268435456
	PF_W(29),             	// 536870912
	PF_FULL(30),         	// 1073741824
	UNDER_ROOF(31);			//-2147483648

	public int flag;

	ClipFlag(int flag) {
		this.flag = 1 << flag;
	}

	ClipFlag(boolean absolute, int flag) {
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

	public static int blockNorth(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_N.flag;
		if (projectiles)
			flags |= ClipFlag.BP_N.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_N.flag;
		return flags;
	}

	public static int blockNorthEast(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_NE.flag;
		if (projectiles)
			flags |= ClipFlag.BP_NE.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_NE.flag;
		return flags;
	}

	public static int blockNorthWest(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_NW.flag;
		if (projectiles)
			flags |= ClipFlag.BP_NW.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_NW.flag;
		return flags;
	}


	public static int blockSouth(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_S.flag;
		if (projectiles)
			flags |= ClipFlag.BP_S.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_S.flag;
		return flags;
	}

	public static int blockSouthEast(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_SE.flag;
		if (projectiles)
			flags |= ClipFlag.BP_SE.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_SE.flag;
		return flags;
	}

	public static int blockSouthWest(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_SW.flag;
		if (projectiles)
			flags |= ClipFlag.BP_SW.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_SW.flag;
		return flags;
	}

	public static int blockEast(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_E.flag;
		if (projectiles)
			flags |= ClipFlag.BP_E.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_E.flag;
		return flags;
	}

	public static int blockWest(boolean walk, boolean projectiles, boolean pathfinder) {
		int flags = 0;
		if (walk)
			flags |= ClipFlag.BW_W.flag;
		if (projectiles)
			flags |= ClipFlag.BP_W.flag;
		if (pathfinder)
			flags |= ClipFlag.PF_W.flag;
		return flags;
	}
}
