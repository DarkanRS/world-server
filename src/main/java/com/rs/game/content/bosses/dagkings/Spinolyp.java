package com.rs.game.content.bosses.dagkings;

import com.rs.game.model.entity.npc.NPC;
import com.rs.engine.pathfinder.Direction;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Spinolyp extends NPC {
	public Spinolyp(int id, Tile tile) {
		super(id, tile);
		setIgnoreDocile(true);
		setRandomWalk(false);
	}
	
	@Override
	public boolean canMove(Direction dir) {
		return false;
	}

	@Override
	public void processNPC() {
		super.processNPC();
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler("Spinolyp", Spinolyp::new);
}
