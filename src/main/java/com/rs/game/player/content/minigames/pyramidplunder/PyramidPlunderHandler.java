package com.rs.game.player.content.minigames.pyramidplunder;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@PluginEventHandler
public class PyramidPlunderHandler {//All objects within the minigame

    public static ObjectClickHandler handlePyramidExits = new ObjectClickHandler(new Object[] { 16458 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            e.getPlayer().setNextWorldTile(new WorldTile(3288, 2801, 0));
            e.getPlayer().getControllerManager().forceStop();
        }
    };


}
