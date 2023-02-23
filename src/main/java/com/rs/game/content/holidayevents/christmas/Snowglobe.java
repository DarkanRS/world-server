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
package com.rs.game.content.holidayevents.christmas;

import com.rs.engine.dialogue.Dialogue;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class Snowglobe {

	public static ItemClickHandler handle = new ItemClickHandler(new Object[] { 11949 }, e -> {
		e.getPlayer().setNextAnimation(new Animation(2926));
		e.getPlayer().startConversation(new Dialogue().addNext(new Dialogue(() -> {
			e.getPlayer().getInterfaceManager().sendInterface(659);
			e.getPlayer().setCloseInterfacesEvent(() -> {
				e.getPlayer().setNextAnimation(new Animation(7538));
			});
		})).addNext(new Dialogue(() -> {
			e.getPlayer().closeInterfaces();
			e.getPlayer().setNextAnimation(new Animation(7528));
			e.getPlayer().setNextSpotAnim(new SpotAnim(1284));
			e.getPlayer().getInventory().addItem(11951, e.getPlayer().getInventory().getFreeSlots());
		})));
	});
}
