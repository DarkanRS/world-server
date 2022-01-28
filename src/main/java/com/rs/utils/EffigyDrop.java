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
package com.rs.utils;

import com.rs.lib.util.Utils;

public class EffigyDrop {

	private static final int COMBAT_LEVEL = 2;
	private static final int SAMPLE = 200_000_000;

	public static void main(String[] args) {
		int effigies = 0;
		for (int i = 0;i < SAMPLE;i++)
			if (dropEffigy(COMBAT_LEVEL))
				effigies++;
		System.out.println("Effigies: " + effigies);
		System.out.println("Simulated: " + (SAMPLE/effigies));
		System.out.println("Actual: " + getRate(COMBAT_LEVEL));
	}

	public static boolean dropEffigy(int combatLevel) {
		return Math.random() <= (1.0 / getRate(combatLevel));
	}

	public static double getRate(int combatLevel) {
		return Utils.clampD(169075.845*(Math.pow(0.9807522225, combatLevel)), 128.0, 1000000.0);
	}

}
