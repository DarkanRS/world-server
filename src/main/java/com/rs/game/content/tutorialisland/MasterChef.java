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
package com.rs.game.content.tutorialisland;

import com.rs.Settings;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.LegacyItemStatement;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

public class MasterChef extends Conversation {

	public MasterChef(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		if (ctrl.getStage() == Stage.TALK_TO_CHEF) {
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Ah! Welcome, newcomer. I am the Master Chef, Lev. It", "is here I will teach you how to cook food truly fit for a", "king."));
			addNext(new PlayerStatement(HeadE.SKEPTICAL_THINKING, "I already know how to cook. Brynna taught me just", "now."));
			addNext(new NPCStatement(npc.getId(), HeadE.LAUGH, "Hahahahahaha! You call THAT cooking? Some shrimp", "on an open log fire? Oh, no, no, no. I am going to", "teach you the fine art of cooking bread."));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "And no fine meal is complete without good music, so", "we'll cover that while you're here too."));
		} else
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Hello again."));
		if (player.getInventory().missingItems(1929, 1933))
			addNext(new Dialogue(new LegacyItemStatement(1929, 1933, "", "The Cooking Guide gives you a <col=0000FF>bucket of water</col> and a,", "<col=0000FF>pot of flour!</col>"), () -> {
				player.getInventory().addItem(1929, 1);
				player.getInventory().addItem(1933, 1);
				ctrl.nextStage(Stage.MAKE_DOUGH);
			}));
		if (ctrl.pastStage(Stage.MAKE_DOUGH)) {
			addNext("Recap", new OptionStatement("What would you like to hear more about?", "Making dough.", "Range cooking.", "Music.", "Nothing, thanks."));

			getStage("Recap")
			.addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me more about making dough again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "It's quite simple: just use a pot of flour on a bucket of", "water, or vice versa, and you'll make dough. You can", "also refill your bucket at the sink."))
			.addNext(getStage("Recap"));

			getStage("Recap")
			.addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about range cooking again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "The range is the only place you can cook a lot of the", "more complex foods in " + Settings.getConfig().getServerName() + ". To cook on a", "range, right click the item you would like to cook, select", "'use', then left click the range."))
			.addNext(getStage("Recap"));

			getStage("Recap")
			.addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about music again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Ah, yes. The music was created by the great bards Ian", "and Adam. As you travel the world of " + Settings.getConfig().getServerName() + " you", "will unlock more of the tunes in your jukebox. Simply", "click on a tune in the music menu to listen to it."))
			.addNext(getStage("Recap"));
		}

		create();
	}
}
