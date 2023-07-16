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

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.ItemStatement;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.PlayerStatement;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;

public class MiningInstructor extends Conversation {

	public MiningInstructor(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		if (ctrl.inSection(Stage.TALK_TO_MINING_GUIDE, Stage.TALK_TO_MINING_GUIDE_2)) {
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Hi there. You must be new around here. So what do I", "call you? 'Newcomer' seems so impersonal, and if we're", "going to be working together, I'd rather call you by", "name."));
			addNext(new PlayerStatement(HeadE.CHEERFUL, "You can call me " + player.getDisplayName() + "."));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Ok then, "+player.getDisplayName()+". My name is Dezzick and I'm a miner", "by trade. Let's prospect some of those rocks."));
			addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.PROSPECTING_TIN)));
		} else if (ctrl.inSection(Stage.TALK_TO_MINING_GUIDE_2, Stage.TALK_TO_MINING_GUIDE_3)) {
			addNext(new PlayerStatement(HeadE.CHEERFUL_EXPOSITION, "I prospected both types of rock! One set contains tin", "and the other has copper ore inside."));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Absolutely right. These two ore types can be smelted", "together to make bronze."));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "So now you know what ore is in the rocks over there,", "why don't you have a go at mining some tin and", "copper? Here, you'll need this to start with."));
		} else if (ctrl.inSection(Stage.TALK_TO_MINING_GUIDE_3, Stage.LEAVE_MINING_AREA)) {
			addNext(new PlayerStatement(HeadE.CONFUSED, "How do I make a weapon out of this?"));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Okay, I'll show you how to make a dagger out of it.", "You'll be needing this..."));
		} else
			addNext(new NPCStatement(npc.getId(), HeadE.NO_EXPRESSION, "Hello again."));
		if (player.getInventory().missingItems(1265) && ctrl.pastStage(Stage.TALK_TO_MINING_GUIDE_2))
			addNext(new Dialogue(new ItemStatement(1265, "Dezzick gives you a <col=0000FF>bronze pickaxe</col>!"), () -> {
				player.getInventory().addItem(1265, 1);
				ctrl.nextStage(Stage.MINING_TIN);
			}));
		if (player.getInventory().missingItems(2347) && ctrl.pastStage(Stage.TALK_TO_MINING_GUIDE_3))
			addNext(new Dialogue(new ItemStatement(2347, "Dezzick gives you a <col=0000FF>hammer</col>!"), () -> {
				player.getInventory().addItem(2347, 1);
				ctrl.nextStage(Stage.CLICK_ANVIL);
			}));
		if (ctrl.pastStage(Stage.LEAVE_MINING_AREA)) {
			addNext("Recap", new OptionStatement("What would you like to hear more about?", "Tell me about prospecting again.", "Tell me about Mining again.", "Tell me about smelting again.", "Tell me about Smithing again.", "Nope, I'm ready to move on!"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about prospecting again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "It's really very simple. Usually when you go mining", "you can see if there is ore in a rock or not by its", "colour."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "If you come across a rock you've not seen before and", "want to know what it is, or if you can't tell if a rock", "contains ore just by sight,"))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "then simply right click on the rock and select 'prospect'", "to check closely. Anything else you wanted to know?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about mining again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Certainly. To mine you need a pickaxe. Different", "pickaxes let you mine more efficiently."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You have a bronze pickaxe there, which is the most", "inefficient pickaxe available, but is perfect for a", "beginner."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "To mine, simply click on a rock that contains ore while", "you have a pickaxe with you, and you will keep mining", "the rock until you manage to get some ore, or until it", "is empty."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "The better the pickaxe you use, the faster you will get", "ore from the rock you're mining."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You will be able to buy better pickaxes from the", "Dwarven Mine when you reach the mainland, but they", "can be expensive."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Also, the better the pickaxe the higher the Mining level", "required to use it will be. Was there anything else you", "wanted to hear?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about smelting again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Smelting is very easy. Simply take the ores required to", "make a metal to a furnace, then use the ores on the", "furnace to smelt them into a bar of metal."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Furnaces are expensive to build and maintain, so there", "are not that many scattered around the world. I", "suggest when you find one you remember its location", "for future use."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "An alternative to using a furnace to smelt your ore is", "to use high-level magic to do it."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "As well as letting you smelt ore anywhere, it has a", "guaranteed success rate in smelting all ores."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Some metals, such as iron, contain impurities and can", "be destroyed during the smelting process in a traditional", "furnace, but magical heat does not destroy them."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Anything else?"))
			.addNext(getStage("Recap"));

			getStage("Recap").addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Tell me about Smithing again."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "When you have acquired enough bars of the metal you", "wish to work with, you are ready to begin smithing."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Take the hammer I have given you, or buy a new one", "from a general store, and proceed to a nearby anvil."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "By using a metal bar on an anvil you will be presented", "with a screen showing the objects you are able to smith", "at your current level."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "It's a pretty straightforward skill as I'm sure you", "discovered while making me that lovely bronze dagger."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "The higher Smithing level you are, the better quality the", "metal you can work with. You start off on bronze and", "work your way up as your smithing skills increase."))
			.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Anything else?"))
			.addNext(getStage("Recap"));
		}

		create();
	}
}
