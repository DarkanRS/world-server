package com.rs.game.content.minigames.crucible;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

import java.util.Set;

@PluginEventHandler
public class Crucible {
    //1291 = teleport interface
    //1292 = warning interface
    //1294-1295 = crucible help interface
    //1296 = overlay
    //1297 = rewards
    //1298 = bounty fee payment

    private static Set<Player> SAFE_PLAYERS = ObjectSets.synchronize(new ObjectOpenHashSet<>());
    private static Set<Player> DANGEROUS_PLAYERS = ObjectSets.synchronize(new ObjectOpenHashSet<>());

    public static ObjectClickHandler entrance = new ObjectClickHandler(new Object[] { 67052 }, e -> {
        e.getPlayer().sendOptionDialogue("Which Bounty Hunter mode would you like to enter as?", ops -> {
            ops.add("Safe (No items lost on death)", () -> {
                e.getPlayer().setNextTile(CrucibleController.getRespawnTile());
                e.getPlayer().getControllerManager().startController(new CrucibleController(false));
            });
            ops.add("<col=FF0000><shad=000000>Dangerous (All but one item lost on death)", () -> {
                e.getPlayer().setNextTile(CrucibleController.getRespawnTile());
                e.getPlayer().getControllerManager().startController(new CrucibleController(true));
            });
            ops.add("Nevermind");
        });
    });

    public static ObjectClickHandler rewardHatch = new ObjectClickHandler(new Object[] { 67051 }, e -> {
       switch(e.getOption()) {
           case "Knock" -> e.getPlayer().sendOptionDialogue(ops -> {
               ops.add("Help", () -> e.getPlayer().getInterfaceManager().sendInterface(1295));
               ops.add("Rewards", () -> e.getPlayer().getInterfaceManager().sendInterface(1297));
           });
           case "Knock for rewards" -> e.getPlayer().getInterfaceManager().sendInterface(1297);
       }
    });

    public static ObjectClickHandler stairs = new ObjectClickHandler(new Object[] { 67050, 67053 }, e -> {
        switch(e.getObjectId()) {
            case 67050 -> e.getPlayer().useStairs(-1, Tile.of(3359, 6110, 0), 1, 2);
            case 67053 -> e.getPlayer().useStairs(-1, Tile.of(3120, 3519, 0), 1, 2);
        }
    });

    public static void add(Player player, boolean dangerous) {
        if (dangerous)
            DANGEROUS_PLAYERS.add(player);
        else
            SAFE_PLAYERS.add(player);
    }

    public static void remove(Player player, boolean dangerous) {
        if (dangerous)
            DANGEROUS_PLAYERS.remove(player);
        else
            SAFE_PLAYERS.remove(player);
    }
}
