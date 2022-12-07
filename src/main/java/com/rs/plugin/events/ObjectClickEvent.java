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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PluginHandler;

public class ObjectClickEvent implements PluginEvent {

	private static Map<Object, Map<Integer, List<ObjectClickHandler>>> METHODS = new HashMap<>();

	private Player player;
	private GameObject object;
	private ClientPacket opNum;
	private String option;
	private boolean atObject;
	private int objectId;

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

	@Override
	public List<PluginHandler<? extends PluginEvent>> getMethods() {
		List<PluginHandler<? extends PluginEvent>> valids = new ArrayList<>();
		Map<Integer, List<ObjectClickHandler>> methodMapping = METHODS.get(getObjectId());
		if (methodMapping == null)
			methodMapping = METHODS.get(getObject().getDefinitions(getPlayer()).getName());
		if (methodMapping == null)
			return null;
		List<ObjectClickHandler> methods = methodMapping.get(getObject().getTile().getTileHash());
		if (methods == null)
			methods = methodMapping.get(-getObject().getType().id);
		if (methods == null)
			methods = methodMapping.get(0);
		if (methods == null)
			return null;
		for (ObjectClickHandler method : methods) {
			if (!isAtObject() && method.isCheckDistance())
				continue;
			valids.add(method);
		}
		return valids;
	}

	public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
		ObjectClickHandler handler = (ObjectClickHandler) method;
		for (Object key : handler.keys()) {
			Map<Integer, List<ObjectClickHandler>> locMap = METHODS.get(key);
			if (locMap == null) {
				locMap = new HashMap<>();
				METHODS.put(key, locMap);
			}
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
				for (WorldTile tile : handler.getTiles()) {
					List<ObjectClickHandler> methods = locMap.get(tile.getTileHash());
					if (methods == null)
						methods = new ArrayList<>();
					methods.add(handler);
					locMap.put(tile.getTileHash(), methods);
				}
		}
	}

}
