package com.rs.game.player.cutscenes.actions;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.Cutscene;

public class PosCameraAction extends CutsceneAction {

	private int moveLocalX;
	private int moveLocalY;
	private int moveZ;
	private int speed;
	private int speed2;

	public PosCameraAction(int moveLocalX, int moveLocalY, int moveZ, int speed, int speed2, int actionDelay) {
		super(-1, actionDelay);
		this.moveLocalX = moveLocalX;
		this.moveLocalY = moveLocalY;
		this.moveZ = moveZ;
		this.speed = speed;
		this.speed2 = speed2;
	}

	public PosCameraAction(int moveLocalX, int moveLocalY, int moveZ, int actionDelay) {
		this(moveLocalX, moveLocalY, moveZ, -1, -1, actionDelay);
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		player.getPackets().sendCameraPos(scene.getLocalX(player, moveLocalX), scene.getLocalY(player, moveLocalY), moveZ, speed, speed2);
	}

}
