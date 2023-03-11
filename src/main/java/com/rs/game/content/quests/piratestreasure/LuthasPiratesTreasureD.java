package com.rs.game.content.quests.piratestreasure;

import static com.rs.game.content.quests.piratestreasure.PiratesTreasure.*;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class LuthasPiratesTreasureD extends Conversation {
	private static final int UNEMPLOYED = 0;
	private static final int EMPLOYED = 1;
	private static final int JOB_FINISHED = 2;

	public LuthasPiratesTreasureD(Player player) {
		super(player);
		switch (player.getQuestManager().getStage(Quest.PIRATES_TREASURE)) {
			case SMUGGLE_RUM -> {
				if(player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("BANANA_COUNT") >= 10)
					player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setI("LUTHAS_EMPLOYMENT", JOB_FINISHED);
				if(player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("LUTHAS_EMPLOYMENT") == UNEMPLOYED) {
					addNPC(LUTHAS, HeadE.CALM_TALK, "Hello I'm Luthas, I run the banana plantation here");
					addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("Could you offer me employment on your plantation?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "Could you offer me employment on your plantation?")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "Yes, I can sort something out. There's a crate ready to be loaded onto the ship. " +
											"You wouldn't believe the demand for bananas from Wydin's shop over in Port Sarim. ")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "I think this is the third crate I've shipped him this month.. If you could fill it up with " +
											"bananas, I'll pay you 30 gold.", ()-> {
												player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setI("LUTHAS_EMPLOYMENT", EMPLOYED);
											})
									);
							option("That customs officer is annoying isn't she?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "That customs officer is annoying isn't she?")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "Well I know her pretty well. She doesn't cause me any trouble any more. She doesn't even " +
											"search my export crates any more. She knows they only contain bananas.")
									.addPlayer(HeadE.HAPPY_TALKING, "Really? How interesting. Whereabouts do you send those to?")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "There is a little shop over in Port Sarim that buys them up by the crate. I believe" +
											" it is run by a man called Wydin."));
						}
					});
				} else if(player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("LUTHAS_EMPLOYMENT") == EMPLOYED) {
					addNPC(LUTHAS, HeadE.CALM_TALK, "Have you completed your task yet?");
					addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("What did I have to do again?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "What did I have to do again?")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "There's a crate ready to be loaded onto the ship. If you could fill it up with bananas, " +
											"I'll pay you 30 gold.")
									);
							option("No, the crate isn't full yet...", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "No, the crate isn't full yet...")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "Well come back when it is.")
									);
							option("So where are these bananas going to be delivered to?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "So where are these bananas going to be delivered to?")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "I sell them to Wydin who runs a grocery store in Port Sarim.")
									);
							option("That customs officer is annoying isn't she?", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "That customs officer is annoying isn't she?")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "Well I know her pretty well. She doesn't cause me any trouble any more. She doesn't even " +
											"search my export crates any more. She knows they only contain bananas.")
									.addPlayer(HeadE.HAPPY_TALKING, "Really? How interesting. Whereabouts do you send those to?")
									.addNPC(LUTHAS, HeadE.CALM_TALK, "There is a little shop over in Port Sarim that buys them up by the crate. I believe" +
											" it is run by a man called Wydin."));
						}
					});
				} else if(player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("LUTHAS_EMPLOYMENT") == JOB_FINISHED) {
					addPlayer(HeadE.HAPPY_TALKING, "I've filled a crate with bananas.");
					addNPC(LUTHAS, HeadE.CALM_TALK, "Well done, here's your payment");
					addSimple("Luthas hands you 30 coins.", ()->{
						player.getInventory().addCoins(30);
						player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).removeI("BANANA_COUNT");
						player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).removeI("LUTHAS_EMPLOYMENT");
						if(player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("RUM_IN_KARAMJA_CRATE")) {
							player.sendMessage("The rum was sent along with the bananas!");
							player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB("RUM_IN_SARIM_CRATE", true);
							player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).removeB("RUM_IN_KARAMJA_CRATE");
						}
					});
				}

			}
			default -> {
				addNPC(LUTHAS, HeadE.CALM_TALK, "Hi.");
				addPlayer(HeadE.HAPPY_TALKING, "Hi, how are you?");
				addNPC(LUTHAS, HeadE.CALM_TALK, "Great, how about yourself?");
				addPlayer(HeadE.HAPPY_TALKING, "Good...");
				addNPC(LUTHAS, HeadE.CALM_TALK, "Great.");
				addPlayer(HeadE.HAPPY_TALKING, "Great");
				addNPC(LUTHAS, HeadE.CALM_TALK, "...");
				addPlayer(HeadE.HAPPY_TALKING, "...");
			}
		}
	}

	public static ObjectClickHandler handleBananaCrate = new ObjectClickHandler(new Object[] { 2072 }, e -> {
		Player p = e.getPlayer();
		if(p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("LUTHAS_EMPLOYMENT") == UNEMPLOYED)
			return;
		int bananaCount = p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("BANANA_COUNT");
		boolean hasRum = p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("RUM_IN_KARAMJA_CRATE");
		if(e.getOption().equalsIgnoreCase("search"))
			p.sendMessage("The crate has " + bananaCount + " out of 10 bananas " + (hasRum ? "and has rum!" : "but does not have rum!"));
		if(e.getOption().equalsIgnoreCase("fill")) {
			if(p.getInventory().containsItem(RUM, 1)) {
				p.getInventory().removeItems(new Item(RUM, 1));
				p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB("RUM_IN_KARAMJA_CRATE", true);
				p.sendMessage("You place rum in the crate!");
			}
			while(p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("BANANA_COUNT") < 10) {
				if(!p.getInventory().containsItem(BANANA, 1))
					break;
				p.getInventory().removeItems(new Item(BANANA, 1));
				p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setI("BANANA_COUNT",
						p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getI("BANANA_COUNT")+1);
			}
		}
	});

	public static NPCClickHandler handleLuthas = new NPCClickHandler(new Object[] { LUTHAS }, e -> e.getPlayer().startConversation(new LuthasPiratesTreasureD(e.getPlayer()).getStart()));
}
