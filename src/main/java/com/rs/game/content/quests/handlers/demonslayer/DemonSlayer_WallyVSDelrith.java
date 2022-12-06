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
package com.rs.game.content.quests.handlers.demonslayer;

import com.rs.game.World;
import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

public class DemonSlayer_WallyVSDelrith extends Controller {
	private static final int WALLY = 4664;
	private static final int GYPSY_ARIS = 882;
	private DynamicRegionReference instance;
	private WorldTile locationBeforeCutscene;
	private WorldTile spawn;

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
        return false;
    }

	private void playCutscene() {
		locationBeforeCutscene = WorldTile.of(player.getX(), player.getY(), player.getPlane());
		player.lock();
		instance = new DynamicRegionReference(4, 4);
		instance.copyMapAllPlanes(401, 419, () -> {
			spawn = instance.getLocalTile(19, 17);
			Logger.debug(DemonSlayer_WallyVSDelrith.class, "playCutscene", spawn);

			WorldTasks.schedule(new WorldTask() {
				int tick;
				NPC npc;

				@Override
				public void run() {
					if (tick == 0) { // setup p1
						player.getInterfaceManager().fadeIn();
						player.musicTrack(-1);
					} else if (tick == 3) {// setup p2, move player
						player.getPackets().setBlockMinimapState(2);
						player.setNextWorldTile(spawn);
						player.getAppearance().transformIntoNPC(266);
					} else if (tick == 5) {// setup p3, camera
						player.getPackets().sendCameraShake(1, 0, 10, 5, 10);
						player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()), 1300);
						player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()) + 4, player.getYInScene(player.getSceneBaseChunkId()) - 4, 50);
					} else if (tick == 6) {// start scene
						player.getInterfaceManager().fadeOut();
						player.musicTrack(196);
						npc = World.spawnNPC(WALLY, WorldTile.of(player.getX() - 1, player.getY() - 5, player.getPlane()), -1, false, true);
						npc.setRandomWalk(false);
					} else if (tick == 7)
						player.startConversation(new Conversation(player) {
							{
								addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "Wally managed to arrive at the stone circle just as Delrith was summoned by a cult of chaos" + " druids...");
								addNext(() -> {
									tick++;
								});
								create();
							}
						});
					else if (tick == 8) { // Wally #1
						npc.faceEntity(player);
						npc.setRun(true);
						npc.setForceWalk(WorldTile.of(spawn.getX(), spawn.getY() - 2, spawn.getPlane()));
						player.startConversation(new Conversation(player) {
							{
								addNPC(WALLY, HeadE.TALKING_ALOT, "Die foul demon!");
								create();
							}
						});
						player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()) - 4, 0, 4, 4);
					} else if (tick == 9) {
						npc.faceEntity(player);
						npc.setNextAnimation(new Animation(12311));
					} else if (tick == 10)
						npc.setNextAnimation(new Animation(2394));
					else if (tick == 11) {
						npc.setNextAnimation(new Animation(16290));
						player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()) - 3, 0, 1, 1);
						player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()), 250, 0, 40);
					} else if (tick == 13)
						player.startConversation(new Conversation(player) { // Wally #2
							{
								addNPC(WALLY, HeadE.TALKING_ALOT, "Now, what was that incantation again?");
								addNext(() -> {
									tick++;
								});
								create();
							}
						});
					else if (tick == 14) {
						npc.faceEntity(player);
						player.startConversation(new Conversation(player) { // Wally #2
							{
								addNPC(WALLY, HeadE.TALKING_ALOT, "Aber, Gabindo, Purchai, Camerinthum, and Carlem");
								addNext(() -> {
									tick++;
								});
								create();
							}
						});
					} else if (tick == 17)
						player.getInterfaceManager().setFadingInterface(115);
					else if (tick == 20) {// setup scene 2
						player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()) - 2, player.getYInScene(player.getSceneBaseChunkId()) - 5, 1450); // move cam scene 2
						player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()) - 2, 600);// face Wally
					} else if (tick == 21)
						player.getInterfaceManager().removeInterface(115);
					else if (tick == 22) {
						npc.faceTile(WorldTile.of(spawn.getX() - 1, spawn.getY() - 3, 0));// face camera
						npc.setNextAnimation(new Animation(12625));
						player.startConversation(new Conversation(player) { // Wally #2
							{
								addNPC(WALLY, HeadE.TALKING_ALOT, "I am the greatest demon slayer EVER!");
								addNext(() -> {
									tick++;
								});
								create();
							}
						});
					} else if (tick == 23)
						player.startConversation(new Conversation(player) {
							{
								addNPC(GYPSY_ARIS, HeadE.TALKING_ALOT, "By reciting the correct magical incantation, and thrusting Silverlight into Delrith " + "while he was newly summoned, Wally was able to imprison Delrith in the stone block in the centre of the circle.");
								addNext(() -> {
									player.getInterfaceManager().setFadingInterface(115);
									tick++;
								});
								create();
							}
						});
					else if (tick == 26) {// closing p1
						player.getPackets().setBlockMinimapState(0);
						player.getControllerManager().forceStop();
						player.getAppearance().transformIntoNPC(-1);
						player.getPackets().sendStopCameraShake();
					} else if (tick == 27) {// closing p2
						player.getInterfaceManager().setFadingInterface(170);
						player.musicTrack(125);
						player.getTempAttribs().setB("DemonSlayerCutscenePlayed", true);
						player.startConversation(new GypsyArisDemonSlayerD(player, 1).getStart());
						player.unlock();
						stop();
					}
					if (tick != 7 && tick != 13 && tick != 14 && tick != 22 && tick != 23)
						tick++;
				}
			}, 0, 1);
		});
	}

	@Override
	public boolean login() {
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
		player.setNextWorldTile(locationBeforeCutscene);
		removeInstance();
		player.unlock();
		removeController();
	}

	private void removeInstance() {
		instance.destroy();
	}
}
