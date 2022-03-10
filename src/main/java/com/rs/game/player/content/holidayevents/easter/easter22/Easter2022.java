package com.rs.game.player.content.holidayevents.easter.easter22;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.managers.EmotesManager;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.Ticks;
import com.rs.utils.music.Music;
import com.rs.utils.music.Song;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@PluginEventHandler
public class Easter2022 {

    //Event configs
    public static String STAGE_KEY = "easter2022";
    public static Boolean ENABLED = true;

    //Rewards
    public static final int PERMANENT_EGGSTERMINATOR = 24146;
    public static final int XP_LAMP = 2528;
    public static final int EGG_ON_FACE_MASK = 24149;
    public static final int CHOCOLATE_EGG_ON_FACE_MASK = 24150;
    public static final Song EASTER_TRACK = Music.getSong(273); //Easter Jigg
    public static final EmotesManager.Emote EASTER_EMOTE = EmotesManager.Emote.AROUND_THE_WORLD;

    //Event Items
    public static final int EGGSTERMINATOR = 24145;
    public static final int CHOCOTREAT = 24148;
    public static final int EVIL_DRUMSTICK = 24147;

    //Event Objects
    public static final int UNCRACKED_EGG = 70103;
    public static final int CRACKED_EGG = 70104;
    public static final int POSSIBLE_EGG = 70105;

    //Event NPCs
    public static final int CHOCOCHICK = 15270;
    public static final int CHICK = 15271;
    public static final int EVIL_CHICKEN = 15262;
    public static final int CHOCATRICE = 15259;
    public static final int EVIL_CHICKEN_MEDIUM = 15263;
    public static final int CHOCATRICE_MEDIUM = 15260;
    public static final int EVIL_CHICKEN_LARGE = 15264;
    public static final int CHOCATRICE_LARGE = 15261;

    public static List<EasterEgg.Spawns> currentEggs = new ArrayList<EasterEgg.Spawns>();

    private static List<String> completedEgg1 = new ArrayList<String>();
    private static List<String> completedEgg2 = new ArrayList<String>();
    private static List<String> completedEgg3 = new ArrayList<String>();
    private static List<String> completedEgg4 = new ArrayList<String>();
    private static List<String> completedEgg5 = new ArrayList<String>();

    /*
     *   Runescape 2012 Easter event - Chocatrice vs Evil Chicken.
     *
     *   Rewards -
     *       1 hunt - xp lamp (skill*10) per hunt completed
     *       1 hunt - previously unlocked easter emotes (Bunny-hop, Around the World in Eggty Days, Invoke Spring - 2011)
     *       3 hunts completed -> Permanent eggsterminator unlocked
     *       3 drumsticks given to the evil chicken -> Egg on face mask
     *       3 chocolate treats given to the chocatrice -> Chocolate egg on face mask
     */

    @ServerStartupEvent
    public static void EasterEvent2022() {
        AtomicLong currentDate = new AtomicLong(Instant.now().getEpochSecond());
        long startDate = 1648771200; //April 1st - 00:00
        long endDate = 1649635200; //April 11th - 00:00

        if (!ENABLED || currentDate.get() >= endDate)
            return;

        //Start 10 ticks after startup, or schedule a task to start on April 1st at 00:00.
        int ticksToStart = (Settings.getConfig().isDebug() || currentDate.get() >= startDate) ? 10 : Ticks.fromSeconds((int)(startDate - currentDate.get()));
        int ticksToEnd = Ticks.fromSeconds((int)(endDate - currentDate.get()));

        WorldTasks.scheduleTimer(ticksToStart, Ticks.fromHours(2), (interval) -> {
            if (currentDate.get() >= endDate) {
                return false;
            }
            if (interval == 0)
                initEasterSpawns(ticksToEnd);
            shuffleEggSpawns();
            completedEgg1 = new ArrayList<String>();
            completedEgg2 = new ArrayList<String>();
            completedEgg3 = new ArrayList<String>();
            completedEgg4 = new ArrayList<String>();
            completedEgg5 = new ArrayList<String>();
            for (Player p : World.getPlayers()) {
                p.getNSV().removeB("talkedWithEvilChicken");
                p.getNSV().removeB("talkedWithChocatrice");
            }
            World.sendWorldMessage("<col=ff0000>News: A new Easter Egg Hunt has begun! Speak with the Evil Chicken or Chocatrice in Varrock Square to start it.", false);
            return true;
        });
    }

