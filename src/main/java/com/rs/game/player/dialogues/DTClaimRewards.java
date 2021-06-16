package com.rs.game.player.dialogues;

public class DTClaimRewards extends Dialogue {

	@Override
	public void start() {
		sendDialogue("You have a Dominion Factor of " + player.getDominionTower().getDominionFactor() + ".");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("If you claim your rewards your progress will be reset.", "Claim rewards", "Cancel");
		} else if (stage == 0) {
			if (componentId == 11)
				player.getDominionTower().openRewardsChest();
			end();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
