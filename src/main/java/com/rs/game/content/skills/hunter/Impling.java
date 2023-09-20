package com.rs.game.content.skills.hunter;

import com.rs.game.content.skills.hunter.FlyingEntityHunter.FlyingEntities;
import com.rs.game.content.skills.hunter.puropuro.ImpDefender;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.ClipType;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Impling extends NPC {

    private boolean dynamic;
    private boolean puropuro;
    private Tile respawnTile;

    public Impling(int id, Tile tile, boolean dynamic) {
        super(id, tile);
        this.dynamic = dynamic;
        this.respawnTile = tile;
        this.puropuro = isPuroPuroImpling(id);
        if (!puropuro)
            finishAfterTicks(Ticks.fromMinutes(30));
        setRandomWalk(true);
        setClipType(ClipType.FLYING);
        addLOSOverrides(id);
    }

    public void processNPC() {
        if (Utils.random(500) == 0)
            setNextForceTalk(new ForceTalk(Utils.random(2) == 0 ? "Tee hee!" : "Wheee!"));
        super.processNPC();
    }

    @Override
    public void sendDeath(final Entity source) {
        getInteractionManager().forceStop();
        resetWalkSteps();
        setNextAnimation(null);
        reset();
        setTile(respawnTile);
        finish();
        int impId = rollImpRespawn();
        setNPC(impId == -1 ? rollLowTierOverworldImp() : impId);
        if (!isSpawned())
            setRespawnTask(getRespawnTicks(getId()));
    }

    @Override
    public void onRespawn() {
        if (dynamic || !puropuro) {
            setHidden(true);
            WorldTasks.schedule(Ticks.fromMinutes(Utils.randomInclusive(1, 3)), () -> { setHidden(false); });
        }
    }

    public boolean getPuroPuro() {
        return puropuro;
    }

    private static boolean isPuroPuroImpling(int npcId) {
        return switch(npcId) {
            case 6055, 6056, 6057, 6058, 6059, 6060, 6061, 6062, 6063, 6064, 7846, 7904, 7905, 7906 -> true;
            default -> false;
        };
    }

    private int rollImpRespawn() {
        if (puropuro && dynamic)
            return rollHighTierPuroPuroImp();
        if (puropuro && !dynamic)
            return getId();
        if (!puropuro && !dynamic)
            return rollLowTierOverworldImp();
        return rollTier();
    }

    public static int rollTier() {
        int random = Utils.random(100);
        if (random > 95) return rollHighTierOverworldImp();
        if (random > 88) return rollMidTierOverworldImp();
        if (random > 74) return rollLowTierOverworldImp();
        return -1;
    }

    public static int rollLowTierOverworldImp() {
        int random = Utils.random(100);
        if (random >= 95) return FlyingEntities.SPIRIT_IMPLING.getNpcId();
        if (random >= 85) return FlyingEntities.ECLECTIC_IMPLING.getNpcId();
        if (random >= 75) return FlyingEntities.ESSENCE_IMPLING.getNpcId();
        if (random >= 55) return FlyingEntities.EARTH_IMPLING.getNpcId();
        if (random >= 35) return FlyingEntities.GOURMET_IMPLING.getNpcId();
        if (random >= 15) return FlyingEntities.YOUNG_IMPLING.getNpcId();
        return FlyingEntities.BABY_IMPLING.getNpcId();
    }

    public static int rollMidTierOverworldImp() {
        int random = Utils.random(100);
        if (random >= 99) return FlyingEntities.NINJA_IMPLING.getNpcId();
        if (random >= 97) return FlyingEntities.MAGPIE_IMPLING.getNpcId();
        if (random >= 77) return FlyingEntities.NATURE_IMPLING.getNpcId();
        if (random >= 40) return FlyingEntities.ECLECTIC_IMPLING.getNpcId();
        if (random >= 20) return FlyingEntities.ESSENCE_IMPLING.getNpcId();
        if (random >= 10) return FlyingEntities.EARTH_IMPLING.getNpcId();
        return FlyingEntities.GOURMET_IMPLING.getNpcId();
    }

    public static int rollHighTierOverworldImp() {
        int random = Utils.getRandomInclusive(100);
        if (random >= 100) return FlyingEntities.KINGLY_IMPLING.getNpcId();
        if (random >= 97) return FlyingEntities.ZOMBIE_IMPLING.getNpcId();
        if (random >= 87) return FlyingEntities.DRAGON_IMPLING.getNpcId();
        if (random >= 75) return FlyingEntities.PIRATE_IMPLING.getNpcId();
        if (random >= 50) return FlyingEntities.NINJA_IMPLING.getNpcId();
        if (random >= 10) return FlyingEntities.MAGPIE_IMPLING.getNpcId();
        return FlyingEntities.NATURE_IMPLING.getNpcId();
    }

    public static int rollLowTierPuroPuroImp() {
        int random = Utils.random(100);
        if (random >= 95) return FlyingEntities.SPIRIT_IMPLING_PP.getNpcId();
        if (random >= 85) return FlyingEntities.ECLECTIC_IMPLING_PP.getNpcId();
        if (random >= 75) return FlyingEntities.ESSENCE_IMPLING_PP.getNpcId();
        if (random >= 55) return FlyingEntities.EARTH_IMPLING_PP.getNpcId();
        if (random >= 35) return FlyingEntities.GOURMET_IMPLING_PP.getNpcId();
        if (random >= 15) return FlyingEntities.YOUNG_IMPLING_PP.getNpcId();
        return FlyingEntities.BABY_IMPLING_PP.getNpcId();
    }

    public static int rollMidTierPuroPuroImp() {
        int random = Utils.random(100);
        if (random >= 99) return FlyingEntities.NINJA_IMPLING_PP.getNpcId();
        if (random >= 97) return FlyingEntities.MAGPIE_IMPLING_PP.getNpcId();
        if (random >= 77) return FlyingEntities.NATURE_IMPLING_PP.getNpcId();
        if (random >= 40) return FlyingEntities.ECLECTIC_IMPLING_PP.getNpcId();
        if (random >= 20) return FlyingEntities.ESSENCE_IMPLING_PP.getNpcId();
        if (random >= 10) return FlyingEntities.EARTH_IMPLING_PP.getNpcId();
        return FlyingEntities.GOURMET_IMPLING_PP.getNpcId();
    }

    public static int rollHighTierPuroPuroImp() {
        int random = Utils.getRandomInclusive(300);
        if (random >= 300) return FlyingEntities.KINGLY_IMPLING_PP.getNpcId();
        if (random >= 297) return FlyingEntities.ZOMBIE_IMPLING_PP.getNpcId();
        if (random >= 288) return FlyingEntities.DRAGON_IMPLING_PP.getNpcId();
        if (random >= 270) return FlyingEntities.PIRATE_IMPLING_PP.getNpcId();
        if (random >= 243) return FlyingEntities.NINJA_IMPLING_PP.getNpcId();
        if (random >= 132) return FlyingEntities.MAGPIE_IMPLING_PP.getNpcId();
        return FlyingEntities.NATURE_IMPLING_PP.getNpcId();
    }

    public int getRespawnTicks(int npcId) {
        if (dynamic)
            return Ticks.fromMinutes(2);

        return switch (npcId) {
            case 6058, 6059, 7904 -> 50; //Earth, Essence, Spirit
            case 6055, 6056, 6057, 6060 -> 7; //Baby, Young, Gourmet, Eclectic
            default -> Ticks.fromMinutes(2); //Catch all for overworld
        };
    }

    @ServerStartupEvent
    public static void initSpawns() {
        Tile[] STATIC_PUROPURO_BABY_IMPLING_SPAWNS = new Tile[]{ Tile.of(2563, 4291, 0), Tile.of(2563, 4348, 0), Tile.of(2568, 4323, 0), Tile.of(2571, 4305, 0), Tile.of(2571, 4337, 0), Tile.of(2581, 4300, 0), Tile.of(2584, 4344, 0), Tile.of(2596, 4296, 0), Tile.of(2609, 4339, 0), Tile.of(2610, 4304, 0), Tile.of(2615, 4322, 0), Tile.of(2620, 4291, 0), Tile.of(2620, 4348, 0) };
        Tile[] STATIC_PUROPURO_YOUNG_IMPLING_SPAWNS = new Tile[]{ Tile.of(2564, 4321, 0), Tile.of(2574, 4321, 0), Tile.of(2574, 4331, 0), Tile.of(2587, 4300, 0), Tile.of(2590, 4348, 0), Tile.of(2592, 4291, 0), Tile.of(2595, 4343, 0), Tile.of(2612, 4309, 0), Tile.of(2612, 4327, 0), Tile.of(2619, 4322, 0) };
        Tile[] STATIC_PUROPURO_GOURMET_IMPLING_SPAWNS = new Tile[]{ Tile.of(2568, 4296, 0), Tile.of(2568, 4327, 0), Tile.of(2574, 4311, 0), Tile.of(2580, 4343, 0), Tile.of(2585, 4296, 0), Tile.of(2597, 4293, 0), Tile.of(2602, 4346, 0), Tile.of(2609, 4317, 0), Tile.of(2615, 4298, 0), Tile.of(2615, 4342, 0), Tile.of(2618, 4321, 0) };
        Tile[] STATIC_PUROPURO_EARTH_IMPLING_SPAWNS = new Tile[]{ Tile.of(2568, 4317, 0), Tile.of(2570, 4330, 0), Tile.of(2574, 4305, 0), Tile.of(2587, 4342, 0), Tile.of(2590, 4298, 0), Tile.of(2598, 4340, 0), Tile.of(2611, 4334, 0), Tile.of(2612, 4310, 0) };
        Tile[] STATIC_PUROPURO_ESSENCE_IMPLING_SPAWNS = new Tile[]{ Tile.of(2574, 4317, 0), Tile.of(2576, 4337, 0), Tile.of(2585, 4298, 0), Tile.of(2601, 4343, 0), Tile.of(2612, 4318, 0) };
        Tile[] STATIC_PUROPURO_ECLECTIC_IMPLING_SPAWNS = new Tile[]{ Tile.of(2567, 4319, 0), Tile.of(2591, 4295, 0), Tile.of(2591, 4340, 0), Tile.of(2615, 4326, 0) };

        Tile[] DYNAMIC_PUROPURO_LOW_IMPLING_SPAWNS = new Tile[] { Tile.of(2569, 4342, 0), Tile.of(2591, 4339, 0), Tile.of(2616, 4343, 0), Tile.of(2578, 4334, 0), Tile.of(2605, 4334, 0), Tile.of(2572, 4321, 0), Tile.of(2610, 4320, 0), Tile.of(2575, 4306, 0), Tile.of(2591, 4301, 0), Tile.of(2605, 4306, 0), Tile.of(2570, 4297, 0), Tile.of(2617, 4295, 0) };
        Tile[] DYNAMIC_PUROPURO_MID_IMPLING_SPAWNS = new Tile[] { Tile.of(2570, 4332, 0), Tile.of(2585, 4340, 0), Tile.of(2603, 4340, 0), Tile.of(2615, 4333, 0), Tile.of(2574, 4322, 0), Tile.of(2609, 4322, 0), Tile.of(2578, 4299, 0), Tile.of(2589, 4299, 0), Tile.of(2598, 4302, 0), Tile.of(2614, 4308, 0) };
        Tile[] DYNAMIC_PUROPURO_HIGH_IMPLING_SPAWNS = new Tile[]{ Tile.of(2591, 4340, 0), Tile.of(2591, 4295, 0) };

        Tile[] STATIC_OVERWORLD_LOW_IMPLING_SPAWNS = new Tile[] { Tile.of(2481, 4442, 0), Tile.of(2354, 3608, 0), Tile.of(2279, 3188, 0), Tile.of(2455, 3084, 0), Tile.of(2567, 3384, 0), Tile.of(2784, 3463, 0), Tile.of(2967, 3411, 0), Tile.of(3093, 3238, 0), Tile.of(3283, 3428, 0), Tile.of(3278, 3155, 0) };
        Tile[] DYNAMIC_OVERWORLD_ALL_IMPLING_SPAWNS = new Tile[] { Tile.of(2203, 3236, 0), Tile.of(2331, 3639, 0), Tile.of(2394, 3512, 0), Tile.of(2459, 3418, 0), Tile.of(2584, 2972, 0), Tile.of(2592, 3252, 0), Tile.of(2739, 3342, 0), Tile.of(2841, 2928, 0), Tile.of(2849, 3033, 0), Tile.of(3018, 3523, 0), Tile.of(3020, 3424, 0), Tile.of(3143, 3231, 0), Tile.of(3239, 3283, 0), Tile.of(3292, 3265, 0), Tile.of(3356, 3013, 0), Tile.of(3408, 3125, 0), Tile.of(3441, 3351, 0), Tile.of(3454, 3486, 0), Tile.of(3548, 3528, 0), Tile.of(3677, 3321, 0), Tile.of(2982, 3276, 0), Tile.of(3169, 3001, 0), Tile.of(2904, 3489, 0), Tile.of(2646, 3421, 0), Tile.of(2654, 3609, 0), Tile.of(3135, 3378, 0), Tile.of(2727, 3768, 0), Tile.of(2814, 3512, 0), Tile.of(2470, 3217, 0), Tile.of(2740, 3535, 0), Tile.of(2843, 3161, 0), Tile.of(3730, 3016, 0), Tile.of(3765, 3798, 0), Tile.of(2198, 2963, 0), Tile.of(2526, 3101, 0), Tile.of(2500, 2875, 0) };


        Tile[] IMP_DEFENDER_SPAWNS = new Tile[]{ Tile.of(2563, 4292, 0), Tile.of(2564, 4348, 0), Tile.of(2566, 4294, 0), Tile.of(2566, 4345, 0), Tile.of(2569, 4297, 0), Tile.of(2570, 4342, 0), Tile.of(2572, 4300, 0), Tile.of(2572, 4339, 0), Tile.of(2575, 4303, 0), Tile.of(2575, 4336, 0), Tile.of(2578, 4306, 0), Tile.of(2578, 4333, 0), Tile.of(2581, 4309, 0), Tile.of(2581, 4330, 0), Tile.of(2602, 4310, 0), Tile.of(2602, 4330, 0), Tile.of(2604, 4306, 0), Tile.of(2605, 4333, 0), Tile.of(2608, 4303, 0), Tile.of(2608, 4336, 0), Tile.of(2611, 4300, 0), Tile.of(2611, 4339, 0), Tile.of(2614, 4297, 0), Tile.of(2614, 4342, 0), Tile.of(2616, 4294, 0), Tile.of(2617, 4345, 0), Tile.of(2619, 4291, 0), Tile.of(2619, 4348, 0) };


        for (Tile tile : STATIC_PUROPURO_BABY_IMPLING_SPAWNS) {
            new Impling(FlyingEntityHunter.FlyingEntities.BABY_IMPLING_PP.getNpcId(), tile, false);
        }

        for (Tile tile : STATIC_PUROPURO_YOUNG_IMPLING_SPAWNS) {
            new Impling(FlyingEntityHunter.FlyingEntities.YOUNG_IMPLING_PP.getNpcId(), tile, false);
        }

        for (Tile tile : STATIC_PUROPURO_GOURMET_IMPLING_SPAWNS) {
            new Impling(FlyingEntityHunter.FlyingEntities.GOURMET_IMPLING_PP.getNpcId(), tile, false);
        }

        for (Tile tile : STATIC_PUROPURO_EARTH_IMPLING_SPAWNS) {
            new Impling(FlyingEntityHunter.FlyingEntities.EARTH_IMPLING_PP.getNpcId(), tile, false);
        }

        for (Tile tile : STATIC_PUROPURO_ESSENCE_IMPLING_SPAWNS) {
            new Impling(FlyingEntityHunter.FlyingEntities.ESSENCE_IMPLING_PP.getNpcId(), tile, false);
        }

        for (Tile tile : STATIC_PUROPURO_ECLECTIC_IMPLING_SPAWNS) {
            new Impling(FlyingEntityHunter.FlyingEntities.ECLECTIC_IMPLING_PP.getNpcId(), tile, false);
        }

        for (Tile tile : DYNAMIC_PUROPURO_LOW_IMPLING_SPAWNS) {
            new Impling(Impling.rollLowTierPuroPuroImp(), tile, true);
        }

        for (Tile tile : DYNAMIC_PUROPURO_MID_IMPLING_SPAWNS) {
            new Impling(Impling.rollMidTierPuroPuroImp(), tile, true);
        }

        for (Tile tile : DYNAMIC_PUROPURO_HIGH_IMPLING_SPAWNS) {
            new Impling(Impling.rollHighTierPuroPuroImp(), tile, true);
        }

        for (Tile tile : STATIC_OVERWORLD_LOW_IMPLING_SPAWNS) {
            new Impling(Impling.rollLowTierOverworldImp(), tile, false);
        }

        WorldTasks.schedule(Ticks.fromMinutes(2), Ticks.fromMinutes(30), () -> {
            for (Tile tile : DYNAMIC_OVERWORLD_ALL_IMPLING_SPAWNS) {
                for (int i = 0; i < 3; i++) {
                    int npcId = rollTier();
                    if (npcId != -1)
                        new Impling(npcId, tile, true);
                }
            }
        });

        for (Tile tile : IMP_DEFENDER_SPAWNS) {
            new ImpDefender(tile);
        }
    }
}