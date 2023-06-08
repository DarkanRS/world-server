package com.rs.game.content.miniquests.troll_warzone;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.actions.EntityFollow;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.game.model.object.GameObject;
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
    }

    @Override
    public boolean sendDeath() {
        player.safeDeath(OUTSIDE);
        return false;
    }

    @Override
    public boolean processNPCClick1(NPC npc) {
        switch (npc.getId()) {
            //Ozan
            case 14983 -> {
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
                }
            }
            //Keymans
            case 14988 -> {
                switch (stage) {
                    case 0 -> player.startConversation(new Dialogue()
                            .addNPC(npc, HeadE.CONFUSED, "Ozan, what should we do next?<br><br><col=2A32C9>Click the green button below or press the space bar."));
                }
            }
        }
        return false;
    }

    @Override
    public boolean processObjectClick1(GameObject object) {
        if (object.getId() == 66534) {
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
}
