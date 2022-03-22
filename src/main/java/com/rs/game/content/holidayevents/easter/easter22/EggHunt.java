package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class EggHunt {

    public enum Spawns {
        BURTHORPE_LODESTONE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2906,3549,0), "near Burthorpe Lodestone/South of the Burthorpe castle."),
        BURTHORPE_AGILITY(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2920,3569,0), "in the Burthorpe agility course area."),
//        TAVERLEY_MINE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "inside the Taverley mine."),
        TAVERLEY_GATE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2932,3461,0), "in Taverley, near and a bit north of the entrance closest to Falador."),
        WHITE_WOLF_MOUNTAIN(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2856,3457,0), "on the east side of White Wolf Mountain."),
        TAVERLEY_DUNGEON(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2884, 9813,0), "inside Taverley Dungeon."),
        WITCHES_HOUSE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2903,3383,0), "north of the Witch's House/south of Taverley, near the wheat field."),
        DARK_WIZARDS_TOWER(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2895,3351,0), "Northwest of the Dark Wizards' Tower/south of the Witch's House."),
        EXAM_CENTRE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3326,3336,0), "west of the Exam Centre."),
//        DIG_SITE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "west of the Varrock Dig Site."),
        SILVAREA(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3339,3482,0), "in Silvarea, the mountain path between Varrock and Morytania."),
        BRIMHAVEN_FISHING(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2776,3175,0), "in Brimhaven, near the fishing spots in the South."),
        MAGE_TRAINING_ARENA(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3348,3291,0), "a little way in front of the gate leading to the Mage Training Arena."),
        CRAFTING_GUILD(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2949,3269,0), "between the Crafting Guild and the Clan Camp"),
        RIMMINGTON(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2939,3197,0), "south-west area of Rimmington"),
        BANANA_PLANTATION(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2917,3159,0), "in the banana plantation on Musa Point"),
        KARAMJA_VOLCANO(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2872,3148,0), "between Karamja Volcano and Musa Point, near the agility shortcut to the south"),
        GAMERS_GROTTO(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2991,3406,0), "west of Gamers' Grotto (near Falador Lodestone)"),
        GOBLIN_VILLAGE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2947,3479,0), "south of the Goblin Village near Falador"),
        CAPTURED_TEMPLE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2975,3461,0), "directly between the Captured Temple and Ice Mountain"),
        NEDS_HOUSE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3095,3262,0), "in Draynor Village (Near Ned's house)"),
        EAST_DRAYNOR_MANOR(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3160,3338,0), "east of Draynor Manor, in the place that used to be Gnomecopter Tours"),
        WEST_DRAYNOR_MANOR(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3072,3352,0), "between the walls of Falador and Draynor Manor"),
        CHAMPIONS_GUILD(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3236,3344,0), "in the sheep's pen east of the Champions' Guild"),
//        SOUTH_VARROCK(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "south of Varrock, next to the Stone circle"),
        GERTRUDES_HOUSE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3154,3390,0), "near the Cooks' Guild, south of Gertrude's house"),
        BARBARIAN_VILLAGE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3094,3450,0), "east of Barbarian Village, near the unicorns"),
        WEST_GRANDE_EXCHANGE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3112,3483,0), "west of the Grand Exchange, near the Outlaw Camp"),
        LUMBRIDGE_CASTLE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3208,3204,0), "around Lumbridge Castle"),
        LUMBRIDGE_SWAMP(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3206,3143,0), "west of Lumbridge Swamp mining spot, behind Father Urhney's house"),
        EDGEVILLE_MONASTERY(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3036,3488,0), "west of Edgeville Monastery"),
