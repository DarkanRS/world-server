package com.rs.game.model.entity;

import com.rs.game.content.skills.magic.TeleType;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

import java.util.function.Supplier;

public record Teleport(Tile start, Tile destination, TeleType type, Supplier<Boolean> meetsRequirements, Runnable begin, Runnable end) {
    public static void execute(Teleport teleport) {
        
    }
}