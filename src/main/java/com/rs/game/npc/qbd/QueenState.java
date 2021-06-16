package com.rs.game.npc.qbd;

/**
 * Represents the Queen Black Dragon's states.
 * 
 * @author Emperor
 * 
 */
public enum QueenState {

	/**
	 * The queen is asleep.
	 */
	SLEEPING(15509, null),

	/**
	 * The default, attackable Qeeun Black Dragon.
	 */
	DEFAULT(15454, null),

	/**
	 * The crystal armour state (weak to physical attacks, strong against
	 * magic).
	 */
	CRYSTAL_ARMOUR(15506, "<col=66FFFF>The Queen Black Dragon takes the consistency of crystal; she is more resistant to</col>(nl)<col=66FFFF>magic, but weaker to physical damage.</col>"),

	/**
	 * The hardened state (weak to magic attacks, strong against melee/range).
	 */
	HARDEN(15507, "<col=669900>The Queen Black Dragon hardens her carapace; she is more resistant to physical</col>(nl)<col=669900>damage, but more vulerable to magic.</col>");

	/**
	 * The NPC id.
	 */
	private final int npcId;

	/**
	 * The message to be send to the player.
	 */
	private final String message;

	/**
	 * Constructs a new {@code QueenState} {@code Object}.
	 * 
	 * @param npcId
	 *            The NPC id.
	 * @param message
	 *            The message to send.
	 */
	private QueenState(int npcId, String message) {
		this.npcId = npcId;
		this.message = message;
	}

	/**
	 * Gets the npcId.
	 * 
	 * @return The npcId.
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Gets the message.
	 * 
	 * @return The message.
	 */
	public String getMessage() {
		return message;
	}
}