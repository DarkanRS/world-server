package com.rs.game.content.world.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.quests.ImpCatcher;
import com.rs.game.content.quests.wolfwhistle.WolfWhistle;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class PetShopOwner extends Conversation {
	final static int PETSHOPOWNER = 6893;

	static final int WHITE_HARE_MEAT = 23067;


	public PetShopOwner(Player p) {
		super(p);

		if (p.getQuestManager().getStage(Quest.WOLF_WHISTLE) == WolfWhistle.WOLPERTINGER_MATERIALS) {
			addOptions("Select an Option", new Options() {
				@Override
				public void create() {

					if (!p.getInventory().containsItem(WHITE_HARE_MEAT) && !p.getBank().containsItem(WHITE_HARE_MEAT, 1)) {
						if (p.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).getB("HARE_MEAT")) {
							option("Ask about the white hare meat.", new Dialogue()
									.addPlayer(HeadE.CONFUSED, "Hello there, do you sell white hare meat?")
									.addNPC(PETSHOPOWNER, HeadE.CONFUSED, "Well I do, but what about the portion I gave you earlier?")
									.addPlayer(HeadE.SAD, "I may have accidentally lost it.")
									.addItem(WHITE_HARE_MEAT, "The pet shop owner gives you a portion of white hare meat.", () -> p.getInventory().addItem(WHITE_HARE_MEAT))
									.addNPC(PETSHOPOWNER, HeadE.CONFUSED, "I do hope this emergency is cleared up soon. I only have a few bits left!")
							);
						} else {
							option("Ask about the white hare meat.", new Dialogue()
									.addPlayer(HeadE.CONFUSED, "Hello there, do you sell white hare meat?")
									.addNPC(PETSHOPOWNER, HeadE.CHUCKLE, "Yes I do! I stock it as a treat for the animals, but some people round here snap it up for themselves! Waste not, want not!")
									.addNPC(PETSHOPOWNER, HeadE.CHEERFUL_EXPOSITION, "I get it imported from a very reputable trapper in Morytania called Rufus.")
									.addNPC(PETSHOPOWNER, HeadE.CHEERFUL, "He always knows how to get the best meat for the animals and ships it pre-skinned, boned and cut. White hare, basilisk, chinchompa, veal...")
									.addPlayer(HeadE.CALM, "That's all very interesting, but do you happen to have some white hare meat I could have? It is an emergency.")
									.addNPC(PETSHOPOWNER, HeadE.CALM, "Well if it is an emergency I suppose I can let you have some. I hope things work out for you!")
									.addItem(WHITE_HARE_MEAT, "The pet shop owner gives you a portion of white hare meat.", () -> {
										p.getInventory().addItem(WHITE_HARE_MEAT);
										p.getQuestManager().getAttribs(Quest.WOLF_WHISTLE).setB("HARE_MEAT", true);
									})
									.addNPC(PETSHOPOWNER, HeadE.CONFUSED, "Is there anything else I can help you with?")
									.addNext(() -> p.startConversation(new PetShopOwner(p)))
							);
						}
					} else {
						option("Ask about the white hare meat.", new Dialogue()
								.addNPC(PETSHOPOWNER, HeadE.CONFUSED, "Well I do, but what about the portion I gave you earlier?")
								.addPlayer(HeadE.CONFUSED, "Uh, right, now I remember where I put it. Thanks.")
						);
					}

					option("Ask about something else.");
				}
			});
		} else
			addNPC(PETSHOPOWNER, HeadE.CHEERFUL, "Hello!");
	}

	public static NPCClickHandler handlePetshopownerDialogue = new NPCClickHandler(new Object[] { PETSHOPOWNER }, e -> {
		if (e.getOption().toLowerCase().equals("talk-to"))
			e.getPlayer().startConversation(new PetShopOwner(e.getPlayer()));
		else if (e.getOption().toLowerCase().equals("trade"))
		ShopsHandler.openShop(e.getPlayer(), "taverly_pet_shop");
	});

}