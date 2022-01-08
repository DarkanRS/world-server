package com.rs.game.player.quests.handlers.piratestreasure;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.quests.Quest;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class AngryGardenerNPC extends NPC {

	public AngryGardenerNPC(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
	}

	@Override
	public void sendDeath(Entity source) {
		if(source instanceof Player p) {
			p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB(PiratesTreasure.KILLED_GARDENER_ATTR, true);
		}
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		removeTarget();
		setNextAnimation(null);
		PluginManager.handle(new NPCDeathEvent(this, source));
		WorldTasks.schedule(new WorldTask() {
			int loop;
			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player player)
						player.getControllerManager().processNPCDeath(AngryGardenerNPC.this);
					drop();
					reset();
					finish();
					if (!isSpawned())
						setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}


	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1217) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new AngryGardenerNPC(npcId, tile, true);
		}
	};
}
