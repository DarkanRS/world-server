package com.rs.utils.music;

public class Genre {
    private String genre;
    private String comment;
    private int[] regionIds;
    private int[] songs;

    public String getGenreName() {
        return genre;
    }

    public String getComment() {
        return comment;
    }

    public int[] getRegionIds() {
        return regionIds;
    }

    public int[] getSongs() {
        return songs;
    }

    public boolean matches(Genre g) {
        if(g == null || genre == null)
            return false;
        return genre.equalsIgnoreCase(g.getGenreName());
    }
}
