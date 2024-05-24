package com.rs.game.content.combat.special_attacks

import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import java.util.function.BiFunction
import java.util.function.Consumer

class SpecialAttack(val energyCost: Int, val type: Type? = null, val execute: BiFunction<Player, Entity, Int>? = null, val instant: Consumer<Player>? = null) {
    enum class Type {
        MELEE,
        RANGE,
        MAGIC
    }

    constructor(type: Type?, energyCost: Int, execute: BiFunction<Player, Entity, Int>) : this(energyCost, type, execute)

    constructor(energyCost: Int, instant: Consumer<Player>) : this(energyCost, null, null, instant)
}
