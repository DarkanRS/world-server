package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.lib.game.WorldTile;

public class EasterEgg {

    public enum Spawns {
        BURTHORPE_LODESTONE(new WorldTile(0,0,0), "near Burthorpe Lodestone/South of the Burthorpe castle."),
        BURTHORPE_AGILITY(new WorldTile(0,0,0), "in the Burthorpe agility course area."),
        TAVERLEY_MINE(new WorldTile(0,0,0), "inside the Taverley mine."),
        TAVERLEY_GATE(new WorldTile(0,0,0), "in Taverley, near and a bit north of the entrance closest to Falador."),
        WHITE_WOLF_MOUNTAIN(new WorldTile(0,0,0), "on the east side of White Wolf Mountain."),
        WITCHES_HOUSE(new WorldTile(2903,3383,0), "north of the Witch's House/south of Taverley, near the wheat field."),
        DARK_WIZARDS_TOWER(new WorldTile(2895,3351,0), "Northwest of the Dark Wizards' Tower/south of the Witch's House."),
        EXAM_CENTRE(new WorldTile(3326,3336,0), "west of the Exam Centre."),
        DIG_SITE(new WorldTile(0,0,0), "west of the Varrock Dig Site."),
        SILVAREA(new WorldTile(0,0,0), "in Silvarea, the mountain path between Varrock and Morytania."),
        BRIMHAVEN_FISHING(new WorldTile(0,0,0), "in Brimhaven, near the fishing spots in the South."),
        MAGE_TRAINING_ARENA(new WorldTile(0,0,0), "a little way in front of the gate leading to the Mage Training Arena."),
        CRAFTING_GUILD(new WorldTile(0,0,0), "between the Crafting Guild and the Clan Camp"),
        RIMMINGTON(new WorldTile(0,0,0), "south-west area of Rimmington"),
        BANANA_PLANTATION(new WorldTile(2917,3159,0), "in the banana plantation on Musa Point"),
        KARAMJA_VOLCANO(new WorldTile(0,0,0), "between Karamja Volcano and Musa Point, near the agility shortcut to the south"),
        GAMERS_GROTTO(new WorldTile(2991,3406,0), "west of Gamers' Grotto (near Falador Lodestone)"),
        GOBLIN_VILLAGE(new WorldTile(0,0,0), "south of the Goblin Village near Falador"),
        CAPTURED_TEMPLE(new WorldTile(0,0,0), "directly between the Captured Temple and Ice Mountain"),
        NEDS_HOUSE(new WorldTile(0,0,0), "in Draynor Village (Near Ned's house)"),
        EAST_DRAYNOR_MANOR(new WorldTile(0,0,0), "east of Draynor Manor, in the place that used to be Gnomecopter Tours"),
        WEST_DRAYNOR_MANOR(new WorldTile(3072,3352,0), "between the walls of Falador and Draynor Manor"),
        CHAMPIONS_GUILD(new WorldTile(0,0,0), "in the sheep's pen east of the Champions' Guild"),
        SOUTH_VARROCK(new WorldTile(0,0,0), "south of Varrock, next to the Stone circle"),
        GERTRUDES_HOUSE(new WorldTile(3154,3390,0), "near the Cooks' Guild, south of Gertrude's house"),
        BARBARIAN_VILLAGE(new WorldTile(0,0,0), "east of Barbarian Village, near the unicorns"),
        WEST_GRANDE_EXCHANGE(new WorldTile(0,0,0), "west of the Grand Exchange, near the Outlaw Camp"),
        LUMBRIDGE_CASTLE(new WorldTile(0,0,0), "around Lumbridge Castle"),
        LUMBRIDGE_SWAMP(new WorldTile(3206,3143,0), "west of Lumbridge Swamp mining spot, behind Father Urhney's house"),
        EDGEVILLE_MONASTERY(new WorldTile(0,0,0), "west of Edgeville Monastery"),
        ICE_MOUNTAIN(new WorldTile(0,0,0), "on top of Ice Mountain"),
        VARROCK_LUMBER_YARD(new WorldTile(0,0,0), "east of Varrock, near the Saradomin statue South-West of Lumber Yard"),
        VARROCK_EAST_STATUE(new WorldTile(0,0,0), "directly in front of Lumber Yard, southern part"),
        DAEMONHEIM(new WorldTile(3428, 3743,0), "around the back of Daemonheim castle"),
        FREMENNIK_CAMP_DAEMONHEIM(new WorldTile(0,0,0), "south of the Fremennik Camp on the Daemonheim Peninsula"),
        MUDSKIPPER_POINT(new WorldTile(0,0,0), "in the centre of Mudskipper Point"),

        /*
         * Possible spawn locations
         *    North of the Witch's House/south of Taverley, near the wheat field.
         *    Northwest of the Dark Wizards' Tower/south of the Witch's House.
         *    West of the Exam Centre.
         *    West of the Varrock Dig Site.
         *    In Silvarea, the mountain path between Varrock and Morytania.
         *    In Brimhaven, near the fishing spots in the South.
         *    A little way in front of the gate leading to the Mage Training Arena.
         *    Between the Crafting Guild and the Clan Camp
         *    South-west area of Rimmington
         *    In the banana plantation on Musa Point
         *    Between Karamja Volcano and Musa Point, near the agility shortcut to the south
         *    West of Gamers' Grotto (near Falador Lodestone)
         *    South of the Goblin Village near Falador
         *    Directly between the Captured Temple and Ice Mountain
         *    In Draynor Village (Near Ned's house)
         *    East of Draynor Manor, in the place that used to be Gnomecopter Tours
         *    Between the walls of Falador and Draynor Manor
         *    In the sheep's pen east of the Champions' Guild
         *    South of Varrock, next to the Stone circle
         *    Near the Cooks' Guild, south of Gertrude's house
         *    East of Barbarian Village, near the unicorns
         *    West of the Grand Exchange, near the Outlaw Camp
         *    Around Lumbridge Castle
         *    West of Lumbridge Swamp mining spot, behind Father Urhney's house
         *    West of Edgeville Monastery
         *    On top of Ice Mountain
         *    East of Varrock, near the Saradomin statue South-West of Lumber Yard
         *    Directly in front of Lumber Yard, southern part
         *    Around the back of Daemonheim castle
         *    South of the Fremennik Camp on the Daemonheim Peninsula
         *    In the centre of Mudskipper Point
         */
        ;

        private WorldTile tile;
        private String hint;

        private Spawns(WorldTile tile, String hint) {
            this.tile = tile;
            this.hint = hint;
        }

        public WorldTile getTile() {
            return tile;
        }

        public String getHint() {
            return hint;
        }

        public static Spawns isLocation(int x, int y, int plane) {
            for (Spawns spawn : Spawns.values()) {
                if (spawn.getTile().getX() == x && spawn.getTile().getY() == y && spawn.getTile().getPlane() == plane)
                    return spawn;
            }
            return null;
        }
    }
}
