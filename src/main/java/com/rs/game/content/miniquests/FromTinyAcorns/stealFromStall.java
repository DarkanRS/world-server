package com.rs.game.content.miniquests.FromTinyAcorns;

import com.rs.engine.miniquest.Miniquest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class stealFromStall extends PlayerAction {

    private GameObject object;

    private boolean success = false;

    public stealFromStall(GameObject object) {
        this.object = object;
    }
    @Override
    public boolean start(Player player) {
        if (checkAll(player)) {
            success = successful(player);
            player.faceObject(object);
            WorldTasks.delay(0, () -> {
                player.anim(881);
            });
            setActionDelay(player, 2);
            player.lock();
            return true;
        }
        return false;
    }

    @Override
    public boolean process(Player player) {
        return checkAll(player);
    }

    @Override
    public int processWithDelay(Player player) {
        if (!success) {
            player.sendMessage("You failed to steal the toy dragon.");
        }
        else {
            if(player.getInventory().hasFreeSlots()) {
                player.getInventory().addItem(18651, 1);
                player.sendMessage("You take the toy dragon from the stall.");
                player.getVars().setVarBit(7821, 0);
                player.getMiniquestManager().setStage(Miniquest.FROM_TINY_ACORNS, 2);
            }
            else {
                player.sendMessage("You do not have enough space to do that.");
            }
        }
        stop(player);
        return -1;
    }

    @Override
    public void stop(Player player) {
        player.unlock();
        player.setNextFaceEntity(null);
        setActionDelay(player, 1);
    }

    public boolean rollSuccess(Player player) {
        return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul() + (hasArdyCloak(player) ? 0.1 : 0.0), 185, 255);
    }

    private boolean successful(Player player) {
        if (!rollSuccess(player))
            return false;
        return true;
    }

    private boolean checkAll(Player player) {
        if (player.isDead() || player.hasFinished() || player.hasPendingHits())
            return false;
        if (player.getAttackedBy() != null && player.inCombat()) {
            player.sendMessage("You can't do this while you're under combat.");
            return false;
        }
        return true;
    }
    public static boolean hasArdyCloak(Player player) {
        switch(player.getEquipment().getCapeId()) {
            case 15349:
            case 19748:
            case 9777:
            case 9778:
                return true;
            default:
                return false;
        }
    }
}
