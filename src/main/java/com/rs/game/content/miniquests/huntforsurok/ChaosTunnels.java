package com.rs.game.content.miniquests.huntforsurok;

import com.rs.cache.loaders.map.WorldMapDefinitions;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.content.miniquests.huntforsurok.bork.Bork;
import com.rs.game.content.miniquests.huntforsurok.bork.BorkController;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Map;

@PluginEventHandler
public class ChaosTunnels {
    public static ObjectClickHandler handleRifts = new ObjectClickHandler(new Object[] { 65203 }, e -> {
            if (e.getPlayer().inCombat(10000) || e.getPlayer().hasBeenHit(10000)) {
                e.getPlayer().sendMessage("You cannot enter the rift while you're under attack.");
                return;
            }
            if (e.objectAt(3058, 3550))
                e.getPlayer().setNextTile(e.getPlayer().transform(125, 1920, 0));
            if (e.objectAt(3118, 3570))
                e.getPlayer().setNextTile(e.getPlayer().transform(130, 1920, 0));
            if (e.objectAt(3129, 3587))
                e.getPlayer().setNextTile(e.getPlayer().transform(105, 1972, 0));
            if (e.objectAt(3164, 3561))
                e.getPlayer().setNextTile(e.getPlayer().transform(128, 1918, 0));
            if (e.objectAt(3176, 3585))
                e.getPlayer().setNextTile(Tile.of(3290, 5539, 0));
    });

    public static ObjectClickHandler handleExitRopes = new ObjectClickHandler(new Object[] { 28782 }, e -> {
        if (e.objectAt(3183, 5470))
            e.getPlayer().setNextTile(e.getPlayer().transform(-125, -1920, 0));
        if (e.objectAt(3248, 5490))
            e.getPlayer().setNextTile(e.getPlayer().transform(-130, -1920, 0));
        if (e.objectAt(3234, 5559))
            e.getPlayer().setNextTile(e.getPlayer().transform(-105, -1972, 0));
        if (e.objectAt(3292, 5479))
            e.getPlayer().setNextTile(e.getPlayer().transform(-128, -1918, 0));
        if (e.objectAt(3291, 5538))
            e.getPlayer().setNextTile(e.getPlayer().transform(-115, -1953, 0));
        e.getPlayer().getControllerManager().startController(new WildernessController());
    });

    public static ObjectClickHandler handleChaosTunnelsPortals = new ObjectClickHandler(new Object[] { 28779, 28888, 29537, 23095 }, e -> {
        PortalPair portal = PortalPair.forTile(e.getObject().getTile());
        if (portal == null)
            return;
        portal.travel(e.getPlayer(), e.getObject());
    });

