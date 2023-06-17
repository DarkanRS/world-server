package com.rs.game.content.world.areas.tzhaar.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.minigames.fightpits.FightPits;
import com.rs.game.ge.GE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler

public class TzHaarMejKah extends Conversation {
	private static final int npcId = 2618;

	public static NPCClickHandler TzHaarMejKah = new NPCClickHandler(new Object[]{npcId}, e -> {
		switch(e.getOption()) {
		case "Bank":
			e.getPlayer().getBank().open();
			break;
		case "Collect":
			GE.openCollection(e.getPlayer());
			break;
		case "Talk-to":
			e.getPlayer().startConversation(new TzHaarMejKah(e.getPlayer()));
			break;
		}
	});

	public TzHaarMejKah(Player player) {
		super(player);
		addNPC(npcId, HeadE.SKEPTICAL_HEAD_SHAKE, "You want help JalYt-Ket-" + player.getDisplayName() + "?");
		addOptions("Choose an option:", ops -> {
			ops.add("What is this place?")
				.addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "What is this place?")
				.addNPC(npcId, HeadE.CALM_TALK, "This is the Fight Pit. TzHaar-Xil made it for their sport but many JalYt come here to fight, too. If you are wanting to fight then enter the cage, you will be summoned when next round is ready to begin.")
				.addOptions("Choose an option:", op2 -> {
					op2.add("Are there any rules?")
						.addPlayer(HeadE.HAPPY_TALKING, "Are there any rules?")
						.addNPC(npcId, HeadE.CALM_TALK, "No rules, you use whatever you want. Last person standing wins and is declared champion, they stay in the pit for next fight.")
						.addOptions("Choose an option:", op3 -> {
							op3.add("Do I win anything?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Do I win anything?")
								.addNPC(npcId, HeadE.CALM_TALK, "You ask a lot questions.<br>Champion gets TokKul as reward, more fights the more TokKul they get.")
								.addPlayer(HeadE.HAPPY_TALKING, "...")
								.addNPC(npcId, HeadE.CALM_TALK, "Before you ask, TokKul is like your coins.")
								.addNPC(npcId, HeadE.CALM_TALK, "Gold is like you JalYt, soft and easily broken, we use hard rock forged in fire like TzHaar!"));

							op3.add("Sounds good.");
						});
					op2.add("Ok thanks.");
				});

			ops.add("Who's the current champion?", new Dialogue()
				.addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Who's the current champion?")
				.addNPC(npcId, HeadE.CALM_TALK, "Ah that would be Y'Haar-Mej-" + (FightPits.currentChampion == null ? "none" : FightPits.currentChampion) + "!"));

			ops.add("What did you call me?", new Dialogue()
				.addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "What did you call me?")
				.addNPC(npcId, HeadE.CALM_TALK, "Are you not a JalYt-Ket?")
				.addOptions("Choose an option:", op2 -> {
					op2.add("What's a 'JalYt-Ket'?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "What's a 'JalYt-Ket'?")
						.addNPC(npcId, HeadE.CALM_TALK, "That what you are...you tough and strong, no?")
						.addNPC(npcId, HeadE.CALM_TALK, "Well, yes I suppose I am...")
						.addNPC(npcId, HeadE.CALM_TALK, "Then you JalYt-Ket!")
						.addOptions("Choose an option:", op3 -> {
							op3.add("What are you then?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What are you then?")
								.addNPC(npcId, HeadE.CALM_TALK, "Foolish JalYt, I am TzHaar-Mej one of the mystics of this city.")
								.addOptions("Choose an option:", op4 -> {
									op4.add("What other types are there?", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "What other types are there?")
										.addNPC(npcId, HeadE.CALM_TALK, "There are the mighty TzHaar-Key who guard us, the swift TzHaar-Xil who hunt for our food, and the skilled TzHaar-Hur who craft our homes and tools.")
										.addPlayer(HeadE.HAPPY_TALKING, "Awesome..."));
									op4.add("Ah ok then.", new Dialogue()
										.addPlayer(HeadE.HAPPY_TALKING, "Ah ok then.")
										.addNPC(npcId, HeadE.CALM_TALK, "..."));
								}));
							op3.add("Thanks for explaining it.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Thanks for explaining it.")
								.addNPC(npcId, HeadE.CALM_TALK, "..."));
						}));
					op2.add("I guess so...?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I guess so...?")
						.addNPC(npcId, HeadE.CALM_TALK, "..."));
					op2.add("No I'm not!", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "No I'm not!")
						.addNPC(npcId, HeadE.CALM_TALK, "..."));
				}));
			ops.add("No I'm fine thanks.", new Dialogue());
		});

		create();
	}
}
