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
package com.rs.game.content.holidayevents.halloween.hw09;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GrimReaper2009D extends Conversation {

	public static NPCClickHandler handleGrimTalk = new NPCClickHandler(new Object[] { 8977 }, e -> {
		e.getNPC().resetDirection();
		e.getPlayer().startConversation(new GrimReaper2009D(e.getPlayer()));
	});

	public GrimReaper2009D(Player player) {
		super(player);

		switch(player.getI(Halloween2009.STAGE_KEY)) {
		case -1:
			addNPC(8867, HeadE.CALM_TALK, "Welcome to the house of Death, mortal.");
			addOptions(new Options() {
				@Override
				public void create() {
					Dialogue mainline = new Dialogue();

					option("Hello sir, um, Your Highness, um...", new Dialogue()
							.addPlayer(HeadE.SCARED, "Hello sir, um, Your Highness, um...")
							.addNPC(8867, HeadE.CALM_TALK, "I need no title.")
							.addNext(mainline));
					option("Hey Death, how's it hanging?", new Dialogue()
							.addPlayer(HeadE.CHEERFUL, "Hey Death, how's it hanging?")
							.addNPC(8867, HeadE.CALM_TALK, "I have not seen a good hanging for some time...but there are other ways for people to meet me.")
							.addNext(mainline));

					mainline.addNPC(8867, HeadE.CALM_TALK, "I have summoned you here because I deem it necessary.")
					.addNPC(8867, HeadE.CALM_TALK, "I have a task for you. A task of grave importance. A task that will shake you to your very core.")
					.addOptions(new Options() {
						@Override
						public void create() {
							option("What is this task?", new Dialogue()
									.addPlayer(HeadE.CONFUSED, "What is this task?")
									.addNPC(8867, HeadE.CALM_TALK, "There is...")
									.addNPC(8867, HeadE.CALM_TALK, "...a spider in my bathtub.")
									.addOptions(new Options("taskResponses", GrimReaper2009D.this) {
										@Override
										public void create() {
											option("You're scared of spiders? Ha!", new Dialogue()
													.addPlayer(HeadE.LAUGH, "You're scared of spiders? Ha!")
													.addNPC(8867, HeadE.ANGRY, "Silence mortal!")
													.addNPC(8867, HeadE.ANGRY, "I am the Grim Reaper, the incarnation of Death, and I cannot be frightened. Besides, this spider is enormous.")
													.addGotoStage("taskResponses", GrimReaper2009D.this));
											option("Why don't you kill it?", new Dialogue()
													.addPlayer(HeadE.CONFUSED, "Why don't you kill it?")
													.addNPC(8867, HeadE.CALM_TALK, "I do not wish to kill a creature whose time to die has not come. However, I cannot touch it without killing it. This is why I need you")
													.addGotoStage("taskResponses", GrimReaper2009D.this));
											option("Don't you have a servant to deal with this?", new Dialogue()
													.addPlayer(HeadE.CONFUSED, "Don't you have a servant to deal with this?")
													.addNPC(8867, HeadE.CALM_TALK, "I have lost too many servants over the years,")
													.addNPC(8867, HeadE.CALM_TALK, "One accidental touch and I must begin recruitment anew. Now I live alone and bring in help only when I need it. Such as now.")
													.addGotoStage("taskResponses", GrimReaper2009D.this));
											option("I'll deal with the spider.", new Dialogue()
													.addPlayer(HeadE.CALM_TALK, "I'll deal with the spider.")
													.addNPC(8867, HeadE.CALM_TALK, "Good. You will find the bathroom on the upper floor.", () -> {
														player.save(Halloween2009.STAGE_KEY, 1);
													}));
											option("I'm not doing your chores!", new Dialogue()
													.addPlayer(HeadE.CHUCKLE, "I'm not doing your chores!")
													.addNPC(8867, HeadE.CALM_TALK, "Then begone, mortal."));
										}
									}));
							option("I'm not doing your chores!", new Dialogue()
									.addPlayer(HeadE.CHUCKLE, "I'm not doing your chores!")
									.addNPC(8867, HeadE.CALM_TALK, "Then begone, mortal."));
						}
					});
				}
			});
			break;
		case 1:
			addPlayer(HeadE.CALM_TALK, "Hey Grim. I'm back.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.CALM_TALK, "I'm sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. Go get that massive spider out of my bathtub.");
			addPlayer(HeadE.CALM_TALK, "Thank you.");
			break;
		case 2:
			addPlayer(HeadE.CALM_TALK, "The spider says it was sent by the Spider Queen, and it will only leave if the Spider Queen tells it to.");
			addNPC(8867, HeadE.CALM_TALK, "That is...concerning.");
			addNPC(8867, HeadE.CALM_TALK, "You must meet with the Spider Queen and ask her to remove the spider from my bath. I will open a portal for you.");
			addNext(() -> player.save(Halloween2009.STAGE_KEY, 3));
			break;
		case 3:
		case 4:
		case 5:
			addPlayer(HeadE.CALM_TALK, "Hey Grim. I'm back.");
			addNPC(8867, HeadE.CALM_TALK, "Evidently.");
			addPlayer(HeadE.CALM_TALK, "I'm sorry, but could you just tell me again what you want me to do?");
			addNPC(8867, HeadE.CALM_TALK, "Repetition should not be necessary here. Go through the portal I opened for you and speak with the Spider Queen.");
			addPlayer(HeadE.CALM_TALK, "Thank you.");
			break;
		case 6:
			addNPC(8867, HeadE.CALM_TALK, "You have returned, mortal. How went your visit to the Spider Realm?");
			addNext(new SpiderStatement("Ooh! Are you really the Grim Reaper?"));
			addNPC(8867, HeadE.CALM_TALK, "Umm...yes.");
			addNext(new SpiderStatement("Wow! You're all bones and stuff!"));
			addNPC(8867, HeadE.CALM_TALK, "Umm...quite. And who might you be?");
			addNext(new SpiderStatement("Eek!"));
			addNPC(8867, HeadE.CALM_TALK, "I am sorry. I know my appearance can be frightening.");
			addNext(new SpiderStatement("Ha ha ha ha ha ha ha!"));
			addNPC(8867, HeadE.CONFUSED, player.getDisplayName()+", who is this spider?");
			addPlayer(HeadE.CALM_TALK, "This is Eek the Spider.");
			addNPC(8867, HeadE.CALM_TALK, "Ah, I see.");
			addNext(new SpiderStatement("Ha ha ha ha ha ha ha!"));
			addNPC(8867, HeadE.CALM_TALK, "Since you have returned with a companion, can I assume your trip met with some success?");
			addNext(new SpiderStatement("We've got to spin webs in your house. All over it!"));
			addPlayer(HeadE.CALM_TALK, "The Spider Queen will remove the spider from your bath if you let Eek the Spider put webs in your house.");
			addNPC(8867, HeadE.CALM_TALK, "Ah, I see.");
			addNPC(8867, HeadE.CALM_TALK, "She is right. Guthix decrees that my house be full of cobwebs, especially at Hallowe'en.");
			addNPC(8867, HeadE.CALM_TALK, "Very well. You have the freedom of my house. Please spin webs over whatever Eek the Spider wishes.");
			addNext(new SpiderStatement("Hold me in your hand, "+player.getDisplayName()+"! Then we'll see what we can web up!"), () -> {
				player.save(Halloween2009.STAGE_KEY, 7);
				Halloween2009.refreshWebbables(player, player.getEquipment().getWeaponId() == 15353);
			});
			break;
		case 7:
			addNPC(8867, HeadE.CALM_TALK, "Have you finished spinning cobwebs on my furniture?");
			addPlayer(HeadE.CALM_TALK, "Not yet, but we're working on it.");
			addNext(new SpiderStatement("Yeah, we're all over it!"));
			break;
		case 8:
			addNPC(8867, HeadE.CALM_TALK, "Have you finished spinning cobwebs on my furniture?");
			addNext(new SpiderStatement("Yes! All done! All done!"));
			addNPC(8867, HeadE.CALM_TALK, "Then please return to the Spider Queen and tell her that you are done here, so that she can tell the spider to move from my bath.", () -> {
				player.save(Halloween2009.STAGE_KEY, 9);
			});
			break;
		case 9:
			addNPC(8867, HeadE.CALM_TALK, "Have you spoken with the Queen about getting that spider gone yet?");
			addPlayer(HeadE.CALM_TALK, "Not yet, but I'm on my way.");
			break;
		case 10:
			addNPC(8867, HeadE.CALM_TALK, "Tremble, mortal! You have the gratitude...of Death!");
			addOptions(new Options() {
				@Override
				public void create() {
					option("You were scarier before I was running errands for you.", new Dialogue()
							.addPlayer(HeadE.CONFUSED, "You were scarier before I was running errands for you.")
							.addNPC(8867, HeadE.CALM_TALK, "It matters not whether you are frightened. Some mortals fear death; some do not. They all meet me sooner or later."));
					option("Are you going to leave all these webs up?", new Dialogue()
							.addPlayer(HeadE.CONFUSED, "Are you going to leave all these webs up?")
							.addNPC(8867, HeadE.CALM_TALK, "Once Hallowe'en is done, I will clear some of them away. I will make sure not to offend the Spider Queen again, though."));
					option("I think the Spider Queen was being unreasonable.", new Dialogue()
							.addPlayer(HeadE.CONFUSED, "I think the Spider Queen was being unreasonable.")
							.addNPC(8867, HeadE.CALM_TALK, "I am not disagreeing with you, but she is a powerful creature and it is wise not to offend her."));
				}
			});
			break;
		}

		create();
	}

}