//        ICE_MOUNTAIN(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "on top of Ice Mountain"),
        VARROCK_LUMBER_YARD(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3275, 3457, 0), "east of Varrock, near the Saradomin statue South-West of Lumber Yard"),
        VARROCK_EAST_STATUE(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3308,3490,0), "directly in front of Lumber Yard, southern part"),
        DAEMONHEIM(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3428, 3743,0), "around the back of Daemonheim castle"),
        FREMENNIK_CAMP_DAEMONHEIM(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3463,3677,0), "south of the Fremennik Camp on the Daemonheim Peninsula"),
        MUDSKIPPER_POINT(new GameObject(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2996,3118,0), "in the centre of Mudskipper Point");
    	
        private GameObject egg;
        private String hint;

        private Spawns(GameObject egg, String hint) {
            this.egg = egg;
            this.hint = hint;
        }

		public GameObject getEgg() {
            return egg;
        }

        public String getHint() {
            return hint;
        }

        public static Spawns getSpawnByObject(GameObject o) {
            for (Spawns spawn : Spawns.values()) {
                if (spawn.getEgg().getX() == o.getX() && spawn.getEgg().getY() == o.getY() && spawn.getEgg().getPlane() == o.getPlane())
                    return spawn;
            }
            return null;
        }
    }

    private static int[] varbits = new int[] { 10954, 11014, 11015, 11016, 11017 };
    private static boolean[] hasBeenFound = new boolean[5];
    
    private static List<Integer> eggs = new ArrayList<Integer>();
    
    private static int hunt = 0;
    private static int chocatriceScore = 0;
    private static int evilChickenScore = 0;

    public EggHunt() { }

    public static LoginHandler resetVars = new LoginHandler() {
        @Override
        public void handle(LoginEvent e) {
            if (!Easter2022.ENABLED)
                return;
            if (e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", 0) != hunt) {
            	System.out.println("Players last hunt does not match the current hunt");
                for (int idx = 0; idx < 5; idx++) {
                    e.getPlayer().getVars().saveVarBit(varbits[idx], 0);
                    System.out.println("On login - setting varbit " + varbits[idx] + " back to 0 for " + e.getPlayer().getDisplayName() + ". Current hunt " + hunt + ", last hunt " + e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", 0));
                }
            }
        }
    };

    public void start() {
        hunt++;
        System.out.println("Current hunt: " + hunt);
        while (eggs.size() < 5) {
        	Spawns spawn = Spawns.values()[Utils.random(EggHunt.Spawns.values().length)];
            if (!eggs.contains(spawn.ordinal())) {
                eggs.add(spawn.ordinal());
                GameObject egg = World.getObject(new WorldTile(EggHunt.Spawns.values()[spawn.ordinal()].getEgg()), ObjectType.SCENERY_INTERACT);
                if (egg != null) {
                    egg.setId(70105 + eggs.size());
                    if (Settings.getConfig().isDebug()) { System.out.println("Setting " + egg.getX() + ", " + egg.getY() + " to egg " + (70105 + eggs.size())); }
                }
            }
        }
        for (Player p : World.getPlayers()) {
	        updateVarbits(p, 0, new int[] { 0, 1, 2, 3, 4 });
            if (Settings.getConfig().isDebug()) { System.out.println("Setting varbits to display uncracked eggs for " + p.getDisplayName()); }
        } 
        WorldTasks.schedule(Ticks.fromMinutes((Settings.getConfig().isDebug() ? 5 : 115)), () -> {
            World.sendWorldMessage("<col=ff0000>News: The Easter Egg Hunt has ended! A new one will commence shortly.", false);
            reset();
        });  
        World.sendWorldMessage("<col=ff0000>News: A new Easter Egg Hunt has begun! Speak with the Evil Chicken or Chocatrice in Varrock Square to start it.", false);
    }

    public void reset() {
        for (Player p : World.getPlayers()) {
            p.getNSV().removeB("talkedWithEvilChicken");
            p.getNSV().removeB("talkedWithChocatrice");
            updateVarbits(p, 3, new int[] { 0, 1, 2, 3, 4 });
            if (Settings.getConfig().isDebug()) { System.out.println("Setting varbits to display rubble for " + p.getDisplayName()); }
        }
        System.out.println("Egg list size: " + eggs.size());
        for (int idx : eggs) {
        	System.out.println("updating ordinal " + idx + " from eggs. " + Spawns.values()[idx].getEgg().getX() + " " + Spawns.values()[idx].getEgg().getY());
            GameObject egg = World.getObject(new WorldTile(Spawns.values()[idx].getEgg()), ObjectType.SCENERY_INTERACT);
            if (egg != null && egg.getId() != 69753) { 
                if (Settings.getConfig().isDebug()) { System.out.println("Setting " + egg.getId() + " at " + egg.getX() + ", " + egg.getY() + " back to rubble object id."); }
                egg.setId(69753);
            }
        }
        chocatriceScore = 0;
        evilChickenScore = 0;
        while (eggs.size() > 0)
        	eggs.remove(0);
        hasBeenFound = new boolean[] { false, false, false, false ,false };
    }
    
    public static void updateVarbits(Player p, int value, int... idxs) {
    	if (idxs.length <= 0)
			return; 	
    	for (int idx : idxs) {
    		if (varbits.length > idx)
    			p.getVars().saveVarBit(varbits[idx], value);
    		if (!hasBeenFound[idx] && value == 1) {
				World.sendWorldMessage("<col=ffff00>" + p.getDisplayName() + " has found an egg " + Spawns.values()[eggs.get(idx)].getHint() + ".", false);
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
