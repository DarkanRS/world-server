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

import java.lang.SuppressWarnings;

public class GenericsUtils {


	public static boolean isi(Object cst) {
		return cst instanceof Integer;
	}

	public static boolean iss(Object cst) {
		return cst instanceof String;
	}

	public static boolean isl(Object cst) {
		return cst instanceof Long;
	}

	public static Integer asi(Object cst) {
		return as(cst);
	}

	public static String ass(Object cst) {
		return as(cst);
	}

	public static Long asl(Object cst) {
		return as(cst);
	}


	public static int shrn(int val, int shr, int n) {
		return (val >>> shr) & n;
	}

	public static long shrn(long val, int shr, long n) {
		return (val >>> shr) & n;
	}


	@SuppressWarnings("unchecked")
	public static <T> T as(Class<T> t, Object cst) {
		return (T)cst;
	}

	@SuppressWarnings("unchecked")
	public static <T> T as(Object cst) {
		return (T)cst;
	}
}
