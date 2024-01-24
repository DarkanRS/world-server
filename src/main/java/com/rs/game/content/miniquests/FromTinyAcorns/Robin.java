package com.rs.game.content.miniquests.FromTinyAcorns;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;

public class Robin {

    public Robin(Player player){
        stage1(player);
    }
    private static final int npcID = 11279;

    private static void stage1(Player player) {
        player.startConversation(new Dialogue()
                .addOptions(ops -> {
                    ops.add("Talk about the current caper.")
                            .addPlayer(HeadE.CHEERFUL, "Do you have any advice for me about this Urist fellow?")
                            .addNext(() -> {
                                if (player.getInventory().containsItem(18651)) {
                                    player.startConversation(new Dialogue()
                                            .addNPC(npcID, HeadE.LAUGH, "Well, since you seem to have bamboozled him handly, I'd have a word with Darren about it.")
                                            .addPlayer(HeadE.CHEERFUL, "Will do, thanks.")
                                    );
                                } else {
                                    player.startConversation(new Dialogue()
                                            .addNPC(npcID, HeadE.SHAKING_HEAD, "I don't know anything about him, sorry. He's only just set up shop and I've not been to Varrock in a while. You'll probably want to ask someone who sees him on a more regular basis.")
                                            .addPlayer(HeadE.SKEPTICAL, "Hmm.")
                                            .addNPC(npcID, HeadE.SCARED, "Oh, and if you're stealing from his stall, watch out for the guards. They keep a close eye on activity in the marketplace and they're not lenient with shoplifters.")
                                            .addPlayer(HeadE.SKEPTICAL_THINKING, "Will do, thanks.")
                                    );
                                }
                            });
                }));
    }
}


