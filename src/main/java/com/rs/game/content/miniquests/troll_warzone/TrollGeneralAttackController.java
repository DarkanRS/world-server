package com.rs.game.content.miniquests.troll_warzone;

import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;

public class TrollGeneralAttackController extends InstancedController {

    private static final Tile OUTSIDE = Tile.of(2878, 3573, 0);

    public TrollGeneralAttackController() {
        super(Instance.of(OUTSIDE, 8, 8).persist().setEntranceOffset(new int[] { 32, 12, 0 }));
    }

    @Override
    public void onBuildInstance() {
        player.lock();
        getInstance().copyMapAllPlanes(272, 544).thenAccept(b -> {
            getInstance().teleportLocal(player, 32, 12, 0);
            player.unlock();
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
