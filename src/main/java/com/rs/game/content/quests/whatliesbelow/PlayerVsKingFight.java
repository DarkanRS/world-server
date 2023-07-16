package com.rs.game.content.quests.whatliesbelow;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.quests.whatliesbelow.npcs.SurokMagis;
import com.rs.game.content.world.areas.varrock.npcs.Zaff;
import com.rs.game.map.instance.Instance;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.InstancedController;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.EmotesManager;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class PlayerVsKingFight extends InstancedController {
    private static final Tile OUTSIDE = Tile.of(3209, 3492, 0);

    private transient NPC surok, king;

    public PlayerVsKingFight() {
        super(Instance.of(OUTSIDE, 4, 4).persist().setEntranceOffset(new int[]{10, 15, 0}));
    }

    @Override
    public void onBuildInstance() {
        player.lock();
        getInstance().copyMapAllPlanes(400, 435).thenAccept(b -> {
            GameObject table = World.getObject(getInstance().getLocalTile(9, 13));
            World.removeObject(table);
            GameObject moved = new GameObject(table);
            moved.setTile(getInstance().getLocalTile(10, 16));
            World.spawnObject(moved);
            player.playCutscene(cs -> {
                cs.dialogue(new Dialogue().addSimple("The room grows dark and you sense objects moving..."));
                cs.fadeIn(5);
                cs.action(1, () -> getInstance().teleportLocal(player, 10, 15, 0));
                cs.action(() -> {
                    player.setForceNextMapLoadRefresh(true);
                    player.loadMapRegions();
                    cs.setEndTile(Tile.of(cs.getX(10), cs.getY(15), 0));
                });
                cs.npcCreate("surok", 5835, 9, 16, 0, n -> {
                    n.persistBeyondCutscene();
                    n.setIgnoreNPCClipping(true);
                    surok = n;
                });
                cs.npcCreate("king", 5838, 10, 10, 0, n -> {
                    n.persistBeyondCutscene();
                    n.setIgnoreNPCClipping(true);
                    king = n;
                });
                cs.camPos(10, 6, 2500);
                cs.camLook(9, 16, 89);
                cs.delay(1);
                cs.camPos(10, 6, 2500, 0, 5);
                cs.camLook(9, 15, 1500, 0, 5);
                cs.fadeOut(5);
                cs.npcFaceNPC("surok", "king");
                cs.npcFaceDir("king", Direction.NORTH);
                cs.npcTalk("surok", "Annach Narh Hin Dei!");
                cs.npcAnim("surok", 6098);
                cs.npcSpotAnim("surok", 1009);
                cs.delay(1);
                cs.npcAnim("surok", -1);
                cs.action(() -> World.sendProjectile(surok, king, 1010, 5, 15, 15, 0.4, 10, 10, proj -> king.spotAnim(1011)));
                cs.action(() -> king.forceTalk("What's going on?"));
                cs.camPos(10, 19, 3048, 0, 10);
                cs.camLook(10, 8, 715, 0, 10);
                cs.delay(1);
                cs.action(() -> king.forceTalk("What's going on?"));
                cs.delay(4);
                cs.action(() -> {
                    king.forceTalk("I...must...kill..." + player.getDisplayName() +"!!");
                    king.setNextAnimation(EmotesManager.Emote.CRY.getAnim());
                });
                cs.dialogue(new Dialogue().addPlayer(HeadE.WORRIED, "Uh oh! King Roald looks evil!"));
                cs.playerTalk("Un oh! King Roald looks evil!");
                cs.delay(4);
                cs.action(() -> {
                    player.unlock();
                    king.setTarget(player);
                });
            });
        });
    }

    @Override
    public void onDestroyInstance() {

    }

    @Override
    public boolean sendDeath() {
        player.safeDeath(OUTSIDE);
        return false;
    }

    @Override
    public boolean processNPCClick1(NPC npc) {
        player.faceTile(npc.getTile());
        switch (npc.getId()) {
            case 5835 -> player.sendMessage("Surok's eyes are glazed over. he must be concentrating hard on the spell over King Roald.");
        }
        return false;
    }

    @Override
    public boolean processObjectClick1(GameObject object) {
        if (object.getId() == 15536) {
            player.sendMessage("As you touch the door, your hand tingles and the room seems to grow dim. You can hear Surok muttering a spell behind you.");
            player.lock();
            player.fadeScreen(() -> {
                player.getControllerManager().forceStop();
                player.setNextTile(Tile.of(3214, 3378, 0));
                player.resetReceivedHits();
                player.unlock();
            });
            return false;
        }
        return true;
    }

    public void attemptZaffSummon(Player player) {
        player.resetWalkSteps();
        player.lock();
        king.setTarget(null);
        king.freeze(50);
        if (king.getHitpoints() >= 20) {
            player.playCutscene(cs -> {
                cs.npcCreate("zaff", Zaff.ID, player.getNearestTeleTile(1).localizeRegion());
                cs.npcSpotAnim("zaff", new SpotAnim(110, 0, 96));
                cs.delay(4);
                cs.camPos(player.getXInRegion(), player.getYInRegion() - 7, 400, 0, 5);
                cs.camLook(player.getXInRegion(), player.getYInRegion(), 87);
                cs.dialogue(new Dialogue().addNPC(Zaff.ID, HeadE.AMAZED, "The king's still too strong! We had better get out of here!"));
                cs.delay(5);
                cs.playerSpotAnim(new SpotAnim(110, 0, 96));
                cs.delay(1);
                cs.action(() -> {
                    player.getControllerManager().forceStop();
                    player.setNextTile(Tile.of(3202, 3432, 0));
                });
            });
            return;
        }
        player.playCutscene(cs -> {
            cs.npcCreate("zaff", Zaff.ID, king.getNearestTeleTile(Direction.getDirectionTo(king, player)).localizeRegion());
            cs.npcSpotAnim("zaff", new SpotAnim(110, 0, 96));
            cs.setEndTile(OUTSIDE);
            cs.action(() -> {
                player.setQuestStage(Quest.WHAT_LIES_BELOW, 8);
                cs.getNPC("zaff").faceEntity(king);
                king.faceEntity(cs.getNPC("zaff"));;
            });
            cs.delay(3);
            cs.npcSync("zaff", 5633, 1006);
            cs.delay(1);
            cs.action(() -> World.sendProjectile(cs.getNPC("zaff"), king, 1007, 5, 15, 15, 0.4, 10, 10, proj -> king.spotAnim(1008)));
            cs.delay(1);
            cs.action(() -> {
                king.anim(6098);
                king.forceTalk("Wh...!");
            });
            cs.delay(2);
            cs.npcAnim("zaff", 1819);
            cs.npcSpotAnim("zaff", new SpotAnim(108, 0, 92));
            cs.delay(1);
            cs.action(() -> World.sendProjectile(cs.getNPC("zaff"), king, 109, 5, 15, 15, 0.4, 10, 10, proj -> {
                king.spotAnim(110);
                king.finishAfterTicks(2);
            }));
            cs.delay(3);
            cs.dialogue(new Dialogue().addNPC(Zaff.ID, HeadE.ANGRY, "The king's mind has been restored to him and he has been teleported away to safety. Now, to deal with Surok!"), true);
            cs.action(() -> player.faceTile(surok.getTile()));
            cs.dialogue(new Dialogue().addNPC(SurokMagis.ID, HeadE.ANGRY, "No! All is lost! I must escape!"), true);
            cs.playerFaceEntity("zaff");
            cs.dialogue(new Dialogue().addNPC(Zaff.ID, HeadE.ANGRY, "You will not escape justice this time, Surok!"), true);
            cs.delay(2);
            cs.playerMove(8, 14, Entity.MoveType.WALK);
            cs.npcWalk("zaff", 10, 14);
            cs.delay(2);
            cs.camPos(player.getXInRegion(), player.getYInRegion() - 5, 3500, 0, 5);
            cs.camLook(player.getXInRegion(), player.getYInRegion(), 87, 0, 5);
            cs.action(() -> {
                player.faceTile(surok.getTile());
                cs.getNPC("zaff").faceTile(surok.getTile());
            });
            cs.delay(2);
            cs.action(() -> {
                surok.forceTalk("Mirra din namus!!");
                surok.faceEntity(cs.getNPC("zaff"));
                surok.anim(6098);
                surok.spotAnim(108, 0, 92);
            });
            cs.delay(1);
            cs.npcAnim("zaff", 1819);
            cs.npcSpotAnim("zaff", new SpotAnim(108, 0, 92));
            cs.npcTalk("zaff", "Stop!!");
            cs.delay(1);
            cs.action(() -> surok.anim(-1));
            cs.action(() -> World.sendProjectile(cs.getNPC("zaff"), surok, 109, 5, 15, 15, 0.4, 10, 10, proj -> {
                surok.spotAnim(345);
                surok.anim(6098);
            }));
            cs.delay(3);
            cs.dialogue(new Dialogue().addNPC(Zaff.ID, HeadE.FRUSTRATED, "Your teleport spell has been corrupted, Surok! I have placed a magic block on this room. You will remain here, under guard, in the library from now on."), true);
            cs.action(() -> player.faceTile(surok.getTile()));
            cs.dialogue(new Dialogue().addNPC(SurokMagis.ID, HeadE.FRUSTRATED, "No! My plans have been ruined! I was so close to success!"), true);
            cs.action(() -> player.faceTile(cs.getNPC("zaff").getTile()));
            cs.dialogue(new Dialogue().addNPC(Zaff.ID, HeadE.CHEERFUL, "Thank you for your help, " + player.getDisplayName() + ". I will put the room back in order and then I must leave. Surok is defeated and will be no more trouble for us. We will guard him more closely from now on!"), true);
            cs.npcAnim("zaff", 1819);
            cs.npcSpotAnim("zaff", new SpotAnim(108, 0, 92));
            cs.delay(1);
            cs.npcSpotAnim("zaff", 110);
            cs.delay(1);
            cs.npcDestroy("zaff");
            cs.delay(3);
            cs.dialogue(new Dialogue().addSimple("The room grows dark again and you sense objects moving..."));
            cs.fadeIn(5);
            cs.action(() -> {
                player.getControllerManager().forceStop();
                player.setNextTile(OUTSIDE);
            });
            cs.fadeOut(5);
        });
    }
}
