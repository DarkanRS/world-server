package com.rs.game.content.quests.whatliesbelow.npcs;

import com.rs.game.content.bosses.tormenteddemon.TormentedDemon;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@PluginEventHandler
public class KingRoaldBossWLB extends NPC {
    public KingRoaldBossWLB(Tile tile) {
        super(5838, tile, true);
    }

    @Override
    public void handlePreHit(Hit hit) {
        if (hit.getDamage() >= getHitpoints())
            hit.setDamage(getHitpoints()-1);
    }

    @Override
    public void processNPC() {
        super.processNPC();
        if (inCombat() && Utils.random(25) == 0)
            setNextForceTalk(new ForceTalk(Stream.of("Have at you!", "I will smite you, cur!", "Take that, knave!").collect(Collectors.toList()).get(Utils.random(3))));
    }

    @Override
    public void sendDeath(Entity killer) {
        setHitpoints(1);
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 5838}, (npcId, tile) -> new KingRoaldBossWLB(tile));
}
