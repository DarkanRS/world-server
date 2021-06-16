package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.handlers.PluginHandler;

public class ButtonClickEvent implements PluginEvent {
	
	private static Map<Object, PluginHandler<? extends PluginEvent>> HANDLERS = new HashMap<>();

	private Player player;
	private int interfaceId, componentId, slotId, slotId2;
	private ClientPacket packet;

	public ButtonClickEvent(Player player, int interfaceId, int componentId, int slotId, int slotId2, ClientPacket packet) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.slotId = slotId;
		this.slotId2 = slotId2;
		this.packet = packet;
	}

	public Player getPlayer() {
		return player;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getComponentId() {
		return componentId;
	}

	public int getSlotId() {
		return slotId;
	}
	
	public int getSlotId2() {
		return slotId2;
	}

	public ClientPacket getPacket() {
		return packet;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		return HANDLERS.get(getInterfaceId());
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, method);
			if (old != null) {
				System.err.println("ERROR: Duplicate ButtonClick methods for key: " + key);
			}
		}
	}

}
