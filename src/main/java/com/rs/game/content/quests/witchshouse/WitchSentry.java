// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.quests.witchshouse;

import com.rs.game.World;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.WorldUtil;

import static com.rs.game.content.quests.witchshouse.WitchsHouse.WITCH;

@PluginEventHandler
public class WitchSentry extends NPC {

    public static NPCInstanceHandler toFunc = new NPCInstanceHandler(WITCH, (npcId, tile) -> new WitchSentry(tile));

    public WitchSentry(Tile tile) {
        super(WITCH, tile, true);
        setRandomWalk(false);
        WorldTasks.schedule(new Task() {
            int tick = 10;
            Player player;

            @Override
            public void run() {
                if (tick > 3) {
                    for (Player p : World.getPlayersInChunkRange(getChunkId(), 1)) {
                        if (p == null)
                            continue;
                        if (lineOfSightTo(p, false)) {
							player = p;
							p.lock();
                            resetWalkSteps();
                            faceEntity(p);
                            setNextSpotAnim(new SpotAnim(108));
                            setNextAnimation(new Animation(711));
                            forceTalk("Get out!");
                            tick = 3;
							break;
                        }
                    }
                }
                if (tick == 1)
                    tick = 10;
                if (tick == 2) {
                    player.unlock();
                    Magic.sendObjectTeleportSpell(player, false, Tile.of(2892, 3373, 0));
					player = null;
                    tick--;
                }
                if (tick == 3)
                    tick--;

                if (tick == 5)
                    walkToAndExecute(Tile.of(2895, 3363, 0), () -> tick = 10);
                if (tick == 10)
                    walkToAndExecute(Tile.of(2922, 3363, 0), () -> tick = 5);

            }
        }, 0, 1);
    }

    @Override
    public void processEntity() {
        super.processEntity();
        processNPC();
    }

    @Override
    public boolean lineOfSightTo(Object target, boolean melee) {
        Tile tile = WorldUtil.targetToTile(target);
        if (World.hasLineOfSight(getMiddleTile(), target instanceof Entity e ? e.getMiddleTile() : tile)) {
            Logger.debug(WitchSentry.class, "lineOfSightTo", "dX:" + getDirection().getDx());
            if (getDirection().getDx() == 1) {
                if (tile.getX() > getX() && checkByConeSightX(tile))
                    return true;
            } else if (getDirection().getDx() == -1 && checkByConeSightX(tile))
                if (tile.getX() < getX())
                    return true;
        }
        return false;
    }

    public boolean checkByConeSightX(Tile tile) {
        int xDifference = Math.abs(tile.getX() - getX());
        int yDifference = Math.abs(tile.getY() - getY());
        if (yDifference <= xDifference)//one for one cone. Multiply difference by 2 for one for two cone.
            return true;
        return false;
    }


}
