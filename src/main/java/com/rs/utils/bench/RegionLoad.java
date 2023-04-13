package com.rs.utils.bench;

import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.engine.thread.LowPriorityTaskExecutor;
import com.rs.engine.thread.WorldThread;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.pathing.FixedTileStrategy;
import com.rs.game.model.entity.pathing.RouteFinder;
import com.rs.game.model.entity.player.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Tile;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.*;
import com.rs.utils.json.ControllerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class RegionLoad {
    private static ExecutorService service = Executors.newFixedThreadPool(1);
    public static void main(String[] args) throws IOException, InterruptedException {
        Logger.setupFormat();
        Logger.setLevel(Level.FINE); //FINER for traces
        JsonFileManager.setGSON(new GsonBuilder()
                .registerTypeAdapter(Controller.class, new ControllerAdapter())
                .registerTypeAdapter(Date.class, new DateAdapter())
                .registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
                .registerTypeAdapter(Packet.class, new PacketAdapter())
                .registerTypeAdapterFactory(new RecordTypeAdapterFactory())
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create());

        Settings.loadConfig();
        if (!Settings.getConfig().isDebug())
            Logger.setLevel(Level.WARNING);
        Cache.init(Settings.getConfig().getCachePath());

        MapXTEAs.loadKeys();
        LowPriorityTaskExecutor.initExecutors();

        ChunkManager.getChunk(Tile.of(3434, 3434, 0).getChunkId(), true);
        ChunkManager.getChunk(Tile.of(3460, 3460, 0).getChunkId(), true);
        ChunkManager.getChunk(Tile.of(3470, 3470, 0).getChunkId(), true);
        ChunkManager.getChunk(Tile.of(3470, 3470, 0).getChunkId(), true);
        WorldThread.init();
    }
}
