package com.rs.game.content.world.areas.waterbirth_island;

import com.rs.game.World;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class DagannothCaves {

    public static ObjectClickHandler handleCaveEntrance = new ObjectClickHandler(new Object[] { 8929, 8966 }, e -> {
        switch (e.getObjectId()) {
            case 8929 -> e.getPlayer().useStairs(-1, Tile.of(2442, 10147, 0), 0, 1);
            case 8966 -> e.getPlayer().useStairs(-1, Tile.of(2523, 3740, 0), 0, 1);
        }
    });

    public static ObjectClickHandler handleDksLadder = new ObjectClickHandler(new Object[] { 10229, 10230 }, e ->  {
        switch (e.getObjectId()) {
            case 10229 -> e.getPlayer().useStairs(828, Tile.of(1910, 4367, 0), 1, 1);
            case 10230 -> e.getPlayer().useStairs(828, Tile.of(2900, 4449, 0), 1, 1);
        }
    });

    public static ObjectClickHandler handleDagDoors = new ObjectClickHandler(new Object[] { 8958, 8959, 8960 }, e -> World.removeObjectTemporary(e.getObject(), Ticks.fromMinutes(1)));
    public static ObjectClickHandler handleStairs = new ObjectClickHandler(new Object[] { 10177, 10193, 8930, 10195, 10196, 10198, 10197, 10199, 10200, 10201, 10202, 10203, 10204, 10205, 10206, 10207, 10208, 10209, 10210, 10211, 10212, 10213, 10214, 10215, 10216, 10230, 10229, 10217, 10218, 10226, 10225, 10228, 10227, 10194, 10219, 10220, 10221, 10222, 10223, 10224 }, e -> {
        switch (e.getObjectId()) {
            case 10177 -> e.getPlayer().promptUpDown(
                828, "Go up the stairs.", Tile.of(2544, 3741, 0),
                "Go down the stairs.", Tile.of(1798, 4407, 3));
            case 10193, 8930 -> e.getPlayer().useStairs(-1, Tile.of(2545, 10143, 0), 0, 1);
            case 10194 -> e.getPlayer().useStairs(-1, Tile.of(2501, 3636, 0), 0, 1);
            case 10195 -> e.getPlayer().useStairs(-1, Tile.of(1810, 4405, 2), 0, 1);
            case 10196 -> e.getPlayer().useStairs(-1, Tile.of(1807, 4405, 3), 0, 1);
            case 10197 -> e.getPlayer().useStairs(-1, Tile.of(1823, 4404, 2), 0, 1);
            case 10198 -> e.getPlayer().useStairs(-1, Tile.of(1825, 4404, 3), 0, 1);
            case 10199 -> e.getPlayer().useStairs(-1, Tile.of(1834, 4388, 2), 0, 1);
            case 10200 -> e.getPlayer().useStairs(-1, Tile.of(1834, 4390, 3), 0, 1);
            case 10201 -> e.getPlayer().useStairs(-1, Tile.of(1810, 4394, 1), 0, 1);
            case 10202 -> e.getPlayer().useStairs(-1, Tile.of(1812, 4394, 2), 0, 1);
            case 10203 -> e.getPlayer().useStairs(-1, Tile.of(1799, 4386, 2), 0, 1);
            case 10204 -> e.getPlayer().useStairs(-1, Tile.of(1799, 4389, 1), 0, 1);
            case 10205 -> e.getPlayer().useStairs(-1, Tile.of(1797, 4382, 1), 0, 1);
            case 10206 -> e.getPlayer().useStairs(-1, Tile.of(1796, 4382, 2), 0, 1);
            case 10207 -> e.getPlayer().useStairs(-1, Tile.of(1800, 4369, 2), 0, 1);
            case 10208 -> e.getPlayer().useStairs(-1, Tile.of(1802, 4369, 1), 0, 1);
            case 10209 -> e.getPlayer().useStairs(-1, Tile.of(1828, 4362, 1), 0, 1);
            case 10210 -> e.getPlayer().useStairs(-1, Tile.of(1825, 4362, 2), 0, 1);
            case 10211 -> e.getPlayer().useStairs(-1, Tile.of(1863, 4373, 2), 0, 1);
            case 10212 -> e.getPlayer().useStairs(-1, Tile.of(1863, 4370, 1), 0, 1);
            case 10213 -> e.getPlayer().useStairs(-1, Tile.of(1864, 4389, 1), 0, 1);
            case 10214 -> e.getPlayer().useStairs(-1, Tile.of(1864, 4387, 2), 0, 1);
            case 10215 -> e.getPlayer().useStairs(-1, Tile.of(1890, 4408, 0), 0, 1);
            case 10216 -> e.getPlayer().useStairs(-1, Tile.of(1890, 4406, 1), 0, 1);
            case 10217 -> e.getPlayer().useStairs(-1, Tile.of(1957, 4373, 1), 0, 1);
            case 10218 -> e.getPlayer().useStairs(-1, Tile.of(1957, 4370, 0), 0, 1);
            case 10219 -> e.getPlayer().useStairs(-1, Tile.of(1824, 4379, 3), 0, 1);
            case 10220 -> e.getPlayer().useStairs(-1, Tile.of(1824, 4382, 2), 0, 1);
            case 10221 -> e.getPlayer().useStairs(-1, Tile.of(1838, 4374, 2), 0, 1);
            case 10222 -> e.getPlayer().useStairs(-1, Tile.of(1838, 4377, 3), 0, 1);
            case 10223 -> e.getPlayer().useStairs(-1, Tile.of(1850, 4385, 1), 0, 1);
            case 10224 -> e.getPlayer().useStairs(-1, Tile.of(1850, 4387, 2), 0, 1);
            case 10225 -> e.getPlayer().useStairs(-1, Tile.of(1932, 4377, 1), 0, 1);
            case 10226 -> e.getPlayer().useStairs(-1, Tile.of(1932, 4380, 2), 0, 1);
            case 10227 -> e.getPlayer().useStairs(-1, Tile.of(1961, 4392, 2), 0, 1);
            case 10228 -> e.getPlayer().useStairs(-1, Tile.of(1961, 4393, 3), 0, 1);
            case 10229 -> e.getPlayer().useStairs(-1, Tile.of(1912, 4367, 0), 0, 1);
            case 10230 -> e.getPlayer().useStairs(-1, Tile.of(2900, 4449, 0), 0, 1);
        }
    });
}
