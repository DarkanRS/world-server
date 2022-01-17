package com.rs.game.player.content.world;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.pathing.DumbRouteFinder;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class TrollheimGuardTroll extends NPC {

    public TrollheimGuardTroll(int id, WorldTile tile) {
        super(id, tile);
        if(isGWDGaurd())
            setRandomWalk(false);
    }

    @Override
    public boolean withinDistance(Player tile, int distance)  {
        if(super.withinDistance(tile, distance)) {
            if(isGWDGaurd() && getTarget() == null && super.withinDistance(tile, 8))
                setTarget(tile);
            return true;
        }
        return false;
    }

    private boolean isGWDGaurd() {
        if(getX() > 2885 && getX() < 2914 && getY() > 3690 && getY() < 3703)
            return true;
        return false;
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (Utils.getDistance(this, getRespawnTile()) > 2) {
            DumbRouteFinder.addDumbPathfinderSteps(this, getRespawnTile(), 5, getClipType());
        }
    }

//    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1130, 1131, 1132, 1133, 1134) {
//        @Override
//        public NPC getNPC(int npcId, WorldTile tile) {
//            return new TrollheimGuardTroll(npcId, tile);
//        }
//    };
}
