package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Yauchomi extends Conversation {

	// Identify NPC by ID
	private static final int npcId = 4903;
	public static NPCClickHandler Yauchomi = new NPCClickHandler(new Object[] { npcId }, e -> {
		switch (e.getOption()) {
		// Start Conversation
		case "Talk-to" -> e.getPlayer().startConversation(new Yauchomi(e.getPlayer()));
		}
	});

	public Yauchomi(Player player) {
		super(player);
		// Identify NPC by ID
		addOptions(new Options() {
			@Override
			public void create() {
				// Player initiates
				addPlayer(HeadE.CALM_TALK, "Good day, sister.");
				// NPC responds
				addNPC(npcId, HeadE.CALM_TALK, "Greetings, " + player.getDisplayName() + ", Can I help you with anything, today?");
				// Player given choices
				addOptions(new Options() {
					@Override
					public void create() {
						option("How can I train my prayer?", new Dialogue()
								.addNPC(npcId, HeadE.CALM_TALK, "Well, if you really want my advice...")
								.addNPC(npcId, HeadE.CALM_TALK, "The most common way to train prayer is by either burying bones, or offering them to the gods at some kind of an altar.")
								.addNPC(npcId, HeadE.CALM_TALK, "Lots of adventurers build such altars in their own homes, or there are a few frequent places of worship around the world.")
								.addNPC(npcId, HeadE.CALM_TALK, "Different kinds of bones will help you to train faster. Generally speaking, the bigger they are and the more frightening a creature they come from, the better they are for it."));

						option(" What is prayer useful for??", new Dialogue()
								.addNPC(npcId, HeadE.CALM_TALK, "The gods look kindly upon their devout followers. There are all kinds of benefits they may provide, if you pray for them!")
								.addPlayer(HeadE.AMAZED_MILD, "Really? What kind of benefits?")
								.addNPC(npcId, HeadE.CALM_TALK, "They could help you in combat, help your wounds to heal more quickly, protect your belongings... There's a lot they can do for you!")
								.addNPC(npcId, HeadE.CALM_TALK, "You can find out more by looking in your prayer book.")
								.addPlayer(HeadE.AMAZED_MILD, "Wow! That sounds great.")
								.addNPC(npcId, HeadE.CALM_TALK, "You need to be careful that your prayers don't run out, though. You can get prayer potions to help you recharge, or you can pray at an altar whenever one's nearby"));

					}

				});
			}
		});
	}
}
