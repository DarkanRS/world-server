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
package com.rs.game.region;

import java.util.ArrayList;

public enum RenderFlag {
	CLIPPED(0x1), //1
	LOWER_OBJECTS_TO_OVERRIDE_CLIPPING(0x2), //2
	UNDER_ROOF(0x4), //4
	FORCE_TO_BOTTOM(0x8), //8
	ROOF(0x10); //16
	
	private int flag;
	
	private RenderFlag(int flag) {
		this.flag = flag;
	}
	
	public static ArrayList<RenderFlag> getFlags(int value) {
		ArrayList<RenderFlag> flags = new ArrayList<>();
		for (RenderFlag f : RenderFlag.values()) {
			if ((value & f.flag) != 0)
				flags.add(f);
		}
		return flags;
	}
	
	public static boolean flagged(int value, RenderFlag... flags) {
    	int flag = 0;
    	for (RenderFlag f : flags)
    		flag |= f.flag;
    	return (value & flag) != 0;
    }
}
