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
package com.rs.game;

public class TestBar extends HitBar {
	
	private int type;
	private int delay;
	private int perc;
	private int toPerc;
	private int timer;
	
	public TestBar(int type, int delay, int perc, int toPerc, int timer) {
		this.type = type;
		this.delay = delay;
		this.perc = perc;
		this.toPerc = toPerc;
		this.timer = timer;
	}
	
	@Override
	public int getTimer() {
		return timer;
	}

	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public int getToPercentage() {
		return toPerc;
	}
	
	@Override
	public int getDelay() {
		return delay;
	}

	@Override
	public int getPercentage() {
		return perc;
	}

}
