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
package com.rs.utils;

public class MapHeightCalc {
	private static final int JAGEX_CIRCULAR_ANGLE = 2048;
	private static final double ANGULAR_RATIO = 360D / JAGEX_CIRCULAR_ANGLE;
	private static final double JAGEX_RADIAN = Math.toRadians(ANGULAR_RATIO);

	private static final int[] SIN = new int[JAGEX_CIRCULAR_ANGLE];
	private static final int[] COS = new int[JAGEX_CIRCULAR_ANGLE];

	static {
		for (int i = 0; i < JAGEX_CIRCULAR_ANGLE; i++) {
			SIN[i] = (int) (65536.0D * Math.sin(i * JAGEX_RADIAN));
			COS[i] = (int) (65536.0D * Math.cos(i * JAGEX_RADIAN));
		}
	}

	public static int calculate(int x, int y) {
		int n = interpolateNoise(x + 45365, y + 91923, 4) - 128 + (interpolateNoise(10294 + x, y + 37821, 2) - 128 >> 1) + (interpolateNoise(x, y, 1) - 128 >> 2);
		n = 35 + (int) (n * 0.3D);
		if (n < 10)
			n = 10;
		else if (n > 60)
			n = 60;

		return n;
	}

	public static int interpolateNoise(int x, int y, int frequency) {
		int intX = x / frequency;
		int fracX = x & frequency - 1;
		int intY = y / frequency;
		int fracY = y & frequency - 1;
		int v1 = smoothedNoise1(intX, intY);
		int v2 = smoothedNoise1(intX + 1, intY);
		int v3 = smoothedNoise1(intX, intY + 1);
		int v4 = smoothedNoise1(1 + intX, 1 + intY);
		int i1 = interpolate(v1, v2, fracX, frequency);
		int i2 = interpolate(v3, v4, fracX, frequency);
		return interpolate(i1, i2, fracY, frequency);
	}

	public static int smoothedNoise1(int x, int y) {
		int corners = noise(x - 1, y - 1) + noise(x + 1, y - 1) + noise(x - 1, 1 + y) + noise(x + 1, y + 1);
		int sides = noise(x - 1, y) + noise(1 + x, y) + noise(x, y - 1) + noise(x, 1 + y);
		int center = noise(x, y);
		return center / 4 + sides / 8 + corners / 16;
	}

	public static int noise(int x, int y) {
		int n = x + y * 57;
		n ^= n << 13;
		return ((n * (n * n * 15731 + 789221) + 1376312589) & Integer.MAX_VALUE) >> 19 & 255;
	}

	public static int interpolate(int a, int b, int x, int y) {
		int f = 65536 - COS[1024 * x / y] >> 1;
		return (f * b >> 16) + (a * (65536 - f) >> 16);
	}

}