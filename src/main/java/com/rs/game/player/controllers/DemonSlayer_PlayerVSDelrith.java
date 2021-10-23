package com.rs.game.player.controllers;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.demonslayer.DelrithBoss;
import com.rs.game.region.Instance;
import com.rs.game.region.RegionBuilder.DynamicRegionReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

public class DemonSlayer_PlayerVSDelrith extends Controller {
    static final int DELRITH = 879;
    static final int DARK_WIZARD7 = 8872;
    static final int DARK_WIZARD20 = 8873;
    static final int DENATH = 4663;

    //Wizard spell animations
    static final int SPELL1 = 707;
    static final int SPELL2 = 718;
    static final int SPELL3 = 717;
    static final int SPELL4 = 711;

    //Delrith animation
    static final int RESURRECT = 4623;

    DynamicRegionReference instance;
    WorldTile locationOnDeath = new WorldTile(3211, 3382, 0);
    WorldTile locationOnVictory = new WorldTile(3228, 3368, 0);
    WorldTile spawn;
    WorldTile combatStartTile;

	@Override
	public void start() {
        playCutscene();
	}

	private void playCutscene() {
        player.lock();
        this.instance = new DynamicRegionReference(64, 64);

        instance.copyMap(0, 0, 401, 419, 6, ()-> {
            this.spawn = this.instance.getLocalTile(19, 17);
            this.combatStartTile = this.instance.getLocalTile(15, 20);
            System.out.println(spawn);

            WorldTasksManager.schedule(new WorldTask() {
                int tick;
                NPC delrith;
                List<NPC> wizards = new ArrayList<>();

                @Override
                public void run() {
                    if (tick == 0) {
                        player.getPackets().sendMusic(-1, 100, 255);
                        player.getInterfaceManager().setFadingInterface(115);
                    } else if (tick == 2) {//setup player
                        player.getPackets().sendMusic(195, 100, 255);
                        player.getPackets().setBlockMinimapState(2);
                        player.getAppearance().transformIntoNPC(266);
                        player.setNextWorldTile(spawn);
                    } else if (tick == 3) {//Setup camera
                        player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()) - 4, player.getYInScene(player.getSceneBaseChunkId()) + 6, 2000);
                        player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()), 50);
                    } else if (tick == 4) {//Camera movement
                        player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()) + 0, player.getYInScene(player.getSceneBaseChunkId()) + 6, 2000, 0, 5);
                        player.startConversation(new Conversation(player) {
                            {
                                addNPC(DENATH, HeadE.EVIL_LAUGH, "Arise, O mighty Delrith! Bring destruction to this soft weak city!");
                            }
                        });

                    } else if (tick == 5) {
                        player.getInterfaceManager().setFadingInterface(170);
                        wizards.add((NPC) World.spawnNPC(DARK_WIZARD7, new WorldTile(spawn.getX() - 1, spawn.getY() + 2, spawn.getPlane()), -1, false, true));
                        wizards.add((NPC) World.spawnNPC(DARK_WIZARD20, new WorldTile(spawn.getX() + 2, spawn.getY() + 2, spawn.getPlane()), -1, false, true));
                        wizards.add((NPC) World.spawnNPC(DARK_WIZARD20, new WorldTile(spawn.getX() - 1, spawn.getY() - 1, spawn.getPlane()), -1, false, true));
                        wizards.add((NPC) World.spawnNPC(DENATH, new WorldTile(spawn.getX() + 2, spawn.getY() - 1, spawn.getPlane()), -1, false, true));
                        for (NPC wizard : wizards) {
                            wizard.setRandomWalk(false);
                            wizard.faceTile(spawn);
                        }
                    } else if (tick == 6) {//start scene
                        for (NPC wizard : wizards)
                            wizard.setNextAnimation(new Animation(SPELL1));
                    } else if (tick == 7) {
                        for (NPC wizard : wizards)
                            wizard.setNextAnimation(new Animation(SPELL2));
                    } else if (tick == 8) {
                        player.startConversation(new Conversation(player) {
                            {
                                addNPC(DARK_WIZARD7, HeadE.EVIL_LAUGH, "Arise Delrith!");
                            }
                        });
                        for (NPC wizard : wizards) {
                            wizard.forceTalk("Arise Delrith!");
                            wizard.setNextAnimation(new Animation(SPELL1));
                        }
                    } else if (tick == 9) {
                        for (NPC wizard : wizards)
                            wizard.setNextAnimation(new Animation(SPELL3));
                    } else if (tick == 10) {
                        player.startConversation(new Conversation(player) {
                            {
                                addSimple("The wizards cast an evil spell...");
                            }
                        });
                        player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()) - 4, 1500);
                        player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()) + 1, 50);
                        player.getPackets().sendCameraShake(3, 100, 1, 30, 1);
                        delrith = (NPC) World.spawnNPC(DELRITH, new WorldTile(spawn.getX(), spawn.getY(), spawn.getPlane()), -1, false, true);
                        delrith.faceTile(new WorldTile(spawn.getX() + 1, spawn.getY() - 1, spawn.getPlane()));
                        delrith.setRandomWalk(false);
                        delrith.setNextAnimation(new Animation(RESURRECT));
                    } else if (tick == 11) {
                        delrith.forceTalk("RaaawRRgh!");
                        for (NPC wizard : wizards) {
                            wizard.setNextSpotAnim(new SpotAnim(108));
                            wizard.setNextAnimation(new Animation(SPELL4));
                        }
                    } else if (tick == 12) {
                        player.getVars().setVarBit(2569, 1);
                        player.getPackets().sendCameraShake(3, 0, 0, 0, 0);
                        player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()) + 10, 250, 0, 1);
                    } else if (tick == 13) {
                        delrith.setForceWalk(new WorldTile(spawn.getX(), spawn.getY() - 2, 0));
                    } else if (tick == 14) {
                        player.getPackets().sendCameraPos(player.getXInScene(player.getSceneBaseChunkId()) - 4, player.getYInScene(player.getSceneBaseChunkId()) + 6, 2000);
                        player.getPackets().sendCameraLook(player.getXInScene(player.getSceneBaseChunkId()), player.getYInScene(player.getSceneBaseChunkId()) - 1, 50);
                    } else if (tick == 15) {
                        for (NPC wizard : wizards) {
                            if (wizard.getId() == DENATH)
                                delrith.faceEntity(wizard);
                            wizard.faceEntity(delrith);
                        }
                        player.startConversation(new Conversation(player) {
                            {
                                addNPC(DENATH, HeadE.EVIL_LAUGH, "Ha ha ha! At last you are free, my demonic brother! Rest now and then have your revenge on this " +
                                        "pitiful city!");
                                addNPC(DENATH, HeadE.EVIL_LAUGH, "We will destroy-");
                                addNPC(DARK_WIZARD7, HeadE.SCARED, "Who's that?", () -> {
                                    for (NPC wizard : wizards)
                                        wizard.faceTile(new WorldTile(spawn.getX() - 4, spawn.getY() + 4, 0));
                                    delrith.faceTile(new WorldTile(spawn.getX() - 4, spawn.getY() + 4, 0));
                                });
                                addNPC(DENATH, HeadE.SCARED, "Noo! Not Silverlight! Delrith is not ready yet!");
                                addNPC(DENATH, HeadE.SCARED, "I've got to get out of here.", () -> {
                                    for (NPC wizard : wizards) {
                                        if (wizard.getId() == DENATH) {
                                            wizard.setCantInteract(true);
                                            WorldTasksManager.schedule(new WorldTask() {
                                                @Override
                                                public void run() {
                                                    wizard.finish();
                                                }
                                            }, Ticks.fromSeconds(13));
                                            wizard.setForceWalk(new WorldTile(spawn.getX() + 13, spawn.getY(), 0));
                                            continue;
                                        }
                                    }
                                });
                                addNext(() -> {
                                    tick++;
                                });
                                create();
                            }
                        });
                    } else if (tick == 16) {

                    } else if (tick == 18) {
                        player.setNextWorldTile(combatStartTile);
                        player.getPackets().sendResetCamera();
                        player.getAppearance().transformIntoNPC(-1);
                        player.unlock();
                    } else if (tick == 19) {
                        player.setForceMultiArea(true);
                        for (NPC wizard : wizards) {
                            wizard.setRandomWalk(true);
                            wizard.setForceMultiArea(true);
                            wizard.setForceAggroDistance(20);
                            wizard.setTarget(player);
                        }
                        delrith.setRandomWalk(true);
                        delrith.setForceMultiArea(true);
                        delrith.setForceAggroDistance(20);
                        delrith.setTarget(player);
                    } else if (tick == 21) {
                        if (((DelrithBoss) delrith).actuallyDead)
                            tick++;
                    } else if (tick == 24) {
                        player.getInterfaceManager().setFadingInterface(115);
                    } else if (tick == 26) {
                        player.getPackets().setBlockMinimapState(0);
                        player.setNextWorldTile(locationOnVictory);
                    } else if (tick == 28) {
                        player.getInterfaceManager().setFadingInterface(170);
                    } else if (tick == 31) {
                        player.getQuestManager().completeQuest(Quest.DEMON_SLAYER);
                        player.sendMessage("Congratulations! Quest complete!");
                        player.getControllerManager().forceStop();
                        player.unlock();
                        stop();
                    }
                    if (tick != 16 && tick != 21)
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
        player.setNextWorldTile(locationOnDeath);
        player.getVars().setVarBit(2569, 0);
        forceClose();
        return false;
    }

	@Override
	public boolean login() {
        player.setNextWorldTile(locationOnDeath);
        System.out.println("Login");
        forceClose();
		return false;
	}

	@Override
    public boolean logout() {
        System.out.println("logout");
        removeInstance();
        player.unlock();
	    return false;
    }

    @Override
    public void forceClose() {
        System.out.println("Force close");
        player.getPackets().setBlockMinimapState(0);
        player.setForceMultiArea(false);
        removeInstance();
        player.setTempB("FinalDemonSlayerCutscene", false);
        player.unlock();
        removeController();
    }

    private void removeInstance() {
        try { instance.destroy(); }
        catch(Exception e) { ; }
    }
}
