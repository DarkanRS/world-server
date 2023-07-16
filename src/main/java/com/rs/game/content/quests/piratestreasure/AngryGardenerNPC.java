package com.rs.game.content.quests.piratestreasure;

import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.PluginManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCDeathEvent;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class AngryGardenerNPC extends NPC {

	public AngryGardenerNPC(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
	}

	@Override
	public void sendDeath(Entity source) {
		if(source instanceof Player player)
			player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB("KILLED_GARDENER", true);
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		removeTarget();
		setNextAnimation(null);
		PluginManager.handle(new NPCDeathEvent(this, source));
		WorldTasks.scheduleTimer(loop -> {
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
				return false;
			}
			return true;
		});
	}


	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1217, (npcId, tile) -> new AngryGardenerNPC(npcId, tile, true));
}
