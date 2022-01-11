package com.rs.game.player.controllers;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.dragonslayer.DragonSlayer;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class DragonSlayer_BoatScene extends Controller {
	DynamicRegionReference instance;
	WorldTile startingTile;
	WorldTile crandor = new WorldTile(2849, 3239, 0);

	NPC captainNed;
	NPC cabinboyJenkins;

	//constants
	int TRAVEL_INTERFACE = 299;
	int BOAT_TO_CRANDOR = 544;
	int HAPPY_TRAVEL_JINGLE = 350; //Custom for this cutscene
	int CAPTAIN_NED = 6084;
	int CABIN_BOY_JENKINS = 6085;
	int ANIM_JENKINS_SHAKE = 2105;
	int ANIM_NED_FEAR = 4280;
	int ANIM_JENKINS_FEAR = 6649;
	int ANIM_JENKINS_DIE = 836;
	int ANIM_PLAYER_GET_UP = 4191;

	@Override
	public void start() {
		startingTile = new WorldTile(player.getX(), player.getY(), player.getPlane());
		player.getPackets().setBlockMinimapState(2);
		player.lock();
		player.getTempAttribs().setB("CUTSCENE_INTERFACE_CLOSE_DISABLED", true);
		playCutscene();
	}

	private void playCutscene() {
		instance = new DynamicRegionReference(8, 8);
		instance.copyMapAllPlanes(256, 688, () -> {
			captainNed = World.spawnNPC(CAPTAIN_NED, instance.getLocalTile(18, 13, 1), -1, false, true);
			cabinboyJenkins = World.spawnNPC(CABIN_BOY_JENKINS, instance.getLocalTile(15, 14, 1), -1, false, true);

			WorldTasks.schedule(new WorldTask() {
				int tick = 0;
				final int PAUSE_FOR_NED1 = 14;
				final int PAUSE_FOR_PLAYER1 = 15;
				final int PAUSE_FOR_NED2 = 16;
				final int PAUSE_FOR_NED3 = 22;
				final int PAUSE_FOR_PLAYER2 = 25;
				final int PAUSE_FOR_NED4 = 26;
				final int PAUSE_FOR_PLAYER3 = 27;
				final int PAUSE_FOR_NED5 = 42;
				final int PAUSE_FOR_PLAYER4 = 43;
				final int PAUSE_FOR_NED6 = 45;
				final int PAUSE_FOR_PLAYER5 = 52;

				@Override
				public void run() {
					if (tick == 0) {  //setup p1
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(115); //for interfaces over interfaces do this
						player.getPackets().sendMusic(HAPPY_TRAVEL_JINGLE, 5, 255);
					}
					if (tick == 3) {
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(516);
						player.getPackets().setBlockMinimapState(2);
						player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(TRAVEL_INTERFACE);
						player.getPackets().setIFHidden(TRAVEL_INTERFACE, 44, false);
					}
					if (tick == 9) {
						player.getInterfaceManager().sendForegroundInterfaceOverGameWindow(BOAT_TO_CRANDOR);
						player.setNextWorldTile(instance.getLocalTile(18, 12, 1));
					}

					if (tick == 11) {
						player.faceEntity(captainNed);
						captainNed.setNextFaceWorldTile(new WorldTile(captainNed.getX()+1, captainNed.getY(), captainNed.getPlane()));
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(170);
						player.getInterfaceManager().closeInterfacesOverGameWindow();

						player.getPackets().sendCameraShake(1, 0, 8, 5, 8);
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(28), instance.getLocalY(14), 0), 0);
						player.getPackets().sendCameraLook(player, new WorldTile(instance.getLocalX(21), instance.getLocalY(13), 0), 0);
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(26), instance.getLocalY(13), 0), 600, 0, 4);
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.HAPPY_TALKING, "Ah, it's good to feel the salt spray on my face once again!");
							}
						});

					}

					if(tick == PAUSE_FOR_NED1) {
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(26), instance.getLocalY(9), 0), 2050, 0, 2);
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.HAPPY_TALKING, "And this is a mighty fine ship. She don't look much but she handles like a dream.");
								addNext(() -> {
									tick++;
								});
								create();
							}
						});
					}
					if(tick == PAUSE_FOR_PLAYER1)
						player.startConversation(new Conversation(player) {
							{
								addPlayer(HeadE.CALM_TALK, "How much longer until we reach Crandor?");
								addNext(()->{
									tick++;
								});
								create();
							}
						});

					if(tick == PAUSE_FOR_NED2)
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.HAPPY_TALKING, "Now long now! According to the chart, we'd be able to see Crandor if it wasn't for " +
										"the clouds on the horizon.");
								addNext(()->{
									tick++;
								});
								create();
							}
						});

					if(tick == 17) {
						player.getPackets().sendMusic(360, 10, 255); //Elvarg's theme
						player.startConversation(new Conversation(player) {
							{
								addSimple("Clouds surround the ship.");
							}
						});
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(543);
					}

					if(tick == 18)
						player.getPackets().sendStopCameraShake();

					if(tick == 19) {
						player.getInterfaceManager().closeInterfacesOverGameWindow();
						player.setNextFaceWorldTile(new WorldTile(cabinboyJenkins.getX(), cabinboyJenkins.getY(), cabinboyJenkins.getPlane()));
						cabinboyJenkins.setForceWalk(new WorldTile(captainNed.getX(), captainNed.getY()+1, cabinboyJenkins.getPlane()));
						player.startConversation(new Conversation(player) {
							{
								addNPC(CABIN_BOY_JENKINS, HeadE.HAPPY_TALKING, "Looks like there's a storm coming up, cap'n. Soon we won't be able to see anything!");
							}
						});
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(23), instance.getLocalY(21), 0), 5000);
						player.getPackets().sendCameraLook(player, new WorldTile(instance.getLocalX(14), instance.getLocalY(14), 0), 0);
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(18), instance.getLocalY(21), 0), 5000, 0, 2);
					}

					if(tick == PAUSE_FOR_NED3) {
						captainNed.setNextFaceWorldTile(new WorldTile(captainNed.getX(), captainNed.getY()+1, captainNed.getPlane()));
						player.setNextFaceWorldTile(new WorldTile(player.getX(), player.getY()+1, player.getPlane()));
						cabinboyJenkins.faceEntity(captainNed);
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.HAPPY_TALKING, "Oh, well. The weather had been so good up until now.", () -> {});
								addNext(()->{
									tick++;
								});
								create();
							}
						});
					}

					if(tick == 23)
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(545);
					if(tick == 24)
						player.getInterfaceManager().closeInterfacesOverGameWindow();
					if(tick == PAUSE_FOR_PLAYER2)
						player.startConversation(new Conversation(player) {
							{
								addPlayer(HeadE.SCARED, "Did you see that?");
								addNext(()->{
									tick++;
								});
								create();
							}
						});

					if(tick == PAUSE_FOR_NED4)
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.CALM_TALK, "See what?", () -> {});
								addNext(()->{
									tick++;
								});
								create();
							}
						});

					if(tick == PAUSE_FOR_PLAYER3)
						player.startConversation(new Conversation(player) {
							{
								addPlayer(HeadE.SCARED, "I thought I saw something above us.");
								addNext(()->{
									tick++;
								});
								create();
							}
						});

					if(tick == 28) {
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(24), instance.getLocalY(13), 0), 1150);
						player.getPackets().sendCameraLook(player, new WorldTile(instance.getLocalX(18), instance.getLocalY(13), 0), 0);
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(24), instance.getLocalY(19), 0), 1150, 0, 3);
					}
					if(tick == 29) {
						WorldTile tile = new WorldTile(instance.getLocalX(17), instance.getLocalY(12), 1);
						captainNed.faceTile(tile);
						player.faceTile(tile);
						cabinboyJenkins.faceTile(tile);
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()-3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick==30) {
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.CALM_TALK, "It's the dragon!", () -> {});
								create();
							}
						});
						WorldTile tile = new WorldTile(instance.getLocalX(16), instance.getLocalY(12), 1);
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()-3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick == 31) {
						WorldTile tile = new WorldTile(instance.getLocalX(15), instance.getLocalY(12), 1);
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()-3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick == 32) {
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(546);
						WorldTile tile = new WorldTile(instance.getLocalX(13), instance.getLocalY(12), 1);
						int fireHeight = 500;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()-3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}

					if(tick == 34) {
						player.getPackets().sendCameraShake(1, 0, 8, 5, 8);
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(19), instance.getLocalY(14), 0), 1200);
						player.getPackets().sendCameraLook(player, new WorldTile(instance.getLocalX(17), instance.getLocalY(14), 0), 700);
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(26), instance.getLocalY(14), 0), 1200, 0, 3);
						player.getInterfaceManager().closeInterfacesOverGameWindow();
						WorldTile tile = new WorldTile(instance.getLocalX(13), instance.getLocalY(14), 1);
						captainNed.faceTile(tile);
						cabinboyJenkins.faceTile(tile);
						int fireHeight = 500;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()+3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick == 35) {
						WorldTile tile = new WorldTile(instance.getLocalX(15), instance.getLocalY(14), 1);
						int fireHeight = 0;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()+3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										cabinboyJenkins.setNextAnimation(new Animation(ANIM_JENKINS_SHAKE));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick == 36) {
						WorldTile tile = new WorldTile(instance.getLocalX(16), instance.getLocalY(14), 1);
						int fireHeight = 0;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()+3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										captainNed.setNextAnimation(new Animation(ANIM_NED_FEAR));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick == 37) {
						WorldTile tile = new WorldTile(instance.getLocalX(17), instance.getLocalY(14), 1);
						int fireHeight = 0;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()+3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										cabinboyJenkins.setNextAnimation(new Animation(ANIM_JENKINS_FEAR));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick == 38) {
						WorldTile tile = new WorldTile(instance.getLocalX(18), instance.getLocalY(14), 1);
						int fireHeight = 0;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()+3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										cabinboyJenkins.setNextAnimation(new Animation(ANIM_JENKINS_DIE));
										World.sendSpotAnim(player, new SpotAnim(1154, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}

					if(tick == 39) {
						WorldTile tile = new WorldTile(instance.getLocalX(19), instance.getLocalY(14), 1);
						int fireHeight = 0;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()+3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}

					if(tick == 40) {
						WorldTile tile = new WorldTile(instance.getLocalX(41), instance.getLocalY(14), 1);
						int fireHeight = 0;
						World.sendProjectile(new WorldTile(tile.getX(), tile.getY()+3, tile.getPlane()), tile, 1155, 99, 0, 0, 0.5, 0, 0, () -> {
							WorldTasks.schedule(new WorldTask() {
								int tick;

								@Override
								public void run() {
									if (tick == 0) {
										World.sendSpotAnim(player, new SpotAnim(1154, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
										World.sendSpotAnim(player, new SpotAnim(2588, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									}
									if (tick > 1)
										World.sendSpotAnim(player, new SpotAnim(453, 0, fireHeight), new WorldTile(tile.getX(), tile.getY(), tile.getPlane()));
									if(tick == 80)
										stop();
									tick++;
								}
							}, 0, 1);
						});
					}
					if(tick == 41) {
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(34), instance.getLocalY(14), 0), 1900);
						player.getPackets().sendCameraLook(player, new WorldTile(instance.getLocalX(17), instance.getLocalY(14), 0), 700);
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(28), instance.getLocalY(14), 0), 1200, 0, 3);
					}
					if(tick == PAUSE_FOR_NED5)
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.CALM_TALK, "We're going to sink!", () -> {});
								addNext(()->{
									captainNed.faceEntity(player);
									player.setNextFaceWorldTile(new WorldTile(player.getX()+1, player.getY(), player.getPlane()));
									tick++;
								});
								create();
							}
						});

					if(tick == PAUSE_FOR_PLAYER4)
						player.startConversation(new Conversation(player) {
							{
								addPlayer(HeadE.SCARED, "Look! Land ahead!");
								addNext(()->{
									tick++;
								});
								create();
							}
						});
					if(tick == 44) {
						player.getPackets().sendCameraPos(player, new WorldTile(instance.getLocalX(20), instance.getLocalY(14), 0), 1200, 0, 3);
						player.faceEntity(captainNed);
						captainNed.setNextFaceWorldTile(new WorldTile(captainNed.getX()+1, captainNed.getY(), captainNed.getPlane()));
					}
					if(tick == PAUSE_FOR_NED6)
						player.startConversation(new Conversation(player) {
							{
								addNPC(CAPTAIN_NED, HeadE.CALM_TALK, "We're going to crash!", () -> {});
								addNext(()->{
									tick++;
								});
								create();
							}
						});
					if(tick == 46) {
						player.getPackets().sendCameraShake(1, 5, 8, 25, 8);
						player.startConversation(new Conversation(player) {
							{
								addSimple("CRASH!");
								create();
							}
						});
					}
					if(tick == 48)
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(115);
					if(tick == 51) {
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(516);
						player.setNextWorldTile(crandor);//crandor
					}

					if(tick == PAUSE_FOR_PLAYER5) {
						player.setNextFaceWorldTile(new WorldTile(player.getX(), player.getY()+1, player.getPlane()));//face north
						player.getPackets().sendStopCameraShake();
						player.getPackets().sendResetCamera();
						player.startConversation(new Conversation(player) {
							{
								addSimple("You are knocked unconscious and later awake on an ash-shrewn beach.");
								addNext(()-> {
									tick++;
								});
								create();
							}
						});
					}
					if(tick == 53) {
						player.setNextAnimation(new Animation(ANIM_PLAYER_GET_UP));
						player.getInterfaceManager().sendBackgroundInterfaceOverGameWindow(170);
					}

					if(tick== 54) {

					}

					if (tick == 60) {
						player.getInterfaceManager().closeInterfacesOverGameWindow();
						player.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).setB(DragonSlayer.FINISHED_BOAT_SCENE_ATTR, true);
						player.getControllerManager().forceStop();
						stop();
					}
					if(tick == PAUSE_FOR_NED1 || tick == PAUSE_FOR_NED2 || tick == PAUSE_FOR_NED3 || tick == PAUSE_FOR_NED4 || tick == PAUSE_FOR_NED5
							|| tick == PAUSE_FOR_NED6 || tick == PAUSE_FOR_PLAYER1 || tick == PAUSE_FOR_PLAYER2 || tick == PAUSE_FOR_PLAYER3
							|| tick == PAUSE_FOR_PLAYER4 || tick == PAUSE_FOR_PLAYER5)
						;
					else
						tick++;
				}
			}, 0, 1);
		});
	}

	@Override
	public boolean sendDeath() {
		forceClose();
		return false;
	}

	@Override
	public boolean login() {
		player.getTempAttribs().setB("CUTSCENE_INTERFACE_CLOSE_DISABLED", false);
		player.setNextWorldTile(startingTile);
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
		player.getTempAttribs().setB("CUTSCENE_INTERFACE_CLOSE_DISABLED", false);
		player.getPackets().setBlockMinimapState(0);
		removeInstance();
		player.unlock();
		removeController();
	}

	private void removeInstance() {
		captainNed.finish();
		cabinboyJenkins.finish();
		instance.destroy();
	}
}
