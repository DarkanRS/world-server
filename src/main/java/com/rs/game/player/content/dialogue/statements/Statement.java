package com.rs.game.player.content.dialogue.statements;

import com.rs.game.player.Player;

public interface Statement {
    void send(Player player);
    int getOptionId(int componentId);
}
