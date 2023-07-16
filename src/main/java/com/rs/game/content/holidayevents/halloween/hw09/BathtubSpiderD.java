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
public class BathtubSpiderD extends Conversation {

	public static NPCClickHandler handleSpiderTalk = new NPCClickHandler(new Object[] { 8978 }, e -> {
		e.getNPC().resetDirection();
		e.getPlayer().startConversation(new BathtubSpiderD(e.getPlayer()));
	});

	public BathtubSpiderD(Player player) {
		super(player);

		switch(player.getI(Halloween2009.STAGE_KEY)) {
		case 1:
			addNPC(8979, HeadE.SPIDER_CALM, "Wot?");
			addOptions(new Options("startOps", BathtubSpiderD.this) {
				@Override
				public void create() {
					option("My goodness! A giant, talking spider!", new Dialogue()
							.addPlayer(HeadE.AMAZED, "My goodness! A giant, talking spider!")
							.addNPC(8979, HeadE.SPIDER_CALM, "Guess so.")
							.addGotoStage("startOps", BathtubSpiderD.this));
					option("Shoo! Horrible spider!", new Dialogue()
							.addPlayer(HeadE.ANGRY, "Shoo! Horrible spider!")
							.addNPC(8979, HeadE.SPIDER_CALM, "Nah.")
							.addGotoStage("startOps", BathtubSpiderD.this));
					option("Please move, spider.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "Please move, spider.")
							.addNPC(8979, HeadE.SPIDER_CALM, "Nope.")
							.addOptions(new Options("plsMoveOps", BathtubSpiderD.this) {
								@Override
								public void create() {
									option("If you don't move, the Grim Reaper will kill you!", new Dialogue()
											.addPlayer(HeadE.AMAZED, "If you don't move, the Grim Reaper will kill you!")
											.addNPC(8979, HeadE.SPIDER_CALM, "Don't think so.")
											.addGotoStage("plsMoveOps", BathtubSpiderD.this));
									option("You don't talk much, do you?", new Dialogue()
											.addPlayer(HeadE.CONFUSED, "You don't talk much, do you?")
											.addNPC(8979, HeadE.SPIDER_CALM, "Nope.")
											.addGotoStage("plsMoveOps", BathtubSpiderD.this));
									option("What are you doing in Grim's bathtub?", new Dialogue()
											.addPlayer(HeadE.CONFUSED, "What are you doing in Grim's bathtub?")
											.addNPC(8979, HeadE.SPIDER_CALM, "Nuthin'.")
											.addPlayer(HeadE.CONFUSED, "I mean, why are you here?")
											.addNPC(8979, HeadE.SPIDER_CALM, "Spider Queen.")
											.addOptions(new Options("queenOps", BathtubSpiderD.this) {
												@Override
												public void create() {
													option("Who is the Spider Queen?", new Dialogue()
															.addPlayer(HeadE.CONFUSED, "Who is the Spider Queen?")
															.addNPC(8979, HeadE.SPIDER_CALM, "Queen of Spiders.")
															.addGotoStage("queenOps", BathtubSpiderD.this));
													option("So, the Spider Queen sent you here?", new Dialogue()
															.addPlayer(HeadE.CONFUSED, "So, the Spider Queen sent you here?")
															.addNPC(8979, HeadE.SPIDER_CALM, "Yup.")
															.addGotoStage("queenOps", BathtubSpiderD.this));
													option("Why would the Spider Queen tell you to sit in Grim's Bath?", new Dialogue()
															.addPlayer(HeadE.CONFUSED, "Why would the Spider Queen tell you to sit in Grim's Bath?")
															.addNPC(8979, HeadE.SPIDER_CALM, "Dunno.")
															.addGotoStage("queenOps", BathtubSpiderD.this));
													option(() -> !player.getTempAttribs().getB("queenKnown"), "Would you move if the Spider Queen told you to?", new Dialogue()
															.addPlayer(HeadE.CONFUSED, "Would you move if the Spider Queen told you to?")
															.addNPC(8979, HeadE.SPIDER_CALM, "Yup.")
															.addPlayer(HeadE.UPSET, "I better report back to the Grim Reaper.", () -> {
																if (player.getI(Halloween2009.STAGE_KEY) == 1)
																	player.save(Halloween2009.STAGE_KEY, 2);
															})
															.addGotoStage("queenOps", BathtubSpiderD.this));
													option("Goodbye.");
												}
											}));
									option("Goodbye.");
								}
							}));
					option("Goodbye.");
				}
			});
			break;
		default:
			addNPC(8979, HeadE.SPIDER_CALM, "Wot?");
			break;
		}
		create();
	}

}
