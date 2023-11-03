package com.rs.game.content.dnds.eviltree;

import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class EvilTrees {
    public enum TreeType {
        NORMAL(11434, 11435, 11436, 14839),
        OAK(11437, 11438, 11439, 14840),
        WILLOW(11440, 11441, 11442, 14841),
        MAPLE(11443, 11444, 11915, 14842),
        YEW(11916, 11917, 11918, 14843),
        MAGIC(11919, 11920, 11921, 14844),
        ELDER(11922, 11923, 11924, 14845);

        public final int healthyObj, deg1Obj, deg2Obj, deadObj;

        TreeType(int healthyObj, int deg1Obj, int deg2Obj, int deadObj) {
            this.healthyObj = healthyObj;
            this.deg1Obj = deg1Obj;
            this.deg2Obj = deg2Obj;
            this.deadObj = deadObj;
        }
    }

    //11391, 11392, 11393, obj x-1/y-1, 11394, 11395 -> grown
    //11425 fire 0, 4
}
