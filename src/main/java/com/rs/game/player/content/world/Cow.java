package com.rs.game.player.content.world;

import com.rs.game.ForceTalk;
import com.rs.game.npc.NPC;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemOnNPCEvent;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Cow extends NPC {

	public Cow(int id, WorldTile tile) {
		super(id, tile);
	}
	
	@Override
	public void processNPC() {
		if (Utils.random(100) == 0)
			setNextForceTalk(new ForceTalk("Moo"));
		super.processNPC();
	}
	
	public static ItemOnNPCHandler itemOnCow = new ItemOnNPCHandler("Cow") {
		@Override
		public void handle(ItemOnNPCEvent e) {
			e.getPlayer().sendMessage("The cow doesn't want that.");
		}
	};
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler("Cow") {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Cow(npcId, tile);
		}
	};

}
