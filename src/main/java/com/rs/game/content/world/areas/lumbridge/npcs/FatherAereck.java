package com.rs.game.content.world.areas.lumbridge.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.content.world.GraveStoneSelection;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class FatherAereck extends Conversation {

	//Identify NPC by ID
	//NPC handler was 9827? check restless ghost quest..
	private static final int npcId = 456;
	public static NPCClickHandler FatherAereck = new NPCClickHandler(new Object[]{npcId}, e -> {
		switch (e.getOption()) {
		
		case "Talk-to" -> e.getPlayer().startConversation(new FatherAereck(e.getPlayer()));
		}
	});

	public FatherAereck(Player player) {
		super(player);
		addNPC(npcId, HeadE.CALM_TALK, "Hello there " + player.getPronoun("brother ", "sister ") + Utils.formatPlayerNameForDisplay(player.getDisplayName()) + ". How may I help you today?");
		addOptions("What would you like to say?", new Options() {
			@Override
			public void create() {
				if(player.getQuestManager().getStage(Quest.RESTLESS_GHOST) == 0)
					option("I'm looking for a quest.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'm looking for a quest.")
							.addNPC(npcId, HeadE.CALM_TALK, "Well that's convenient. I seem to be having a bit of a<br>ghost problem. Could you go speak to " +
									"speak to<br>Father Urhney down in the swamp about how to<br>exorcise the spirit?", () -> {
								player.getQuestManager().setStage(Quest.RESTLESS_GHOST, 1, true);
							})
					);
				option("Can I have a different gravestone?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Can I have a different gravestone?")
						.addNPC(npcId, HeadE.CALM_TALK, "Of course you can. Have a look at this selection of gravestones.")
						.addNext(()->{GraveStoneSelection.openSelectionInterface(player);})
				);
				option("Can you restore my prayer?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Can you restore my prayer?")
						.addNPC(npcId, HeadE.CALM_TALK, "I think the Gods prefer it if you pray<br>to them at an altar dedicated to their name.")
				);
			}
		});
	}
}

//TODO restore standard conversation options