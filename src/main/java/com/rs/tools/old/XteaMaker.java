package com.rs.tools.old;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class XteaMaker {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			BufferedReader stream = new BufferedReader(new InputStreamReader(new FileInputStream("xtea650.txt")));
			while (true) {
				String line = stream.readLine();
				if (line == null)
					break;
				if (line.startsWith("--"))
					continue;
				String[] spaceSplitLine = line.split(" ");
				int regionId = Integer.valueOf(spaceSplitLine[0]);
				String[] xteaSplit = spaceSplitLine[3].split("\\.");
				/*
				 * for(byte c : spaceSplitLine[3].getBytes()) {
				 * System.out.println(c); System.out.println((char) c); }
				 */

				if (xteaSplit[0].equals("0") && xteaSplit[1].equals("0") && xteaSplit[2].equals("0") && xteaSplit[3].equals("0"))
					continue;
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("convertedXtea/" + regionId + ".txt")));
				for (String xtea : xteaSplit) {
					writer.append(xtea);
					writer.newLine();
					writer.flush();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
