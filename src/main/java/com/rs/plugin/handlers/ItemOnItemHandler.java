package com.rs.plugin.handlers;

import java.util.HashSet;
import java.util.Set;

import com.rs.plugin.events.ItemOnItemEvent;

public abstract class ItemOnItemHandler extends PluginHandler<ItemOnItemEvent> {
			
	public ItemOnItemHandler(int[] itemsUsed, int[] usedWiths) {
		super(null);
		Set<Object> list = new HashSet<>();
		if (usedWiths.length > 0) {
			for (Integer usedWith : usedWiths) {
				for (int used : itemsUsed) {
					list.add((usedWith << 16) + used);
					list.add((used << 16) + usedWith);
				}
			}
		} else {
			for (int used : itemsUsed)
				list.add(-used);
		}
		this.keys = list.toArray();
	}
	
	public ItemOnItemHandler(int itemUsed, int[] usedWith) {
		this(new int[] { itemUsed }, usedWith);
	}
	
	public ItemOnItemHandler(int[] itemUsed, int usedWith) {
		this(itemUsed, new int[] { usedWith });
	}
	
	public ItemOnItemHandler(int used, int usedWith) {
		this(new int[] { used },  new int[] { usedWith });
	}
	
	public ItemOnItemHandler(int[] used) {
		this(used,  new int[] { });
	}
	
	public ItemOnItemHandler(int used) {
		this(new int[] { used },  new int[] { });
	}

	public ItemOnItemHandler(boolean directKeySet, Object[] keys) {
		super(keys);
	}

}
