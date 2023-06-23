package com.rs.game.model.entity.player;

import com.rs.game.map.instance.Instance;

import java.util.function.Consumer;

public abstract class InstancedController extends Controller {
    private Instance instance;

    public InstancedController(Instance instance) {
        this.instance = instance;
    }

    public final void start() {
        _buildInstance(() -> {  });
    }

    public void magicTeleported(int type) {
        _destroyInstance();
        removeController();
    }

    @Override
    public void forceClose() {
        _destroyInstance();
    }

    public final void _buildInstance(Runnable onBuild) {
        instance.requestChunkBound().thenAccept(b -> {
            onBuild.run();
            onBuildInstance();
        });
    }

    public abstract void onBuildInstance();

    public final void _destroyInstance() {
        if (instance != null)
            instance.destroy();
        onDestroyInstance();
    }

    public abstract  void onDestroyInstance();

    public final boolean login() {
        if (instance.isPersistent())
            _buildInstance(() -> {
                player.setForceNextMapLoadRefresh(true);
                player.loadMapRegions();
            });
        return !instance.isPersistent();
    }

    public final boolean logout() {
        player.setTile(instance.getReturnTo());
        _destroyInstance();
        return !instance.isPersistent();
    }

    public Instance getInstance() {
        return instance;
    }
}
