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
package com.rs.game.model.entity.npc.pestqueen.attack.impl;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.pestqueen.PestQueen;
import com.rs.game.model.entity.npc.pestqueen.attack.Attack;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class MeleeAttack implements Attack {

	@Override
	public void processAttack(PestQueen queen, Entity target) {
		queen.setNextFaceEntity(target);
		queen.setNextAnimation(getAttackAnimation());
		int hit = Utils.random(0, getMaxHit());// TODO make the combat rolls
		// actually accurate using npc
		// bonuses
		target.applyHit(new Hit(queen, hit, hit == 0 ? HitLook.MISSED : HitLook.MELEE_DAMAGE));
	}

	@Override
	public Animation getAttackAnimation() {
		return new Animation(14801);
	}

	@Override
	public int getMaxHit() {
		return 200;
	}

	@Override
	public boolean canAttack(PestQueen queen, Entity target) {
		return true;
	}
}
