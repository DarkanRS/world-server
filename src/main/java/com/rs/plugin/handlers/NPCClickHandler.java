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

import com.rs.plugin.events.NPCClickEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class NPCClickHandler extends PluginHandler<NPCClickEvent> {
	private boolean checkDistance = true;
	private Set<String> options;

	public NPCClickHandler(boolean checkDistance, Object[] namesOrIds, String[] options) {
		super(namesOrIds == null ? new Object[] { null } : namesOrIds);
		if (options != null && options.length > 0)
			this.options = new HashSet<>(Arrays.asList(options));
		this.checkDistance = checkDistance;
	}

	public NPCClickHandler(Object[] namesOrIds, String[] options) {
		this(true, namesOrIds, options);
	}

	public NPCClickHandler(boolean checkDistance, Object[] namesOrIds) {
		this(checkDistance, namesOrIds, null);
	}

	public NPCClickHandler(Object[] namesOrIds) {
		this(true, namesOrIds, null);
	}

	public boolean isCheckDistance() {
		return checkDistance;
	}

	public Set<String> getOptions() {
		return options;
	}
	public boolean containsOption(String option) {
		return (options == null || options.size() == 0) ? true : options.contains(option);
	}
}
