package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EggHunt {

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

//   70106 - null - transforms into with vb10954:
//   [0: 70103 (Easter egg)],
//   [1: 70104 (Easter egg (cracked))],
//   [2: 70105 (Easter Egg (Cracked))],
//   [3: 69753 (Rubble)],
//   [4: INVISIBLE],
//
//   70107 - null - transforms into with vb11014:
//   [0: 70103 (Easter egg)],
//   [1: 70104 (Easter egg (cracked))],
//   [2: 70105 (Easter Egg (Cracked))],
//   [3: 69753 (Rubble)],
//   [4: INVISIBLE],
//
//   70108 - null - transforms into with vb11015:
//   [0: 70103 (Easter egg)],
//   [1: 70104 (Easter egg (cracked))],
//   [2: 70105 (Easter Egg (Cracked))],
//   [3: 69753 (Rubble)],
//   [4: INVISIBLE],
//
//   70109 - null - transforms into with vb11016:
//   [0: 70103 (Easter egg)],
//   [1: 70104 (Easter egg (cracked))],
//   [2: 70105 (Easter Egg (Cracked))],
//   [3: 69753 (Rubble)],
//   [4: INVISIBLE],
//
//   70110 - null - transforms into with vb11017:
//   [0: 70103 (Easter egg)],
//   [1: 70104 (Easter egg (cracked))],
//   [2: 70105 (Easter Egg (Cracked))],
//   [3: 69753 (Rubble)],
//   [4: INVISIBLE],


    private static int[] vars = new int[] { 10954, 11014, 11015, 11016, 11017 };
    private static List<Spawns> eggs = new ArrayList<Spawns>();
    private static boolean[] hasBeenFound = new boolean[5];
    private static int hunt = 0;
    private static int chocatriceScore = 0;
    private static int evilChickenScore = 0;
    private static boolean ended = false;

    public EggHunt() { }

    public static LoginHandler resetVars = new LoginHandler() {
        @Override
        public void handle(LoginEvent e) {
            if (!Easter2022.ENABLED)
                return;
            if (e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", -1) != hunt)
                for (int idx = 0; idx < 5; idx++)
                    e.getPlayer().getVars().saveVar(vars[idx], 0);
        }
    };

    public static void start() {
        hunt++;
        long endOfHunt = World.getServerTicks() + Ticks.fromMinutes(115);
        while (eggs.size() < 5) {
            int random = Utils.random(EggHunt.Spawns.values().length);
            if (!eggs.contains(EggHunt.Spawns.values()[random])) {
                eggs.add(EggHunt.Spawns.values()[random]);
                GameObject easterEgg = World.getObject(EggHunt.Spawns.values()[random].getTile());
                if (easterEgg != null)
                    easterEgg.setId(70105 + eggs.size());
            }
        }
        WorldTasks.schedule(Ticks.fromMinutes(115), () -> {
            World.sendWorldMessage("<col=ff0000>News: The Easter Egg Hunt has ended! A new one will commence shortly.", false);
            reset();
            ended = true;
        });
        World.sendWorldMessage("<col=ff0000>News: A new Easter Egg Hunt has begun! Speak with the Evil Chicken or Chocatrice in Varrock Square to start it.", false);
    }

    public static void reset() {
        chocatriceScore = 0;
        evilChickenScore = 0;
        eggs = new ArrayList<Spawns>();
        hasBeenFound = new boolean[] { false, false, false, false ,false };
        ended = false;
        for (Player p : World.getPlayers()) {
            p.getNSV().removeB("talkedWithEvilChicken");
            p.getNSV().removeB("talkedWithChocatrice");
            for (int idx = 0; idx < 5; idx++)
                p.getVars().saveVar(vars[idx], 0);
        }
        for (Spawns s : Spawns.values()) {
            GameObject egg = World.getObject(s.getTile());
            if (egg != null)
                egg.setId(69753);
        }
    }

    public static void updateEgg(Player p, GameObject o, int varValue) {
        Spawns loc = Spawns.getEggByLocation(o.getX(), o.getY(), o.getPlane());
        if (loc != null) {
            int idx = Arrays.asList(eggs).indexOf(loc.ordinal());
            if (idx > -1 && idx < 5) {
                if (!hasBeenFound[idx] && varValue == 1) {
                    World.sendWorldMessage("<col=ffff00>" + p.getDisplayName() + " has found an egg " + loc.getHint(), false);
                    hasBeenFound[idx] = true;
                }
                p.getVars().saveVar(vars[idx], varValue);
            }
        }
    }

    public static boolean hasFoundHintEgg(Player p) {
        if (p.getVars().getVar(vars[0]) == 3)
            return true;
        return false;
    }

    public static String getHint() {
        if (eggs.size() < 1 )
            return "";
        return eggs.get(0).getHint();
    }

    public static boolean isFinished(Player p) {
        for (int idx = 0; idx < 5; idx++)
            if (vars[idx] != 3)
                return false;
        return true;
    }

    public static void incrementChocatriceScore() {
        chocatriceScore++;
    }

    public static int getChocatriceScore() {
        return chocatriceScore;
    }

    public static void incrementEvilChickenScore() {
        evilChickenScore++;
    }

    public static int getEvilChickenScore() {
        return evilChickenScore;
    }

    public static int getHunt() {
        return hunt;
    }

    public static boolean hasEnded() {
        return ended;
    }
}
