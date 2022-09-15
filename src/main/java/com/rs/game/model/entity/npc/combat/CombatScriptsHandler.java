// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.entity.npc.combat;

import java.util.HashMap;
import java.util.List;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;

@PluginEventHandler
public class CombatScriptsHandler {

	private static final HashMap<Object, CombatScript> MAPPED_SCRIPTS = new HashMap<>();
	private static final CombatScript DEFAULT_SCRIPT = new Default();

	@ServerStartupEvent(Priority.FILE_IO)
	public static final void loadScripts() {
		List<Class<?>> classes;
		try {
			classes = Utils.getSubClasses("com.rs", CombatScript.class);
			for (Class<?> c : classes) {
				if (c.isAnonymousClass()) // next
					continue;
				Object o = c.getDeclaredConstructor().newInstance();
				if (!(o instanceof CombatScript script))
					continue;
				for (Object key : script.getKeys()) {
					if (key instanceof Object[] arr) {
						for (Object val : arr)
							MAPPED_SCRIPTS.put(val, script);
					} else
						MAPPED_SCRIPTS.put(key, script);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Logger.info(CombatScriptsHandler.class, "loadScripts", "Loaded combat scripts for " + MAPPED_SCRIPTS.size() + " NPCs...");
	}

	public static int attack(final NPC npc, final Entity target) {
		CombatScript script = MAPPED_SCRIPTS.get(npc.getId());
		if (script == null) {
			script = MAPPED_SCRIPTS.get(npc.getDefinitions().getName());
			if (script == null)
				script = DEFAULT_SCRIPT;
		}
		return script.attack(npc, target);
	}
}
