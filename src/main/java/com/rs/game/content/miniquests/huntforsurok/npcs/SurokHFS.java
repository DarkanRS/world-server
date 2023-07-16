package com.rs.game.content.miniquests.huntforsurok.npcs;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.managers.EmotesManager;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SurokHFS {
    public static final int ID = 7009;

    public static NPCClickHandler talk = new NPCClickHandler(new Object[] { ID }, e -> {
        if (e.getPlayer().isQuestComplete(Quest.WHAT_LIES_BELOW))
            e.getPlayer().startConversation(new Dialogue()
                    .addNPC(ID, HeadE.FRUSTRATED, e.getPlayer().getDisplayName()+"! The meddling adventurer.")
                    .addPlayer(HeadE.AMAZED, "Surok! What are you doing here? How did you-")
                    .addNPC(ID, HeadE.FRUSTRATED, "Escape from Varrock Palace Library? That cruel imprisonment you left me in?")
                    .addPlayer(HeadE.AMAZED, "Well...er...yes.")
                    .addNPC(ID, HeadE.FRUSTRATED, "Bah! A mere trifle for a powerful mage such as myself. There were plenty of other foolish people to help with my plans and you would do well to stay out of my way.")
                    .addNextIf(() -> !Miniquest.HUNT_FOR_SUROK.meetsReqs(e.getPlayer(), "to start Hunt for Surok."), new Dialogue()
                            .addStop())
                    .addPlayer(HeadE.FRUSTRATED, "Stop, Surok! As a member of the Varrock Palace Secret Guard, I arrest you! Again!")
                    .addNPC(ID, HeadE.FRUSTRATED, "Ha! I tire of this meaningless drivel. Catch me if you can.")
                    .addNext(() -> e.getPlayer().playCutscene(cs -> {
                        cs.setEndTile(e.getPlayer().getTile());
                        cs.fadeIn(5);
                        cs.action(() -> e.getPlayer().setMiniquestStage(Miniquest.HUNT_FOR_SUROK, 1));
                        cs.dynamicRegion(e.getPlayer().getTile(), 408, 431, 5, 5, true);
                        cs.npcCreate("surok", 7002, 18, 18, 0);
                        cs.playerMove(19, 16, Entity.MoveType.TELE);
                        cs.npcFaceTile("surok", 19, 16);
                        cs.playerFaceTile(18, 18);
                        cs.camPos(19, 3, 8572);
                        cs.camLook(19, 19, 0);
                        cs.camPos(19, 7, 3541, 0, 5);
                        cs.fadeOut(5);
                        cs.npcTalk("surok", "A distraction is needed!");
                        cs.npcSync("surok", 6098, 1009);
                        cs.delay(1);
                        cs.npcAnim("surok", -1);
                        cs.action(() -> World.sendProjectile(cs.getNPC("surok"), cs.getPlayer().transform(2, -3, 0), 1010, 5, 15, 15, 0.4, 10, 10));
                        cs.delay(1);
                        cs.playerTalk("Whoah!");
                        cs.playerAnim(EmotesManager.Emote.CRY.getAnim());
                        cs.delay(1);
                        cs.npcWalk("surok", 19, 19);
                        cs.delay(0);
                        cs.npcFaceTile("surok", 19, 20);
                        cs.npcAnim("surok", 451);
                        cs.delay(0);
                        cs.npcDestroy("surok");
                        cs.delay(2);
                        cs.fadeIn(5);
                        cs.returnPlayerFromInstance();
                        cs.camPosReset();
                        cs.fadeOut(5);
                        cs.dialogue(new Dialogue().addPlayer(HeadE.SAD_MILD, "I'd better get after him before he gets away."));
                    })));
    });
}
