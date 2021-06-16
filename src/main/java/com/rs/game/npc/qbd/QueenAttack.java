package com.rs.game.npc.qbd;

import com.rs.game.player.Player;

/**
 * Represents an attack from the Queen Black Dragon.
 * 
 * @author Emperor
 * 
 */
public interface QueenAttack {

	/**
	 * Starts the attack.
	 * 
	 * @param npc
	 *            The NPC.
	 * @param victim
	 *            The victim.
	 * @return The next attack value.
	 */
	int attack(QueenBlackDragon npc, Player victim);

	/**
	 * Checks if the QBD can use this attack.
	 * 
	 * @param npc
	 *            The QBD.
	 * @param victim
	 *            The player.
	 * @return {@code True} if so.
	 */
	boolean canAttack(QueenBlackDragon npc, Player victim);

}