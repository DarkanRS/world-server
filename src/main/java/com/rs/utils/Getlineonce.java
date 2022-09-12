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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.rs.lib.util.Logger;

/**
 * My custom Runtime debugging tool
 * Author: Jawarrior1
 */
public class Getlineonce {
	static Dictionary<String, List<Integer>> lines = new Hashtable<>();

	public Getlineonce(boolean repeat) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

		//Check if this line as been taking, so as not to repeat with loops or recursion
		int line = stackTrace[2].getLineNumber();
		String fileName = stackTrace[2].getFileName();
		List<Integer> tempLines = new ArrayList<>();//only used if key doesn't exist
		if (lines.isEmpty()) {
			tempLines.add(line);
			lines.put(fileName, tempLines);
			//            lines.put("tester", oneline);
		} else {
			boolean keyExists = false;
			for (Enumeration<String> k = lines.keys(); k.hasMoreElements(); ) {
				String key = k.nextElement();

				//Check if there is a kay
				if (!key.equalsIgnoreCase(fileName))
					continue;
				keyExists = true;
				if (!lines.get(key).contains(line)) {
					tempLines = lines.get(key);
					tempLines.add(line);
					lines.put(fileName, tempLines);
					Logger.debug(Getlineonce.class, "constructor()", fileName + ": " + line);// should run once
				} else if (repeat)
					Logger.debug(Getlineonce.class, "constructor()", fileName + ": " + line);
			}
			//If after searching all of those you found nothing
			if (!keyExists) {
				tempLines.add(line);
				lines.put(fileName, tempLines);
			}
		}
	}
}