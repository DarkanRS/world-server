package com.rs.game.content.holidayevents.easter.easter22.npcs;

import com.rs.game.content.holidayevents.easter.easter22.EggHunt;
import com.rs.game.model.entity.npc.others.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class EasterChick extends OwnedNPC {

    public static EggHunt.Spawns spawnEgg;

    public EasterChick(Player player, int id, WorldTile tile) {
        super(player, id, tile, true);
        setRandomWalk(true);
        
        //3673 falling spawn anim
        //3806 lay down and die anim
        //14842 lay down and die long anim
        //cannon impact gfx 3037
        
//        16432: [3959] Unknown
//        scotch egg anim 16433: [3960] SpotAnim: 3034
//        marshmallow anim 16434: [3962] SpotAnim: 3035
//        projectile impact maybe? 16435: [3962] SpotAnim: 3036
//        explosion anim 16436: [3798] SpotAnim: 3037
//        16437: [3959] Object: 70104, Object: 70105
//        16438: [2923] Unknown
//        16439: [3959] Unknown
//        16440: [3959] Object: 69753
//        16441: [3958] Object: 70115, Object: 70116, Object: 70117, Object: 70118, Object: 72591, Object: 72592, Object: 72593, Object: 72594, Object: 72595, Object: 72596, Object: 72597, Object: 72598, Object: 72599, Object: 72600, Object: 72601, Object: 72602, Object: 72603, Object: 72604, Object: 73669, Object: 73670, Object: 73671, Object: 73672
//        16442: [990] SpotAnim: 3038
//        16443: [990] SpotAnim: 3039
//        16444: [233] Unknown
//        16445: [233] Unknown
    }

    @Override
    public void processNPC() {
        super.processNPC();
    }

    public static void setEasterEggSpawn(EggHunt.Spawns egg) { spawnEgg = egg; }

    public static EggHunt.Spawns getEasterEggSpawn() { return spawnEgg; }

}