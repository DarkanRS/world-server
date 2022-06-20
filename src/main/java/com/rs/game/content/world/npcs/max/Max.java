package com.rs.game.content.world.npcs.max;

import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Max extends NPC {
	
	public static NPCClickHandler click = new NPCClickHandler(new Object[] { "Max" }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (!(e.getNPC() instanceof Max max))
				return;
			if (e.getOption().toLowerCase().contains("talk"))
				e.getPlayer().startConversation(new MaxD(e.getPlayer(), max));
		}
	};

	public Max(int id, WorldTile tile) {
		super(id, tile);
		
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler("Max") {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Max(npcId, tile);
		}
	};
}
