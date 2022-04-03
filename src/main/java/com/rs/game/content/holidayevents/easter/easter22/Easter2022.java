package com.rs.game.content.holidayevents.easter.easter22;

import com.rs.Settings;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.holidayevents.easter.easter22.EggHunt.Spawns;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.Ticks;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@PluginEventHandler
public class Easter2022 {

    //Event configs
    public static String STAGE_KEY = "easter2022";
    public static Boolean ENABLED = false;

    //Rewards
    public static final int PERMANENT_EGGSTERMINATOR = 24146;
    public static final int XP_LAMP = 2528;
    public static final int LOYALTY_POINTS_AWARDED = 2500;
    public static final int EGG_ON_FACE_MASK = 24149;
    public static final int CHOCOLATE_EGG_ON_FACE_MASK = 24150;
    public static final int EASTER_TRACK = 273; //Easter Jigg
    public static final Emote EASTER_EMOTE = Emote.AROUND_THE_WORLD;

    //Event Items
    public static final int EGGSTERMINATOR = 24145;
    public static final int CHOCOTREAT = 24148;
    public static final int EVIL_DRUMSTICK = 24147;

    //Event Objects
    public static final int RUBBLE = 69753;

    //Event NPCs
    public static final int CHOCOCHICK = 15270;
    public static final int CHICK = 15271;
    public static final int EVIL_CHICKEN = 15262;
    public static final int CHOCATRICE = 15259;
    public static final int EVIL_CHICKEN_MEDIUM = 15263;
    public static final int CHOCATRICE_MEDIUM = 15260;
    public static final int EVIL_CHICKEN_LARGE = 15264;
    public static final int CHOCATRICE_LARGE = 15261;
    public static EggHunt event;// = new EggHunt();

    static AtomicLong currentTime = new AtomicLong(Instant.now().getEpochSecond());
    static long startDate = 1649548800; //April 10th - 00:00
    static long endDate = 1650412800; //April 20th - 00:00

    /*
     *   Runescape 2012 Easter event - Chocatrice vs Evil Chicken.
     *
     *   Rewards -
     *       1 hunt - xp lamp (skill*10) per hunt completed
     *       1 hunt - Emote -> Around the World in Eggty Days
     *       1 hunt - Song -> Easter Jigg
     *       3 hunts - Permanent eggsterminator unlocked
     *       3 hunts - drumsticks given to the evil chicken -> Egg on face mask
     *       3 hunts - chocotreats given to the chocatrice -> Chocolate egg on face mask
     */
    
    //TODO - TYPO IN ObjectType - STRAIGHT_OUSIDE_WALL_DEC
    //TODO - Dialogue when unequiping temporary eggsterminator still prompting to destroy twice
    //TODO - verify locking/animations look and feel okay when firing the eggsterminator.
    //TODO - test to make sure there are no bugs with awarding xp lamps + loyalty points
    //TODO - verify players can buy egg hats 1 time, once its awarded to diango reclaim they should not be prompted anymore
    //TODO - verify other rewards are given, song/emote after 1 successful hunt, permanent eggsterminator after 3.
    //TODO - add player v player splattering with the eggsterminator, its also supposed to have a special animation of twirling if the player has an egg hat on, but do we care?
    //TODO - test event automatically ending and removing objects/npcs.
    //TODO - is it possible to get the chocolate bars and easter eggs to sit on varrock fountain properly?

