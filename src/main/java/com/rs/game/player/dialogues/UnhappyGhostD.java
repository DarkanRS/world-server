package com.rs.game.player.dialogues;

import com.rs.game.player.content.skills.dungeoneering.rooms.puzzles.UnhappyGhostRoom;

public class UnhappyGhostD extends Dialogue {

	@Override
	public void start() {
		UnhappyGhostRoom puzzle = (UnhappyGhostRoom) parameters[0];
		sendNPCDialogue(UnhappyGhostRoom.GHOST, puzzle.isComplete() ? NORMAL : SAD, "Woooo wooooo woooooooo wooo");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
