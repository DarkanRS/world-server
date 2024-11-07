package com.rs.db.model;

import com.rs.lib.game.Tile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Penguin {
    private final String uuid;
    private final Date date;
    private final int npcId;
    private final String name;
    private final Tile location;
    private final String wikiLocation;
    private final int week;
    private final int points;
    private final String locationHint;
    private final List<String> spotters;

    public Penguin(int npcId, String name, List<String> spotters, Tile location, String wikiLocation, int week, int points, String locationHint) {
        this.uuid = UUID.randomUUID().toString();
        this.date = new Date();
        this.npcId = npcId;
        this.name = name;
        this.spotters = new ArrayList<>(spotters);
        this.locationHint = locationHint;
        this.location = location;
        this.wikiLocation = wikiLocation;
        this.week = week;
        this.points = points;
    }

    // Getters
    public String getUuid() {
        return uuid;
    }

    public Date getDate() {
        return date;
    }

    public int getNpcId() {
        return npcId;
    }

    public String getName() {
        return name;
    }

    public Tile getLocation() {
        return location;
    }

    public String getWikiLocation() {
        return wikiLocation;
    }

    public int getWeek() {
        return week;
    }

    public int getPoints() {
        return points;
    }

    public String getLocationHint() {
        return locationHint;
    }

    public List<String> getSpotters() {
        return spotters;
    }

    public void addSpotter(String username) {
        if (!spotters.contains(username)) {
            spotters.add(username);
        }
    }
}
