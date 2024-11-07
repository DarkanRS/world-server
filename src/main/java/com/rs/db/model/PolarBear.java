package com.rs.db.model;

import com.rs.game.content.dnds.penguins.PolarBearLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PolarBear {
    private final String uuid;
    private final Date date;
    private final String name;
    private final PolarBearLocation location;
    private final int week;
    private final int points;
    private final List<String> previousLocations;
    private final List<String> spotters;

    public PolarBear(String name, PolarBearLocation location, List<String> previousLocations, List<String> spotters, int week, int points) {
        this.date = new Date();
        this.name = name;
        this.previousLocations = new ArrayList<>(previousLocations);
        this.location = location;
        this.spotters = new ArrayList<>(spotters);
        this.week = week;
        this.points = points;
        this.uuid = UUID.randomUUID().toString();
    }

    // Getters
    public String getUuid() {
        return uuid;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public PolarBearLocation getLocation() {
        return location;
    }

    public int getWeek() {
        return week;
    }

    public int getPoints() {
        return points;
    }

    public List<String> getPreviousLocations() {
        return previousLocations;
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
