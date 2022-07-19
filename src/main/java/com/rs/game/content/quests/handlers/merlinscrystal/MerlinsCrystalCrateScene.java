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
package com.rs.game.content.quests.handlers.merlinscrystal;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.utils.music.Genre;
import com.rs.utils.music.Music;

public class MerlinsCrystalCrateScene extends Controller {
	private DynamicRegionReference instance;
	private WorldTile locationBeforeCutscene;
	private WorldTile insideCrate;
	private WorldTile destination = new WorldTile(2779, 3400, 0);

	@Override
	public void start() {
		player.lock();
		locationBeforeCutscene = new WorldTile(player.getX(), player.getY(), player.getPlane());
		instance = new DynamicRegionReference(4, 4);
		playCutscene();
	}

    @Override
    public Genre getGenre() {
        return Music.getGenreByName("Other Kandarin");
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
		instance.copyMapAllPlanes(347, 1229, () -> {
			insideCrate = instance.getLocalTile(2, 7);
			WorldTasks.schedule(new WorldTask() {
				int tick;
				static final int CROUCH_CRATE_ANIM = 14592;
				@Override
				public void run() {
					if (tick == 0)
						player.getInterfaceManager().setFadingInterface(115);
					else if (tick == 3) {// setup p2, move player
                        player.getPackets().sendMusic(-1, 100, 255);
						player.getPackets().setBlockMinimapState(2);
						player.setNextWorldTile(insideCrate);
					} else if (tick == 4) {
						player.setNextAnimation(new Animation(CROUCH_CRATE_ANIM));
						player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId())-3, player.getYInScene(player.getSceneBaseChunkId())-3, 4000);
						player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()), 200);
						player.faceTile(new WorldTile(player.getX()+1, player.getY()-1, player.getPlane()));
					} else if (tick == 6)
						player.getInterfaceManager().setFadingInterface(170);
					else if (tick == 8)
						player.startConversation(new Conversation(player) {
							{
								addSimple("You climb inside the crate and wait. And wait...");
								addNext(()->{tick++;});
								create();
							}
						});
					else if (tick == 13)
						player.startConversation(new Conversation(player) {
							{
								addSimple("You hear voices outside the crate....");
								addSimple("<col=0000FF>Is this your crate, Arhein?</col>");
								addSimple("<col=FF0000>Yeah, I think so. Pack it aboard soon as you can. I'm on a tight schedule for deliveries!</col>");
								addSimple("You feel the crate being lifted.");
								addSimple("<col=0000FF>Oof. Wow, this is pretty heavy! I never knew candles weighed so much!</col>");
								addSimple("<col=FF0000>Quit your whining, and stow it in the hold.</col>");
								addSimple("You feel the crate being put down inside the ship. You wait... And wait...");
								addNext(()->{tick++;});
								create();
							}
						});
					else if(tick == 20)
						player.startConversation(new Conversation(player) {
							{
								addSimple("<col=FF0000>Casting off!</col>");
								addSimple("You feel the ship start to move. Feels like you're now out at sea. The ship comes to a stop.");
								addSimple("<col=FF0000>Unload Mordred's deliveries onto the jetty.</col>");
								addSimple("<col=0000FF>Aye-aye cap'n!</col>");
								addSimple("You feel the crate being lifted. You can hear someone mumbling outside the crate.");
								addSimple("<col=0000FF>...stupid Arhein...making me...candles...never weigh THIS much...hurts...union about this!...</col>");
								addSimple("<col=0000FF>...if...MY ship be different!...stupid Arhein...</col>");
								addSimple("You feel the crate being put down.");
								addNext(()->{tick++;});
								create();
							}
						});
					else if(tick == 25)
						player.startConversation(new Conversation(player) {
							{
								addOptions("Climb out the crate?", new Options() {
									@Override
									public void create() {
										option("Yes", new Dialogue()
												.addNext(()->{tick++;}));
										option("No", new Dialogue()
												.addNext(()->{tick = 22;}));
									}
								});

								addNext(()->{tick++;});
								create();
							}
						});
					else if(tick == 27)
						player.getInterfaceManager().setFadingInterface(115);
					else if(tick == 30) {
						player.getPackets().setBlockMinimapState(0);
						player.setNextWorldTile(destination);
						player.getPackets().sendResetCamera();
						player.setNextAnimation(new Animation(-1));
					} else if(tick == 31)
						player.faceWest();
					else if(tick == 33) {
						player.getInterfaceManager().setFadingInterface(170);
						removeInstance();
						player.getControllerManager().forceStop();
						player.unlock();
						stop();
					}
					if(tick != 8+1 && tick != 13+1 && tick != 20+1 && tick != 25+1)
						tick++;
				}
			}, 0, 1);
		});
	}

	@Override
	public boolean login() {
		player.getControllerManager().forceStop();
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
		player.getPackets().setBlockMinimapState(0);
	}

	private void removeInstance() {
		instance.destroy();
	}
}
