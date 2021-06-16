package com.rs.tools.old;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.rs.cache.Cache;
import com.rs.cache.Index;
import com.rs.cache.IndexType;
import com.rs.cache.Store;

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
