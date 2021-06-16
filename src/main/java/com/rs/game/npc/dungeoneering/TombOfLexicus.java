package com.rs.game.npc.dungeoneering;

import com.rs.game.Entity;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.lib.game.WorldTile;

public class TombOfLexicus extends DungeonNPC {

	private LexicusRunewright boss;

	public TombOfLexicus(LexicusRunewright boss, int id, WorldTile tile, DungeonManager manager) {
		super(id, tile, manager);
		setForceAgressive(true);
		this.boss = boss;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void drop() {

	}

	@Override
	public int getMaxHitpoints() {
		return 10;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		boss.removeBook(this);
	}
}
