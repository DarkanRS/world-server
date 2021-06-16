package com.rs.game.player.content.dialogue;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Options {
	
	private Map<String, Dialogue> options = new LinkedHashMap<>(); //LinkedHashMap O(1) but allows ordered keys
	
	public Options() {
		create();
	}
	
	public abstract void create();

	public void option(String name, Dialogue dialogue) {
		options.put(name, dialogue.getHead());
	}
	
	public void option(String name, Runnable consumer) {
		options.put(name, new Dialogue(null, consumer));
	}
	
	public void option(String name) {
		option(name, () -> {});
	}
	
	public Map<String, Dialogue> getOptions() {
		return options;
	}
}
