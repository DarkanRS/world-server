package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Saniboch {

	public static NPCClickHandler handleSaniboch = new NPCClickHandler(new Object[] { 1595 }, new String[] { "Talk-to", "Pay" }, e -> {
		Player player = e.getPlayer();
		NPC npc = e.getNPC();

		boolean hasPaid = player.getTempAttribs().getB("paid_brimhaven_entrance_fee");
		boolean hasEnoughGP = player.getInventory().hasCoins(875);
		Dialogue lackOfGPResponse = new Dialogue()
				.addPlayer(HeadE.SAD, "I don't have that...")
				.addNPC(npc, HeadE.FRUSTRATED, "Well this is a dungeon for the more wealthy discerning adventurer, be gone with you riff raff.")
				.addPlayer(HeadE.HAPPY_TALKING, "But you don't even have clothes, how can you seriously call anyone riff raff.")
				.addNPC(npc, HeadE.FRUSTRATED, "Hummph.");

		switch (e.getOption()) {
			case "Talk-to" -> {
				if (hasPaid) {
					player.startConversation(new Dialogue()
							.addNPC(npc, HeadE.HAPPY_TALKING, "Thank you for your payment, bwana."));
					return;
				}
				player.startConversation(new Dialogue()
						.addNPC(npc, HeadE.HAPPY_TALKING, "Good day to you bwana")
						.addOptions("Choose an option:", (ops) -> {
							Dialogue goThroughDialogue = new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Can I go through that door please?")
									.addNPC(npc, HeadE.HAPPY_TALKING, "Most certainly, but I must charge you the sum of 875 coins first");
							if (!hasEnoughGP) {
								goThroughDialogue.addNext(lackOfGPResponse.getHead());
							} else {
								goThroughDialogue.addOptions("Choose an option:", (ops1) -> {
									ops1.option("Ok, here's 875 coins", new Dialogue()
											.addPlayer(HeadE.CALM_TALK, "Ok, here's 875 coins")
											.addItem(6964, "You give Saniboch 875 coins.", () -> {
												player.getInventory().removeCoins(875);
												player.getTempAttribs().setB("paid_brimhaven_entrance_fee", true);
											})
											.addNPC(npc, HeadE.HAPPY_TALKING, "Many thanks. You may now pass the door. May your death be a glorious one!"));
									ops1.option("Never mind.", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Never mind."));
									ops1.option("Why is it worth the entry cost?", new Dialogue()
											.addPlayer(HeadE.HAPPY_TALKING, "Why is it worth the entry cost?")
											.addNPC(npc, HeadE.CALM_TALK, "It leads to a huge fearsome dungeon, populated by giants and strange dogs. Adventurers come from all around to explore its depths.")
											.addNPC(npc, HeadE.CALM_TALK, "I know not what lies deeper in myself, for my skills in agility and woodcutting are inadequate, but I hear tell of even greater dangers deeper in.")
											.addPlayer(HeadE.HAPPY_TALKING, "That's nice."));
								});
							}
							ops.option("Can I go through that door please?", goThroughDialogue);
							ops.option("Where does this strange entrance lead?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Where does this strange entrance lead?")
									.addNPC(npc, HeadE.CALM_TALK, "To a huge fearsome dungeon, populated by giants and strange dogs. Adventurers come from all around to explore its depths.")
									.addNPC(npc, HeadE.CALM_TALK, "I know not what lies deeper in myself, for my skills in agility and woodcutting are inadequate.")
									.addPlayer(HeadE.HAPPY_TALKING, "That's nice."));
							ops.option("Good day to you too", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Good day to you too")
									.addNPC(npc, HeadE.CALM_TALK, "..."));
							ops.option("I'm impressed, that tree is growing on that shed.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I'm impressed, that tree is growing on that shed.")
									.addNPC(npc, HeadE.CALM_TALK, "My employer tells me it is an uncommon sort of tree called the Fyburglars tree.")
									.addPlayer(HeadE.HAPPY_TALKING, "That's nice."));
						}));
			}
			case "Pay" -> {
				if (hasPaid) {
					player.startConversation(new Dialogue()
							.addNPC(npc, HeadE.HAPPY_TALKING, "You already paid, bwana."));
					return;
				}
				if (hasEnoughGP) {
					player.startConversation(new Dialogue()
							.addItem(6964, "You give Saniboch 875 coins.", () -> {
								player.getInventory().removeCoins(875);
								player.getTempAttribs().setB("paid_brimhaven_entrance_fee", true);
							})
							.addNPC(npc, HeadE.HAPPY_TALKING, "Many thanks. You may now pass the door. May your death be a glorious one!"));
					return;
				}
				player.startConversation(lackOfGPResponse);
			}
		}
	});

}
