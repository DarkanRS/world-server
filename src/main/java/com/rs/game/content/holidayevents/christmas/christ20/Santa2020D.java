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
package com.rs.game.content.holidayevents.christmas.christ20;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Santa2020D extends Conversation {

	private static final int SNOW_QUEEN = 9398, SANTA = 9400;

	public static NPCClickHandler handleSantaTalk = new NPCClickHandler(new Object[] { SNOW_QUEEN, SANTA }, e -> e.getPlayer().startConversation(new Santa2020D(e.getPlayer())));

	public Santa2020D(Player player) {
		super(player);
		switch(player.getI(Christmas2020.STAGE_KEY, 0)) {
		case 0:
			addPlayer(HeadE.HAPPY_TALKING, "Merry Christmas, guys!");
			addNPC(SANTA, HeadE.LAUGH, "Ho ho hohhh! Merry Christmas!");
			addPlayer(HeadE.CONFUSED, "Wait.. you seem relatively jolly this year.");
			addNPC(SANTA, HeadE.LAUGH, "Of course I am! I am Santa Claus, and it is Christmas time!");
			addNPC(SNOW_QUEEN, HeadE.CONFUSED, "Are you okay, " + player.getDisplayName() + "? You look like you've slipped and hit your head on the ice.");
			addPlayer(HeadE.SKEPTICAL, "No, I was just expecting something to have gone wrong and it to be all up to me to fix it.");
			addNPC(SNOW_QUEEN, HeadE.LAUGH, "Oh no! Everything has gone rather smoothly this year. Right Nicky?");
			addNPC(SANTA, HeadE.CHEERFUL, "Why yes it has Snowie.");
			addPlayer(HeadE.CONFUSED, "So there's nothing for me to do for you guys?");
			addNPC(SANTA, HeadE.LAUGH, "Ho ho hohhh! You could guess a number between 1-1000 for me if that would make you feel more deserving of your presents this year.");
			addPlayer(HeadE.CHEERFUL, "That's a lot more simple than last year, I guess!");
			addNPC(SANTA, HeadE.CHEERFUL, "Yeah, when Torpid Trent has things he'd rather do. It seems to make our jobs easier.", () -> {
				player.getTempAttribs().setI("santaRandNum", Utils.random(1, 1000));
			});
			addNext(() -> {
				player.save(Christmas2020.STAGE_KEY, 1);
				guessNumber(player);
			});
			break;
		case 1:
			addNext(() -> {
				guessNumber(player);
			});
			break;
		case 2:
			addNPC(SANTA, HeadE.CHEERFUL, "Good job. Do you feel deserving of your Christmas presents now?");
			addPlayer(HeadE.CHEERFUL, "Good enough for me!");
			addItem(1050, "Santa hands you a yo-yo and one of his hats!");
			addNext(() -> {
				player.save(Christmas2020.STAGE_KEY, 3);
				player.getInventory().addItemDrop(new Item(962, 1));
				player.getInventory().addItemDrop(new Item(4079, 1));
				player.addDiangoReclaimItem(4079);
				player.sendMessage("You've unlocked the 'Freeze' emote!");
				player.getEmotesManager().unlockEmote(Emote.FREEZE);
			});
			break;
		case 3:
			addPlayer(HeadE.CHEERFUL, "Merry Christmas!");
			addNPC(SANTA, HeadE.LAUGH, "Merry Christmas!");
			addNPC(SNOW_QUEEN, HeadE.SAD_MILD_LOOK_DOWN, "*cough*trentslazy*cough*");
			addNPC(SANTA, HeadE.LAUGH, "What was that, Snowie?");
			addNPC(SNOW_QUEEN, HeadE.LAUGH, "Oh, nothing! Merry Christmas!");
			break;
		}
		create();
	}

	private static void guessNumber(Player player) {
		if (player.getTempAttribs().getI("santaRandNum", -1) == -1)
			player.getTempAttribs().setI("santaRandNum", Utils.random(1, 1000));
		player.sendInputInteger("Guess Santa's number between 1-1000!", result -> {
			int answer = player.getTempAttribs().getI("santaRandNum");
			if (result > answer)
				player.startConversation(new Dialogue()
						.addNPC(SANTA, player.getDisplayName().equalsIgnoreCase("jiren") ? HeadE.DRUNK : HeadE.CHEERFUL, player.getDisplayName().equalsIgnoreCase("jiren") ? "Dat shit's higha dan Snoop Dogg hoe." : "That's too high! Try again.")
						.addNext(() -> guessNumber(player)));
			else if (result < answer)
				player.startConversation(new Dialogue()
						.addNPC(SANTA, player.getDisplayName().equalsIgnoreCase("jiren") ? HeadE.DRUNK : HeadE.CHEERFUL, player.getDisplayName().equalsIgnoreCase("jiren") ? "Shawty too low low low low low low low low low low low low low." : "That's too low! Try again.")
						.addNext(() -> guessNumber(player)));
			else {
				player.save(Christmas2020.STAGE_KEY, 2);
				player.startConversation(new Santa2020D(player));
			}
		});
	}

}
