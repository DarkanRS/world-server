package com.rs.game.content.dnds.shootingstar;

import com.rs.lib.game.Tile;

public class ShootingStars {
    public enum Location {
        AL_KHARID(Tile.of(3286, 3197, 0)),
        LUMBRIDGE_SWAMP(Tile.of(3217, 3189, 0)),
        DRAYNOR(Tile.of(3076, 3278, 0)),
        RIMMINGTON(Tile.of(2972, 3249, 0)),
        FALADOR(Tile.of(2966, 3379, 0)),
        DWARVEN_MINE(Tile.of(3008, 3392, 0)),
        EDGEVILLE(Tile.of(3045, 3515, 0)),
        CHAMPIONS_GUILD(Tile.of(3167, 3362, 0)),
        GRAND_EXCHANGE(Tile.of(3136, 3456, 0)),
        BARBARIAN_VILLAGE(Tile.of(3064, 3434, 0)),
        CATHERBY(Tile.of(2783, 3465, 0)),
        RANGING_GUILD(Tile.of(2685, 3443, 0)),
        FISHING_GUILD(Tile.of(2594, 3384, 0)),
        CLOCK_TOWER(Tile.of(2560, 3200, 0)),
        FIGHT_ARENA(Tile.of(2572, 3135, 0)),
        YANILLE(Tile.of(2531, 3079, 0)),
        CASTLE_WARS(Tile.of(2459, 3100, 0)),
        MOBILISING_ARMIES(Tile.of(2408, 2853, 0)),
        PHOENIX_LAIR(Tile.of(2280, 3621, 0)),
        BARBARIAN_ASSAULT(Tile.of(2527, 3590, 0)),
        LEGENDS_GUILD(Tile.of(2740, 3340, 0)),
        KARAMJA_GOLD_MINE(Tile.of(2732, 3220, 0)),
        TAI_BWO_WANNAI_JUNGLE(Tile.of(2835, 3047, 0)),
        MUDSKIPPER_POINT(Tile.of(2996, 3124, 0)),
        KARAMJA_VOLCANO(Tile.of(2821, 3164, 0)),
        VARROCK_SOUTHEAST_MINE(Tile.of(3290, 3352, 0)),
        AL_KHARID_MINE(Tile.of(3305, 3276, 0)),
        NARDAH(Tile.of(3433, 2915, 0)),
        POLLNIVNEACH(Tile.of(3348, 3011, 0)),
        FREMENNIK_SLAYER_DUNGEON(Tile.of(2773, 3595, 0)),
        RELLEKKA(Tile.of(2674, 3698, 0)),
        PARTY_ROOM(Tile.of(3026, 3365, 0)),
        LUMBRIDGE_CASTLE(Tile.of(3200, 3200, 0)),

        ;

        private Tile tile;

        Location(Tile tile) {
            this.tile = tile;
        }
    }

}
