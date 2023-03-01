package com.rs.game.map.update;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.PacketEncoder;

import java.util.function.Function;

public class ChunkUpdate {
    private int chunkLocalHash;
    private PacketEncoder encoder;
    private Function<Player, Boolean> canSee;

    public ChunkUpdate(int chunkLocalHash, PacketEncoder encoder, Function<Player, Boolean> canSee) {
        this.chunkLocalHash = chunkLocalHash;
        this.encoder = encoder;
        this.canSee = canSee;
    }
}
