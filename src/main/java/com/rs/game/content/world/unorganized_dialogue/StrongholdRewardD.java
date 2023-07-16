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
package com.rs.game.content.world.unorganized_dialogue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager.Emote;

public class StrongholdRewardD extends Conversation {

	public StrongholdRewardD(Player player, int reward) {
		super(player);

		switch(reward) {
		case 0:
			addSimple("The box hinges and crack and appear to be forming audible words....");
			addSimple("...congratulations adventurer, you have been deemed worthy of this reward. You have also unlocked the Flap emote!", () -> {
				player.getInventory().addCoins(2000);
				player.getEmotesManager().unlockEmote(Emote.FLAP);
			});
			break;
		case 1:
			addSimple("The grain shifts in the sack, sighing audible words....");
			addSimple("...congratulations adventurer, you have been deemed worthy of this reward. You have also unlocked the Slap Head emote!", () -> {
				player.getInventory().addCoins(3000);
				player.getEmotesManager().unlockEmote(Emote.SLAP_HEAD);
			});
			break;
		case 2:
			addSimple("The box hinges creak and appear to be forming audible words....");
			addSimple("...congratulations adventurer, you have been deemed worthy of this reward. You have also unlocked the Idea emote!", () -> {
				player.getInventory().addCoins(5000);
				player.getEmotesManager().unlockEmote(Emote.IDEA);
				player.sendMessage("You feel refreshed.");
				player.reset();
			});
			break;
		case 3:
			addSimple("As your hand touches the cradle, you hear the voices of a million dead adventurers...");
			if (!player.containsOneItem(9005, 9006)) {
				addSimple("Welcome adventurer... you appear to have a choice...");
				addSimple("You can choose between these two pairs of boots. They will both protect your feet in exactly same manner; however, they look very different. You can always come back and get another pair if you lose them, or even swap them for the other style!");

				Dialogue op = addOption("Choose your style of boots", "I'll take the colourful ones.", "I'll take the fighting ones.");
				if (player.getEmotesManager().unlockedEmote(Emote.STOMP)) {
					op.addPlayer(HeadE.CHEERFUL, "I'll take the colourful ones.")
					.addSimple("Enjoy your new boots.", () -> {
						player.getInventory().addItem(9005, 1);
					});
					op.addPlayer(HeadE.CHEERFUL, "I'll take the fighting ones.")
					.addSimple("Enjoy your new boots.", () -> {
						player.getInventory().addItem(9006, 1);
					});
				} else {
					op.addPlayer(HeadE.CHEERFUL, "I'll take the colourful ones.")
					.addSimple("Congratulations! You have succefully nagivated the Stronghold of secuity and learned to secure your account. You have unlocked the 'Stamp Foot'emote. Remember to keep your account secured in the future!", () -> {
						player.getInventory().addItem(9005, 1);
						player.getEmotesManager().unlockEmote(Emote.STOMP);
					});
					op.addPlayer(HeadE.CHEERFUL, "I'll take the fighting ones.")
					.addSimple("Congratulations! You have succefully nagivated the Stronghold of secuity and learned to secure your account. You have unlocked the 'Stamp Foot'emote. Remember to keep your account secured in the future!", () -> {
						player.getInventory().addItem(9006, 1);
						player.getEmotesManager().unlockEmote(Emote.STOMP);
					});
				}
			} else
				addSimple("You already have your boots. Return if you lose them.");
			break;
		}

		create();
	}
}
