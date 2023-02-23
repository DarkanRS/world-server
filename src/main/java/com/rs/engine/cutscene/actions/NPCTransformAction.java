package com.rs.engine.cutscene.actions;

import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

import java.util.Map;

public class NPCTransformAction extends CutsceneAction {

	private int id;

	public NPCTransformAction(String key, int id, int actionDelay) {
		super(key, actionDelay);
		this.id = id;
	}

	@Override
	public void process(Player player, Map<String, Object> objects) {
		NPC npc = (NPC) objects.get(getObjectKey());
		npc.transformIntoNPC(id);
	}

}
