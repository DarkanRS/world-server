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
package com.rs.game.player.content.skills.magic;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.lib.Constants;

public enum Spell {
	HOME_TELEPORT(-1, 0.0, new RuneSet()),

	;

	private static Map<Integer, Map<Integer, Spell>> SPELL_MAP = new HashMap<>();

	static {
		Map<Integer, Spell> MODERN = new HashMap<>();
		Map<Integer, Spell> DUNG = new HashMap<>();
		Map<Integer, Spell> LUNAR = new HashMap<>();
		Map<Integer, Spell> ANCIENT = new HashMap<>();

		SPELL_MAP.put(192, MODERN);
		SPELL_MAP.put(193, ANCIENT);
		SPELL_MAP.put(430, LUNAR);
		SPELL_MAP.put(950, DUNG);
	}

	public static Spell forId(int spellBook, int spellId) {
		return SPELL_MAP.get(spellBook).get(spellId);
	}

	protected int req;
	protected double xp;
	protected RuneSet runes;

	private Spell(int req, double xp, RuneSet runes) {
		this.req = req;
		this.xp = xp;
		this.runes = runes;
	}

	public int getReq() {
		return req;
	}

	public double getXP() {
		return xp;
	}

	public RuneSet getRuneSet() {
		return runes;
	}

	public final void cast(Entity caster, Object target) {

	}

	public boolean extraReqs(Player player) {
		return true;
	}

	public final boolean canCast(Player player) {
		if (player.getSkills().getLevel(Constants.MAGIC) < req) {
			player.sendMessage("You need a magic level of " + req + " to cast this spell.");
			return false;
		}
		if (!extraReqs(player))
			return false;
		return runes.meetsRequirements(player);
	}
}
