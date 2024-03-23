@file:Suppress("unused")

package com.rs.engine.pathfinder.collision

public object CollisionStrategies {
    public val Normal: CollisionStrategy = NormalBlockFlagCollision()
    public val Blocked: CollisionStrategy = BlockedFlagCollision()
    public val Fly: CollisionStrategy = LineOfSightBlockFlagCollision()
    public val Indoors: CollisionStrategy = IndoorsFlagCollision()
    public val Outdoors: CollisionStrategy = OutdoorsFlagCollision()
}
