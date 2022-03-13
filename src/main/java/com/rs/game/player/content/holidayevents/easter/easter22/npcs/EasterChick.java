package com.rs.game.player.content.holidayevents.easter.easter22.npcs;

import com.rs.game.npc.others.OwnedNPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.holidayevents.easter.easter22.EggHunt;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class EasterChick extends OwnedNPC {

    public static EggHunt.Spawns spawnEgg;

    public EasterChick(Player player, int id, WorldTile tile) {
        super(player, id, tile, true);
    }

    @Override
    public void processNPC() {
        super.processNPC();
    }

    public static void setEasterEggSpawn(EggHunt.Spawns egg) { spawnEgg = egg; }

    public static EggHunt.Spawns getEasterEggSpawn() { return spawnEgg; }

}