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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.plugin.handlers;

import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.plugin.events.NPCInteractionDistanceEvent;
import com.rs.plugin.events.ObjectInteractionDistanceEvent;

public abstract class ObjectInteractionDistanceHandler extends PluginHandler<ObjectInteractionDistanceEvent> {

	public ObjectInteractionDistanceHandler(Object... keys) {
		super(keys);
	}

	public abstract int getDistance(Player player, GameObject obj);

	@Override
	public final void handle(ObjectInteractionDistanceEvent e) { }

	@Override
	public final boolean handleGlobal(ObjectInteractionDistanceEvent e) { return false; }

	@Override
	public final Object getObj(ObjectInteractionDistanceEvent e) {
		return getDistance(e.getPlayer(), e.getObj());
	}

}
