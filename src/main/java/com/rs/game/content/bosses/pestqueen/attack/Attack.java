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
package com.rs.game.content.bosses.pestqueen.attack;

import com.rs.game.content.bosses.pestqueen.PestQueen;
import com.rs.game.model.entity.Entity;
import com.rs.lib.game.Animation;

public interface Attack {

	/**
	 * Process the attack.
	 *
	 * @param queen
	 *            The {@code PestQueen} instance.
	 * @param target
	 *            The {@code Entity} instance.
	 */
	public void processAttack(PestQueen queen, Entity target);

	public Animation getAttackAnimation();

	public int getMaxHit();

	public boolean canAttack(PestQueen queen, Entity target);
}
