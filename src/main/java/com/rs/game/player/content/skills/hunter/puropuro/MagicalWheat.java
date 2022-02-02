package com.rs.game.player.content.skills.hunter.puropuro;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.ClipType;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.controllers.PuroPuroController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class MagicalWheat {

    public enum MagicWheat {
        SPAWN0(new WorldTile(2560, 4308, 0), new WorldTile(2561, 4308, 0)),
        SPAWN1(new WorldTile(2560, 4324, 0), new WorldTile(2561, 4324, 0)),
        SPAWN2(new WorldTile(2560, 4347, 0), new WorldTile(2561, 4347, 0)),
        SPAWN3(new WorldTile(2562, 4333, 0), new WorldTile(2562, 4334, 0)),
        SPAWN4(new WorldTile(2563, 4300, 0), new WorldTile(2564, 4300, 0)),
        SPAWN5(new WorldTile(2563, 4327, 0), new WorldTile(2564, 4327, 0)),
        SPAWN6(new WorldTile(2563, 4344, 0), new WorldTile(2564, 4344, 0)),
        SPAWN7(new WorldTile(2564, 4288, 0), new WorldTile(2564, 4289, 0)),
        SPAWN8(new WorldTile(2565, 4310, 0), new WorldTile(2565, 4311, 0)),
        SPAWN9(new WorldTile(2566, 4299, 0), new WorldTile(2567, 4299, 0)),
        SPAWN10(new WorldTile(2566, 4313, 0), new WorldTile(2567, 4313, 0)),
        SPAWN11(new WorldTile(2566, 4326, 0), new WorldTile(2567, 4326, 0)),
        SPAWN12(new WorldTile(2566, 4333, 0), new WorldTile(2567, 4333, 0)),
        SPAWN13(new WorldTile(2567, 4291, 0), new WorldTile(2567, 4292, 0)),
        SPAWN14(new WorldTile(2567, 4347, 0), new WorldTile(2567, 4348, 0)),
        SPAWN15(new WorldTile(2568, 4329, 0), new WorldTile(2568, 4330, 0)),
        SPAWN16(new WorldTile(2569, 4300, 0), new WorldTile(2570, 4300, 0)),
        SPAWN17(new WorldTile(2569, 4323, 0), new WorldTile(2570, 4323, 0)),
        SPAWN18(new WorldTile(2569, 4350, 0), new WorldTile(2569, 4351, 0)),
        SPAWN19(new WorldTile(2571, 4315, 0), new WorldTile(2571, 4316, 0)),
        SPAWN20(new WorldTile(2572, 4320, 0), new WorldTile(2573, 4320, 0)),
        SPAWN21(new WorldTile(2572, 4334, 0), new WorldTile(2573, 4334, 0)),
        SPAWN22(new WorldTile(2572, 4341, 0), new WorldTile(2572, 4342, 0)),
        SPAWN23(new WorldTile(2573, 4294, 0), new WorldTile(2573, 4295, 0)),
        SPAWN24(new WorldTile(2574, 4310, 0), new WorldTile(2574, 4311, 0)),
        SPAWN25(new WorldTile(2575, 4316, 0), new WorldTile(2576, 4316, 0)),
        SPAWN26(new WorldTile(2575, 4325, 0), new WorldTile(2576, 4325, 0)),
        SPAWN27(new WorldTile(2575, 4333, 0), new WorldTile(2576, 4333, 0)),
        SPAWN28(new WorldTile(2577, 4300, 0), new WorldTile(2577, 4301, 0)),
        SPAWN29(new WorldTile(2577, 4327, 0), new WorldTile(2577, 4328, 0)),
        SPAWN30(new WorldTile(2577, 4338, 0), new WorldTile(2577, 4339, 0)),
        SPAWN31(new WorldTile(2578, 4313, 0), new WorldTile(2579, 4313, 0)),
        SPAWN32(new WorldTile(2578, 4322, 0), new WorldTile(2579, 4322, 0)),
        SPAWN33(new WorldTile(2578, 4344, 0), new WorldTile(2578, 4345, 0)),
        SPAWN34(new WorldTile(2579, 4303, 0), new WorldTile(2579, 4304, 0)),
        SPAWN35(new WorldTile(2580, 4325, 0), new WorldTile(2580, 4326, 0)),
        SPAWN36(new WorldTile(2581, 4290, 0), new WorldTile(2582, 4290, 0)),
        SPAWN37(new WorldTile(2581, 4316, 0), new WorldTile(2582, 4316, 0)),
        SPAWN38(new WorldTile(2581, 4337, 0), new WorldTile(2582, 4337, 0)),
        SPAWN39(new WorldTile(2582, 4346, 0), new WorldTile(2583, 4346, 0)),
        SPAWN40(new WorldTile(2583, 4296, 0), new WorldTile(2584, 4296, 0)),
        SPAWN41(new WorldTile(2583, 4300, 0), new WorldTile(2583, 4301, 0)),
        SPAWN42(new WorldTile(2584, 4291, 0), new WorldTile(2584, 4292, 0)),
        SPAWN43(new WorldTile(2584, 4347, 0), new WorldTile(2584, 4348, 0)),
        SPAWN44(new WorldTile(2585, 4306, 0), new WorldTile(2585, 4307, 0)),
        SPAWN45(new WorldTile(2585, 4332, 0), new WorldTile(2585, 4333, 0)),
        SPAWN46(new WorldTile(2586, 4302, 0), new WorldTile(2587, 4302, 0)),
        SPAWN47(new WorldTile(2586, 4334, 0), new WorldTile(2587, 4334, 0)),
        SPAWN48(new WorldTile(2587, 4288, 0), new WorldTile(2587, 4289, 0)),
        SPAWN49(new WorldTile(2587, 4297, 0), new WorldTile(2587, 4298, 0)),
        SPAWN50(new WorldTile(2588, 4309, 0), new WorldTile(2588, 4310, 0)),
        SPAWN51(new WorldTile(2588, 4329, 0), new WorldTile(2588, 4330, 0)),
        SPAWN52(new WorldTile(2588, 4335, 0), new WorldTile(2588, 4336, 0)),
        SPAWN53(new WorldTile(2590, 4299, 0), new WorldTile(2591, 4299, 0)),
        SPAWN54(new WorldTile(2594, 4306, 0), new WorldTile(2594, 4307, 0)),
        SPAWN55(new WorldTile(2595, 4303, 0), new WorldTile(2595, 4304, 0)),
        SPAWN56(new WorldTile(2595, 4308, 0), new WorldTile(2596, 4308, 0)),
        SPAWN57(new WorldTile(2595, 4343, 0), new WorldTile(2596, 4343, 0)),
        SPAWN58(new WorldTile(2596, 4331, 0), new WorldTile(2597, 4331, 0)),
        SPAWN59(new WorldTile(2596, 4350, 0), new WorldTile(2596, 4351, 0)),
        SPAWN60(new WorldTile(2598, 4309, 0), new WorldTile(2598, 4310, 0)),
        SPAWN61(new WorldTile(2598, 4329, 0), new WorldTile(2598, 4330, 0)),
        SPAWN62(new WorldTile(2600, 4293, 0), new WorldTile(2601, 4293, 0)),
        SPAWN63(new WorldTile(2601, 4306, 0), new WorldTile(2601, 4307, 0)),
        SPAWN64(new WorldTile(2601, 4316, 0), new WorldTile(2602, 4316, 0)),
        SPAWN65(new WorldTile(2601, 4326, 0), new WorldTile(2602, 4326, 0)),
        SPAWN66(new WorldTile(2601, 4332, 0), new WorldTile(2601, 4333, 0)),
        SPAWN67(new WorldTile(2601, 4340, 0), new WorldTile(2602, 4340, 0)),
        SPAWN68(new WorldTile(2602, 4344, 0), new WorldTile(2602, 4345, 0)),
        SPAWN69(new WorldTile(2602, 4349, 0), new WorldTile(2603, 4349, 0)),
        SPAWN70(new WorldTile(2603, 4313, 0), new WorldTile(2603, 4314, 0)),
        SPAWN71(new WorldTile(2604, 4322, 0), new WorldTile(2605, 4322, 0)),
        SPAWN72(new WorldTile(2604, 4335, 0), new WorldTile(2604, 4336, 0)),
        SPAWN73(new WorldTile(2605, 4294, 0), new WorldTile(2605, 4295, 0)),
        SPAWN74(new WorldTile(2605, 4347, 0), new WorldTile(2605, 4348, 0)),
        SPAWN75(new WorldTile(2606, 4328, 0), new WorldTile(2606, 4329, 0)),
        SPAWN76(new WorldTile(2607, 4315, 0), new WorldTile(2608, 4315, 0)),
        SPAWN77(new WorldTile(2607, 4332, 0), new WorldTile(2608, 4332, 0)),
        SPAWN78(new WorldTile(2608, 4288, 0), new WorldTile(2608, 4289, 0)),
        SPAWN79(new WorldTile(2608, 4300, 0), new WorldTile(2608, 4301, 0)),
        SPAWN80(new WorldTile(2608, 4338, 0), new WorldTile(2608, 4339, 0)),
        SPAWN81(new WorldTile(2609, 4309, 0), new WorldTile(2609, 4310, 0)),
        SPAWN82(new WorldTile(2610, 4319, 0), new WorldTile(2611, 4319, 0)),
        SPAWN83(new WorldTile(2610, 4336, 0), new WorldTile(2611, 4336, 0)),
        SPAWN84(new WorldTile(2611, 4291, 0), new WorldTile(2611, 4292, 0)),
        SPAWN85(new WorldTile(2611, 4297, 0), new WorldTile(2611, 4298, 0)),
        SPAWN86(new WorldTile(2612, 4323, 0), new WorldTile(2612, 4324, 0)),
        SPAWN87(new WorldTile(2613, 4339, 0), new WorldTile(2614, 4339, 0)),
        SPAWN88(new WorldTile(2615, 4314, 0), new WorldTile(2615, 4315, 0)),
        SPAWN89(new WorldTile(2616, 4340, 0), new WorldTile(2617, 4340, 0)),
        SPAWN90(new WorldTile(2618, 4304, 0), new WorldTile(2618, 4305, 0)),
        SPAWN91(new WorldTile(2619, 4295, 0), new WorldTile(2620, 4295, 0)),
        SPAWN92(new WorldTile(2619, 4312, 0), new WorldTile(2620, 4312, 0)),
        SPAWN93(new WorldTile(2619, 4333, 0), new WorldTile(2620, 4333, 0)),
        SPAWN94(new WorldTile(2619, 4339, 0), new WorldTile(2620, 4339, 0)),
        SPAWN95(new WorldTile(2621, 4328, 0), new WorldTile(2621, 4329, 0)),
        SPAWN96(new WorldTile(2622, 4288, 0), new WorldTile(2622, 4289, 0)),
        SPAWN97(new WorldTile(2622, 4290, 0), new WorldTile(2623, 4290, 0)),
        SPAWN98(new WorldTile(2622, 4303, 0), new WorldTile(2623, 4303, 0)),
        ;

        private final WorldTile tile1, tile2;

        MagicWheat(WorldTile tile1, WorldTile tile2) {
            this.tile1 = tile1;
            this.tile2 = tile2;
        }

        public WorldTile getTile1() {
            return tile1;
        }

        public WorldTile getTile2() {
            return tile2;
        }
    }

// 		Anims
//		6593 - You use your strength to push through the wheat in the most efficient fashion.
//		6954 - You use your strength to push through the wheat.
//		6955 - You push through the wheat. It's hard work, though.
//		6615 - Impling animation while being caught

    public enum CropCirlces {

//        ARDOUGNE(new WorldTile()), //Ardougne - Wheat patch north of the Ardougne Market.
//        BRIMHAVEN(new WorldTile()), //Brimhaven - Wheat patch north of the Brimhaven Agility Arena.
        CATHERBY(new WorldTile(2818, 3470, 0)), //Catherby - Wheat patch north-east of the Catherby allotment patch.
//        DRAYNOR_VILLAGE(new WorldTile()), //Draynor Village - Wheat patch north-east of the Draynor Village bank.
//        HARMONY_ISLAND(new WorldTile()), //Harmony Island - Wheat patch north of the chapel.
//        LUMBRIDGE(new WorldTile()), //Lumbridge - Wheat patch north-west of Lumbridge, south-west of the Mill Lane Mill.
//        MISCELLANIA(new WorldTile()), //Miscellania - Wheat patch south-east of the Miscellania Castle.
//        MOS_LE_HARMLESS(new WorldTile()), //Mos Le'Harmless - Wheat patch east of Dodgy Mike's Second Hand Clothing.
//        RIMMINGTON(new WorldTile()), //Rimmington - Wheat patch south of the Rimmington mine.
//        TAVERLEY_NORTH(new WorldTile()), //Taverley - Wheat patch north-east of the Taverley Dungeon entrance.
//        TAVERLEY_SOUTH(new WorldTile()), //Taverley - Wheat patch south of Doric's hut.
//        TREE_GNOME_STRONGHOLD(new WorldTile()), //Tree Gnome Stronghold - Wheat patch south-west of the Grand Tree.
//        VARROCK_NORTH(new WorldTile()), //Varrock - Wheat patch north of the Cooks' Guild.
//        VARROCK_SOUTH(new WorldTile()), //Varrock - Wheat patch south-east of the Champions' Guild.
//        YANILLE(new WorldTile()), //Yanille - Wheat patch north-west of the Wizards' Guild.
        ;

        private final WorldTile entranceTile;

        CropCirlces(WorldTile tile) {
            this.entranceTile = tile;
        }

        public WorldTile getEntranceTile() {
            return entranceTile;
        }

        public int getX() {
            return entranceTile.getX();
        }

        public int getY() {
            return entranceTile.getY();
        }

        public int getPlane() {
            return entranceTile.getPlane();
        }
    }

    public static ObjectClickHandler teleportToPuroPuro = new ObjectClickHandler(new Object[] { 24988, 24991 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            WorldTasks.schedule(10, new WorldTask() {
                @Override
                public void run() {
                    if (e.getObjectId() == 24988) {
                        e.getPlayer().getControllerManager().startController(new PuroPuroController(currentLocation.getEntranceTile()));
                        e.getPlayer().addEffect(Effect.FARMERS_AFFINITY, 3000);
                        e.getPlayer().sendMessage("You feel the magic of the crop circle grant you a Farmer's affinity.");
                    } else {
                        e.getPlayer().getControllerManager().startController(new PuroPuroController(new WorldTile(2427, 4446, 0))); //Zanaris
                    }
                }
            });
            Magic.sendTeleportSpell(e.getPlayer(), 6601, -1, 1118, -1, 0, 0, new WorldTile(2590 + Utils.randomInclusive(0, 3), 4318 + Utils.randomInclusive(0, 3), 0), 9, false, Magic.OBJECT_TELEPORT);
        }
    };
    
    private static CropCirlces currentLocation = null;


    @ServerStartupEvent
    public static void initPuroPuroTasks() {
        final int NO_WHEAT = 25000;
        final int HAS_WHEAT = 25021;
        final int GROWING_WHEAT = 25022;
        final int WILTING_WHEAT = 25023;
        WorldTasks.schedule(0, 25, () -> {
            for (MagicWheat wheat : MagicWheat.values()) {
                if (Utils.random(0,4) == 0) {
                    GameObject obj = World.getObject(wheat.getTile1());
                    boolean growing = (obj == null || obj.getId() != HAS_WHEAT);
                    int rotation = (wheat.getTile1().getX() != wheat.getTile2().getX() ? 3 : 0);
                    WorldTasks.scheduleTimer(tick -> {
                        if (tick == 0) {
                            if (growing) {
                                World.spawnObject(new GameObject(GROWING_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile1()));
                                World.spawnObject(new GameObject(GROWING_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile2()));
                            } else {
                                World.spawnObject(new GameObject(WILTING_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile1()));
                                World.spawnObject(new GameObject(WILTING_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile2()));
                            }
                        }

                        if (tick == 1) {
                            if (growing) {
                                World.sendObjectAnimation(World.getObject(wheat.getTile1()), new Animation(6596));
                                World.sendObjectAnimation(World.getObject(wheat.getTile2()), new Animation(6596));
                            } else {
                                World.sendObjectAnimation(World.getObject(wheat.getTile1()), new Animation(6599));
                                World.sendObjectAnimation(World.getObject(wheat.getTile2()), new Animation(6599));
                            }
                        }

                        if (tick == 6 && growing) {
                            World.spawnObject(new GameObject(HAS_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile1()));
                            World.spawnObject(new GameObject(HAS_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile2()));
                            return false;
                        }

                        if (tick == 10) {
                            World.spawnObject(new GameObject(NO_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile1()));
                            World.spawnObject(new GameObject(NO_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile2()));
                            return false;
                        }
                        return true;
                    });
                }
            }
        });

        WorldTasks.schedule(0, 3000, () -> {
            currentLocation = CropCirlces.values()[Utils.random(0, CropCirlces.values().length)];

            WorldTile[] impPath = new WorldTile[]{
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() - 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() - 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() - 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() - 1, currentLocation.getPlane())
            };

            NPC imp = new NPC(1531, new WorldTile(currentLocation.getX()-1, currentLocation.getY()-1, currentLocation.getPlane()));
            imp.setRandomWalk(false);
            imp.finishAfterTicks(42);

            WorldTasks.scheduleTimer(ticks -> {
                if (ticks % 5 == 0 && ticks <= 35) {
                    imp.setForceWalk(impPath[ticks/5]);
                }

                if (ticks == 41)
                    imp.setNextSpotAnim(new SpotAnim(1119)); //931?

                if (ticks == 45) {
                    spawnCropCircle();
                    return false;
                }
                return true;
            });
        });
    }

    private static void spawnCropCircle() {
        World.spawnObjectTemporary(new GameObject(24986, 2, currentLocation.getX()-1, currentLocation.getY()+1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24985, 2, currentLocation.getX(), currentLocation.getY()+1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24984, 2, currentLocation.getX()+1, currentLocation.getY()+1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24987, 2, currentLocation.getX()-1, currentLocation.getY(), currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24988, 0, currentLocation.getX(), currentLocation.getY(), currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24987, 0, currentLocation.getX()+1, currentLocation.getY(), currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24984, 0, currentLocation.getX()-1, currentLocation.getY()-1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24985, 0, currentLocation.getX(), currentLocation.getY()-1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24986, 0, currentLocation.getX()+1, currentLocation.getY()-1, currentLocation.getPlane()), Ticks.fromMinutes(30));
    }
}
