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
package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.World;
import com.rs.game.content.PlayerLook;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Iconis extends Conversation {
	private static final int npcId = 8879;

	public static NPCClickHandler Iconis = new NPCClickHandler(new Object[]{ npcId }, e -> {
		switch (e.getOption()) {
			
			case "Talk-to" -> e.getPlayer().startConversation(new Iconis(e.getPlayer()));
			case "Take-picture" -> e.getPlayer().startConversation(new Dialogue()
					.addNPC(npcId, HeadE.SHAKING_HEAD, "Sorry, the imp is a little busy. There's a lot of demand, you see.")
			);
		}
	});

	public static ObjectClickHandler PhotoBooth = new ObjectClickHandler(new Object[] { 46396 }, e -> {
		e.getPlayer().startConversation(new Dialogue()
				.addNPC(npcId, HeadE.SHAKING_HEAD, "Sorry, the imp is a little busy. There's a lot of demand, you see."));
	});

	public Iconis(Player player) {
		super(player);
		addNPC(npcId, HeadE.HAPPY_TALKING, "Good day! Would you like to have your picture taken?");
		addOptions(new Options() {
			@Override
			public void create() {

				option("Yes, please!", new Dialogue()
						.addNPC(npcId, HeadE.SHAKING_HEAD, "Sorry, the imp is a little busy. There's a lot of demand, you see."));

				option("No, thank you.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "No, thank you.")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "As you wish.")
				);

				option("How does it work?", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "How does it work?")
						.addNPC(npcId, HeadE.HAPPY_TALKING, "There's an imp in there who uses a spell to take your picture. He's having a hard time keeping up at the moment. There's a lot of demand, you see.")
						.addOptions(new Options() {
							@Override
							public void create() {
								option("Why an imp?", new Dialogue()
										.addPlayer(HeadE.CALM_TALK, "Why an imp?")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "Ah, good question! ")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "See, I've experimented with lots of ways of making accurate visual representations of things in a magical way. ")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "Most recently, I've been trying to modify scrying orbs to get a still picture. Because of their shape, though, they always made the subject look fat! ")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "So I came up with a scrying cube, but that made things look even worse!")
										.addPlayer(HeadE.CALM_TALK, "So what did you do?")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "I tweaked and tweaked the shape until I got a decent picture - took me weeks to make - but then this imp just teleported in from out of nowhere right next to it. My scrying device fell on the floor and smashed.")
										.addPlayer(HeadE.AMAZED_MILD, "Oh, no!")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "Indeed. I caught the culprit in a magic imp box and had a word with him. He seemed rather confused that I was using orbs to take pictures. Apparently, imps have a rather handy spell for that, so I hired him!")
										.addPlayer(HeadE.CONFUSED, "Imps take pictures?")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "Yes. He wouldn't reveal the exact working of the spell to me, or even why they do it, but the result is very convincing.")
								);

								option("Is it safe?", new Dialogue()
										.addPlayer(HeadE.SKEPTICAL, "Is it safe?")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "Oh, absolutely! I've tried the procedure myself thousands of times to make sure it's safe and that there are no side effects.")
										.addPlayer(HeadE.SKEPTICAL, "But are my belongings safe?")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "Totally! I've got a few runes, orbs and trinkets in there to ensure of that.")
								);

								option("That's all, thank you.", new Dialogue()
										.addPlayer(HeadE.CALM_TALK, "That's all, thank you.")
										.addNPC(npcId, HeadE.HAPPY_TALKING, "You're welcome!")
								);
							}
						})
				);
			}
		});
	}
}
