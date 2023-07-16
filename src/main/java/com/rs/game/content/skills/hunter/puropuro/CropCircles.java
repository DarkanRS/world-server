package com.rs.game.content.skills.hunter.puropuro;

import com.rs.engine.cutscene.Cutscene;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class CropCircles {

    private static CropCircle locationOne, locationTwo;

    public enum CropCircle {
        ARDOUGNE(Tile.of(2647, 3347, 0)),
        BRIMHAVEN(Tile.of(2808, 3200, 0)),
        CATHERBY(Tile.of(2818, 3470, 0)),
        DRAYNOR_VILLAGE(Tile.of(3115, 3272, 0)),
        HARMONY_ISLAND(Tile.of(3810, 2852, 0)),
        LUMBRIDGE(Tile.of(3160, 3298, 0)),
        MISCELLANIA(Tile.of(2538, 3845, 0)),
        MOS_LE_HARMLESS(Tile.of(3697, 3025, 0)),
        RIMMINGTON(Tile.of(2979, 3216, 0)),
        DORICS_HOUSE(Tile.of(2953, 3444, 0)),
        COOKS_GUILD(Tile.of(3141, 3461, 0)),
        CHAMPIONS_GUILD(Tile.of(3212, 3345, 0)),
        TAVERLEY(Tile.of(2893, 3398, 0)),
        TREE_GNOME_STRONGHOLD(Tile.of(2435, 3472, 0)),
        YANILLE(Tile.of(2582, 3104, 0));

        private final Tile entranceTile;

        CropCircle(Tile tile) {
            this.entranceTile = tile;
        }

        public Tile getEntranceTile() {
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

    public static ObjectClickHandler teleportToPuroPuro = new ObjectClickHandler(new Object[] { 24988, 24991 }, e -> {
        if (!e.isAtObject())
            return;
        WorldTasks.schedule(10, () -> {
            e.getPlayer().getControllerManager().startController(new PuroPuroController(e.getObject().getTile()));
            if (e.getObjectId() == 24988) {
                e.getPlayer().addEffect(Effect.FARMERS_AFFINITY, 3000);
                e.getPlayer().sendMessage("You feel the magic of the crop circle grant you a Farmer's affinity.");
            }
        });
        Magic.sendTeleportSpell(e.getPlayer(), 6601, -1, 1118, -1, 0, 0, Tile.of(2590 + Utils.randomInclusive(0, 3), 4318 + Utils.randomInclusive(0, 3), 0), 9, false, Magic.OBJECT_TELEPORT, null);
    });

    public static NPCClickHandler handleWanderingImpling = new NPCClickHandler(new Object[] { 6073 }, e -> {
        Cutscene currentLocations = new Cutscene() {
            @Override
            public void construct(Player player) {
                Tile loc1 = locationOne.getEntranceTile();
                Tile loc2 = locationTwo.getEntranceTile();
                setEndTile(player.getTile());
                fadeIn(3);
                hideMinimap();
                action(() -> {
                    player.lock();
                    player.moveTo(loc1);
                    player.getAppearance().setHidden(true);
                });
                delay(2);
                camPos(loc1.getX()-3, loc1.getY()-3, 2500);
                camLook(loc1.getX(), loc1.getY(), 0);
                fadeOut(3);
                camPos(loc1.getX()-10, loc1.getY()-10, 7000, 50,1);
                delay(3);
                camPos(loc1.getX(), loc1.getY()-10, 7000,50,1);
                delay(3);
                camPos(loc1.getX()+10, loc1.getY()-10, 7000,50,1);
                delay(4);

                fadeIn(3);
                action(() -> { player.moveTo(loc2); });
                delay(2);
                camPos(loc2.getX()-3, loc2.getY()-3, 2500);
                camLook(loc2.getX(), loc2.getY(), 0);
                fadeOut(3);
                camPos(loc2.getX()-10, loc2.getY()-10, 7000, 50,1);
                delay(3);
                camPos(loc2.getX(), loc2.getY()-10, 7000,50,1);
                delay(3);
                camPos(loc2.getX()+10, loc2.getY()-10, 7000,50,1);
                delay(6);
            }
        };
        switch (e.getOption()) {
            case "Talk-to" -> {
                e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
                    {
                        addPlayer(HeadE.CHEERFUL, "Hello there.");
                        addNPC(e.getNPCId(), HeadE.CONFUSED, "Are they here? Are they there? Are they elsewhere?");
                        addPlayer(HeadE.CHEERFUL, "Are what where?");
                        addNPC(e.getNPCId(), HeadE.CONFUSED, "This gate's sisters...");
                        addPlayer(HeadE.CHEERFUL, "I didn't know gates had relations.");
                        addNPC(e.getNPCId(), HeadE.CONFUSED, "Oh yes. This one here: she is calm and boring. She stays here all the time. She doesn't like to move, but her sisters are fidgety and refuse to stay in the same place for more than half an hour.");
                        addPlayer(HeadE.CHEERFUL, "So, where are the, errr, fidgety sisters at the moment?");
                        addNPC(e.getNPCId(), HeadE.CONFUSED, "Oh well. I don't know what you humans call these places. Let me show you.");
                        addNext(() -> {
                            e.getPlayer().playCutscene(currentLocations);
                        });
                    }
                });
            }
            case "Check-gates" -> { e.getPlayer().playCutscene(currentLocations); }
            default -> {}
        }
    });

    @ServerStartupEvent
    public static void initCropCircles() {
        WorldTasks.schedule(0, Ticks.fromMinutes(30), () -> {
            int random = Utils.random(0, CropCircle.values().length);
            int random2 = Utils.random(0, CropCircle.values().length);
            while (random2 == random)
                random2 = Utils.random(0, CropCircle.values().length);

            locationOne = CropCircle.values()[random];
            locationTwo = CropCircle.values()[random2];

            NPC impOne = new NPC(1531, Tile.of(locationOne.getX() - 1, locationOne.getY() - 1, locationOne.getPlane()));
            NPC impTwo = new NPC(1531, Tile.of(locationTwo.getX() - 1, locationTwo.getY() - 1, locationTwo.getPlane()));

            Tile[] impPathOne = setImpPath(locationOne);
            Tile[] impPathTwo = setImpPath(locationTwo);

            impOne.setRandomWalk(false);
            impOne.finishAfterTicks(42);

            impTwo.setRandomWalk(false);
            impTwo.finishAfterTicks(42);

            WorldTasks.scheduleTimer(ticks -> {
                if (ticks % 5 == 0 && ticks <= 35) {
                    impOne.setForceWalk(impPathOne[ticks / 5]);
                    impTwo.setForceWalk(impPathTwo[ticks / 5]);
                }

                if (ticks == 41) {
                    impOne.setNextSpotAnim(new SpotAnim(1119)); // 931?
                    impTwo.setNextSpotAnim(new SpotAnim(1119)); // 931?
                }

                if (ticks >= 45) {
                    spawnCropCircle(locationOne);
                    spawnCropCircle(locationTwo);
                    return false;
                }
                return true;
            });
        });
    }

    private static Tile[] setImpPath(CropCircle location) {
        Tile[] impPath = new Tile[] {
                Tile.of(location.getX() + 1, location.getY() - 1, location.getPlane()),
                Tile.of(location.getX() + 1, location.getY() + 1, location.getPlane()),
                Tile.of(location.getX() - 1, location.getY() + 1, location.getPlane()),
                Tile.of(location.getX() - 1, location.getY() - 1, location.getPlane()),
                Tile.of(location.getX() + 1, location.getY() - 1, location.getPlane()),
                Tile.of(location.getX() + 1, location.getY() + 1, location.getPlane()),
                Tile.of(location.getX() - 1, location.getY() + 1, location.getPlane()),
                Tile.of(location.getX() - 1, location.getY() - 1, location.getPlane())
        };
        return impPath;
    }

    private static void spawnCropCircle(CropCircle location) {
        World.spawnObjectTemporary(new GameObject(24986, 2, location.getX() - 1, location.getY() + 1, location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24985, 2, location.getX(), location.getY() + 1, location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24984, 2, location.getX() + 1, location.getY() + 1, location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24987, 2, location.getX() - 1, location.getY(), location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24988, 0, location.getX(), location.getY(), location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24987, 0, location.getX() + 1, location.getY(), location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24984, 0, location.getX() - 1, location.getY() - 1, location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24985, 0, location.getX(), location.getY() - 1, location.getPlane()), Ticks.fromMinutes(30));
        World.spawnObjectTemporary(new GameObject(24986, 0, location.getX() + 1, location.getY() - 1, location.getPlane()), Ticks.fromMinutes(30));
    }
}