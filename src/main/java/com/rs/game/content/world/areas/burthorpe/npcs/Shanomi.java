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
package com.rs.game.content.world.areas.burthorpe.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class Shanomi extends Conversation {
	private static final int npcId = 4290;

	public Shanomi(Player player) {
		super(player);

		addNPC(npcId, HeadE.HAPPY_TALKING, "Greetings " + player.getDisplayName() + " Welcome you are in the test of combat.");

		Dialogue option = addOption("What do I do here?", "What do I do here?", "Where do the machines come from?", "Bye!");

		option.addPlayer(HeadE.CONFUSED, "What do I do here?")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "A spare suit of armor need you will. Full helm, plate leggings and platebody yes? Placing it in the centre of the magical machines you will be doing. KA-POOF! The armor, it attaks its most furiously as if alive! Kill it you must, yes.")
				.addPlayer(HeadE.CONFUSED, "So I use a full set of plate armor on the centre plate of the machines and it will animate it? Then I have to kill my own armor... how bizzare!")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "Yes. It is as you are saying. For this earn tokens you will. Also gain experience in combat you will. Trained long and hard here have I.")
				.addPlayer(HeadE.CHUCKLE, "Your not from around here are you?")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "Yes. It is as you say.")
				.addPlayer(HeadE.CONFUSED, "So can my armor get damaged?")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "Lose armor you will if damaged too much it becomes. Rare this is, but still possible. If kill you the armor does, also lose armor you will.")
				.addPlayer(HeadE.CONFUSED, "So, occasionally I might lose a bit because it's being bashed about and I'll obviously lose it if I die... that it?")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "It is as you say.")
				.addNext(option);

		option.addPlayer(HeadE.CONFUSED, "Where do the machines come from?")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "Make them I did, with magics.")
				.addPlayer(HeadE.CONFUSED, "Magic, in the Warrior's Guild?")
				.addNPC(npcId, HeadE.HAPPY_TALKING, "A skilled warrior also am I. Harallak mistakes does not make. Potential in my invention he sees and opprotunity grasps.")
				.addPlayer(HeadE.CHEERFUL, "I see, so you made the magical machines and Harrallak saw how they could be used in the guild to train warrior's combat... interesting. Harrallak certainly is an intellegent guy.")
				.addNext(option);

		option.addNPC(npcId, HeadE.HAPPY_TALKING, "Health be with you travelling.");
	}
}
