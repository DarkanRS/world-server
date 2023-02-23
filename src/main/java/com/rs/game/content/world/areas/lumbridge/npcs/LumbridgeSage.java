package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class LumbridgeSage extends Conversation {

	// Identify NPC by ID
	private static final int npcId = 2244;
	public static NPCClickHandler LumbridgeSage = new NPCClickHandler(new Object[] { 2244 }, e -> {
		switch (e.getOption()) {
		// Start Conversation
		case "Talk-to" -> e.getPlayer().startConversation(new LumbridgeSage(e.getPlayer()));
		}
	});

	public LumbridgeSage(Player player) {
		super(player);
		// Identify NPC by ID
		addOptions(new Options() {
			@Override
			public void create() {
				// Give player options
				addOptions(new Options() {
					@Override
					public void create() {
						// Simple Reply
						addNPC(npcId, HeadE.CALM_TALK, "Greetings, adventurer. How may I help you?");
						option("Who are you?", new Dialogue()
								.addNPC(npcId, HeadE.CALM_TALK, "I am Phileas, the Lumbridge Sage. ")
								.addNPC(npcId, HeadE.CALM_TALK, " In times past, people came from all around to ask me for advice. ")
								.addNPC(npcId, HeadE.CALM_TALK, "My renown seems to have diminished somewhat in recent years, though.")
								.addNPC(npcId, HeadE.CALM_TALK, "Can I help you with anything?"));

						// Conversation
						option("Tell me about the town of Lumbridge",
								new Dialogue().addNPC(npcId, HeadE.CALM_TALK, "Lumbridge is one of the older towns in the human-controlled kingdoms. IT was founded over two hundred years ago towards the end of the Fourth Age. It's called Lumbridge because of this bridge built over the River Lum.")
								.addNPC(npcId, HeadE.CALM_TALK, "The town is governed by Duke Horacio, who is a good friend of our monarch, King Roald of Misthalin.")
										.addNPC(npcId, HeadE.CALM_TALK, "Recently, however, there have been great changes due to the Battle of Lumbridge.")
										.addNPC(npcId, HeadE.CALM_TALK, "Indeed, not long ago there was a great fight between Saradomin and Zamorak on the battlefield to the west of the castle.")
										.addNPC(npcId, HeadE.CALM_TALK, "Titanic forces were unleashed as neither side could gain the upper hand. Each side sought advantages, but it was close until the end.")
										.addNPC(npcId, HeadE.CALM_TALK, "The battle lasted for months, but in the end the forces of the holy Saradomin were triumphant. Zamorak was defeated... but...")
										.addNPC(npcId, HeadE.CALM_TALK, "Before Saradomin could complete his victory, Moia, the general of Zamorak's forces, transported him away.")
										.addNPC(npcId, HeadE.CALM_TALK, "Now, the battlefield lies empty save for a single Saradominist devotee, and Lumbridge lies in ruins. Luckily, Foreman George is organising a rebuilding effort, to the north of the castle."));
						option("Goodbye");
					}
				});
			}
		});
	}
}