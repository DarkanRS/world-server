/*
 * Class AutoBackup
 *
 * Created by Mikee
 */

package com.rs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AutoBackup {

	public AutoBackup() {
		backupAll();
	}
	
	public static void backupAll() {
		backup("./data/characters/", "./data/backups/players/players");
		backup("./data/clans/", "./data/backups/clans/clans");
		backup("./data/grandexchange/", "./data/backups/grandexchange/grandexchange");
	}
	
	public static void backup(String fromDir, String toDir) {
		File f1 = new File(fromDir);
		File f2 = new File(toDir +" "+ getDate() + "/" +getTime()+ ".zip");
		File path = new File(toDir +" "+ getDate());
		if (!path.exists())
			path.mkdir();
		if (!f2.exists()) {
			try {
				zipDirectory(f1, f2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
		}
	}

	public static final void zipDirectory(File f, File zf) throws IOException {
		ZipOutputStream z = new ZipOutputStream(new FileOutputStream(zf));
		zip(f, f, z);
		z.close();
	}

	private static final void zip(File directory, File base, ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		if (files == null)
			return;
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zip(files[i], base, zos);
			} else {
				long time = System.currentTimeMillis()-files[i].lastModified();
				//86400000L 1 day
				//3600000L 1 hour
				//21600000L 6 hours
				if (time < (21600000L)) {
					FileInputStream in = new FileInputStream(files[i]);
					ZipEntry entry = new ZipEntry(files[i].getPath().substring(base.getPath().length() + 1));
					zos.putNextEntry(entry);
					while (-1 != (read = in.read(buffer))) {
						zos.write(buffer, 0, read);
					}
					in.close();
				}
			}
		}
	}
	
	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("kk mm ss");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		date = null;
		dateFormat = null;
		return currentDate;
	}

	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		date = null;
		dateFormat = null;
		return currentDate;
	}
}