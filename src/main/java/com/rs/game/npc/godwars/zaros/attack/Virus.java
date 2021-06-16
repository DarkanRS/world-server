package com.rs.game.npc.godwars.zaros.attack;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class Virus implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		nex.setNextForceTalk(new ForceTalk("Let the virus flow through you."));
		nex.playSound(3296, 2);
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
