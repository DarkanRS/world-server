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

import com.rs.plugin.events.ButtonClickEvent;

import java.util.function.Consumer;

public class ButtonClickHandler extends PluginHandler<ButtonClickEvent> {
	public ButtonClickHandler(Object[] interfaceIds, Consumer<ButtonClickEvent> handler) {
		super(interfaceIds, handler);
	}
	
	public ButtonClickHandler(Object interfaceId, Consumer<ButtonClickEvent> handler) {
		super(new Object[] { interfaceId }, handler);
	}
}
