package com.rs.game.player.quests.handlers.shieldofarrav;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;

public class KingRoaldShieldOfArravD extends Conversation {
    final int KING_ROALD = 648;

    public KingRoaldShieldOfArravD(Player p) {
        super(p);
        if(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) < ShieldOfArrav.HAS_SHIELD_STAGE) {
            addPlayer(HeadE.WORRIED, "There is nothing to say...");
            return;
        }

        if(p.getInventory().containsItem(ShieldOfArrav.CERTIFICATE_FULL)) {
            addPlayer(HeadE.HAPPY_TALKING, "Your majesty, I have come to claim the reward for the return of the Shield Of Arrav.");
            addItem(ShieldOfArrav.CERTIFICATE_FULL, "You show the certificate to King Roald");
            addNPC(KING_ROALD, HeadE.AMAZED, "My goodness! My father set a bounty on this shield many years ago!");
            addNPC(KING_ROALD, HeadE.AMAZED_MILD, "I never thought I would live to see the day when someone came forward to claim it.");
            addNPC(KING_ROALD, HeadE.TALKING_ALOT, "1,200 coins was a fortune in my father's day. I hope it serves you well in your adventures.");
            addNPC(KING_ROALD, HeadE.HAPPY_TALKING, "The museum of Varrock shall proudly display the Shield of Arrav once again.");
            addNext(() -> {
                p.getInventory().deleteItem(ShieldOfArrav.CERTIFICATE_FULL, 1);
               p.getQuestManager().completeQuest(Quest.SHIELD_OF_ARRAV);
            });
            return;
        }

        if(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) == ShieldOfArrav.SPOKE_TO_KING_STAGE) {
            addNPC(KING_ROALD, HeadE.SKEPTICAL, "Have you gotten the shield authenticated with the museum curator?");
            addPlayer(HeadE.SAD_MILD_LOOK_DOWN, "Not yet...");
            return;
        }

        if(p.getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) == ShieldOfArrav.HAS_CERTIFICATE_STAGE) {
            addNPC(KING_ROALD, HeadE.SKEPTICAL, "How am I supposed to believe just half of a certificate?");
            addPlayer(HeadE.SAD, "Darn, I need to find someone with another half...");
            return;
        }

        if(p.getInventory().containsItem(ShieldOfArrav.SHIELD_LEFT_HALF) || p.getInventory().containsItem(ShieldOfArrav.SHIELD_RIGHT_HALF)) {
            addPlayer(HeadE.HAPPY_TALKING, " Your majesty, I have recovered the Shield Of Arrav; I would like to claim the reward.");
            addNPC(KING_ROALD, HeadE.SKEPTICAL, "The Shield of Arrav, eh? Yes, I do recall my father, King Roald, put a reward out for that");
            addNPC(KING_ROALD, HeadE.SKEPTICAL, "Very well, If you get the authenticity of the shield verified by the curator at the museum and then return" +
                    " here with authentication, I will grant you your reward.");
            p.getQuestManager().setStage(Quest.SHIELD_OF_ARRAV, ShieldOfArrav.SPOKE_TO_KING_STAGE);
        }
    }


}
