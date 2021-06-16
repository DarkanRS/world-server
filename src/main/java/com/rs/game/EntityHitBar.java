package com.rs.game;

import com.rs.game.player.Player;

public class EntityHitBar extends HitBar {

	public EntityHitBar(Entity entity) {
		this.entity = entity;
	}

	private Entity entity;

	@Override
	public int getPercentage() {
		int hp = entity.getHitpoints();
		int maxHp = entity.getMaxHitpoints();
		if (hp > maxHp)
			hp = maxHp;
		return maxHp == 0 ? 0 : (hp * 255 / maxHp);
	}

	@Override
	public int getType() {
		int size = entity.getSize();
		return size >= 5 ? 3 : size >= 3 ? 4 : 0;
	}

	@Override
	public boolean display(Player player) {
		return true;
	}

}
