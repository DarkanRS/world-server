package com.rs.game.content.quests.fightarena.dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class AngorFightArenaD extends Conversation {
	private static final int NPC = 259;
	public AngorFightArenaD(Player player) {
		super(player);
		addPlayer(HeadE.HAPPY_TALKING, "Hello.");
		addNPC(NPC, HeadE.CALM_TALK, "Hi, what can I get you? We have a range of quality brews");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(player.getInventory().hasCoins(5)) {
					option("I'll have a beer, please.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'll have a beer, please.")
							.addNPC(NPC, HeadE.CALM_TALK, "That will be 5 coins...")
							.addPlayer(HeadE.HAPPY_TALKING, "Okiedokie")
							.addSimple("He passes you a beer.", ()->{
								player.getInventory().removeCoins(5);
								player.getInventory().addItem(1917, 1);
							})
					);
					option(" I'd like a Khali brew please.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, " I'd like a Khali brew please.")
							.addNPC(NPC, HeadE.CALM_TALK, "That will be 5 coins...")
							.addPlayer(HeadE.HAPPY_TALKING, "Okiedokie")
							.addSimple("He passes you a Khali brew.", ()->{
								player.getInventory().removeCoins(5);
								player.getInventory().addItem(77, 1);
							})
					);
				} else {
					option("I'll have a beer, please.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "I'll have a beer, please.")
							.addNPC(NPC, HeadE.CALM_TALK, "That will be 5 coins...")
							.addSimple("You don't have enough coins...")
					);
					option(" I'd like a Khali brew please.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, " I'd like a Khali brew please.")
							.addNPC(NPC, HeadE.CALM_TALK, "That will be 5 coins...")
							.addSimple("You don't have enough coins...")
					);
				}
				option("Got any news?", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Got any news?")
						.addNPC(NPC, HeadE.CALM_TALK, "Nothing fresh, just a bunch of drunks around here...")
						.addPlayer(HeadE.CALM_TALK, "Umm, thanks.")
				);
			}
		});
		create();
	}

    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new AngorFightArenaD(e.getPlayer())));
}
