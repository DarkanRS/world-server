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
package com.rs.game.player.controllers;

import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn21Room;
import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn31Room;
import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn41Room;
import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn51Room;
import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn61Room;
import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn71Room;
import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn81Room;
import static com.rs.game.player.content.minigames.pyramidplunder.PyramidPlunder.isIn91Room;

import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.utils.Ticks;

public class PyramidPlunderController extends Controller {
    final static int PLUNDER_INTERFACE = 428;

	@Override
	public void start() { //600ms
		startMinigame();
	}

    public void startMinigame() {
        player.lock(11);
        WorldTasks.schedule(new WorldTask() {
            int tick;
            @Override
            public void run() {
                if(tick == 0)
                    player.getInterfaceManager().setFadingInterface(115);
                if(tick == 2) {
                    player.faceSouth();
                    player.setNextWorldTile(new WorldTile(1927, 4477, 0));
                }
                if(tick == 5)
                    player.getInterfaceManager().setFadingInterface(170);
                if(tick == 6)
                    startTimer();
                if(tick == 8)
                    stop();
                tick++;
            }
        }, 0, 1);
    }

    public void startTimer() {
        WorldTasks.scheduleTimer(tick -> {
            if(!player.getControllerManager().isIn(PyramidPlunderController.class))
                return false;
            player.getVars().setVarBit(2375, tick);
            if(tick == Ticks.fromMinutes(5)) {
                kickPlayer();
                return false;
            }
            if(tick % 5 == 0)
                updatePlunderInterface();
            return true;
        });
    }

    private void updatePlunderInterface() {
        player.getInterfaceManager().setOverlay(PLUNDER_INTERFACE);
        if(isIn21Room(player)) {
            player.getVars().setVar(822, 21);
            player.getVars().setVarBit(2377, 1);
        } else if(isIn31Room(player)) {
            player.getVars().setVar(822, 31);
            player.getVars().setVarBit(2377, 2);
        } else if(isIn41Room(player)) {
            player.getVars().setVar(822, 41);
            player.getVars().setVarBit(2377, 3);
        } else if(isIn51Room(player)) {
            player.getVars().setVar(822, 51);
            player.getVars().setVarBit(2377, 4);
        } else if(isIn61Room(player)) {
            player.getVars().setVar(822, 61);
            player.getVars().setVarBit(2377, 5);
        } else if(isIn71Room(player)) {
            player.getVars().setVar(822, 71);
            player.getVars().setVarBit(2377, 6);
        } else if(isIn81Room(player)) {
            player.getVars().setVar(822, 81);
            player.getVars().setVarBit(2377, 7);
        } else if(isIn91Room(player)) {
            player.getVars().setVar(822, 91);
            player.getVars().setVarBit(2377, 8);
        }
    }

    private void kickPlayer() {
        player.lock(6);
        player.startConversation(new Conversation(player) {
            {
                addNPC(4476, HeadE.FRUSTRATED, "You have had your five minutes, time to go!");
                create();
            }
        });
        WorldTasks.scheduleTimer(tick -> {
            if(tick == 1)
                player.getInterfaceManager().setFadingInterface(115);
            if(tick == 3)
                exitMinigame();
            if(tick == 6) {
                player.getInterfaceManager().setFadingInterface(170);
                return false;
            }
            return true;
        });

    }

	@Override
	public boolean login() {
		exitMinigame();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean sendDeath() {
        forceClose();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
        forceClose();
	}

	@Override
	public void forceClose() {
        player.getInterfaceManager().removeOverlay();
		removeController();
	}



	public void exitMinigame() {
		player.setNextWorldTile(new WorldTile(3288, 2801, 0));
        forceClose();
	}
}
