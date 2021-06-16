package com.rs.game.npc.slayer;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.pathing.Direction;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public class AbyssalDemon extends NPC {

	public AbyssalDemon(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = getCombat().getTarget();
		if (target != null && WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), getX(), getY(), getSize(), 4) && Utils.random(50) == 0)
			sendTeleport(Utils.random(2) == 0 ? target : this);
	}

	private void sendTeleport(Entity entity) {
		int entitySize = entity.getSize();
		for (int c = 0; c < 10; c++) {
			Direction dir = Direction.values()[Utils.random(Direction.values().length)];
			if (World.checkWalkStep(entity.getPlane(), entity.getX(), entity.getY(), dir, entitySize)) {
				entity.setNextSpotAnim(new SpotAnim(409));
				entity.setNextWorldTile(entity.transform(dir.getDx(), dir.getDy(), 0));
				break;
			}
		}
	}
	
	public static NPCInstanceHandler toAbyssalDemon = new NPCInstanceHandler(1615) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new AbyssalDemon(npcId, tile);
		}
	};
}