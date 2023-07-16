package com.rs.game.content.world.areas.nardah.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Artimeus extends Conversation {

	public static NPCClickHandler handleArtimeus = new NPCClickHandler(new Object[] { 5109 }, e -> {
		switch(e.getOption()) {
		case "Talk-to" -> e.getPlayer().startConversation(new Artimeus(e.getPlayer(), e.getNPC()));
		}
	});

	public Artimeus(Player player, NPC npc) {
		super(player);

		addNPC(npc.getId(), HeadE.CHEERFUL, "Greetings, friend. My business here deals with Hunter- related items. Is there anything in which I can interest you?");
		addOptions(this, "baseOptions", ops -> {
			ops.add("What kinds of items do you stock?")
				.addPlayer(HeadE.CONFUSED, "What kinds of items do you stock?")
				.addNPC(npc.getId(), HeadE.CALM_TALK, "Take a look for yourself.")
				.addGotoStage("baseOptions", this);
			ops.add("I'm not in the market for Hunter equipment right now, thanks.")
				.addPlayer(HeadE.CALM_TALK, "I'm not in the market for Hunter equipment right now, thanks.")
				.addNPC(npc.getId(), HeadE.CALM_TALK, "Maybe another time, then.")
				.addGotoStage("baseOptions", this);
			ops.add("Do you have any Hunter wisdom to share?")
				.addPlayer(HeadE.CONFUSED, "Do you have any Hunter wisdom to share?")
				.addNPC(npc.getId(), HeadE.CALM_TALK, "Some creatures can be particularly fond of certain food. If you bait your trap you are more likely to succeed.")
				.addPlayer(HeadE.CONFUSED, "So I need to use bait?")
				.addNPC(npc.getId(), HeadE.CALM_TALK, "Sometimes it's essential. Creatures in Isafdar, for example, won't go near a trap that isn't baited. They are clever animals but quite greedy, it seems.")
				.addOptions(ops2 -> {
					ops2.add("Can you tell me more about hunting in Isafdar?")
						.addPlayer(HeadE.CONFUSED, "Can you tell me more about hunting in Isafdar?")
						.addNPC(npc.getId(), HeadE.CALM_TALK, "There are two animals ideal for hunting there: the pawya and the grenwall. Grenwall hunt for pawya, so pawya meat is a good bait to catch them.")
						.addPlayer(HeadE.CONFUSED, "And what about catching pawya?")
						.addNPC(npc.getId(), HeadE.CALM_TALK, "From the Hunter tales I've heard around campfires, they seem to find papaya fruit irresistable.")
						.addGotoStage("baseOptions", this);
					ops2.add("Thanks for your help.")
						.addPlayer(HeadE.CONFUSED, "Thanks for your help.")
						.addNPC(npc.getId(), HeadE.CALM_TALK, "It's always a pleasure to share Hunter knowledge.")
						.addNPC(npc.getId(), HeadE.CALM_TALK, "Is there anything else in which I can interest you?")
						.addGotoStage("baseOptions", this);
				});
		});
	}
}