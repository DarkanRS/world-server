package com.rs.game.player.content.holidayevents.easter.easter22;

import com.google.errorprone.annotations.Var;
import com.rs.Settings;
import com.rs.cache.loaders.ObjectType;
import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.content.commands.Commands;
import com.rs.game.region.Region;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.utils.Ticks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class Easter2022 {

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
        long currentDate = Instant.now().getEpochSecond();
        long startDate = 1648771200; //April 1st - 00:00
        long endDate = 1649635200; //April 11th - 00:00

        if (currentDate >= endDate)
            return;

        //Start 10 ticks after startup, or schedule a task to start on April 1st at 00:00.
        int ticksToStart = Settings.getConfig().isDebug() ? 10 : (currentDate >= startDate) ? 50 : Ticks.fromSeconds((int)(startDate - currentDate));
        int ticksToEnd = Ticks.fromSeconds((int)(endDate - currentDate));

        initEasterSpawns(ticksToEnd);

//        WorldTasks.schedule(ticksToStart, () -> {
//            CoresManager.schedule(() -> {
//                World.sendWorldMessage("<col=ff0000>News: A new Easter Egg Hunt has begun! Speak with the Evil Chicken or Chocatrice in Varrock Square to start it", false);
//                //Start a new hunt - Rotate egg spawns
//                //Reset players hunt progress
//            }, 0, Ticks.fromHours(2));
//        });
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

        World.spawnObjectTemporary(new GameObject(70117, ObjectType.SCENERY_INTERACT, 2, new WorldTile(3232,3428,1)), ticksToEnd); //Varrock east path banner
        World.spawnObjectTemporary(new GameObject(70129, ObjectType.WALL_STRAIGHT_CORNER, 2, new WorldTile(3232,3431,0)), ticksToEnd); //Varrock east path pole
        World.spawnObjectTemporary(new GameObject(70123, ObjectType.GROUND_INTERACT, 1, new WorldTile(3229,3428,0)), ticksToEnd); //Varrock east path pole


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
     *
     *
     * Possible spawn locations
     *    Near Burthorpe Lodestone /South of Burthorpe Castle.
     *    In Burthorpe Agility Course Area.
     *    Inside Taverley Mine.
     *    In Taverley, near and a bit north of the entrance closest to Falador.
     *    East side of White Wolf Mountain.
     *    Inside Taverley Dungeon.
     *    North of the Witch's House/south of Taverley, near the wheat field.
     *    Northwest of the Dark Wizards' Tower/south of the Witch's House.
     *    West of the Exam Centre.
     *    West of the Varrock Dig Site.
     *    In Silvarea, the mountain path between Varrock and Morytania.
     *    In Brimhaven, near the fishing spots in the South.
     *    A little way in front of the gate leading to the Mage Training Arena.
     *   Between the Crafting Guild and the Clan Camp
     *   South-west area of Rimmington
     *   In the banana plantation on Musa Point
     *   Between Karamja Volcano and Musa Point, near the agility shortcut to the south
     *   West of Gamers' Grotto (near Falador Lodestone)
     *   South of the Goblin Village near Falador
     *   Directly between the Captured Temple and Ice Mountain
     *   In Draynor Village (Near Ned's house)
     *   East of Draynor Manor, in the place that used to be Gnomecopter Tours
     *   Between the walls of Falador and Draynor Manor
     *   In the sheep's pen east of the Champions' Guild
     *   South of Varrock, next to the Stone circle
     *   Near the Cooks' Guild, south of Gertrude's house
     *   East of Barbarian Village, near the unicorns
     *   West of the Grand Exchange, near the Outlaw Camp
     *   Around Lumbridge Castle
     *   West of Lumbridge Swamp mining spot, behind Father Urhney's house
     *   West of Edgeville Monastery
     *   On top of Ice Mountain
     *   East of Varrock, near the Saradomin statue South-West of Lumber Yard
     *   Directly in front of Lumber Yard, southern part
     *   Around the back of Daemonheim castle
     *   South of the Fremennik Camp on the Daemonheim Peninsula
     *   In the centre of Mudskipper Point
     */
}
