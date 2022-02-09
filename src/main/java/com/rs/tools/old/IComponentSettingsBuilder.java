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
package com.rs.tools.old;

public class IComponentSettingsBuilder {

	/**
	 * Contains the value which should be sended in access mask packet.
	 */
	private int value;

	/**
	 * Set's standart option settings. Great example of standart click option is
	 * the Continue button in dialog interface. If the option is not allowed the
	 * packet won't be send to server.
	 *
	 * @param allowed
	 */
	public void setStandartClickOptionSettings(boolean allowed) {
		value &= ~(0x1);
		if (allowed)
			value |= 0x1;
	}

	/**
	 * Set's right click option settings. Great example of right click option is
	 * the Dismiss option in summoning orb. If specified option is not allowed ,
	 * it will not appear in right click menu and the packet will not be send to
	 * server when clicked.
	 */
	public void setRightClickOptionSettings(int optionID, boolean allowed) {
		if (optionID < 0 || optionID > 9)
			throw new IllegalArgumentException("optionID must be 0-9.");
		value &= ~(0x1 << (optionID + 1)); // disable
		if (allowed)
			value |= (0x1 << (optionID + 1));
	}

	/**
	 * Sets use on settings. By use on , I mean the options such as Cast in
	 * spellbook or use in inventory. If nothing is allowed then 'use' option
	 * will not appear in right click menu.
	 */
	public void setUseOnSettings(boolean canUseOnGroundItems, boolean canUseOnNpcs, boolean canUseOnObjects, boolean canUseOnNonselfPlayers, boolean canUseOnSelfPlayer, boolean canUseOnInterfaceComponent) {
		int useFlag = 0;
		if (canUseOnGroundItems)
			useFlag |= 0x1;
		if (canUseOnNpcs)
			useFlag |= 0x2;
		if (canUseOnObjects)
			useFlag |= 0x4;
		if (canUseOnNonselfPlayers)
			useFlag |= 0x8;
		if (canUseOnSelfPlayer)
			useFlag |= 0x10;
		if (canUseOnInterfaceComponent)
			useFlag |= 0x20;
		value &= ~(127 << 7); // disable
		value |= useFlag << 7;
	}

	/**
	 * Set's interface events depth. For example, we have inventory interface
	 * which is opened on gameframe interface (548 or 746). If depth is 1 , then
	 * the clicks in inventory will also invoke click event handler scripts on
	 * gameframe interface.
	 */
	public void setInterfaceEventsDepth(int depth) {
		if (depth < 0 || depth > 7)
			throw new IllegalArgumentException("depth must be 0-7.");
		value &= ~(0x7 << 18);
		value |= (depth << 18);
	}

	/**
	 * Set's canUseOnFlag. if it's true then other interface components can do
	 * use on this interface component. Example would be using High alchemy
	 * spell on the inventory item. If inventory component where items are
	 * stored doesn't allow the canUseOn , it would not be possible to use High
	 * Alchemy on that item.
	 */
	public void setCanUseOn(boolean allow) {
		value &= ~(1 << 22);
		if (allow)
			value |= (1 << 22);
	}

	public int getValue() {
		return value;
	}

}
