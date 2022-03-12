package com.rs.game.player.quests.handlers.monksfriend.dialogues;

import static com.rs.game.player.quests.handlers.monksfriend.MonksFriend.HELP_CEDRIC;
import static com.rs.game.player.quests.handlers.monksfriend.MonksFriend.RETURN_TO_OMAD;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class BrotherCedricMonksFriendD extends Conversation {
	private static final int NPC = 280;

	public BrotherCedricMonksFriendD(Player p) {
		super(p);
		switch (p.getQuestManager().getStage(Quest.MONKS_FRIEND)) {
			case HELP_CEDRIC -> {
				if (p.getQuestManager().getAttribs(Quest.MONKS_FRIEND).getB("cedric_drank_water")) {
					if (p.getInventory().containsItem(1511, 1)) {
						addNPC(NPC, HeadE.CALM_TALK, "Now I just need to fix this cart and we can go party.");
						addNPC(NPC, HeadE.CALM_TALK, "Could you help and bring me logs?");
						addPlayer(HeadE.HAPPY_TALKING, "I have some right here...");
						addSimple("You give him the logs", () -> {
							p.getInventory().removeItems(new Item(1511, 1));
							p.getQuestManager().setStage(Quest.MONKS_FRIEND, RETURN_TO_OMAD);
						});
						addNPC(NPC, HeadE.CALM_TALK, "Well done! Now I'll fix this cart. You head back to Brother Omad and tell him I'll be there soon.");
						addPlayer(HeadE.HAPPY_TALKING, "Ok! I'll see you later!");

					} else {
						addNPC(NPC, HeadE.CALM_TALK, "Now I just need to fix this cart and we can go party.");
						addNPC(NPC, HeadE.CALM_TALK, "Do you have some wood?");
						addPlayer(HeadE.FRUSTRATED, "No I'm afraid.");
					}
					return;
				}

				if (p.getQuestManager().getAttribs(Quest.MONKS_FRIEND).getB("cedric_needs_water")) {
					if (p.getInventory().containsItem(1937, 1)) {
						addPlayer(HeadE.HAPPY_TALKING, "Are you okay?");
						addNPC(NPC, HeadE.CALM_TALK, "Hic up! Oh my head! I need a jug of water.");
						addPlayer(HeadE.HAPPY_TALKING, "Cedric! Here, drink! I have some water.");
						addNPC(NPC, HeadE.CALM_TALK, "Good stuff, my head's spinning!");
						addNPC(NPC, HeadE.CALM_TALK, "Aah! That's better!", () -> {
							p.getInventory().removeItems(new Item(1937, 1));
							p.getQuestManager().getAttribs(Quest.MONKS_FRIEND).setB("cedric_drank_water", true);
						});
						addNPC(NPC, HeadE.CALM_TALK, "Now I just need to fix this cart and we can go party.");
						addNPC(NPC, HeadE.CALM_TALK, "Can you get me som wood?");
						addPlayer(HeadE.FRUSTRATED, "*Sigh");
					} else {
						addNPC(NPC, HeadE.DRUNK, "Do you have a jug of *hic water?");
						addPlayer(HeadE.FRUSTRATED, "Umm no...");
					}
					return;
				}
				addPlayer(HeadE.CALM_TALK, "Brother Cedric are you okay?");
				addNPC(NPC, HeadE.DRUNK, "Yeesshhh, I'm very, very drunk..hic..up..");
				addPlayer(HeadE.SKEPTICAL_THINKING, "Brother Omad needs the wine for the party.");
				addNPC(NPC, HeadE.DRUNK, "Oh dear, oh dear, I knew I had to do something!");
				addNPC(NPC, HeadE.DRUNK, "Hic up! Oh my head! I need a jug of water.");
				addPlayer(HeadE.FRUSTRATED, "I'll see if I can get some.", () -> {
					p.getQuestManager().getAttribs(Quest.MONKS_FRIEND).setB("cedric_needs_water", true);
				});
				addNPC(NPC, HeadE.DRUNK, "Thanks! *hic*");
			}

			case RETURN_TO_OMAD -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello Cedric.");
				addNPC(NPC, HeadE.CALM_TALK, "Hi, I'm almost done here. Could you tell Omad that I'll be back soon?");
			}

			default -> {
				addPlayer(HeadE.CALM, "Hello.");
				addNPC(NPC, HeadE.DRUNK, "Honey, money, woman and wine!");
				addPlayer(HeadE.SECRETIVE, "Are you ok?");
				addNPC(NPC, HeadE.DRUNK, "Yesshh...hic up...beautiful!");
				addPlayer(HeadE.CALM_TALK, "Take care old monk.");
				addNPC(NPC, HeadE.DRUNK, "La..di..da..hic..up!");
			}
		}
	}
}