    public enum PortalPair {
        _1(Tile.of(3254, 5451, 0), Tile.of(3250, 5448, 0)),
        _2(Tile.of(3241, 5445, 0), Tile.of(3233, 5445, 0)),
        _3(Tile.of(3259, 5446, 0), Tile.of(3265, 5491, 0), true),
        _4(Tile.of(3299, 5484, 0), Tile.of(3303, 5477, 0)),
        _5(Tile.of(3286, 5470, 0), Tile.of(3285, 5474, 0)),
        _6(Tile.of(3290, 5463, 0), Tile.of(3302, 5469, 0)),
        _7(Tile.of(3296, 5455, 0), Tile.of(3299, 5450, 0)),
        _8(Tile.of(3280, 5501, 0), Tile.of(3285, 5508, 0)),
        _9(Tile.of(3300, 5514, 0), Tile.of(3297, 5510, 0)),
        _10(Tile.of(3289, 5533, 0), Tile.of(3288, 5536, 0)),
        _11(Tile.of(3285, 5527, 0), Tile.of(3282, 5531, 0)),
        _12(Tile.of(3325, 5518, 0), Tile.of(3323, 5531, 0)),
        _13(Tile.of(3299, 5533, 0), Tile.of(3297, 5536, 0)),
        _14(Tile.of(3321, 5554, 0), Tile.of(3315, 5552, 0)),
        _15(Tile.of(3291, 5555, 0), Tile.of(3285, 5556, 0)),
        _16(Tile.of(3266, 5552, 0), Tile.of(3262, 5552, 0)),
        _17(Tile.of(3256, 5561, 0), Tile.of(3253, 5561, 0)),
        _18(Tile.of(3249, 5546, 0), Tile.of(3252, 5543, 0)),
        _19(Tile.of(3261, 5536, 0), Tile.of(3268, 5534, 0)),
        _20(Tile.of(3243, 5526, 0), Tile.of(3241, 5529, 0)),
        _21(Tile.of(3230, 5547, 0), Tile.of(3226, 5553, 0)),
        _22(Tile.of(3206, 5553, 0), Tile.of(3204, 5546, 0)),
        _23(Tile.of(3211, 5533, 0), Tile.of(3214, 5533, 0)),
        _24(Tile.of(3208, 5527, 0), Tile.of(3211, 5523, 0)),
        _25(Tile.of(3201, 5531, 0), Tile.of(3197, 5529, 0), true),
        _26(Tile.of(3202, 5515, 0), Tile.of(3196, 5512, 0), true),
        _27(Tile.of(3190, 5515, 0), Tile.of(3190, 5519, 0)),
        _28(Tile.of(3185, 5518, 0), Tile.of(3181, 5517, 0)),
        _29(Tile.of(3187, 5531, 0), Tile.of(3182, 5530, 0)),
        _30(Tile.of(3169, 5510, 0), Tile.of(3159, 5501, 0)),
        _31(Tile.of(3165, 5515, 0), Tile.of(3173, 5530, 0)),
        _32(Tile.of(3156, 5523, 0), Tile.of(3152, 5520, 0)),
        _33(Tile.of(3148, 5533, 0), Tile.of(3153, 5537, 0)),
        _34(Tile.of(3143, 5535, 0), Tile.of(3147, 5541, 0)),
        _35(Tile.of(3168, 5541, 0), Tile.of(3171, 5542, 0)),
        _36(Tile.of(3190, 5549, 0), Tile.of(3190, 5554, 0)),
        _37(Tile.of(3180, 5557, 0), Tile.of(3174, 5558, 0)),
        _38(Tile.of(3162, 5557, 0), Tile.of(3158, 5561, 0)),
        _39(Tile.of(3166, 5553, 0), Tile.of(3162, 5545, 0)),
        _40(Tile.of(3115, 5528, 0), Tile.of(3142, 5545, 0)),
        _41(Tile.of(3260, 5491, 0), Tile.of(3266, 5446, 0), true),
        _42(Tile.of(3241, 5469, 0), Tile.of(3233, 5470, 0)),
        _43(Tile.of(3235, 5457, 0), Tile.of(3229, 5454, 0)),
        _44(Tile.of(3280, 5460, 0), Tile.of(3273, 5460, 0)),
        _45(Tile.of(3283, 5448, 0), Tile.of(3287, 5448, 0)),
        _46(Tile.of(3244, 5495, 0), Tile.of(3239, 5498, 0)),
        _47(Tile.of(3232, 5501, 0), Tile.of(3238, 5507, 0)),
        _48(Tile.of(3218, 5497, 0), Tile.of(3222, 5488, 0)),
        _49(Tile.of(3218, 5478, 0), Tile.of(3215, 5475, 0)),
        _50(Tile.of(3224, 5479, 0), Tile.of(3222, 5474, 0)),
        _51(Tile.of(3208, 5471, 0), Tile.of(3210, 5477, 0)),
        _52(Tile.of(3214, 5456, 0), Tile.of(3212, 5452, 0)),
        _53(Tile.of(3204, 5445, 0), Tile.of(3197, 5448, 0), true),
        _54(Tile.of(3189, 5444, 0), Tile.of(3187, 5460, 0)),
        _55(Tile.of(3192, 5472, 0), Tile.of(3186, 5472, 0)),
        _56(Tile.of(3185, 5478, 0), Tile.of(3191, 5482, 0)),
        _57(Tile.of(3171, 5473, 0), Tile.of(3167, 5471, 0)),
        _58(Tile.of(3171, 5478, 0), Tile.of(3167, 5478, 0)),
        _59(Tile.of(3168, 5456, 0), Tile.of(3178, 5460, 0)),
        _60(Tile.of(3191, 5495, 0), Tile.of(3194, 5490, 0)),
        _61(Tile.of(3141, 5480, 0), Tile.of(3142, 5489, 0)),
        _62(Tile.of(3142, 5462, 0), Tile.of(3154, 5462, 0)),
        _63(Tile.of(3143, 5443, 0), Tile.of(3155, 5449, 0)),
        _64(Tile.of(3307, 5496, 0), Tile.of(3317, 5496, 0)),
        _65(Tile.of(3318, 5481, 0), Tile.of(3322, 5480, 0)),
        TUNNELS_OF_CHAOS(Tile.of(3326, 5469, 0), Tile.of(3159, 5208, 0), true),
        CHAOS_ALTAR(Tile.of(3152, 5233, 0), Tile.of(2282, 4837, 0), true),
        BORK(Tile.of(3142, 5545, 0), Tile.of(3115, 5528, 0), true);
        private static Map<Integer, PortalPair> MAPPING = new Int2ObjectOpenHashMap<>();

        static {
            for (PortalPair p : PortalPair.values()) {
                MAPPING.put(p.tile1.getTileHash(), p);
                MAPPING.put(p.tile2.getTileHash(), p);
            }
        }

        public final Tile tile1;
        public final Tile tile2;
        public final boolean surokLocked;

        PortalPair(Tile tile1, Tile tile2, boolean surokLocked) {
            this.tile1 = tile1;
            this.tile2 = tile2;
            this.surokLocked = surokLocked;
        }

        PortalPair(Tile tile1, Tile tile2) {
            this(tile1, tile2, false);
        }

        public static PortalPair forTile(Tile tile) {
            return MAPPING.get(tile.getTileHash());
        }

        public void travel(Player player, GameObject fromPortal) {
            if (surokLocked && !player.isMiniquestComplete(Miniquest.HUNT_FOR_SUROK, "to travel through this portal."))
                return;
            if (this == BORK) {
                if (player.getDailyB("borkKilled")) {
                    player.sendMessage("You have already killed Bork today.");
                    return;
                }
                boolean entering = tile1.getTileHash() == fromPortal.getTile().getTileHash();
                if (entering)
                    player.getControllerManager().startController(new BorkController());
                return;
            }
            player.setNextTile(tile1.getTileHash() == fromPortal.getTile().getTileHash() ? tile2 : tile1);
        }
    }
}
