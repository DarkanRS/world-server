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
//  Copyright � 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.quests.dragonslayer;

import com.rs.game.World;
import com.rs.engine.cutscene.Cutscene;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.Entity.MoveType;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;

public class BoatCutscene extends Cutscene {
	
	private static final int TRAVEL_INTERFACE = 299;
	private static final int BOAT_TO_CRANDOR = 544;

	@Override
	public void construct(Player player) {
		setEndTile(Tile.of(2849, 3239, 0));
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
		npcFaceNPC("ned", "jenkins");
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
		projectile(1, Tile.of(17, 9, 1), Tile.of(17, 12, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0));
				
		dialogue(new Dialogue().addNPC(6084, HeadE.SCARED, "It's the dragon!"), true);
		projectile(1, Tile.of(17, 9, 1), Tile.of(16, 12, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0));
				
		projectile(1, Tile.of(17, 9, 1), Tile.of(15, 12, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0));
		
		action(() -> player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(546));
		projectile(2, Tile.of(17, 9, 1), Tile.of(13, 12, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 500));

		action(() -> player.getInterfaceManager().closeInterfacesOverGameWindow());
		camShake(1, 0, 8, 5, 8);
		camPos(19, 14, 1200);
		camLook(17, 14, 700);
		camPos(26, 14, 1200, 0, 3);
		npcFaceTile("ned", 13, 14);
		npcFaceTile("jenkins", 13, 14);
		action(() -> player.getInterfaceManager().closeInterfacesOverGameWindow());
		projectile(1, Tile.of(13, 11, 1), Tile.of(13, 14, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 500));

		projectile(1, Tile.of(15, 11, 1), Tile.of(15, 14, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0, () -> getNPC("jenkins").setNextAnimation(new Animation(2105))));
		projectile(1, Tile.of(16, 17, 1), Tile.of(16, 14, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0, () -> getNPC("ned").setNextAnimation(new Animation(4280))));
		projectile(1, Tile.of(17, 17, 1), Tile.of(17, 14, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0, () -> getNPC("jenkins").setNextAnimation(new Animation(6649))));
		projectile(1, Tile.of(18, 17, 1), Tile.of(18, 14, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0, () -> getNPC("jenkins").setNextAnimation(new Animation(836))));
		projectile(1, Tile.of(19, 17, 1), Tile.of(19, 14, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0));
		projectile(1, Tile.of(41, 17, 1), Tile.of(41, 14, 1), 1155, 99, 0, 0, 0.5, 0, 0, p -> repeatFireSpotAnim(player, p.getDestination(), 0));

		camPos(34, 14, 1900);
		camLook(17, 14, 700);
		camPos(28, 14, 1200, 0, 3);
		
		delay(1);
		
		dialogue(new Dialogue().addNPC(6084, HeadE.SCARED, "We're going to sink!"), true);
		npcFaceTile("ned", 18, 12);
		playerFaceDir(Direction.EAST);
		
		dialogue(new Dialogue().addPlayer(HeadE.AMAZED_MILD, "Look! Land ahead!"), true);
		delay(1);
		
		camPos(20, 14, 1200, 0, 3);
		playerFaceTile(18, 13);
		npcFaceDir("ned", Direction.EAST);
		delay(1);
		
		dialogue(new Dialogue().addNPC(6084, HeadE.AMAZED, "We're going to crash!"), true);
		delay(1);
		
		camShake(1, 5, 8, 25, 8);
		dialogue(new Dialogue().addSimple("CRASH!"));
		delay(2);
		
		fadeInBG(3);
		action(() -> player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(516));
		action(1, () -> player.setNextTile(getEndTile()));
		playerFaceDir(Direction.NORTH);
		camShakeReset();
		camPosReset();
		dialogue(new Dialogue().addSimple("You are knocked unconcious and later awake on an ash-strewn beach."), true);
		delay(1);
		
		playerAnim(new Animation(4191));
		fadeOutBG(7);
		action(() -> { 
			player.getInterfaceManager().closeInterfacesOverGameWindow();
			player.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(DragonSlayer.FINISHED_BOAT_SCENE_ATTR, true);
		});
	}
	
	private void repeatFireSpotAnim(Player player, Tile target, int fireHeight, Runnable extra) {
		WorldTasks.scheduleTimer(tick -> {
			if (tick == 0) {
				World.sendSpotAnim(Tile.of(target.getX(), target.getY(), target.getPlane()), new SpotAnim(1154));
				World.sendSpotAnim(Tile.of(target.getX(), target.getY(), target.getPlane()), new SpotAnim(2588));
				if (extra != null)
					extra.run();
			}
			if (tick > 1)
				World.sendSpotAnim(Tile.of(target.getX(), target.getY(), target.getPlane()), new SpotAnim(453, 0, fireHeight));
			if(tick == 80)
				return false;
			return true;
		});
	}
	
	private void repeatFireSpotAnim(Player player, Tile target, int fireHeight) {
		repeatFireSpotAnim(player, target, fireHeight, null);
	}
	
}
