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

import com.rs.plugin.events.IFOnPlayerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InterfaceOnPlayerHandler extends PluginHandler<IFOnPlayerEvent> {
	private boolean checkDistance = true;

	public InterfaceOnPlayerHandler(boolean checkDistance, int[] interfaceIds, int[] componentIds, Consumer<IFOnPlayerEvent> handler) {
		super(null, handler);
		this.checkDistance = checkDistance;
		List<Object> list = new ArrayList<>();
		if (componentIds == null || componentIds.length <= 0)
			for (Integer id : interfaceIds)
				list.add(id);
		else
			for (int id : interfaceIds)
				for (int comp : componentIds)
					list.add((id << 16) + comp);
		keys = list.toArray();
	}
	
	public InterfaceOnPlayerHandler(boolean checkDistance, int[] interfaceIds, Consumer<IFOnPlayerEvent> handler) {
		this(checkDistance, interfaceIds, null, handler);
	}

	public InterfaceOnPlayerHandler(int[] interfaceIds, int[] componentIds, Consumer<IFOnPlayerEvent> handler) {
		this(true, interfaceIds, componentIds, handler);
	}
	
	public InterfaceOnPlayerHandler(int[] interfaceIds, Consumer<IFOnPlayerEvent> handler) {
		this(true, interfaceIds, null, handler);
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}
}
