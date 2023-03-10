package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.NpcID;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Doomsayer extends Conversation {

	private static final int npcId = NpcID.NPCS.valueOf("Doomsayer").getId();
	
	@ServerStartupEvent
	public static void addLoSOverrides() {
		Entity.addLOSOverride(npcId);
	}
	
	public static NPCClickHandler Doomsayer = new NPCClickHandler(new Object[] { npcId }, e -> {
		switch(e.getOption()) {
		case "Talk-to" -> e.getPlayer().startConversation(new Doomsayer(e.getPlayer()));
		}
	});

	public Doomsayer(Player player) {
		super(player);
		addNPC(npcId, HeadE.SCARED, "Dooooom!")
		.addPlayer( HeadE.WORRIED, "Do you mean the Battle of Lumbridge? Are you telling me I should go and help out by going to join in?")
		.addNPC(npcId, HeadE.CONFUSED,"No, why should I be doing that? I'm talking about doooooom here, not some battlefield.")
		.addPlayer(HeadE.CONFUSED, "Well, everyone else seems to be... um... anyway, you mentioned doom. Where is this doom?")
		.addNPC(npcId, HeadE.SCARED, "All around us! I can feel it in the air, hear it on the wind, smell it...also in the air!")
		.addPlayer(HeadE.CONFUSED, "Is there anything we can do about this doom?")
		.addNPC(npcId, HeadE.HAPPY_TALKING, "There is nothing you need to do my friend! I am the Doomsayer, although my real title could be something like the Danger Tutor.")
		.addPlayer(HeadE.CONFUSED, "Danger Tutor?")
		.addNPC(npcId, HeadE.CHEERFUL, "Yes! I roam the world sensing danger.")
		.addNPC(npcId, HeadE.CHEERFUL, " If I find a dangerous area, then I put up warning signs that will tell you what is so dangerous about that area.");
		create();
	}
}

//TODO add toggle warnings handler

//Correct Behaviour
//Doomsayer: If you see the signs often enough, then you can turn them of; by that time you likely known what the area has in store for you.
//Player: But what If I want to see the warnings again?
//Doomsayer: That's why I'm waiting here!
//Doomsayer: If you want to see the warning messages again, I can turn them back on for you.
//Doomsayer: Do you need to turn on any warnings right now?
//Player: Yes, I do.
//Screen opens
//Player: Not right now.
//Doomsayer: Ok, keep an eye out for the messages though!
//Player: I will.