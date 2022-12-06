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
import com.rs.game.content.dialogue.statements.NPCStatement;
import com.rs.game.content.dialogue.statements.OptionStatement;
import com.rs.game.content.dialogue.statements.SimpleStatement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.lib.game.WorldTile;

public class Skippy extends Conversation {

	public Skippy(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		addNext(new NPCStatement(npc.getId(), HeadE.DRUNK, "Hey. Do you wanna skip the Tutorial?", "I can send you straight to Lumbridge, easy."));
		addNext(new OptionStatement("Do you want to go to the mainland?", "Yes, send me to Lumbridge now.", "No, I'd like to enjoy the nostalgic Tutorial Trent worked so hard on."));
		addNext(new NPCStatement(npc.getId(), HeadE.DRUNK, "Right on. I'll read you the official messages first, then", "send you on your way. Ahem..."));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "When you get to the mainland you will find yourself in", "the town of Lumbridge. If you want some ideas on", "where to go next, talk to my friend the Lumbridge", "Guide. You can't miss him; he's holding a big staff with"));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "a question mark on the end. He also has a white beard", "and carries a rucksack full of scrolls. There are also", "many tutors willing to teach you about the many skills", "you could learn."));
		addNext(new ItemStatement(5079, 1100, "", "When you get to Lumbridge, look for this icon on your", "mini-map. The Lumbridge Guide or one of the other", "tutors should be standing near there. The Lumbridge", "Guide should be standing slightly to the north-east of"));
		addNext(new ItemStatement(5079, 1100, "", "the castle's courtyard and the others you will find", "scattered around Lumbridge."));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "If all else fails, visit the " + Settings.getConfig().getServerName() + " website for a whole", "chestload of information on quests, skills and minigames", "as well as a very good starter's guide."));
		addNext(new Dialogue(new SimpleStatement("Welcome to Lumbridge! To get more help, simply click on the", "Lumbridge Guide or one of the Tutors - these can be found by", "looking for the question mark icon on your mini map. If you find", "you are lost at any time, look for a signpost or use the Lumbridge", "Home Port spell."), () -> {
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has just joined "+Settings.getConfig().getServerName()+"!</col>", false);
			player.setNextWorldTile(WorldTile.of(Settings.getConfig().getPlayerStartTile()));
			player.getControllerManager().forceStop();
			player.getInterfaceManager().flashTabOff();
			player.getInterfaceManager().sendSubDefaults(Sub.ALL_GAME_TABS);
			player.giveStarter();
			player.getInterfaceManager().sendAchievementComplete(Achievement.THE_JOURNEY_BEGINS_3521);
		}));
		create();
	}
}
