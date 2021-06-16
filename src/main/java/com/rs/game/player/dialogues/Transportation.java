package com.rs.game.player.dialogues;

import com.rs.game.player.content.transportation.ItemTeleports;
import com.rs.lib.game.Item;

public class Transportation extends Dialogue {
	
	Item item;

	@Override
	public void start() {
		String[] locations = (String[]) parameters[0];
		if (parameters.length > 2 && parameters[2] != null)
			item = (Item) parameters[2];
		sendOptionsDialogue("Where would you like to teleport to", locations);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		String[] locations = (String[]) parameters[0];
		if (item != null)
			ItemTeleports.sendTeleport(player, item, componentId == OPTION_1 ? 0 : componentId - 12);
		else
			ItemTeleports.sendTeleport(player, player.getInventory().getItems().lookup((Integer) parameters[1]), componentId == OPTION_1 ? 0 : componentId - 12, false, locations.length);
		end();
	}

	@Override
	public void finish() {
	}

}
