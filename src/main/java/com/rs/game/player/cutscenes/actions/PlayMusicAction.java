package com.rs.game.player.cutscenes.actions;

import com.rs.game.player.Player;

public class PlayMusicAction extends CutsceneAction {

	private int id;
	private int delay;
	private int volume;

	public PlayMusicAction(int id, int delay, int volume, int actionDelay) {
		super(-1, actionDelay);
		this.id = id;
		this.delay = delay;
		this.volume = volume;
	}

	@Override
	public void process(Player player, Object[] cache) {
		player.getPackets().sendMusic(id, delay, volume);
	}

}
