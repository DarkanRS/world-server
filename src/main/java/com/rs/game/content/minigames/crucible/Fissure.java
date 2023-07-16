package com.rs.game.content.minigames.crucible;

import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;

import java.util.HashMap;
import java.util.Map;

public enum Fissure {
    WEST_BANK(Tile.of(3210, 6146, 0)),
    NORTH_BANK(Tile.of(3267, 6198, 0)),
    EAST_BANK(Tile.of(3317, 6139, 0)),
    SOUTH_BANK(Tile.of(3263, 6090, 0)),
    CENTER(Tile.of(3264, 6131, 0)),
    SOUTHEAST(Tile.of(3292, 6117, 0)),
    EAST(Tile.of(3278, 6152, 0)),
    NORTHEAST(Tile.of(3287, 6174, 0)),
    NORTH(Tile.of(3259, 6184, 0)),
    NORTHWEST(Tile.of(3248, 6156, 0)),
    WEST(Tile.of(3228, 6143, 0)),
    SOUTHWEST(Tile.of(3225, 6114, 0)),
    SOUTH(Tile.of(3257, 6101, 0));

    public final Tile location;

    private static Map<Integer, Fissure> LOC_MAP = new HashMap<>();

    static {
        for (Fissure fissure : Fissure.values())
            LOC_MAP.put(fissure.location.getTileHash(), fissure);
    }

    Fissure(Tile location) {
        this.location = location;
    }

    public static Fissure forLocation(Tile location) {
        return LOC_MAP.get(location.getTileHash());
    }
}
