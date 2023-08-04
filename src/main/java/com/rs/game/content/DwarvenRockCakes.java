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
package com.rs.game.content;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class DwarvenRockCakes {

	public static ItemClickHandler rockCakeClick = new ItemClickHandler(new Object[] { 7509, 7510 }, new String[] { "Eat" }, e -> {
		if (e.getItem().getId() == 7509 && e.getPlayer().getHitpoints() > 20) {
			Hit h = new Hit(20, HitLook.TRUE_DAMAGE);
			e.getPlayer().removeHitpoints(h);
			e.getPlayer().fakeHit(h);
			e.getPlayer().setNextForceTalk(new ForceTalk("Ow! Ow! That's hot!"));
		} else if (e.getItem().getId() == 7510 && e.getPlayer().getHitpoints() > 100) {
			Hit h = new Hit(100, HitLook.TRUE_DAMAGE);
			e.getPlayer().removeHitpoints(h);
			e.getPlayer().fakeHit(h);
			e.getPlayer().setNextForceTalk(new ForceTalk("Ow! I nearly broke a tooth!"));
		}
	});

	public static NPCClickHandler clickRohak = new NPCClickHandler(new Object[] { "Rohak" }, e -> {
		e.getPlayer().startConversation(new Dialogue().addOptions(new Options() {
			@Override
			public void create() {
				option("Hot Dwarven Rock Cake", () -> e.getPlayer().getInventory().addItemDrop(7509, 1));
				option("Cool Dwarven Rock Cake", () -> e.getPlayer().getInventory().addItemDrop(7510, 1));
			}
		}));
	});
}
