package com.rs.game.content.combat.special_attacks;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;

import java.util.function.BiFunction;

public class SpecialAttack {
    public enum Type {
        MELEE,
        RANGE,
        MAGIC
    }

    private boolean instant = false;
    private int energyCost;
    private Type type;
    private BiFunction<Player, Entity, Integer> execute;

    public SpecialAttack(Type type, int energyCost, BiFunction<Player, Entity, Integer> execute) {
        this.type = type;
        this.energyCost = energyCost;
        this.execute = execute;
    }

    public SpecialAttack(boolean instant, int energyCost, BiFunction<Player, Entity, Integer> execute) {
        this.instant = instant;
        this.energyCost = energyCost;
        this.execute = execute;
    }

    public boolean isInstant() {
        return instant;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public BiFunction<Player, Entity, Integer> getExecute() {
        return execute;
    }

    public Type getType() {
        return type;
    }
}
