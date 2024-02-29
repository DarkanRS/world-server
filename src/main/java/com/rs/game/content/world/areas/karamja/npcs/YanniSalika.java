package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Inventory;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class YanniSalika {

	public static NPCClickHandler handleYanniSalika = new NPCClickHandler(new Object[] { 515 }, new String[] { "Talk-to" }, e -> {
		Player player = e.getPlayer();
		NPC npc = e.getNPC();

		Dialogue oneSmallFavour = new Dialogue()
				.addPlayer(HeadE.SECRETIVE, "Is there anything else interesting to do around here?")
				.addNPC(npc, HeadE.CALM_TALK, "You sound a bit bored of Shilo Village...what's wrong?")
				.addPlayer(HeadE.CALM_TALK, "Nothing, I was just wondering if there was anything else to do?")
				.addNPC(npc, HeadE.CALM_TALK, "Well, if you're bored, you could do me a small favour and nip to see the jungle forester. I need a piece of red mahogany to resurface an antique wardrobe.")
				.addOptions((ops1) -> {
					ops1.add("You want me to do you a favour?")
							.addPlayer(HeadE.CALM_TALK, "You want me to do you a favour?")
							.addNPC(npc, HeadE.CALM_TALK, "Well, if you've got nothing better to do...I mean, you looked bored.");
					ops1.add("An adventurer of my calibre, isn't that overkill.")
							.addPlayer(HeadE.CALM_TALK, "An adventurer of my calibre, isn't that overkill.")
							.addNPC(npc, HeadE.CALM_TALK, "Coooiii! Sorry your most amazingness, didn't realise you were too above it all to do someone a favour!");
					ops1.add("What are you going to give me if I do it?")
							.addPlayer(HeadE.CALM_TALK, "What are you going to give me if I do it?")
							.addNPC(npc, HeadE.CALM_TALK, "Give you? I don't think you heard me...I asked for a favour! It means that I'll 'owe you one' when you need something doing. That's what a favour is!");
					ops1.add("Nah thanks, I've got bigger fish to fry.")
							.addPlayer(HeadE.CALM_TALK, "Nah thanks, I've got bigger fish to fry.")
							.addNPC(npc, HeadE.CALM_TALK, "Fair enough! Hope it all goes well...but, you know, you looked bored so I just thought I'd suggest it.");
					ops1.add("Ok, see you in a tick!")
							.addSimple("Sorry, the 'One Small Favor' quest is not implemented yet.");
				});

		player.startConversation(new Dialogue()
				.addOptions((ops) -> {
					ops.add("Tell me about this antiques business.")
							.addNPC(npc, HeadE.CALM_TALK, "I buy antiques and other interesting items. If you have any interesting items that you might want to sell me, please let me see them and I'll offer you a fair price. Would you like me to have a look at your items and give")
							.addNPC(npc, HeadE.CALM_TALK, "you a quote?")
							.addOptions((ops1) -> {
								ops1.add("Yes please!")
										.addPlayer(HeadE.CALM_TALK, "Yes please!")
										.addNPC(npc, HeadE.CALM_TALK, "Great Bwana!")
										.addNext(() -> {
											Dialogue tradeIn = new Dialogue();
											Inventory inventory = player.getInventory();
											int items = 0;
											if (inventory.containsItem(605)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 100 Gold for the Bone Key.");
												items++;
											}
											if (inventory.containsItem(606)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 100 Gold for the Stone-Plaque.");
												items++;
											}
											if (inventory.containsItem(607)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 100 Gold for the tattered scroll.");
												items++;
											}
											if (inventory.containsItem(608)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 100 Gold for the crumpled scroll.");
												items++;
											}
											if (inventory.containsItem(624)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 100 Gold for your Bervirius notes.");
												items++;
											}
											if (inventory.containsItem(611)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 500 Gold for your locating crystal.");
												items++;
											}
											if (inventory.containsItem(616)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 1000 Gold for your 'Beads of the Dead'.");
												items++;
											}
											if (inventory.containsItem(4808)) {
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "I'll give you 5000 Gold for your black prism.");
												items++;
											}
											if (items == 0)
												tradeIn = tradeIn.addNPC(npc, HeadE.CALM_TALK, "Sorry Bwana, you have nothing I am interested in.");
											if (items == 1)
												tradeIn = tradeIn
														.addNPC(npc, HeadE.CALM_TALK, "And that's the only item I am interested in.")
														.addNPC(npc, HeadE.CALM_TALK, "If you want to sell me that item, simply show it to me.");
											if (items > 1) {
												tradeIn = tradeIn
														.addNPC(npc, HeadE.CALM_TALK, "Those are the items I am interested in Bwana.")
														.addNPC(npc, HeadE.CALM_TALK, "If you want to sell me those items, simply show them to me.");
											}
											player.startConversation(tradeIn);
										});
								ops1.add("Maybe some other time?")
										.addPlayer(HeadE.CALM_TALK, "Maybe some other time?")
										.addNPC(npc, HeadE.CALM_TALK, "Sure thing. Have a nice day Bwana.");
								ops1.add("Is there anything else interesting to do around here?")
										.addNext(() -> player.startConversation(oneSmallFavour));
								ops1.add("Hmm, sorry, not interested.")
										.addPlayer(HeadE.CALM_TALK, "Hmm, sorry, not interested.")
										.addNPC(npc, HeadE.CALM_TALK, "Fair enough, come back if  you change your mind.");
							});
					ops.add("Is there anything else interesting to do around here?")
							.addNext(() -> player.startConversation(oneSmallFavour));

					ops.add("Hmm, sorry, not interested.")
							.addPlayer(HeadE.CALM_TALK, "Hmm, sorry, not interested.")
							.addNPC(npc, HeadE.CALM_TALK, "Fair enough, come back if you change your mind.");
				}));
	});

}
