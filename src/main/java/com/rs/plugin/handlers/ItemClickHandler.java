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
