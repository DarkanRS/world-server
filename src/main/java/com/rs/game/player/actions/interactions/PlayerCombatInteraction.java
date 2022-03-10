package com.rs.game.player.actions.interactions;

import com.rs.game.Entity;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;

public class PlayerCombatInteraction extends EntityInteractionAction {

	public PlayerCombatInteraction(Player player, Entity target) {
		super(target, new PlayerCombat(target), PlayerCombat.getAttackRange(player));
	}

	@Override
	public boolean canStart(Player player) {
		return true;
	}

	@Override
	public boolean checkAll(Player player) {
		player.setNextFaceEntity(target);
		return true;
	}
}
