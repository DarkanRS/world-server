package com.rs.game.content.quests.merlinscrystal;

import com.rs.game.World;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class MordredMob extends NPC {
	private final static int MORGAN = 248;
	public MordredMob(int id, Tile tile) {
		super(id, tile, false);
	}

	@Override
	public void sendDeath(Entity source) {
		if(source instanceof Player player) {
			if(player.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == MerlinsCrystal.CONFRONT_KEEP_LA_FAYE) {
				for(NPC npc : World.getNPCsInChunkRange(player.getChunkId(), 1))
					if(npc.getId() == MORGAN)
						return;
                OwnedNPC morgan = new OwnedNPC(player, MORGAN, Tile.of(2769, 3403, 2), true);
				morgan.setNextSpotAnim(new SpotAnim(1605, 0, 0));
				morgan.forceTalk("Stop! Spare my son!");
				morgan.faceSouth();
				morgan.setRandomWalk(false);
                WorldTasks.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        resetNPC(null);
                    }
                }, 10);
			} else
				super.sendDeath(source);
		} else
			super.sendDeath(source);
	}

	public void resetNPC(final Entity source) {
		resetWalkSteps();
		removeTarget();
		reset();
		finish();
		if (!isSpawned())
			setRespawnTask(10);
	}

	@Override
	public boolean canBeAttackedBy(Player player) {
		for(NPC npc : World.getNPCsInChunkRange(player.getChunkId(), 1))
			if(npc.getId() == MORGAN && npc instanceof OwnedNPC morgan)
                if(player.getUsername().equalsIgnoreCase(morgan.getOwner().getUsername()))
				    return false;
		return true;
	}

	@Override
	public boolean canAggroPlayer(Player player) {
		for(NPC npc : World.getNPCsInChunkRange(player.getChunkId(), 1))
            if(npc.getId() == MORGAN && npc instanceof OwnedNPC morgan)
                if(player.getUsername().equalsIgnoreCase(morgan.getOwner().getUsername()))
                    return false;
		return true;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(247, (npcId, tile) -> new MordredMob(npcId, tile));


}
