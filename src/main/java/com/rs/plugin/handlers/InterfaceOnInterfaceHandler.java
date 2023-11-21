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

import com.rs.plugin.events.IFOnIFEvent;
import com.rs.plugin.events.IFOnNPCEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InterfaceOnInterfaceHandler extends PluginHandler<IFOnIFEvent> {
	private boolean bidirectional;
	private Object[] fromKeys;
	private Object[] toKeys;
	public InterfaceOnInterfaceHandler(boolean bidirectional, int[] fromInterfaceIds, int[] fromComponentIds, int[] toInterfaceIds, int[] toComponentIds, Consumer<IFOnIFEvent> handler) {
		super(new Object[] { "meme" }, handler);
		this.bidirectional = bidirectional;
		List<Object> fromList = new ArrayList<>();
		if (fromComponentIds == null || fromComponentIds.length <= 0)
			for (Integer id : fromInterfaceIds)
				fromList.add(id);
		else
			for (int id : fromInterfaceIds)
				for (int comp : fromComponentIds)
					fromList.add((id << 16) + comp);
		fromKeys = fromList.toArray();

		List<Object> toList = new ArrayList<>();
		if (toComponentIds == null || toComponentIds.length <= 0)
			for (Integer id : toInterfaceIds)
				toList.add(id);
		else
			for (int id : toInterfaceIds)
				for (int comp : toComponentIds)
					toList.add((id << 16) + comp);
		toKeys = toList.toArray();
	}

	public InterfaceOnInterfaceHandler(int[] fromInterfaceIds, int[] fromComponentIds, int[] toInterfaceIds, int[] toComponentIds, Consumer<IFOnIFEvent> handler) {
		this(false, fromInterfaceIds, fromComponentIds, toInterfaceIds, toComponentIds, handler);
	}

	public InterfaceOnInterfaceHandler(boolean bidirectional, int fromInterfaceId, int[] fromComponentIds, int toInterfaceId, int[] toComponentIds, Consumer<IFOnIFEvent> handler) {
		this(bidirectional, new int[] {fromInterfaceId}, fromComponentIds, new int[] {toInterfaceId}, toComponentIds, handler);
	}

	public InterfaceOnInterfaceHandler(boolean bidirectional, int fromInterfaceId, int fromComponentId, int toInterfaceId, int toComponentId, Consumer<IFOnIFEvent> handler) {
		this(bidirectional, fromInterfaceId, new int[] { fromComponentId }, toInterfaceId, new int[] {toComponentId}, handler);
	}

	public InterfaceOnInterfaceHandler(boolean bidirectional, int fromInterfaceId, int toInterfaceId, Consumer<IFOnIFEvent> handler) {
		this(bidirectional, fromInterfaceId, null, toInterfaceId, null, handler);
	}

	public InterfaceOnInterfaceHandler(int fromInterfaceId, int[] fromComponentIds, int toInterfaceId, int[] toComponentIds, Consumer<IFOnIFEvent> handler) {
		this(false, new int[] {fromInterfaceId}, fromComponentIds, new int[] {toInterfaceId}, toComponentIds, handler);
	}

	public InterfaceOnInterfaceHandler(int fromInterfaceId, int fromComponentId, int toInterfaceId, int toComponentId, Consumer<IFOnIFEvent> handler) {
		this(false, fromInterfaceId, new int[] { fromComponentId }, toInterfaceId, new int[] {toComponentId}, handler);
	}

	public InterfaceOnInterfaceHandler(int fromInterfaceId, int toInterfaceId, Consumer<IFOnIFEvent> handler) {
		this(false, fromInterfaceId, null, toInterfaceId, null, handler);
	}

	public Object[] getFromKeys() {
		return fromKeys;
	}

	public Object[] getToKeys() {
		return toKeys;
	}

	public boolean isBidirectional() {
		return bidirectional;
	}
}
