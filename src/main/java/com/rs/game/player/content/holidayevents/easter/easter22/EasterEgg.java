package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.lib.game.WorldTile;

public class EasterEgg {

    public enum Spawns {
        BURTHORPE_LODESTONE(new WorldTile(2906,3549,0), "near Burthorpe Lodestone/South of the Burthorpe castle."),
        BURTHORPE_AGILITY(new WorldTile(2920,3569,0), "in the Burthorpe agility course area."),
        TAVERLEY_MINE(new WorldTile(0,0,0), "inside the Taverley mine."),
        TAVERLEY_GATE(new WorldTile(2932,3461,0), "in Taverley, near and a bit north of the entrance closest to Falador."),
        WHITE_WOLF_MOUNTAIN(new WorldTile(2856,3457,0), "on the east side of White Wolf Mountain."),
        TAVERLEY_DUNGEON(new WorldTile(2884, 9813,0), "inside Taverley Dungeon."),
        WITCHES_HOUSE(new WorldTile(2903,3383,0), "north of the Witch's House/south of Taverley, near the wheat field."),
        DARK_WIZARDS_TOWER(new WorldTile(2895,3351,0), "Northwest of the Dark Wizards' Tower/south of the Witch's House."),
        EXAM_CENTRE(new WorldTile(3326,3336,0), "west of the Exam Centre."),
        DIG_SITE(new WorldTile(0,0,0), "west of the Varrock Dig Site."),
        SILVAREA(new WorldTile(3339,3482,0), "in Silvarea, the mountain path between Varrock and Morytania."),
        BRIMHAVEN_FISHING(new WorldTile(2776,3175,0), "in Brimhaven, near the fishing spots in the South."),
        MAGE_TRAINING_ARENA(new WorldTile(3348,3291,0), "a little way in front of the gate leading to the Mage Training Arena."),
        CRAFTING_GUILD(new WorldTile(2949,3269,0), "between the Crafting Guild and the Clan Camp"),
        RIMMINGTON(new WorldTile(2939,3197,0), "south-west area of Rimmington"),
        BANANA_PLANTATION(new WorldTile(2917,3159,0), "in the banana plantation on Musa Point"),
        KARAMJA_VOLCANO(new WorldTile(2872,3148,0), "between Karamja Volcano and Musa Point, near the agility shortcut to the south"),
        GAMERS_GROTTO(new WorldTile(2991,3406,0), "west of Gamers' Grotto (near Falador Lodestone)"),
        GOBLIN_VILLAGE(new WorldTile(2947,3479,0), "south of the Goblin Village near Falador"),
        CAPTURED_TEMPLE(new WorldTile(2975,3461,0), "directly between the Captured Temple and Ice Mountain"),
        NEDS_HOUSE(new WorldTile(3095,3262,0), "in Draynor Village (Near Ned's house)"),
        EAST_DRAYNOR_MANOR(new WorldTile(3160,3338,0), "east of Draynor Manor, in the place that used to be Gnomecopter Tours"),
        WEST_DRAYNOR_MANOR(new WorldTile(3072,3352,0), "between the walls of Falador and Draynor Manor"),
        CHAMPIONS_GUILD(new WorldTile(3236,3344,0), "in the sheep's pen east of the Champions' Guild"),
        SOUTH_VARROCK(new WorldTile(0,0,0), "south of Varrock, next to the Stone circle"),
        GERTRUDES_HOUSE(new WorldTile(3154,3390,0), "near the Cooks' Guild, south of Gertrude's house"),
        BARBARIAN_VILLAGE(new WorldTile(3094,3450,0), "east of Barbarian Village, near the unicorns"),
        WEST_GRANDE_EXCHANGE(new WorldTile(3112,3483,0), "west of the Grand Exchange, near the Outlaw Camp"),
        LUMBRIDGE_CASTLE(new WorldTile(3208,3204,0), "around Lumbridge Castle"),
        LUMBRIDGE_SWAMP(new WorldTile(3206,3143,0), "west of Lumbridge Swamp mining spot, behind Father Urhney's house"),
        EDGEVILLE_MONASTERY(new WorldTile(3036,3488,0), "west of Edgeville Monastery"),
        ICE_MOUNTAIN(new WorldTile(0,0,0), "on top of Ice Mountain"),
        VARROCK_LUMBER_YARD(new WorldTile(3275, 3457, 0), "east of Varrock, near the Saradomin statue South-West of Lumber Yard"),
        VARROCK_EAST_STATUE(new WorldTile(3308,3490,0), "directly in front of Lumber Yard, southern part"),
        DAEMONHEIM(new WorldTile(3428, 3743,0), "around the back of Daemonheim castle"),
        FREMENNIK_CAMP_DAEMONHEIM(new WorldTile(3463,34677,0), "south of the Fremennik Camp on the Daemonheim Peninsula"),
        MUDSKIPPER_POINT(new WorldTile(2996,3118,0), "in the centre of Mudskipper Point");

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

        public static Spawns getEggByLocation(int x, int y, int plane) {
            for (Spawns spawn : Spawns.values()) {
                if (spawn.getTile().getX() == x && spawn.getTile().getY() == y && spawn.getTile().getPlane() == plane)
                    return spawn;
            }
            return null;
        }
    }
}
