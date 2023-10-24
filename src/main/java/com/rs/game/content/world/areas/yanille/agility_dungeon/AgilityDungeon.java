package com.rs.game.content.world.areas.yanille.agility_dungeon;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class AgilityDungeon {

    public static ObjectClickHandler handleSinisterChest = new ObjectClickHandler(new Object[] { 377 }, e -> {
        if (!e.getPlayer().getInventory().containsItem(993, 1)) {
            e.getPlayer().sendMessage("The chest is securely locked.");
            return;
        }
        e.getPlayer().anim(536);
        e.getPlayer().lock(2);
        e.getPlayer().sendMessage("You unlock the chest with your key.");
        e.getPlayer().getInventory().deleteItem(993, 1);
        e.getPlayer().getInventory().addItemDrop(206, 2);
        e.getPlayer().getInventory().addItemDrop(208, 3);
        e.getPlayer().getInventory().addItemDrop(210, 1);
        e.getPlayer().getInventory().addItemDrop(212, 1);
        e.getPlayer().getInventory().addItemDrop(214, 1);
        e.getPlayer().getInventory().addItemDrop(220, 1);
    });

    public static ObjectClickHandler handleThievingDoor = new ObjectClickHandler(new Object[] { 2559 }, e -> {
        switch(e.getOption()) {
            case "Open" -> e.getPlayer().sendMessage("The door is securely locked.");
            case "Pick-lock" -> {
                if (!e.getPlayer().getInventory().containsOneItem(1523, 11682)) {
                    e.getPlayer().sendMessage("You need a lockpick to open this door.");
                    return;
                }
                if (e.getPlayer().getSkills().getLevel(Skills.THIEVING) < 82) {
                    e.getPlayer().sendMessage("You need a Thieving level of 82 to pick this lock.");
                    return;
                }
                Doors.handleLeftHandedDoor(e.getPlayer(), e.getObject());
            }
        }
    });

    public static ObjectClickHandler handleWebStairs = new ObjectClickHandler(new Object[] { 32270, 32271, 37023 }, e -> {
        if (e.getObject().getTile().isAt(2603, 9478) || e.getObjectId() == 32271)
            e.getPlayer().useStairs(e.getPlayer().getTile().transform(e.getObjectId() == 32271 ? -4 : 4, e.getObjectId() == 32271 ? 6400 : -6400));
        else
            e.getPlayer().useStairs(e.getPlayer().getTile().transform(0, e.getObjectId() == 37023 ? 6404 : -6404));
    });


    public static ObjectClickHandler handleLockpickRoomStairs = new ObjectClickHandler(new Object[] { 1728, 1729 }, e ->
            e.getPlayer().useStairs(e.getPlayer().getTile().transform(0, e.getObjectId() == 1728 ? 68 : -68)));

    public static ObjectClickHandler poisonSpiderStairs = new ObjectClickHandler(new Object[] { 2316 }, e ->
            e.getPlayer().useStairs(Tile.of(2572, 9533, 0)));

    public static NPCClickHandler handleSigbert = new NPCClickHandler(new Object[] { 37 }, e -> e.getPlayer().startConversation(new Dialogue()
            .addNPC(37, HeadE.CALM_TALK, "I'd be very careful going down there, friend.")
            .addOptions(ops -> {
                ops.add("Why? What's down there?")
                        .addPlayer(HeadE.CONFUSED, "Why? What's down there?")
                        .addNPC(37, HeadE.CALM_TALK, "Salarin the Twisted, one of Kandarin's most dangerous chaos druids. I tried to take him on , then suddenly felt immensely weak.")
                        .addNPC(37, HeadE.CALM_TALK, "I hear he's susceptible to attacks from the mind. However, I have no idea what that means, so it's not much help to me.");

                ops.add("Fear not! I am very strong!")
                        .addPlayer(HeadE.CHEERFUL, "Fear not! I am very strong!")
                        .addNPC(37, HeadE.CALM_TALK, "You might find you are not so strong shortly...");
            })));

    public static ObjectClickHandler handleLedge = new ObjectClickHandler(new Object[] { 2303, 35969 }, e -> {
        if (!Agility.hasLevel(e.getPlayer(), 40))
            return;
        final Tile north = Tile.of(2580, 9520, 0);
        final Tile south = Tile.of(2580, 9512, 0);
        final Tile mid = Tile.of(2580, 9516, 0);
        final boolean isNorth = e.getPlayer().getY() > mid.getY();
        if (!Utils.skillSuccess(e.getPlayer().getSkills().getLevel(Skills.AGILITY), -67, 416)) {
            Agility.crossLedge(e.getPlayer(), isNorth ? north : south, e.getPlayer().transform(0, isNorth ? -2 : 2), 20, isNorth);
            WorldTasks.schedule(6, () -> {
                e.getPlayer().setNextTile(Tile.of(2572, 9568, 0));
                e.getPlayer().applyHit(Hit.flat(e.getPlayer(), (int) (e.getPlayer().getMaxHitpoints() * 0.2)));
            });
            return;
        }
        Agility.crossLedge(e.getPlayer(), isNorth ? north : south, isNorth ? south : north, 20, isNorth);
    });

    public static ObjectClickHandler handlePipe = new ObjectClickHandler(new Object[] { 2290 }, e -> {
        if (!Agility.hasLevel(e.getPlayer(), 49))
            return;
        e.getPlayer().forceMove(e.getObject().getTile().transform(e.getPlayer().getX() < 2575 ? 5 : -5, 0, 0), 10580, 35, 120, () -> e.getPlayer().getSkills().addXp(Constants.AGILITY, 5.0));
        if (!Utils.skillSuccess(e.getPlayer().getSkills().getLevel(Skills.AGILITY), -35, 350))
            e.getPlayer().applyHit(Hit.flat(e.getPlayer(), (int) (e.getPlayer().getMaxHitpoints() * 0.1)));
    });

    public static ObjectClickHandler handleMonkeybars = new ObjectClickHandler(new Object[] { 2321 }, e -> {
        if (!Agility.hasLevel(e.getPlayer(), 57))
            return;
        if (!Utils.skillSuccess(e.getPlayer().getSkills().getLevel(Skills.AGILITY), -15, 323)) {
            Agility.crossMonkeybars(e.getPlayer(), e.getObject().getTile(), e.getObject().getTile().transform(0, e.getPlayer().getY() > 9491 ? -2 : 2), 0);
            WorldTasks.schedule(6, () -> {
                e.getPlayer().setNextTile(Tile.of(2572, 9568, 0));
                e.getPlayer().applyHit(Hit.flat(e.getPlayer(), (int) (e.getPlayer().getMaxHitpoints() * 0.2)));
            });
            return;
        }
        Agility.crossMonkeybars(e.getPlayer(), e.getObject().getTile(), e.getObject().getTile().transform(0, e.getPlayer().getY() > 9491 ? -5 : 5), 20);
    });

    public static ObjectClickHandler handleRubble = new ObjectClickHandler(new Object[] { 2317, 2318 }, e -> {
        if (!Agility.hasLevel(e.getPlayer(), 67))
            return;
        e.getPlayer().useStairs(e.getPlayer().getTile().transform(e.getObjectId() == 2318 ? 1 : -1, e.getObjectId() == 2318 ? 65 : -65));
        if (e.getObjectId() == 2317)
            e.getPlayer().getSkills().addXp(Skills.AGILITY, 5.5);
    });
}
