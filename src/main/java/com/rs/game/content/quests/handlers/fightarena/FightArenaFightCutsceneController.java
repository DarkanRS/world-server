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
package com.rs.game.content.quests.handlers.fightarena;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;
import com.rs.game.region.Region;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

public class FightArenaFightCutsceneController extends Controller {
	public DynamicRegionReference instance;
	List<NPC> dynamicNPCs = new ArrayList<>();
	WorldTile locationOnFail = new WorldTile(2617, 3167, 0);
	WorldTile locationOnVictory = new WorldTile(2617, 3172, 0);
	WorldTile spawn = null;
	boolean canLeave = false;
	boolean playerHasDied = false;

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
		player.lock();
		instance = new DynamicRegionReference(10, 10);
		instance.copyMapAllPlanes(320, 391, () -> {
			spawn = instance.getLocalTile(57, 39);

			WorldTasks.schedule(new WorldTask() {
				int tick;
				NPC jeremy;
				NPC ogre;
				NPC father;
				NPC scorpion;
				NPC bouncer;
				NPC general_khazard;
				@Override
				public void run() {
					if(playerHasDied) {
						tick = -1;
						player.unlock();
						stop();
					}
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
							if(npc.getId() == 7522)
								ogre = npc;
							if(npc.getId() == 267)
								father = npc;
							if(npc.getId() == 7521)
								bouncer = npc;
						}
					}
					if (tick == 5) {
						player.getInterfaceManager().setFadingInterface(170);
						player.getPackets().setBlockMinimapState(0);
					}
					if(tick == 6)
						player.faceWest();
					if(tick == 7)
						player.setNextAnimation(new Animation(2098));
					if(tick == 8) {
						jeremeysCell(true);
						jeremy.setRandomWalk(false);
						jeremy.faceEntity(player);
					}
					if(tick == 9) {
						jeremy.walkToAndExecute(spawn, ()->{
							jeremy.faceNorth();
							jeremy.forceTalk("I'll run ahead");
							tick++;
						});
						player.walkToAndExecute(instance.getLocalTile(57, 40), () -> {
							player.faceSouth();
						});
					}
					if(tick == 11) {
						jeremeysCell(false);
						jeremy.walkToAndExecute(new WorldTile(jeremy.getX(), jeremy.getY()-12, jeremy.getPlane()), ()->{});
					}
					if(tick == 13) {
						general_khazard = new NPC(7551, new WorldTile(instance.getLocalX(45), instance.getLocalY(28), 0));
						general_khazard.faceWest();
						general_khazard.setRandomWalk(false);
						dynamicNPCs.add(general_khazard);

						player.getPackets().sendCameraPos(new WorldTile(instance.getLocalX(41), instance.getLocalY(35), 0), 1500);
						player.getPackets().sendCameraLook(new WorldTile(instance.getLocalX(44), instance.getLocalY(26), 0), 0);
						player.faceNorth();
						jeremy.faceNorth();
						player.setNextWorldTile(new WorldTile(instance.getLocalX(43), instance.getLocalY(26), player.getPlane()));
						jeremy.setNextWorldTile(new WorldTile(instance.getLocalX(42), instance.getLocalY(26), 0));
					}
					if(tick == 15) {
						player.faceEntity(jeremy);
						jeremy.faceEntity(player);
						player.startConversation(new Dialogue()
								.addPlayer(HeadE.SCARED, "Jeremy, where's your father?")
								.addNPC(jeremy.getId(), HeadE.CHILD_UNSURE, "Quick, help him! That beast will kill him. He's too old to fight.")
								.addNext(()->{
									tick = 17;
								})
						);
					}
					if(tick == 17) {
						ogre.setNextWorldTile(new WorldTile(instance.getLocalX(42), instance.getLocalY(40), 0));
						ogre.setRandomWalk(false);
						ogre.faceEntity(father);
						father.faceEntity(ogre);
						father.setLocked(true);
						father.setHitpoints(9999);
						ogre.setTarget(father);
						player.getPackets().sendResetCamera();
						player.unlock();
					}
					if(tick == 20) {
						ogre.forceTalk("Puny human go splat!");
						ogre.setTarget(father);
						father.setNextAnimation(new Animation(2836));
						ogre.setRandomWalk(true);
					}
					if(tick == 21) {
						if(ogre.hasFinished())
							tick = 22;
					}
					if(tick == 22) {
						player.lock();
						player.startConversation(new Dialogue()
								.addNPC(7535, HeadE.CALM_TALK, "You saved both my life and that of my son. I am eternally in your debt, brave traveller.", () ->{
									father.faceEntity(player);
									player.faceEntity(father);
								})
								.addNPC(7551, HeadE.CALM_TALK, "Haha! Well done, well done. That was rather entertaining. I am the great General Khazard" +
										" and the two men you just \'saved\' are my property.", () ->{
									general_khazard.faceEntity(player);
								})
								.addPlayer(HeadE.HAPPY_TALKING, "They belong to nobody.", ()->{
									player.faceEntity(general_khazard);
								})
								.addNPC(7551, HeadE.CALM_TALK, "Well, I suppose we could make some arrangement for their freedom.")
								.addPlayer(HeadE.HAPPY_TALKING, "What do you mean?")
								.addNPC(7551, HeadE.CALM_TALK, "I'll let them go, but you must stay and fight!")
								.addNext(()->{tick = 24;})
						);
					}
					if(tick == 24) {
						player.getInterfaceManager().setFadingInterface(115);
					}
					if(tick == 27) {
						player.getPackets().setBlockMinimapState(2);
						scorpion = new NPC(271,
								new WorldTile(instance.getLocalX(45), instance.getLocalY(31), 0), true);
						dynamicNPCs.add(scorpion);
					}
					if(tick == 29) {
						player.getInterfaceManager().setFadingInterface(170);
						player.getPackets().setBlockMinimapState(0);
						scorpion.setTarget(player);
						player.unlock();
					}
					if(tick == 30) {
						if(scorpion.hasFinished())
							tick = 31;
					}
					if(tick == 31) {
						player.lock();
						player.startConversation(new Dialogue()
								.addNPC(7551, HeadE.EVIL_LAUGH, "Let's see how you do against this!")
								.addNext(()->{tick = 33;})
						);
					}
					if(tick == 33) {
						player.getInterfaceManager().setFadingInterface(115);
					}
					if(tick == 36) {
						player.getPackets().setBlockMinimapState(2);
						bouncer.setNextWorldTile(new WorldTile(instance.getLocalX(45), instance.getLocalY(34), 0));
					}
					if(tick == 38) {
						player.getPackets().setBlockMinimapState(0);
						player.getInterfaceManager().setFadingInterface(170);
						bouncer.setTarget(player);
						player.unlock();
					}
					if(tick == 39) {
						if(bouncer.hasFinished())
							tick = 40;
					}
					if(tick == 40) {
						player.lock();
						player.startConversation(new Dialogue()
								.addNPC(7551, HeadE.AMAZED, "Nooooo! Bouncer! How dare you? For his sake you'll suffer, traveller." +
										" Prepare to meet your maker.", () ->{general_khazard.faceEntity(player);})
								.addPlayer(HeadE.HAPPY_TALKING, "You agreed to let the Servils go if I stayed to fight.", ()->{player.faceEntity(general_khazard);})
								.addNPC(7551, HeadE.CALM_TALK, "Indeed you shall see that I am not cowardly enough to make false promises. They may go.")
								.addNPC(7551, HeadE.CALM_TALK, "You, however, have caused me much trouble today. You will remain here so that I may have the pleasure of killing you myself.")
								.addNPC(jeremy.getId(), HeadE.CHILD_UNSURE, "No, you don't have to fight him! Come with us, " + player.getDisplayName() + "!", ()->{
									jeremy.faceEntity(player);
								})
								.addNext(()->{
									canLeave = true;
									tick = 42;
								})
						);
					}
					if(tick == 42) {
						player.getInterfaceManager().setFadingInterface(115);

					}
					if(tick == 45) {
						player.getPackets().setBlockMinimapState(2);
						dynamicNPCs.add(new NPC(7552, general_khazard.getTile(), true));
						general_khazard.finish();
					}
					if(tick == 47) {
						player.getInterfaceManager().setFadingInterface(170);
						player.unlock();
						player.getPackets().setBlockMinimapState(0);
						stop();
					}
					if(tick != 9 && tick != 16 && tick != 21 && tick != 23 && tick != 30 && tick != 32 && tick != 39 && tick != 41)
						tick++;
				}
			}, 0, 1);
		});
	}

	public boolean processObjectClick1(GameObject object) {
		if(object.getId() == 82 && canLeave) {
			WorldTasks.schedule(new WorldTask() {
				int tick;
				@Override
				public void run() {
					if(tick == 0) {
						player.getInterfaceManager().setFadingInterface(115);
					}
					if (tick == 2) {// setup player
						player.getPackets().setBlockMinimapState(2);
						player.setNextWorldTile(locationOnVictory);
						player.getQuestManager().setStage(Quest.FIGHT_ARENA, FightArena.RETURN_TO_LADY_SERVIL);
					}
					if(tick == 5) {
						player.getInterfaceManager().setFadingInterface(170);
						player.getPackets().setBlockMinimapState(0);
						forceClose();
					}
					if(tick == 6) {
						player.getVars().setVarBit(6163, 2);
						stop();
					}
					tick++;
				}
			}, 0, 1);
			return true;
		}
		player.startConversation(new Dialogue().addPlayer(HeadE.AMAZED, "I just can't right now!!"));
		return false;
	}

	public boolean processObjectClick2(GameObject object) {
		player.startConversation(new Dialogue().addPlayer(HeadE.AMAZED, "I just can't right now!!"));
		return false;
	}

	public boolean processObjectClick3(GameObject object) {
		player.startConversation(new Dialogue().addPlayer(HeadE.AMAZED, "I just can't right now!!"));
		return false;
	}

	public boolean processObjectClick4(GameObject object) {
		player.startConversation(new Dialogue().addPlayer(HeadE.AMAZED, "I just can't right now!!"));
		return false;
	}

	@Override
	public boolean sendDeath() {
		playerHasDied = true;
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
