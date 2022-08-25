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
	
	private static final int MAX_NORM = 3373, MAX_PESTLE = 3374, MAX_FLETCH = 3380, MAX_SMITH = 3399, MAX_ADZE = 3705;
	
	private enum Task {
		FARMING,
		WOODCUTTING,
		MINING,
		FLETCHING,
		SMITHING
	}
	
	private Task task;

	public Max(int id, WorldTile tile) {
		super(id, tile);
		setRun(true);
		setIgnoreNPCClipping(true);
		task = Task.FARMING;
		transformIntoNPC(MAX_NORM);
	}
	
	@Override
	public void processNPC() {
		
	}
	
	public static NPCClickHandler clickClose = new NPCClickHandler(new Object[] { MAX_NORM, MAX_PESTLE, MAX_FLETCH, MAX_SMITH, MAX_ADZE }, new String[] { "Talk-to", "Trade" }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (!(e.getNPC() instanceof Max max))
				return;
			switch(e.getOption()) {
				case "Talk-to" -> e.getPlayer().startConversation(new MaxD(e.getPlayer(), max));
				case "Trade" -> e.getPlayer().sendMessage("Sending trade request...");
			}
		}
	};

	public static NPCClickHandler clickDistance = new NPCClickHandler(false, new Object[] { MAX_NORM, MAX_PESTLE, MAX_FLETCH, MAX_SMITH, MAX_ADZE }, new String[] { "Follow" }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().getActionManager().setAction(new EntityFollow(e.getNPC()));
		}
	};
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(MAX_NORM, MAX_PESTLE, MAX_FLETCH, MAX_SMITH, MAX_ADZE) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Max(npcId, tile);
		}
	};
}
