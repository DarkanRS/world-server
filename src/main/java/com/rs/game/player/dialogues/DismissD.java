package com.rs.game.player.dialogues;

public class DismissD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue(player.getPet() != null ? "Free pet?" : "Dismiss Familiar?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1 && componentId == OPTION_1) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().sendDeath(player);
			} else if (player.getPet() != null) {
				stage = 0;
				sendPlayerDialogue(9827, "Run along; I'm setting you free.");
				return;
			}
		} else if (stage == 0 && player.getPet() != null) {
			player.getPetManager().setNpcId(-1);
			player.getPetManager().setItemId(-1);
			player.getPetManager().removeDetails(player.getPet().getItemId());
			player.getPet().switchOrb(false);
			player.getInterfaceManager().removeWindowInterface(98, 212);
			player.getPackets().setIFTargetParamsDefault(747, 17, 0, 0);
			player.getPet().finish();
			player.setPet(null);
			player.sendMessage("Your pet runs off until it's out of sight.");
		}
		end();
	}

	@Override
	public void finish() {

	}

}
