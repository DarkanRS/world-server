package com.rs.game.content.world.npcs.max;

import com.rs.game.model.entity.actions.EntityFollow;
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
			switch(e.getOption()) {
				case "Talk-to" -> e.getPlayer().startConversation(new MaxD(e.getPlayer(), max));
				case "Trade" -> e.getPlayer().sendMessage("Sending trade request...");
				case "Follow" -> e.getPlayer().getActionManager().setAction(new EntityFollow(e.getNPC()));
			}
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
