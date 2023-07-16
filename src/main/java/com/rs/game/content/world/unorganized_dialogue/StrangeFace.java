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
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;

public class StrangeFace extends Conversation {

	public StrangeFace(Player player) {
		super(player);
		addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "Hello?");
		addSimple("Hello.", () -> {player.voiceEffect(7890);});
		addPlayer(HeadE.AMAZED, "Woah!", ()->{player.getPackets().resetSounds();});
		addSimple("It is intriguing that you took so before coming to me. Fearful, traveller?", ()->{
			player.getPackets().resetSounds();
			player.voiceEffect(7895);
		});
		addPlayer(HeadE.SKEPTICAL_THINKING, "Should I be?", ()->{player.getPackets().resetSounds();});
		addSimple("It is my duty inform you that many warriors fight here, and they all succumb to defeat eventually. If that instills terror in you, " +
				"walk away now.", ()->{player.voiceEffect(7881);});
		addPlayer(HeadE.HAPPY_TALKING, "There are monsters in the tower?", ()->{player.getPackets().resetSounds();});
		addSimple("If that is the terminolgy you would use, yes. Through the powers \"bestowed upon me by my creator, I can generate opponents for you\", " +
				"\"based on your memories of them. Men and women have fought here\", \"for generations.", ()->{player.voiceEffect(7908);});
		addPlayer(HeadE.HAPPY_TALKING, "Impressive. So you control the tower?", ()->{player.getPackets().resetSounds();});
		addSimple("The Tower is I, and I have control of the tower. I see what happens, in any corner of any floor. I am always watching.", ()->{player.voiceEffect(7909);});
		addSimple("So you believe yourself a mighty warrior?", ()->{
			player.getPackets().resetSounds();
			//player.voiceEffect(7907);//Need to find a way to reset sounds when starting options...
		});
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Only the greatest warrior that ever lived!", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Only the greatest warrior that ever lived!")
						.addSimple("Intriguing. Such belief in your own abilities...", ()->{player.voiceEffect(7906);})
						.addPlayer(HeadE.HAPPY_TALKING, "What?", ()->{player.getPackets().resetSounds();})
						.addSimple("Your confidence may have a foundation, but judgment will come in battle.", ()->{player.voiceEffect(7896);})
						.addPlayer(HeadE.HAPPY_TALKING, "You mentioned that you were created by someone, but why?", ()->{player.getPackets().resetSounds();})
						.addSimple("My purpose...must never stop...", ()->{player.voiceEffect(7902);})
						.addPlayer(HeadE.HAPPY_TALKING, "Sorry? Are you alright?", ()->{player.getPackets().resetSounds();})
						.addSimple("You must fight in the tower, warrior. Demonstrate your ability to others and learn.", ()->{player.voiceEffect(7879);})
						.addPlayer(HeadE.HAPPY_TALKING, "I'd thought that, as a guide, you'd be a little more welcoming.",()->{player.getPackets().resetSounds();})
						.addSimple("You will find I am welcoming enough.", ()->{player.voiceEffect(7911);})
						.addSimple("Now, I can offer you more guidance; or, if you overflow with confidence, you can figure out yourself. I am the " +
								"tower, I am ever-present, so come to me if you change your mind.", ()->{
							player.getPackets().resetSounds();
							//player.voiceEffect(7872);//Need a way to reset sounds on option
							player.getDominionTower().setTalkedWithFace(true);
						})
						.addOptions("Receive further instruction?", new Options() {
							@Override
							public void create() {
								option("Yes.", new Dialogue()
										.addNext(()->{player.getDominionTower().talkToFace(true);})
								);
								option("No.", new Dialogue()
										.addSimple("Your choice. Come back if you change your mind.", ()->{
											//player.voiceEffect(7878);//Need a way to reset sounds on dialogue exit
										})
								);
							}
						})
				);
				option("I'm pretty handy with a weapon.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "I'm pretty handy with a weapon.")
						.addSimple("Intriguing. I sense humility in you...", ()->{player.voiceEffect(7887);})
						.addPlayer(HeadE.HAPPY_TALKING, "What?", ()->{player.getPackets().resetSounds();})
				);
			}
		});

		create();
	}

}
