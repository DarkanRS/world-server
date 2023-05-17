package com.rs.game.content.quests.piratestreasure;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.content.transportation.BoatingD;
import com.rs.game.content.transportation.TravelMethods;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.CUSTOMS_OFFICER;
import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.RUM;

@PluginEventHandler
public class CustomsOfficerPiratesTreasureD extends Conversation {



	public CustomsOfficerPiratesTreasureD(Player player) {
		super(player);
		addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "Halt! I am going to need to inspect your items.");
		addPlayer(HeadE.WORRIED, "Can I journey on this ship?");
		addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "You need to be searched before you can board.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(player.getInventory().containsItem(RUM))
					option("Search away, I have nothing to hide.", new Dialogue()
							.addPlayer(HeadE.WORRIED, "Search away, I have nothing to hide.")
							.addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "Aha, trying to smuggle rum are we?")
							.addPlayer(HeadE.HAPPY_TALKING, "Umm... it's for personal use?")
							.addSimple("The customs officer confiscates your rum.", () -> {
								while(player.getInventory().containsItem(RUM))
									player.getInventory().removeItems(new Item(RUM, 1));
							})
							.addSimple("You will need to find some way to smuggle it off the island.")
							);
				else
					option("Search away, I have nothing to hide.", new Dialogue()
							.addPlayer(HeadE.WORRIED, "Search away, I have nothing to hide.")
							.addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "Well, you've got some odd stuff, but it's all legal. You can board...")
							.addNext(()->{
								Object[] attributes = BoatingD.getBoatForShip(CustomsOfficerPiratesTreasureD.this.player, CUSTOMS_OFFICER);
								TravelMethods.Carrier ship = (TravelMethods.Carrier) attributes[0];
								boolean returning = (Boolean) attributes[1];
								TravelMethods.sendCarrier(CustomsOfficerPiratesTreasureD.this.player, ship, returning);
							})
							);

				option("You're not putting your hands on my things!", new Dialogue()
						.addPlayer(HeadE.ANGRY, "You're not putting your hands on my things!")
						.addNPC(CUSTOMS_OFFICER, HeadE.CALM_TALK, "You're not getting on this ship then."));
			}
		});


	}
}
