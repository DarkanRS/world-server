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
package com.rs.game.model.entity;

import com.rs.lib.util.Utils;

public class BodyGlow {

	private int time;
	private final int redAdd;
	private final int greenAdd;
	private final int blueAdd;
	private final int scalar;

	public BodyGlow(int time, int color1, int color2, int color3, int color4) {
		this.time = time;
		redAdd = color1;
		greenAdd = color2;
		blueAdd = color3;
		scalar = color4;
	}

	public static BodyGlow generateRandomBodyGlow(int time) {
		return new BodyGlow(time, Utils.random(254), Utils.random(254), Utils.random(254), Utils.random(254));
	}

	public static BodyGlow GREEN(int time) {
		return new BodyGlow(time, 20, 20, 110, 150);
	}

	public static BodyGlow BLUE(int time) {
		return new BodyGlow(time, 92, 44, 126, 130);
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getRedAdd() {
		return redAdd;
	}

	public int getGreenAdd() {
		return greenAdd;
	}

	public int getScalar() {
		return scalar;
	}

	public int getBlueAdd() {
		return blueAdd;
	}
}
