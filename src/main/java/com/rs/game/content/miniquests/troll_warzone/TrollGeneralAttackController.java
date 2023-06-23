package com.rs.game.content.miniquests.troll_warzone;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.World;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.Entity.MoveType;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;

public class TrollGeneralAttackController extends InstancedController {

    private static final Tile OUTSIDE = Tile.of(2878, 3573, 0);

    private transient int stage = 0;
    private transient NPC ozan, keymans, trollGeneral;

    public TrollGeneralAttackController() {
        super(Instance.of(OUTSIDE, 8, 8).persist().setEntranceOffset(new int[]{32, 12, 0}));
    }

    @Override
    public void onBuildInstance() {
        player.lock();
        getInstance().copyMapAllPlanes(272, 544).thenAccept(b -> {
            player.playCutscene(cs -> {
                cs.fadeIn(5);
                cs.action(1, () -> getInstance().teleportLocal(player, 32, 12, 0));
                cs.action(() -> {
                    player.setForceNextMapLoadRefresh(true);
                    player.loadMapRegions();
                    cs.setEndTile(Tile.of(cs.getX(32), cs.getY(12), 0));
                });
                cs.npcCreate("ozan", 14983, 33, 28, 0, n -> {
                    n.persistBeyondCutscene();
                    n.setIgnoreNPCClipping(true);
                    n.setRun(true);
                    ozan = n;
                });
                cs.npcCreate("keymans", 14988, 34, 28, 0, n -> {
                    n.persistBeyondCutscene();
                    n.setIgnoreNPCClipping(true);
                    n.setRun(true);
                    keymans = n;
                });
                cs.npcCreate("brute", 14980, 27, 43, 0, n -> {
                    n.setRandomWalk(true);
                    n.persistBeyondCutscene();
                });
                cs.npcCreate("chucker", 14981, 29, 46, 0, n -> {
                    n.setRandomWalk(true);
                    n.persistBeyondCutscene();
                });
                cs.npcCreate("shaman", 14982, 31, 44, 0, n -> {
                    n.setRandomWalk(true);
                    n.persistBeyondCutscene();
                });
                cs.action(() -> player.getHintIconsManager().addHintIcon(cs.getNPC("ozan"), 0, -1, false));
                cs.fadeOut(5);
                cs.action(() -> player.unlock());
            });
        });
    }

    @Override
    public void onDestroyInstance() {
        player.setForceMultiArea(false);
    }

    @Override
    public boolean sendDeath() {
        player.safeDeath(OUTSIDE);
        return false;
    }

