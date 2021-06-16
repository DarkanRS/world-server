package com.rs.game.npc.others;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class MutatedZygomite extends ConditionalDeath {

	boolean lvl74;

	public MutatedZygomite(int id, WorldTile tile) {
		super(7421, null, false, id, tile);
		this.lvl74 = id == 3344;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!isUnderCombat() && !isDead())
			resetNPC();
	}

	private void resetNPC() {
		setNextNPCTransformation(lvl74 ? 3344 : 3345);
		setNextWorldTile(getRespawnTile());
	}
	
	@Override
	public void onRespawn() {
		resetNPC();
	}

	public static void transform(final Player player, final NPC npc) {
		if (npc.isCantInteract())
			return;
		player.setNextAnimation(new Animation(2988));
		npc.setNextNPCTransformation(npc.getId() + 2);
		npc.setNextAnimation(new Animation(2982));
		npc.setCantInteract(true);
		npc.getCombat().setTarget(player);
		npc.setHitpoints(npc.getMaxHitpoints());
		npc.resetLevels();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				npc.setCantInteract(false);
			}
		}, 1);
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(3344, 3345, 3346, 3347) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new MutatedZygomite(npcId, tile);
		}
	};
}
