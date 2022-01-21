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
package com.rs.game.player.quests.handlers.dragonslayer;

import com.rs.game.World;
import com.rs.game.Entity.MoveType;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class BoatCutscene extends Cutscene {
	
	private static final int TRAVEL_INTERFACE = 299;
	private static final int BOAT_TO_CRANDOR = 544;

	@Override
	public void construct(Player player) {
		fadeInBG(2);
		hideMinimap();
		dynamicRegion(256, 688, 8, 8);
		npcCreate("ned", 6084, 18, 13, 1);
		npcCreate("jenkins", 6085, 15, 14, 1);
		playerMove(18, 12, 1, MoveType.TELE);
		music(350);
		
		action(7, () -> {
			player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(516);
			player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(TRAVEL_INTERFACE);
			player.getPackets().setIFHidden(TRAVEL_INTERFACE, 44, false);
		});
				
		action(4, () -> player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(BOAT_TO_CRANDOR));
				
		action(() -> player.getInterfaceManager().closeInterfacesOverGameWindow());
		
		fadeOutBG(-1);
		playerFaceEntity("ned");
		npcFaceDir("ned", Direction.EAST);
		camShake(1, 0, 8, 5, 8);
		camPos(28, 14, 0);
		camLook(21, 13, 0);
		camPos(26, 13, 600, 0, 4);
		dialogue(new Dialogue().addNPC(6084, HeadE.HAPPY_TALKING, "Ah, it's good to feel the salt spray on my face once again!"));
		
		delay(4);
		
		camPos(26, 9, 2050, 0, 2);
		dialogue(new Dialogue()
				.addNPC(6084, HeadE.HAPPY_TALKING, "And this is a mighty fine ship. She don't look much but she handles like a dream.")
				.addPlayer(HeadE.CALM_TALK, "How much longer until we reach Crandor?")
				.addNPC(6084, HeadE.HAPPY_TALKING, "Not long now! According to the chart, we'd be able to see Crandor if it wasn't for the clouds on the horizon."), true);
		dialogue(new Dialogue().addSimple("Clouds surround the ship."));
		music(360);
		action(1, () -> player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(543));
		camShakeReset(1);
		
		action(() -> player.getInterfaceManager().closeInterfacesOverGameWindow());
		playerFaceTile(15, 14);
		npcMove("jenkins", 18, 14, MoveType.WALK);
		dialogue(new Dialogue().addNPC(6085, HeadE.HAPPY_TALKING, "Looks like there's a storm coming up, cap'n. Soon we won't be able to see anything!"), true);
		camPos(23, 21, 5000);
		camLook(14, 14, 0);
		camPos(18, 21, 5000, 0, 2);
		
		delay(3);
		
		npcFaceDir("ned", Direction.NORTH);
		playerFaceDir(Direction.NORTH);
		npcFaceEntity("ned", "jenkins");
		dialogue(new Dialogue().addNPC(6084, HeadE.HAPPY_TALKING, "Oh, well. The weather had been so good up until now."), true);
		
		action(1, () -> player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(545));
		action(1, () -> player.getInterfaceManager().closeInterfacesOverGameWindow());
		
		dialogue(new Dialogue()
				.addPlayer(HeadE.SCARED, "Did you see that?")
				.addNPC(6084, HeadE.CALM_TALK, "See what?")
				.addPlayer(HeadE.SCARED, "I thought I saw something above us."), true);
		camPos(24, 13, 1150);
		camLook(18, 13, 0, 0);
		camPos(24, 19, 0, 1150, 3);
		
		delay(1);
		
		npcFaceTile("ned", 17, 12);
		npcFaceTile("jenkins", 17, 12);
		playerFaceTile(17, 12);
		projectile(new WorldTile(17, 9, 1), new WorldTile(17, 12, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> {
			WorldTasks.scheduleTimer(tick -> {
				if (tick == 0) {
					World.sendSpotAnim(player, new SpotAnim(1154), new WorldTile(p.getDestination().getX(), p.getDestination().getY(), p.getDestination().getPlane()));
					World.sendSpotAnim(player, new SpotAnim(2588), new WorldTile(p.getDestination().getX(), p.getDestination().getY(), p.getDestination().getPlane()));
				}
				if (tick > 1)
					World.sendSpotAnim(player, new SpotAnim(453), new WorldTile(p.getDestination().getX(), p.getDestination().getY(), p.getDestination().getPlane()));
				if(tick == 80)
					return false;
				return true;
			});
		});
		
		
		
	}

}
