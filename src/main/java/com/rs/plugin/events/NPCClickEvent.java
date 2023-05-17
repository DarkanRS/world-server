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
package com.rs.plugin.events;

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.HashMap;
import java.util.Map;

public class NPCClickEvent implements PluginEvent {

	private static Map<Object, Map<String, NPCClickHandler>> HANDLERS = new HashMap<>();

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
		option = npc.getDefinitions(player).getOption(opNum-1);
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
		Map<String, NPCClickHandler> options = HANDLERS.get(getNPC().getId());
		if (options == null)
			options = HANDLERS.get(getNPC().getDefinitions().getName(getPlayer().getVars()));
		if (options == null) {
			options = HANDLERS.get(getOption());
			if (options != null) {
				NPCClickHandler globalOption = options.get("global");
				if (globalOption == null)
					throw new RuntimeException("No global NPCClick method for option: " + getOption());
				if (!isAtNPC() && globalOption.isCheckDistance())
					return null;
				return globalOption;
			}
		}
		if (options == null)
			return null;
		NPCClickHandler method = options.get(getOption());
		if (method == null)
			method = options.get("global");
		if (method == null) {
			options = HANDLERS.get(getOption());
			if (options != null) {
				NPCClickHandler globalOption = options.get("global");
				method = globalOption;
			}
		}
		if ((method == null) || (!isAtNPC() && method.isCheckDistance()))
			return null;
		return method;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		NPCClickHandler handler = (NPCClickHandler) method;
		if (handler.getOptions() == null || handler.getOptions().isEmpty()) {
			for (Object key : handler.keys()) {
				Map<String, NPCClickHandler> map = HANDLERS.get(key);
				if (map == null)
					map = new HashMap<>();
				NPCClickHandler old = map.put("global", handler);
				HANDLERS.put(key, map);
				if (old != null)
					System.err.println("ERROR: Duplicate global NPCClick methods for key: " + key);
			}
		} else {
			for (String option : handler.getOptions()) {
				if (handler.keys() == null || handler.keys().length == 0 || handler.keys()[0] == null) {
					Map<String, NPCClickHandler> map = HANDLERS.get(option);
					if (map == null)
						map = new HashMap<>();
					NPCClickHandler old = map.put("global", handler);
					HANDLERS.put(option, map);
					if (old != null)
						System.err.println("ERROR: Duplicate global NPCClick option methods for key: " + option);
				} else {
					for (Object key : handler.keys()) {
						Map<String, NPCClickHandler> map = HANDLERS.get(key);
						if (map == null)
							map = new HashMap<>();
						NPCClickHandler old = map.put(option, handler);
						HANDLERS.put(key, map);
						if (old != null)
							System.err.println("ERROR: Duplicate NPCClick option methods for key: " + key + " option: " + option + " method: " + method);
					}
				}
			}
		}
	}

}
