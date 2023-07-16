package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class EggHunt {

    public enum Spawns {
        BURTHORPE_LODESTONE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2906,3549,0), "near Burthorpe Lodestone/South of the Burthorpe castle."),
        BURTHORPE_AGILITY(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2920,3569,0), "in the Burthorpe agility course area."),
//        TAVERLEY_MINE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "inside the Taverley mine."),
        TAVERLEY_GATE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2932,3461,0), "in Taverley, near and a bit north of the entrance closest to Falador."),
        WHITE_WOLF_MOUNTAIN(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2856,3457,0), "on the east side of White Wolf Mountain."),
        TAVERLEY_DUNGEON(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2884, 9813,0), "inside Taverley Dungeon."),
        WITCHES_HOUSE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2903,3383,0), "north of the Witch's House/south of Taverley, near the wheat field."),
        DARK_WIZARDS_TOWER(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2895,3351,0), "Northwest of the Dark Wizards' Tower/south of the Witch's House."),
        EXAM_CENTRE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3326,3336,0), "west of the Exam Centre."),
//        DIG_SITE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "west of the Varrock Dig Site."),
        SILVAREA(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3339,3482,0), "in Silvarea, the mountain path between Varrock and Morytania."),
        BRIMHAVEN_FISHING(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2776,3175,0), "in Brimhaven, near the fishing spots in the South."),
        MAGE_TRAINING_ARENA(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3348,3291,0), "a little way in front of the gate leading to the Mage Training Arena."),
        CRAFTING_GUILD(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2949,3269,0), "between the Crafting Guild and the Clan Camp."),
        RIMMINGTON(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2939,3197,0), "south-west area of Rimmington."),
        BANANA_PLANTATION(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2917,3159,0), "in the banana plantation on Musa Point."),
        KARAMJA_VOLCANO(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2872,3148,0), "between Karamja Volcano and Musa Point, near the agility shortcut to the south."),
        GAMERS_GROTTO(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2991,3406,0), "west of Gamers' Grotto (near Falador Lodestone)."),
        GOBLIN_VILLAGE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2947,3479,0), "south of the Goblin Village near Falador."),
        CAPTURED_TEMPLE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2975,3461,0), "directly between the Captured Temple and Ice Mountain."),
        NEDS_HOUSE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3095,3262,0), "in Draynor Village (Near Ned's house)."),
        EAST_DRAYNOR_MANOR(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3160,3338,0), "east of Draynor Manor, in the place that used to be Gnomecopter Tours."),
        WEST_DRAYNOR_MANOR(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3072,3352,0), "between the walls of Falador and Draynor Manor."),
        CHAMPIONS_GUILD(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3236,3344,0), "in the sheep's pen east of the Champions' Guild."),
//        SOUTH_VARROCK(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "south of Varrock, next to the Stone circle"),
        GERTRUDES_HOUSE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3154,3390,0), "near the Cooks' Guild, south of Gertrude's house."),
        BARBARIAN_VILLAGE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3094,3450,0), "east of Barbarian Village, near the unicorns."),
        WEST_GRANDE_EXCHANGE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3112,3483,0), "west of the Grand Exchange, near the Outlaw Camp."),
        LUMBRIDGE_CASTLE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3208,3204,0), "around Lumbridge Castle."),
        LUMBRIDGE_SWAMP(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3206,3143,0), "west of Lumbridge Swamp mining spot, behind Father Urhney's house."),
        EDGEVILLE_MONASTERY(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3036,3488,0), "west of Edgeville Monastery."),
