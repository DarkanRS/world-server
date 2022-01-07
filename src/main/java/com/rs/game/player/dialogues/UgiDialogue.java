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

import com.rs.game.npc.others.Ugi;
import com.rs.game.player.managers.TreasureTrailsManager;
import com.rs.lib.util.Utils;

public class UgiDialogue extends Dialogue {

	@Override
	public void start() {
		Ugi npc = (Ugi) parameters[0];
		stage = npc.getOwner() == player && player.getTreasureTrailsManager().getPhase() == 4 ? (byte) 0 : (byte) -1;
		run(-1, -1);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		Ugi npc = (Ugi) parameters[0];
		if (stage == 0) {
			sendNPCDialogue(npc.getId(), NORMAL, TreasureTrailsManager.UGIS_QUOTES[Utils.random(TreasureTrailsManager.UGIS_QUOTES.length)]);
			stage = 1;
		} else if (stage == 1) {
			sendPlayerDialogue(NORMAL, "What?");
			stage = 2;
		} else if (stage == 2) {
			end();
			npc.finish();

			player.getTreasureTrailsManager().setPhase(5);
			player.getTreasureTrailsManager().setNextClue(TreasureTrailsManager.SOURCE_EMOTE);
		} else if (stage == -1) {
			sendNPCDialogue(npc.getId(), NORMAL, TreasureTrailsManager.UGI_BADREQS);
			stage = -2;
		} else
			end();
	}

	@Override
	public void finish() {

	}
}
