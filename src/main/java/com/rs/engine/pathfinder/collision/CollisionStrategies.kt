@file:Suppress("unused")

package com.rs.engine.pathfinder.collision

enum class CollisionStrategyType(val strategy: CollisionStrategy) {
    NORMAL(NormalBlockFlagCollision()),
    WATER(BlockedFlagCollision()),
    FLY(LineOfSightBlockFlagCollision()),
    INDOOR(IndoorsFlagCollision()),
    OUTDOOR(OutdoorsFlagCollision())
}
