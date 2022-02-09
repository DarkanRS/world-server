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

public class NanotimeSleeper {

	private long aLong7735;
	private long aLong7738;
	private long aLong7734;
	private int anInt7737;
	private int anInt7736 = 1;

	long[] aLongArray7733 = new long[10];

	public NanotimeSleeper() {
		aLong7738 = System.nanoTime();
		aLong7734 = System.nanoTime();
	}

	public long method4852() {
		return aLong7738;
	}

	public long getSleepTimeMs() {
		aLong7738 += method12495();
		return aLong7734 > aLong7738 ? (aLong7734 - aLong7738) / 1000000L : 0L;
	}

	public int sleepNano(long nanoseconds) {
		long sleepTime = getSleepTimeMs();
		try {
			if (sleepTime > 0L)
				if (sleepTime % 10L == 0L) {
					Thread.sleep(sleepTime - 1L);
					Thread.sleep(1L);
				} else
					Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
		}
		return method4845(nanoseconds);
	}

	public int method4845(long long_1) {
		if (aLong7734 > aLong7738) {
			aLong7735 += aLong7734 - aLong7738;
			aLong7738 += aLong7734 - aLong7738;
			aLong7734 += long_1;
			return 1;
		}
		int ticksSkipped = 0;
		do {
			++ticksSkipped;
			aLong7734 += long_1;
		} while (ticksSkipped < 10 && aLong7734 < aLong7738);
		if (aLong7734 < aLong7738)
			aLong7734 = aLong7738;
		return ticksSkipped;
	}

	public void method4853() {
		aLong7735 = 0L;
		if (aLong7734 > aLong7738)
			aLong7738 += aLong7734 - aLong7738;
	}

	public long method12495() {
		long currTime = System.nanoTime();
		long elapsed = currTime - aLong7735;
		aLong7735 = currTime;
		if (elapsed > -5000000000L && elapsed < 5000000000L) {
			aLongArray7733[anInt7737] = elapsed;
			anInt7737 = (anInt7737 + 1) % 10;
			if (anInt7736 < 1)
				++anInt7736;
		}
		long long_6 = 0L;
		for (int i_8 = 1; i_8 <= anInt7736; i_8++)
			long_6 += aLongArray7733[(anInt7737 - i_8 + 10) % 10];
		return long_6 / anInt7736;
	}
}
