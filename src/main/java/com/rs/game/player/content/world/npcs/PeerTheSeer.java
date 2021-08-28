package com.rs.game.player.content.world.npcs;

import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class PeerTheSeer {

	public static NPCClickHandler handler = new NPCClickHandler("Peer the Seer") {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getOption()) {
			case "Deposit":
				if (Quest.FREMENNIK_TRIALS.meetsRequirements(e.getPlayer(), "to deposit with Peer."))
					e.getPlayer().getBank().openDepositBox();
				break;
			}
		}
	};
	
}
