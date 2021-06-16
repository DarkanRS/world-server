package com.rs.game.player.cutscenes.actions;

import com.rs.game.player.Player;
import com.rs.lib.game.SpotAnim;

public class PlayerGraphicAction extends CutsceneAction {

	private SpotAnim gfx;

	public PlayerGraphicAction(SpotAnim gfx, int actionDelay) {
		super(-1, actionDelay);
		this.gfx = gfx;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.setNextSpotAnim(gfx);
	}

}
