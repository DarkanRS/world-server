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