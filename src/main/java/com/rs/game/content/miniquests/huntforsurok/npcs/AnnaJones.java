package com.rs.game.content.miniquests.huntforsurok.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.miniquest.Miniquest;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class AnnaJones extends Conversation {
    public static final int ID = 5837;

    public static NPCClickHandler handle = new NPCClickHandler(new Object[] { ID }, e -> e.getPlayer().startConversation(new AnnaJones(e.getPlayer())));

    @ServerStartupEvent
    public static void addLoSOverrides() {
        Entity.addLOSOverride(ID);
    }

    public AnnaJones(Player player) {
        super(player);
        addNPC(ID, HeadE.CHEERFUL, "Yes? Can I help you?");
        addOptions(this, "startOps", ops -> {
            ops.add("Who are you?")
                    .addPlayer(HeadE.CONFUSED, "Who are you?")
                    .addNPC(ID, HeadE.CHEERFUL, "Well, now. Do you always go around asking about people like that? It's very rude, you know.")
                    .addPlayer(HeadE.AMAZED, "Sorry! I didn't mean to pry!")
                    .addNPC(ID, HeadE.CHEERFUL, "That's alright. My name is Louisiana Jones, although most people call me Anna. I'm an archaeologist.")
                    .addPlayer(HeadE.CONFUSED, "Oh. Do you work for the Varrock Museum?")
                    .addNPC(ID, HeadE.CHEERFUL, "Hah! No. I used to, but now I prefer to work freelance for independent employers.")
                    .addPlayer(HeadE.CONFUSED, "I see.")
                    .addGotoStage("startOps", this);

            ops.add("What are you doing here?")
                    .addPlayer(HeadE.CONFUSED, "What are you doing here?")
                    .addNPC(ID, HeadE.CHEERFUL, "I'm investigating something for someone.")
                    .addPlayer(HeadE.AMAZED, "That doesn't really explain anything!")
                    .addNPC(ID, HeadE.CHEERFUL, "I never said it would.")
                    .addPlayer(HeadE.CONFUSED, "So what are you investigating?")
                    .addNPC(ID, HeadE.CHEERFUL, "I'm afraid I can't say.")
                    .addPlayer(HeadE.CONFUSED, "Okay, so who are you investigating it for?")
                    .addNPC(ID, HeadE.CHEERFUL, "I'm afraid I can't say that either.")
                    .addPlayer(HeadE.SAD_MILD, "Okay, so is there anything you CAN say?")
                    .addNPC(ID, HeadE.CHEERFUL, "I can say that I can't say anything about what you want me to say!")
                    .addPlayer(HeadE.CONFUSED, "Okay, I think you've said enough!")
                    .addGotoStage("startOps", this);

            ops.add("Who does this statue represent?")
                    .addPlayer(HeadE.CONFUSED, "Who does this statue represent?")
                    .addNPC(ID, HeadE.CHEERFUL, "That, my dear, is the statue of the great god Saradomin himself. Stand and admire in awe, for you are in the presence of greatness!")
                    .addOptions(hailOps -> {
                        hailOps.add("All hail Saradomin!")
                               .addPlayer(HeadE.CHEERFUL, "All hail Saradomin!")
                               .addNPC(ID, HeadE.CHEERFUL, "Indeed. Saradomin watches over us all.")
                               .addGotoStage("startOps", this);
                        hailOps.add("...Yay Zamorak...!")
                               .addPlayer(HeadE.CALM_TALK, "...Yay Zamorak...!")
                               .addNPC(ID, HeadE.ANGRY, "WHAT?!?!")
                               .addPlayer(HeadE.AMAZED, "Uh...I said I've lost my anorak!")
                               .addNPC(ID, HeadE.CALM_TALK, "I see. I'm afraid I haven't seen it.")
                               .addGotoStage("startOps", this);
                    });

            ops.add("Okay, I better go.")
                    .addPlayer(HeadE.CALM_TALK, "Okay, I'd better go.")
                    .addNPC(ID, HeadE.CHEERFUL, "Okay, then!");
        });
    }
}
