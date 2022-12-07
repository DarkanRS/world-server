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
package com.rs.game.content.skills.runecrafting.runespan;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;

public class WizardFinix extends Conversation {

	private static final int FINIX = 15417;

	public WizardFinix(Player player) {
		super(player);

		addOptions(new Options() {
			@Override
			public void create() {
				option("Hello. Who are you?", new Dialogue()
						.addNPC(FINIX, HeadE.CHEERFUL, "I'm Wizard Finix. I am a student of runecrafting.")
						.addPlayer(HeadE.CHEERFUL_EXPOSITION, "Ah, pleased to meet you, Wizard Finix. I'm " + player.getDisplayName() + ". What are you doing here?")
						.addNPC(FINIX, HeadE.CHEERFUL, "I'm creating runes by siphoning energy out of the Runespan.")
						.addPlayer(HeadE.CONFUSED, "Creating runes by siphoning?")
						.addNPC(FINIX, HeadE.CHEERFUL, "Yes, it's a new way to make runes. I discovered it, actually!")
						.addOptions(new Options() {
							@Override
							public void create() {
								option("Why don't you just use the altars?", new Dialogue()
										.addNPC(FINIX, HeadE.CHEERFUL, "That is another one of my discoveries! Runecrafting altars will not last forever. One day, the altars will run out!")
										.addPlayer(HeadE.SCARED, "Run out?")
										.addNPC(FINIX, HeadE.SKEPTICAL, "Yes - of course, it is still only a theory. The Runecrafting Guild Wizards are still skeptical, but they cannot prove me wrong.")
										.addPlayer(HeadE.SCARED, "How will humans perform magic if they cannot make runes?")
										.addNPC(FINIX, HeadE.CHEERFUL, "Exactly. That is why learning to siphon energy from the world around us is important.")
										.addPlayer(HeadE.SCARED, "You mean I could create runes by pulling energy from Gielinor itself?")
										.addNPC(FINIX, HeadE.SKEPTICAL, "Theoretically, yes, but there is no runecrafter skilled enough to do this yet. In theory, runecrafting and magic should be a self-supporting cycle.")
										.addPlayer(HeadE.CONFUSED, "So how can I learn to siphon energy if it is so difficult?")
										.addNPC(FINIX, HeadE.CHEERFUL, "That is why we are in this area. In the Runespan, energy is still in a very raw state and is not tightly bound to matter, as it is in Gielinor. The wizards of the Runecrafting Guild have been using their tower to study.")
										.addNPC(FINIX, HeadE.CHEERFUL, "runic energy in the Runespan for years.")
										.addPlayer(HeadE.SCARED, "So that means...")
										.addNPC(FINIX, HeadE.CHEERFUL, "...we are outside the Runecrafting Guild - yes!"));
								option("How do you make runes by siphoning?", new Dialogue()
										.addNPC(FINIX, HeadE.SKEPTICAL, "Well, firstly, you must bring your own rune essence into the Runespan as rune essence is depletable resource. Feel free to grab from the floating rune rocks we have placed around, but only if you have")
										.addNPC(FINIX, HeadE.SKEPTICAL, "bank notes of your own to exchange, as we would like to keep the stock plentiful and reduce trips taken to restore the supply.")
										.addNPC(FINIX, HeadE.CHEERFUL, "Don't worry, though, that means you are free to keep any runes you create while within the Runespan! The Runecrafting Guild is also willing to keep track of the runes you craft")
										.addNPC(FINIX, HeadE.CHEERFUL, "and award you points to spend in my shop in the Wizards' Tower.")
										.addPlayer(HeadE.CONFUSED, "Alright, so how does making runes work?")
										.addNPC(FINIX, HeadE.CHEERFUL, "The process is relatively simple. Once you have rune essence, you can siphon energy from the creatures and pockets of energy that appear randomly on islands in this area.")
										.addNPC(FINIX, HeadE.CHEERFUL, "The energy in the creatures is not as dense as it is in the pockets of energy, so they will give you a smaller number of runes.")
										.addNPC(FINIX, HeadE.CHEERFUL, "Just be aware it can be tricky to succeed in siphoning when you are starting out. I have infused the various wicked fabric items with some of my experience")
										.addNPC(FINIX, HeadE.CHEERFUL, "should you wish to use it. Wearing various parts of the outfit whilst training in here will certainly help you to be more successful in your training."));
							}
						}));
				option("Can you teleport me back to the Wizards' Tower?", new Dialogue()
						.addNPC(FINIX, HeadE.CHEERFUL, "Of course!")
						.addNext(() -> {
							Magic.sendNormalTeleportSpell(player, WorldTile.of(3107, 3162, 1));
						}));
			}
		});

		create();
	}
}
