package com.rs.game.content.world.areas.karamja.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Saniboch {

	public static NPCClickHandler handleSaniboch = new NPCClickHandler(new Object[] { 1595 }, e -> {
		if(e.getOption().equalsIgnoreCase("Talk-to")) {
			int NPC = e.getNPCId();
			if(e.getPlayer().getTempAttribs().getB("paid_brimhaven_entrance_fee")) {
				e.getPlayer().startConversation(new Dialogue().addNPC(NPC, HeadE.HAPPY_TALKING, "Thank you for your payment, bwana."));
				return;
			}
			e.getPlayer().startConversation(new Dialogue()
					.addNPC(NPC, HeadE.HAPPY_TALKING, "Good day to you bwana")
					.addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							if(e.getPlayer().getInventory().hasCoins(875)) {
								option("Can I go through that door please?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Can I go through that door please?")
										.addNPC(NPC, HeadE.HAPPY_TALKING, "Most certainly, but I must charge you the sum of 875 coins first")
										.addOptions("Choose an option:", new Options() {
											@Override
											public void create() {
												option("Ok, here's 875 coins", new Dialogue()
														.addPlayer(HeadE.CALM_TALK, "Ok, here's 875 coins")
														.addItem(6964, "You give SaniBoch 875 coins.", ()->{
															e.getPlayer().getInventory().removeCoins(875);
															e.getPlayer().getTempAttribs().setB("paid_brimhaven_entrance_fee", true);
														})
														.addNPC(NPC, HeadE.HAPPY_TALKING, "Many thanks. You may now pass the door. May your death be a glorious one!")
												);
												option("Never mind.", new Dialogue()
														.addPlayer(HeadE.HAPPY_TALKING, "Never mind.")
												);
												option("Why is it worth the entry cost?", new Dialogue()
														.addPlayer(HeadE.HAPPY_TALKING, "Why is it worth the entry cost?")
														.addNPC(NPC, HeadE.CALM_TALK, "It leads to a huge fearsome dungeon, populated by giants and strange dogs. Adventurers come from all around to explore its depths.")
														.addNPC(NPC, HeadE.CALM_TALK, "I know not what lies deeper in myself, for my skills in agility and woodcutting are inadequate, but I hear tell of even greater dangers deeper in.")
														.addPlayer(HeadE.HAPPY_TALKING, "That's nice.")


												);
											}
										})
								);
							} else {
								option("Can I go through that door please?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Can I go through that door please?")
										.addNPC(NPC, HeadE.HAPPY_TALKING, "Most certainly, but I must charge you the sum of 875 coins first")
										.addPlayer(HeadE.SAD, "I don't have that...")
										.addNPC(NPC, HeadE.FRUSTRATED, "Well this is a dungeon for the more wealthy discerning adventurer, be gone with you riff raff.")
										.addPlayer(HeadE.HAPPY_TALKING, "But you don't even have clothes, how can you seriously call anyone riff raff.")
										.addNPC(NPC, HeadE.FRUSTRATED, "Hummph.")
								);
							}
							option("Where does this strange entrance lead?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Where does this strange entrance lead?")
									.addNPC(NPC, HeadE.CALM_TALK, "To a huge fearsome dungeon, populated by giants and strange dogs. Adventurers come from all around to explore its depths.")
									.addNPC(NPC, HeadE.CALM_TALK, "I know not what lies deeper in myself, for my skills in agility and woodcutting are inadequate.")
									.addPlayer(HeadE.HAPPY_TALKING, "That's nice.")
							);
							option("Good day to you too", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Good day to you too")
									.addNPC(NPC, HeadE.CALM_TALK, "...")
							);
							option("I'm impressed, that tree is growing on that shed.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I'm impressed, that tree is growing on that shed.")
									.addNPC(NPC, HeadE.CALM_TALK, "My employer tells me it is an uncommon sort of tree called the Fyburglars tree.")
									.addPlayer(HeadE.HAPPY_TALKING, "That's nice.")
							);
						}
					})
			);
		}
		if(e.getOption().equalsIgnoreCase("pay")) {
			if(e.getPlayer().getTempAttribs().getB("paid_brimhaven_entrance_fee")) {
				e.getPlayer().startConversation(new Dialogue().addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "You already paid, bwana."));
				return;
			}
			e.getPlayer().startConversation(new Dialogue()
					.addItem(6964, "You give SaniBoch 875 coins.", ()->{
						e.getPlayer().getInventory().removeCoins(875);
						e.getPlayer().getTempAttribs().setB("paid_brimhaven_entrance_fee", true);
					})
					.addNPC(e.getNPCId(), HeadE.HAPPY_TALKING, "Many thanks. You may now pass the door. May your death be a glorious one!")
			);
		}
	});

}
