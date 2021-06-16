package com.rs.game;

import com.rs.lib.util.Utils;

public class BodyGlow {

	private int time;
	private int redAdd;
	private int greenAdd;
	private int blueAdd;
	private int scalar;

	public BodyGlow(int time, int color1, int color2, int color3, int color4) {
		this.time = time;
		this.redAdd = color1;
		this.greenAdd = color2;
		this.blueAdd = color3;
		this.scalar = color4;
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
