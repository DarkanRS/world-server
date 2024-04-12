package com.rs.game.model.entity.interactions;

import com.rs.game.content.combat.PlayerCombat;
import com.rs.game.content.combat.PlayerCombatKt;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.interactions.PlayerEntityInteractionAction;

public class PlayerCombatInteraction extends PlayerEntityInteractionAction<PlayerCombat> {

	public PlayerCombatInteraction(Player player, Entity target) {
		super(target, new PlayerCombat(target), PlayerCombatKt.getAttackRange(player));
	}

	@Override
	public boolean canStart(Player player) {
		player.faceEntity(target);
		return true;
	}

	@Override
	public boolean checkAll(Player player) {
		if (target.isDead() || (target instanceof NPC n && n.isCantInteract()))
			return false;
		setDistance(PlayerCombatKt.getAttackRange(player));
		player.faceEntity(target);
		return getAction().checkAll(player);
	}
}
