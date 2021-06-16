package com.rs.game.player.content.minigames.wguild;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class WGCyclops extends NPC {

	public WGCyclops(int id, WorldTile tile) {
		super(id, tile);
	}
	
	@Override
	public void drop(Player killer) {
		super.drop(killer);
		WarriorsGuild.killedCyclopses++;
		if (killer.getControllerManager().getController() != null && killer.getControllerManager().getController() instanceof WarriorsGuild) {
			WarriorsGuild controller = (WarriorsGuild) killer.getControllerManager().getController();
			if (controller.inCyclopse) {
				if (Utils.random(50) == 0)
					sendDrop(killer, new Item(WarriorsGuild.getBestDefender(killer)));
			} else {
				killer.sendMessage("Your time has expired and the cyclops will no longer drop defenders.");
			}
		}
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(4291, 4292, 6078, 6079, 6080, 6081) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new WGCyclops(npcId, tile);
		}
	};
}
