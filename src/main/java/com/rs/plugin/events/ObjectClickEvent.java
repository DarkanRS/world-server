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

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PluginHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectClickEvent implements PluginEvent {

	private static final Map<Object, Map<Integer, List<ObjectClickHandler>>> METHODS = new HashMap<>();

	private final Player player;
	private final GameObject object;
	private final ClientPacket opNum;
	private final String option;
	private final boolean atObject;
	private final int objectId;

	public ObjectClickEvent(Player player, GameObject object, ClientPacket opNum, boolean atObject) {
		this.player = player;
		this.object = object;
		this.opNum = opNum;
		this.atObject = atObject;
		objectId = object.getId();
		option = object.getDefinitions(player).getOption(opNum);
	}

	public String getOption() {
		return option;
	}

	public Player getPlayer() {
		return player;
	}

	public GameObject getObject() {
		return object;
	}

	public ClientPacket getOpNum() {
		return opNum;
	}

	public boolean isAtObject() {
		return atObject;
	}

	public int getObjectId() {
		return objectId;
	}

	public boolean objectAt(int x, int y) {
		return object.getTile().isAt(x, y);
	}

	public boolean objectAt(int x, int y, int plane) {
		return object.getTile().isAt(x, y, plane);
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		ObjectClickHandler handler = (ObjectClickHandler) method;
		for (Object key : handler.keys()) {
            Map<Integer, List<ObjectClickHandler>> locMap = METHODS.computeIfAbsent(key, k -> new HashMap<>());
            if (handler.getType() == null && (handler.getTiles() == null || handler.getTiles().length <= 0)) {
				List<ObjectClickHandler> methods = locMap.get(0);
				if (methods == null)
					methods = new ArrayList<>();
				methods.add(handler);
				locMap.put(0, methods);
			} else if (handler.getType() != null) {
				List<ObjectClickHandler> methods = locMap.get(-handler.getType().id);
				if (methods == null)
					methods = new ArrayList<>();
				methods.add(handler);
				locMap.put(-handler.getType().id, methods);
			} else
				for (Tile tile : handler.getTiles()) {
					List<ObjectClickHandler> methods = locMap.get(tile.getTileHash());
					if (methods == null)
						methods = new ArrayList<>();
					methods.add(handler);
					locMap.put(tile.getTileHash(), methods);
				}
			}
		}
	}

	@Override
	public List<PluginHandler<? extends PluginEvent>> getMethods() {
		List<PluginHandler<? extends PluginEvent>> valids = new ArrayList<>();
		Map<String, List<ObjectClickHandler>> optionMap = METHODS.get(getObjectId());
		if (optionMap == null) {
			optionMap = METHODS.get(getObject().getDefinitions(getPlayer()).getName());
		}
		if (optionMap != null) {
			List<ObjectClickHandler> handlers = optionMap.getOrDefault(getOption(), optionMap.get("global"));
			if (handlers != null) {
				for (ObjectClickHandler handler : handlers) {
					if (!isAtObject() && handler.isCheckDistance())
						continue;
					valids.add(handler);
				}
			}
		}
		return valids;
	}
	public Player component1() {
		return player;
	}

	public GameObject component2() {
		return object;
	}

	public String component3() {
		return option;
	}
}
