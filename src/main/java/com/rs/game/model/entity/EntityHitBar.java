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
package com.rs.game.model.entity;

import com.rs.game.model.entity.player.Player;

public class EntityHitBar extends HitBar {

	public EntityHitBar(Entity entity) {
		this.entity = entity;
	}

	private Entity entity;

	@Override
	public int getPercentage() {
		int hp = entity.getHitpoints();
		int maxHp = entity.getMaxHitpoints();
		if (hp > maxHp)
			hp = maxHp;
		return maxHp == 0 ? 0 : (int) ((long)hp * 255 / maxHp);
	}

	@Override
	public int getType() {
		int size = entity.getSize();
		return size >= 5 ? 3 : size >= 3 ? 4 : 0;
	}

	@Override
	public boolean display(Player player) {
		return true;
	}

}
