package com.rs.game.content.combat.special_attacks

import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.player.Player
import java.util.function.BiFunction

class SpecialAttack(val isInstant: Boolean = false, val energyCost: Int, val type: Type? = null, val execute: BiFunction<Player, Entity?, Int>) {
    enum class Type {
        MELEE,
        RANGE,
        MAGIC
    }

    constructor(type: Type?, energyCost: Int, execute: BiFunction<Player, Entity?, Int>) : this(false, energyCost, type, execute)

    constructor(instant: Boolean, energyCost: Int, execute: BiFunction<Player, Entity?, Int>) : this(instant, energyCost, null, execute)
}
