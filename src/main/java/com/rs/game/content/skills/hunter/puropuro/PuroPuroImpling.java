package com.rs.game.content.skills.hunter.puropuro;

import com.rs.game.content.skills.hunter.FlyingEntityHunter;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.ClipType;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class PuroPuroImpling extends NPC {

    public PuroPuroImpling(int id, WorldTile tile) {
        super(id, tile);
        setRandomWalk(true);
        setClipType(ClipType.FLYING);
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6055, 6056, 6057, 6058, 6059, 6060, 7904, 6061, 6062, 6063, 7846, 6064, 7905, 7906 }) {
        @Override
        public NPC getNPC(int npcId, WorldTile tile) { return new PuroPuroImpling(npcId, tile); }
    };

    public static int getRandomRareImplingId() {
        int random = Utils.getRandomInclusive(1000);
        if (random < 3) //.33% chance
            return FlyingEntityHunter.FlyingEntities.KINGLY_IMPLING_PP.getNpcId();
        if (random < 13) //1% chance
            return FlyingEntityHunter.FlyingEntities.ZOMBIE_IMPLING_PP.getNpcId();
        if (random < 43) //3% chance
            return FlyingEntityHunter.FlyingEntities.DRAGON_IMPLING_PP.getNpcId();
        if (random < 93) //5% chance
            return FlyingEntityHunter.FlyingEntities.PIRATE_IMPLING_PP.getNpcId();
        if (random < 160) //6.7% chance
            return FlyingEntityHunter.FlyingEntities.NINJA_IMPLING_PP.getNpcId();
        if (random < 360) //20% chance
            return FlyingEntityHunter.FlyingEntities.MAGPIE_IMPLING_PP.getNpcId();
        return FlyingEntityHunter.FlyingEntities.NATURE_IMPLING_PP.getNpcId();
    }

    @ServerStartupEvent
    public static void initPuroPuroSpawns() {
        WorldTile[] BABY_IMPLING_SPAWNS = new WorldTile[]{new WorldTile(2563, 4291, 0), new WorldTile(2563, 4348, 0), new WorldTile(2569, 4323, 0), new WorldTile(2571, 4305, 0), new WorldTile(2581, 4300, 0), new WorldTile(2596, 4296, 0), new WorldTile(2609, 4339, 0), new WorldTile(2610, 4304, 0), new WorldTile(2615, 4322, 0), new WorldTile(2620, 4291, 0), new WorldTile(2620, 4348, 0)};
        WorldTile[] YOUNG_IMPLING_SPAWNS = new WorldTile[]{new WorldTile(2564, 4321, 0), new WorldTile(2573, 4330, 0), new WorldTile(2574, 4321, 0), new WorldTile(2590, 4348, 0), new WorldTile(2592, 4291, 0), new WorldTile(2595, 4343, 0), new WorldTile(2612, 4327, 0), new WorldTile(2612, 4309, 0), new WorldTile(2619, 4322, 0), new WorldTile(2587, 4300, 0)};
        WorldTile[] GOURMET_IMPLING_SPAWNS = new WorldTile[]{new WorldTile(2568, 4296, 0), new WorldTile(2569, 4327, 0), new WorldTile(2574, 4311, 0), new WorldTile(2574, 4311, 0), new WorldTile(2585, 4296, 0), new WorldTile(2597, 4293, 0), new WorldTile(2609, 4317, 0), new WorldTile(2615, 4298, 0), new WorldTile(2618, 4321, 0)};
        WorldTile[] EARTH_IMPLING_SPAWNS = new WorldTile[]{new WorldTile(2570, 4330, 0), new WorldTile(2598, 4340, 0), new WorldTile(2587, 4342, 0), new WorldTile(2612, 4310, 0), new WorldTile(2611, 4334, 0)};
        WorldTile[] ESSENCE_IMPLING_SPAWNS = new WorldTile[]{new WorldTile(2585, 4298, 0), new WorldTile(2574, 4316, 0), new WorldTile(2576, 4337, 0), new WorldTile(2619, 4341, 0), new WorldTile(2612, 4318, 0)};
        WorldTile[] ECLECTIC_IMPLING_SPAWNS = new WorldTile[]{new WorldTile(2567, 4319, 0), new WorldTile(2591, 4340, 0), new WorldTile(2591, 4295, 0), new WorldTile(2615, 4326, 0)};
        WorldTile[] RARE_IMPLING_SPAWNS = new WorldTile[]{new WorldTile(2591, 4340, 0), new WorldTile(2591, 4295, 0)};
        WorldTile[] IMP_DEFENDER_SPAWNS = new WorldTile[]{ new WorldTile(2561, 4350, 0), new WorldTile(2564, 4347, 0), new WorldTile(2567, 4344, 0), new WorldTile(2570, 4341, 0), new WorldTile(2573, 4338, 0), new WorldTile(2576, 4335, 0), new WorldTile(2579, 4332, 0), new WorldTile(2582, 4329, 0), new WorldTile(2601, 4310, 0), new WorldTile(2604, 4307, 0), new WorldTile(2607, 4304, 0), new WorldTile(2610, 4301, 0), new WorldTile(2613, 4298, 0), new WorldTile(2616, 4295, 0), new WorldTile(2619, 4292, 0), new WorldTile(2622, 4289, 0), new WorldTile(2622, 4350, 0), new WorldTile(2619, 4347, 0), new WorldTile(2616, 4344, 0), new WorldTile(2613, 4341, 0), new WorldTile(2610, 4338, 0), new WorldTile(2607, 4335, 0), new WorldTile(2604, 4332, 0), new WorldTile(2601, 4329, 0), new WorldTile(2582, 4310, 0), new WorldTile(2579, 4307, 0), new WorldTile(2576, 4304, 0), new WorldTile(2573, 4301, 0), new WorldTile(2570, 4298, 0), new WorldTile(2567, 4295, 0), new WorldTile(2564, 4292, 0), new WorldTile(2561, 4289, 0) };

        for (WorldTile tile : BABY_IMPLING_SPAWNS) {
            new PuroPuroImpling(FlyingEntityHunter.FlyingEntities.BABY_IMPLING_PP.getNpcId(), tile);
        }

        for (WorldTile tile : YOUNG_IMPLING_SPAWNS) {
            new PuroPuroImpling(FlyingEntityHunter.FlyingEntities.YOUNG_IMPLING_PP.getNpcId(), tile);
        }

        for (WorldTile tile : GOURMET_IMPLING_SPAWNS) {
            new PuroPuroImpling(FlyingEntityHunter.FlyingEntities.GOURMET_IMPLING_PP.getNpcId(), tile);
        }

        for (WorldTile tile : EARTH_IMPLING_SPAWNS) {
            new PuroPuroImpling(FlyingEntityHunter.FlyingEntities.EARTH_IMPLING_PP.getNpcId(), tile);
        }

        for (WorldTile tile : ESSENCE_IMPLING_SPAWNS) {
            new PuroPuroImpling(FlyingEntityHunter.FlyingEntities.ESSENCE_IMPLING_PP.getNpcId(), tile);
        }

        for (WorldTile tile : ECLECTIC_IMPLING_SPAWNS) {
            new PuroPuroImpling(FlyingEntityHunter.FlyingEntities.ECLECTIC_IMPLING_PP.getNpcId(), tile);
        }

        for (WorldTile tile : RARE_IMPLING_SPAWNS) {
            new PuroPuroImpling(getRandomRareImplingId(), tile);
        }

        for (WorldTile tile : IMP_DEFENDER_SPAWNS) {
            new ImpDefender(6074, tile);
        }
    }
}