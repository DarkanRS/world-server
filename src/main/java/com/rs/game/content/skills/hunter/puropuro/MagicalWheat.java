package com.rs.game.content.skills.hunter.puropuro;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class MagicalWheat {

    public enum MagicWheat {
        SPAWN1(WorldTile.of(2565, 4310, 0), WorldTile.of(2565, 4311, 0)),
        SPAWN2(WorldTile.of(2568, 4329, 0), WorldTile.of(2568, 4330, 0)),
        SPAWN3(WorldTile.of(2571, 4315, 0), WorldTile.of(2571, 4316, 0)),
        SPAWN4(WorldTile.of(2574, 4310, 0), WorldTile.of(2574, 4311, 0)),
        SPAWN5(WorldTile.of(2577, 4327, 0), WorldTile.of(2577, 4328, 0)),
        SPAWN6(WorldTile.of(2576, 4338, 0), WorldTile.of(2576, 4339, 0)),
        SPAWN7(WorldTile.of(2580, 4325, 0), WorldTile.of(2580, 4326, 0)),
        SPAWN8(WorldTile.of(2581, 4337, 0), WorldTile.of(2582, 4337, 0)),
        SPAWN9(WorldTile.of(2582, 4346, 0), WorldTile.of(2583, 4346, 0)),
        SPAWN10(WorldTile.of(2583, 4296, 0), WorldTile.of(2584, 4296, 0)),
        SPAWN11(WorldTile.of(2583, 4303, 0), WorldTile.of(2583, 4304, 0)),
        SPAWN12(WorldTile.of(2584, 4329, 0), WorldTile.of(2584, 4330, 0)),
        SPAWN13(WorldTile.of(2584, 4335, 0), WorldTile.of(2584, 4336, 0)),
        SPAWN14(WorldTile.of(2587, 4306, 0), WorldTile.of(2587, 4307, 0)),
        SPAWN15(WorldTile.of(2582, 4332, 0), WorldTile.of(2582, 4333, 0)),
        SPAWN16(WorldTile.of(2586, 4302, 0), WorldTile.of(2587, 4302, 0)),
        SPAWN17(WorldTile.of(2586, 4334, 0), WorldTile.of(2587, 4334, 0)),
        SPAWN18(WorldTile.of(2585, 4309, 0), WorldTile.of(2585, 4310, 0)),
        SPAWN19(WorldTile.of(2590, 4299, 0), WorldTile.of(2591, 4299, 0)),
        SPAWN20(WorldTile.of(2594, 4329, 0), WorldTile.of(2594, 4330, 0)),
        SPAWN21(WorldTile.of(2595, 4308, 0), WorldTile.of(2596, 4308, 0)),
        SPAWN22(WorldTile.of(2595, 4343, 0), WorldTile.of(2596, 4343, 0)),
        SPAWN23(WorldTile.of(2596, 4331, 0), WorldTile.of(2597, 4331, 0)),
        SPAWN24(WorldTile.of(2598, 4309, 0), WorldTile.of(2598, 4310, 0)),
        SPAWN25(WorldTile.of(2598, 4335, 0), WorldTile.of(2598, 4336, 0)),
        SPAWN26(WorldTile.of(2599, 4305, 0), WorldTile.of(2600, 4305, 0)),
        SPAWN27(WorldTile.of(2600, 4293, 0), WorldTile.of(2601, 4293, 0)),
        SPAWN28(WorldTile.of(2601, 4332, 0), WorldTile.of(2601, 4333, 0)),
        SPAWN29(WorldTile.of(2601, 4340, 0), WorldTile.of(2602, 4340, 0)),
        SPAWN30(WorldTile.of(2602, 4306, 0), WorldTile.of(2602, 4307, 0)),
        SPAWN31(WorldTile.of(2603, 4303, 0), WorldTile.of(2603, 4304, 0)),
        SPAWN32(WorldTile.of(2603, 4313, 0), WorldTile.of(2603, 4314, 0)),
        SPAWN33(WorldTile.of(2604, 4338, 0), WorldTile.of(2604, 4339, 0)),
        SPAWN34(WorldTile.of(2606, 4328, 0), WorldTile.of(2606, 4329, 0)),
        SPAWN35(WorldTile.of(2609, 4309, 0), WorldTile.of(2609, 4310, 0)),
        SPAWN36(WorldTile.of(2612, 4323, 0), WorldTile.of(2612, 4324, 0)),
        SPAWN37(WorldTile.of(2615, 4314, 0), WorldTile.of(2615, 4315, 0)),
        SPAWN38(WorldTile.of(2618, 4304, 0), WorldTile.of(2618, 4305, 0));

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

    @ServerStartupEvent
    public static void initMagicalWheat() {
        final int NO_WHEAT = 25000;
        final int HAS_WHEAT = 25021;
        final int GROWING_WHEAT = 25022;
        final int WILTING_WHEAT = 25023;
        WorldTasks.schedule(0, 25, () -> {
            for (MagicWheat wheat : MagicWheat.values()) {
                if (Utils.random(0, 4) != 0)
                    continue;
                GameObject obj = World.getObject(wheat.getTile1());
                boolean growing = (obj == null || obj.getId() != HAS_WHEAT);
                int rotation = (wheat.getTile1().getX() != wheat.getTile2().getX() ? 1 : 0);
                WorldTasks.scheduleTimer(tick -> {
                    if (tick == 0) {
                        if (growing) {
                            World.spawnObject(new GameObject(GROWING_WHEAT, ObjectType.GROUND_DECORATION, rotation,
                                    wheat.getTile1()));
                            World.spawnObject(new GameObject(GROWING_WHEAT, ObjectType.GROUND_DECORATION, rotation+2,
                                    wheat.getTile2()));
                        } else {
                            World.spawnObject(new GameObject(WILTING_WHEAT, ObjectType.GROUND_DECORATION, rotation,
                                    wheat.getTile1()));
                            World.spawnObject(new GameObject(WILTING_WHEAT, ObjectType.GROUND_DECORATION, rotation+2,
                                    wheat.getTile2()));
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
                        World.spawnObject(new GameObject(HAS_WHEAT, ObjectType.GROUND_DECORATION, rotation,
                                wheat.getTile1()));
                        World.spawnObject(new GameObject(HAS_WHEAT, ObjectType.GROUND_DECORATION, rotation,
                                wheat.getTile2()));
                        return false;
                    }

                    if (tick == 10) {
                        World.spawnObject(
                                new GameObject(NO_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile1()));
                        World.spawnObject(
                                new GameObject(NO_WHEAT, ObjectType.GROUND_DECORATION, rotation, wheat.getTile2()));
                        return false;
                    }
                    return true;
                });
            }
        });
    }

    public static ObjectClickHandler pushThrough = new ObjectClickHandler(new Object[] { "Magical wheat" }, e -> {
        if (e.isAtObject()) {
            e.getPlayer().faceObject(e.getObject());

            Direction dir = e.getPlayer().getDirection();
            int speed = Utils.randomInclusive(0, 2) * 2;
            int finalSpeed = e.getPlayer().hasEffect(Effect.FARMERS_AFFINITY) ? 3 + speed : 6 + speed;
            WorldTile finalTile = e.getPlayer().getFrontfacingTile(2);

            GameObject loc = World.getObject(finalTile);
            if (loc != null && loc.getDefinitions(e.getPlayer()).getName().equals("Magical wheat")) {
                e.getPlayer().sendMessage("The wheat here seems unusually stubborn. You cannot push through.");
                return;
            }

            e.getPlayer().lock();
            switch (speed) {
                case 0 -> {
                    if (e.getPlayer().hasEffect(Effect.FARMERS_AFFINITY))
                        e.getPlayer().sendMessage("You use your strength to push through the wheat in the most efficient fashion.");
                    else
                        e.getPlayer().sendMessage("You use your strength to push through the wheat.");
                }
                case 2 -> e.getPlayer().sendMessage("You push through the wheat.");
                case 4 -> e.getPlayer().sendMessage("You push through the wheat. It's hard work, though.");
            }

            WorldTasks.scheduleTimer(ticks -> {
                if (ticks == 0) {
                    e.getPlayer().setNextForceMovement(new ForceMovement(finalTile, finalSpeed, dir));
                    e.getPlayer().setNextAnimation(new Animation(6593 + speed / 2));
                }
                if (ticks == finalSpeed) {
                    e.getPlayer().unlock();
                    e.getPlayer().setNextWorldTile(finalTile);
                    if (e.getPlayer().getO("ppStrengthEnabled") == null)
                        e.getPlayer().save("ppStrengthEnabled", true);
                    if (e.getPlayer().getBool("ppStrengthEnabled"))
                        e.getPlayer().getSkills().addXp(Skills.STRENGTH, 4 - speed);
                    return false;
                }
                return true;
            });
        }
    });
}