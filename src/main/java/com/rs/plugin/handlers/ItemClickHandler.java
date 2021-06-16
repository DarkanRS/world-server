package com.rs.plugin.handlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.rs.plugin.events.ItemClickEvent;

public abstract class ItemClickHandler extends PluginHandler<ItemClickEvent> {
	private Set<String> options;
	
	public ItemClickHandler(Object... namesOrIds) {
		super(namesOrIds);
	}
	
	public ItemClickHandler(Object[] namesOrIds, String[] options) {
		super(namesOrIds);
		this.options = new HashSet<>(Arrays.asList(options));
	}
	
	public ItemClickHandler(String[] options) {
		super(null);
		this.options = new HashSet<>(Arrays.asList(options));
	}
	
	public boolean containsOption(String option) {
		return (options == null || options.size() == 0) ? true : options.contains(option);
	}
	
	@Override
	public Object[] keys() {
		return (keys == null || keys.length <= 0) ? options.toArray() : keys;
	}
}
