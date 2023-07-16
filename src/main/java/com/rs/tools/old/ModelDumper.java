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

import com.rs.cache.Cache;
import com.rs.cache.Index;
import com.rs.cache.IndexType;
import com.rs.cache.Store;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ModelDumper {

	public static void main(String[] args) throws IOException {
		Cache.STORE = new Store("C:/Users/Alexandre/Desktop/Java/projectos/FileStore2/cache562/");
		Index index = Cache.STORE.getIndex(IndexType.MODELS);
		System.out.println(index.getLastArchiveId());
		for (int i = 0; i < index.getLastArchiveId(); i++) {
			byte[] data = index.getFile(i);
			if (data == null)
				continue;
			// if(!(data[data.length + -1] == -1 && data[-2 + data.length] ==
			// -1))
			// if((data[-1 + data.length] ^ 0xffffffff) != 0 || data[-2 +
			// data.length] != -1)
			// System.out.println(i);
			writeFile(data, "C:/Users/Alexandre/Downloads/RSMV 2/Models3/" + i + ".dat");
		}

	}

	public static void writeFile(byte[] data, String fileName) throws IOException {
		OutputStream out = new FileOutputStream(fileName);
		out.write(data);
		out.close();
	}

}
