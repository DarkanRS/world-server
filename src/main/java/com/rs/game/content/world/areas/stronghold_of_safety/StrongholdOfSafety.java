package com.rs.game.content.world.areas.stronghold_of_safety;

import com.rs.game.World;
import com.rs.game.model.entity.player.managers.EmotesManager;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class StrongholdOfSafety {
    public static ObjectClickHandler handleTrainingCentreStairsDown = new ObjectClickHandler(new Object[] { 29592 }, Tile.of(3084, 3452, 0), e -> {
        e.getPlayer().useStairs(827, Tile.of(3086, 4247, 0));
    });

    public static ObjectClickHandler handleTrainingCentreStairsUp = new ObjectClickHandler(new Object[] { 29589 }, Tile.of(3086, 4244, 0), e -> {
        e.getPlayer().useStairs(827, Tile.of(3084, 3452, 0), 1, 2);
    });

    public static ObjectClickHandler handleSecureZoneLever = new ObjectClickHandler(new Object[]{ 29736 }, Tile.of(3146, 4278, 3), e -> {
        e.getObject().setIdTemporary(29731, Ticks.fromSeconds(20));
        e.getPlayer().getTempAttribs().setB("TrainingCentreSecureZoneDoor", true);
        e.getPlayer().sendMessage("You hear a door unlock.");
    });

    public static ObjectClickHandler handleSecureZoneDoorEast = new ObjectClickHandler(new Object[] { 29624 }, Tile.of(3178, 4269, 2), e -> {
        if(!e.getPlayer().getTempAttribs().getB("TrainingCentreSecureZoneDoor"))
            e.getPlayer().sendMessage("This door is locked, maybe there is a way to unlock it?");
        else
        e.getPlayer().useStairs(Tile.of(3177, 4266, 0));
        e.getPlayer().faceEast();
    });

    public static ObjectClickHandler handleSecureZoneDoorWest = new ObjectClickHandler(new Object[] { 29624 }, Tile.of(3141, 4272, 1), e -> {
        if(!e.getPlayer().getTempAttribs().getB("TrainingCentreSecureZoneDoor"))
            e.getPlayer().sendMessage("This door is locked, maybe there is a way to unlock it?");
        else{
        e.getPlayer().useStairs(Tile.of(3143, 4270, 0));
        e.getPlayer().faceWest();
        }
    });

    public static ObjectClickHandler handleSecureZoneChest = new ObjectClickHandler(new Object[] { 29734 }, Tile.of(3166, 4259, 0), e -> {
        if(e.getPlayer().getEmotesManager().unlockedEmote(EmotesManager.Emote.SAFETY_FIRST)) {
            e.getPlayer().sendMessage("You have already looted this chest.");
        }
        else
        {
            e.getPlayer().save("sopsRew", true);
            e.getPlayer().simpleDialogue("You open the chest to find a large pile of gold, along with a pair of safety gloves, and two antique lamps. Also in the chest is the secret to the 'Safety First' emote.");
            e.getPlayer().getEmotesManager().unlockEmote(EmotesManager.Emote.SAFETY_FIRST);
            e.getPlayer().getInventory().addCoins(10000);
            if(e.getPlayer().getInventory().hasFreeSlots())
                e.getPlayer().getInventory().addItem(12629);
            else
                World.addGroundItem(new Item(12629),e.getPlayer().getTile(),e.getPlayer());
            if(e.getPlayer().getInventory().hasFreeSlots())
                e.getPlayer().getInventory().addItem(12628);
            else
                World.addGroundItem(new Item(12628),e.getPlayer().getTile(),e.getPlayer());
            if(e.getPlayer().getInventory().hasFreeSlots())
                e.getPlayer().getInventory().addItem(12628);
            else
                World.addGroundItem(new Item(12628),e.getPlayer().getTile(),e.getPlayer());
        }
    });
}
