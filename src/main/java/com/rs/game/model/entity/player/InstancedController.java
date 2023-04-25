package com.rs.game.model.entity.player;

import com.rs.game.map.instance.Instance;

public abstract class InstancedController extends Controller {
    private Instance instance;

    public InstancedController(Instance instance) {
        this.instance = instance;
    }

    public final void start() {
        _buildInstance();
    }

    public void magicTeleported(int type) {
        _destroyInstance();
        removeController();
    }

    @Override
    public void forceClose() {
        _destroyInstance();
    }

    public final void _buildInstance() {
        instance.requestChunkBound().thenAccept(b -> {
           instance.teleportTo(player);
           onBuildInstance();
        });
    }

    public abstract void onBuildInstance();

    public final void _destroyInstance() {
        instance.destroy().thenAccept(b -> onDestroyInstance());
    }

    public abstract  void onDestroyInstance();

    public final boolean login() {
        if (instance.isPersistent())
            _buildInstance();
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
