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
package com.rs.game.content.holidayevents.easter.easter21;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class EasterBunnyJrD extends Conversation {

	private static final int EASTER_BUNNY_JR = 7411;

	public static NPCClickHandler handleEasterBunnyJrTalk = new NPCClickHandler(new Object[] { EASTER_BUNNY_JR }, e -> e.getPlayer().startConversation(new EasterBunnyJrD(e.getPlayer())));

	public EasterBunnyJrD(Player player) {
		super(player);

		switch(player.getI(Easter2021.STAGE_KEY, 0)) {
		case 4:
			addPlayer(HeadE.CHEERFUL, "Hello!");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_PURRING, "What do you want?..");
			addPlayer(HeadE.CHEERFUL, "I need help fixing the incubator and your father told me you might know what happened to it.");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_SAD, "Sure whatever.. The incubator exploded and they're somewhere around the factory.");
			addPlayer(HeadE.CONFUSED, "Can you tell me where they are exactly?..");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_SAD, "I don't really care and I don't remember.. All I know is that 3 parts went flying off when it exploded. Leave me alone now, I'm trying to sleep.");
			addPlayer(HeadE.ANGRY, "That doesn't help much at all!");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_DISAPPOINTED2, "Ok boomer.");
			addPlayer(HeadE.ROLL_EYES, "*What a lazy sack of garbage.*", () -> {
				player.save(Easter2021.STAGE_KEY, 5);
			});
			break;
		case 5:
			addPlayer(HeadE.ANGRY, "Tell me where the incubator parts are.");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_SAD, "As I said before, I don't really care and I don't remember.. All I know is that 3 parts went flying off around the factory when it exploded.");
			break;
		default:
			addPlayer(HeadE.CHEERFUL, "Hello!");
			addNPC(EASTER_BUNNY_JR, HeadE.CAT_PURRING, "Zzzz...");
			break;
		}

		create();
	}

}
