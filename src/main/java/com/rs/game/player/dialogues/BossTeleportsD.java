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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.dialogues;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.controllers.QueenBlackDragonController;
import com.rs.lib.Constants;
import com.rs.lib.game.WorldTile;

public class BossTeleportsD extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "I can teleport you to boss areas around "+Settings.getConfig().getServerName()+"." }, IS_NPC, npcId, 9827);
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			sendOptionsDialogue("Where would you like to go?", "King Black Dragon", "Kalphite Queen", "Dagannoth Kings", "Glacors", "More Options");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3068, 10256, 0));
			else if (componentId == OPTION_2)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3226, 3108, 0));
			else if (componentId == OPTION_3)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1907, 4365, 0));
			else if (componentId == OPTION_4)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(4183, 5726, 0));
			else if (componentId == OPTION_5) {
				stage = 3;
				sendOptionsDialogue("Where would you like to go?", "Tormented Demons", "Godwars Dungeon", "Queen Black Dragon", "Corporeal Beast", "More Options");
			}
		} else if (stage == 3) {
			if (componentId == OPTION_1)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2562, 5739, 0));
			else if (componentId == OPTION_2)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2914, 3746, 0));
			else if (componentId == OPTION_3) {
				end();
				if (player.getSkills().getLevelForXp(Constants.SUMMONING) < 60) {
					player.sendMessage("You need a summoning level of 60 to go through this portal.");
					return;
				}
				player.getControllerManager().startController(new QueenBlackDragonController());
			} else if (componentId == OPTION_4)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2966, 4383, 2));
			else if (componentId == OPTION_5) {
				stage = 4;
				sendOptionsDialogue("Where would you like to go?", "Coming soon..", "Coming soon..", "Coming soon..", "Coming soon..", "More Options");
			}
		} else if (stage == 4)
			if (componentId == OPTION_1) {
				//Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3421, 3537, 0));
			} else if (componentId == OPTION_2) {
				//Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2931, 3899, 0));
			} else if (componentId == OPTION_3) {
				//Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3654, 5115, 0));
			} else if (componentId == OPTION_4) {
				//Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2897, 4845, 0));
			} else if (componentId == OPTION_5) {
				stage = 1;
				sendOptionsDialogue("Where would you like to go?", "King Black Dragon", "Kalphite Queen", "Dagannoth Kings", "Glacors", "More Options");
			}
	}

	@Override
	public void finish() {

	}

}
