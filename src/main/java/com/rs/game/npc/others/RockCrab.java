package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class RockCrab extends NPC {

	private int realId;

	public RockCrab(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		realId = id;
		setForceAgressive(true); // to ignore combat lvl
	}

	@Override
	public void setTarget(Entity entity) {
		if (realId == getId())
			this.setNextNPCTransformation(realId - 1);
		this.setHitpoints(this.getMaxHitpoints());
		super.setTarget(entity);
	}

	@Override
	public void reset() {
		setNPC(realId);
		super.reset();
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1266, 1268, 2453, 2886) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new RockCrab(npcId, tile, false);
		}
	};
}
