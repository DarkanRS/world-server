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
package com.rs.game.content.world.areas.fisher_realm;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class FisherRealm {
	//Fisher Realm
	public static ObjectClickHandler handlefisherkingstairs = new ObjectClickHandler(new Object[] { 1730, 1731 }, e -> {
		if (e.getObjectId() == 1730)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? -0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? 4 : e.getObject().getRotation() == 3 ? -0 : 0, 1));
		else if (e.getObjectId() == 1731)
			e.getPlayer().setNextTile(e.getPlayer().transform(e.getObject().getRotation() == 0 ? 0 : e.getObject().getRotation() == 3 ? -0 : 0, e.getObject().getRotation() == 0 ? -4 : e.getObject().getRotation() == 3 ? 3 : 0, -1));

	});

}
