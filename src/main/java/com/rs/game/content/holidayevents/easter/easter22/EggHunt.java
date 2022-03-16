package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.Settings;
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
//        TAVERLEY_MINE(new WorldTile(0,0,0), "inside the Taverley mine."),
        TAVERLEY_GATE(new WorldTile(2932,3461,0), "in Taverley, near and a bit north of the entrance closest to Falador."),
        WHITE_WOLF_MOUNTAIN(new WorldTile(2856,3457,0), "on the east side of White Wolf Mountain."),
        TAVERLEY_DUNGEON(new WorldTile(2884, 9813,0), "inside Taverley Dungeon."),
        WITCHES_HOUSE(new WorldTile(2903,3383,0), "north of the Witch's House/south of Taverley, near the wheat field."),
        DARK_WIZARDS_TOWER(new WorldTile(2895,3351,0), "Northwest of the Dark Wizards' Tower/south of the Witch's House."),
        EXAM_CENTRE(new WorldTile(3326,3336,0), "west of the Exam Centre."),
//        DIG_SITE(new WorldTile(0,0,0), "west of the Varrock Dig Site."),
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
//        SOUTH_VARROCK(new WorldTile(0,0,0), "south of Varrock, next to the Stone circle"),
        GERTRUDES_HOUSE(new WorldTile(3154,3390,0), "near the Cooks' Guild, south of Gertrude's house"),
        BARBARIAN_VILLAGE(new WorldTile(3094,3450,0), "east of Barbarian Village, near the unicorns"),
        WEST_GRANDE_EXCHANGE(new WorldTile(3112,3483,0), "west of the Grand Exchange, near the Outlaw Camp"),
        LUMBRIDGE_CASTLE(new WorldTile(3208,3204,0), "around Lumbridge Castle"),
        LUMBRIDGE_SWAMP(new WorldTile(3206,3143,0), "west of Lumbridge Swamp mining spot, behind Father Urhney's house"),
        EDGEVILLE_MONASTERY(new WorldTile(3036,3488,0), "west of Edgeville Monastery"),
//        ICE_MOUNTAIN(new WorldTile(0,0,0), "on top of Ice Mountain"),
        VARROCK_LUMBER_YARD(new WorldTile(3275, 3457, 0), "east of Varrock, near the Saradomin statue South-West of Lumber Yard"),
        VARROCK_EAST_STATUE(new WorldTile(3308,3490,0), "directly in front of Lumber Yard, southern part"),
        DAEMONHEIM(new WorldTile(3428, 3743,0), "around the back of Daemonheim castle"),
        FREMENNIK_CAMP_DAEMONHEIM(new WorldTile(3463,3677,0), "south of the Fremennik Camp on the Daemonheim Peninsula"),
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

    private static int[] varbits = new int[] { 10954, 11014, 11015, 11016, 11017 };
    private static List<Integer> eggs = new ArrayList<Integer>();
    private static boolean[] hasBeenFound = new boolean[5];
    private static int hunt = 0;
    private static int chocatriceScore = 0;
    private static int evilChickenScore = 0;

    public EggHunt() { }

    public static LoginHandler resetVars = new LoginHandler() {
        @Override
        public void handle(LoginEvent e) {
            if (!Easter2022.ENABLED)
                return;
            if (e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", -1) != hunt)
                for (int idx = 0; idx < 5; idx++) {
                    e.getPlayer().getVars().saveVarBit(varbits[idx], 0);
                    System.out.println("On login - setting varbit " + varbits[idx] + "back to 0 for " + e.getPlayer().getDisplayName() + ". Current hunt " + hunt + ", last hunt " + e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", -1));
                }
        }
    };

    public void start() {
        hunt++;
        while (eggs.size() < 5) {
        	Spawns spawn = Spawns.values()[Utils.random(EggHunt.Spawns.values().length)];
            if (!eggs.contains(spawn.ordinal())) {
                eggs.add(spawn.ordinal());
                GameObject egg = World.getObject(EggHunt.Spawns.values()[spawn.ordinal()].getTile());
                if (egg != null) {
                    egg.setId(70105 + eggs.size());
                    if (Settings.getConfig().isDebug()) { System.out.println("Setting " + egg.getX() + ", " + egg.getY() + " to egg " + (70105 + eggs.size())); }
                }
            }
        }
        for (Player p : World.getPlayers()) {
	        updateVarbits(p, 0, new int[] { 1, 2, 3, 4, 5 });
            if (Settings.getConfig().isDebug()) { System.out.println("Setting varbits to display uncracked eggs for " + p.getDisplayName()); }
        } 
        WorldTasks.schedule(Ticks.fromMinutes((Settings.getConfig().isDebug() ? 5 : 115)), () -> {
            World.sendWorldMessage("<col=ff0000>News: The Easter Egg Hunt has ended! A new one will commence shortly.", false);
            reset();
        });  
        World.sendWorldMessage("<col=ff0000>News: A new Easter Egg Hunt has begun! Speak with the Evil Chicken or Chocatrice in Varrock Square to start it.", false);
    }

    public void reset() {
        chocatriceScore = 0;
        evilChickenScore = 0;
        eggs = new ArrayList<Integer>();
        hasBeenFound = new boolean[] { false, false, false, false ,false };
        for (Player p : World.getPlayers()) {
            p.getNSV().removeB("talkedWithEvilChicken");
            p.getNSV().removeB("talkedWithChocatrice");
            updateVarbits(p, 3, new int[] { 1, 2, 3, 4, 5 });
            if (Settings.getConfig().isDebug()) { System.out.println("Setting varbits to display rubble for " + p.getDisplayName()); }
        }
        for (Spawns s : Spawns.values()) {
            GameObject egg = World.getObject(s.getTile());
            if (egg != null) {
                egg.setId(69753);
                if (Settings.getConfig().isDebug()) { System.out.println("Setting " + egg.getX() + ", " + egg.getY() + " back to rubble object id."); }
            }
        }
    }
    
    public static void updateVarbits(Player p, int value, int... idxs) {
    	if (idxs.length <= 0)
			return; 	
    	for (int idx : idxs) {
    		p.getVars().saveVar(varbits[idx], value);
    		if (!hasBeenFound[idx] && value == 1) {
				World.sendWorldMessage("<col=ffff00>" + p.getDisplayName() + " has found an egg " + Spawns.values()[eggs.get(idx)], false);
				hasBeenFound[idx] = true;
    		}
    	}
    }
    
    public static boolean hasFoundHintEgg(Player p) {
        if (p.getVars().getVarBit(varbits[0]) == 3)
            return true;
        return false;
    }
    
    public static int getEggIdx(int ordinal) {
    	if (eggs.size() <= 0)
    		return -1;
    	return eggs.indexOf(ordinal);
    }

    public static String getHint() {
        if (eggs.size() < 1 )
            return "";
        return Spawns.values()[(eggs.get(0))].getHint();
    }

    public static boolean isFinished(Player p) {
        for (int idx = 0; idx < 5; idx++)
            if (varbits[idx] != 3)
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
}
