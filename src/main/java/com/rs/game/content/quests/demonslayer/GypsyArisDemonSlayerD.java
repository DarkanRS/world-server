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
package com.rs.game.content.quests.demonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class GypsyArisDemonSlayerD extends Conversation {
	private final int GYPSY_ARIS = 882;

	public GypsyArisDemonSlayerD(Player player) {
		super(player);

		switch (player.getQuestManager().getStage(Quest.DEMON_SLAYER)) {
		case DemonSlayer.NOT_STARTED_STAGE:
			if(player.getTempAttribs().getB("DemonSlayerCutscenePlayed")) {
				afterCutsceneConvo(player);
				break;
			}
			addNPC(GYPSY_ARIS, HeadE.CALM_TALK, "Hello young one.");
			addNPC(GYPSY_ARIS, HeadE.CALM_TALK, "Cross my palm with silver and the future will be revealed to you.");
			addOptions("Cross her palm?", new Options() {
				@Override
				public void create() {
					option("Yes.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Okay, here you go.")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Come closer, and listen carefully to what the future holds for you, as I peer into the " +
									"swirling mists of the crystal ball.")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "I can see images forming. I can see you.")
							.addNPC(GYPSY_ARIS, HeadE.NERVOUS, "You are holding a very impressive looking sword. I'm sure I recognise that sword...")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "There is a big dark shadow appearing now.")
							.addNPC(GYPSY_ARIS, HeadE.AMAZED, "Aaargh!")
							.addPlayer(HeadE.SKEPTICAL, "Are you all right?")
							.addNPC(GYPSY_ARIS, HeadE.SCARED, "It's Delrith! Delrith is coming!")
							.addPlayer(HeadE.SKEPTICAL, "Who's Delrith?")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Delrith...")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Delrith is a powerful demon.")
							.addNPC(GYPSY_ARIS, HeadE.SCARED, "Oh! I really hope he didn't see me looking at him through my crystal ball!")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "He tried to destroy this city 150 years ago. He was stopped just in time by the great hero Wally.")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Using his magic sword Silverlight, Wally managed to trap the demon in the stone circle just" +
									" south of this city.")
							.addNPC(GYPSY_ARIS, HeadE.AMAZED, "Ye gods! Silverlight was the sword you were holding in my vision! You are the one destined to " +
									"stop the demon this time.")
							.addNext(() -> {
								player.startConversation(new GypsyArisDemonSlayerD(player, 0).getStart());
							}));
					option("No.", new Dialogue()
							.addPlayer(HeadE.SKEPTICAL, "No, I don't believe in that stuff")
							.addNPC(GYPSY_ARIS, HeadE.CALM_TALK, "Ok suit yourself."));
				}
			});
			break;
		case DemonSlayer.AFTER_GYPSY_ARIS_INTRO_STAGE:
		case DemonSlayer.AFTER_SIR_PRYSIN_INTRO_STAGE:
		case DemonSlayer.KEY1_DRAIN_LOCATION_KNOWN_STAGE:
		case DemonSlayer.KEY2_WIZARD_LOCATION_KNOWN_STAGE:
		case DemonSlayer.KEY3_ROVIN_LOCATION_KNOWN_STAGE:
		case DemonSlayer.SILVERLIGHT_OBTAINED_STAGE:
			addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Greetings. How goes thy quest?");
			addPlayer(HeadE.WORRIED, "I'm still working on it.");
			addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Well if you need any advice I'm always here, young one.");
			afterQuestStartConvo(player);
			break;
		case DemonSlayer.QUEST_COMPLETE_STAGE:
			addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Thank you for saving us from that demon.");
			break;
		}
	}

	public GypsyArisDemonSlayerD(Player player, int convoID) {
		super(player);

		switch(convoID) {
		case 0:
			introductoryOptions(player);
			break;
		case 1:
			afterCutsceneConvo(player);
			break;
		case 2:
			afterQuestStartConvo(player);
			break;

		}

	}

	private void introductoryOptions(Player p) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("How am I meant to fight a demon who can destroy cities?", new Dialogue()
						.addPlayer(HeadE.SCARED, "How am I meant to fight a demon who can destroy cities?!")
						.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "If you face Delrith while he is still weak from being summoned, and use the correct weapon, you will " +
								"not find the task too arduous.")
						.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Do not fear. If you follow the path of the great hero Wally, then you are sure to defeat the demon.")
						.addNext(() -> {
							p.startConversation(new GypsyArisDemonSlayerD(p, 0).getStart());
						}));
				option("Okay, where is he? I'll kill him for you!", new Dialogue()
						.addPlayer(HeadE.ANGRY, "Okay, where is he? I'll kill him for you!")
						.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Ah, the overconfidence of the young!")
						.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Delrith can't be harmed by ordinary weapons. You must face him using the same weapon that Wally used.")
						.addNext(() -> {
							p.startConversation(new GypsyArisDemonSlayerD(p, 0).getStart());
						}));

				if(p.getTempAttribs().getB("DemonSlayerCutscenePlayed")) {
					option("What is the magical incantation?", new Dialogue()
							.addPlayer(HeadE.WORRIED, "What is the magical incantation?")
							.addNPC(GYPSY_ARIS, HeadE.SKEPTICAL_THINKING, "Oh yes, let me think a second...")
							.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Alright, I think I've got it now, it goes Aber... Gabindo... Purchai... Camerinthum... and Carlem..." +
									" Have you got that?")
							.addPlayer(HeadE.HAPPY_TALKING, "I think so, yes.")
							.addNext(() -> {
								p.startConversation(new GypsyArisDemonSlayerD(p, 0).getStart());
							}));
					option("Where can I find Silverlight?", new Dialogue()
							.addPlayer(HeadE.SKEPTICAL_THINKING, "Where can I find Silverlight?")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Silverlight has been passed down through Wally's descendants. I believe it is currently in the" +
									" care of one of the King's knights called Sir Prysin.")
							.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "He shouldn't be too hard to find. He lives in the royal palace in this city. Tell him Gypsy " +
									"Aris sent you.")
							.addNext(() -> {
								p.startConversation(new GypsyArisDemonSlayerD(p, 0).getStart());
							}));
					option("Okay, thanks. I'll do my best to stop the demon.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Ok thanks. I'll do my best to stop the demon.")
							.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Good luck, and may Guthix be with you!")
							.addNext(()->{
								p.getQuestManager().setStage(Quest.DEMON_SLAYER, DemonSlayer.AFTER_GYPSY_ARIS_INTRO_STAGE);
							}));
				} else {
					option("Wally doesn't sound like a very heroic name.", new Dialogue()
							.addPlayer(HeadE.SKEPTICAL, "Wally doesn't sound a very heroic name.")
							.addNPC(GYPSY_ARIS, HeadE.CALM_TALK, "Yes I know. Maybe that is why history doesn't remember him. However he was a very great hero.")
							.addNPC(GYPSY_ARIS, HeadE.CALM_TALK, "Who knows how much pain and suffering Delrith would have brought forth without Wally to stop him!")
							.addNPC(GYPSY_ARIS, HeadE.CALM_TALK, "It looks like you are going to need to perform similar heroics.")
							.addNext(() -> {
								p.startConversation(new GypsyArisDemonSlayerD(p, 0).getStart());
							}));
					option("So how did Wally kill Delrith?", new Dialogue()
							.addPlayer(HeadE.AMAZED_MILD, "So how did Wally kill Delrith?")
							.addNext(() -> {
								p.playCutscene(new WallyVSDelrithCutscene());
							}));
				}
			}
		});
	}

	private void afterCutsceneConvo(Player p) {
		addNPC(GYPSY_ARIS, HeadE.SCARED, "Delrith will come forth from the stone circle again.");
		addNPC(GYPSY_ARIS, HeadE.SCARED, "I would imagine an evil sorcerer is already starting on the rituals to summon Delrith as we speak.");
		introductoryOptions(p);
	}

	private void afterQuestStartConvo(Player p) {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("What is the magical incantation?", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "What is the magical incantation?")
						.addNPC(GYPSY_ARIS, HeadE.SKEPTICAL_THINKING, "Oh yes, let me think a second...")
						.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Alright, I think I've got it now, it goes Aber... Gabindo... Purchai... Camerinthum... and Carlem..." +
								" Have you got that?")
						.addPlayer(HeadE.HAPPY_TALKING, "I think so, yes.")
						.addNext(() -> {
							p.startConversation(new GypsyArisDemonSlayerD(p, 2).getStart());
						}));
				option("Where can I find the Silverlight?", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Where can I find the Silverlight?")
						.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Silverlight has been passed down through Wally's descendants. I believe it is currently in the" +
								" care of one of the King's knights called Sir Prysin.")
						.addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "He shouldn't be too hard to find. He lives in the royal palace in this city. Tell him Gypsy " +
								"Aris sent you.")
						.addNext(() -> {
							p.startConversation(new GypsyArisDemonSlayerD(p, 2).getStart());
						}));
				option("Stop calling me that!", new Dialogue()
						.addPlayer(HeadE.ANGRY, "Stop calling me that!")
						.addNPC(GYPSY_ARIS, HeadE.WORRIED, "In the scheme of things you are very young.")
						.addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								option("Ok but how old are you?", new Dialogue()
										.addPlayer(HeadE.ANGRY, "Ok but how old are you?")
										.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "Count the number of legs on the stools in the Blue Moon inn, and multiply that " +
												"number by seven.")
										.addPlayer(HeadE.SKEPTICAL_THINKING, "Er, yeah, whatever."));
								option("Oh if it's in the scheme of things that's ok.", new Dialogue()
										.addPlayer(HeadE.CALM_TALK, "Oh if it's in the scheme of things that's ok.")
										.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "You show wisdom for one so young."));
							}
						}));
				option("Well I'd better press on with it.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "Well I'd better press on with it.")
						.addNPC(GYPSY_ARIS, HeadE.HAPPY_TALKING, "See you soon."));
			}
		});
	}


	public static NPCClickHandler handleGypsyAris = new NPCClickHandler(new Object[] { 882 }, e -> e.getPlayer().startConversation(new GypsyArisDemonSlayerD(e.getPlayer()).getStart()));
}
