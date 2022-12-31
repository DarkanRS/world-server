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

import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class GrilleGoatsDialogue extends Conversation {
	private static final int NPC = 3807;
	public GrilleGoatsDialogue(Player player) {
		super(player);
		addNPC(NPC, HeadE.CALM_TALK, "Tee hee! You have never milked a cow before, have you?");
		addPlayer(HeadE.HAPPY_TALKING, "Erm... no. How could you tell?");
		addNPC(NPC, HeadE.CALM_TALK, "Because you're spilling milk all over the floor. What a waste! \" + \"You need something to hold the milk.");
		addPlayer(HeadE.HAPPY_TALKING, "Derp. Ah, yes, I really should have guessed that one, shouldn't I?");
		addNPC(NPC, HeadE.CALM_TALK, "You're from the city, arent you? Try it again with an empty bucket.");
		addPlayer(HeadE.HAPPY_TALKING, "Right, I'll do that.");
		create();
	}
}