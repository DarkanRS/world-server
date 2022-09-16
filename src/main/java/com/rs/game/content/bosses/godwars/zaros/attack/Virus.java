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
package com.rs.game.content.bosses.godwars.zaros.attack;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.content.bosses.godwars.zaros.Nex;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class Virus implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Let the virus flow through you."));
		nex.voiceEffect(3296);
		nex.setNextAnimation(new Animation(6987));
		sendVirus(nex, new ArrayList<Entity>(), nex.getPossibleTargets(), target);
		return nex.getAttackSpeed();
	}

	public void sendVirus(Nex nex, List<Entity> hitedEntitys, List<Entity> possibleTargets, Entity infected) {
		for (Entity t : possibleTargets) {
			if (hitedEntitys.contains(t))
				continue;
			if (Utils.getDistance(t.getX(), t.getY(), infected.getX(), infected.getY()) <= 1) {
				t.setNextForceTalk(new ForceTalk("*Cough*"));
				t.applyHit(new Hit(nex, Utils.getRandomInclusive(100), HitLook.TRUE_DAMAGE));
				hitedEntitys.add(t);
				sendVirus(nex, hitedEntitys, possibleTargets, infected);
			}
		}
	}

}
