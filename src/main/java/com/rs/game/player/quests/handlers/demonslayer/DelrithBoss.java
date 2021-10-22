package com.rs.game.player.quests.handlers.demonslayer;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class DelrithBoss extends NPC {
    Player p;
    private static int DELRITH_ID = 879;

    //Delrith animations
    static final int STUNNED = 4619;
    static final int REVIVE = 4620;
    static final int DIE = 4624;

    public boolean actuallyDead = false;

    public DelrithBoss(WorldTile tile) {
        super(DELRITH_ID, tile, true);
        p = World.getPlayersInRegion(this.getRegionId()).get(0);
    }

    private DelrithBoss getSelf() {
        return this;
    }

    @Override
    public void sendDeath(Entity source) {
        setNextAnimation(new Animation(STUNNED));
        removeTarget();

        WorldTasksManager.schedule(new WorldTask() {
            int tick = 0;
            int finalTick = Ticks.fromSeconds(12);
            boolean conversating = false;
            @Override
            public void run() {
                if(tick == finalTick)
                    setNextAnimation(new Animation(REVIVE));
                if(tick == finalTick+1) {
                    resetHP();
                    setTarget(source);
                    stop();
                }

                if(!conversating && !p.inCombat() && p.withinDistance(new WorldTile(getX(), getY(), getPlane()), 2)) {
                    conversating = true;
                    tick = -10;
                    p.faceTile(new WorldTile(getX(), getY(), getPlane()));
                    p.startConversation(new EncantationOptionsD(p, getSelf()).getStart());
                } else {
                    tick++;
                }
            }
        }, 0, 1);
    }

    public void die() {
        WorldTasksManager.schedule(new WorldTask() {
            int tick = 0;
            @Override
            public void run() {
                if(tick == 0) {
                    setNextAnimation(new Animation(DIE));
                    actuallyDead = true;
                }
                if(tick == 3) {
                    finish();
                    stop();
                }
                tick++;
            }
        }, 0, 1);
    }

    @Override
    public void handlePreHit(Hit hit) {
        if (hit.getSource() instanceof Player) {
            Player source = (Player) hit.getSource();
            if (source.getEquipment().getWeaponId() != -1) {
                if (ItemDefinitions.getDefs(source.getEquipment().getWeaponId()).getName().contains("Silverlight") ||
                        ItemDefinitions.getDefs(source.getEquipment().getWeaponId()).getName().contains("Darklight")) {
                    super.handlePreHit(hit);
                } else {
                    source.sendMessage("You need silverlight to damage Delrith.");
                    hit.setDamage(0);
                }
            }
        }
    }

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(DELRITH_ID) {
        @Override
        public NPC getNPC(int npcId, WorldTile tile) {
            return new DelrithBoss(tile);
        }
    };


}
