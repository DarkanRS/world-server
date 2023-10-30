package com.rs.game.content.dnds.shootingstar;

import com.rs.game.content.skills.mining.Mining;
import com.rs.game.content.skills.mining.RockType;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class ShootingStars {
    public enum Location {
        AL_KHARID(Tile.of(3286, 3197, 0)),
        LUMBRIDGE_SWAMP(Tile.of(3217, 3189, 0)),
        DRAYNOR(Tile.of(3076, 3278, 0)),
        RIMMINGTON(Tile.of(2972, 3249, 0)),
        FALADOR(Tile.of(2966, 3379, 0)),
        DWARVEN_MINE(Tile.of(3017, 3445, 0)),
        EDGEVILLE(Tile.of(3045, 3515, 0)),
        CHAMPIONS_GUILD(Tile.of(3167, 3362, 0)),
        GRAND_EXCHANGE(Tile.of(3136, 3456, 0)),
        BARBARIAN_VILLAGE(Tile.of(3064, 3434, 0)),
        CATHERBY(Tile.of(2783, 3465, 0)),
        RANGING_GUILD(Tile.of(2688, 3443, 0)),
        FISHING_GUILD(Tile.of(2594, 3386, 0)),
        CLOCK_TOWER(Tile.of(2580, 3244, 0)),
        FIGHT_ARENA(Tile.of(2563, 3202, 0)),
        YANILLE(Tile.of(2533, 3079, 0)),
        CASTLE_WARS(Tile.of(2456, 3104, 0)),
        MOBILISING_ARMIES(Tile.of(2408, 2853, 0)),
        PHOENIX_LAIR(Tile.of(2285, 3625, 0)),
        BARBARIAN_ASSAULT(Tile.of(2542, 3559, 0)),
        LEGENDS_GUILD(Tile.of(2742, 3390, 0)),
        KARAMJA_GOLD_MINE(Tile.of(2736, 3222, 0)),
        TAI_BWO_WANNAI_JUNGLE(Tile.of(2835, 3047, 0)),
        MUDSKIPPER_POINT(Tile.of(2996, 3124, 0)),
        KARAMJA_VOLCANO(Tile.of(2824, 3170, 0)),
        VARROCK_SOUTHEAST_MINE(Tile.of(3290, 3352, 0)),
        AL_KHARID_MINE(Tile.of(3299, 3276, 0)),
        NARDAH(Tile.of(3433, 2915, 0)),
        POLLNIVNEACH(Tile.of(3348, 3014, 0)),
        FREMENNIK_SLAYER_DUNGEON(Tile.of(2773, 3595, 0)),
        RELLEKKA(Tile.of(2687, 3691, 0)),
        PARTY_ROOM(Tile.of(3026, 3365, 0)),
        LUMBRIDGE_CASTLE(Tile.of(3209, 3203, 0)),

        ;

        public Tile tile;

        Location(Tile tile) {
            this.tile = tile;
        }
    }

    public static ObjectClickHandler handleStarClick = new ObjectClickHandler(new Object[] { "Crashed star" }, e -> {
        if (!(e.getObject() instanceof Star star)) {
            e.getPlayer().sendMessage("Star is not a real star. Report this to a staff member.");
            return;
        }
        switch(e.getOption()) {
            case "Prospect" -> e.getPlayer().simpleDialogue("This looks like a size " + star.getTier() + " star.");
            case "Mine" -> e.getPlayer().getActionManager().setAction(new Mining(RockType.valueOf("CRASHED_STAR_" + star.getTier()), e.getObject()));
        }
    });
}
