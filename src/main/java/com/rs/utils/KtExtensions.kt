package com.rs.utils

import com.rs.game.model.entity.Entity
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils.getDistance

fun Set<Entity>.closestOrNull(tile: Tile): Entity? = this.minByOrNull { entity -> getDistance(entity.tile, tile) }