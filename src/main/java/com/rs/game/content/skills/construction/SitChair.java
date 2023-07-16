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
package com.rs.game.content.skills.construction;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;

public class SitChair extends PlayerAction {

	private Tile originalTile;
	private Tile chairTile;
	private boolean tped;
	private int animation;

	public SitChair(Player player, GameObject object) {
		animation = HouseConstants.getSitAnimation(object.getId());
		originalTile = Tile.of(player.getTile());
		chairTile = object.getTile();
		Tile face = Tile.of(player.getTile());
		if (object.getType() == ObjectType.SCENERY_INTERACT) {
			if (object.getRotation() == 0)
				face = face.transform(0, -1, 0);
			else if (object.getRotation() == 2)
				face = face.transform(0, 1, 0);
			else if (object.getRotation() == 3)
				face = face.transform(1, 0, 0);
			else if (object.getRotation() == 1)
				face = face.transform(-1, 0, 0);
		} else if (object.getType() == ObjectType.GROUND_INTERACT)
			if (object.getRotation() == 1)
				face = face.transform(-1, 1, 0);
			else if (object.getRotation() == 2)
				face = face.transform(1, 1, 0);
			else if (object.getRotation() == 0)
				face = face.transform(-1, -1, 0);
			else if (object.getRotation() == 3)
				face = face.transform(1, -1, 0);
		player.setNextFaceTile(face);
	}

	@Override
	public boolean start(Player player) {
		setActionDelay(player, 1);
		return true;
	}

	@Override
	public boolean process(Player player) {
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!tped) {
			player.setNextTile(chairTile);
			tped = true;
		}
		player.setNextAnimation(new Animation(animation));
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.lock(1);
		player.setNextTile(originalTile);
		player.setNextAnimation(new Animation(-1));
	}
}