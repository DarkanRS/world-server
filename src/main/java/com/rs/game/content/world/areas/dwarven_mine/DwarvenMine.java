package com.rs.game.content.world.areas.dwarven_mine;

import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class DwarvenMine {

    public static ObjectClickHandler handleDwarvenMineLadders = new ObjectClickHandler(new Object[] { 2113, 30942, 6226, 30943, 30944 }, e -> {
        Player player = e.getPlayer();
        switch (e.getObjectId()) {
            case 30942 -> player.useStairs(828, e.getPlayer().transform(0, 6400, 0));
            case 6226 ->  player.useStairs(828, e.getPlayer().transform(0, -6400, 0));
            case 30943 -> player.useStairs(-1, Tile.of(3061, 3376, 0), 0, 1);
            case 30944 -> player.useStairs(-1, Tile.of(3058, 9776, 0), 0, 1);
            case 2113 -> {
                if (player.getSkills().getLevelForXp(Constants.MINING) < 60) {
                    player.npcDialogue(3294, HeadE.CHEERFUL, "Sorry, but you need level 60 Mining to go in there.");
                    return;
                }
                player.useStairs(-1, Tile.of(3021, 9739, 0), 0, 1);
            }
        }
    });

    public static ObjectClickHandler handleMiningGuildDoor = new ObjectClickHandler(new Object[] { 2112 }, new Tile[] { Tile.of(3046, 9756, 0) }, e -> {
        if (e.getPlayer().getSkills().getLevelForXp(Constants.MINING) < 60) {
            e.getPlayer().npcDialogue(3294, HeadE.CHEERFUL, "Sorry, but you need level 60 Mining to go in there.");
            return;
        }
        Doors.handleDoor(e.getPlayer(), e.getObject());
    });

    public static ObjectClickHandler handlePickaxeFactoryLadder = new ObjectClickHandler(new Object[]{31002, 31012}, e -> {
        switch (e.getObjectId()) {
            case 31002 -> e.getPlayer().useStairs(828, Tile.of(2998, 3452, 0), 1, 1);
            case 31012 -> e.getPlayer().useStairs(828, Tile.of(2996, 9845, 0), 1, 1);
        }
    });

    public static ObjectClickHandler handleCartSearch = new ObjectClickHandler(new Object[] { 6045 }, e -> e.getPlayer().sendMessage("You search the cart but find nothing."));

    public static ObjectClickHandler handleObstacle = new ObjectClickHandler(new Object[] { 5906 }, e -> {
        var player = e.getPlayer();
        var object = e.getObject();
        if (player.getSkills().getLevel(Constants.AGILITY) < 42) {
            player.sendMessage("You need an agility level of 42 to use this obstacle.");
            return;
        }
        player.lock();
        WorldTasks.schedule(0,
            () -> player.forceMove(Tile.of(object.getX() + (object.getRotation() == 2 ? -2 : +2), object.getY(), 0), 2594, 0, 120, false,
            () -> player.forceMove(Tile.of(object.getX() + (object.getRotation() == 2 ? -5 : +5), object.getY(), 0), 2590, 0, 120, false,
            () -> player.forceMove(Tile.of(object.getX() + (object.getRotation() == 2 ? -6 : +6), object.getY(), 0), 2595, 0, 75))));
    });

    public static ObjectClickHandler handleRopeClimbDown = new ObjectClickHandler(new Object[] { 45077 }, e -> {
        var player = e.getPlayer();
        var object = e.getObject();
        player.lock();

        if (player.getX() != object.getX() || player.getY() != object.getY()) {
            player.addWalkSteps(object.getX(), object.getY(), -1, false);
        }

        WorldTasks.scheduleTimer((ticks) -> {
            switch (ticks) {
                case 0 -> {
                    player.setNextFaceTile(Tile.of(object.getX() - 1, object.getY(), 0));
                    player.anim(12216);
                }
                case 2 -> {
                    player.tele(Tile.of(3651, 5122, 0));
                    player.setNextFaceTile(Tile.of(3651, 5121, 0));
                    player.anim(12217);
                }
                case 3 -> {
                    // TODO: Find emote
                    // player.getPackets().sendObjectAnimation(new WorldObject(45078, 0, 3, 3651, 5123, 0), new Animation(12220));
                }
                case 5 -> {
                    player.unlock();
                    return false;
                }
            }
            return true;
        });
    });

    public static ObjectClickHandler getHandleRopeClimbUp = new ObjectClickHandler(new Object[] { 45078 }, e -> e.getPlayer().useStairs(2413, Tile.of(3012, 9832, 0), 2, 2));
}