    @Override
    public boolean processNPCClick1(NPC npc) {
        npc.faceTile(player.getTile());
        player.faceTile(npc.getTile());
        switch (npc.getId()) {
            //Ozan
            case 14983, 14987 -> {
                switch (stage) {
                    case 0 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.CALM_TALK, "The trolls are bypassing Burthorpe's defenses through this tunnel!<br><br><col=2A32C9>Click the green button below or press the space bar.")
                            .addNPC(npc, HeadE.CALM_TALK, "You lead, we'll follow.", () -> {
                                stage = 1;
                                ozan.follow(player);
                                keymans.follow(ozan);
                                player.getHintIconsManager().removeAll();
                                trollGeneral = World.spawnNPC(14991, Tile.of(getInstance().getLocalX(36), getInstance().getLocalY(53), 0), -1, true, true, true);
                                trollGeneral.setRandomWalk(false);
                                trollGeneral.setCantInteract(true);
                                player.getHintIconsManager().addHintIcon(getInstance().getLocalX(37), getInstance().getLocalY(54), 0, 50, 0, 0, -1, false);
                            }));
                    case 1 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.CALM_TALK, "I can hear some loud footsteps up ahead. You lead, we'll follow."));
                    case 2 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.ANGRY, "There's no time to talk! Attack!"));
                    case 3 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.HAPPY_TALKING, "Congratulations all around, friends! Now all we need to do is-")
                            .addNPC(npc, HeadE.CALM, "...")
                            .addNPC(npc, HeadE.CONFUSED, "Hey, did you hear that?")
                            .addNPC(npc, HeadE.CALM_TALK, "It came from over there.")
                            .addNext(() -> player.playCutscene(cs -> {
                                cs.fadeIn(5);
                                cs.npcCreate("babyTroll", 14846, 28, 48, 0);
                                cs.action(() -> {
                                    player.getHintIconsManager().removeUnsavedHintIcon();
                                    ozan.setNextTile(Tile.of(getInstance().getLocalX(36), getInstance().getLocalY(51), 0));
                                    player.setNextTile(Tile.of(getInstance().getLocalX(36), getInstance().getLocalY(48), 0));
                                });
                                cs.camPos(36, 51, 1000);
                                cs.camLook(31, 51, 0);
                                cs.fadeOut(5);
                                cs.npcMove("babyTroll", 28, 51, MoveType.RUN, 2);
                                cs.camPos(32, 45, 2000, 0, 10, 1);
                                cs.npcMove("babyTroll", 30, 51, MoveType.RUN, 3);
                                cs.action(4, () -> {
                                    ozan.setRun(false);
                                    ozan.addWalkSteps(getInstance().getLocalX(33), getInstance().getLocalY(51));
                                });
                                cs.action(1, () -> ozan.faceTile(Tile.of(getInstance().getLocalX(20), getInstance().getLocalY(51), 0)));
                                cs.action(() -> ozan.setNextTile(Tile.of(getInstance().getLocalX(31), getInstance().getLocalY(51), 0)));
                                cs.npcDestroy("babyTroll");
                                cs.action(() -> {
                                    ozan.transformIntoNPC(14987);
                                    ozan.anim(15817);
                                    cs.npcDestroy("babyTroll");
                                });
                                cs.delay(23);
                                cs.action(() -> {
                                    stage = 4;
                                    player.getHintIconsManager().addHintIcon(getInstance().getLocalX(ozan.getXInRegion()), getInstance().getLocalY(ozan.getYInRegion()), 0, 50, 0, 0, -1, false);
                                });
                            })));
                    case 4 -> {
                        Dialogue endDialogue = new Dialogue() .addNPC(14846, HeadE.T_CONFUSED, "Food?")
                                .addNPC(npc, HeadE.CALM_TALK, "I'm going to take this little fellow to the training grounds.")
                                .addNPC(npc, HeadE.CALM_TALK, "Thanks again for your help. You should check in with Captain Jute outside the cave.")
                                .addNext(() -> player.playCutscene(cs -> {
                                    cs.action(() -> {
                                        ozan.addWalkSteps(ozan.transform(0, -10, 0).getX(), ozan.transform(0, -10, 0).getY());
                                        keymans.addWalkSteps(keymans.transform(0, -10, 0).getX(), keymans.transform(0, -10, 0).getY());
                                    });
                                    cs.fadeIn(5);
                                    cs.action(() -> {
                                        ozan.finish();
                                        keymans.finish();
                                    });
                                    cs.fadeOut(5);
                                    cs.action(() -> {
                                        stage = 5;
                                        player.getHintIconsManager().removeUnsavedHintIcon();
                                        player.getHintIconsManager().addHintIcon(getInstance().getLocalX(32), getInstance().getLocalY(12), 0, 50, 0, 0, -1, false);
                                    });
                                })).getHead();

                        player.startConversation(new Dialogue()
                                .addNPC(npc, HeadE.CONFUSED, "Where did this little guy come from? Do trolls always bring babies along on raids?")
                                .addOptions(ops -> {
                                    ops.add("Don't we have more important things to worry about?")
                                            .addNPC(npc, HeadE.SAD, "We can't just leave it here to die.")
                                            .addNext(endDialogue);
                                    ops.add("He's so cute!")
                                            .addNPC(npc, HeadE.SAD_MILD, "Isn't he? He's so wubbly!")
                                            .addNext(endDialogue);
                                    ops.add("We should kill it before it becomes a threat!")
                                            .addNPC(npc, HeadE.SAD, "I can't just execute a baby even if it is a troll!")
                                            .addNext(endDialogue);
                                }));
                    }
                }
            }
            //Keymans
            case 14988 -> {
                switch (stage) {
                    case 0 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.CONFUSED, "Ozan, what should we do next?<br><br><col=2A32C9>Click the green button below or press the space bar."));
                    case 1 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.AMAZED, "Whoah! There is something huge up ahead."));
                    case 2 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.ANGRY, "There's no time to talk! Attack!"));
                    case 3 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.CONFUSED, "What should we do next, Ozan?"));
                }
            }
        }
        return false;
    }

    @Override
    public boolean processObjectClick1(GameObject object) {
        if (object.getId() == 66534) {
            if (stage == 5) {
                player.getHintIconsManager().removeUnsavedHintIcon();
                player.getMiniquestManager().setStage(Miniquest.TROLL_WARZONE, 2);
                player.setNextTile(OUTSIDE);
                player.getControllerManager().forceStop();
                return false;
            }
            player.sendOptionDialogue("Would you like to leave the tutorial area?", ops -> {
                ops.add("Yes, please.", () -> {
                    player.setNextTile(OUTSIDE);
                    player.getControllerManager().forceStop();
                });
                ops.add("No, I'm not done here yet.");
            });
            return false;
        }
        return true;
    }

    @Override
    public boolean canMove(Direction dir) {
        if (stage == 0 && player.getTile().getXInRegion() >= 32 && player.getTile().getXInRegion() <= 35 && player.getTile().getYInRegion() >= 27 && dir.getDy() > 0)
            return false;
        if (stage == 1 && player.getTile().getYInRegion() >= 49) {
            player.stopAll();
            player.lock();
            player.playCutscene(cs -> {
                cs.action(() -> {
                    ozan.stopAll();
                    keymans.stopAll();
                    player.setNextTile(Tile.of(getInstance().getLocalX(34), getInstance().getLocalY(50), 0));
                    ozan.setNextTile(Tile.of(getInstance().getLocalX(33), getInstance().getLocalY(51), 0));
                    keymans.setNextTile(Tile.of(getInstance().getLocalX(35), getInstance().getLocalY(49), 0));
                });
                cs.camPos(33, 45, 2000, 0, 5);
                cs.camLook(trollGeneral.getXInRegion(), trollGeneral.getYInRegion(), 10, 0, 5);
                cs.delay(5);
                cs.action(() -> {
                    trollGeneral.faceTile(player.getTile());
                    player.faceTile(trollGeneral.getTile());
                    ozan.faceTile(trollGeneral.getTile());
                    keymans.faceTile(trollGeneral.getTile());
                });
                cs.dialogue(new Dialogue()
                        .addNPC(ozan, HeadE.ANGRY, "The troll general! Bring him down!")
                        .addNPC(trollGeneral, HeadE.T_ANGRY, "STUPID HUMANS! TROLLS SMASH YOUR STUPID FACE!"), true);
                cs.action(() -> {
                    trollGeneral.setForceMultiArea(true);
                    ozan.setForceMultiArea(true);
                    keymans.setForceMultiArea(true);
                    player.setForceMultiArea(true);
                    trollGeneral.setCantInteract(false);
                    trollGeneral.setTarget(player);
                    trollGeneral.addReceivedDamage(player, 5000);
                    ozan.setTarget(trollGeneral);
                    keymans.setTarget(trollGeneral);
                });
                cs.camPosResetSoft();
            });
            stage = 2;
            return false;
        }
        return true;
    }

    @Override
    public void processNPCDeath(NPC npc) {
        if (npc.getId() == 14991 && stage == 2) {
            for (NPC n : World.getNPCsInChunkRange(player.getChunkId(), 3)) {
                if (n.getId() >= 14980 && n.getId() <= 14982)
                    n.finish();
            }
            World.addGroundItem(new Item(23042), npc.getMiddleTile());
            World.addGroundItem(new Item(23031), npc.getMiddleTile());
            stage = 3;
            player.getHintIconsManager().removeAll();
            player.getHintIconsManager().addHintIcon(getInstance().getLocalX(ozan.getXInRegion()), getInstance().getLocalY(ozan.getYInRegion()), 0, 50, 0, 0, -1, false);
        }
    }
}
