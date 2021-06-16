package com.rs.game.player.cutscenes.actions;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.Cutscene;

public class ConstructMapAction extends CutsceneAction {

	private int baseChunkX, baseChunkY, widthChunks, heightChunks;

	public ConstructMapAction(int baseChunkX, int baseChunkY, int widthChunks, int heightChunks) {
		super(-1, -1);
		this.baseChunkX = baseChunkX;
		this.baseChunkY = baseChunkY;
		this.widthChunks = widthChunks;
		this.heightChunks = heightChunks;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		scene.constructArea(player, baseChunkX, baseChunkY, widthChunks, heightChunks);
	}
}