    private static void shuffleEggSpawns() {
        if (!currentEggs.isEmpty()) {
            for (int i = 0; i < currentEggs.size(); i++) {
                currentEggs.remove(i);
            }
        }

        int counter = 0;
        while (currentEggs.size() < 5 || counter < 25) {
            counter++;
            int random = Utils.random(EasterEgg.Spawns.values().length);
            if (!currentEggs.contains(EasterEgg.Spawns.values()[random]))
                currentEggs.add(EasterEgg.Spawns.values()[random]);
        }
    }

    public static void foundEgg(Player p, GameObject o) {
        EasterEgg.Spawns loc = EasterEgg.Spawns.isLocation(o.getX(), o.getY(), o.getPlane());
        if (loc == null)
            return;
        if (currentEggs.contains(loc)) {
            switch (currentEggs.indexOf(loc)) {
                case 0 -> completedEgg1.add(p.getUsername());
                case 1 -> completedEgg2.add(p.getUsername());
                case 2 -> completedEgg3.add(p.getUsername());
                case 3 -> completedEgg4.add(p.getUsername());
                case 4 -> completedEgg5.add(p.getUsername());
            }
        }
    }

    public static boolean hasFoundHintEgg(Player p) {
        if (completedEgg1.contains(p.getUsername()))
            return true;
        return false;
    }

    public static boolean hasCompletedHunt(Player p) {
        if (!completedEgg1.contains(p.getUsername()))
            return false;
        if (!completedEgg2.contains(p.getUsername()))
            return false;
        if (!completedEgg3.contains(p.getUsername()))
            return false;
        if (!completedEgg4.contains(p.getUsername()))
            return false;
        if (!completedEgg5.contains(p.getUsername()))
            return false;
        return true;
    }

