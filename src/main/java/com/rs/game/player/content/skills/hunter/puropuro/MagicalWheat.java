package com.rs.game.player.content.skills.hunter.puropuro;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.content.Effect;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.player.controllers.PuroPuroController;
import com.rs.game.player.controllers.PyramidPlunderController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class MagicalWheat {

//Clean up push through logic with varying times and farmers affinity buff taken into account
//Add task for moving wheat
//Debug to ensure rotating crop cirlce spawns is working
//Add the rest of the crop circle locations

//		Objects
//		25000 - No wheat
//		25021 - Has wheat
//		25022 - Growing wheat
//		25023 - Wilting wheat

// 		Anims
//		6593 - You use your strength to push through the wheat in the most efficient fashion.
//		6954 - You use your strength to push through the wheat.
//		6955 - You push through the wheat. It's hard work, though.
//		6615 - Impling animation while being caught

    public enum CropCirlces {

//        ARDOUGNE(new WorldTile()), //Ardougne - Wheat patch north of the Ardougne Market.
//        BRIMHAVEN(new WorldTile()), //Brimhaven - Wheat patch north of the Brimhaven Agility Arena.
        CATHERBY(new WorldTile(2818, 3470, 0)), //Catherby - Wheat patch north-east of the Catherby allotment patch.
//        DRAYNOR_VILLAGE(new WorldTile()), //Draynor Village - Wheat patch north-east of the Draynor Village bank.
//        HARMONY_ISLAND(new WorldTile()), //Harmony Island - Wheat patch north of the chapel.
//        LUMBRIDGE(new WorldTile()), //Lumbridge - Wheat patch north-west of Lumbridge, south-west of the Mill Lane Mill.
//        MISCELLANIA(new WorldTile()), //Miscellania - Wheat patch south-east of the Miscellania Castle.
//        MOS_LE_HARMLESS(new WorldTile()), //Mos Le'Harmless - Wheat patch east of Dodgy Mike's Second Hand Clothing.
//        RIMMINGTON(new WorldTile()), //Rimmington - Wheat patch south of the Rimmington mine.
//        TAVERLEY_NORTH(new WorldTile()), //Taverley - Wheat patch north-east of the Taverley Dungeon entrance.
//        TAVERLEY_SOUTH(new WorldTile()), //Taverley - Wheat patch south of Doric's hut.
//        TREE_GNOME_STRONGHOLD(new WorldTile()), //Tree Gnome Stronghold - Wheat patch south-west of the Grand Tree.
//        VARROCK_NORTH(new WorldTile()), //Varrock - Wheat patch north of the Cooks' Guild.
//        VARROCK_SOUTH(new WorldTile()), //Varrock - Wheat patch south-east of the Champions' Guild.
//        YANILLE(new WorldTile()), //Yanille - Wheat patch north-west of the Wizards' Guild.
        ;

        private final WorldTile entranceTile;

        CropCirlces(WorldTile tile) {
            this.entranceTile = tile;
        }

        public WorldTile getEntranceTile() {
            return entranceTile;
        }

        public int getX() {
            return entranceTile.getX();
        }

        public int getY() {
            return entranceTile.getY();
        }

        public int getPlane() {
            return entranceTile.getPlane();
        }
    }

    public static ObjectClickHandler teleportToPuroPuro = new ObjectClickHandler(new Object[] { 24988, 24991 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            WorldTasks.schedule(10, new WorldTask() {
                @Override
                public void run() {
                    e.getPlayer().getControllerManager().startController(new PuroPuroController());
                    PuroPuroController ctrl = e.getPlayer().getControllerManager().getController(PuroPuroController.class);
                    ctrl.setEntranceTile(currentLocation.getEntranceTile());
                    if (e.getObjectId() == 24988)
                        e.getPlayer().addEffect(Effect.FARMERS_AFFINITY, 3000);
                }
            });
            Magic.sendTeleportSpell(e.getPlayer(), 6601, -1, 1118, -1, 0, 0, new WorldTile(2590 + Utils.randomInclusive(0, 3), 4318 + Utils.randomInclusive(0, 3), 0), 9, false, Magic.OBJECT_TELEPORT);
        }
    };
    
    private static CropCirlces currentLocation = null;

    @ServerStartupEvent
    public static void initCropCircleSpawns() {
        WorldTasks.schedule(0, 3000, () -> {
            currentLocation = CropCirlces.values()[Utils.random(0, CropCirlces.values().length)];

            WorldTile[] impPath = new WorldTile[]{
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() - 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() - 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() - 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() + 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() + 1, currentLocation.getPlane()),
                    new WorldTile(currentLocation.getX() - 1, currentLocation.getY() - 1, currentLocation.getPlane())
            };

            NPC imp = new NPC(1531, new WorldTile(currentLocation.getX()-1, currentLocation.getY()-1, currentLocation.getPlane()));
            imp.setRandomWalk(false);
            imp.finishAfterTicks(42);

            WorldTasks.scheduleTimer(ticks -> {
                if (ticks % 5 == 0 && ticks <= 35) {
                    imp.setForceWalk(impPath[ticks/5]);
                }

                if (ticks == 41)
                    imp.setNextSpotAnim(new SpotAnim(1119)); //931?

                if (ticks == 45) {
                    spawnCropCircle();
                    return false;
                }
                return true;
            });
        });
    }

    private static void spawnCropCircle() {
        World.spawnObjectTemporary(new GameObject(24986, 2, currentLocation.getX()-1, currentLocation.getY()+1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24985, 2, currentLocation.getX(), currentLocation.getY()+1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24984, 2, currentLocation.getX()+1, currentLocation.getY()+1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24987, 2, currentLocation.getX()-1, currentLocation.getY(), currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24988, 0, currentLocation.getX(), currentLocation.getY(), currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24987, 0, currentLocation.getX()+1, currentLocation.getY(), currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24984, 0, currentLocation.getX()-1, currentLocation.getY()-1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24985, 0, currentLocation.getX(), currentLocation.getY()-1, currentLocation.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24986, 0, currentLocation.getX()+1, currentLocation.getY()-1, currentLocation.getPlane()), Ticks.fromMinutes(30));
    }
}
