package com.rs.game.npc.pestqueen.attack;

import com.rs.game.Entity;
import com.rs.game.npc.pestqueen.PestQueen;
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
