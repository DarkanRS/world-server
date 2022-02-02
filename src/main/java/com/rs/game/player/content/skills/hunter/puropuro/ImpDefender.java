package com.rs.game.player.content.skills.hunter.puropuro;

import com.rs.game.npc.NPC;
import com.rs.game.pathing.ClipType;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class ImpDefender extends NPC {

    //Add logic for catching nearby players
    //Fix clipping
    //Add imp repellent protection

    public ImpDefender(int id, WorldTile tile) {
        super(id, tile);
        setClipType(ClipType.NORMAL);
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6074 }) {
        @Override
        public NPC getNPC(int npcId, WorldTile tile) { return new ImpDefender(npcId, tile); }
    };

    public static NPCClickHandler testImp = new NPCClickHandler(6074) {
        @Override
        public void handle(NPCClickEvent e) {
            e.getPlayer().sendMessage("clip type: " + e.getNPC().getClipType() + ", randomWalk: " + e.getNPC().shouldRandomWalk());
        }
    };
}

