package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Jadinko extends NPC {

	public Jadinko(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (source instanceof Player) {
			Player player = (Player) source;
			player.addJadinkoFavor((getId() == 13820 ? 3 : getId() == 13821 ? 7 : 10));
		}
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(13820, 13821, 13822) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Jadinko(npcId, tile);
		}
	};
}
