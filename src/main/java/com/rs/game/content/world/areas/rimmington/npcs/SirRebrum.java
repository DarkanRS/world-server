package com.rs.game.content.world.areas.rimmington.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SirRebrum {

	public static NPCClickHandler talkToSirRebrum = new NPCClickHandler(new Object[] { 15460 }, e -> {
		Player player = e.getPlayer();
		NPC npc = e.getNPC();
		player.startConversation(new Dialogue()
				.addNPC(npc, HeadE.DRUNK, "Half, adventurer! Go no further! Mortal, wormy peril lies within this cave!")
				.addOptions(ops -> {
					ops.add("Wormy?");
					ops.add("Who are you again?");
					ops.add("Don't worry, I eat peril for breakfast.");
					if (!player.containsAnyItems(24303, 24337, 24338, 24339)) {
						ops.add("Would you happen to have found a Coral Crossbow?")
								.addNPC(npc, HeadE.DRUNK, "Why, yes I have! The Raptor passed by earlier. He said you might need it.")
								.addItemToInv(player, new Item(24303, 1), "You receive a Coral Crossbow.");
					}
				}));
	});
}
