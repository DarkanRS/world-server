package com.rs.game.player.cutscenes.actions;

import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.SpotAnim;

public class NPCGraphicAction extends CutsceneAction {

	private SpotAnim gfx;

	public NPCGraphicAction(int cachedObjectIndex, SpotAnim gfx, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.gfx = gfx;
	}

	@Override
	public void process(Player player, Object[] cache) {
		NPC npc = (NPC) cache[getCachedObjectIndex()];
		npc.setNextSpotAnim(gfx);
	}

}
