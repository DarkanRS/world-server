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
package com.rs.game.player.cutscenes;

import com.rs.game.Entity.MoveType;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;

public class ExampleCutscene extends Cutscene {
	
	private static final String GUTHIX = "guthix", GUARD1 = "guard1", GUARD2 = "guard2";

	@Override
	public void construct(Player player) {
		constructMap(360, 482, 3, 3);
		musicEffect(215);
		playerMove(10, 0, 0, MoveType.TELE);
		camLook(10, 8, 1000);
		camPos(10, 0, 2000, 4);
		npcCreate(GUTHIX, 8008, 10, 6, 0);
		npcFaceTile(GUTHIX, 10, 5);
		npcSpotAnim(GUTHIX, new SpotAnim(184), 3);
		npcTalk(GUTHIX, "....", 4);
		npcTalk(GUTHIX, "GuthiXx!@!@!@!");
		npcAnim(GUTHIX, new Animation(2108), 4);
		npcFaceTile(GUTHIX, 9, 6);
		playerMove(9, 6, 0, MoveType.TELE);
		playerFaceTile(9, 5);
		playerAnim(new Animation(2111));
		playerSpotAnim(new SpotAnim(184), 2);
		npcDestroy(GUTHIX);
		playerFaceTile(9, 7, 2);
		playerFaceTile(8, 6, 2);
		playerFaceTile(10, 6, 2);
		playerTalk("Huh?", 2);
		playerAnim(new Animation(857));
		playerTalk("Where am I?", 4);
		npcCreate(GUARD1, 296, 3, 7, 0);
		npcCreate(GUARD2, 298, 3, 5, 0);
		npcMove(GUARD1, 8, 7, MoveType.WALK);
		npcMove(GUARD2, 8, 5, MoveType.WALK, 3);
		npcTalk(GUARD1, "You! What are you doing here?");
		playerFaceTile(8, 6, 4);
		playerTalk("Idk... Walking??", 3);
		npcTalk(GUARD1, "You must have slipped", 2);
		npcTalk(GUARD1, "and hit your head on the ice.", 2);
		npcTalk(GUARD2, "Does it matter?", 2);
		npcTalk(GUARD1, "Lets just take him to Falador...", 3);
		npcMove(GUARD1, 15, 7, MoveType.WALK);
		playerMove(15, 6, MoveType.WALK);
		npcMove(GUARD2, 15, 5, MoveType.WALK);
		playerTalk("What's Falador?");
		npcTalk(GUARD2, "Dammit...", 6);
		constructMap(369, 421, 4, 6);
		musicEffect(214);
		npcCreate(GUARD1, 296, 12, 38, 0);
		npcCreate(GUARD2, 298, 14, 38, 0);
		playerMove(13, 37, 0, MoveType.TELE);
		camPos(14, 5, 5000);
		camLook(14, 20, 3000);
		playerMove(13, 25, MoveType.WALK);
		npcMove(GUARD1, 12, 24, MoveType.WALK);
		npcMove(GUARD2, 14, 24, MoveType.WALK);
		camPos(14, 16, 4000, 6, 6, 11);
	}
}
