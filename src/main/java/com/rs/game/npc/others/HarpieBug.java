package com.rs.game.npc.others;

import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class HarpieBug extends NPC {

	public HarpieBug(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public void handlePreHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
			Player player = (Player) hit.getSource();
			if (player.getEquipment().getShieldId() != 7053)
				hit.setDamage(0);
		}
		super.handlePreHit(hit);
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(3153) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new HarpieBug(npcId, tile);
		}
	};

}
