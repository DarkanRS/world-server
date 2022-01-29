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
package com.rs.game.player.dialogues;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.lib.game.WorldTile;

public class WildernessTeleportsD extends Dialogue {
	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_2_TEXT_CHAT, new String[] { NPCDefinitions.getDefs(npcId).getName(), "I can teleport you to wilderness areas around "+Settings.getConfig().getServerName()+"." }, IS_NPC, npcId, 9827);
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			sendOptionsDialogue("Where would you like to go?", "Wilderness Agility Course", "Green Dragons (West)", "Green Dragons (East)", "Revenants", "More Options");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2998, 3910, 0));
			else if (componentId == OPTION_2)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2982, 3602, 0));
			else if (componentId == OPTION_3)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3356, 3679, 0));
			else if (componentId == OPTION_4)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3081, 10058, 0));
			else if (componentId == OPTION_5) {
				stage = 3;
				sendOptionsDialogue("Where would you like to go?", "Chaos Elemental", "Mage Bank", "Coming soon..", "Coming soon..", "More Options");
			}
		} else if (stage == 3) {
			if (componentId == OPTION_1)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3299, 3918, 0));
			else if (componentId == OPTION_2)
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2539, 10057, 0));
			else if (componentId == OPTION_3) {
				//Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3654, 5115, 0));
			} else if (componentId == OPTION_4) {
				//Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2897, 4845, 0));
			} else if (componentId == OPTION_5) {
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
				sendOptionsDialogue("Where would you like to go?", "Wilderness Agility Course", "Green Dragons (West)", "Green Dragons (East)", "Revenants", "More Options");
			}
	}

	@Override
	public void finish() {

	}
}
