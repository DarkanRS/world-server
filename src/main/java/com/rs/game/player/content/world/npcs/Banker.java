package com.rs.game.player.content.world.npcs;

import com.rs.game.ge.GE;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Banker extends Conversation {

	public Banker(Player player, NPC npc) {
		super(player);
		
		addNPC(npc.getId(), HeadE.CHEERFUL, "Good day. How may I help you?");
		addOptions(new Options() {
			@Override
			public void create() {
				option("I'd like to access my bank account, please.", () -> player.getBank().open());
				option("I'd like to check my PIN settings.", () -> player.getBank().openPinSettings());
				option("I'd like to see my collection box.", () -> GE.openCollection(player));
				option("What is this place?", new Dialogue()
					.addNPC(npc.getId(), HeadE.CHEERFUL, "This is a branch of the Bank of Gielinor. We have branches in many towns.")
					.addOptions(new Options() {
						@Override
						public void create() {
							option("And what do you do?", new Dialogue()
								.addNPC(npc.getId(), HeadE.CHEERFUL, "We will look after your items and money for you. Leave your valuables with us if you want to keep them safe."));
							option("Didn't you used to be called the Bank of Varrock?", new Dialogue()
								.addNPC(npc.getId(), HeadE.CALM, "Yes we did, but people kept on coming into our branches outside of Varrock and telling us that our signs were wrong. They acted as if we didn't know what town we were in or something."));
						}
					}));
			}
		});
		
		create();
	}
	
	public static NPCInteractionDistanceHandler bankerDistance = new NPCInteractionDistanceHandler("Banker") {
		@Override
		public int getDistance(Player player, NPC npc) {
			return 1;
		}
	};

	public static NPCClickHandler bankerHandler = new NPCClickHandler("Banker") {
		@Override
		public void handle(NPCClickEvent e) {
			
		}
	};
	
	public static ObjectClickHandler bankObjHandler = new ObjectClickHandler(new Object[] { "Bank booth", "Bank", "Bank chest", "Bank table", "Counter", "Shantay chest", "Darkmeyer Treasury" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getOption()) {
			case "Bank":
				e.getPlayer().getBank().open();
				break;
			case "Collect":
				GE.openCollection(e.getPlayer());
				break;
			case "Use":
				if (e.getObject().getDefinitions().getName(e.getPlayer().getVars()).equals("Bank chest"))
					e.getPlayer().getBank().open();
				break;
			case "Open":
				if (e.getObject().getDefinitions().getName(e.getPlayer().getVars()).equals("Shantay chest"))
					e.getPlayer().getBank().open();
				break;
			default:
				e.getPlayer().sendMessage("Unhandled bank object option: " + e.getOption());
				break;
			}
		}
	};
	
	public static ObjectClickHandler depositBoxHandler = new ObjectClickHandler(new Object[] { "Bank deposit box", "Deposit box", "Deposit Box", "Deposit chest", "Pulley lift" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			switch(e.getOption()) {
			case "Deposit":
			case "Use":
				e.getPlayer().getBank().openDepositBox();
				break;
			default:
				e.getPlayer().sendMessage("Unhandled deposit box object option: " + e.getOption());
				break;
			}
		}
	};
}
