package com.rs.game.player.dialogues;

public class GrilleGoatsDialogue extends Dialogue {

	public static final int GRILLEGOATS = 3807;

	private int stage;

	@Override
	public void start() {
		sendNPCDialogue(GRILLEGOATS, 9827, "Tee hee! You have never milked a cow before, have you?");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(GRILLEGOATS, 9827, "Tee hee! You have never milked a cow before, have you?");
			break;
		case 0:
			sendPlayerDialogue(9827, "Erm... no. How could you tell?");
			break;
		case 1:
			sendNPCDialogue(GRILLEGOATS, 9827, "Because you're spilling milk all over the floor. What a waste! " + "You need something to hold the milk.");
			break;
		case 2:
			sendPlayerDialogue(9827, "Derp. Ah, yes, I really should have guessed that one, shouldn't I?");
			break;
		case 3:
			sendNPCDialogue(GRILLEGOATS, 9827, "You're from the city, arent you? Try it again with an empty bucket.");
			break;
		case 4:
			sendPlayerDialogue(9827, "Right, I'll do that.");
			break;
		case 5:
			end();
			break;
		}
		stage++;
	}

	@Override
	public void finish() {

	}
}
