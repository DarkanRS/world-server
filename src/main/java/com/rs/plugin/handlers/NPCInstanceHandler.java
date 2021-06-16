package com.rs.plugin.handlers;

import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.events.NPCInstanceEvent;

public abstract class NPCInstanceHandler extends PluginHandler<NPCInstanceEvent> {

	public NPCInstanceHandler(Object... keys) {
		super(keys);
	}
	
	public abstract NPC getNPC(int npcId, WorldTile tile);

	@Override
	public final void handle(NPCInstanceEvent e) { }
	
	@Override
	public final boolean handleGlobal(NPCInstanceEvent e) { return false; }
	
	@Override
	public final Object getObj(NPCInstanceEvent e) {
		NPC npc = getNPC(e.getNpcId(), e.getTile());
		npc.setSpawned(e.isSpawned());
		return npc;
	}

}
