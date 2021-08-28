package com.rs.game.player.content.world.regions;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Neitiznot  {
	
	static class MawnisBurowgarD extends Conversation {
		
		private static final int MAWNIS = 5503;
		
		public MawnisBurowgarD(Player player) {
			super(player);
			
			addNPC(MAWNIS, HeadE.HAPPY_TALKING, "It makes me proud to know that the helm of my ancestors will be worn in battle.");
			addNPC(MAWNIS, HeadE.HAPPY_TALKING, "I thank you on behalf of all my kinsmen Dallim Far-strider.");
			addPlayer(HeadE.WORRIED, "Ah yes, about that beautiful helmet.");
			addNPC(MAWNIS, HeadE.CONFUSED, "You mean the priceless heirloom that I gave to you as a sign of my trust and gratitude?");
			addPlayer(HeadE.WORRIED, "Err yes, that one. I may have mislaid it.");
			addNPC(MAWNIS, HeadE.CONFUSED, "It's a good job I have alert and loyal men who notice when something like this is left lying around and picks it up.");
			addNPC(MAWNIS, HeadE.CONFUSED, "I'm afraid I'm going to have to charge you a 50,000GP handling cost.");
			Dialogue op = addOption("Pay 50,000GP to recover your helmet?", "Yes, that would be fine.", "No, that's too much.");
			if (player.getInventory().containsItem(995, 50000)) {
				op.addNPC(MAWNIS, HeadE.HAPPY_TALKING, "Please be more careful with it in the future.", () -> {
					if (player.getInventory().containsItem(995, 50000)) {
						player.getInventory().deleteItem(995, 50000);
						player.getInventory().addItem(10828, 1, true);
					}
				});
			} else {
				op.addNPC(MAWNIS, HeadE.HAPPY_TALKING, "You don't have enough gold right now.");
			}
			op.addNPC(MAWNIS, HeadE.HAPPY_TALKING, "Okay. Come back later if you change your mind.");
			
			create();
		}
	}
	
	public static ObjectClickHandler handleLadders = new ObjectClickHandler(new Object[] { 21512, 21513, 21514, 21515 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 21512 || e.getObjectId() == 21513) {
				e.getPlayer().useLadder(e.getObjectId() == 21512 ? e.getPlayer().transform(2, 0, 2) : e.getPlayer().transform(-2, 0, -2));
			} else if (e.getObjectId() == 21514 || e.getObjectId() == 21515) {
				e.getPlayer().useLadder(e.getObjectId() == 21514 ? e.getPlayer().transform(-2, 0, 1) : e.getPlayer().transform(2, 0, -1));
			}
		}
	};
	
	public static NPCClickHandler handleMawnis = new NPCClickHandler(5503) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new MawnisBurowgarD(e.getPlayer()));
		}
	};
	
	public static NPCClickHandler handleShops = new NPCClickHandler(5509, 5487, 5484, 5486, 5485, 5483, 5495) {
		@Override
		public void handle(NPCClickEvent e) {
			switch(e.getNPC().getId()) {
			case 5509:
				ShopsHandler.openShop(e.getPlayer(), "neitiznot_supplies");
				break;
			case 5487:
				ShopsHandler.openShop(e.getPlayer(), "keepa_kettilons_store");
				break;
			case 5484:
				ShopsHandler.openShop(e.getPlayer(), "flosis_fishmongers");
				break;
			case 5486:
				ShopsHandler.openShop(e.getPlayer(), "weapons_galore");
				break;
			case 5485:
				ShopsHandler.openShop(e.getPlayer(), "armour_shop");
				break;
			case 5483:
				ShopsHandler.openShop(e.getPlayer(), "ore_store");
				break;
			case 5495:
				ShopsHandler.openShop(e.getPlayer(), "contraband_yak_produce");
				break;
			}
		}
	};
	
	public static NPCClickHandler handleCureHide = new NPCClickHandler(5506) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().sendOptionDialogue("What can I help you with?", new String[] {"Cure my yak-hide, please.", "Nothing, thanks."}, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (getOption() == 1) {
						if (e.getPlayer().getInventory().containsItem(10818, 1)) {
							int number = e.getPlayer().getInventory().getAmountOf(10818);
							e.getPlayer().getInventory().deleteItem(10818, number);
							e.getPlayer().getInventory().addItem(10820, number);
						}
					}
				}
			});
		}
	};
	
	public static NPCClickHandler handleNeitzTravel = new NPCClickHandler(5507, 5508) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getNPC().getId() == 5507 ? new WorldTile(2644, 3709, 0) : new WorldTile(2310, 3781, 0));
		}
	};
	
	public static NPCClickHandler handleJatizoTravel = new NPCClickHandler(5482, 5481) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().setNextWorldTile(e.getNPC().getId() == 5482 ? new WorldTile(2644, 3709, 0) : new WorldTile(2420, 3781, 0));
		}
	};
	
	public static NPCClickHandler handleMagnusBanker = new NPCClickHandler(5488) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().getBank().open();
		}
	};
}
