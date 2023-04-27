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
package com.rs.utils.music;

import com.google.gson.JsonIOException;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.file.JsonFileManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

import java.io.File;
import java.io.IOException;
import java.util.*;

@PluginEventHandler
public class Music {
	/** Summary of Music System
	 * -When initially entering a region the songs associated with that region play first
	 * from songs.json.
	 * -Afterward genre ambient songs play. Region specific songs are added to that genre.
	 * -When entering other regions the only time it switches songs is when the region
	 * has a different genre in the genre map or the region has no genre. This means
	 * less song changes as the player moves around.
	 * -allowAmbient allows players to play the music as ambient music, though it is still
	 * locked. This works to increase variety but never adds to unlocked music. This is
	 * mostly used just to increase ambient song variety and does not affect region or
	 * playlist music.
	 * Chunks are secondary and do not change the current music but whatever plays next.
	 */
	private static Map<Integer, Song> MUSICS = new HashMap<>();//Full music listing
	private static Map<Integer, int[]> MUSICS_REGION = new HashMap<>();//hints & unlocks

	private static Map<Integer, Genre> GENRE_CHUNKS = new HashMap<>();//Genre per chunk
	private static Map<Integer, Genre> GENRE_REGION = new HashMap<>();//Genre per region
	private static List<Genre> genres = new ArrayList<>();
	private static Genre[] parentGenres;
	private static ArrayList<Integer> allowAmbientMusic = new ArrayList<>();

	@ServerStartupEvent(Priority.FILE_IO)
	public static void init() {
		try {
			Song[] songs = JsonFileManager.loadJsonFile(new File("./data/music/songs.json"), Song[].class);
			for (Song s : songs) {
				MUSICS.put(s.getId(), s);
				for (int regionId : s.getRegionIds())
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

			parentGenres = JsonFileManager.loadJsonFile(new File("./data/music/parent-genres.json"), Genre[].class);
			genres.addAll(Arrays.asList(JsonFileManager.loadJsonFile(new File("./data/music/regions/asgarnia.json"), Genre[].class)));
			genres.addAll(Arrays.asList(JsonFileManager.loadJsonFile(new File("./data/music/regions/kandarin.json"), Genre[].class)));
			genres.addAll(Arrays.asList(JsonFileManager.loadJsonFile(new File("./data/music/regions/misthalin.json"), Genre[].class)));
			genres.addAll(Arrays.asList(JsonFileManager.loadJsonFile(new File("./data/music/regions/morytania.json"), Genre[].class)));
			genres.addAll(Arrays.asList(JsonFileManager.loadJsonFile(new File("./data/music/regions/dungeons.json"), Genre[].class)));
            genres.addAll(Arrays.asList(JsonFileManager.loadJsonFile(new File("./data/music/regions/karamja.json"), Genre[].class)));
			genres.addAll(Arrays.asList(JsonFileManager.loadJsonFile(new File("./data/music/regions/other.json"), Genre[].class)));
			addGenresToChunkMap();
			addGenresToRegionMap();

			//Auto-unlock songs list.
			for(Song s : songs)
				if(s.isAllowAmbient())
					allowAmbientMusic.add(s.getId());

		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void addGenresToRegionMap() {
		for(Genre g : genres)
			if(g.isActive())
				for (int regionId : g.getRegionIds()) {
					if (GENRE_REGION.containsKey(regionId))
						throw new java.lang.Error("Error, duplicate key at: " + regionId);
					GENRE_REGION.put(regionId, g);//none of the values can be empty
				}
	}
	private static void addGenresToChunkMap() {
		for(Genre g : genres)
			if(g.isActive())
				for (int chunkId : g.getChunkIds()) {
					if (GENRE_CHUNKS.containsKey(chunkId))
						throw new java.lang.Error("Error, duplicate key at: " + chunkId);
					GENRE_CHUNKS.put(chunkId, g);//none of the values can be empty
				}
	}

	/**
	 * This is for the music system to check if a song is unlocked.
	 * @param regionId
	 * @return
	 */
	public static int[] getRegionMusics(int regionId) {
		return MUSICS_REGION.get(regionId);
	}

	public static Song getSong(int musicId) {
		return MUSICS.get(musicId);
	}

	/**
	 * Start with the region genre but if there are genre chunks then default to that.
	 * Lastly make sure the genre is active.
	 * @param player
	 * @return
	 */
	public static Genre getGenre(Player player) {
		if(!GENRE_REGION.containsKey(player.getRegionId()))
			return null;
		Genre g = GENRE_REGION.get(player.getRegionId());
		if(GENRE_CHUNKS.containsKey(player.getChunkId()))
			g = GENRE_CHUNKS.get(player.getChunkId());
		if(g.isActive())
			return g;
		return null;
	}

	public static Genre getParent(String name) {
		for(Genre g : parentGenres)
			if(g.getGenreName().equalsIgnoreCase(name))
				return g;
		return null;
	}

	public static String[] getSongGenres(int musicId) {
		List<String> genreNames = new ArrayList<>();
		for(Genre g : genres)
			for(int songId : g.getSongs())
				if(songId == musicId)
					genreNames.add(g.getGenreName());
		return genreNames.stream().toArray(String[]::new);
	}

    public static Genre getGenreByName(String name) {
        for(Genre g : genres)
            if(g.getGenreName().equalsIgnoreCase(name))
                return g;
        return null;
    }

	public static ArrayList<Integer> getAllowAmbientMusic() {
		return allowAmbientMusic;
	}
}