    @ServerStartupEvent
    public static void EasterEvent2022() {
        if (!ENABLED || currentTime.get() >= endDate)
            return;

        int ticksToStart = (Settings.getConfig().isDebug() || currentTime.get() >= startDate) ? 10 : Ticks.fromSeconds((int)(startDate - currentTime.get()));
        int ticksToEnd = Settings.getConfig().isDebug() ? 3000 : Ticks.fromSeconds((int)(endDate - currentTime.get()));
        
        WorldTasks.scheduleTimer(ticksToStart, (interval) -> {
        	if (interval >= ticksToEnd) {
                World.sendWorldMessage("<col=ff0000>News: The Easter event is now over! Thanks for participating!", false);
                ENABLED = false;
                return false;
            }
            if (interval == 0) {
                initEasterSpawns(ticksToEnd);
                event = new EggHunt();
            }
            return true;
        });
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

        final NPC Chocatrice = new NPC(15259, new WorldTile(3208, 3426, 0));
        final NPC EvilChicken = new NPC(15262, new WorldTile(3216,3426,0));
        Chocatrice.faceSouth();
        EvilChicken.faceSouth();
        World.addNPC(Chocatrice);
        World.addNPC(EvilChicken);

        for (Spawns spawn : Spawns.values())
            World.spawnObject(spawn.getEgg());

        WorldTasks.schedule(ticksToEnd, () -> {
            World.removeNPC(Chocatrice);
            World.removeNPC(EvilChicken);
            for (EggHunt.Spawns spawn : EggHunt.Spawns.values())
            	World.removeObject(spawn.getEgg());
        });

        World.spawnObjectTemporary(new GameObject(70111, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3210,3426,0)), ticksToEnd); //Southwest fountain corner
        World.spawnObjectTemporary(new GameObject(70111, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3210,3430,0)), ticksToEnd); //Northwest fountain corner
        World.spawnObjectTemporary(new GameObject(70112, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3211,3427,1)), ticksToEnd); //Southwest fountain chocolate bar
        World.spawnObjectTemporary(new GameObject(70112, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3211,3429,1)), ticksToEnd); //Northwest fountain chocolate bar
        World.spawnObjectTemporary(new GameObject(70114, ObjectType.SCENERY_INTERACT, 4, new WorldTile(3213,3427,1)), ticksToEnd); //Southeast fountain evil egg
        World.spawnObjectTemporary(new GameObject(70114, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3213,3429,1)), ticksToEnd); //Northeast fountain evil egg
        World.spawnObjectTemporary(new GameObject(70124, ObjectType.WALL_STRAIGHT, 0, new WorldTile(3212,3431,0)), ticksToEnd); //North chocolate egg wall
        World.spawnObjectTemporary(new GameObject(70125, ObjectType.WALL_STRAIGHT, 1, new WorldTile(3210,3429,0)), ticksToEnd); //Northwest chocolate egg wall
        World.spawnObjectTemporary(new GameObject(70125, ObjectType.WALL_STRAIGHT, 0, new WorldTile(3212,3426,0)), ticksToEnd); //South chocolate egg wall
        World.spawnObjectTemporary(new GameObject(70124, ObjectType.WALL_STRAIGHT, 3, new WorldTile(3210,3428,0)), ticksToEnd); //Southwest chocolate egg wall
        World.spawnObjectTemporary(new GameObject(70127, ObjectType.WALL_STRAIGHT, 2, new WorldTile(3213,3431,0)), ticksToEnd); //North evil egg wall
        World.spawnObjectTemporary(new GameObject(70126, ObjectType.WALL_STRAIGHT, 1, new WorldTile(3215,3429,0)), ticksToEnd); //Northeast evil egg wall
        World.spawnObjectTemporary(new GameObject(70126, ObjectType.WALL_STRAIGHT, 2, new WorldTile(3213,3426,0)), ticksToEnd); //South evil egg wall
        World.spawnObjectTemporary(new GameObject(70127, ObjectType.WALL_STRAIGHT, 3, new WorldTile(3215,3428,0)), ticksToEnd); //Southeast evil egg wall
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3211,3438,0)), ticksToEnd); //Castle entrance west standing banner
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3214,3438,0)), ticksToEnd); //Castle entrance east standing banner
        World.spawnObjectTemporary(new GameObject(70119, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3224,3427,0)), ticksToEnd); //Varrock east path pole
        World.spawnObjectTemporary(new GameObject(70119, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3224,3432,0)), ticksToEnd); //Varrock east path pole
        World.spawnObjectTemporary(new GameObject(70117, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3224,3428,1)), ticksToEnd); //Varrock east path banner
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.GROUND_INTERACT, 1, new WorldTile(3229,3428,0)), ticksToEnd); //Varrock east path standing banner
        World.spawnObjectTemporary(new GameObject(70116, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3208,3410,1)), ticksToEnd); //Varrock south path banner
        World.spawnObjectTemporary(new GameObject(70116, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3200,3427,1)), ticksToEnd); //Varrock west path banner
        World.spawnObjectTemporary(new GameObject(70119, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3200,3426,0)), ticksToEnd); //Varrock west path pole
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3205,3431,0)), ticksToEnd); //Zaffs shop standing banner
        World.spawnObjectTemporary(new GameObject(70122, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3205,3433,1)), ticksToEnd); //Zaffs shop hanging banner
        World.spawnObjectTemporary(new GameObject(70121, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3209,3416,1)), ticksToEnd); //Thessalia's shop hanging banner
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.GROUND_INTERACT, 2, new WorldTile(3209,3419,0)), ticksToEnd); //Thessalia's shop standing banner
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.GROUND_INTERACT, 1, new WorldTile(3213,3420,0)), ticksToEnd); //General store standing banner
        World.spawnObjectTemporary(new GameObject(42651, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3222,3435,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42652, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3224,3425,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42650, ObjectType.SCENERY_INTERACT, 3, new WorldTile(3219,3421,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42650, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3223,3421,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42650, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3207,3420,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42652, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3206,3421,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42651, ObjectType.SCENERY_INTERACT, 0, new WorldTile(3200,3442,0)), ticksToEnd);  //Random easter eggs
        World.spawnObjectTemporary(new GameObject(42651, ObjectType.SCENERY_INTERACT, 1, new WorldTile(3199,3441,0)), ticksToEnd);  //Random easter eggs
    }
}
