package com.rs.plugin.events;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.handlers.PluginHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCKillParticipatedEvent implements PluginEvent {
    private static final Map<Object, List<PluginHandler<? extends PluginEvent>>> HANDLERS = new HashMap<>();

    private final NPC npc;
    private final Entity participant;

    public NPCKillParticipatedEvent(NPC npc, Entity participant) {
        this.npc = npc;
        this.participant = participant;
    }

    public NPC getNPC() {
        return npc;
    }

    public Entity getParticipant() {
        return participant;
    }

    public Entity component1() {
        return participant;
    }

    public NPC component2() {
        return npc;
    }

    public boolean participatingPlayer() {
        if (participant != null && (participant instanceof Player))
            return true;
        return false;
    }

    @Override
    public List<PluginHandler<? extends PluginEvent>> getMethods() {
        List<PluginHandler<? extends PluginEvent>> methods = HANDLERS.get(npc.getId());
        if (methods == null)
            methods = HANDLERS.get(npc.getDefinitions().getName());
        if (methods == null)
            return null;
        return methods;
    }

    public static void registerMethod(Class<?> eventType, PluginHandler<? extends PluginEvent> method) {
        for (Object key : method.keys()) {
            List<PluginHandler<? extends PluginEvent>> methods = HANDLERS.computeIfAbsent(key, _ -> new ArrayList<>());
            methods.add(method);
        }
    }
}
