package com.rs.utils.music;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonIOException;
import com.rs.lib.file.JsonFileManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class Music {
	
	private static Map<Integer, Song> MUSICS = new HashMap<>();
	private static Map<Integer, int[]> MUSICS_REGION = new HashMap<>();
	
	@ServerStartupEvent
	public static void init() {
		try {
			Song[] songs = (Song[]) JsonFileManager.loadJsonFile(new File("./data/music.json"), Song[].class);
			for (Song s : songs) {
				MUSICS.put(s.getId(), s);
				for (int regionId : s.getRegionIds()) {
					if (MUSICS_REGION.get(regionId) == null)
						MUSICS_REGION.put(regionId, new int[] { s.getId() });
					else {
						int[] musicIds = MUSICS_REGION.get(regionId);
						int[] newMusicIds = new int[musicIds.length+1];
						for (int i = 0;i < musicIds.length;i++)
							newMusicIds[i] = musicIds[i];
						newMusicIds[musicIds.length] = s.getId();
						MUSICS_REGION.put(regionId, newMusicIds);
					}
				}
			}
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}

	public static int[] getRegionMusics(int regionId) {
		return MUSICS_REGION.get(regionId);
	}

	public static Song getSong(int musicId) {
		return MUSICS.get(musicId);
	}

}
