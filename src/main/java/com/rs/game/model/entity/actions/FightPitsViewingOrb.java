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
package com.rs.game.model.entity.actions;

import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.lib.game.WorldTile;

public class FightPitsViewingOrb extends PlayerAction {

	public static final WorldTile[] ORB_TELEPORTS = { WorldTile.of(4571, 5092, 0), WorldTile.of(4571, 5107, 0), WorldTile.of(4590, 5092, 0), WorldTile.of(4571, 5077, 0), WorldTile.of(4557, 5092, 0) };

	private WorldTile tile;

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		tile = WorldTile.of(player.getTile());
		player.getAppearance().switchHidden();
		player.getPackets().setBlockMinimapState(5);
		player.setNextWorldTile(ORB_TELEPORTS[0]);
		player.getInterfaceManager().sendInventoryInterface(374);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getPoison().isPoisoned()) {
			player.sendMessage("You can't use orb while you're poisoned.");
			return false;
		}
		if (player.getFamiliar() != null) {
			player.sendMessage("You can't use orb with a familiar.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.lock(2);
		player.getInterfaceManager().removeInventoryInterface();
		player.getAppearance().switchHidden();
		player.getPackets().setBlockMinimapState(0);
		player.setNextWorldTile(tile);
	}

}
