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
package com.rs.game.content.miniquests.huntforsurok.bork;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.content.miniquests.huntforsurok.ChaosTunnels;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;

public class BorkController extends InstancedController {
	private boolean quest;

	public BorkController(boolean quest) {
		super(Instance.of(ChaosTunnels.PortalPair.BORK.tile1, 8, 8).setEntranceOffset(new int[] { 43, 24, 0 }));
		this.quest = quest;
	}

	@Override
	public void onBuildInstance() {
		player.lock();
		getInstance().copyMapAllPlanes(384, 688).thenAccept(b -> player.playCutscene(cs -> {
			cs.fadeIn(5);
			cs.hideMinimap(true);
			cs.action(() -> {
				getInstance().teleportLocal(player, 43, 24, 0);
				player.setForceMultiArea(true);
				cs.setEndTile(quest ? Tile.of(cs.getX(39), cs.getY(25), 0) : Tile.of(cs.getX(43), cs.getY(24), 0));
			});
			cs.delay(0);
			if (quest) {
				cs.npcCreate("surok", 7002, 39, 26, 0);
				cs.camLook(41, 24, 0);
				cs.camPos(33, 16, 12324);
				cs.camPos(29, 28, 3615, 0, 10);
				cs.fadeOut(5);
				cs.playerMove(39, 25, Entity.MoveType.WALK);
				cs.delay(3);
				cs.npcFaceTile("surok", 39, 25);
				cs.playerFaceTile(39, 26);
				cs.dialogue(new Dialogue()
								.addPlayer(HeadE.FRUSTRATED, "It's a dead end, Surok. There's nowhere left to run.")
								.addNPC(7002, HeadE.FRUSTRATED, "You're wrong, " + player.getDisplayName()+". I am right where I need to be.")
								.addPlayer(HeadE.FRUSTRATED, "What do you mean? You won't escape.")
								.addNPC(7002, HeadE.FRUSTRATED, "You cannot stop me, " + player.getDisplayName() + ". But just in case you try, allow me to introduce you to someone..."), true);
				cs.npcFaceDir("surok", Direction.WEST);
				cs.npcTalk("surok", "Bork! Kill the meddler!");
				cs.delay(1);
				cs.playerFaceDir(Direction.WEST);
				cs.delay(2);
				cs.playerAnim(EmotesManager.Emote.SCARED.getAnim());
				cs.delay(3);
			}
			cs.action(() -> player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(692));
			cs.delay(15);
			cs.action(() -> player.getInterfaceManager().closeInterfacesOverGameWindow());
			cs.camPosReset();
			cs.fadeOut(3);
			cs.hideMinimap(false);
			cs.action(() -> {
				World.spawnNPC(7134, Tile.of(cs.getX(27), cs.getY(33), 0), -1, true, true, true).setForceMultiArea(true);
				player.resetReceivedHits();
				player.unlock();
			});
		}));
	}

	@Override
	public void onDestroyInstance() {
		player.setForceMultiArea(false);
		player.unlock();
	}

	@Override
	public boolean processMagicTeleport(Tile toTile) {
		return true;
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 29537) {
			player.getControllerManager().forceStop();
			player.setNextSpotAnim(new SpotAnim(110, 10, 96));
			player.useStairs(-1, getInstance().getReturnTo(), 2, 3);
		}
		return true;
	}
}