//        ICE_MOUNTAIN(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,0,0,0), "on top of Ice Mountain"),
        VARROCK_LUMBER_YARD(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3275, 3457,0), "east of Varrock, near the Saradomin statue South-West of Lumber Yard."),
        VARROCK_EAST_STATUE(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3308,3490,0), "directly in front of Lumber Yard, southern part."),
        DAEMONHEIM(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3428, 3743,0), "around the back of Daemonheim castle."),
        FREMENNIK_CAMP_DAEMONHEIM(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,3463,3677,0), "south of the Fremennik Camp on the Daemonheim Peninsula."),
        MUDSKIPPER_POINT(new EventEasterEgg(Easter2022.RUBBLE,ObjectType.SCENERY_INTERACT,0,2996,3118,0), "in the centre of Mudskipper Point.");
    	
        private EventEasterEgg egg;

        private Spawns(EventEasterEgg egg, String hint) {
            this.egg = egg;
            egg.setHint(hint);
        }

		public EventEasterEgg getEgg() {
            return egg;
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
	private static List<Integer> eggs = new ArrayList<Integer>();
	private static int hunt = 0;
	private static int chocatriceScore = 0;
	private static int evilChickenScore = 0;
	private static int timer = 1;
	private static boolean active = false;
    
    public EggHunt() { 
    	WorldTasks.scheduleTimer(0, (ticks) -> {
    		if (!Easter2022.ENABLED)
    			return false;
    		timer--;
    		switch (timer) {
	    		case 12000 -> start();
	    		case 1000 -> {
	    			if (Settings.getConfig().isDebug())
	    				start();
	    		}
	    		case 500 -> end();
	    		case 0 -> timer = Settings.getConfig().isDebug() ? 1001 : 12001;
    		}
    		return true;
    	});
    }

    public static LoginHandler resetVars = new LoginHandler(e -> {
    	if (!Easter2022.ENABLED)
            return;
        if (e.getPlayer().getI(Easter2022.STAGE_KEY+"CurrentHunt", 0) != hunt)
            for (int idx = 0; idx < 5; idx++)
                e.getPlayer().getVars().saveVarBit(varbits[idx], 0);
    });

    public void start() {
    	active = true;
    	hunt++;
    	while (eggs.size() > 0)
    		eggs.remove(0);
        while (eggs.size() < 5) {
        	Spawns spawn = Spawns.values()[Utils.random(EggHunt.Spawns.values().length)];
            if (!eggs.contains(spawn.ordinal())) {
                eggs.add(spawn.ordinal());
                spawn.getEgg().setId(70105 + eggs.size());
        		Logger.debug(EggHunt.class, "start", "Setting egg [" + spawn.ordinal() + "] " + spawn.getEgg().getX() + ", " + spawn.getEgg().getY() + " with a varbit of " + spawn.getEgg().getDefinitions().varpBit);
            }
        }
        for (Player p : World.getPlayers()) {
            for (int idx = 0; idx < 5; idx++)
                p.getVars().saveVarBit(varbits[idx], 0);
            p.getNSV().removeB("talkedWithEvilChicken");
			p.getNSV().removeB("talkedWithChocatrice");
        }
        World.sendWorldMessage("<col=ff0000>News: A new Easter Egg Hunt has begun! Speak with the Evil Chicken or Chocatrice in Varrock Square to start it.", false);
    }
    
    public void end() {
    	active = false;
    	for (int spawn : eggs) {
    		Spawns.values()[spawn].getEgg().setId(Easter2022.RUBBLE);
    		Spawns.values()[spawn].getEgg().setFound(false);
    	}
    	World.sendWorldMessage("<col=ff0000>News: The Easter Egg Hunt has ended! A new one will commence shortly.", false);
    }
 
    public static boolean hasCompletedHunt(Player p) {
    	if (p.getI(Easter2022.STAGE_KEY+"CurrentHunt", 0) != hunt)
    		return false;
        for (int idx = 0; idx < 5; idx++)
            if (p.getVars().getVarBit(varbits[idx]) != 3)
                return false;
        return true;
    }

    public static int getChocatriceScore() {
        return chocatriceScore;
    }

    public static int getEvilChickenScore() {
        return evilChickenScore;
    }

    public static int getHunt() {
        return hunt;
    }
    
    public static String getHint() {
    	return Spawns.values()[eggs.get(0)].getEgg().getHint();
    }
    
    public static int getTime() {
    	return (int)Math.ceil(timer/100);
    }
    
    public static String getTimeString() {
    	if (active)
    		return "The hunt will be ending in " + (getTime()-5) + ((getTime()-5 > 1) ? " minutes." : " minute.");
    	else {
    		if (getTime() == 0)
    			return "The next hunt will be starting in less than a minute.";
    		return "The next hunt will be starting in " + getTime() + ((getTime() > 1) ? " minutes." : " minute.");
    	}
    }

	public static void incrementScore(int attackStyle) {
		if (attackStyle == 0)
			evilChickenScore++;
		else
			chocatriceScore++;		
	}
	
	public static boolean active() {
		return active;
	}
}
