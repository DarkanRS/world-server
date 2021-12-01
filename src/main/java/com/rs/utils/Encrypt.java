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

import java.security.MessageDigest;

/**
 * Handles the encryption of player passwords.
 * 
 * @author Apache Ah64
 */
public class Encrypt {

	/**
	 * Encrypt the string using the SHA-1 encryption algorithm.
	 * 
	 * @param string
	 *            The string.
	 * @return The encrypted string.
	 */
	public static String encryptSHA1(String string) {
		String hash = null;
		try {
			hash = byteArrayToHexString(hash(string));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}

	/**
	 * Encrypt the string to a SHA-1 hash.
	 * 
	 * @param x
	 *            The string to encrypt.
	 * @return The byte array.
	 * @throws Exception
	 *             when an exception occurs.
	 */
	public static byte[] hash(String x) throws Exception {
		MessageDigest string;
		string = java.security.MessageDigest.getInstance("SHA-1");
		string.reset();
		string.update(x.getBytes());
		return string.digest();
	}

	/**
	 * Converts a byte array to hex string.
	 * 
	 * @param b
	 *            The byte array.
	 * @return The hex string.
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer string = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				string.append('0');
			}
			string.append(Integer.toHexString(v));
		}
		return string.toString();
	}
}