package com.rs.game.content.world.areas.varrock.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;


@PluginEventHandler
public class VarrockBankDoor extends Conversation {
    private final int npcId = 2759;

    public static ObjectClickHandler VarrockBankDoor = new ObjectClickHandler(new Object[]{24389}, new Tile[]{Tile.of(3190, 3445, 0)}, e -> {
        if (e.getOpNum() == ClientPacket.OBJECT_OP1) {
            e.getPlayer().startConversation(new VarrockBankDoor(e.getPlayer()));
        }
    });


    public VarrockBankDoor(Player player) {
        super(player);
        player.lock(10);
        player.anim(9105);
        player.forceTalk("Knock knock...");
        addPlayer(HeadE.SKEPTICAL_HEAD_SHAKE, "I don't think I'm ever going to be allowed in there.");
        addNPC(npcId, HeadE.CONFUSED, "Who's there?");
        addOptions(new Options() {
            @Override
            public void create() {
                option("I'm " + player.getDisplayName() + ". Please let me in.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "I'm " + player.getDisplayName() + ". Please let me in.")
                        .addNPC(npcId, HeadE.SHAKING_HEAD, "No. Staff only beyond this point. You can't come in here."));

                option("Boo.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Boo.")
                        .addNPC(npcId, HeadE.SKEPTICAL, "Boo who?")
                        .addPlayer(HeadE.LOSING_IT_LAUGHING, "There's no need to cry!")
                        .addNPC(npcId, HeadE.ANGRY, "What? I'm not... oh, just go away!"));

                option("Kanga.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Kanga.")
                        .addNPC(npcId, HeadE.SKEPTICAL, "Kanga who?")
                        .addPlayer(HeadE.LOSING_IT_LAUGHING, "No, 'kangaroo'.")
                        .addNPC(npcId, HeadE.ANGRY, "Stop messing about and go away!"));

                option("Thank.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Thank.")
                        .addNPC(npcId, HeadE.SKEPTICAL, "Thank who?")
                        .addPlayer(HeadE.LOSING_IT_LAUGHING, "You're welcome!")
                        .addNPC(npcId, HeadE.ANGRY, "Stop it!"));

                option("Doctor.", new Dialogue()
                        .addPlayer(HeadE.HAPPY_TALKING, "Doctor.")
                        .addNPC(npcId, HeadE.SKEPTICAL, "Doctor wh.. hang on, I'm not falling for that one again! Go away."));
            }
        });;
    }
}




