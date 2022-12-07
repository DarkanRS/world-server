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
import com.rs.game.World;
import com.rs.game.content.achievements.Achievement;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.statements.ItemStatement;
import com.rs.game.content.dialogue.statements.LegacyItemStatement;
import com.rs.game.content.dialogue.statements.NPCStatement;
import com.rs.game.content.dialogue.statements.OptionStatement;
import com.rs.game.content.dialogue.statements.PlayerStatement;
import com.rs.game.content.dialogue.statements.SimpleStatement;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.lib.game.WorldTile;

public class MagicInstructor extends Conversation {

	public MagicInstructor(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);

		if (ctrl.inSection(Stage.TALK_TO_MAGIC_INSTRUCTOR, Stage.TALK_TO_MAGIC_INSTRUCTOR_2)) {
			addNext(new PlayerStatement(HeadE.NO_EXPRESSION, "Hello."));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Good day, newcomer. My name is Terrova. I'm here", "to tell you about Magic. Let's start by opening your", "spell list."));
			addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.OPEN_MAGIC_TAB)));
		} else if (ctrl.inSection(Stage.TALK_TO_MAGIC_INSTRUCTOR_2, Stage.TALK_TO_MAGIC_INSTRUCTOR_3)) {
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Currently you can only cast one offensive spell called", "Wind Strike. Let's try it out on one of those chickens."));
			if (player.getInventory().missingItems(558, 556))
				addNext(new Dialogue(new LegacyItemStatement(558, 556, "", "Terrova gives you some <col=0000FF>air runes</col> and <col=0000FF>mind runes</col>."), () -> {
					player.getInventory().addItem(558, 5);
					player.getInventory().addItem(556, 5);
					ctrl.nextStage(Stage.CAST_WIND_STRIKE);
				}));
		} else if (ctrl.pastStage(Stage.TALK_TO_MAGIC_INSTRUCTOR_3)) {
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Well, you're all finished here now. I'll give you a", "reasonable number of runes when you leave."));
			addNext(new OptionStatement("Do you want to go to the mainland?", "Yes", "No"));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "When you get to the mainland you will find yourself in", "the town of Lumbridge. If you want some ideas on", "where to go next, talk to my friend the Lumbridge", "Guide. You can't miss him; he's holding a big staff with"));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "a question mark on the end. He also has a white beard", "and carries a rucksack full of scrolls. There are also", "many tutors willing to teach you about the many skills", "you could learn."));
			addNext(new ItemStatement(5079, 1100, "", "When you get to Lumbridge, look for this icon on your", "mini-map. The Lumbridge Guide or one of the other", "tutors should be standing near there. The Lumbridge", "Guide should be standing slightly to the north-east of"));
			addNext(new ItemStatement(5079, 1100, "", "the castle's courtyard and the others you will find", "scattered around Lumbridge."));
			addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "If all else fails, visit the "+Settings.getConfig().getServerName()+" website for a whole", "chestload of information on quests, skills and minigames", "as well as a very good starter's guide."));
			addNext(new Dialogue(new SimpleStatement("Welcome to Lumbridge! To get more help, simply click on the", "Lumbridge Guide or one of the Tutors - these can be found by", "looking for the question mark icon on your mini map. If you find", "you are lost at any time, look for a signpost or use the Lumbridge", "Home Port spell."), () -> {
				World.sendWorldMessage("<img=5><col=FF0000>"+player.getDisplayName()+" has just joined "+Settings.getConfig().getServerName()+"!</col>", false);
				player.setNextWorldTile(WorldTile.of(Settings.getConfig().getPlayerStartTile()));
				player.getControllerManager().forceStop();
				player.getInterfaceManager().sendSubDefaults(Sub.ALL_GAME_TABS);
				player.giveStarter();
				player.getInterfaceManager().sendAchievementComplete(Achievement.THE_JOURNEY_BEGINS_3521);
			}));
		}

		create();
	}
}
