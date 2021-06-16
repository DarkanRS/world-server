package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.PluginHandler;

public class NPCClickEvent implements PluginEvent {
	
	private static Map<Object, NPCClickHandler> HANDLERS = new HashMap<>();

	private Player player;
	private NPC npc;
	private int opNum;
	private String option;
	private boolean atNPC;

	public NPCClickEvent(Player player, NPC npc, int opNum, boolean atNPC) {
		this.player = player;
		this.npc = npc;
		this.opNum = opNum;
		this.atNPC = atNPC;
		this.option = npc.getDefinitions(player).getOption(opNum-1);
	}

	public Player getPlayer() {
		return player;
	}

	public NPC getNPC() {
		return npc;
	}
	
	public int getNPCId() {
		return npc.getId();
	}

	public String getOption() {
		return option;
	}
	
	public int getOpNum() {
		return opNum;
	}

	public boolean isAtNPC() {
		return atNPC;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		NPCClickHandler method = HANDLERS.get(getNPC().getId());
		if (method == null)
			method = HANDLERS.get(getNPC().getDefinitions().getName(getPlayer().getVars()));
		if (method == null)
			method = HANDLERS.get(getOption());
		if (method == null)
			return null;
		if (!isAtNPC() && method.isCheckDistance())
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (NPCClickHandler) method);
			if (old != null) {
				System.err.println("ERROR: Duplicate NPCClick methods for key: " + key);
			}
		}
	}

}
