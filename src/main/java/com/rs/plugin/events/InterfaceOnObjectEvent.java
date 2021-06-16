package com.rs.plugin.events;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.plugin.handlers.InterfaceOnObjectHandler;
import com.rs.plugin.handlers.PluginHandler;

public class InterfaceOnObjectEvent implements PluginEvent {
	
	private static Map<Object, InterfaceOnObjectHandler> HANDLERS = new HashMap<>();

	private Player player;
	private int interfaceId;
	private int componentId;
	private int slotId;
	private GameObject object;
	private boolean atObject;

	public InterfaceOnObjectEvent(Player player, GameObject object, int interfaceId, int componentId, int slotId, boolean atObject) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.componentId = componentId;
		this.slotId = slotId;
		this.object = object;
		this.atObject = atObject;
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

	public GameObject getObject() {
		return object;
	}

	public int getObjectId() {
		return object.getId();
	}

	public boolean isAtObject() {
		return atObject;
	}

	@Override
	public PluginHandler<? extends PluginEvent> getMethod() {
		InterfaceOnObjectHandler method = HANDLERS.get((interfaceId << 16) + componentId);
		if (method == null)
			method = HANDLERS.get(getInterfaceId());
		if (method == null)
			return null;
		if (!isAtObject() && method.isCheckDistance())
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		for (Object key : method.keys()) {
			PluginHandler<? extends PluginEvent> old = HANDLERS.put(key, (InterfaceOnObjectHandler) method);
			if (old != null) {
				System.err.println("ERROR: Duplicate InterfaceOnObject methods for key: " + key);
			}
		}
	}
}
