// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.quests.princealirescue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.princealirescue.PrinceAliRescue.*;

@PluginEventHandler
public class LeelaPrinceAliRescueD extends Conversation {
	public final static int LEELA = 915;

	public LeelaPrinceAliRescueD(Player player) {
		super(player);
		if(player.getQuestManager().getStage(Quest.PRINCE_ALI_RESCUE) <= PrinceAliRescue.STARTED) {
			addPlayer(HeadE.HAPPY_TALKING, "What are you waiting here for?");
			addNPC(LEELA, HeadE.FRUSTRATED, "That is no concern of yours, adventurer.");
		}
		if(player.getQuestManager().getStage(Quest.PRINCE_ALI_RESCUE) == PrinceAliRescue.GEAR_CHECK)
			//bronze key complete
			if(player.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).getB("Leela_has_key") && !player.getInventory().containsItem(PrinceAliRescue.BRONZE_KEY, 1)) {
				if(player.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).getB("Leela_gave_key")) {
					addNPC(LEELA, HeadE.CALM_TALK, "You lost the key?");
					addNPC(LEELA, HeadE.CALM_TALK, "I am going to need 15 coins from you to pay for the bronze.");
					if(player.getInventory().hasCoins(15))
						addSimple("Leela gives you a copy of the key to the prince's door.", () -> {
							player.getInventory().removeCoins(15);
							player.getInventory().addItem(PrinceAliRescue.BRONZE_KEY, 1);
						});
					else {
						addNPC(LEELA, HeadE.CALM_TALK, "Do you have that?");
						addPlayer(HeadE.SAD, "No...");
					}
				}
				else {
					addNPC(LEELA, HeadE.CALM_TALK, "My father sent this key for you. Be careful not to lose it.");
					addSimple("Leela gives you a copy of the key to the prince's door.", () -> {
						player.getInventory().addItem(PrinceAliRescue.BRONZE_KEY, 1);
						player.getQuestManager().getAttribs(Quest.PRINCE_ALI_RESCUE).setB("Leela_gave_key", true);
					});
					addNPC(LEELA, HeadE.CALM_TALK, "Don't forget to deal with the guard on the door. He is talkative, try to find a weakness in him.");
				}
			} else if(player.getInventory().containsItem(PrinceAliRescue.KEY_PRINT, 1)) {
				addNPC(LEELA, HeadE.CALM_TALK, "You can give the key print and a bronze bar to Osman in Al-kharid.");
				addNPC(LEELA, HeadE.CALM_TALK, "Then come back to me to get the key");
			}
			else if(player.getInventory().containsItem(BEER, 3) && player.getInventory().containsItem(BRONZE_KEY, 1) &&
					player.getInventory().containsItem(PASTE, 1) && player.getInventory().containsItem(BLONDE_WIG, 1) &&
					player.getInventory().containsItem(PINK_SKIRT, 1) && player.getInventory().containsItem(ROPE, 1)) {
				addNPC(LEELA, HeadE.HAPPY_TALKING, "You have everything you need to start the breakout.");
				addNPC(LEELA, HeadE.HAPPY_TALKING, "Don't forget the plan");
				addNPC(LEELA, HeadE.SECRETIVE, "Get Joe the guard drunk with 3 beers, tie Lady Keli up with a rope, open the prison with the jail key" +
						", put the disguise on Prince Ali and escape.");
				addNPC(LEELA, HeadE.HAPPY_TALKING, "Got it?");
				addPlayer(HeadE.HAPPY_TALKING, "Got it.");
			}
			else {
				addPlayer(HeadE.HAPPY_TALKING, "I am here to help you free the prince.");
				addNPC(LEELA, HeadE.CALM_TALK, "Your employment is known to me. Now, do you know all that we need to make the break?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I must make a disguise. What do you suggest?", new Dialogue()
								.addNPC(LEELA, HeadE.CALM_TALK, "Only the lady Keli, can wander about outside the jail. The guards will shoot to kill if " +
										"they see the prince out so we need a disguise good enough to fool them at a distance.")
								.addNPC(LEELA, HeadE.CALM_TALK, "You need a wig, maybe made from wool. If you find someone who can work with wool ask " +
										"them about it. There's a witch nearby may be able to help you dye it.")
								.addNPC(LEELA, HeadE.CALM_TALK, "We will need a skirt like hers, a pink one should be fine.")
								.addNPC(LEELA, HeadE.CALM_TALK, "We still need something to colour the Prince's skin lighter. There's a witch close to here. " +
										"She knows about many things. She may know some way to make the skin lighter.")
								.addNPC(LEELA, HeadE.CALM_TALK, "You have rope I see, to tie up Keli. That will be the most dangerous part of the plan."));
						option("I need to get the key made.", new Dialogue()
								.addNPC(LEELA, HeadE.CALM_TALK, "Yes, that is most important. There is no way you can get the real key. It is on a chain around " +
										"Keli's neck. Almost impossible to steal.")
								.addNPC(LEELA, HeadE.CALM_TALK, "Get some soft clay and get her to show you the key somehow. Then take the print, with bronze, to my father."));
						option("What can I do with the guards?", new Dialogue()
								.addNPC(LEELA, HeadE.CALM_TALK, "Most of the guards will be easy. The disguise will get past them. The only guard who will be " +
										"a problem will be the one at the door.")
								.addNPC(LEELA, HeadE.CALM_TALK, "We can discuss this more when you have the rest of the escape kit."));
						option("I will go and get the rest of the escape equipment.", new Dialogue()
								.addNPC(LEELA, HeadE.CALM_TALK, "Good, I shall await your return with everything."));
					}
				});


			}

		if(player.isQuestComplete(Quest.PRINCE_ALI_RESCUE))
			addNPC(LEELA, HeadE.HAPPY_TALKING, "Thank you, Al-Kharid will forever owe you for your help. I think that if there is ever anything that " +
					"needs to be done, you will be someone they can rely on.");

	}

	public static NPCClickHandler handleLeela = new NPCClickHandler(new Object[] { LEELA }, e -> e.getPlayer().startConversation(new LeelaPrinceAliRescueD(e.getPlayer()).getStart()));
}

