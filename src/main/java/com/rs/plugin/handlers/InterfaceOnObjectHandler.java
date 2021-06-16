package com.rs.plugin.handlers;

import java.util.ArrayList;
import java.util.List;

import com.rs.plugin.events.InterfaceOnObjectEvent;

public abstract class InterfaceOnObjectHandler extends PluginHandler<InterfaceOnObjectEvent> {
	private boolean checkDistance = true;
	
	public InterfaceOnObjectHandler(boolean checkDistance, int[] interfaceIds, int[] componentIds) {
		super(null);
		this.checkDistance = checkDistance;
		List<Object> list = new ArrayList<>();
		if (componentIds.length <= 0) {
		for (Integer id : interfaceIds)
			list.add(id);
		} else {
			for (int id : interfaceIds)
				for (int comp : componentIds)
					list.add((id << 16) + comp);
		}
		keys = list.toArray();
	}
	
	public InterfaceOnObjectHandler(int[] interfaceIds, int[] componentIds) {
		this(true, interfaceIds, componentIds);
	}

	public boolean isCheckDistance() {
		return checkDistance;
	};
}
