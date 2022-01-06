package com.rs.utils.music;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * There should be no duplicate region keys. But there can be parent genres.
 */
public class Genre {
    private String genre;
    private String parent;
    private String comment;
    private boolean isActive;
    private int[] regionIds;
    private int[] songs;

    public String getGenreName() {
        return genre;
    }

    public String getParent() {
        return parent;
    }

    public String getComment() {
        return comment;
    }

    public boolean isActive() {
        return isActive;
    }

    public int[] getRegionIds() {
        return regionIds;
    }

    public int[] getSongs() {
        Genre parent = Music.getParent(this.parent);
        if(parent == null) {
            return songs;
        }
        List<Integer> allSongs = Arrays.stream(parent.getSongs()).boxed().collect(Collectors.toList());
        allSongs.addAll(Arrays.stream(songs).boxed().collect(Collectors.toList()));
        return allSongs.stream().distinct().mapToInt(Integer::intValue).toArray();
    }

    public boolean matches(Genre g) {
        if(g == null || genre == null)
            return false;
        return genre.equalsIgnoreCase(g.getGenreName());
    }
}
