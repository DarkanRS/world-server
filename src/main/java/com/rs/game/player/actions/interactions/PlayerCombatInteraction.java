package com.rs.game.player.actions.interactions;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.lib.game.WorldTile;

public class PlayerCombatInteraction extends EntityInteractionAction {

	public PlayerCombatInteraction(Player player, Entity target) {
		super(target, new PlayerCombat(target), PlayerCombat.getAttackRange(player));
	}

	@Override
	public boolean canStart(Player player) {
		player.setNextFaceWorldTile(new WorldTile(target.getCoordFaceX(target.getSize()), target.getCoordFaceY(target.getSize()), target.getPlane()));
		return true;
	}

	@Override
	public boolean checkAll(Player player) {
		setDistance(PlayerCombat.getAttackRange(player));
		player.setNextFaceEntity(target);
		return true;
	}
}
