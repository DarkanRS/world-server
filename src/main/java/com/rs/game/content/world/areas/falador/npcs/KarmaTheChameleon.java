package com.rs.game.content.world.areas.falador.npcs;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class KarmaTheChameleon extends Conversation {
    private static final int npcId = 8580;

    public static NPCClickHandler KarmaTheChameleon = new NPCClickHandler(new Object[]{ npcId }, e -> {
        switch (e.getOption()) {
            
            case "Talk-to" -> e.getPlayer().startConversation(new KarmaTheChameleon(e.getPlayer()));
        }
    });

    public KarmaTheChameleon(Player player) {
        super(player);
        if(player.getSkills().getLevel(Skills.SUMMONING) <= 98){
            addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "Hssshsss hssssh sssshssss sss.");
            addOptions(new Options() {
                @Override
                public void create() {

                    option("Who are you?", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Who are you?")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "Hssshsss hssssh sssshssss sss.")
                            .addPlayer(HeadE.HAPPY_TALKING, "That's nice.")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "Hssshsss hssssh sssshssss sss.")
                            .addPlayer(HeadE.HAPPY_TALKING, "Rock on!.")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "Hssshsss hssssh sssshssss sss.")

                    );
                    option("Rock on!.", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Rock on!.")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "Well, see you around, Tiffy.")
                    );
                }
            });
        }
        else {
            addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "Party on, dude!");
            addOptions(new Options() {
                @Override
                public void create() {

                    option("Who are you?", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Who are you?")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "My name's Karma. I'm Pete's pet.")
                            .addPlayer(HeadE.HAPPY_TALKING, "That's nice.")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "Oh yeah, man, it majorly rocks!")
                            .addPlayer(HeadE.HAPPY_TALKING, "Rock on!.")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "You betcha!")

                    );
                    option("Rock on!.", new Dialogue()
                            .addPlayer(HeadE.HAPPY_TALKING, "Rock on!.")
                            .addNPC(npcId, HeadE.CHILD_HAPPY_TALK, "You betcha!")
                    );
                }
            });
        }

    }


}
