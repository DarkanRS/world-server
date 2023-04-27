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
package com.rs.game.model.entity;

import com.rs.game.model.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public final class Hit {

	public static enum HitLook {
		MISSED(8),
		TRUE_DAMAGE(3),
		MELEE_DAMAGE(0),
		RANGE_DAMAGE(1),
		MAGIC_DAMAGE(2),
		REFLECTED_DAMAGE(4),
		ABSORB_DAMAGE(5),
		POISON_DAMAGE(6),
		DESEASE_DAMAGE(7),
		HEALED_DAMAGE(9),
		CANNON_DAMAGE(13);

		private int mark;

		private HitLook(int mark) {
			this.mark = mark;
		}

		public int getMark() {
			return mark;
		}
	}

	private Entity source;
	private HitLook look;

	private int maxHit;
	private int damage;
	private boolean critical;
	private Hit soaking;
	private int delay;
	private Map<String, Object> data = new HashMap<>();

	public void setCriticalMark() {
		critical = true;
	}

	public void setHealHit() {
		look = HitLook.HEALED_DAMAGE;
		critical = false;
	}

	public Hit(int damage, HitLook look) {
		this(null, damage, look, 0);
	}

	public Hit(Entity source, int damage, HitLook look) {
		this(source, damage, look, 0);
	}

	public Hit(Entity source, int damage, HitLook look, int delay) {
		this.source = source;
		this.damage = damage;
		this.look = look;
		this.delay = delay;
	}

	public boolean missed() {
		return damage == 0;
	}

	public boolean interactingWith(Player player, Entity victm) {
		return player == victm || player == source;
	}

	public int getMark(Player player, Entity victm) {
		if (HitLook.HEALED_DAMAGE == look)
			return look.getMark();
		if (damage == 0)
			return HitLook.MISSED.getMark();
		int mark = look.getMark();
		if (critical)
			mark += 10;
		if (!interactingWith(player, victm))
			mark += 14;
		return mark;
	}

	public Hit setMaxHit(int maxHit) {
		this.maxHit = maxHit;
		return this;
	}

	public HitLook getLook() {
		return look;
	}

	public int getDamage() {
		return damage;
	}

	public Hit setDamage(int damage) {
		this.damage = damage;
		return this;
	}

	public Entity getSource() {
		return source;
	}

	public Hit setSource(Entity source) {
		this.source = source;
		return this;
	}

	public Hit setLook(HitLook look) {
		this.look = look;
		return this;
	}

	public boolean isCriticalHit() {
		return critical;
	}

	public Hit getSoaking() {
		return soaking;
	}

	public void setSoaking(Hit soaking) {
		this.soaking = soaking;
	}

	public int getDelay() {
		return delay;
	}

	public int getMaxHit() {
		return maxHit;
	}

	public Hit setData(String key, Object obj) {
		data.put(key, obj);
		return this;
	}

	public static Hit miss(Player source) {
		return new Hit(source, 0, HitLook.MISSED);
	}

	public static Hit melee(Player source, int damage) {
		return new Hit(source, damage, HitLook.MELEE_DAMAGE);
	}

	public static Hit range(Player source, int damage) {
		return new Hit(source, damage, HitLook.RANGE_DAMAGE);
	}

	public static Hit magic(Player source, int damage) {
		return new Hit(source, damage, HitLook.MAGIC_DAMAGE);
	}

	public static Hit flat(Player source, int damage) {
		return new Hit(source, damage, HitLook.TRUE_DAMAGE);
	}

	public <T> T getData(String key, Class<T> clazz) {
		try {
			if (data.containsKey(key))
				return clazz.cast(data.get(key));
		} catch(ClassCastException e) {
			return null;
		}
		return null;
	}

	public Object getData(String key) {
		if (data.containsKey(key))
			return data.get(key);
		return null;
	}
}
