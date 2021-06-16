package com.rs.game.player.dialogues;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.minigames.partyroom.PartyRoom;

public class PartyPete extends Dialogue {

	@Override
	public void start() {
		sendEntityDialogue(SEND_3_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(659).getName(), "The items in the party chest are worth " + PartyRoom.getTotalCoins() + "", "coins! Hang around until they drop and you might get",
				"something valuable!" }, IS_NPC, 659, 9843);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
