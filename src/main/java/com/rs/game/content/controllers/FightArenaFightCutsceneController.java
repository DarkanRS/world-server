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
package com.rs.game.content.controllers;

import com.rs.game.World;
import com.rs.game.content.commands.Commands;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.Region;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.rs.game.content.world.doors.Doors.handleDoor;

public class FightArenaFightCutsceneController extends Controller {
	public DynamicRegionReference instance;
	List<NPC> dynamicNPCs = new ArrayList<>();
	WorldTile locationOnFail = new WorldTile(2617, 3167, 0);
	WorldTile locationOnVictory = new WorldTile(3228, 3368, 0);
	WorldTile spawn = null;

	@Override
	public void start() {
		playCutscene();
	}

    @Override
    public Genre getGenre() {
        return Music.getGenreByName("Other Dungeons");
    }

    @Override
    public boolean playAmbientOnControllerRegionEnter() {
        return false;
    }

    @Override
    public boolean playAmbientMusic() {
        return true;
    }

	private void playCutscene() {
		player.lock(9);
		instance = new DynamicRegionReference(10, 10);
		instance.copyMapAllPlanes(320, 391, () -> {
			spawn = instance.getLocalTile(57, 39);

			WorldTasks.schedule(new WorldTask() {
				int tick;
				NPC jeremy;
				@Override
				public void run() {
					if (tick == 0)
						player.getInterfaceManager().setFadingInterface(115);
					if (tick == 2) {// setup player
						player.getPackets().setBlockMinimapState(2);
						player.setNextWorldTile(spawn);
					}
					if (tick == 3) {
						addAllFightArenaNPCs();
						for (NPC npc : dynamicNPCs) {
							if (npc.getId() == 265)
								jeremy = npc;
						}
						// Setup camera
//						player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()) - 4, player.getYInScene(player.getSceneBaseChunkId()) + 6, 2000);
//						player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()), 50);
					}
					if (tick == 5) {
						player.getInterfaceManager().setFadingInterface(170);
						player.getPackets().setBlockMinimapState(0);
					}
//					if(tick == 6)
//						player.faceWest();
//					if(tick == 7)
//						player.setNextAnimation(new Animation(2098));
//					if(tick == 8) {
//						jeremeysCell(true);
//						jeremy.setRandomWalk(false);
//						jeremy.faceEntity(player);
//					}
//					if(tick == 9) {
//						jeremy.walkToAndExecute(new WorldTile(player.getX(), player.getY(), player.getPlane()), ()->{
//							jeremeysCell(false);
//							jeremy.faceNorth();
//							jeremy.forceTalk("I'll run ahead");
//							tick++;
//						});
//						player.walkToAndExecute(new WorldTile(player.getX(), player.getY() + 1, player.getPlane()), () -> {
//							player.faceSouth();
//						});
//					}
//					if(tick == 11) {
//						jeremy.walkToAndExecute(new WorldTile(jeremy.getX(), jeremy.getY()-12, jeremy.getPlane()), ()->{});
//					}
//					if(tick == 13) {
////						player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()) - 4, player.getYInScene(player.getSceneBaseChunkId()) + 6, 2000);
////						player.setNextWorldTile(new WorldTile(player.getX()-7, player.getY()-17, player.getPlane()));
//					}

					if(tick != 10)
						tick++;
				}
			}, 0, 1);
		});
	}

	@Override
	public boolean sendDeath() {
		player.stopAll();
		player.reset();
		player.sendMessage("You have been defeated!");
		player.setNextWorldTile(locationOnFail);
		player.getVars().setVarBit(2569, 0);
		forceClose();
		return false;
	}

	@Override
	public boolean login() {
		player.setNextWorldTile(locationOnFail);
		forceClose();
		return false;
	}

	@Override
	public boolean logout() {
		removeInstance();
		player.unlock();
		return false;
	}

	@Override
	public void forceClose() {
		player.getPackets().setBlockMinimapState(0);
		player.setForceMultiArea(false);
		removeInstance();
//		player.getTempAttribs().setB("FinalDemonSlayerCutscene", false);
		player.unlock();
		removeController();
	}

	private void removeInstance() {
		removeDynamicNPCs();
		instance.destroy();
	}

	private void addAllFightArenaNPCs() {
		Region fightArena = World.getRegion(10289, true);//Trent is this perma loaded after this?
		List<NPC> originalNPCs = World.getNPCsInRegion(10289);
		List<Integer> xDiff = new ArrayList<>();
		List<Integer> yDiff = new ArrayList<>();
		for(NPC npc : originalNPCs) {
			xDiff.add(2617 - npc.getX());//Same spawn X as dynamic but from original Fight Arena
			yDiff.add(3167 - npc.getY());//Same spawn Y
		}
		if(fightArena.getPlayerIndexes().size() == 0)
			fightArena.removeMapFromMemory();

		if(spawn != null)
			for(int i = 0; i < originalNPCs.size(); i++) {
				NPC npc = originalNPCs.get(i);
				dynamicNPCs.add(new NPC(npc.getId(),
						new WorldTile(spawn.getX() - xDiff.get(i), spawn.getY() - yDiff.get(i), npc.getPlane()), true));
			}
	}

	private void jeremeysCell(boolean open) {
		GameObject door = World.getClosestObject(80, player.getTile());
		if(open) {
			if(door.getRotation() == 3)
				return;
			World.removeObject(door);
			door.setRotation(3);
			door.setLocation(door.getX() - 1, door.getY(), door.getPlane());
			World.spawnObject(door);
			return;
		}
		if(door.getRotation() == 0)
			return;
		World.removeObject(door);
		door.setRotation(0);
		door.setLocation(door.getX()+1, door.getY(), door.getPlane());
		World.spawnObject(door);;
	}

	private void removeDynamicNPCs() {
		for(NPC npc : dynamicNPCs)
			npc.finish();
	}
}
