package com.rs.game.player.content.minigames.pyramidplunder;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.ForceMovement;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.region.ClipFlag;
import com.rs.game.region.RenderFlag;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.NPCInteractionDistanceHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.ObjectInteractionDistanceHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.Ticks;

import javax.tools.Diagnostic;
import java.util.List;

@PluginEventHandler
public class PyramidPlunderHandler {//All objects within the minigame
    final static int PLUNDER_INTERFACE = 428;

    static int[] exitDoors = new int[] {Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3),
            Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3)};

    @ServerStartupEvent
    public static void init() {
        WorldTasks.schedule(new WorldTask() {
            @Override
            public void run() {
                exitDoors = new int[] {Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3),
                        Utils.randomInclusive(0,3), Utils.randomInclusive(0,3), Utils.randomInclusive(0,3)};
            }
        }, 0, Ticks.fromMinutes(4));
    }

	public static ObjectClickHandler handlePyramidExits = new ObjectClickHandler(new Object[] { 16458 }) {
		@Override
		public void handle(ObjectClickEvent e) {
            e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                {
                    addOptions("Would you like to exit?", new Options() {
                        @Override
                        public void create() {
                            option("Yes", new Dialogue()
                                    .addNext(()-> {
                                        e.getPlayer().setNextWorldTile(new WorldTile(3288, 2801, 0));
                                        e.getPlayer().getControllerManager().forceStop();
                                    }));
                            option("No", new Dialogue());
                        }
                    });
                }
            });
		}
	};

    public static ObjectClickHandler handlePyramidTombDoors = new ObjectClickHandler(new Object[] { 16539, 16540, 16541, 16542 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            GameObject obj = e.getObject();

            if(isIn21Room(obj)) {
                if(16542 - obj.getId() == exitDoors[0]) {
                    p.setNextWorldTile(new WorldTile(1954, 4477, 0));
                    return;
                }
            } else if(isIn31Room(obj)) {
                if(16542 - obj.getId() == exitDoors[1]) {
                    ;
                    return;
                }
            } else if(isIn41Room(obj)) {
                if(16542 - obj.getId() == exitDoors[2]) {
                    ;
                    return;
                }
            } else if(isIn51Room(obj)) {
                if(16542 - obj.getId() == exitDoors[3]) {
                    ;
                    return;
                }
            } else if(isIn61Room(obj)) {
                if(16542 - obj.getId() == exitDoors[4]) {
                    ;
                    return;
                }
            } else if(isIn71Room(obj)) {
                if(16542 - obj.getId() == exitDoors[5]) {
                    ;
                    return;
                }
            } else if(isIn81Room(obj))
                if(16542 - obj.getId() == exitDoors[6]) {
                    ;
                    return;
                }
            p.sendMessage("This door is not the exit...");

        }
    };

    public static ObjectClickHandler handleSpearTrap = new ObjectClickHandler(new Object[] { 16517 }) {
        @Override
        public void handle(ObjectClickEvent e) {
            Player p = e.getPlayer();
            GameObject obj = e.getObject();
            if(isIn21Room(obj)) {
                if (p.getSkills().getLevel(Constants.THIEVING) < 21) {
                    p.sendMessage("You need a thieving level of 21 or higher...");
                    return;
                }
            } else if(isIn31Room(obj)) {
                if (p.getSkills().getLevel(Constants.THIEVING) < 31) {
                    p.sendMessage("You need a thieving level of 31 or higher...");
                    return;
                }
            } else if(isIn41Room(obj)) {
                if (p.getSkills().getLevel(Constants.THIEVING) < 41) {
                    p.sendMessage("You need a thieving level of 41 or higher...");
                    return;
                }
            } else if(isIn51Room(obj)) {
                if (p.getSkills().getLevel(Constants.THIEVING) < 51) {
                    p.sendMessage("You need a thieving level of 51 or higher...");
                    return;
                }
            } else if(isIn61Room(obj)) {
                if (p.getSkills().getLevel(Constants.THIEVING) < 61) {
                    p.sendMessage("You need a thieving level of 61 or higher...");
                    return;
                }
            } else if(isIn71Room(obj)) {
                if (p.getSkills().getLevel(Constants.THIEVING) < 71) {
                    p.sendMessage("You need a thieving level of 71 or higher...");
                    return;
                }
            } else if(isIn81Room(obj))
                if(p.getSkills().getLevel(Constants.THIEVING) < 81) {
                    p.sendMessage("You need a thieving level of 81 or higher...");
                    return;
                }
            passTrap(e);
        }
    };

    private static void passTrap(ObjectClickEvent e) {
        Player p = e.getPlayer();
        WorldTile tile = e.getObject();
        WorldTile[] nearbyTiles = new WorldTile[] {
            new WorldTile(tile.getX(), tile.getY()+1, tile.getPlane()), new WorldTile(tile.getX()+1, tile.getY(), tile.getPlane()),
            new WorldTile(tile.getX(), tile.getY()-1, tile.getPlane()), new WorldTile(tile.getX()-1, tile.getY(), tile.getPlane())
        };
        WorldTile[] farTiles = new WorldTile[] {
            new WorldTile(p.getX(), p.getY()+3, p.getPlane()), new WorldTile(p.getX()+3, p.getY(), p.getPlane()),
            new WorldTile(p.getX(), p.getY()-3, p.getPlane()), new WorldTile(p.getX()-3, p.getY(), p.getPlane())
        };
        int i = 0;
        for(WorldTile nearbyTile : nearbyTiles) {
            GameObject obj2 = World.getObject(nearbyTile, ObjectType.SCENERY_INTERACT);
            if (obj2 != null && obj2.getId() == 16517) {
                p.lock(3);
                p.addWalkSteps(farTiles[i], 4, false);
                return;
            }
            i++;
        }
    }

    private static boolean isIn21Room(WorldTile tile) {
        return tile.withinDistance(new WorldTile(1928, 4470, 0));
    }

    private static boolean isIn31Room(WorldTile tile) {
        return tile.withinDistance(new WorldTile(1956, 4466, 0));
    }

    private static boolean isIn41Room(WorldTile tile) {
        return tile.withinDistance(new WorldTile(1936, 4453, 0));
    }

    private static boolean isIn51Room(WorldTile tile) {
        return tile.withinDistance(new WorldTile(1955, 4448, 0));
    }

    private static boolean isIn61Room(WorldTile tile) {
        return tile.withinDistance(new WorldTile(1927, 4437, 0));
    }

    private static boolean isIn71Room(WorldTile tile) {
        return tile.withinDistance(new WorldTile(1949, 4429, 0));
    }

    private static boolean isIn81Room(WorldTile tile) {
        return tile.withinDistance(new WorldTile(1973, 4430, 0));
    }

    final static WorldTile[] rightHandSpearTraps = new WorldTile[] {
        new WorldTile(1927, 4473, 0), new WorldTile(1928, 4473, 0),
        new WorldTile(1930, 4452, 0), new WorldTile(1930, 4453, 0),
        new WorldTile(1955, 4474, 0), new WorldTile(1954, 4474, 0),
        new WorldTile(1961, 4444, 0), new WorldTile(1961, 4445, 0),
        new WorldTile(1927, 4428, 0), new WorldTile(1926, 4428, 0),
        new WorldTile(1944, 4425, 0), new WorldTile(1945, 4425, 0),
        new WorldTile(1974, 4424, 0), new WorldTile(1975, 4424, 0)
    };
    final static WorldTile[] leftHandSpearTraps = new WorldTile[] {
        new WorldTile(1927, 4472, 0), new WorldTile(1928, 4472, 0),
        new WorldTile(1931, 4452, 0), new WorldTile(1931, 4453, 0),
        new WorldTile(1955, 4473, 0), new WorldTile(1954, 4473, 0),
        new WorldTile(1962, 4444, 0), new WorldTile(1962, 4445, 0),
        new WorldTile(1927, 4427, 0), new WorldTile(1926, 4427, 0),
        new WorldTile(1944, 4424, 0), new WorldTile(1945, 4424, 0),
        new WorldTile(1974, 4423, 0), new WorldTile(1975, 4423, 0)
    };

    public static PlayerStepHandler handleRightHandSpearTraps = new PlayerStepHandler(rightHandSpearTraps) {
        @Override
        public void handle(PlayerStepEvent e) {
            if(e.getPlayer().isLocked())
                return;
            Direction rightHandTrap = Direction.rotateClockwise(e.getStep().getDir(), 2);//90 degree turn
            activateTrap(e, rightHandTrap);
            hitPlayer(e);
        }
    };

    public static PlayerStepHandler handleLeftHandSpearTraps = new PlayerStepHandler(leftHandSpearTraps) {
        @Override
        public void handle(PlayerStepEvent e) {
            if(e.getPlayer().isLocked())
                return;
            Direction leftHandTrap = Direction.rotateClockwise(e.getStep().getDir(), 6);//270 degree turn
            activateTrap(e, leftHandTrap);
            hitPlayer(e);
        }
    };

    private static void activateTrap(PlayerStepEvent e, Direction trapDir) {
        WorldTile trapTile = e.getTile();
        for(GameObject obj : World.getRegion(trapTile.getRegionId()).getObjects())
            if(obj.getId() == 16517)
                if(trapTile.matches(obj)) {
                    obj.animate(new Animation(463));
                    break;
                } else if(obj.getX() - trapDir.getDx() == trapTile.getX() && obj.getY() - trapDir.getDy() == trapTile.getY()) {
                    obj.animate(new Animation(463));
                    break;
                }
    }

    private static void hitPlayer(PlayerStepEvent e) {
        Player p = e.getPlayer();
        p.applyHit(new Hit(30, Hit.HitLook.POISON_DAMAGE));
        Direction oppositeDir = Direction.rotateClockwise(e.getStep().getDir(), 4);//180 degree turn
        int dX = oppositeDir.getDx();
        int dY = oppositeDir.getDy();
        WorldTile prevTile = new WorldTile(e.getTile().getX() + dX, e.getTile().getY() + dY, e.getTile().getPlane());
        p.lock(3);
        WorldTasks.schedule(new WorldTask() {
            int ticks = 0;
            @Override
            public void run() {
                if(ticks == 0) {
                    p.setNextAnimation(new Animation(1832));
                    p.setNextForceMovement(new ForceMovement(prevTile, 1, e.getStep().getDir()));
                }
                else if (ticks == 1) {
                    p.setNextWorldTile(prevTile);
                    p.forceTalk("Ouch!");
                }
                else if (ticks == 2)
                    stop();
                ticks++;
            }
        }, 0, 1);
    }

}
