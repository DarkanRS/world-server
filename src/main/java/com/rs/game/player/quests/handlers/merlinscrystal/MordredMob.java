package com.rs.game.player.quests.handlers.merlinscrystal;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.controllers.MerlinsCrystalCrateScene;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.lostcity.ArcherLostCityD;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

import static com.rs.game.player.quests.handlers.dragonslayer.DragonSlayer.PREPARE_FOR_CRANDOR;
import static com.rs.game.player.quests.handlers.merlinscrystal.MerlinsCrystal.*;

@PluginEventHandler
public class MordredMob extends NPC {
    final static int MORGAN = 248;
    public MordredMob(int id, WorldTile tile) {
        super(id, tile, false);
    }

    @Override
    public void sendDeath(Entity source) {
        if(source instanceof Player) {
            Player p = (Player)source;
            if(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == MerlinsCrystal.CONFRONT_KEEP_LA_FAYE) {
                for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
                    if(npc.getId() == MORGAN)
                        return;
                NPC morgan =  World.spawnNPC(MORGAN, new WorldTile(2769, 3403, 2), -1, false, true);
                morgan.setNextSpotAnim(new SpotAnim(1605, 0, 0));
                morgan.forceTalk("Stop! Spare my son!");
                morgan.faceSouth();
                morgan.setRandomWalk(false);
                morgan.lingerForPlayer(p);
            } else {
                super.sendDeath(source);
            }
        } else
            super.sendDeath(source);
    }

    public void resetNPC(final Entity source) {
        resetWalkSteps();
        removeTarget();
        reset();
        finish();
        if (!isSpawned())
            setRespawnTask(0);
    }

    @Override
    public boolean canBeAttackedBy(Player player) {
        for(NPC npc : World.getNPCsInRegion(player.getRegionId()))
            if(npc.getId() == MORGAN)
                return false;
        return true;
    }

    @Override
    public boolean canAggroPlayer(Player player) {
        for(NPC npc : World.getNPCsInRegion(player.getRegionId()))
            if(npc.getId() == MORGAN)
                return false;
        return true;
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(247) {
        @Override
        public NPC getNPC(int npcId, WorldTile tile) {
            return new MordredMob(npcId, tile);
        }
    };


}
