package com.rs.game.player.dialogues;

import com.rs.lib.game.WorldTile;

public class MagicPortal extends Dialogue {

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1)
			stage = 0;
		if (stage == 0) {
			if (componentId == 2) {
				player.sendMessage("You enter the portal...");
				player.useStairs(10584, new WorldTile(3233, 9315, 0), 2, 3, "..and are transported to an altar.");
				player.addWalkSteps(1599, 4515, -1, false);
				end();
			}
			if (componentId == 3) {
				player.sendMessage("You enter the portal...");
				player.useStairs(10584, new WorldTile(2152, 3868, 0), 2, 3, "..and are transported to an altar.");
				player.addWalkSteps(1600, 4514, -1, false);
				end();
			} else
				end();
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Select an Option", "Ancient Magic Altar", "Lunar Magic Altar", "Never Mind");
	}

}
