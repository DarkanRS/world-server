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
package com.rs.plugin.handlers;

import java.util.function.Consumer;

import com.rs.plugin.events.NPCDropEvent;

public class NPCDropHandler extends PluginHandler<NPCDropEvent> {
	private Object[] npcKeys, itemKeys;
	
	public NPCDropHandler(Object[] npcNamesOrIds, Object[] itemNamesOrIds, Consumer<NPCDropEvent> handler) {
		super(new Object[] { "meme" }, handler);
		this.npcKeys = npcNamesOrIds;
		this.itemKeys = itemNamesOrIds;
	}

	public Object[] getNpcKeys() {
		return npcKeys;
	}

	public Object[] getItemKeys() {
		return itemKeys;
	}
}