    public static void initEasterSpawns(int ticksToEnd) {
        //Load varrock
        Region varrock = World.getRegion(12853, true);

        //Remove default game objects that will be in the way
        WorldTasks.schedule(Ticks.fromSeconds(5), () -> {
            World.removeObject(varrock.getObject(0,5,39, ObjectType.STRAIGHT_OUSIDE_WALL_DEC));
            World.removeObject(varrock.getObject(0,11,46, ObjectType.STRAIGHT_OUSIDE_WALL_DEC));
            World.removeObject(varrock.getObject(0,14,46, ObjectType.STRAIGHT_OUSIDE_WALL_DEC));
            World.removeObject(varrock.getObject(1,9,24, ObjectType.STRAIGHT_OUSIDE_WALL_DEC));
        });

        //Spawn holiday NPCs
        final NPC Chocatrice = new NPC(15259, new WorldTile(3208, 3426, 0));
        final NPC EvilChicken = new NPC(15262, new WorldTile(3216,3426,0));
        Chocatrice.faceSouth();
        EvilChicken.faceSouth();
        World.addNPC(Chocatrice);
        World.addNPC(EvilChicken);

        WorldTasks.schedule(ticksToEnd, () -> {
            World.removeNPC(Chocatrice);
            World.removeNPC(EvilChicken);
        });

        //Spawn holiday objects
        //Fountains
        World.spawnObjectTemporary(new GameObject(70111, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3210,3426,0)), ticksToEnd); //Southwest fountain corner
        World.spawnObjectTemporary(new GameObject(70111, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3210,3430,0)), ticksToEnd); //Northwest fountain corner

        //Fountain statues
        World.spawnObjectTemporary(new GameObject(70112, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3211,3427,1)), ticksToEnd); //Southwest fountain chocolate bar
        World.spawnObjectTemporary(new GameObject(70112, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3211,3429,1)), ticksToEnd); //Northwest fountain chocolate bar
        World.spawnObjectTemporary(new GameObject(70114, ObjectType.SCENERY_INTERACT, 4, new WorldTile(3213,3427,1)), ticksToEnd); //Southeast fountain evil egg
        World.spawnObjectTemporary(new GameObject(70114, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3213,3429,1)), ticksToEnd); //Northeast fountain evil egg

        //Fountain walls
        World.spawnObjectTemporary(new GameObject(70124, ObjectType.WALL_STRAIGHT, 0, new WorldTile(3212,3431,0)), ticksToEnd); //North chocolate egg wall
        World.spawnObjectTemporary(new GameObject(70125, ObjectType.WALL_STRAIGHT, 1, new WorldTile(3210,3429,0)), ticksToEnd); //Northwest chocolate egg wall
        World.spawnObjectTemporary(new GameObject(70125, ObjectType.WALL_STRAIGHT, 0, new WorldTile(3212,3426,0)), ticksToEnd); //South chocolate egg wall
        World.spawnObjectTemporary(new GameObject(70124, ObjectType.WALL_STRAIGHT, 3, new WorldTile(3210,3428,0)), ticksToEnd); //Southwest chocolate egg wall

        World.spawnObjectTemporary(new GameObject(70127, ObjectType.WALL_STRAIGHT, 2, new WorldTile(3213,3431,0)), ticksToEnd); //North evil egg wall
        World.spawnObjectTemporary(new GameObject(70126, ObjectType.WALL_STRAIGHT, 1, new WorldTile(3215,3429,0)), ticksToEnd); //Northeast evil egg wall
        World.spawnObjectTemporary(new GameObject(70126, ObjectType.WALL_STRAIGHT, 2, new WorldTile(3213,3426,0)), ticksToEnd); //South evil egg wall
        World.spawnObjectTemporary(new GameObject(70127, ObjectType.WALL_STRAIGHT, 3, new WorldTile(3215,3428,0)), ticksToEnd); //Southeast evil egg wall

        //TODO - TYPO IN STRAIGHT_OUSIDE_WALL_DEC
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3211,3438,0)), ticksToEnd); //Castle entrance west standing banner
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3214,3438,0)), ticksToEnd); //Castle entrance east standing banner

        World.spawnObjectTemporary(new GameObject(70116, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3200,3427,1)), ticksToEnd); //Varrock west path banner
        World.spawnObjectTemporary(new GameObject(70119, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3200,3426,0)), ticksToEnd); //Varrock west path pole

        //TODO - FIX THIS SPAWN https://youtu.be/sbTo1HMvHas?t=133
        World.spawnObjectTemporary(new GameObject(70117, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3232,3428,1)), ticksToEnd); //Varrock east path banner
        World.spawnObjectTemporary(new GameObject(70129, ObjectType.WALL_STRAIGHT_CORNER, 2, new WorldTile(3232,3431,0)), ticksToEnd); //Varrock east path pole

        World.spawnObjectTemporary(new GameObject(70123, ObjectType.GROUND_INTERACT, 1, new WorldTile(3229,3428,0)), ticksToEnd); //Varrock east path standing banner


        World.spawnObjectTemporary(new GameObject(70123, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3205,3431,0)), ticksToEnd); //Zaffs shop standing banner
        World.spawnObjectTemporary(new GameObject(70122, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3205,3433,1)), ticksToEnd); //Zaffs shop hanging banner


        World.spawnObjectTemporary(new GameObject(70121, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3209,3416,1)), ticksToEnd); //Thessalia's shop hanging banner
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.GROUND_INTERACT, 2, new WorldTile(3209,3419,0)), ticksToEnd); //Thessalia's shop standing banner

        World.spawnObjectTemporary(new GameObject(70123, ObjectType.GROUND_INTERACT, 1, new WorldTile(3213,3420,0)), ticksToEnd); //General store standing banner

        World.spawnObjectTemporary(new GameObject(70116, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3208,3410,1)), ticksToEnd); //Varrock south path banner

        World.spawnObjectTemporary(new GameObject(42651, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3222,3435,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42652, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3224,3425,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42650, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3219,3421,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42650, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3223,3421,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42650, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3207,3420,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42652, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3206,3421,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42651, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3200,3442,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42651, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3199,3441,0)), ticksToEnd);  //Random easter eggs
    }

    /*
     *
     * Possible object ids
     *    70103 - Easter egg [10] [Crack, null, null, null, null]                  (CRACKABLE EGG)
     *    70104 - Easter egg (cracked) [10] [null, null, null, null, null]         (CRACKED EGG)
     *    70105 - Easter Egg (Cracked) [10] [null, null, null, null, null]         (CRACKED EGG)
     *    70106 - null null [null, null, null, null, null]                         (CRACKABLE EGG)
     *    70107 - null null [null, null, null, null, null]                         (CRACKABLE EGG)
     *    70108 - null null [null, null, null, null, null]                         (CRACKABLE EGG)
     *    70109 - null null [null, null, null, null, null]                         (CRACKABLE EGG)
     *    70110 - null null [null, null, null, null, null]                         (CRACKABLE EGG)
     *    70111 - Fountain [10] [null, null, null, null, null]                     (CHOCOLATE VARROCK FOUNTAIN CORNER)
     *    70112 - null [10] [null, null, null, null, null]                         (CHOCOLATE BARS SITTING ON VARROCK FOUNTAIN)
     *    70113 - Fountain [10] [null, null, null, null, null]                     (WATER POURING INTO CHOCOLATE FOUNTAIN CORNER)
     *    70114 - null [10] [null, null, null, null, null]                         (GIANT EGG SITTING ON VARROCK FOUNTAIN)
     *    70115 - null [10] [null, null, null, null, null]                         (BIG BANNER TRANSFORMED FURTHEST FROM CENTER)
     *    70116 - null [10] [null, null, null, null, null]                         (BIG BANNER TRANSFORMED SLIGHTLY FROM CENTER)
     *    70117 - null [10] [null, null, null, null, null]                         (SMALL BANNER)
     *    70118 - null [10] [null, null, null, null, null]                         (BIG BANNER CENTERED)
     *    70119 - null [10] [null, null, null, null, null]                         (BANNER POLE NORTH OF TILE)
     *    70120 - null [3] [null, null, null, null, null]                          (BANNER POLE NORTH OF TILE)
     *    70121 - Banner [10] [null, null, null, null, null]                       (WALL-HANGING SMALL BANNER)
     *    70122 - Banner [10] [null, null, null, null, null]                       (WALL-HANGING SMALL BANNER CLOSER TO WALL)
     *    70123 - Banner [10] [null, null, null, null, null]                       (STANDALONE BANNER)
     *    70124 - null [0] [null, null, null, null, null]                          (NORTH FOUNTAIN CHOCOLATE EGG)
     *    70125 - null [0] [null, null, null, null, null]                          (SOUTH FOUNTAIN CHOCOLATE EGG)
     *    70126 - null [0] [null, null, null, null, null]                          (NORTH FOUNTAIN EVIL EGG)
     *    70127 - null [0] [null, null, null, null, null]                          (SOUTH FOUNTAIN EVIL EGG)
     *    70128 - null [3] [null, null, null, null, null]                          (BANNER POLE NORTH OF TILE)
     *    70129 - null [3] [null, null, null, null, null]                          (BANNER POLE SOUTH OF TILE)
     */
}
