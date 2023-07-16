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
public class SpiderHeraldD extends Conversation {

	public static NPCClickHandler handleSpiderTalk = new NPCClickHandler(new Object[] { 8976 }, e -> {
		e.getNPC().resetDirection();
		e.getPlayer().startConversation(new SpiderHeraldD(e.getPlayer()));
	});

	public SpiderHeraldD(Player player) {
		super(player);

		switch(player.getI(Halloween2009.STAGE_KEY)) {
		case 3:
			addNPC(8976, HeadE.SPIDER_CALM, "Who goes there? Four-limbed intruder! Come no further! State your business!");
			addOptions(new Options() {
				@Override
				public void create() {
					option("I must meet the Queen of Spiders.", new Dialogue()
							.addPlayer(HeadE.CALM_TALK, "I must meet the Queen of Spiders.")
							.addNPC(8976, HeadE.SPIDER_CALM, "It may be, among your people, that the lowliest may enter the bed-chamber of a monarch; But we spiders are more formal.")
							.addNPC(8976, HeadE.SPIDER_CALM, "Tell me, then, have you some patron on whose business you come hither?")
							.addPlayer(HeadE.CALM_TALK, "Death has sent me; the Grim Reaper.")
							.addNPC(8976, HeadE.SPIDER_EXCLAIM, "The Grim Reaper! I implore you, grant your pardon for my rudeness! I was told the Queen expected the Dread Lord to send an agent.")
							.addNPC(8976, HeadE.SPIDER_CALM, "Do not let my words detain you. You should ascend the ladder.", () -> {
								player.save(Halloween2009.STAGE_KEY, 4);
							}));
					option("Wretched spiders! I will crush you!", new Dialogue()
							.addPlayer(HeadE.ANGRY, "Wretched spiders! I will crush you!")
							.addNPC(8976, HeadE.SPIDER_CALM, "What an introduction. I don't think you would stand the slightest chance against us, human."));
				}
			});
			break;
		case 4:
			addNPC(8976, HeadE.SPIDER_CALM, "Visitor, you should ascend the ladder.");
			break;
		case 5:
			addNPC(8976, HeadE.SPIDER_CALM, "Visitor, you should...");
			addNPC(8976, HeadE.SPIDER_NONE, "...");
			addNPC(8976, HeadE.SPIDER_CALM, "Through vibrations of the web-strands at my feet, below your hearing, or Dread Monarch has informed me that your business is concluded.");
			addNPC(8976, HeadE.SPIDER_NONE, "...");
			addNPC(8976, HeadE.SPIDER_CALM, "And she tells me a companion, a web-spinner, will be needed for your return to the dwelling of our noble friend the Reaper.");
			addNPC(8976, HeadE.SPIDER_CALM, "Who, though?");
			addNPC(8976, HeadE.SPIDER_CALM, "Who, though? Yes! I know the very Emissary I can give you. Small, but most enthusiastic: Eek the Spider will go with you.");
			addNext(new SpiderStatement("Boo!"));
			addPlayer(HeadE.HAPPY_TALKING, "Hello, Eek.");
			addNext(new SpiderStatement("Hello big human!"));
			addItem(15353, "(Eek the Spider jumps into your backpack.)", () -> {
				player.save(Halloween2009.STAGE_KEY, 6);
				player.getInventory().addItem(15353);
			});
			addNext(new SpiderStatement("Let's go! This'll be fun!"));
			break;
		case 6:
		case 7:
		case 8:
		case 9:
			if (!player.containsItem(15353)) {
				addItem(15353, "(Eek the Spider jumps into your backpack.)", () -> {
					player.save(Halloween2009.STAGE_KEY, 6);
					player.getInventory().addItem(15353);
				});
				addNext(new SpiderStatement("Let's go! This'll be fun!"));
			} else
				addNPC(8976, HeadE.SPIDER_CALM, "My sincerest apologies for my rudeness. I hope you found your meeting with our Queen delightful.");
			break;
		default:
			addNPC(8976, HeadE.SPIDER_CALM, "Human, let me add my praises to the thanks of our Dread Monarch. You have joined the hallowed canon of heroes of our nation.");
			addNPC(8976, HeadE.SPIDER_CALM, "With stubborn dedication of our brother Incey-Wincey;With the curds and whey that scattered from the tuffet of Miss Muffet;");
			addNPC(8976, HeadE.SPIDER_CALM, "With the weaving of Arachne;With the cunning of Anansi;With these and our other stories your time here will be remembered.");
			break;
		}

		create();
	}

}
