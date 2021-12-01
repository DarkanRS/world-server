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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class OwnedNPC extends NPC {

    private Player owner;
    private boolean hideFromOtherPlayers;
    private final int[][] checkNearDirs;
    private boolean autoDespawnAtDistance = true;

    public OwnedNPC(Player owner, int id, WorldTile tile, boolean hideFromOtherPlayers) {
        super(id, tile, true);
        this.owner = owner;
        this.hideFromOtherPlayers = hideFromOtherPlayers;
        this.checkNearDirs = Utils.getCoordOffsetsNear(super.getSize());
    }

    @Override
    public void processNPC() {
        if (getOwner() == null || getOwner().hasFinished() || (autoDespawnAtDistance && !withinDistance(getOwner(), 15))) {
            onDespawnEarly();
            finish();
        }
        super.processNPC();
    }

    @Override
    public boolean withinDistance(Player player, int distance) {
        if (!hideFromOtherPlayers || player == getOwner())
            return super.withinDistance(player, distance);
        return false;
    }
    
    @Override
    public boolean canBeAttackedBy(Player player) {
    	if (getOwner() != player) {
			player.sendMessage("They aren't interested in you.");
			return false;
		}
    	return true;
    }

    public void onDespawnEarly() {}

    public Player getOwner() {
        return owner;
    }
    
    public WorldTile getNearestTileToEntity(Entity entity) {
    	int size = getSize();
		WorldTile teleTile = null;
		for (int dir = 0; dir < checkNearDirs[0].length; dir++) {
			final WorldTile tile = new WorldTile(new WorldTile(entity.getX() + checkNearDirs[0][dir], entity.getY() + checkNearDirs[1][dir], entity.getPlane()));
			if (World.floorAndWallsFree(tile, size)) {
				teleTile = tile;
				break;
			}
		}
		if (teleTile == null)
			teleTile = new WorldTile(getOwner());
		return teleTile;
    }
    
	public void teleToOwner() {
		setNextWorldTile(getNearestTileToEntity(owner));
	}

	public boolean isAutoDespawnAtDistance() {
		return autoDespawnAtDistance;
	}

	public void setAutoDespawnAtDistance(boolean autoDespawnAtDistance) {
		this.autoDespawnAtDistance = autoDespawnAtDistance;
	}
}
