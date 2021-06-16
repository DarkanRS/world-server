package com.rs.game.player.dialogues;

import com.rs.game.pathing.RouteEvent;
import com.rs.game.player.content.minigames.partyroom.PartyRoom;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PartyRoomLever extends Dialogue {
	
	public static ObjectClickHandler handle = new ObjectClickHandler(false, new Object[] { 26194 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setRouteEvent(new RouteEvent(new WorldTile(e.getObject()), () -> {
				e.getPlayer().getDialogueManager().execute(new PartyRoomLever());
			}));
		}
	};

	@Override
	public void start() {
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Balloon Bonanza (1000 coins).", "Nightly Dance (500 coins).", "No action.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			PartyRoom.purchase(player, true);
		} else if (componentId == OPTION_2) {
			PartyRoom.purchase(player, false);
		}
		end();
	}

	@Override
	public void finish() {

	}
}
