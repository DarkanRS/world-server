package com.rs.game.npc.dungeoneering;

import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.impl.dung.ToKashBloodChillerCombat;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;

public class FrozenAdventurer extends NPC {

	private transient Player player;

	public FrozenAdventurer(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, true);
	}

	@Override
	public void processNPC() {
		if (player == null || player.isDead() || player.hasFinished()) {
			finish();
			return;
		} else if (!player.getAppearance().isNPC()) {
			ToKashBloodChillerCombat.removeSpecialFreeze(player);
			finish();
			return;
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getFrozenPlayer() {
		return player;
	}

}
