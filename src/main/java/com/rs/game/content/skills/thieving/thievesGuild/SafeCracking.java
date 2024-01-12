package com.rs.game.content.skills.thieving.thievesGuild;

import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class SafeCracking extends PlayerAction {
    public int animationIDCracking = 2246;
    public int animationIDSpikes = 615;

    public static GameObject object;

    private boolean success = false;

    public SafeCracking(GameObject object) {
        super();
        SafeCracking.object = object;
    }

    @Override
    public boolean start(Player player) {
        if (checkAll(player)) {
            success = successful(player);
            player.faceObject(object);
            player.sendMessage("You attempt to crack the safe.");
            WorldTasks.delay(0, () -> {
                player.setNextAnimation(new Animation(animationIDCracking));
            });
            setActionDelay(player, 4);
            player.lock();
            return true;
        }
        return false;
    }

    @Override
    public boolean process(Player player) {
        return checkAll(player);
    }

    public static double calculateExperience(Player player) {
        return 0.5 * player.getSkills().getLevel(Skills.THIEVING) + 26;
    }

    @Override
    public int processWithDelay(Player player) {
        if (!success) {
            player.sendMessage("You fail to crack the safe and have activated a trap.");
            player.spotAnim(animationIDSpikes);
            player.applyHit(new Hit(player, 1, Hit.HitLook.TRUE_DAMAGE));
            stop(player);
        }
        else {
            double totalXp = calculateExperience(player);
            player.getSkills().addXp(Constants.THIEVING, totalXp);
            if(object.getId() == 52306) {
                if (player.getWeeklyI("HankyPoints") < HankyPoints.maxPoints(player)) {
                    player.simpleDialogue("You gain 4 Hanky Points.");
                    player.incWeeklyI("HankyPoints", 4);
                } else
                    player.sendMessage("You have earned the maximum number of Hanky Points this week.");
            }
            stop(player);
        }
        return -1;
    }
    @Override
    public void stop(Player player) {
        player.unlock();
        setActionDelay(player, 1);
        if (!success)
            player.lock(4);
    }

    public boolean rollSuccess(Player player) {
        if(player.getInventory().containsItem(5560))
            return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul(), 8, 160);
        else
            return Utils.skillSuccess(player.getSkills().getLevel(Constants.THIEVING), player.getAuraManager().getThievingMul(), 16, 192);
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

    public static ObjectClickHandler handleGuildSafes = new ObjectClickHandler(new Object[] { 52306 }, e -> {
        if(e.getOption().equalsIgnoreCase("Crack"))
            e.getPlayer().getActionManager().setAction(new SafeCracking(e.getObject()));
    });
}
