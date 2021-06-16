package com.rs.game.npc.combat;

import java.util.ArrayList;
import java.util.HashMap;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class CombatScriptsHandler {

	private static final HashMap<Object, CombatScript> cachedCombatScripts = new HashMap<Object, CombatScript>();
	private static final CombatScript DEFAULT_SCRIPT = new Default();

	@ServerStartupEvent
	public static final void loadScripts() {
		ArrayList<Class<?>> classes;
		try {
			classes = Utils.getClasses("com.rs.game.npc.combat.impl");
			for (Class<?> c : classes) {
				if (c.isAnonymousClass()) // next
					continue;
				Object o = c.getDeclaredConstructor().newInstance();
				if (!(o instanceof CombatScript))
					continue;
				CombatScript script = (CombatScript) o;
				for (Object key : script.getKeys())
					cachedCombatScripts.put(key, script);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Logger.log("CombatScriptsHandler", "Loaded combat scripts for " + cachedCombatScripts.size() + " NPCs...");
	}

	public static int attack(final NPC npc, final Entity target) {
		CombatScript script = cachedCombatScripts.get(npc.getId());
		if (script == null) {
			script = cachedCombatScripts.get(npc.getDefinitions().getName());
			if (script == null)
				script = DEFAULT_SCRIPT;
		}
		return script.attack(npc, target);
	}
}
