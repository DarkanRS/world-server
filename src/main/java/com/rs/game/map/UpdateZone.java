package com.rs.game.map;

import com.google.common.base.Objects;
import com.rs.cache.loaders.map.RegionSize;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.net.packets.encoders.updatezone.*;
import com.rs.lib.util.MapUtils;
import com.rs.lib.util.MapUtils.Structure;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Set;

public class UpdateZone {
    private int baseChunkId;
    private int baseChunkX;
    private int baseChunkY;
    private RegionSize size;
    private Set<Integer> chunkIds = new IntOpenHashSet();
    protected Set<Integer> playerWatchers = IntSets.synchronize(new IntOpenHashSet());
    protected Set<Integer> npcWatchers = IntSets.synchronize(new IntOpenHashSet());
    private List<UpdateZonePartialEnclosed> chunkUpdates = new ObjectArrayList<>();

    public UpdateZone(int baseChunkId, RegionSize size) {
        int[] coords = MapUtils.decode(Structure.CHUNK, baseChunkId);
        this.baseChunkId = baseChunkId;
        this.baseChunkX = coords[0];
        this.baseChunkY = coords[1];
        this.size = size;
        for (int planeOff = 0;planeOff < 4 * Chunk.PLANE_INC;planeOff += Chunk.PLANE_INC) {
            for (int chunkXOff = 0; chunkXOff <= (size.size / 8) * Chunk.X_INC; chunkXOff += Chunk.X_INC) {
                for (int chunkYOff = 0; chunkYOff <= (size.size / 8); chunkYOff++) {
                    chunkIds.add(baseChunkId + chunkXOff + chunkYOff + planeOff);
                }
            }
        }
    }

    public static int getId(int baseChunkId, RegionSize size) {
        return Objects.hashCode(baseChunkId, size);
    }

    public void rebuildUpdateZone() {
       for (int chunkId : chunkIds) {
           Chunk chunk = ChunkManager.getChunk(chunkId);
           if (!chunk.getUpdates().isEmpty()) {
               chunkUpdates.add(new UpdateZonePartialEnclosed(baseChunkId, chunk.getId(), chunk.getUpdates()));
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
        for (int pid : playerWatchers)
            sendUpdates(World.getPlayers().get(pid));
        chunkUpdates.clear();
    }

    public void addPlayerWatcher(int pid) {
        playerWatchers.add(pid);
    }

    public void removePlayerWatcher(int pid) {
        playerWatchers.remove(pid);
        if (playerWatchers.isEmpty() && npcWatchers.isEmpty())
            ChunkManager.removeUpdateZone(baseChunkId, size);
    }

    public void addNPCWatcher(int nid) {
        npcWatchers.add(nid);
    }

    public void removeNPCWatcher(int nid) {
        npcWatchers.remove(nid);
        if (playerWatchers.isEmpty() && npcWatchers.isEmpty())
            ChunkManager.removeUpdateZone(baseChunkId, size);
    }

    public Set<Integer> getChunkIds() {
        return chunkIds;
    }
}
