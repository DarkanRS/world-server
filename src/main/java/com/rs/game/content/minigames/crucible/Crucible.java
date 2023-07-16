package com.rs.game.content.minigames.crucible;

import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;

import java.util.*;
import java.util.stream.Collectors;

@PluginEventHandler
public class Crucible {
    //1291 = teleport interface
    //1292 = warning interface
    //1294-1295 = crucible help interface
    //1296 = overlay
    //1297 = rewards
    //1298 = bounty fee payment

    //591 classic BH interface

    public static ObjectClickHandler entrance = new ObjectClickHandler(new Object[]{67052}, e -> {
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
    public static ObjectClickHandler rewardHatch = new ObjectClickHandler(new Object[]{67051}, e -> {
        switch (e.getOption()) {
            case "Knock" -> e.getPlayer().sendOptionDialogue(ops -> {
                ops.add("Help", () -> e.getPlayer().getInterfaceManager().sendInterface(1295));
                ops.add("Rewards", () -> e.getPlayer().getInterfaceManager().sendInterface(1297));
            });
            case "Knock for rewards" -> e.getPlayer().getInterfaceManager().sendInterface(1297);
        }
    });
    public static ObjectClickHandler stairs = new ObjectClickHandler(new Object[]{67050, 67053}, e -> {
        switch (e.getObjectId()) {
            case 67050 -> e.getPlayer().useStairs(-1, Tile.of(3359, 6110, 0), 1, 2);
            case 67053 -> e.getPlayer().useStairs(-1, Tile.of(3120, 3519, 0), 1, 2);
        }
    });
    public static ButtonClickHandler handleFissureTravel = new ButtonClickHandler(1291, e -> {
        int fissureIndex = e.getComponentId() - 4;
        if (fissureIndex < 0 || fissureIndex >= Fissure.values().length)
            return;
        useFissure(e.getPlayer(), Fissure.values()[fissureIndex]);
    });
    private static Set<Player> SAFE_PLAYERS = ObjectSets.synchronize(new ObjectOpenHashSet<>());
    private static Set<Player> DANGEROUS_PLAYERS = ObjectSets.synchronize(new ObjectOpenHashSet<>());

    private static Map<String, String> BH_TARGETS = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());

    public static void add(Player player, boolean dangerous) {
        if (dangerous)
            DANGEROUS_PLAYERS.add(player);
        else
            SAFE_PLAYERS.add(player);
    }

    public static void remove(Player player, boolean dangerous) {
        if (dangerous) {
            DANGEROUS_PLAYERS.remove(player);
            for (Player pt : DANGEROUS_PLAYERS)
                updateParticipants(pt, true);
        } else {
            SAFE_PLAYERS.remove(player);
            for (Player pt : SAFE_PLAYERS)
                updateParticipants(pt, false);
        }
    }

    public static void updateInterface(Player player, CrucibleController controller) {
        updateRank(player, controller.rank);
        updateRankPoints(player, controller.points, 0);
        updateParticipants(player, controller.dangerous);
    }

    private static void updateParticipants(Player player, boolean dangerous) {
        StringBuilder participants = new StringBuilder();
        participants.append((dangerous ? "<col=FF0000>Dangerous" : "<col=00FF00>Safe") + " Participants:</col><br>");
        for (Player participant : dangerous ? DANGEROUS_PLAYERS : SAFE_PLAYERS)
            participants.append(participant.getDisplayName()+"<br>");
        player.getPackets().setIFText(1296, 0, participants.toString());
    }

    /**
     * -1 = none
     * 0 = lowest rank (6)
     * 1 = rank 5
     * 2 = rank 4
     * 3 = rank 3
     * 4 = rank 2
     * 5 = rank 1
     * 6 = supreme champion
     * 7 = temporarily invulnerable
     */
    private static void updateRank(Player player, int tier) {
        player.getPackets().sendRunScript(6284, tier);
    }

    /**
     * Positive values add points
     * 0 or lower values remove all points
     */
    public static void updateRankPoints(Player player, int currPoints, int amountToAddOrRemove) {
        if (amountToAddOrRemove != 0)
            player.getPackets().sendRunScript(6288, amountToAddOrRemove);
        player.getPackets().setIFText(1296, 27, Utils.formatNumber(currPoints));
    }

    public static void useFissure(Player player, GameObject object, boolean fast, boolean fastBank) {
        Fissure fissure = Fissure.forLocation(object.getTile());
        if (fissure == null) {
            player.sendMessage("Invalid fissure location.");
            return;
        }
        if (!fast && !fastBank) {
            player.getInterfaceManager().sendInterface(1291);
            player.getPackets().sendRunScript(6271, fissure.ordinal() + 1);
            return;
        }
        List<Fissure> shuffled = Arrays.stream(Fissure.values()).filter(f -> f != fissure && ((fastBank && f.name().contains("BANK")) || (!fastBank && !f.name().contains("BANK")))).collect(Collectors.toList());
        Collections.shuffle(shuffled);
        Optional<Fissure> randomFissure = shuffled.stream().findFirst();
        if (shuffled.stream().findFirst().isPresent())
            useFissure(player, randomFissure.get());
    }

    public static void useFissure(Player player, Fissure fissure) {
        player.setNextTile(World.getFreeTile(fissure.location, 2));
        player.closeInterfaces();
    }
}
