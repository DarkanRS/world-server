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
package com.rs.game.model.entity.interactions;

import com.rs.game.model.entity.Entity;

public class StandardEntityInteraction extends EntityInteraction {

	private final Runnable onReached;

	public StandardEntityInteraction(Entity target, int distance, Runnable onReached) {
		super(target, distance);
		this.onReached = onReached;
	}

	@Override
	public boolean canStart(Entity player) {
		return true;
	}

	@Override
	public boolean checkAll(Entity player) {
		return true;
	}

	@Override
	public void interact(Entity player) {
		onReached.run();
	}

	@Override
	public void onStop(Entity player) {

	}

}