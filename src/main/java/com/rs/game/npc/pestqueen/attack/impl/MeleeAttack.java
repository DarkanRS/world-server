package com.rs.game.npc.pestqueen.attack.impl;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.pestqueen.PestQueen;
import com.rs.game.npc.pestqueen.attack.Attack;
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
