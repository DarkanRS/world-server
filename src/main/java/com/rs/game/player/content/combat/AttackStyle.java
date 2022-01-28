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
package com.rs.game.player.content.combat;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;

public class AttackStyle {

	private static Map<Integer, AttackStyle[]> ATTACK_STYLES = new HashMap<>();
	private static AttackStyle[] UNARMED = {
			new AttackStyle(0, "Punch", XPType.ACCURATE, AttackType.CRUSH),
			new AttackStyle(1, "Kick", XPType.AGGRESSIVE, AttackType.CRUSH),
			new AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
	};

	static {
		ATTACK_STYLES.put(1, new AttackStyle[] {
				new AttackStyle(0, "Bash", XPType.ACCURATE, AttackType.CRUSH),
				new AttackStyle(1, "Pound", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(2, "Focus", XPType.DEFENSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(2, new AttackStyle[] {
				new AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
				new AttackStyle(1, "Hack", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(2, "Smash", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
		});
		ATTACK_STYLES.put(3, new AttackStyle[] {
				new AttackStyle(0, "Bash", XPType.ACCURATE, AttackType.CRUSH),
				new AttackStyle(1, "Pound", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(4, new AttackStyle[] {
				new AttackStyle(0, "Spike", XPType.ACCURATE, AttackType.STAB),
				new AttackStyle(1, "Impale", XPType.AGGRESSIVE, AttackType.STAB),
				new AttackStyle(2, "Smash", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
		});
		ATTACK_STYLES.put(5, new AttackStyle[] {
				new AttackStyle(0, "Stab", XPType.ACCURATE, AttackType.STAB),
				new AttackStyle(1, "Lunge", XPType.AGGRESSIVE, AttackType.STAB),
				new AttackStyle(2, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
		});
		ATTACK_STYLES.put(6, new AttackStyle[] {
				new AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
				new AttackStyle(1, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(2, "Lunge", XPType.CONTROLLED, AttackType.STAB),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
		});
		ATTACK_STYLES.put(7, new AttackStyle[] {
				new AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
				new AttackStyle(1, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(2, "Smash", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
		});
		ATTACK_STYLES.put(8, new AttackStyle[] {
				new AttackStyle(0, "Pound", XPType.ACCURATE, AttackType.CRUSH),
				new AttackStyle(1, "Pummel", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(2, "Spike", XPType.CONTROLLED, AttackType.STAB),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(9, new AttackStyle[] {
				new AttackStyle(0, "Chop", XPType.ACCURATE, AttackType.SLASH),
				new AttackStyle(1, "Slash", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(2, "Lunge", XPType.CONTROLLED, AttackType.STAB),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.SLASH)
		});
		ATTACK_STYLES.put(10, new AttackStyle[] {
				new AttackStyle(0, "Pound", XPType.ACCURATE, AttackType.CRUSH),
				new AttackStyle(1, "Pummel", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(11, new AttackStyle[] {
				new AttackStyle(0, "Flick", XPType.ACCURATE, AttackType.SLASH),
				new AttackStyle(1, "Lash", XPType.CONTROLLED, AttackType.SLASH),
				new AttackStyle(2, "Deflect", XPType.DEFENSIVE, AttackType.SLASH)
		});
		ATTACK_STYLES.put(12, new AttackStyle[] {
				new AttackStyle(0, "Pound", XPType.ACCURATE, AttackType.CRUSH),
				new AttackStyle(1, "Pummel", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(2, "Block", XPType.DEFENSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(13, new AttackStyle[] {
				new AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
				new AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
		});
		ATTACK_STYLES.put(14, new AttackStyle[] {
				new AttackStyle(0, "Lunge", XPType.CONTROLLED, AttackType.STAB),
				new AttackStyle(1, "Swipe", XPType.CONTROLLED, AttackType.SLASH),
				new AttackStyle(2, "Pound", XPType.CONTROLLED, AttackType.CRUSH),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
		});
		ATTACK_STYLES.put(15, new AttackStyle[] {
				new AttackStyle(0, "Jab", XPType.CONTROLLED, AttackType.STAB),
				new AttackStyle(1, "Swipe", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(2, "Fend", XPType.DEFENSIVE, AttackType.STAB)
		});
		ATTACK_STYLES.put(16, new AttackStyle[] {
				new AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
				new AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
		});
		ATTACK_STYLES.put(17, new AttackStyle[] {
				new AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
				new AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
		});
		ATTACK_STYLES.put(18, new AttackStyle[] {
				new AttackStyle(0, "Accurate", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(1, "Rapid", XPType.RANGED, AttackType.RAPID),
				new AttackStyle(2, "Long range", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
		});
		ATTACK_STYLES.put(19, new AttackStyle[] {
				new AttackStyle(0, "Short fuse", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(1, "Medium fuse", XPType.RANGED, AttackType.RAPID),
				new AttackStyle(2, "Long fuse", XPType.RANGED, AttackType.LONG_RANGE)
		});
		ATTACK_STYLES.put(20, new AttackStyle[] {
				new AttackStyle(0, "Aim and fire", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(1, "Kick", XPType.AGGRESSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(21, new AttackStyle[] {
				new AttackStyle(0, "Scorch", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(1, "Flare", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(2, "Blaze", XPType.MAGIC, AttackType.MAGIC)
		});
		ATTACK_STYLES.put(22, new AttackStyle[] {
				new AttackStyle(0, "Reap", XPType.ACCURATE, AttackType.SLASH),
				new AttackStyle(1, "Chop", XPType.AGGRESSIVE, AttackType.STAB),
				new AttackStyle(2, "Jab", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(3, "Block", XPType.DEFENSIVE, AttackType.STAB)
		});
		ATTACK_STYLES.put(23, new AttackStyle[] {
				new AttackStyle(0, "Slash", XPType.ACCURATE, AttackType.SLASH),
				new AttackStyle(1, "Crush", XPType.AGGRESSIVE, AttackType.CRUSH),
				new AttackStyle(2, "Slash", XPType.DEFENSIVE, AttackType.SLASH)
		});
		ATTACK_STYLES.put(24, new AttackStyle[] {
				new AttackStyle(0, "Sling", XPType.RANGED, AttackType.ACCURATE),
				new AttackStyle(1, "Chuck", XPType.RANGED, AttackType.RAPID),
				new AttackStyle(2, "Lob", XPType.RANGED_DEFENSIVE, AttackType.LONG_RANGE)
		});
		ATTACK_STYLES.put(25, new AttackStyle[] {
				new AttackStyle(0, "Jab", XPType.ACCURATE, AttackType.STAB),
				new AttackStyle(1, "Swipe", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(2, "Fend", XPType.DEFENSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(26, new AttackStyle[] {
				new AttackStyle(0, "Jab", XPType.ACCURATE, AttackType.STAB),
				new AttackStyle(1, "Swipe", XPType.AGGRESSIVE, AttackType.SLASH),
				new AttackStyle(2, "Fend", XPType.DEFENSIVE, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(27, new AttackStyle[] {
				new AttackStyle(0, "Hack!", XPType.CONTROLLED, AttackType.SLASH),
				new AttackStyle(1, "Gouge!", XPType.CONTROLLED, AttackType.STAB),
				new AttackStyle(2, "Smash!", XPType.CONTROLLED, AttackType.CRUSH)
		});
		ATTACK_STYLES.put(28, new AttackStyle[] {
				new AttackStyle(0, "Accurate", XPType.MAGIC, AttackType.POLYPORE_ACCURATE),
				new AttackStyle(1, "Long range", XPType.MAGIC, AttackType.POLYPORE_LONGRANGE)
		});
	}

	private int index;
	private String name;
	private AttackType attackType;
	private XPType xpType;

	public AttackStyle(int index, String name, XPType xpType, AttackType attackType) {
		this.index = index;
		this.name = name;
		this.xpType = xpType;
		this.attackType = attackType;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public XPType getXpType() {
		return xpType;
	}

	public static AttackStyle[] getStyles(int itemId) {
		ItemDefinitions defs = ItemDefinitions.getDefs(itemId);
		int weaponType = defs.getParamVal(686);
		return ATTACK_STYLES.get(weaponType) == null ? UNARMED : ATTACK_STYLES.get(weaponType);
	}
}
