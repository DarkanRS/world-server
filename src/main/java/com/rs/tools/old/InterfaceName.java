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
package com.rs.tools.old;

import com.rs.cache.IndexType;
import com.rs.cache.Store;
import com.rs.cache.utils.CacheUtil;

import java.io.IOException;

public class InterfaceName {

	public static final char[] VALID_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static void printAllCombinations4Letters() {
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		Store rscache = new Store("data/cache/");

		System.out.println(rscache.getIndex(IndexType.INTERFACES).getTable().isNamed());

		System.out.println(rscache.getIndex(IndexType.INTERFACES).getArchiveId("chat"));
		System.out.println(CacheUtil.getNameHash("price checker"));
		/*
		 * System.out.println(Utils.getNameHash("prayer"));
		 * System.out.println(Utils.unhash(Utils.getNameHash("t")));
		 */
		// System.out.println(Utils.getNameHash("prayer"));

		/*
		 * int hash =
		 * rscache.getIndexes()[Constants.INTERFACE_DEFINITIONS_INDEX]
		 * .getTable().getArchives()[884].getNameHash(); for(char l1 :
		 * VALID_CHARS) { System.out.println(l1); for(char l2 : VALID_CHARS) {
		 * for(char l3 : VALID_CHARS) {
		 *
		 * for(char l4 : VALID_CHARS) { for(char l5 : VALID_CHARS) { for(char l6
		 * : VALID_CHARS) { String name = new String(new char[] {l1, l2, l3,
		 * l4,l5, l6}); if(Utils.getNameHash(name) == hash)
		 * System.out.println(name); } } } } } }
		 */

	}

}
