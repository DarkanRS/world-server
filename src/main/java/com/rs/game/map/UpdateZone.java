package com.rs.game.map;

import com.google.common.base.Objects;
import com.rs.cache.loaders.map.RegionSize;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.net.packets.encoders.updatezone.*;
import com.rs.lib.util.Logger;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpdateZone {
    private int baseChunkId;
    private int baseChunkX;
    private int baseChunkY;
    private RegionSize size;
    protected Set<Integer> watchers = IntSets.synchronize(new IntOpenHashSet());
    private List<UpdateZonePartialEnclosed> chunkUpdates = new ObjectArrayList<>();

    public UpdateZone(int baseChunkId, RegionSize size) {
        int[] coords = MapUtils.decode(Structure.CHUNK, baseChunkId);
        this.baseChunkId = baseChunkId;
        this.baseChunkX = coords[0];
        this.baseChunkY = coords[1];
        this.size = size;
    }

    public static int getId(int baseChunkId, RegionSize size) {
        return Objects.hashCode(baseChunkId, size);
    }

    public void rebuildUpdateZone() {
        for (int planeOff = 0;planeOff < 4 * Chunk.PLANE_INC;planeOff += Chunk.PLANE_INC) {
            for (int chunkXOff = 0; chunkXOff <= (size.size / 8) * Chunk.X_INC; chunkXOff += Chunk.X_INC) {
                for (int chunkYOff = 0; chunkYOff <= (size.size / 8); chunkYOff++) {
                    Chunk chunk = ChunkManager.getChunk(baseChunkId + chunkXOff + chunkYOff + planeOff);
                    if (!chunk.getUpdates().isEmpty()) {
                        chunkUpdates.add(new UpdateZonePartialEnclosed(baseChunkId, chunk.getId(), chunk.getUpdates()));
                    }
                }
            }
        }
    }

    private void sendUpdates(Player player) {
        if (chunkUpdates.isEmpty() || player == null || player.hasFinished() || !player.hasStarted())
            return;
        for (UpdateZonePartialEnclosed packet : chunkUpdates)
            player.getSession().writeToQueue(packet);
    }

    public void update() {
        rebuildUpdateZone();
        for (int pid : watchers)
            sendUpdates(World.getPlayers().get(pid));
        chunkUpdates.clear();
    }

//    public void init(Player player) {
//        for (int plane = 0;plane < 4;plane++) {
//            for (int chunkX = baseChunkX; chunkX < baseChunkX + (size.size / 8); baseChunkX++) {
//                for (int chunkY = baseChunkX; chunkY < baseChunkY + (size.size / 8); baseChunkY++) {
//                    Chunk chunk = World.getChunk(MapUtils.encode(Structure.CHUNK, chunkX, chunkY, plane));
//                    chunk.init(player);
//                }
//            }
//        }
//    }

    public void addWatcher(int pid) {
        watchers.add(pid);
    }

    public void removeWatcher(int pid) {
        watchers.remove(pid);
        if (watchers.isEmpty())
            ChunkManager.removeUpdateZone(baseChunkId, size);
    }
}
