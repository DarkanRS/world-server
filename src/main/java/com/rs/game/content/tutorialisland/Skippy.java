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
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.ItemStatement;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.engine.dialogue.statements.OptionStatement;
import com.rs.engine.dialogue.statements.SimpleStatement;
import com.rs.game.World;
import com.rs.game.content.achievements.Achievement;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.lib.game.Tile;

public class Skippy extends Conversation {

	public Skippy(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		addNext(new NPCStatement(npc.getId(), HeadE.DRUNK, "Hey. Do you wanna skip the Tutorial?", "I can send you straight to Burthorpe, easy."));
		addNext(new OptionStatement("Do you want to go to the mainland?", "Yes, send me to Burthorpe now.", "No, I'd like to enjoy the nostalgic Tutorial Trent worked so hard on."));
		addNext(new NPCStatement(npc.getId(), HeadE.DRUNK, "Right on. I'll read you the official messages first, then", "send you on your way. Ahem..."));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "When you get to the mainland you will find yourself in", "the town of Burthorpe. If you want some ideas on", "where to go next, talk to my old drinking buddy Major", "Nigel Corothers You can't miss him; he's in grey armour"));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "with a sword icon above him and always in a ", "standard confidence pose. There are also many beginner friendly locations", "and shops in Burthorpe for you to skill and learn from"));
		addNext(new ItemStatement(2402, 1100, "", "When you get to Burthorpe, look for a sword icon on your", "mini-map. Major Nigel Corothers should be standing slightly off center where you start in Burthorpe, near a bank chest.", "All the other beginner locations you will find scattered around Burthorpe"));
		addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "If all else fails, visit the " + Settings.getConfig().getServerName() + " Discord for a whole", "chestload of information on quests, skills and minigames", "as well as a very good community."));
		addNext(new Dialogue(new SimpleStatement("Welcome to Burthorpe!", "To further learn about Gielinor, simply talk to Major Nigel Corothers.", "He can be found by looking for the sword icon on your mini map. If you find", "you are lost at any time, use the home teleport spell to go to common destinations."), () -> {
			World.sendWorldMessage("<img=5><col=FF0000>" + player.getDisplayName() + " has just joined "+Settings.getConfig().getServerName()+"!</col>", false);
			player.setNextTile(Tile.of(Settings.getConfig().getPlayerStartTile()));
			player.getControllerManager().forceStop();
			player.getInterfaceManager().flashTabOff();
			player.getInterfaceManager().sendSubDefaults(Sub.ALL_GAME_TABS);
			player.giveStarter();
			player.getInterfaceManager().sendAchievementComplete(Achievement.THE_JOURNEY_BEGINS_3521);
		}));
		create();
	}
}
