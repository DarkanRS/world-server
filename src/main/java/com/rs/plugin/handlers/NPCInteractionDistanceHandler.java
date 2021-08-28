package com.rs.plugin.handlers;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.plugin.events.NPCInteractionDistanceEvent;

public abstract class NPCInteractionDistanceHandler extends PluginHandler<NPCInteractionDistanceEvent> {

	public NPCInteractionDistanceHandler(Object... keys) {
		super(keys);
	}
	
	public abstract int getDistance(Player player, NPC npc);

	@Override
	public final void handle(NPCInteractionDistanceEvent e) { }
	
	@Override
	public final boolean handleGlobal(NPCInteractionDistanceEvent e) { return false; }
	
	@Override
	public final Object getObj(NPCInteractionDistanceEvent e) {
		return getDistance(e.getPlayer(), e.getNpc());
	}

}
