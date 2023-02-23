package com.rs.game.content.minigames.pyramidplunder;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class MummyNPC {
	public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[] { 4476 }, e -> {
		Player p = e.getPlayer();
		int NPC = 4476;
		if(e.getOption().equalsIgnoreCase("talk-to"))
			p.startConversation(new Conversation(p) {
				{
					addNPC(NPC, HeadE.CHILD_FRUSTRATED, "*sigh* Not another one.");
					addPlayer(HeadE.CALM_TALK, "Another what?");
					addNPC(NPC, HeadE.CHILD_FRUSTRATED, "Another 'archaeologist'. I'm not going to let you plunder my master's tomb you know.");
					addPlayer(HeadE.HAPPY_TALKING, "That's a shame. Have you got anything else I could do while I'm here?");
					addNPC(NPC, HeadE.CHILD_FRUSTRATED, "If it will keep you out of mischief I suppose I could set something up for you... I have a few rooms " +
							"full of some things you humans might consider valuable, do you want to give it a go?");
					addOptions("Play OuterPyramidHandler Plunder?", new Options() {
						@Override
						public void create() {
							option("That sounds like fun; what do I do?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "That sounds like fun; what do I do?")
									.addNPC(NPC, HeadE.CHILD_FRUSTRATED, "You have five minutes to explore the treasure rooms and collect as many artefacts as " +
											"you can. The artefacts are in the urns, chests and sarcophagi found in each room.")
									.addNPC(NPC, HeadE.CHILD_FRUSTRATED, "There are eight treasure rooms, each subsequent room requires higher thieving skills to" +
											" both enter the room and thieve from the urns and other containers")
									.addNPC(NPC, HeadE.CHILD_FRUSTRATED, "The rewards also become more lucrative the further into the tomb you go. You will also have" +
											" to deactivate a trap in order to enter the main part of each room. ")
									.addNPC(NPC, HeadE.CHILD_FRUSTRATED, "When you want to move onto the next room you need to find the correct door first. " +
											"There are four possible exits... you must open the door before finding out whether it is the exit or not.")
									.addNPC(NPC, HeadE.CHILD_FRUSTRATED, "Opening the doors require picking their locks. Having a lockpick will make this easier.")
									.addNPC(NPC, HeadE.CHILD_FRUSTRATED, "Do you want to do it?")
									.addOptions("Would you like to start?", new Options() {
										@Override
										public void create() {
											option("I am ready to give it a go now.", new Dialogue()
													.addNext(()->{
														p.getControllerManager().startController(new PyramidPlunderController());
													})
													);
											option("I think I will wait a little", new Dialogue()
													.addPlayer(HeadE.HAPPY_TALKING, "I think I will wait")
													);
										}
									})
									);
							option("Not right now.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Not right now.")
									.addNPC(NPC, HeadE.CHILD_FRUSTRATED, "Well, get out of here then.")
									);
							option("I know what I'm doing, so let's get on with it.", new Dialogue()
									.addNext(()->{
										p.getControllerManager().startController(new PyramidPlunderController());
									}));
						}
					});
					create();
				}
			});
		if(e.getOption().equalsIgnoreCase("start-minigame"))
			p.getControllerManager().startController(new PyramidPlunderController());
	});
}
