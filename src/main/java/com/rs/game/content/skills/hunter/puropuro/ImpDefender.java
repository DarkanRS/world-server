package com.rs.game.content.skills.hunter.puropuro;

import java.util.List;

import com.rs.game.World;
import com.rs.game.content.skills.hunter.FlyingEntityHunter;
import com.rs.game.content.skills.hunter.FlyingEntityHunter.FlyingEntities;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.ClipType;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class ImpDefender extends NPC {

    public ImpDefender(Tile tile) {
        super(6074, tile);
        setClipType(ClipType.NORMAL);
        WorldTasks.schedule(0, Ticks.fromSeconds(5), () -> freeImplings());
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 6074 }, (npcId, tile) -> new ImpDefender(tile));

    public void freeImplings() {
        List<Player> players = World.getPlayersInChunkRange(getChunkId(), 1);

        if (players.size() == 0)
            return;

        for (Player p : players) {
            if (p.withinDistance(Tile.of(getX(), getY(), getPlane()), 3)) {
                if (p.getInventory().containsItem(11262)) {
                    Item i = p.getInventory().getItemById(11262);
                    boolean used = (boolean)i.getMetaData("used");
                    if (used) {
                        i.setId(229);
                        i.deleteMetaData();
                    } else
                        i.addMetaData("used", true);
                    p.sendMessage("Your repellent protects you from the Imp Defender.");
                    if (Utils.random(10000) == 0) {
                        p.faceEntity(this);
                        p.setNextAnimation(new Animation(8991));
                        p.setNextForceTalk(new ForceTalk("Swiper, no swiping!"));
                    }
                    return;
                }
                if (Utils.random(10) == 0) {
                   int jar = getLowestImplingJar(p);
                   if (jar == -1)
                       return;
                    p.lock();
                    walkToAndExecute(p.getNearestTeleTile(1), () -> {
                        p.faceEntity(this);
                        p.getInventory().replace(jar, FlyingEntityHunter.IMPLING_JAR);
                        setNextForceTalk(new ForceTalk("Be Free!"));
                    });
                    p.unlock();
                }
            }
        }
    }

    public static int getLowestImplingJar(Player p) {
        int jar = -1;
        for (Item i : p.getInventory().getItems().getItemsNoNull()) {
            if (FlyingEntities.forItem(i.getId()) != null) {
                if (jar == -1 || jar > i.getId())
                    jar = i.getId();
            }
        }
        return jar;
    }
}