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
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonResourceShop;
import com.rs.game.model.entity.player.Player;

public class SmugglerD extends Conversation {

	public SmugglerD(Player player, int complexity) {
		super(player);
		
		addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "Hail, " + player.getDisplayName() + ". Need something?");
		addOptions(ops -> {
			ops.add("What can you tell me about this place?")
				.addPlayer(HeadE.CONFUSED, "What can you tell me about this place?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "You know all that I can teach you already, friend, having conquered many floors yourself."); 
			
			ops.add("Who are you?")
				.addPlayer(HeadE.CONFUSED, "Who are you?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.SECRETIVE, "A friend.")
				.addPlayer(HeadE.CONFUSED, "Okay, what are you doing here, friend?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.SECRETIVE, "I'm here to help out.")
				.addPlayer(HeadE.CONFUSED, "With what?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.SECRETIVE, "Well, let's say you find yourself in need of an adventuring kit, and you've a heavy pile of rusty coins weighing you down. I can help you with both those problems. Savvy?")
				.addPlayer(HeadE.AMAZED, "Ah, so your a trader?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.ANGRY, "Keep it down, you fool!")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.SECRETIVE, "Yes, I'm a trader. But I'm not supposed to be trading here.")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.SECRETIVE, "If you want my goods, you'll learn not to talk about me.")
				.addPlayer(HeadE.CALM_TALK, "Right, got you.")
				.addPlayer(HeadE.CONFUSED, "Is there anything else you can do for me?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "Well, there's the job I'm supposed to be doing down here.")
				.addPlayer(HeadE.CONFUSED, "Which is?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "Say you chance upon an object that you know little about. Show it to me, and I'll tell you what it's used for.")
				.addPlayer(HeadE.CALM_TALK, "That's good to know.")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "I can also offer you knowledge about the behaviour of powerful opponents you might meet in the area. I've spent a long time down here, observing them.")
				.addPlayer(HeadE.CALM_TALK, "I'll be sure to come back if I find a particularly strong opponent, then.")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "You'd be wise to " + player.getDisplayName() + ".")
				.addPlayer(HeadE.CONFUSED, "How do you know my name?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "Nothing gets in or out of Daemonhiem wihout me knowing about it.")
				.addPlayer(HeadE.CALM_TALK, "Fair enough.");
			
			ops.add("Do I have any rewards to claim?")
				.addPlayer(HeadE.CONFUSED, "Do I have any rewards to claim?")
				.addNPC(DungeonConstants.SMUGGLER, HeadE.CALM_TALK, "I have no rewards for you at the moment.");
			
			ops.add("I'm here to trade.")
				.addPlayer(HeadE.CALM_TALK, "I'm here to trade.")
				.addNext(() -> DungeonResourceShop.openResourceShop(player, complexity)); 
		});
	}
}
