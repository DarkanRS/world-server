package com.rs.game.content.dnds.shootingstar;

import com.rs.game.content.skills.mining.Mining;
import com.rs.game.content.skills.mining.RockType;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.*;

@PluginEventHandler
public class ShootingStars {
    public enum Location {
        AL_KHARID(Tile.of(3286, 3197, 0), "Al Kharid"),
        LUMBRIDGE_SWAMP(Tile.of(3217, 3189, 0), "Lumbridge Swamp"),
        DRAYNOR(Tile.of(3076, 3278, 0), "Draynor Village"),
        RIMMINGTON(Tile.of(2972, 3249, 0), "Rimmington"),
        FALADOR(Tile.of(2966, 3379, 0), "Falador"),
        DWARVEN_MINE(Tile.of(3017, 3445, 0), "Ice Mountain"),
        EDGEVILLE(Tile.of(3045, 3515, 0), "Edgeville"),
        CHAMPIONS_GUILD(Tile.of(3167, 3362, 0), "the Champion's Guild"),
        GRAND_EXCHANGE(Tile.of(3136, 3456, 0), "the Grand Exchange"),
        BARBARIAN_VILLAGE(Tile.of(3064, 3434, 0), "Barbarian Village"),
        CATHERBY(Tile.of(2783, 3465, 0), "Catherby"),
        RANGING_GUILD(Tile.of(2688, 3443, 0), "the Ranging Guild"),
        FISHING_GUILD(Tile.of(2594, 3386, 0), "the Fishing Guild"),
        CLOCK_TOWER(Tile.of(2580, 3244, 0), "the clocktower"),
        FIGHT_ARENA(Tile.of(2563, 3202, 0), "the Fight Arena"),
        YANILLE(Tile.of(2533, 3079, 0), "Yanille"),
        CASTLE_WARS(Tile.of(2456, 3104, 0), "Castle Wars"),
        MOBILISING_ARMIES(Tile.of(2408, 2853, 0), "Mobilising Armies"),
        PHOENIX_LAIR(Tile.of(2285, 3625, 0), "the Phoenix Lair"),
        BARBARIAN_ASSAULT(Tile.of(2542, 3559, 0), "Barbarian Assault"),
        LEGENDS_GUILD(Tile.of(2742, 3390, 0), "the Legend's Guild"),
        KARAMJA_GOLD_MINE(Tile.of(2736, 3222, 0), "the Brimhaven gold mine"),
        TAI_BWO_WANNAI_JUNGLE(Tile.of(2835, 3047, 0), "Tai Bwo Wannai jungle"),
        MUDSKIPPER_POINT(Tile.of(2996, 3124, 0), "Mudskipper Point"),
        KARAMJA_VOLCANO(Tile.of(2824, 3170, 0), "the Karamja volcano"),
        VARROCK_SOUTHEAST_MINE(Tile.of(3290, 3352, 0), "the Varrock south-east mine"),
        AL_KHARID_MINE(Tile.of(3299, 3276, 0), "the Al-Kharid mine"),
        NARDAH(Tile.of(3433, 2915, 0), "Nardah"),
        POLLNIVNEACH(Tile.of(3348, 3014, 0), "Pollnivneach"),
        FREMENNIK_SLAYER_DUNGEON(Tile.of(2773, 3595, 0), "the Fremennik Slayer Dungeon"),
        RELLEKKA(Tile.of(2687, 3691, 0), "Rellekka"),
        PARTY_ROOM(Tile.of(3026, 3365, 0), "the Party Room"),
        LUMBRIDGE_CASTLE(Tile.of(3209, 3203, 0), "Lumbridge Castle"),

        ;

        public final Tile tile;
        public final String description;

        Location(Tile tile, String description) {
            this.tile = tile;
            this.description = description;
        }
    }

    private static Star currentStar = null;
    private static final List<Location> LOCATIONS = new ArrayList<>(Arrays.stream(Location.values()).toList());
    private static Iterator<Location> locIterator = LOCATIONS.iterator();

    @ServerStartupEvent
    public static void schedule() {
        Collections.shuffle(LOCATIONS);
        locIterator = LOCATIONS.iterator();
        WorldTasks.scheduleNthHourly(2, ShootingStars::spawnStar);
    }

    public static void spawnStar() {
        if (currentStar != null)
            currentStar.destroy();
        currentStar = new Star(Utils.randomInclusive(1, 9), nextLocation());
    }

    private static Location nextLocation() {
        if (!locIterator.hasNext())
            locIterator = LOCATIONS.iterator();
        return locIterator.next();
    }

    public static ObjectClickHandler handleStarClick = new ObjectClickHandler(new Object[] { "Crashed star" }, e -> {
        if (!(e.getObject() instanceof Star star) || star.getTier() < 1 || star.getTier() > 9) {
            e.getPlayer().sendMessage("Star is not a real star. Report this to a staff member.");
            return;
        }
        switch(e.getOption()) {
            case "Prospect" -> e.getPlayer().simpleDialogue("This looks like a size " + star.getTier() + " star.");
            case "Mine" -> e.getPlayer().getActionManager().setAction(new Mining(RockType.valueOf("CRASHED_STAR_" + star.getTier()), e.getObject()));
        }
    });
}
