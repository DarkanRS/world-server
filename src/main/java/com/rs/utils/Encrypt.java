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