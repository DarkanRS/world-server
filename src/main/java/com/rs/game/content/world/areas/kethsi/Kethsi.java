package com.rs.game.content.world.areas.kethsi;

import com.rs.game.content.skills.agility.Agility;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Kethsi {
    //sync 11705 1340 (get stone of jas buff)

    public static ObjectClickHandler handleGlacorCavePassBarrier = new ObjectClickHandler(new Object[] { 61584 }, e ->
            e.getPlayer().setNextTile(e.getPlayer().transform(e.getPlayer().getX() == 4206 ? -1 : 1, 0, 0)));

    public static ObjectClickHandler statueArmRubble = new ObjectClickHandler(new Object[] { 6655 }, e -> {
       if (e.getPlayer().getVars().getVarBit(9833) == 1 || e.getPlayer().getInventory().containsItem(21797)) {
           e.getPlayer().simpleDialogue("You search the rubble, but find nothing of interest.");
           return;
       }
       e.getPlayer().itemDialogue(21797, "You find a statue arm.");
       e.getPlayer().getInventory().addItem(21797, 1);
    });

    public static ItemOnObjectHandler restoreArm = new ItemOnObjectHandler(new Object[] { 10466 }, new Object[] { 21797 }, e -> {
        if (e.getPlayer().getVars().getVarBit(9833) == 1) {
            e.getPlayer().simpleDialogue("It doesn't look like the arm will fit.");
            return;
        }
        e.getPlayer().simpleDialogue("You attach the missing arm to the statue and notice it doing a very vertraut pose. A ramp falls down nearby.");
        e.getPlayer().getInventory().deleteItem(21797, 1);
        e.getPlayer().getVars().saveVarBit(9833, 1);
    });

    public static ObjectClickHandler ladders = new ObjectClickHandler(new Object[] { 6754, 6755, 6753 }, e -> {
        boolean up = e.getObjectId() == 6753;
       e.getPlayer().useLadder(e.getPlayer().transform(e.getObject().getRotation() == 1 ? (up ? 2 : -2) : e.getObject().getRotation() == 3 ? (up ? -2 : 2) : 0, e.getObject().getRotation() == 2 ? (up ? -2 : 2) : e.getObject().getRotation() == 0 ? (up ? 2 : -2) : 0, up ? 1 : -1));
    });

    public static ObjectClickHandler jumpGap = new ObjectClickHandler(new Object[] { 10372, 10390 }, e -> {
        e.getPlayer().walkToAndExecute(e.getObject().getTile(), () -> e.getPlayer().forceMove(e.getPlayer().transform(e.getObjectId() == 10372 ? -3 : 3, 0, 0), 11729, 20, 60));
    });

    public static ObjectClickHandler crossSpire = new ObjectClickHandler(new Object[] { 10456 }, e -> {
        Agility.walkToAgility(e.getPlayer(), 155, e.getPlayer().getY() < 5709 ? Direction.NORTH : Direction.SOUTH, 6, 6);
    });

    public static ObjectClickHandler ramps = new ObjectClickHandler(new Object[] { 6751, 6752 }, e -> {
        e.getPlayer().useStairs(e.getPlayer().transform(0, e.getObjectId() == 6751 ? -5 : 5, e.getObjectId() == 6751 ? 1 : -1));
    });

}
