package com.rs.game.player.managers;

import com.rs.game.player.Player;
import com.rs.game.player.actions.interactions.Interaction;

public final class InteractionManager {

	private Player player;
	private Interaction interaction;

	public InteractionManager(Player player) {
		this.player = player;
	}

	public void process() {
		if (interaction != null && (interaction.isStopped() || player.isDead() || !interaction.process(player)))
			forceStop();
	}

	public boolean setInteraction(Interaction skill) {
		forceStop();
		if (!skill.start(player))
			return false;
		this.interaction = skill;
		return true;
	}

	public void forceStop() {
		if (interaction == null)
			return;
		player.setNextFaceEntity(null);
		interaction.stop(player);
		interaction = null;
	}
	
	public Interaction getInteraction() {
		return interaction;
	}
}
